/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.drugorders.api.impl;

import java.util.List;
import org.openmrs.Concept;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.drugorders.planorders;
import org.springframework.transaction.annotation.Transactional;
import org.openmrs.module.drugorders.api.db.planordersDAO;
import org.openmrs.module.drugorders.api.planordersService;

/**
 *
 * @author harini-geek
 */
public class planordersServiceImpl extends BaseOpenmrsService implements planordersService{
    
    private planordersDAO dao;
    protected final Log log = LogFactory.getLog(this.getClass());

    public Log getLog() {
        return log;
    }

    public planordersDAO getDao() {
	    return dao;
    }
    
    public void setDao(planordersDAO dao) {
	    this.dao = dao;
    }
    
    @Transactional(readOnly = true)
    @Override
    public int getLastPlanID(){
        return dao.getLastPlanID();
    }
    
    @Transactional
    @Override
    public planorders saveDrugOrder(planorders order){
        return dao.saveDrugOrder(order);
    }
    
    @Transactional(readOnly = true)
    @Override
    public planorders getDrugOrderByOrderID(Integer id){
        return dao.getDrugOrderByOrderID(id);
    }
    
    @Transactional(readOnly = true)
    @Override
    public List<planorders> getDrugOrdersByPlanID(Integer planId){
        return dao.getDrugOrdersByPlanID(planId);
    }
    
    @Transactional(readOnly = true)
    @Override
    public List<planorders> getDrugOrdersByPlanAndPatient(Concept concept,Patient patient){
        return dao.getDrugOrdersByPlanAndPatient(concept, patient);
    }
    
}
