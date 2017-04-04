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
        
    public standardplans getMedicationPlan(Integer id);
    
    public void deleteMedicationPlan(standardplans plan);
        
    public standardplans saveMedicationPlan(standardplans plan);
    
    public List<standardplans> getMedicationPlans(Integer planId);
        
}