/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.drugorders.api.db;

import java.util.List;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.module.drugorders.planorders;

/**
 *
 * @author harini-geek
 */
public interface planordersDAO {
    
    // Get last assigned Plan ID
    public int getLastPlanID();
    
    // Save plan order record
    public planorders savePlanOrder(planorders order);
    
    // Get the planorders record using the drug order ID
    public planorders getPlanOrderByOrderID(Integer orderId);
        
    // Get the list of planorders records having the same plan ID
    public List<planorders> getPlanOrdersByPlanID(Integer planId);
    
    // Get the list of planorders records by plan name and Patient
    public List<planorders> getPlanOrdersByPlanAndPatient(Concept concept, Patient patient);
      
}
