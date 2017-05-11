/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.drugorders.api;

import java.util.List;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.drugorders.standardplans;

/**
 *
 * @author harini-geek
 */
public interface standardplansService extends OpenmrsService{
        
    // Get standard medication plan by ID
    public standardplans getMedPlanByID(Integer id);
    
    // Discard standard medication plan
    public void discardMedPlan(standardplans plan);
        
    // Save standard medication plan
    public standardplans saveMedPlan(standardplans plan);
    
    // Get standard medication plans by plan ID
    public List<standardplans> getMedPlansByPlanID(Integer planId);
        
}