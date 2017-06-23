/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.drugorders.page.controller;

/**
 *
 * @author harini-geek
 */

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import org.openmrs.CareSetting;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.EncounterRole;
import org.openmrs.OrderFrequency;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.allergyapi.Allergies;
import org.openmrs.module.allergyapi.Allergy;
import org.openmrs.module.allergyapi.api.PatientService;
import org.openmrs.module.drugorders.api.drugordersService;
import org.openmrs.module.drugorders.api.newplansService;
import org.openmrs.module.drugorders.api.planordersService;
import org.openmrs.module.drugorders.api.standardplansService;
import org.openmrs.module.drugorders.drugorders;
import org.openmrs.module.drugorders.planorders;
import org.openmrs.module.drugorders.standardplans;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.module.uicommons.util.InfoErrorMessageUtil;
import org.springframework.web.bind.annotation.RequestParam;

public class DrugordersHomePageController {
    
    /*
      Using the @RequestParam annotation, we access the values entered in the 'Create Drug Order', 'Select Med Plans', 'Discontinue/Renew Drug Order' forms.
    */
    public void controller( PageModel model, @RequestParam("patientId") Patient patient, HttpSession session,
                            @SpringBean("allergyService") PatientService patientService, 
                            @RequestParam(value = "drugName", required = false) String drugName,
                            @RequestParam(value = "startDate", required = false) Date startDate,
                            @RequestParam(value = "route", required = false) String route,
                            @RequestParam(value = "dose", required = false) String dose, 
                            @RequestParam(value = "doseUnits", required = false) String doseUnits,
                            @RequestParam(value = "quantity", required = false) String quantity, 
                            @RequestParam(value = "quantityUnits", required = false) String quantityUnits,
                            @RequestParam(value = "duration", required = false) Integer duration, 
                            @RequestParam(value = "durationUnits", required = false) String durationUnits,
                            @RequestParam(value = "frequency", required = false) String frequency,
                            @RequestParam(value = "priority", required = false) String priority,
                            @RequestParam(value = "refill", required = false) Integer refill,
                            @RequestParam(value = "interval", required = false) Integer interval,
                            @RequestParam(value = "diagnosis", required = false) String diagnosis,
                            @RequestParam(value = "orderReason", required = false) String orderReason,
                            @RequestParam(value = "patientInstrn", required = false) String patientInstrn, 
                            @RequestParam(value = "pharmacistInstrn", required = false) String pharmacistInstrn,
                            @RequestParam(value = "codedDiscardReason", required = false) String codedDiscardReason,
                            @RequestParam(value = "nonCodedDiscardReason", required = false) String nonCodedDiscardReason,
                            @RequestParam(value = "action", required = false) String action,
                            @RequestParam(value = "orderId", required = false) Integer orderId,
                            @RequestParam(value = "groupOrderID", required = false) Integer groupOrderID,
                            @RequestParam(value = "groupCheckBox", required=false) long[] groupCheckBox,
                            @RequestParam(value = "selectedPlan", required = false) String selectedPlan,
                            @RequestParam(value = "planOrderReason", required = false) String[] planOrderReason,
                            @RequestParam(value = "reviseOrderReason", required = false) String[] reviseOrderReason) {

        int patientID = patient.getPatientId();
        drugName = drugName.trim();
        diagnosis = diagnosis.trim();
        
        /*
          Get the list of drugs the Patient is allergic to
        */
        Allergies allergies = patientService.getAllergies(patient);
        ArrayList<String> allergicDrugList = new ArrayList<>();
        
        if(allergies.size() > 0){
            for(Allergy allergy : allergies){
                allergicDrugList.add(allergy.getAllergen().toString().toUpperCase());
                model.addAttribute("allergicDrugs", allergicDrugList);
            }
        } else {
            model.addAttribute("allergicDrugs", "null");
        }
        
        List<String> currentOrders = getCurrentDrugOrders(patient);
        
        if (StringUtils.isNotBlank(action)) {
            try {
                if ("CREATE DRUG ORDER".equals(action)) {
                    // Check if the specified drug concept and diagnosis concept exists.
                    if(ConceptName(drugName) == null || ConceptName(diagnosis) == null){
                        InfoErrorMessageUtil.flashErrorMessage(session, "Unrecognized drug or diagnosis! Cannot place order!");
                    } 
                    else {
                        /*
                            Check if an order for the selected drug does not already exist.
                            Ensure that all the required parameters are specified and then create an order.
                        */
                        if(!currentOrders.contains(drugName.toUpperCase())){
                            if (!(drugName.equals("")) && !(route.equals("")) && !(dose.equals("")) && !(doseUnits.equals("")) && !(quantity.equals("")) && !(quantityUnits.equals("")) && !(frequency.equals("")) && (duration != null) && !(durationUnits.equals(""))) {

                                DrugOrder drugOrder = null;
                                drugorders drugorder = null;
                                // Create a DrugOrder record
                                int order = createNewDrugOrder(drugOrder, patient, drugName, route, dose, doseUnits, quantity, quantityUnits, frequency, duration, durationUnits);
                                // Create a drugorders record
                                createDrugOrderExtension(drugorder, order, patientID, drugName, startDate, orderReason, diagnosis, priority, "Active", refill, interval, patientInstrn, pharmacistInstrn);
                                // Add the name of the drug to the list of current drug orders.
                                currentOrders.add(drugName.toUpperCase());
                                // If the orderId property value retrieved is not null, then the order is a created to be a part of an existing order group.
                                if(orderId != null){
                                    Context.getService(drugordersService.class).getDrugOrderByOrderID(order).setGroupId(orderId);
                                    Context.getService(drugordersService.class).getDrugOrderByOrderID(order).setOrderStatus("Active-Group");
                                }
                                InfoErrorMessageUtil.flashInfoMessage(session, "Order Created!");
                            }
                        } else {
                            InfoErrorMessageUtil.flashErrorMessage(session, "Drug already prescribed!");
                        }
                    }              
                }
                
                /*
                  If a medication plan is selected, create an order for all the drugs for which the corresponding checkbox is checked.
                */
                if ("selectMedPlan".equals(action)) {
                    /*
                        If drugs being ordered (as a part of the plan are known to be allergic, 
                         retrieve and save the reasons entered for ordering the drugs.
                    */
                    List<String> allergicPlanOrderReason = new ArrayList<>();
                    for(String reason : planOrderReason){
                        if(!reason.equals(""))
                            allergicPlanOrderReason.add(reason);                
                    }
                    
                    /*
                      If one or more check-boxes (corresponding to a drug) is selected, retrieve the name of the drug.
                    */
                    if(groupCheckBox.length > 0){
                        int planID = Context.getService(planordersService.class).getLastPlanID() + 1;
                        
                        List<String> planOrderList = new ArrayList<>();
                        for(int i=0;i<groupCheckBox.length;i++){
                            planOrderList.add(Long.toString(groupCheckBox[i]));  
                        } 
                        
                        /*
                          Fetch the standard plans (list of generic drug orders) for the selected plan (disease).
                        */
                        List<standardplans> standardPlans = Context.getService(standardplansService.class).getMedPlansByPlanID(Context.getService(newplansService.class).getMedPlanByPlanName(ConceptName(selectedPlan)).getId());
                        for(standardplans standardPlan : standardPlans){
                            // If the given standard plan drug is selected to be ordered, create a drug order for the drug.
                            if(planOrderList.contains(standardPlan.getDrugId().toString())){
                                DrugOrder drugOrder = null;
                                drugorders drugorder = null;
                                
                                // Create a DrugOrder record
                                int order = createNewDrugOrder(drugOrder, patient, standardPlan.getDrugId().getDisplayString(), standardPlan.getRoute().getDisplayString(), standardPlan.getDose().toString(), standardPlan.getDoseUnits().getDisplayString(), standardPlan.getQuantity().toString(), standardPlan.getQuantityUnits().getDisplayString(), standardPlan.getFrequency().getName(), standardPlan.getDuration(), standardPlan.getDurationUnits().getDisplayString());
                                // Create a drugorders record
                                createDrugOrderExtension(drugorder, order, patientID, standardPlan.getDrugId().getDisplayString(), startDate, "", selectedPlan, priority, "Draft-Plan", 0, 0, patientInstrn, pharmacistInstrn);
                                // Add the name of the drug to the list of current drug orders.
                                currentOrders.add(standardPlan.getDrugId().getDisplayString().toUpperCase());
                                
                                Context.getService(drugordersService.class).getDrugOrderByOrderID(order).setPriority(ConceptName("High")); // Set the default priority to 'High'.
                                Context.getService(drugordersService.class).getDrugOrderByOrderID(order).setStartDate(Calendar.getInstance().getTime()); // Set the default start date to current date.
                                
                                // If a drug that the Patient is allergic to is ordered and the reason for ordering the drug is provided (mandatory), save the reason for ordering the drug.
                                if(allergicDrugList.size() > 0 && allergicPlanOrderReason.size() > 0){
                                    if(allergicDrugList.contains(standardPlan.getDrugId().getDisplayString())){
                                        Context.getService(drugordersService.class).getDrugOrderByOrderID(order).setIsAllergicOrderReasons(allergicPlanOrderReason.get(0));
                                        allergicPlanOrderReason.remove(0);
                                    }
                                }
                                // Create a planorders record.
                                createPlanOrder(order, planID, patientID, selectedPlan);
                            }
                        }
                        InfoErrorMessageUtil.flashInfoMessage(session, "Plan Saved!");
                    }
                }
                
                /*
                  If one or more check-boxes (corresponding to a drug order) is selected, retrieve the order ID.
                  Discontinue the selected order. Save the reason for discontinuing the order and set status to Non-Active.
                */
                if ("DISCONTINUE ORDER".equals(action)){
                    if(groupCheckBox.length > 0){
                        // Retrieve the order ID from the check-box which holds the order ID as a value.
                        int id = Integer.parseInt(Long.toString(groupCheckBox[0]));
                        drugorders order = Context.getService(drugordersService.class).getDrugOrderByOrderID(id);
                        
                        // Remove the name of the drug from the list of current drug orders.
                        currentOrders.remove(order.getDrugName().getDisplayString().toUpperCase());
                        order.setOrderStatus("Non-Active");
                        // Remove this order from the group or the plan if it is a part of a group or a plan.
                        if(order.getGroupId() != null)
                            order.setGroupId(null);
                        if(Context.getService(planordersService.class).getPlanOrderByOrderID(id) != null)
                            Context.getService(planordersService.class).getPlanOrderByOrderID(id).setPlanId(null);
                        // Set the reason for discontinuing the order.
                        setDiscontinueReason(order, codedDiscardReason, nonCodedDiscardReason);
                        // Void the drug order.
                        Context.getOrderService().voidOrder(Context.getOrderService().getOrder(id), "Discontinued");
                        InfoErrorMessageUtil.flashInfoMessage(session, "Order Discontinued!");
                    }
                }
                
                /*
                  If one or more check-boxes (corresponding to a drug order) is selected, retrieve the order ID.
                  Discontinue all the orders in the selected order group. 
                  Save the reason for discontinuing the orders and set status to Non-Active.
                */
                if ("DISCARD ORDER GROUP".equals(action)) {
                    // Calculate the number of drug orders in the selected order group.
                    int ordersInGrp = Context.getService(drugordersService.class).getDrugOrdersByGroupID(groupOrderID).size();
                    
                    if(groupCheckBox.length > 0){
                        // Retrieve the list of orders to be discarded from the check-boxes which hold the order ID as a value.
                        for(int i=0;i<groupCheckBox.length;i++){
                            int id = Integer.parseInt(Long.toString(groupCheckBox[i]));
                            drugorders order = Context.getService(drugordersService.class).getDrugOrderByOrderID(id);
                            // If all the orders in the group are selected to be discarded, set the order status as 'Non-Active-Group' else, set the order status as 'Non-Active'.
                            if(ordersInGrp == groupCheckBox.length)
                                order.setOrderStatus("Non-Active-Group");
                            else {
                                order.setOrderStatus("Non-Active");
                                order.setGroupId(null);
                            }                                
                            // Remove the name of the drug from the list of current drug orders.
                            currentOrders.remove(order.getDrugName().getDisplayString().toUpperCase());
                            // Set the reason for discontinuing the order.
                            setDiscontinueReason(order, codedDiscardReason, nonCodedDiscardReason);
                            Context.getOrderService().voidOrder(Context.getOrderService().getOrder(order.getOrderId()), "Discontinued");
                        }
                        InfoErrorMessageUtil.flashInfoMessage(session, "Orders Discontinued!");
                    }
                }
                
                /*
                  If one or more check-boxes (corresponding to a drug order) is selected, retrieve the order ID.
                */
                if ("RENEW ORDER GROUP".equals(action)) {
                    if(groupCheckBox.length > 0){
                        // Set a Group ID.
                        int groupID = Context.getService(drugordersService.class).getLastGroupID() + 1;
                        
                        // If the drug is identified to be allergic, save the reason for ordering the drug and create an order.
                        List<String> allergicPlanOrderReason = new ArrayList<>();
                        for(String reason : reviseOrderReason){
                            if(!reason.equals(""))
                                allergicPlanOrderReason.add(reason);                
                        }
                    
                        for(int i=0;i<groupCheckBox.length;i++){
                            // Retrieve the original order records to create new orders with the same specifications.
                            int id = Integer.parseInt(Long.toString(groupCheckBox[i]));
                            DrugOrder orderMain = (DrugOrder) Context.getOrderService().getOrder(id);
                            drugorders orderExtn = Context.getService(drugordersService.class).getDrugOrderByOrderID(id);
                            
                            DrugOrder drugOrder = null;
                            drugorders drugorder = null;
                            // Create a DrugOrder record..
                            int order = createNewDrugOrder(drugOrder, patient, orderExtn.getDrugName().getDisplayString(), orderMain.getRoute().getDisplayString(), orderMain.getDose().toString(), orderMain.getDoseUnits().getDisplayString(), orderMain.getQuantity().toString(), orderMain.getQuantityUnits().getDisplayString(), orderMain.getFrequency().getName(), orderMain.getDuration(), orderMain.getDurationUnits().getDisplayString());
                            // Create a drugorders record
                            createDrugOrderExtension(drugorder, order, patientID, orderExtn.getDrugName().getDisplayString(), Calendar.getInstance().getTime(), "", orderExtn.getAssociatedDiagnosis().getDisplayString(), orderExtn.getPriority().getDisplayString(), "Active-Group", orderExtn.getRefill(), orderExtn.getRefillInterval(), "", "");
                            // Add the name of the drug to the list of current drug orders.
                            currentOrders.add(orderExtn.getDrugName().getDisplayString().toUpperCase());
                                    
                            Context.getService(drugordersService.class).getDrugOrderByOrderID(order).setGroupId(groupID);
                            Context.getService(drugordersService.class).getDrugOrderByOrderID(order).setOrderStatus("Active-Group");
                        
                            // If a drug that the Patient is allergic to is ordered and the reason for ordering the drug is provided (mandatory), save the reason for ordering the drug.
                            if(allergicDrugList.size() > 0 && allergicPlanOrderReason.size() > 0){
                                if(allergicDrugList.contains(orderExtn.getDrugName().getDisplayString())){
                                    Context.getService(drugordersService.class).getDrugOrderByOrderID(order).setIsAllergicOrderReasons(allergicPlanOrderReason.get(0));
                                    allergicPlanOrderReason.remove(0);
                                }
                            }
                        }
                        InfoErrorMessageUtil.flashInfoMessage(session, "Orders Renewed!");
                    }
                }
                
                /*
                  If one or more check-boxes (corresponding to a drug order) is selected, retrieve the order ID.
                  Discontinue all the orders in the selected medication plan.
                  Save the reason for discontinuing the orders and set status to Non-Active.
                */
                if ("DISCARD MED PLAN".equals(action)){
                    // Calculate the number of drug orders in the selected medication plans.
                    int ordersInPlan = Context.getService(planordersService.class).getPlanOrdersByPlanID(groupOrderID).size();
                    
                    if(groupCheckBox.length > 0){
                        // Retrieve the list of orders to be discarded from the check-boxes which hold the order ID as a value.
                        for(int i=0;i<groupCheckBox.length;i++){
                            int id = Integer.parseInt(Long.toString(groupCheckBox[i]));
                            drugorders order = Context.getService(drugordersService.class).getDrugOrderByOrderID(id);
                            
                            // If all the orders in the plan order are selected to be discarded, set the order status as 'Non-Active-Plan' else, set the order status as 'Non-Active'.
                            if(ordersInPlan == groupCheckBox.length)
                                order.setOrderStatus("Non-Active-Plan");
                            else {
                                order.setOrderStatus("Non-Active");
                                Context.getService(planordersService.class).getPlanOrderByOrderID(id).setPlanId(null);
                            }                                
                            // Remove the name of the drug from the list of current drug orders.
                            currentOrders.remove(order.getDrugName().getDisplayString().toUpperCase());
                            // Set the reason for discontinuing the order.
                            setDiscontinueReason(order, codedDiscardReason, nonCodedDiscardReason);
                            Context.getOrderService().voidOrder(Context.getOrderService().getOrder(order.getOrderId()), "Discontinued");
                        }
                        InfoErrorMessageUtil.flashInfoMessage(session, "Orders Discontinued!");
                    }
                }
                
                /*
                  If one or more check-boxes (corresponding to a drug order) is selected, retrieve the order ID.
                  Renew selected orders from the set of orders in the plan.
                */
                if ("RENEW MED PLAN".equals(action)){
                    if(groupCheckBox.length > 0){
                        int planID = Context.getService(planordersService.class).getLastPlanID() + 1;
                        
                        //If the drug is identified to be allergic, save the reason for ordering the drug and create an order.
                        List<String> allergicPlanOrderReason = new ArrayList<>();
                        for(String reason : reviseOrderReason){
                            if(!reason.equals(""))
                                allergicPlanOrderReason.add(reason);                
                        }
                        
                        for(int i=0;i<groupCheckBox.length;i++){
                            // Retrieve the original order records to create new orders with the same specifications.
                            int id = Integer.parseInt(Long.toString(groupCheckBox[i]));
                            DrugOrder orderMain = (DrugOrder) Context.getOrderService().getOrder(id);
                            drugorders orderExtn = Context.getService(drugordersService.class).getDrugOrderByOrderID(id);

                            DrugOrder drugOrder = null;
                            drugorders drugorder = null;

                            // Create a DrugOrder record.
                            int order = createNewDrugOrder(drugOrder, patient, orderExtn.getDrugName().getDisplayString(), orderMain.getRoute().getDisplayString(), orderMain.getDose().toString(), orderMain.getDoseUnits().getDisplayString(), orderMain.getQuantity().toString(), orderMain.getQuantityUnits().getDisplayString(), orderMain.getFrequency().getName(), orderMain.getDuration(), orderMain.getDurationUnits().getDisplayString());
                            // Create a drugorders record.
                            createDrugOrderExtension(drugorder, order, patientID, orderExtn.getDrugName().getDisplayString(), Calendar.getInstance().getTime(), "", orderExtn.getAssociatedDiagnosis().getDisplayString(), orderExtn.getPriority().getDisplayString(), "Draft-Plan", orderExtn.getRefill(), orderExtn.getRefillInterval(), "", "");
                            // Create an entry in the Plan Orders table.
                            createPlanOrder(order, planID, patientID, orderExtn.getAssociatedDiagnosis().getDisplayString());
                            // Add the name of the drug to the list of current drug orders.
                            currentOrders.add(orderExtn.getDrugName().getDisplayString().toUpperCase());
                            
                            // If a drug that the Patient is allergic to is ordered and the reason for ordering the drug is provided (mandatory), save the reason for ordering the drug.
                            if(allergicDrugList.size() > 0 && allergicPlanOrderReason.size() > 0){
                                if(allergicDrugList.contains(orderExtn.getDrugName().getDisplayString())){
                                    Context.getService(drugordersService.class).getDrugOrderByOrderID(order).setIsAllergicOrderReasons(allergicPlanOrderReason.get(0));
                                    allergicPlanOrderReason.remove(0);
                                }
                            }
                        }
                        InfoErrorMessageUtil.flashInfoMessage(session, "Plan Renewed!");
                    }                        
                }
                
                /*
                  Edit the drug order. This will void the previous order and create a new drug order.
                */
                if ("EDIT DRUG ORDER".equals(action)) {
                    // Check if the specified drug concept and diagnosis concept exists.
                    if(ConceptName(drugName) == null || ConceptName(diagnosis) == null){
                        InfoErrorMessageUtil.flashErrorMessage(session, "Unrecognized drug or diagnosis! Cannot place order!");
                    }
                    else {
                        // Retrieve the original order record to create a new order with the same specifications.
                        drugorders originalOrder = Context.getService(drugordersService.class).getDrugOrderByOrderID(orderId);
                        // Void the original order
                        Context.getOrderService().voidOrder(Context.getOrderService().getOrder(orderId), "Discontinued");

                        DrugOrder drugOrder = null;
                        drugorders drugorder = null;

                        // Create a DrugOrder record.
                        int order = createNewDrugOrder(drugOrder, patient, drugName, route, dose, doseUnits, quantity, quantityUnits, frequency, duration, durationUnits);
                        // Create a drugorders record.
                        createDrugOrderExtension(drugorder, order, patientID, drugName, startDate, orderReason, diagnosis, priority, "Active", refill, interval, patientInstrn, pharmacistInstrn);
                        // Remove the name of the drug from the original order from the list of current drug orders.
                        currentOrders.remove(originalOrder.getDrugName().getDisplayString().toUpperCase());
                        // Add the name of the selected drug to the list of current drug orders.
                        currentOrders.add(drugName.toUpperCase());

                        /*
                          When editing an individual order, ensure to record its status as it was before to ensure that the Single, Group and Med Plan orders are segregated.
                        */
                        switch (originalOrder.getOrderStatus()) {
                            case "Active":
                                Context.getService(drugordersService.class).getDrugOrderByOrderID(order).setOrderStatus("Active");
                                break;
                            case "Active-Plan":
                                Context.getService(drugordersService.class).getDrugOrderByOrderID(order).setOrderStatus("Active-Plan");
                                Context.getService(planordersService.class).getPlanOrderByOrderID(originalOrder.getOrderId()).setOrderId(order);
                                break;
                            case "Draft-Plan":
                                Context.getService(drugordersService.class).getDrugOrderByOrderID(order).setOrderStatus("Draft-Plan");
                                Context.getService(planordersService.class).getPlanOrderByOrderID(originalOrder.getOrderId()).setOrderId(order);
                                break;
                            case "Active-Group":
                                Context.getService(drugordersService.class).getDrugOrderByOrderID(order).setOrderStatus("Active-Group");
                                Context.getService(drugordersService.class).getDrugOrderByOrderID(order).setGroupId(originalOrder.getGroupId());
                                originalOrder.setGroupId(null);
                                break;
                        }
                        originalOrder.setOrderStatus("Non-Active");
                        InfoErrorMessageUtil.flashInfoMessage(session, "Order Changes Saved!");
                    }
                    
                }

                /*
                  Renew an individual drug order with the selected standard order composition.
                */
                if ("RENEW DRUG ORDER".equals(action)) {
                    // Check if the specified drug concept and diagnosis concept exists.
                    if(ConceptName(drugName) == null || ConceptName(diagnosis) == null){
                        InfoErrorMessageUtil.flashErrorMessage(session, "Unrecognized drug or diagnosis! Cannot place order!");
                    } 
                    else {
                        // Retrieve the original order record to create a new order with the same specifications.
                        drugorders originalOrder = Context.getService(drugordersService.class).getDrugOrderByOrderID(orderId);
                        // Fetch the name of the drug from the original order.
                        String name = originalOrder.getDrugName().getDisplayString();
                        // If no active order for the give drug currently exists, create a new drug order.
                        if(!currentOrders.contains(name.toUpperCase())){
                            DrugOrder drugOrder = null;
                            drugorders drugorder = null;

                            // Create a DrugOrder record.
                            int order = createNewDrugOrder(drugOrder, patient, name, route, dose, doseUnits, quantity, quantityUnits, frequency, duration, durationUnits);
                            // Create a drugorders record.
                            createDrugOrderExtension(drugorder, order, patientID, name, startDate, orderReason, diagnosis, priority, "Active", refill, interval, patientInstrn, pharmacistInstrn);
                            // Add the name of the drug to the list of current drug orders.
                            currentOrders.add(name.toUpperCase());

                            InfoErrorMessageUtil.flashInfoMessage(session, "Order Renewed!");
                        } else {
                            InfoErrorMessageUtil.flashErrorMessage(session, "Drug already prescribed!");
                        }
                    }                  
                }   
                
                /*
                  Save the draft of orders created for medication plan drugs.
                */
                if ("saveDraft".equals(action)){
                    List<drugorders> draftOrders = Context.getService(drugordersService.class).getDrugOrdersByPatientAndStatus(patient, "Draft-Plan");
                    for(drugorders draftOrder : draftOrders)
                        draftOrder.setOrderStatus("Active-Plan");
                }
                
            } catch (APIException | NumberFormatException e) {
                System.out.println(e.toString());
            }
        }
        
        model.addAttribute("currentOrders", currentOrders);
    }

    /*
      This function will save the standard parameters associated with the Order and Drug Order class.
      It will save the order's basic parameters in the drug_orders table and create an Drug Order record.
    */
    private int createNewDrugOrder( DrugOrder order, Patient patient, String drugNameConfirmed, String route,
                                    String dose, String doseUnits, String quantity, String quantityUnits,
                                    String frequency, Integer duration, String durationUnits) {

        order = new DrugOrder();
        // Save the drug concept associated with the order.
        order.setConcept(ConceptName(drugNameConfirmed));
        order.setDrug(Context.getConceptService().getDrugByNameOrId(drugNameConfirmed));
                  
        // Save the care setting.
        CareSetting careSetting = Context.getOrderService().getCareSettingByName("Outpatient");
        order.setCareSetting(careSetting);
        
        // Save the encounter, provider details associated with the order.
        Encounter enc = new Encounter();
        enc.setEncounterDatetime(new Date());
        enc.setPatient(patient);
        enc.setEncounterType(Context.getEncounterService().getEncounterType("Visit Note"));
        enc.setLocation(Context.getLocationService().getDefaultLocation());
        Provider provider = Context.getProviderService().getProviderByIdentifier("doctor");
        EncounterRole encRole = Context.getEncounterService().getEncounterRoleByName("Clinician");
        enc.setProvider(encRole, provider);
        enc = (Encounter) Context.getEncounterService().saveEncounter(enc);

        order.setPatient(patient);
        order.setEncounter(enc);
        order.setOrderer(provider);

        order.setRoute(ConceptName(route));
        order.setDose(Double.valueOf(dose));
        order.setDoseUnits(ConceptName(doseUnits));
        order.setQuantity(Double.valueOf(quantity));
        order.setQuantityUnits(ConceptName(quantityUnits));
        order.setDuration(duration);
        order.setDurationUnits(ConceptName(durationUnits));
        OrderFrequency orderFrequency = Context.getOrderService().getOrderFrequencyByConcept(ConceptName(frequency));
        order.setFrequency(orderFrequency);
        order.setNumRefills(0);
        order = (DrugOrder) Context.getOrderService().saveOrder(order, null);
        int orderID = order.getOrderId();
        return orderID; 
    }
    
    /*
      This function will save the additional parameters associated with the drug orders.
      It saves data in the drug_order_extn table.
    */
    private void createDrugOrderExtension(drugorders drugorder, int drugOrderID, int patientID, String drugName, Date startDate, String orderReason, String diagnosis, String priority, String status, int refill, int interval, String patientInstrn, String pharmacistInstrn){
        drugorder = new drugorders();
        drugorder.setOrderId(drugOrderID);
        drugorder.setDrugName(ConceptName(drugName));
        drugorder.setStartDate(startDate);
        drugorder.setPatientId(patientID);
        drugorder.setRefill(refill);
        drugorder.setRefillInterval(interval);
        drugorder.setOrderStatus(status);
        drugorder.setPriority(ConceptName(priority));
        drugorder.setOnHold(0);
        drugorder.setForDiscard(0);
        
        // Save the diagnosis concept associated with the order.
        drugorder.setAssociatedDiagnosis(ConceptName(diagnosis));
            
        if(!(orderReason).equals(""))
            drugorder.setIsAllergicOrderReasons(orderReason);
        if(!(patientInstrn).equals(""))
            drugorder.setPatientInstructions(patientInstrn);
        if(!(pharmacistInstrn).equals(""))
            drugorder.setPharmacistInstructions(pharmacistInstrn);
        
        Context.getService(drugordersService.class).saveDrugOrder(drugorder);
    }
    
    /*
      This function will record an order placed for a drug that is a part of a medication plan / regimen.
    */
    private void createPlanOrder(int drugOrderID, int planID, int patientID, String diseaseName){
        
        planorders diseaseDrugOrder = new planorders();
        diseaseDrugOrder.setPlanId(planID);
        diseaseDrugOrder.setOrderId(drugOrderID);
        diseaseDrugOrder.setPatientId(patientID);
        diseaseDrugOrder.setDiseaseId(ConceptName(diseaseName));
        Context.getService(planordersService.class).savePlanOrder(diseaseDrugOrder);
        
    }
    
    // Get Concept ID by name
    private Concept ConceptName(String conceptString){
        return Context.getConceptService().getConceptByName(conceptString);
    }
    
    /*
      Set the reason for discontinuing the drug.
      Save the coded concept or the non-coded concept, whatever is provided.
    */
    private void setDiscontinueReason(drugorders drugorder, String coded, String nonCoded){
        if(!(coded.equalsIgnoreCase(""))){
            drugorder.setDiscontinueReason(ConceptName(coded.trim()));
        }
        else if(!(nonCoded.equals(""))){
            drugorder.setDiscontinuationReasons(nonCoded);
        }
    }
    
    /*
      Retrieve current active drug orders for the Patient.
    */
    private List<String> getCurrentDrugOrders(Patient patient){
        
        List<String> drugOrders = new ArrayList<>();
        List<drugorders> orders;
        
        // Retrieve orders in "Active" status
        orders = getOrders(patient, "Active");
        for(drugorders order : orders) {
            drugOrders.add(order.getDrugName().getDisplayString().toUpperCase().trim());
        }
        // Retrieve orders in "Active-Plan" status
        orders = getOrders(patient, "Active-Plan");
        for(drugorders order : orders) {
            drugOrders.add(order.getDrugName().getDisplayString().toUpperCase().trim());
        }
        // Retrieve orders in "Draft-Plan" status
        orders = getOrders(patient, "Draft-Plan");
        for(drugorders order : orders) {
            drugOrders.add(order.getDrugName().getDisplayString().toUpperCase().trim());
        }
        // Retrieve orders in "Active-Group" status
        orders = getOrders(patient, "Active-Group");
        for(drugorders order : orders) {
            drugOrders.add(order.getDrugName().getDisplayString().toUpperCase().trim());
        }
        
        return drugOrders;
    }
    
    private List<drugorders> getOrders(Patient patient, String status){
        return Context.getService(drugordersService.class).getDrugOrdersByPatientAndStatus(patient, status);
    }
}