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
    
    @Transactional(readOnly = true)
    @Override
    public newplans getMedicationPlan(Integer id){
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(newplans.class);
        crit.add(Restrictions.eq("id", id));
        return (newplans) crit.uniqueResult();
    }
    
    @Transactional(readOnly = true)
    @Override
    public List<newplans> getAllMedicationPlans(){
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(newplans.class);
        return crit.list();
    }
    
    @Transactional
    @Override
    public void deleteMedicationPlan(newplans plan){
        sessionFactory.getCurrentSession().delete(plan);
    };
    
    @Transactional(readOnly = true)
    @Override
    public newplans getMedicationPlan(Concept planName){
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(newplans.class);
        crit.add(Restrictions.eq("planName", planName));
        return (newplans) crit.uniqueResult();
    };
    
    @Override
    public newplans saveMedicationPlan(newplans plan){
        sessionFactory.getCurrentSession().saveOrUpdate(plan);
        return plan;
    };
    
}
