/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.drugorders.api.db.hibernate;

import java.util.Iterator;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.apache.commons.logging.Log;
import org.hibernate.criterion.Restrictions;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Projections;
import org.openmrs.module.drugorders.planorders;
import org.springframework.transaction.annotation.Transactional;
import org.openmrs.module.drugorders.api.db.planordersDAO;


/**
 *
 * @author harini-geek
 */
public class HibernateplanordersDAO implements planordersDAO {
    
    private SessionFactory sessionFactory;
    protected final Log log = LogFactory.getLog(this.getClass());
	
    public SessionFactory getSessionFactory() {
	    return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
	    this.sessionFactory = sessionFactory;
    }
        
    // Save plan order record
    @Override
    public planorders savePlanOrder(planorders order){
        sessionFactory.getCurrentSession().saveOrUpdate(order);
        return order;
    };
    
    // Get the planorders record using the drug order ID
    @Override
    public planorders getPlanOrderByOrderID(Integer orderId){
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(
                planorders.class);
        crit.add(Restrictions.eq("orderId", orderId));
        return (planorders) crit.uniqueResult();
    };
    
    /**
     *
     * @param planId
     * @return
     * Get the list of planorders records having the same plan ID
     */
    @Transactional(readOnly = true)
    @Override
    public List<planorders> getPlanOrdersByPlanID(Integer planId){
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(
                planorders.class);
        crit.add(Restrictions.idEq(planId));
        return crit.list();
    };
        
    // Get last assigned Plan ID
    @Override
    public int getLastPlanID(){
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(
                planorders.class);
        crit.setProjection(Projections.property("id"));
        List l=crit.list();
        Iterator it=l.iterator();
        int planId = 0;
        if(it.hasNext()){
            Criteria critMax = sessionFactory.getCurrentSession().createCriteria(planorders.class).setProjection(Projections.max("standardPlanId"));
            if(critMax.uniqueResult() == null)
                planId = 0;
            else
                planId = (Integer)critMax.uniqueResult();
        }
            
        return planId;
    };
}
