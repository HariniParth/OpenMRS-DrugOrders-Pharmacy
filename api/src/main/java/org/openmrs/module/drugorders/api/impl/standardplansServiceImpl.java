/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.drugorders.api.impl;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.drugorders.standardplans;
import org.openmrs.module.drugorders.api.db.standardplansDAO;
import org.openmrs.module.drugorders.api.standardplansService;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author harini-geek
 */
public class standardplansServiceImpl extends BaseOpenmrsService implements standardplansService{
    
    private standardplansDAO dao;
    protected final Log log = LogFactory.getLog(this.getClass());

    public Log getLog() {
        return log;
    }

    public standardplansDAO getDao() {
	    return dao;
    }
    
    public void setDao(standardplansDAO dao) {
	    this.dao = dao;
    }
    
    /**
     *
     * @param plan
     */
    
    // Discard standard medication plan
    @Override
    public void discardMedPlan(standardplans plan){
        dao.discardMedPlan(plan);
    }
    
    // Get standard medication plan by ID
    @Transactional(readOnly = true)
    @Override
    public standardplans getMedPlanByID(Integer planId){
        return dao.getMedPlanByID(planId);
    }
    
    // Save standard medication plan
    @Transactional
    @Override
    public standardplans saveMedPlan(standardplans plan) {
        return dao.saveMedPlan(plan);
    }
    
    // Get standard medication plans by plan ID
    @Transactional(readOnly = true)
    @Override
    public List<standardplans> getMedPlansByPlanID(Integer planId){
        return dao.getMedPlansByPlanID(planId);
    }
    
}
