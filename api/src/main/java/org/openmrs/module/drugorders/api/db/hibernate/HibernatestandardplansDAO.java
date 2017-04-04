/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.drugorders.api.db.hibernate;

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.apache.commons.logging.Log;
import org.hibernate.criterion.Restrictions;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.drugorders.standardplans;
import org.openmrs.module.drugorders.api.db.standardplansDAO;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author harini-geek
 */
public class HibernatestandardplansDAO implements standardplansDAO {
    
    private SessionFactory sessionFactory;
    protected final Log log = LogFactory.getLog(this.getClass());

    public SessionFactory getSessionFactory() {
	    return sessionFactory;
    }
    
    public void setSessionFactory(SessionFactory sessionFactory) {
	    this.sessionFactory = sessionFactory;
    }
    
    @Transactional
    @Override
    public void deleteMedicationPlan(standardplans plan){
        sessionFactory.getCurrentSession().delete(plan);
    };
    
    @Override
    public standardplans saveMedicationPlan(standardplans plan) {
        sessionFactory.getCurrentSession().saveOrUpdate(plan);
        return plan;
    };
        
    @Transactional(readOnly = true)
    @Override
    public List<standardplans> getMedicationPlans(Integer planId){
        
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(standardplans.class);
        crit.add(Restrictions.eq("planId", planId));
        return crit.list();
    };
    
    @Override
    public standardplans getMedicationPlan(Integer id){
        return (standardplans) sessionFactory.getCurrentSession().get(standardplans.class, id);
    };
}
