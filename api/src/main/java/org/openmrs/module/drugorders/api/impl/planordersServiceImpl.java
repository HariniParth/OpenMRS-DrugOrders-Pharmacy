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
    
    // Get last assigned Plan ID
    @Transactional(readOnly = true)
    @Override
    public int getLastPlanID(){
        return dao.getLastPlanID();
    }
    
    // Save plan order record
    @Transactional
    @Override
    public planorders savePlanOrder(planorders order){
        return dao.savePlanOrder(order);
    }
    
    // Get the planorders record using the drug order ID
    @Transactional(readOnly = true)
    @Override
    public planorders getPlanOrderByOrderID(Integer id){
        return dao.getPlanOrderByOrderID(id);
    }
    
    // Get the list of planorders records having the same plan ID
    @Transactional(readOnly = true)
    @Override
    public List<planorders> getPlanOrdersByPlanID(Integer planId){
        return dao.getPlanOrdersByPlanID(planId);
    }
    
}
