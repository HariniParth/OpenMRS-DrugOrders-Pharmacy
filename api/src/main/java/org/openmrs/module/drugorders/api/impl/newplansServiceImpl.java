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
    
    @Transactional(readOnly = true)
    @Override
    public newplans getMedicationPlan(Integer id){
        return dao.getMedicationPlan(id);
    }
    
    @Transactional(readOnly = true)
    @Override
    public List<newplans> getAllMedicationPlans(){
        return dao.getAllMedicationPlans();
    }
    
    @Override
    public void deleteMedicationPlan(newplans plan){
        dao.deleteMedicationPlan(plan);
    }
    
    @Transactional
    @Override
    public newplans saveMedicationPlan(newplans plan){
        return dao.saveMedicationPlan(plan);
    }
    
    @Transactional(readOnly = true)
    @Override
    public newplans getMedicationPlan(Concept planName){
        return dao.getMedicationPlan(planName);
    }
    
}
