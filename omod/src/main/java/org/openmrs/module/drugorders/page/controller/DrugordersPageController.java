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

public class DrugordersPageController {
    
    
    public void controller(PageModel model, @RequestParam("patientId") Patient patient, 
            @RequestParam(value = "drugNameEntered", required = false) String drugName,
            @RequestParam(value = "startDateEntered", required = false) Date startDate,
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
            @RequestParam(value = "codedReason", required = false) String codedReason,
            @RequestParam(value = "nonCodedReason", required = false) String nonCodedReason,
            @SpringBean("allergyService") PatientService patientService, HttpSession session,
            @RequestParam(value = "action", required = false) String action,
            @RequestParam(value = "order_id", required = false) Integer orderId,
            @RequestParam(value = "groupOrderID", required = false) Integer groupOrderID,
            @RequestParam(value = "groupCheckBox", required=false) long[] groupCheckBox,
            @RequestParam(value = "selectedPlan", required = false) String selectedPlan,
            @RequestParam(value = "planOrderReason", required = false) String[] planOrderReason,
            @RequestParam(value = "reviseOrderReason", required = false) String[] reviseOrderReason) {

        int patientID = patient.getPatientId();
        drugName = drugName.trim();
        diagnosis = diagnosis.trim();
        
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
                
                if ("selectMedPlan".equals(action)) {
                    
                    List<String> allergicPlanOrderReason = new ArrayList<>();
                    for(String reason : planOrderReason){
                        if(!reason.equals(""))
                            allergicPlanOrderReason.add(reason);                
                    }
                    
                    if(groupCheckBox.length > 0){
                        int planID = Context.getService(planordersService.class).getLastPlanID() + 1;
                        
                        List<String> planOrderList = new ArrayList<>();
                        for(int i=0;i<groupCheckBox.length;i++){
                            planOrderList.add(Long.toString(groupCheckBox[i]));  
                        } 
                        
                        List<standardplans> standardPlans = Context.getService(standardplansService.class).getMedicationPlans(Context.getService(newplansService.class).getMedicationPlan(ConceptName(selectedPlan)).getId());
                        for(standardplans standardPlan : standardPlans){
                            
                            if(planOrderList.contains(standardPlan.getDrugId().toString())){
                                DrugOrder drugOrder = null;
                                drugorders drugorder = null;                            

                                int order = createNewDrugOrder(drugOrder, patient, standardPlan.getDrugId().getDisplayString(), standardPlan.getRoute().getDisplayString(), standardPlan.getDose().toString(), standardPlan.getDoseUnits().getDisplayString(), standardPlan.getQuantity().toString(), standardPlan.getQuantityUnits().getDisplayString(), standardPlan.getFrequency().getName(), standardPlan.getDuration(), standardPlan.getDurationUnits().getDisplayString());
                                createDrugOrderExtension(drugorder, order, patientID, standardPlan.getDrugId().getDisplayString(), startDate, "", selectedPlan, priority, "Draft-Plan", 0, 0, patientInstrn, pharmacistInstrn);
                                currentOrders.add(standardPlan.getDrugId().getDisplayString());
                                
                                Context.getService(drugordersService.class).getDrugOrderByOrderID(order).setPriority(ConceptName("High"));
                                Context.getService(drugordersService.class).getDrugOrderByOrderID(order).setStartDate(Calendar.getInstance().getTime());
                                
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
                
                if ("DISCONTINUE ORDER".equals(action)){
                    if(groupCheckBox.length > 0){
                        int id = Integer.parseInt(Long.toString(groupCheckBox[0]));
                        drugorders order = Context.getService(drugordersService.class).getDrugOrderByOrderID(id);
                        
                        order.setGroupId(null);
                        order.setOrderStatus("Non-Active");
                        currentOrders.remove(order.getDrugName().getDisplayString());
                        
                        setDiscontinueReason(order, codedReason, nonCodedReason);
                        if(Context.getService(planordersService.class).getDrugOrderByOrderID(id) != null)
                            Context.getService(planordersService.class).getDrugOrderByOrderID(id).setPlanId(null);
                        
                        Context.getOrderService().voidOrder(Context.getOrderService().getOrder(id), "Discontinued");
                        InfoErrorMessageUtil.flashInfoMessage(session, "Order Discontinued!");
                    }
                }
                
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
                            setDiscontinueReason(order, codedReason, nonCodedReason);
                            Context.getOrderService().voidOrder(Context.getOrderService().getOrder(order.getOrderId()), "Discontinued");
                        }
                        InfoErrorMessageUtil.flashInfoMessage(session, "Orders Discontinued!");
                    }
                }
                
                if ("RENEW ORDER GROUP".equals(action)) {
                    if(groupCheckBox.length > 0){
                        int groupID = Context.getService(drugordersService.class).getLastGroupID() + 1;
                        
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
                
                if ("DISCARD MED PLAN".equals(action)){
                    int ordersInPlan = Context.getService(planordersService.class).getDrugOrdersByPlanID(groupOrderID).size();
                    
                    if(groupCheckBox.length > 0){
                        for(int i=0;i<groupCheckBox.length;i++){
                            int id = Integer.parseInt(Long.toString(groupCheckBox[i]));
                            drugorders order = Context.getService(drugordersService.class).getDrugOrderByOrderID(id);
                        
                            if(ordersInPlan == groupCheckBox.length)
                                order.setOrderStatus("Non-Active-Plan");
                            else {
                                order.setOrderStatus("Non-Active");
                                Context.getService(planordersService.class).getDrugOrderByOrderID(id).setPlanId(null);
                            }                                
                            
                            currentOrders.remove(order.getDrugName().getDisplayString());
                            setDiscontinueReason(order, codedReason, nonCodedReason);
                            Context.getOrderService().voidOrder(Context.getOrderService().getOrder(order.getOrderId()), "Discontinued");
                        }
                        InfoErrorMessageUtil.flashInfoMessage(session, "Orders Discontinued!");
                    }
                }
                
                if ("RENEW MED PLAN".equals(action)){
                    if(groupCheckBox.length > 0){
                        int planID = Context.getService(planordersService.class).getLastPlanID() + 1;
                        
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
                
                if ("EDIT DRUG ORDER".equals(action)) {
                    
                    drugorders originalOrder = Context.getService(drugordersService.class).getDrugOrderByOrderID(orderId);
                    Context.getOrderService().voidOrder(Context.getOrderService().getOrder(orderId), "Discontinued");

                    DrugOrder drugOrder = null;
                    drugorders drugorder = null;

                    int order = createNewDrugOrder(drugOrder, patient, drugName, route, dose, doseUnits, quantity, quantityUnits, frequency, duration, durationUnits);
                    createDrugOrderExtension(drugorder, order, patientID, drugName, startDate, orderReason, diagnosis, priority, "Active", refill, interval, patientInstrn, pharmacistInstrn);
                    currentOrders.remove(originalOrder.getDrugName().getDisplayString());
                    currentOrders.add(drugName);
                    
                    switch (originalOrder.getOrderStatus()) {
                        case "Active":
                            Context.getService(drugordersService.class).getDrugOrderByOrderID(order).setOrderStatus("Active");
                            break;
                        case "Active-Plan":
                            Context.getService(drugordersService.class).getDrugOrderByOrderID(order).setOrderStatus("Active-Plan");
                            Context.getService(planordersService.class).getDrugOrderByOrderID(originalOrder.getOrderId()).setOrderId(order);
                            break;
                        case "Draft-Plan":
                            Context.getService(drugordersService.class).getDrugOrderByOrderID(order).setOrderStatus("Draft-Plan");
                            Context.getService(planordersService.class).getDrugOrderByOrderID(originalOrder.getOrderId()).setOrderId(order);
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

    private int createNewDrugOrder(DrugOrder order, Patient patient, String drugNameConfirmed, String route,
            String dose, String doseUnits, String quantity, String quantityUnits,
            String frequency, Integer duration, String durationUnits) {

        order = new DrugOrder();
        
        if(ConceptName(drugNameConfirmed) == null){
            
            drugordersActivator activator = new drugordersActivator();
            Concept drugConcept =  activator.saveConcept(drugNameConfirmed, Context.getConceptService().getConceptClassByName("Drug"));
            order.setConcept(drugConcept);
            
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
    
    private void createPlanOrder(int drugOrderID, int planID, int patientID, String diseaseName){
        
        planorders diseaseDrugOrder = new planorders();
        diseaseDrugOrder.setPlanId(planID);
        diseaseDrugOrder.setOrderId(drugOrderID);
        diseaseDrugOrder.setPatientId(patientID);
        diseaseDrugOrder.setDiseaseId(ConceptName(diseaseName));
        Context.getService(planordersService.class).saveDrugOrder(diseaseDrugOrder);
        
    }
    
    private Concept ConceptName(String conceptString){
        return Context.getConceptService().getConceptByName(conceptString);
    }
    
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