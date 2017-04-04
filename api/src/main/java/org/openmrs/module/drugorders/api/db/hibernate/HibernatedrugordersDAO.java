/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.drugorders.api.db.hibernate;


import java.util.List;
import java.util.Iterator;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.apache.commons.logging.Log;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.drugorders.drugorders;
import org.openmrs.module.drugorders.api.db.drugordersDAO;


/**
 * It is a default implementation of  {@link drugordersDAO}.
 */
public class HibernatedrugordersDAO implements drugordersDAO {
    
    private SessionFactory sessionFactory;
    protected final Log log = LogFactory.getLog(this.getClass());
	
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
   
    @Override
    public List<drugorders> getOrdersOnHold(){
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(
                drugorders.class);
        crit.add(Restrictions.eq("onHold", 1));
        return crit.list();
    };
    
    @Override
    public List<drugorders> getOrdersForDiscard(){
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(
                drugorders.class);
        crit.add(Restrictions.eq("forDiscard", 1));
        return crit.list();
    };
    
    @Override
    public drugorders saveDrugOrder(drugorders drugOrder) {
        sessionFactory.getCurrentSession().saveOrUpdate(drugOrder);
        return drugOrder;
    };
    
    @Override
    public drugorders getDrugOrderByOrderID(Integer orderId){
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(
                drugorders.class);
        crit.add(Restrictions.eq("orderId", orderId));
        return (drugorders) crit.uniqueResult();
    };

    @Override
    public List<drugorders> getDrugOrdersByGroupID(Integer groupId){
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(
                drugorders.class);
        crit.add(Restrictions.eq("groupId", groupId));
        return crit.list();
    };
    
    @Override
    public List<drugorders> getDrugOrdersByPatient(Patient patient) {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(
                drugorders.class);
        crit.add(Restrictions.eq("patientId", patient.getPatientId()));
        return crit.list();
    };
    
    @Override
    public int getLastGroupID(){
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(
                drugorders.class);
        crit.setProjection(Projections.property("id"));
        List l=crit.list();
        Iterator it=l.iterator();
        int groupId = 0;
        if(it.hasNext()){
            Criteria critMax = sessionFactory.getCurrentSession().createCriteria(drugorders.class).setProjection(Projections.max("groupId"));
            if(critMax.uniqueResult() == null)
                groupId = 0;
            else
                groupId = (Integer)critMax.uniqueResult();
        }
            
        return groupId;
    };
    
    @Override
    public drugorders getDrugOrderByDrugAndPatient(Concept drug, Patient patient){
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(
                drugorders.class);
        crit.add(Restrictions.eq("drugName", drug)).add(Restrictions.eq("patientId", patient.getPatientId()));
        return (drugorders) crit.uniqueResult();
    };
    
    @Override
    public List<drugorders> getDrugOrdersByPatientAndStatus(Patient patient, String status){
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(
                drugorders.class);
        crit.add(Restrictions.eq("orderStatus", status)).add(Restrictions.eq("patientId", patient.getPatientId()));
        return crit.list();
    };
    
}