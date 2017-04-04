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
    
    @Override
    public void deleteMedicationPlan(standardplans plan){
        dao.deleteMedicationPlan(plan);
    }
    
    @Transactional(readOnly = true)
    @Override
    public standardplans getMedicationPlan(Integer planId){
        return dao.getMedicationPlan(planId);
    }
    
    @Transactional
    @Override
    public standardplans saveMedicationPlan(standardplans plan) {
        return dao.saveMedicationPlan(plan);
    }
    
    @Transactional(readOnly = true)
    @Override
    public List<standardplans> getMedicationPlans(Integer planId){
        return dao.getMedicationPlans(planId);
    }
    
}
