/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.drugorders;

import java.util.List;
import java.util.Locale;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptName;
import org.openmrs.GlobalProperty;
import org.openmrs.OrderFrequency;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleActivator;

/**
 * This class contains the logic that is run every time this module is either
 * started or stopped.
 */
public class drugordersActivator implements ModuleActivator {

    protected Log log = LogFactory.getLog(getClass());

    /**
     * @see ModuleActivator${symbol_pound}willRefreshContext()
     */
    @Override
    public void willRefreshContext() {
        log.info("Refreshing drugorders Module");
    }

    /**
     * @see ModuleActivator${symbol_pound}contextRefreshed()
     */
    @Override
    public void contextRefreshed() {
        log.info("drugorders Module refreshed");
    }

    /**
     * @see ModuleActivator${symbol_pound}willStart()
     */
    @Override
    public void willStart() {
        log.info("Starting drugorders Module");
    }

    /**
     * @see ModuleActivator${symbol_pound}started()
     */
    @Override
    public void started() {
        log.info("drugorders Module started");
        AdministrationService administrationService = Context.getAdministrationService();
        /*
          Set Global properties for Dispensing units, Dosing units, Route and Duration units.
        */
        setGlobalProperties(administrationService, "order.drugDispensingUnitsConceptUuid", "162384AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        setGlobalProperties(administrationService, "order.drugDosingUnitsConceptUuid", "162384AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        setGlobalProperties(administrationService, "order.drugRoutesConceptUuid", "162394AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        setGlobalProperties(administrationService, "order.durationUnitsConceptUuid", "1732AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

        ConceptService cs = Context.getConceptService();        

        /*
          ===================================================================================
          Create a concept class for 'Discontinue Order Reasons' if it does not exist.
          ===================================================================================
        */
        if (cs.getConceptClassByName("Discontinue Order Reasons") == null) {

            saveConceptClass("Discontinue Order Reasons");
            saveConcept("Discontinue Order Reasons", Context.getConceptService().getConceptClassByName("Finding"));
        }

        String orderDiscontinueReasons[] = {"Allergic", "Alternative", "Ineffective", "Not for Sale", "Recuperated", "Unavailable", "Wrong Diagnosis"};
        
        ConceptClass conceptClass = cs.getConceptClassByName("Discontinue Order Reasons");
        Concept setConcept = cs.getConceptByName("Discontinue Order Reasons");
        
        for (String reason : orderDiscontinueReasons) {
            // Create a concept for a set of options and add these concepts to the concept class 'Discontinue Order Reasons'.
            if(cs.getConceptByName(reason) == null){
                saveConcept(reason, conceptClass);
            }
            // Set the concept class if it is not set.
            if(cs.getConceptByName(reason).getConceptClass() != conceptClass)
                cs.getConceptByName(reason).setConceptClass(conceptClass);
            
            // Add the given string's concept as a member of the 'Discontinue Order Reasons' concept set member.
            if(!setConcept.getSetMembers().contains(cs.getConceptByName(reason)))
                setConcept.addSetMember(cs.getConceptByName(reason));
        }
        
        // Create a concept for the 'Other' option.
        Concept otherConcept = cs.getConceptByName("Other");
        if(!otherConcept.getConceptClass().equals(cs.getConceptClassByName("Discontinue Order Reasons"))){
            otherConcept.setConceptClass(conceptClass);
            cs.saveConcept(otherConcept);
        }
        if(!setConcept.getSetMembers().contains(otherConcept))
            setConcept.addSetMember(otherConcept);
            
        /*
          ===================================================================================
          Create a concept class for 'Order Priority' if it does not exist.
          ===================================================================================
        */
        if (cs.getConceptClassByName("Order Priority") == null) {

            saveConceptClass("Order Priority");
            saveConcept("Order Priority", cs.getConceptClassByName("Units of Measure"));
        }
        
        String orderPriority[] = {"Normal", "High", "Exception"};
        conceptClass = cs.getConceptClassByName("Order Priority");
        setConcept = cs.getConceptByName("Order Priority");

        for (String orderPrio : orderPriority) {
            // Create a concept for a set of options and add these concepts to the concept class 'Order Priority'.
            if(cs.getConceptByName(orderPrio) == null){
                saveConcept(orderPrio, conceptClass);
            }
            // Set the concept class if it is not set.
            if(cs.getConceptByName(orderPrio).getConceptClass() != conceptClass)
                cs.getConceptByName(orderPrio).setConceptClass(conceptClass);
            
            // Add the given string's concept as a member of the 'Order Priority' concept set member.
            if(!setConcept.getSetMembers().contains(cs.getConceptByName(orderPrio)))
                setConcept.addSetMember(cs.getConceptByName(orderPrio));
        }

        /*
          ===================================================================================
          Create a concept class for 'Units of Dose' if it does not exist.
          ===================================================================================
        */
        if (cs.getConceptClassByName("Units of Dose") == null) {

            saveConceptClass("Units of Dose");
            saveConcept("Units of Dose", cs.getConceptClassByName("Units of Measure"));
        }
        
        String doseUnits[] = {"Fluid ounce", "Gram", "Liter", "Milliliter", "Milligram", "Microgram"};
        conceptClass = cs.getConceptClassByName("Units of Dose");
        setConcept = cs.getConceptByName("Units of Dose");
        
        for (String unit : doseUnits) {
            // Create a concept for a set of options and add these concepts to the concept class 'Units of Dose'.
            if(cs.getConceptByName(unit) == null){
                saveConcept(unit, conceptClass);
            }
            // Set the concept class if it is not set.
            if(cs.getConceptByName(unit).getConceptClass() != conceptClass)
                cs.getConceptByName(unit).setConceptClass(conceptClass);
            
            // Add the given string's concept as a member of the 'Units of Dose' concept set member.
            if(!setConcept.getSetMembers().contains(cs.getConceptByName(unit)))
                setConcept.addSetMember(cs.getConceptByName(unit));
        }
        
        /*
          ===================================================================================
          Create a concept class for 'Units of Quantity' if it does not exist.
          ===================================================================================
        */
        if (cs.getConceptClassByName("Units of Quantity") == null) {

            saveConceptClass("Units of Quantity");
            saveConcept("Units of Quantity", cs.getConceptClassByName("Units of Measure"));
        }
        
        String quantityUnits[] = {"Capsule", "Drop", "Syringe", "Tablet", "Tablespoon", "Teaspoon", "Tube", "Vial"};
        conceptClass = cs.getConceptClassByName("Units of Quantity");
        setConcept = cs.getConceptByName("Units of Quantity");

        for (String unit : quantityUnits) {
            // Create a concept for a set of options and add these concepts to the concept class 'Units of Quantity'.
            if(cs.getConceptByName(unit) == null){
                saveConcept(unit, conceptClass);
            }
            // Set the concept class if it is not set.
            if(cs.getConceptByName(unit).getConceptClass() != conceptClass)
                cs.getConceptByName(unit).setConceptClass(conceptClass);
            
            // Add the given string's concept as a member of the 'Units of Quantity' concept set member.
            if(!setConcept.getSetMembers().contains(cs.getConceptByName(unit)))
                setConcept.addSetMember(cs.getConceptByName(unit));
        }
        
        /*
          ===================================================================================
          Create a concept class for 'Routes of drug administration' if it does not exist.
          ===================================================================================
        */
        if (cs.getConceptClassByName("Routes of drug administration") == null) {

            saveConceptClass("Routes of drug administration");
            saveConcept("Routes of drug administration", cs.getConceptClassByName("Procedure"));
        }
        
        String routes[] = {"In both ears", "In left ear", "In right ear", "In both eyes", "In left eye", "In right eye", "Inhalation", "Intramuscular", "Intranasal", "Intraosseous", "Intravenous", "Oral", "Per NG tube", "Rectally", "Subcutaneous", "Transdermal", "Vaginally"};
        conceptClass = cs.getConceptClassByName("Routes of drug administration");
        setConcept = cs.getConceptByName("Routes of drug administration");

        for (String route : routes) {
            // Create a concept for a set of options and add these concepts to the concept class 'Routes of drug administration'.
            if(cs.getConceptByName(route) == null){
                saveConcept(route, conceptClass);
            }
            // Set the concept class if it is not set.
            if(cs.getConceptByName(route).getConceptClass() != conceptClass)
                cs.getConceptByName(route).setConceptClass(conceptClass);
            
            // Add the given string's concept as a member of the 'Routes of drug administration' concept set member.
            if(!setConcept.getSetMembers().contains(cs.getConceptByName(route)))
                setConcept.addSetMember(cs.getConceptByName(route));
        }
        
        /*
          ===================================================================================
          Create a concept class for 'Units of Duration' if it does not exist.
          ===================================================================================
        */
        if (cs.getConceptClassByName("Units of Duration") == null) {

            saveConceptClass("Units of Duration");
            saveConcept("Units of Duration", cs.getConceptClassByName("Units of Measure"));
        }
        
        String durationUnits[] = {"Days", "Hours", "Minutes", "Months", "Number of occurrences", "Weeks", "Years"};
        conceptClass = cs.getConceptClassByName("Units of Duration");
        // Find the concept named 'Units of Duration' belonging to the class 'Units of Duration'.
        List<Concept> setConcepts = cs.getConceptsByClass(conceptClass);
        for(Concept con : setConcepts){
            if(con.isNamed("Units of Duration"))
                setConcept = con;
        }

        for (String unit : durationUnits) {
            // Create a concept for a set of options and add these concepts to the concept class 'Units of Duration'.
            if(cs.getConceptByName(unit) == null){
                saveConcept(unit, conceptClass);
            }
            // Set the concept class if it is not set.
            if(cs.getConceptByName(unit).getConceptClass() != conceptClass)
                cs.getConceptByName(unit).setConceptClass(conceptClass);
            
            // Add the given string's concept as a member of the 'Units of Duration' concept set member.
            if(!setConcept.getSetMembers().contains(cs.getConceptByName(unit)))
                setConcept.addSetMember(cs.getConceptByName(unit));
        }

        /*
          ===================================================================================
          Add frequency options to the class OrderFrequency.
          ===================================================================================
        */
        if(Context.getOrderService().getOrderFrequencies(true) != null){
            
            String frequencyConcepts[] = {"Once daily","Twice daily","Thrice daily","Four times daily","Weekly","Monthly"};
        
            for(String freqConcept : frequencyConcepts){
                if(Context.getOrderService().getOrderFrequencyByConcept(cs.getConceptByName(freqConcept)) == null){
                    OrderFrequency orderFrequency = new OrderFrequency();
                    orderFrequency.setFrequencyPerDay(0.0);
                    orderFrequency.setConcept(cs.getConceptByName(freqConcept));
                    Context.getOrderService().saveOrderFrequency(orderFrequency);
                }

            }
        }
    }

    /*
      Create and save a Concept Class
    */
    public ConceptClass saveConceptClass(String conceptName) {

        ConceptClass conceptClass = new ConceptClass();
        conceptClass.setName(conceptName);
        conceptClass.setDescription(conceptName);
        Context.getConceptService().saveConceptClass(conceptClass);
        return conceptClass;
    }

    /*
      Create and save a Concept
    */
    public Concept saveConcept(String newConcept, ConceptClass conceptClass) {

        Concept concept = new Concept();
        concept.setConceptClass(conceptClass);
        ConceptName conceptName = new ConceptName(newConcept, Locale.US);
        concept.setFullySpecifiedName(conceptName);
        ConceptDatatype conceptDatatype = Context.getConceptService().getConceptDatatypeByName("Text");
        concept.setDatatype(conceptDatatype);
        Context.getConceptService().saveConcept(concept);
        return concept;
    }

    /**
     * @see ModuleActivator${symbol_pound}willStop()
     */
    @Override
    public void willStop() {
        log.info("Stopping drugorders Module");
    }

    /**
     * @see ModuleActivator${symbol_pound}stopped()
     */
    @Override
    public void stopped() {
        log.info("drugorders Module stopped");
    }

    public void setGlobalProperties(AdministrationService administrationService, String propertyName, String propertyValue) {

        GlobalProperty glbProp = administrationService.getGlobalPropertyObject(propertyName);
        glbProp.setPropertyValue(propertyValue);
        administrationService.saveGlobalProperty(glbProp);
    }

}
