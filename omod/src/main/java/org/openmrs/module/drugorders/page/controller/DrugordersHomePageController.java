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

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import org.openmrs.CareSetting;
import org.openmrs.Concept;
import org.openmrs.Drug;
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
import org.openmrs.module.drugorders.drugordersActivator;
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
                allergicDrugList.add(allergy.getAllergen().toString());
                model.addAttribute("allergicDrugs", allergicDrugList);
            }
        } else {
            model.addAttribute("allergicDrugs", "null");
        }
        
        List<String> currentOrders = getCurrentDrugOrders(patient);
        
        if (StringUtils.isNotBlank(action)) {
            try {
                if ("CREATE DRUG ORDER".equals(action)) {
                    /*
                      Check if an order for the selected drug does not already exist.
                      Ensure that all the required parameters are specified and then create an order.
                    */
                    if(!currentOrders.contains(drugName)){
                        if (!(drugName.equals("")) && !(route.equals("")) && !(dose.equals("")) && !(doseUnits.equals("")) && !(quantity.equals("")) && !(quantityUnits.equals("")) && !(frequency.equals("")) && (duration != null) && !(durationUnits.equals(""))) {
                        
                            DrugOrder drugOrder = null;
                            drugorders drugorder = null;
                            int order = createNewDrugOrder(drugOrder, patient, drugName, route, dose, doseUnits, quantity, quantityUnits, frequency, duration, durationUnits);
                            createDrugOrderExtension(drugorder, order, patientID, drugName, startDate, orderReason, diagnosis, priority, "Active", refill, interval, patientInstrn, pharmacistInstrn);
                            currentOrders.add(drugName);
                            
                            if(orderId != null){
                                Context.getService(drugordersService.class).getDrugOrderByOrderID(order).setGroupId(orderId);
                                Context.getService(drugordersService.class).getDrugOrderByOrderID(order).setOrderStatus("Active-Group");
                            }
                            InfoErrorMessageUtil.flashInfoMessage(session, "Order Created!");
                        }
                    } else {
                        InfoErrorMessageUtil.flashInfoMessage(session, "Drug already prescribed!");
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
                          Create a drug order for each drug that is checked to be ordered.
                        */
                        List<standardplans> standardPlans = Context.getService(standardplansService.class).getMedPlansByPlanID(Context.getService(newplansService.class).getMedPlanByPlanName(ConceptName(selectedPlan)).getId());
                        for(standardplans standardPlan : standardPlans){
                            
                            if(planOrderList.contains(standardPlan.getDrugId().toString())){
                                DrugOrder drugOrder = null;
                                drugorders drugorder = null;                            

                                int order = createNewDrugOrder(drugOrder, patient, standardPlan.getDrugId().getDisplayString(), standardPlan.getRoute().getDisplayString(), standardPlan.getDose().toString(), standardPlan.getDoseUnits().getDisplayString(), standardPlan.getQuantity().toString(), standardPlan.getQuantityUnits().getDisplayString(), standardPlan.getFrequency().getName(), standardPlan.getDuration(), standardPlan.getDurationUnits().getDisplayString());
                                createDrugOrderExtension(drugorder, order, patientID, standardPlan.getDrugId().getDisplayString(), startDate, "", selectedPlan, priority, "Draft-Plan", 0, 0, patientInstrn, pharmacistInstrn);
                                currentOrders.add(standardPlan.getDrugId().getDisplayString());
                                
                                Context.getService(drugordersService.class).getDrugOrderByOrderID(order).setPriority(ConceptName("High"));
                                Context.getService(drugordersService.class).getDrugOrderByOrderID(order).setStartDate(Calendar.getInstance().getTime());
                                
                                /*
                                  If the drug is known to be allergic, save the reason for ordering the drug.
                                */
                                if(allergicDrugList.size() > 0 && allergicPlanOrderReason.size() > 0){
                                    if(allergicDrugList.contains(standardPlan.getDrugId().getDisplayString())){
                                        Context.getService(drugordersService.class).getDrugOrderByOrderID(order).setIsAllergicOrderReasons(allergicPlanOrderReason.get(0));
                                        allergicPlanOrderReason.remove(0);
                                    }
                                }
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
                        int id = Integer.parseInt(Long.toString(groupCheckBox[0]));
                        drugorders order = Context.getService(drugordersService.class).getDrugOrderByOrderID(id);
                        order.setOrderStatus("Non-Active");
                        currentOrders.remove(order.getDrugName().getDisplayString());
                        
                        /*
                          Remove this order from a group or plan if it is a part of a group or a plan.
                        */
                        order.setGroupId(null);
                        if(Context.getService(planordersService.class).getPlanOrderByOrderID(id) != null)
                            Context.getService(planordersService.class).getPlanOrderByOrderID(id).setPlanId(null);
                        
                        setDiscontinueReason(order, codedDiscardReason, nonCodedDiscardReason);
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
                    int ordersInGrp = Context.getService(drugordersService.class).getDrugOrdersByGroupID(groupOrderID).size();
                    
                    if(groupCheckBox.length > 0){
                        for(int i=0;i<groupCheckBox.length;i++){
                            int id = Integer.parseInt(Long.toString(groupCheckBox[i]));
                            drugorders order = Context.getService(drugordersService.class).getDrugOrderByOrderID(id);
                            
                            if(ordersInGrp == groupCheckBox.length)
                                order.setOrderStatus("Non-Active-Group");
                            else {
                                order.setOrderStatus("Non-Active");
                                order.setGroupId(null);
                            }                                
                            
                            currentOrders.remove(order.getDrugName().getDisplayString());
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
                    
                        // Renew selected orders from the set of orders in the group.
                        for(int i=0;i<groupCheckBox.length;i++){
                            int id = Integer.parseInt(Long.toString(groupCheckBox[i]));
                            DrugOrder orderMain = (DrugOrder) Context.getOrderService().getOrder(id);
                            drugorders orderExtn = Context.getService(drugordersService.class).getDrugOrderByOrderID(id);
                            
                            DrugOrder drugOrder = null;
                            drugorders drugorder = null;

                            int order = createNewDrugOrder(drugOrder, patient, orderExtn.getDrugName().getDisplayString(), orderMain.getRoute().getDisplayString(), orderMain.getDose().toString(), orderMain.getDoseUnits().getDisplayString(), orderMain.getQuantity().toString(), orderMain.getQuantityUnits().getDisplayString(), orderMain.getFrequency().getName(), orderMain.getDuration(), orderMain.getDurationUnits().getDisplayString());
                            createDrugOrderExtension(drugorder, order, patientID, orderExtn.getDrugName().getDisplayString(), Calendar.getInstance().getTime(), "", orderExtn.getAssociatedDiagnosis().getDisplayString(), orderExtn.getPriority().getDisplayString(), "Active-Group", orderExtn.getRefill(), orderExtn.getRefillInterval(), "", "");
                            currentOrders.add(orderExtn.getDrugName().getDisplayString());
                                    
                            Context.getService(drugordersService.class).getDrugOrderByOrderID(order).setGroupId(groupID);
                            Context.getService(drugordersService.class).getDrugOrderByOrderID(order).setOrderStatus("Active-Group");
                        
                            if(allergicDrugList.size() > 0 && allergicPlanOrderReason.size() > 0){
                                if(allergicDrugList.contains(orderExtn.getDrugName().getDisplayString())){
                                    Context.getService(drugordersService.class).getDrugOrderByOrderID(order).setIsAllergicOrderReasons(allergicPlanOrderReason.get(0));
                                    allergicPlanOrderReason.remove(0);
                                }
                            }
                        }
                        InfoErrorMessageUtil.flashInfoMessage(session, "Orders Saved!");
                    }
                }
                
                /*
                  If one or more check-boxes (corresponding to a drug order) is selected, retrieve the order ID.
                  Discontinue all the orders in the selected medication plan.
                  Save the reason for discontinuing the orders and set status to Non-Active.
                */
                if ("DISCARD MED PLAN".equals(action)){
                    int ordersInPlan = Context.getService(planordersService.class).getPlanOrdersByPlanID(groupOrderID).size();
                    
                    if(groupCheckBox.length > 0){
                        for(int i=0;i<groupCheckBox.length;i++){
                            int id = Integer.parseInt(Long.toString(groupCheckBox[i]));
                            drugorders order = Context.getService(drugordersService.class).getDrugOrderByOrderID(id);
                        
                            if(ordersInPlan == groupCheckBox.length)
                                order.setOrderStatus("Non-Active-Plan");
                            else {
                                order.setOrderStatus("Non-Active");
                                Context.getService(planordersService.class).getPlanOrderByOrderID(id).setPlanId(null);
                            }                                
                            
                            currentOrders.remove(order.getDrugName().getDisplayString());
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
                            int id = Integer.parseInt(Long.toString(groupCheckBox[i]));
                            DrugOrder orderMain = (DrugOrder) Context.getOrderService().getOrder(id);
                            drugorders orderExtn = Context.getService(drugordersService.class).getDrugOrderByOrderID(id);

                            DrugOrder drugOrder = null;
                            drugorders drugorder = null;

                            int order = createNewDrugOrder(drugOrder, patient, orderExtn.getDrugName().getDisplayString(), orderMain.getRoute().getDisplayString(), orderMain.getDose().toString(), orderMain.getDoseUnits().getDisplayString(), orderMain.getQuantity().toString(), orderMain.getQuantityUnits().getDisplayString(), orderMain.getFrequency().getName(), orderMain.getDuration(), orderMain.getDurationUnits().getDisplayString());
                            createDrugOrderExtension(drugorder, order, patientID, orderExtn.getDrugName().getDisplayString(), Calendar.getInstance().getTime(), "", orderExtn.getAssociatedDiagnosis().getDisplayString(), orderExtn.getPriority().getDisplayString(), "Draft-Plan", orderExtn.getRefill(), orderExtn.getRefillInterval(), "", "");
                            
                            // Create an entry in the Plan Orders table.
                            createPlanOrder(order, planID, patientID, orderExtn.getAssociatedDiagnosis().getDisplayString());
                            currentOrders.add(orderExtn.getDrugName().getDisplayString());
                            
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
                    
                    drugorders originalOrder = Context.getService(drugordersService.class).getDrugOrderByOrderID(orderId);
                    Context.getOrderService().voidOrder(Context.getOrderService().getOrder(orderId), "Discontinued");

                    DrugOrder drugOrder = null;
                    drugorders drugorder = null;

                    int order = createNewDrugOrder(drugOrder, patient, drugName, route, dose, doseUnits, quantity, quantityUnits, frequency, duration, durationUnits);
                    createDrugOrderExtension(drugorder, order, patientID, drugName, startDate, orderReason, diagnosis, priority, "Active", refill, interval, patientInstrn, pharmacistInstrn);
                    currentOrders.remove(originalOrder.getDrugName().getDisplayString());
                    currentOrders.add(drugName);
                    
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

                /*
                  Renew an individual drug order with the selected standard order composition.
                */
                if ("RENEW DRUG ORDER".equals(action)) {
                    
                    drugorders originalOrder = Context.getService(drugordersService.class).getDrugOrderByOrderID(orderId);
                    String name = originalOrder.getDrugName().getDisplayString();
                    if(!currentOrders.contains(name)){
                        DrugOrder drugOrder = null;
                        drugorders drugorder = null;
                        
                        int order = createNewDrugOrder(drugOrder, patient, name, route, dose, doseUnits, quantity, quantityUnits, frequency, duration, durationUnits);
                        createDrugOrderExtension(drugorder, order, patientID, name, startDate, orderReason, diagnosis, priority, "Active", refill, interval, patientInstrn, pharmacistInstrn);
                        currentOrders.add(name);
                        
                        InfoErrorMessageUtil.flashInfoMessage(session, "Order Renewed!");
                    } else {
                        InfoErrorMessageUtil.flashInfoMessage(session, "Drug already prescribed!");
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
        
        if(ConceptName(drugNameConfirmed) == null){
            
            drugordersActivator activator = new drugordersActivator();
            Concept drugConcept =  activator.saveConcept(drugNameConfirmed, Context.getConceptService().getConceptClassByName("Drug"));
            order.setConcept(drugConcept);
            
            // Save the drug concept
            Drug drug = new Drug();
            drug.setName(drugNameConfirmed);
            drug.setConcept(drugConcept);
            Context.getConceptService().saveDrug(drug);
            order.setDrug(drug);
            
        } else {
            order.setConcept(ConceptName(drugNameConfirmed));
            order.setDrug(Context.getConceptService().getDrugByNameOrId(drugNameConfirmed));
        }
                  
        CareSetting careSetting = Context.getOrderService().getCareSettingByName("Outpatient");
        order.setCareSetting(careSetting);

        Date start = defaultStartDate(),
                end = defaultEndDate(start);
        List<Encounter> encs = Context.getEncounterService().getEncounters(patient, null, start, end, null, null, null, null, null, false);

        Encounter encOld = encs.get(0), enc = new Encounter();
        enc.setEncounterDatetime(new Date());
        enc.setPatient(patient);
        enc.setEncounterType(encOld.getEncounterType());
        enc.setLocation(encOld.getLocation());
        List<Provider> provs = Context.getProviderService().getAllProviders();
        Provider provider = provs.get(0);
        EncounterRole encRole = Context.getEncounterService().getEncounterRoleByName("Unknown");
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
        
        if(ConceptName(diagnosis) == null){
            drugordersActivator activator = new drugordersActivator();
            Concept diagnosisConcept =  activator.saveConcept(diagnosis, Context.getConceptService().getConceptClassByName("Diagnosis"));
            drugorder.setAssociatedDiagnosis(diagnosisConcept);
        } else {
            drugorder.setAssociatedDiagnosis(ConceptName(diagnosis));
        }
            
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

    private Date defaultStartDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(2014, 1, 1);
        return cal.getTime();
    }

    private Date defaultEndDate(Date startDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.MILLISECOND, -1);
        cal.set(2014, 12, 22);
        return cal.getTime();
    }

    /*
      Retrieve current active drug orders for the Patient.
    */
    private List<String> getCurrentDrugOrders(Patient patient){
        
        List<String> drugOrders = new ArrayList<>();
        List<drugorders> orders;
        
        orders = getActiveOrders(patient, "Active");
        for(drugorders order : orders) {
            drugOrders.add(order.getDrugName().getDisplayString().trim());
        }
        
        orders = getActiveOrders(patient, "Active-Plan");
        for(drugorders order : orders) {
            drugOrders.add(order.getDrugName().getDisplayString().trim());
        }
        
        orders = getActiveOrders(patient, "Draft-Plan");
        for(drugorders order : orders) {
            drugOrders.add(order.getDrugName().getDisplayString().trim());
        }
        
        orders = getActiveOrders(patient, "Active-Group");
        for(drugorders order : orders) {
            drugOrders.add(order.getDrugName().getDisplayString().trim());
        }
        
        return drugOrders;
    }
    
    private List<drugorders> getActiveOrders(Patient patient, String status){
        return Context.getService(drugordersService.class).getDrugOrdersByPatientAndStatus(patient, status);
    }
}