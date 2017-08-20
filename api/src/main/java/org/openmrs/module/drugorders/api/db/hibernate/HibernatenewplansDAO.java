/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.drugorders.api.db.hibernate;

import java.util.List;
import org.openmrs.Concept;
import org.hibernate.Criteria;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.module.drugorders.api.db.newplansDAO;
import org.openmrs.module.drugorders.newplans;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author harini-geek
 */
public class HibernatenewplansDAO implements newplansDAO {
    
    private SessionFactory sessionFactory;
    protected final Log log = LogFactory.getLog(this.getClass());

    public SessionFactory getSessionFactory() {
	    return sessionFactory;
    }
    
    public void setSessionFactory(SessionFactory sessionFactory) {
	    this.sessionFactory = sessionFactory;
    }
    
    // Get a medication plan by its ID
    @Transactional(readOnly = true)
    @Override
    public newplans getMedPlanByPlanID(Integer id){
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(newplans.class);
        crit.add(Restrictions.eq("id", id));
        return (newplans) crit.uniqueResult();
    }
    
    // Get the list of active medication plans
    @Transactional(readOnly = true)
    @Override
    public List<newplans> getAllMedPlans(){
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(newplans.class);
        return crit.list();
    }
    
    // Discard a medication plan
    @Transactional
    @Override
    public void discardMedPlan(newplans plan){
        sessionFactory.getCurrentSession().delete(plan);
    };
    
    // Get a medication plan by its name
    @Transactional(readOnly = true)
    @Override
    public newplans getMedPlanByPlanName(Concept planName){
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(newplans.class);
        crit.add(Restrictions.eq("planName", planName)).add(Restrictions.eq("planStatus", "Active"));
        return (newplans) crit.uniqueResult();
    };
    
    // Save a new medication plan
    @Override
    public newplans saveMedPlan(newplans plan){
        if(sessionFactory.getCurrentSession().contains(plan))
            sessionFactory.getCurrentSession().saveOrUpdate(plan);
        else
            sessionFactory.getCurrentSession().merge(plan);
        
        return plan;
    };
    
}
