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
    
    public int getLastPlanID();
    public planorders saveDrugOrder(planorders order);
    public planorders getDrugOrderByOrderID(Integer orderId);    
    public List<planorders> getDrugOrdersByPlanID(Integer planId);
    public List<planorders> getDrugOrdersByPlanAndPatient(Concept concept,Patient patient);
    
}
