/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.drugorders.page.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import org.openmrs.DrugOrder;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.module.allergyapi.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.allergyapi.Allergies;
import org.openmrs.module.drugorders.api.drugordersService;
import org.openmrs.module.drugorders.drugorders;
import org.openmrs.module.uicommons.util.InfoErrorMessageUtil;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author harini-geek
 */
public class PharmacyPatientPageController {

    public void controller(PageModel model, HttpSession session,
            @RequestParam("patientId") Patient patient, @SpringBean("allergyService") PatientService patientService,
            @RequestParam(value = "action", required = false) String action,
            @RequestParam(value = "groupCheckBox", required=false) long[] groupCheckBox,
            @RequestParam(value = "pharmaGroupAction", required = false) String groupAction,
            @RequestParam(value = "groupComments", required = false) String groupComments,
            @RequestParam(value = "drugExpiryDate", required = false) Date[] drugExpiryDate,
            @RequestParam(value = "commentForPatient", required = false) String[] commentForPatient) {

        Allergies allergies = patientService.getAllergies(patient);
        model.addAttribute("allergies", allergies);
        
        if (StringUtils.isNotBlank(action)) {
            try {
                if ("Confirm".equals(action)) {
                    if(groupCheckBox.length > 0){
                        for(int i=0;i<groupCheckBox.length;i++){
                            
                            int orderID = Integer.parseInt(Long.toString(groupCheckBox[i]));
                            drugorders drugorder = Context.getService(drugordersService.class).getDrugOrderByOrderID(orderID);
                            
                            //Change Order Status when Pharmacist performs a new action on the Order
                            switch (groupAction) {
                                case "Discard":
                                    drugorder.setForDiscard(1);
                                    if(drugorder.getOnHold() == 1)
                                        drugorder.setOnHold(0);
                                    if(groupComments != null)
                                        drugorder.setCommentForOrderer(groupComments);
                                    break;
                                case "On Hold":
                                    drugorder.setOnHold(1);
                                    if(drugorder.getForDiscard()== 1)
                                        drugorder.setForDiscard(0);
                                    if(groupComments != null)
                                        drugorder.setCommentForOrderer(groupComments);
                                    break;
                                case "Dispatch":
                                    //Change Order Status when Pharmacist performs a new action on the Order
                                    if(drugorder.getForDiscard() == 1)
                                        drugorder.setForDiscard(0);
                                    else if(drugorder.getOnHold() == 1)
                                        drugorder.setOnHold(0);
                                    if (drugorder.getRefill() > 0) {
                                        drugorder.setLastDispatchDate(Calendar.getInstance().getTime());
                                        drugorder.setRefill(drugorder.getRefill() - 1);
                                    }
                                    else {
                                        switch (drugorder.getOrderStatus()) {
                                            case "Active":
                                                drugorder.setOrderStatus("Non-Active");
                                                break;
                                            case "Active-Group":
                                                drugorder.setOrderStatus("Non-Active-Group");
                                                break;
                                            case "Active-Plan":
                                                drugorder.setOrderStatus("Non-Active-Plan");
                                                break;
                                        }
                                        Context.getOrderService().voidOrder(Context.getOrderService().getOrder(drugorder.getOrderId()), "No Longer Active");
                                    }   
                                    drugorder.setDrugExpiryDate(drugExpiryDate[i]);
                                    drugorder.setCommentForPatient(commentForPatient[i]);
                                    
                                    printOrder(drugorder.getOrderId());
                                    break;
                            }
                            Context.getService(drugordersService.class).saveDrugOrder(drugorder);
                        }
                    }
                }
            } 
            catch (NumberFormatException | APIException e) {
                System.out.println(e.toString());
            }
            InfoErrorMessageUtil.flashInfoMessage(session, "Order Status - " + groupAction);
        }
        model.addAttribute("group_order_status", groupAction);
    }
    
    void printOrder(int orderID){
        
        try {
            DrugOrder order = (DrugOrder) Context.getOrderService().getOrder(orderID);
            drugorders drugorder = Context.getService(drugordersService.class).getDrugOrderByOrderID(orderID);
            
            PrintService service = PrintServiceLookup.lookupDefaultPrintService();
            
            String OrderDetails = drugorder.getDrugName().getDisplayString() + " " + order.getDose() + " " + order.getDoseUnits().getDisplayString() + " " +
                    order.getDuration() + " " + order.getDurationUnits().getDisplayString() + " " + order.getQuantity() + " " + order.getQuantityUnits() + "\n" +
                    "Route: " + order.getRoute().getDisplayString() + " " + "Frequency: " + order.getFrequency().getName() + "\n" +
                    "Start Date: " + drugorder.getStartDate().toString() + "\n" +
                    "Patient Instructions: " + drugorder.getPatientInstructions();
            
            try (InputStream is = new ByteArrayInputStream(OrderDetails.getBytes())) {
                PrintRequestAttributeSet  pras = new HashPrintRequestAttributeSet();
                pras.add(new Copies(1));
                
                if(service != null){
                    DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
                    Doc doc = new SimpleDoc(is, flavor, null);
                    DocPrintJob job = service.createPrintJob();
                    
                    job.print(doc, pras);
                }
            }
            
        } catch (IOException | PrintException ex) {
            Logger.getLogger(PharmacyPatientPageController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}