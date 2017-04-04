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
            @RequestParam(value = "drugNameEntered", required = false) String drugNameSelected,
            @RequestParam(value = "startDateEntered", required = false) Date startDateEntered,
            @RequestParam(value = "drugRoute", required = false) String drugRoute,
            @RequestParam(value = "drugDose", required = false) String drugDose, 
            @RequestParam(value = "drugDoseUnits", required = false) String drugDoseUnits,
            @RequestParam(value = "drugQuantity", required = false) String drugQuantity, 
            @RequestParam(value = "quantityUnits", required = false) String quantityUnits,
            @RequestParam(value = "drugDuration", required = false) Integer drugDuration, 
            @RequestParam(value = "durationUnits", required = false) String durationUnits,
            @RequestParam(value = "drugFrequency", required = false) String drugFrequency,
            @RequestParam(value = "orderPriority", required = false) String orderPriority,
            @RequestParam(value = "refill", required = false) Integer refill,
            @RequestParam(value = "refillInterval", required = false) Integer refillInterval,
            @RequestParam(value = "associatedDiagnosis", required = false) String selectedDiagnosis,
            @RequestParam(value = "allergicOrderReason", required = false) String allergicOrderReason,
            @RequestParam(value = "patientInstructions", required = false) String patientInstructions, 
            @RequestParam(value = "pharmacistInstructions", required = false) String pharmacistInstructions,
            @RequestParam(value = "codedReason", required = false) String codedReason,
            @RequestParam(value = "nonCodedReason", required = false) String nonCodedReason,
            @SpringBean("allergyService") PatientService patientService, HttpSession session,
            @RequestParam(value = "action", required = false) String action,
            @RequestParam(value = "order_id", required = false) Integer orderId,
            @RequestParam(value = "orderClass", required = false) String orderClass,
            @RequestParam(value = "groupOrderID", required = false) Integer groupOrderID,
            @RequestParam(value = "groupCheckBox", required=false) long[] groupCheckBox,
            @RequestParam(value = "selectedPlan", required = false) String selectedPlan,
            @RequestParam(value = "allergicPlanItemOrderReason", required = false) String[] allergicPlanItemOrderReason) {

        int patientID = patient.getPatientId();
        String drugNameEntered = drugNameSelected.trim();
        String associatedDiagnosis = selectedDiagnosis.trim();
        
        Allergies allergies = patientService.getAllergies(patient);
        model.addAttribute("allergies", allergies);
        
        List<String> allergicDrugList = new ArrayList<>();
        for(Allergy allergy : allergies){
            allergicDrugList.add(allergy.getAllergen().toString());
        }
                
        if (StringUtils.isNotBlank(action)) {
            try {
                if ("CREATE DRUG ORDER".equals(action)) {
                    if (!(drugNameEntered.equals("")) && !(drugRoute.equals("")) && !(drugDose.equals("")) && !(drugDoseUnits.equals("")) && !(drugQuantity.equals("")) && !(quantityUnits.equals("")) && !(drugFrequency.equals("")) && (drugDuration != null) && !(durationUnits.equals(""))) {
                        
                        drugorders o = Context.getService(drugordersService.class).getDrugOrderByDrugAndPatient(ConceptName(drugNameEntered), patient);
                        if(o == null || !o.getOrderStatus().equals("Active")){
                            
                            DrugOrder drugOrder = null;
                            drugorders drugorder = null;
                            int order = createNewDrugOrder(drugOrder, patient, drugNameEntered, drugRoute, drugDose, drugDoseUnits, drugQuantity, quantityUnits, drugFrequency, drugDuration, durationUnits);
                            createDrugOrderExtension(drugorder, order, patientID, drugNameEntered, startDateEntered, allergicOrderReason, associatedDiagnosis, orderPriority, refill, refillInterval, patientInstructions, pharmacistInstructions);
                            
                            if(orderId != null){
                                Context.getService(drugordersService.class).getDrugOrderByOrderID(order).setGroupId(orderId);
                                Context.getService(drugordersService.class).getDrugOrderByOrderID(order).setOrderStatus("Active-Group");
                            }
                            InfoErrorMessageUtil.flashInfoMessage(session, "Order Created!");
                        } 
                        else {
                            InfoErrorMessageUtil.flashInfoMessage(session, "Order Exists!");
                        }
                    }
                }
                
                if ("selectMedPlan".equals(action)) {
                    
                    List<String> allergicPlanOrderReason = new ArrayList<>();
                    for(String orderReason : allergicPlanItemOrderReason){
                        if(!orderReason.equals(""))
                            allergicPlanOrderReason.add(orderReason);                
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
                                String orderReason = "";
                                if(allergicDrugList.size() > 0){
                                    if(allergicDrugList.contains(standardPlan.getDrugId().getDisplayString())){
                                        orderReason = allergicPlanOrderReason.get(0);
                                        allergicPlanOrderReason.remove(0);
                                    }
                                }
                                
                                DrugOrder drugOrder = null;
                                drugorders drugorder = null;                            

                                int order = createNewDrugOrder(drugOrder, patient, standardPlan.getDrugId().getDisplayString(), standardPlan.getRoute().getDisplayString(), standardPlan.getDose().toString(), standardPlan.getDoseUnits().getDisplayString(), standardPlan.getQuantity().toString(), standardPlan.getQuantityUnits().getDisplayString(), standardPlan.getFrequency().getName(), standardPlan.getDuration(), standardPlan.getDurationUnits().getDisplayString());
                                createDrugOrderExtension(drugorder, order, patientID, standardPlan.getDrugId().getDisplayString(), startDateEntered, orderReason, selectedPlan, orderPriority, 0, 0, patientInstructions, pharmacistInstructions);

                                Context.getService(drugordersService.class).getDrugOrderByOrderID(order).setOrderStatus("Active-Plan");
                                Context.getService(drugordersService.class).getDrugOrderByOrderID(order).setPriority(ConceptName("High"));
                                Context.getService(drugordersService.class).getDrugOrderByOrderID(order).setStartDate(Calendar.getInstance().getTime());
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
                        
                        setDiscontinueReason(order, codedReason, nonCodedReason);
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
                            
                            setDiscontinueReason(order, codedReason, nonCodedReason);
                            Context.getOrderService().voidOrder(Context.getOrderService().getOrder(order.getOrderId()), "Discontinued");
                        }
                        InfoErrorMessageUtil.flashInfoMessage(session, "Orders Discontinued!");
                    }
                }
                
                if ("RENEW ORDER GROUP".equals(action)) {
                    if(groupCheckBox.length > 0){
                        int groupID = Context.getService(drugordersService.class).getLastGroupID() + 1;
                        
                        for(int i=0;i<groupCheckBox.length;i++){
                            int id = Integer.parseInt(Long.toString(groupCheckBox[i]));
                            drugorders order = Context.getService(drugordersService.class).getDrugOrderByOrderID(id);
                        
                            DrugOrder drugOrderMain = (DrugOrder) Context.getOrderService().getOrder(order.getOrderId());
                            DrugOrder drugOrder = null;
                            drugorders drugorder = null;

                            int orderID = createNewDrugOrder(drugOrder, patient, order.getDrugName().getDisplayString(), drugOrderMain.getRoute().getDisplayString(), drugOrderMain.getDose().toString(), drugOrderMain.getDoseUnits().getDisplayString(), drugOrderMain.getQuantity().toString(), drugOrderMain.getQuantityUnits().getDisplayString(), drugOrderMain.getFrequency().getName(), drugOrderMain.getDuration(), drugOrderMain.getDurationUnits().getDisplayString());
                            createDrugOrderExtension(drugorder, orderID, patientID, order.getDrugName().getDisplayString(), Calendar.getInstance().getTime(), "", order.getAssociatedDiagnosis().getDisplayString(), order.getPriority().getDisplayString(), order.getRefill(), order.getRefillInterval(), "", "");

                            Context.getService(drugordersService.class).getDrugOrderByOrderID(orderID).setGroupId(groupID);
                            Context.getService(drugordersService.class).getDrugOrderByOrderID(orderID).setOrderStatus("Active-Group");
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
                            
                            setDiscontinueReason(order, codedReason, nonCodedReason);
                            Context.getOrderService().voidOrder(Context.getOrderService().getOrder(order.getOrderId()), "Discontinued");
                        }
                        InfoErrorMessageUtil.flashInfoMessage(session, "Orders Discontinued!");
                    }
                }
                
                if ("RENEW MED PLAN".equals(action)){
                    if(groupCheckBox.length > 0){
                        int planID = Context.getService(planordersService.class).getLastPlanID() + 1;
                        
                        for(int i=0;i<groupCheckBox.length;i++){
                            int id = Integer.parseInt(Long.toString(groupCheckBox[i]));
                            DrugOrder orderMain = (DrugOrder) Context.getOrderService().getOrder(id);
                            drugorders orderExtn = Context.getService(drugordersService.class).getDrugOrderByOrderID(id);

                            DrugOrder drugOrder = null;
                            drugorders drugorder = null;

                            int order = createNewDrugOrder(drugOrder, patient, orderExtn.getDrugName().getDisplayString(), orderMain.getRoute().getDisplayString(), orderMain.getDose().toString(), orderMain.getDoseUnits().getDisplayString(), orderMain.getQuantity().toString(), orderMain.getQuantityUnits().getDisplayString(), orderMain.getFrequency().getName(), orderMain.getDuration(), orderMain.getDurationUnits().getDisplayString());
                            createDrugOrderExtension(drugorder, order, patientID, orderExtn.getDrugName().getDisplayString(), Calendar.getInstance().getTime(), "", orderExtn.getAssociatedDiagnosis().getDisplayString(), orderExtn.getPriority().getDisplayString(), orderExtn.getRefill(), orderExtn.getRefillInterval(), "", "");
                            createPlanOrder(order, planID, patientID, orderExtn.getAssociatedDiagnosis().getDisplayString());

                            Context.getService(drugordersService.class).getDrugOrderByOrderID(order).setOrderStatus("Active-Plan");
                        }
                        InfoErrorMessageUtil.flashInfoMessage(session, "Plan Renewed!");
                    }                        
                }
                
                if ("EDIT DRUG ORDER".equals(action)) {
                    
                    drugorders originalOrderExtension = Context.getService(drugordersService.class).getDrugOrderByOrderID(orderId);
                    Context.getOrderService().voidOrder(Context.getOrderService().getOrder(orderId), "Discontinued");

                    DrugOrder drugOrder = null;
                    drugorders drugorder = null;

                    int order = createNewDrugOrder(drugOrder, patient, drugNameEntered, drugRoute, drugDose, drugDoseUnits, drugQuantity, quantityUnits, drugFrequency, drugDuration, durationUnits);
                    createDrugOrderExtension(drugorder, order, patientID, drugNameEntered, startDateEntered, allergicOrderReason, associatedDiagnosis, orderPriority, refill, refillInterval, patientInstructions, pharmacistInstructions);
                    originalOrderExtension.setOrderStatus("Non-Active");
                    
                    switch (orderClass) {
                        case "PLAN":
                            Context.getService(drugordersService.class).getDrugOrderByOrderID(order).setOrderStatus("Active-Plan");
                            Context.getService(planordersService.class).getDrugOrderByOrderID(originalOrderExtension.getOrderId()).setOrderId(order);
                            break;
                        case "SINGLE":
                            Context.getService(drugordersService.class).getDrugOrderByOrderID(order).setOrderStatus("Active");
                            break;
                        case "GROUP":
                            Context.getService(drugordersService.class).getDrugOrderByOrderID(order).setOrderStatus("Active-Group");
                            Context.getService(drugordersService.class).getDrugOrderByOrderID(order).setGroupId(originalOrderExtension.getGroupId());
                            originalOrderExtension.setGroupId(null);
                            break;
                    }
                    
                    InfoErrorMessageUtil.flashInfoMessage(session, "Order Changes Saved!");
                }

                if ("RENEW DRUG ORDER".equals(action)) {
                    
                    drugorders originalOrderExtension = Context.getService(drugordersService.class).getDrugOrderByOrderID(orderId);
                    String drugName = originalOrderExtension.getDrugName().getDisplayString();

                    DrugOrder drugOrder = null;
                    drugorders drugorder = null;
                    int order = createNewDrugOrder(drugOrder, patient, drugName, drugRoute, drugDose, drugDoseUnits, drugQuantity, quantityUnits, drugFrequency, drugDuration, durationUnits);
                    createDrugOrderExtension(drugorder, order, patientID, drugName, startDateEntered, allergicOrderReason, associatedDiagnosis, orderPriority, refill, refillInterval, patientInstructions, pharmacistInstructions);
                    InfoErrorMessageUtil.flashInfoMessage(session, "Order Renewed!");
                }   
                
            } catch (APIException | NumberFormatException e) {
                System.out.println(e.toString());
            }
        }
    }

    private int createNewDrugOrder(DrugOrder order, Patient patient, String drugNameConfirmed, String drugRoute,
            String drugDose, String drugDoseUnits, String drugQuantity, String quantityUnits,
            String drugFrequency, Integer drugDuration, String durationUnits) {

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

        order.setRoute(ConceptName(drugRoute));
        order.setDose(Double.valueOf(drugDose));
        order.setDoseUnits(ConceptName(drugDoseUnits));
        order.setQuantity(Double.valueOf(drugQuantity));
        order.setQuantityUnits(ConceptName(quantityUnits));
        order.setDuration(drugDuration);
        order.setDurationUnits(ConceptName(durationUnits));
        OrderFrequency orderFrequency = Context.getOrderService().getOrderFrequencyByConcept(ConceptName(drugFrequency));
        order.setFrequency(orderFrequency);
        order.setNumRefills(0);
        order = (DrugOrder) Context.getOrderService().saveOrder(order, null);
        int orderID = order.getOrderId();
        return orderID; 
    }
    
    private void createDrugOrderExtension(drugorders drugorder, int drugOrderID, int patientID, String drugName, Date startDate, String allergicOrderReason, String diagnosis, String orderPriority, int refill, int refillInterval, String patientInstructions, String pharmacistInstructions){
        drugorder = new drugorders();
        drugorder.setOrderId(drugOrderID);
        drugorder.setDrugName(ConceptName(drugName));
        drugorder.setStartDate(startDate);
        drugorder.setPatientId(patientID);
        drugorder.setRefill(refill);
        drugorder.setRefillInterval(refillInterval);
        drugorder.setOrderStatus("Active");
        drugorder.setPriority(ConceptName(orderPriority));
        drugorder.setOnHold(0);
        drugorder.setForDiscard(0);
        
        if(ConceptName(diagnosis) == null){
            drugordersActivator activator = new drugordersActivator();
            Concept diagnosisConcept =  activator.saveConcept(diagnosis, Context.getConceptService().getConceptClassByName("Diagnosis"));
            drugorder.setAssociatedDiagnosis(diagnosisConcept);
        } else {
            drugorder.setAssociatedDiagnosis(ConceptName(diagnosis));
        }
            
        if(!(allergicOrderReason).equals(""))
            drugorder.setIsAllergicOrderReasons(allergicOrderReason);
        if(!(patientInstructions).equals(""))
            drugorder.setPatientInstructions(patientInstructions);
        if(!(pharmacistInstructions).equals(""))
            drugorder.setPharmacistInstructions(pharmacistInstructions);
        
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

}