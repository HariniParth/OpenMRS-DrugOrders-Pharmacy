/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.drugorders.api;

import java.util.List;
import org.openmrs.Concept;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.drugorders.newplans;

/**
 *
 * @author harini-geek
 */
public interface newplansService extends OpenmrsService{
    
    // Get the list of active medication plans
    public List<newplans> getAllMedPlans();
    
    // Get a medication plan by its ID
    public newplans getMedPlanByPlanID(Integer id);
    
    // Discard a medication plan
    public void discardMedPlan(newplans plan);
        
    // Save a new medication plan
    public newplans saveMedPlan(newplans plan);
    
    // Get a medication plan by its name
    public newplans getMedPlanByPlanName(Concept planName);
    
}
