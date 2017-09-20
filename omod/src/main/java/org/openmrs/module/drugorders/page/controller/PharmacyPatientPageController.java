/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.drugorders.page.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.text.SimpleDateFormat;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.module.allergyapi.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.allergyapi.Allergies;
import org.openmrs.module.allergyapi.Allergy;
import org.openmrs.module.drugorders.api.drugordersService;
import org.openmrs.module.drugorders.drugorders;
import org.openmrs.module.uicommons.util.InfoErrorMessageUtil;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author harini-parthasarathy
 */
public class PharmacyPatientPageController {

    public void controller(PageModel model, HttpSession session,
            @RequestParam("patientId") Patient patient, @SpringBean("allergyService") PatientService patientService,
            @RequestParam(value = "action", required = false) String action,
            @RequestParam(value = "groupCheckBox", required=false) long[] groupCheckBox,
            @RequestParam(value = "pharmaGroupAction", required = false) String groupAction,
            @RequestParam(value = "groupComments", required = false) String groupComments,
            @RequestParam(value = "drugExpiryDate", required = false) String[] drugExpiryDate,
            @RequestParam(value = "commentForPatient", required = false) String[] commentForPatient) throws ParseException {

        /*
          Get the list of drugs the Patient is allergic to
        */
        Allergies allergies = patientService.getAllergies(patient);
        ArrayList<String> allergicDrugList = new ArrayList<>();
        
        if(allergies.size() > 0){
            int count = 0;
            for(Allergy allergy : allergies){
                allergicDrugList.add(allergy.getAllergen().toString().toUpperCase());
                if(count<allergies.size()-1){
                    allergicDrugList.add(",");
                    count++;
                }
            }
            model.addAttribute("allergicDrugs", allergicDrugList);
        } else {
            model.addAttribute("allergicDrugs", "null");
        }
        
        if (StringUtils.isNotBlank(action)) {
            try {
                /*
                  If an action (Dispatch, On-Hold, Discard is selected and confirmed, take actions.
                */
                if ("Confirm".equals(action)) {
                    /*
                      If one or more check-boxes corresponding to a drug or a drug order is checked, retrieve the order ID.
                    */
                    if(groupCheckBox.length > 0){
                        boolean dispatched = false, notDispatched = false;
                        
                        for(int i=0;i<groupCheckBox.length;i++){
                            
                            int orderID = Integer.parseInt(Long.toString(groupCheckBox[i]));
                            drugorders drugorder = Context.getService(drugordersService.class).getDrugOrderByOrderID(orderID);
                            
                            /*
                              If the order is selected to be put on hold or requested to be discarded,
                               apply the appropriate status on the order and save the comments.
                            */
                            switch (groupAction) {
                                case "Discard":
                                    drugorder.setForDiscard(1);
                                    // If order was previously set on hold, remove the on-hold flag.
                                    if(drugorder.getOnHold() == 1)
                                        drugorder.setOnHold(0);
                                    // If comments to discard the order are provided, save the comments.
                                    if(groupComments != null){
                                        // Fix saving multiple lines of text input.
                                        String [] comments = groupComments.trim().split("\n");
                                        StringBuilder sb = new StringBuilder();
                                        for(String s : comments){
                                            sb.append(s.trim()).append("newline");
                                        }
                                        drugorder.setCommentForOrderer(sb.substring(0, sb.length()-7)); 
                                    }

                                    break;

                                case "On Hold":
                                    // If the order was previously set to be discarded, it cannot be modified.
                                    if(drugorder.getForDiscard()== 1){
                                        InfoErrorMessageUtil.flashErrorMessage(session, "Order(s) selected to be discontinued cannot be modified!");
                                        return;
                                    }
                                    
                                    drugorder.setOnHold(1);
                                    // If comments to put the order on hold are provided, save the comments.
                                    if(groupComments != null){
                                        // Fix saving multiple lines of text input.
                                        String [] comments = groupComments.trim().split("\n");
                                        StringBuilder sb = new StringBuilder();
                                        for(String s : comments){
                                            sb.append(s.trim()).append("newline");
                                        }
                                        drugorder.setCommentForOrderer(sb.substring(0, sb.length()-7)); 
                                    }

                                    break;

                                case "Dispatch":
                                    // If the order was previously set to be discarded, it cannot be modified.
                                    if(drugorder.getForDiscard() == 1){
                                        InfoErrorMessageUtil.flashErrorMessage(session, "Order(s) selected to be discontinued cannot be modified!");
                                        return;
                                    }
                                        
                                    // Check if the drug is allergic but has no order reasons specified.
                                    if(!(drugorder.getIsAllergicOrderReasons() == null && allergicDrugList.contains(drugorder.getDrugName().getDisplayString().toUpperCase()))){
                                        // If order was previously set on hold, remove the on-hold flag.
                                        if(drugorder.getOnHold() == 1)
                                            drugorder.setOnHold(0);
                                        /*
                                          If the order is selected to be dispatched, set the last dispatch date and decrement the allowed number of refills.
                                          Update the order status.
                                        */
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
                                        }   
                                        // Save the drug expiry date and comments entered for the Patient.
                                        drugorder.setDrugExpiryDate(new SimpleDateFormat("MM/dd/yyyy").parse(drugExpiryDate[i]));

                                        // Fix saving multiple lines of text input.
                                        String [] comments = commentForPatient[i].trim().split("\n");
                                        StringBuilder sb = new StringBuilder();
                                        for(String s : comments){
                                            sb.append(s.trim()).append("newline");
                                        }
                                        drugorder.setCommentForPatient(sb.substring(0, sb.length()-7));

                                        dispatched = true;
                                    } 
                                    else {
                                        InfoErrorMessageUtil.flashErrorMessage(session, "Cannot dispatch allergic drug(s) without valid reasons.");
                                        notDispatched = true;
                                    }
                                    
                                    break;
                            }
                            
                            Context.getService(drugordersService.class).saveDrugOrder(drugorder);
                        }
                        if(!(dispatched == false && notDispatched == true))
                            InfoErrorMessageUtil.flashInfoMessage(session, "Order Status - " + groupAction);
                    }
                }
            } 
            catch (NumberFormatException | APIException ex) {
                Logger.getLogger(PharmacyPatientPageController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        model.addAttribute("group_order_status", groupAction);
    }
}