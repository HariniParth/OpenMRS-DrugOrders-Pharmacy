/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.drugorders;

import org.openmrs.Concept;
import java.io.Serializable;
import org.openmrs.BaseOpenmrsObject;

/**
 *
 * @author harini-geek
 */
public class planorders extends BaseOpenmrsObject implements Serializable{
    
    private Integer id;
    // Plan ID number
    private Integer planId;
    // Drug order ID number
    private Integer orderId;
    // Patient for whom the plan is ordered
    private Integer patientId;
    // Disease to treat which the plan is ordered
    private Concept diseaseId;    
    
    public planorders(){
        
    }
    
    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }
    
    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }
    
    public Concept getDiseaseId() {
        return diseaseId;
    }

    public void setDiseaseId(Concept diseaseId) {
        this.diseaseId = diseaseId;
    }
    
    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }
}
