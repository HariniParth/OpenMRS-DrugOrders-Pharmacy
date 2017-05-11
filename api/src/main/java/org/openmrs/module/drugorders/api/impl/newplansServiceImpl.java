/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.drugorders.api.impl;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.drugorders.api.db.newplansDAO;
import org.openmrs.module.drugorders.api.newplansService;
import org.openmrs.module.drugorders.newplans;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author harini-geek
 */
public class newplansServiceImpl extends BaseOpenmrsService implements newplansService {
    
    private newplansDAO dao;
    protected final Log log = LogFactory.getLog(this.getClass());

    /**
     * @return 
    */
    
    public Log getLog() {
        return log;
    }
        
    public newplansDAO getDao() {
	    return dao;
    }
    
    public void setDao(newplansDAO dao) {
	    this.dao = dao;
    }
    
    // Get a medication plan by its ID
    @Transactional(readOnly = true)
    @Override
    public newplans getMedPlanByPlanID(Integer id){
        return dao.getMedPlanByPlanID(id);
    }
    
    // Get the list of active medication plans
    @Transactional(readOnly = true)
    @Override
    public List<newplans> getAllMedPlans(){
        return dao.getAllMedPlans();
    }
    
    // Discard a medication plan
    @Override
    public void discardMedPlan(newplans plan){
        dao.discardMedPlan(plan);
    }
    
    // Save a new medication plan
    @Transactional
    @Override
    public newplans saveMedPlan(newplans plan){
        return dao.saveMedPlan(plan);
    }
    
    // Get a medication plan by its name
    @Transactional(readOnly = true)
    @Override
    public newplans getMedPlanByPlanName(Concept planName){
        return dao.getMedPlanByPlanName(planName);
    }
    
}
