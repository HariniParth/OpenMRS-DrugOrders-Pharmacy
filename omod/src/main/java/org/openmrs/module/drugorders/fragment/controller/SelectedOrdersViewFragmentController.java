/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.drugorders.fragment.controller;

import java.util.HashMap;
import java.util.List;
import org.openmrs.DrugOrder;
import org.openmrs.Patient;
import java.util.Calendar;
import java.util.Date;
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
import org.openmrs.api.context.Context;
import org.openmrs.module.drugorders.api.drugordersService;
import org.openmrs.module.drugorders.api.planordersService;
import org.openmrs.module.drugorders.drugorders;
import org.openmrs.module.drugorders.page.controller.PharmacyPatientPageController;
import org.openmrs.module.drugorders.planorders;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author harini-parthasarathy
 */
public class SelectedOrdersViewFragmentController {
    
    public void controller(FragmentModel model, @RequestParam("patientId") Patient patient,
                            @RequestParam(value = "planID", required = false) String planID,
                            @RequestParam(value = "groupID", required = false) String groupID,
                            @RequestParam(value = "orderID", required = false) String orderID){
        
        // Get the current date to set the default drug expiry date value as default.
        Calendar cal = Calendar.getInstance();
        Date expiryDate = cal.getTime();
        model.addAttribute("expiryDate", expiryDate);
        
        // Store HashMap<Order-ID, Orderer name>
        HashMap<Integer,String> provider = new HashMap<>();
        // Store HashMap<Order-Id, DrugOrder>
        HashMap<Integer,DrugOrder> groupOrderMain = new HashMap<>();
        // Store HashMap<Order-Id, drugorders>
        HashMap<Integer,drugorders> groupOrderExtn = new HashMap<>();
        // Store the name of the plan.
        StringBuilder planId = new StringBuilder();
        // Store the list of selected drug order IDs.
        StringBuilder orderList = new StringBuilder();
        // Store the details of the selected drug orders.
        StringBuilder orderDetails = new StringBuilder();
        
        if(!planID.equals("")){
            
            // Retrieve the list of Med Plan Orders ordered for this Patient as a part of the selected plan (diagnosis/disease).
            List<planorders> plans = Context.getService(planordersService.class).getPlanOrdersByPlanID(Integer.parseInt(planID));
            // Store the name of the plan.
            planId.append(plans.get(0).getDiseaseId().getDisplayString());
            
            for(planorders plan : plans){
                // Retrieve the order ID record of the order made as a part of the plan.
                drugorders drugorder = Context.getService(drugordersService.class).getDrugOrderByOrderID(plan.getOrderId());
                // From the retrieved med plan order list, select the list of active drug orders.
                if(drugorder.getOrderStatus().equals("Active-Plan")){
                    // Retrieve and Store the corresponding drug_orders table record.
                    DrugOrder DrugOrder = (DrugOrder) Context.getOrderService().getOrder(drugorder.getOrderId());
                    groupOrderMain.put(drugorder.getOrderId(), DrugOrder);
                    // Append the Order ID to the list of selected order IDs.
                    orderList.append(drugorder.getOrderId()).append(" ");
                    // Append the Order details to the list of selected order details.
                    orderDetails = orderDetails.append("Order ID:   ").append(Integer.toString(drugorder.getOrderId())).append("%0ADrug Order: ").append(drugorder.getDrugName().getDisplayString().toUpperCase()).append(" ").append(DrugOrder.getDose()).append(" ").append(DrugOrder.getDoseUnits().getDisplayString()).append("%0AStart Date: ").append(drugorder.getStartDate().toString()).append("%0A%0A");
                    // Store the corresponding drug_orders_extn table record.
                    groupOrderExtn.put(drugorder.getOrderId(), drugorder);
                    // Save the Orderer's name corresponding to the drug order.
                    provider.put(drugorder.getOrderId(), DrugOrder.getOrderer().getPerson().getGivenName() + " " + DrugOrder.getOrderer().getPerson().getFamilyName());                    
                }
            }
        }
        
        if(!groupID.equals("")){
            // Retrieve the list of Drug Orders associated with the given Group Order ID.
            List<drugorders> drugorders = Context.getService(drugordersService.class).getDrugOrdersByGroupID(Integer.parseInt(groupID));
            
            for(drugorders drugorder : drugorders){
                // From the retrieved med plan order list, select the list of active drug orders.
                if(drugorder.getOrderStatus().equals("Active-Group")){
                    // Retrieve and Store the corresponding drug_orders table record.
                    DrugOrder DrugOrder = (DrugOrder) Context.getOrderService().getOrder(drugorder.getOrderId());
                    groupOrderMain.put(DrugOrder.getOrderId(), DrugOrder);
                    // Append the Order ID to the list of selected order IDs.
                    orderList.append(drugorder.getOrderId()).append(" ");
                    // Append the Order details to the list of selected order details.
                    orderDetails = orderDetails.append("Order ID: ").append(Integer.toString(drugorder.getOrderId())).append("%0ADrug: ").append(drugorder.getDrugName().getDisplayString().toUpperCase()).append(" ").append(DrugOrder.getDose()).append(" ").append(DrugOrder.getDoseUnits().getDisplayString()).append("%0AStart Date: ").append(drugorder.getStartDate().toString()).append("%0A%0A");
                    // Store the corresponding drug_orders_extn table record.
                    groupOrderExtn.put(drugorder.getOrderId(), drugorder);
                    // Save the Orderer's name corresponding to the drug order.
                    provider.put(DrugOrder.getOrderId(), DrugOrder.getOrderer().getPerson().getGivenName() + " " + DrugOrder.getOrderer().getPerson().getFamilyName() + ", " + StringUtils.capitalize(DrugOrder.getOrderer().getIdentifier()));
                }
            }
        }
        
        if(!orderID.equals("")){
            // Retrieved the Drug Order associated with the given Order ID
            int order = Integer.parseInt(orderID);
            drugorders drugorder = Context.getService(drugordersService.class).getDrugOrderByOrderID(order);
            // Retrieve and Store the corresponding drug_orders table record.
            DrugOrder DrugOrder = (DrugOrder) Context.getOrderService().getOrder(order);
            groupOrderMain.put(order, DrugOrder);
            // Append the Order ID as the selected order ID.
            orderList.append(drugorder.getOrderId()).append(" ");
            // Append the Order details to the list of selected order details.
            orderDetails = orderDetails.append("Order ID: ").append(Integer.toString(drugorder.getOrderId())).append("%0ADrug: ").append(drugorder.getDrugName().getDisplayString().toUpperCase()).append(" ").append(DrugOrder.getDose()).append(" ").append(DrugOrder.getDoseUnits().getDisplayString()).append("%0AStart Date: ").append(drugorder.getStartDate().toString()).append("%0A%0A");
            // Store the corresponding drug_orders_extn table record.
            groupOrderExtn.put(order, drugorder);
            // Save the Orderer's name corresponding to the drug order.
            provider.put(DrugOrder.getOrderId(), DrugOrder.getOrderer().getPerson().getGivenName() + " " + DrugOrder.getOrderer().getPerson().getFamilyName() + ", " + StringUtils.capitalize(DrugOrder.getOrderer().getIdentifier()));
        }
        
        model.addAttribute("planID", planId.toString());
        model.addAttribute("groupID", groupID);
        model.addAttribute("orderID", orderID);
        
        model.addAttribute("groupOrderMain", groupOrderMain);
        model.addAttribute("groupOrderExtn", groupOrderExtn);
        model.addAttribute("orderDetails", orderDetails);
        model.addAttribute("orderList", orderList);
        model.addAttribute("provider", provider);
        
        model.addAttribute("patientID", patient.getPatientId());
        model.addAttribute("patientName", patient.getGivenName()+" "+patient.getFamilyName());
        model.addAttribute("patientDOB", patient.getBirthdate());
        model.addAttribute("patientAddr", patient.getPersonAddress().getAddress1()+" "+patient.getPersonAddress().getCityVillage()+" "+patient.getPersonAddress().getStateProvince()+" "+patient.getPersonAddress().getPostalCode()+" "+patient.getPersonAddress().getCountry());
    }
    
    /*
      This function will send a command to the printer to print out the details of the drug order that is relavant to the Patient.
    */
    public void printLabel(@RequestParam(value = "comment", required = false) String comment,
                           @RequestParam(value = "order", required = false) Integer orderID,
                           @RequestParam(value = "date", required = false) String date){
        
        // Fetch the details to be printed on the prescription.
        DrugOrder order = (DrugOrder) Context.getOrderService().getOrder(orderID);
        drugorders drugorder = Context.getService(drugordersService.class).getDrugOrderByOrderID(orderID);

        String OrderDetails = drugorder.getDrugName().getDisplayString() + " " + order.getDose() + " " + order.getDoseUnits().getDisplayString() + " " +
                order.getDuration() + " " + order.getDurationUnits().getDisplayString() + " " + order.getQuantity() + " " + order.getQuantityUnits().getDisplayString() + "\n" +
                "Route: " + order.getRoute().getDisplayString() + " " + "Frequency: " + order.getFrequency().getName() + "\n" +
                "Start Date: " + drugorder.getStartDate().toString() + "\n" +
                "Patient Instructions: " + drugorder.getPatientInstructions().replaceAll("newline", "\n") + "\n" +
                "Comments from Pharmacist: " + comment + "\n" +
                "Drug Expiry Date: " + date;
            
        try {
            // Fetch the default print service.
            PrintService service = PrintServiceLookup.lookupDefaultPrintService();
            
            try {
                PrintRequestAttributeSet  pras = new HashPrintRequestAttributeSet();
                pras.add(new Copies(1));
                
                if(service != null){
                    byte[] is = OrderDetails.getBytes();
                    DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
                    Doc doc = new SimpleDoc(is, flavor, null);
                    DocPrintJob job = service.createPrintJob();
                    job.print(doc, pras);
                }
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(PharmacyPatientPageController.class.getName()).log(Level.SEVERE, null, ex);
            } 
            
        } catch (PrintException ex) {
            Logger.getLogger(PharmacyPatientPageController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("Prescription "+OrderDetails);
    }
}