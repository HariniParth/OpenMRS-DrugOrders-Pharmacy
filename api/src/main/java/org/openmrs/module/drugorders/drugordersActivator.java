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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptName;
import org.openmrs.ConceptSet;
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
        setGlobalProperties(administrationService, "order.drugDispensingUnitsConceptUuid", "162384AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        setGlobalProperties(administrationService, "order.drugDosingUnitsConceptUuid", "162384AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        setGlobalProperties(administrationService, "order.drugRoutesConceptUuid", "162394AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        setGlobalProperties(administrationService, "order.durationUnitsConceptUuid", "1732AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

        ConceptService cs = Context.getConceptService();

        if (cs.getConceptClassByName("Discontinue Order Reasons") == null) {

            ConceptClass conceptClass = saveConceptClass("Discontinue Order Reasons");
            Concept setConcept = saveConcept("Discontinue Order Reasons", Context.getConceptService().getConceptClassByName("Finding"));
            String orderDiscontinueReasons[] = {"Allergic", "Alternative", "Ineffective", "Not for Sale", "Recuperated", "Unavailable", "Wrong Diagnosis"};

            for (String reasons : orderDiscontinueReasons) {
                Concept concept = saveConcept(reasons, conceptClass);
                setConcept.addSetMember(concept);
            }
            Concept otherConcept = cs.getConceptByName("Other");
            otherConcept.setConceptClass(conceptClass);
            cs.saveConcept(otherConcept);
            setConcept.addSetMember(otherConcept);
        }

        if (cs.getConceptClassByName("Order Priority") == null) {

            ConceptClass conceptClass = saveConceptClass("Order Priority");
            Concept setConcept = saveConcept("Order Priority", cs.getConceptClassByName("Units of Measure"));
            String orderPriority[] = {"Normal", "High", "Exception"};

            for (String orderPrio : orderPriority) {
                Concept concept = saveConcept(orderPrio, conceptClass);
                setConcept.addSetMember(concept);
            }
        }

        if (cs.getConceptClassByName("Units of Duration") == null) {

            ConceptClass conceptClass = saveConceptClass("Units of Duration");
            Concept setConcept = saveConcept("Units of Duration", cs.getConceptClassByName("Units of Measure"));

            List<Concept> durations = new ArrayList<>();
            Concept durationConcept = cs.getConceptByName("Duration units");

            for (ConceptSet durationConcepts : durationConcept.getConceptSets()) {
                Concept durationMember = durationConcepts.getConcept();
                durations.add(durationMember);
            }

            for (Concept duration : durations) {
                duration.setConceptClass(conceptClass);
                cs.saveConcept(duration);
                setConcept.addSetMember(duration);
            }
        }

        if (cs.getConceptClassByName("Routes of drug administration") == null) {

            ConceptClass conceptClass = saveConceptClass("Routes of drug administration");
            Concept setConcept = saveConcept("Routes of drug administration", cs.getConceptClassByName("Procedure"));

            List<Concept> routes = new ArrayList<>();
            Concept routeConcept = cs.getConceptByName("Routes of administration");

            for (ConceptSet routeConcepts : routeConcept.getConceptSets()) {
                Concept routeMember = routeConcepts.getConcept();
                routes.add(routeMember);
            }

            for (Concept route : routes) {
                route.setConceptClass(conceptClass);
                cs.saveConcept(route);
                setConcept.addSetMember(route);
            }
        }

        if (cs.getConceptClassByName("Units of Dose") == null) {

            ConceptClass conceptClass = saveConceptClass("Units of Dose");
            Concept setConcept = saveConcept("Units of Dose", cs.getConceptClassByName("Units of Measure"));
            String doseUnits[] = {"Fluid ounce", "Gram", "Liter", "Milliliter", "Milligram", "Microgram"};

            List<Concept> doses = new ArrayList<>();
            for (String doseUnit : doseUnits) {
                doses.add(cs.getConceptByName(doseUnit));
            }

            for (Concept dose : doses) {
                dose.setConceptClass(conceptClass);
                cs.saveConcept(dose);
                setConcept.addSetMember(dose);
            }
        }

        if (cs.getConceptClassByName("Units of Quantity") == null) {

            ConceptClass conceptClass = saveConceptClass("Units of Quantity");
            Concept setConcept = saveConcept("Units of Quantity", cs.getConceptClassByName("Units of Measure"));
            String quantityUnits[] = {"Capsule", "Drop", "Syringe", "Tablet", "Tablespoon", "Teaspoon", "Tube", "Vial"};

            List<Concept> quantities = new ArrayList<>();
            for (String quantityUnit : quantityUnits) {
                quantities.add(cs.getConceptByName(quantityUnit));
            }

            for (Concept quantity : quantities) {
                quantity.setConceptClass(conceptClass);
                cs.saveConcept(quantity);
                setConcept.addSetMember(quantity);
            }
        }
        
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

    public ConceptClass saveConceptClass(String conceptName) {

        ConceptClass conceptClass = new ConceptClass();
        conceptClass.setName(conceptName);
        conceptClass.setDescription(conceptName);
        Context.getConceptService().saveConceptClass(conceptClass);
        return conceptClass;
    }

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
