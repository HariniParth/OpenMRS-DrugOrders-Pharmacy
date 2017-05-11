/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.drugorders;

import java.io.Serializable;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.Concept;

/**
 *
 * @author harini-geek
 */
public class newplans extends BaseOpenmrsObject implements Serializable{
    
    private Integer id;
    // Description of the medication plan
    private String planDesc;
    // Name of the medication plan
    private Concept planName;   
    // Status of the medication plan
    private String planStatus;
    // Reason why this plan was discarded (if discarded)
    private String discardReason;
    
    public newplans(){
        
    }
    
    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Concept getPlanName() {
        return planName;
    }

    public void setPlanName(Concept planName) {
        this.planName = planName;
    }
    
    public String getPlanDesc() {
        return planDesc;
    }

    public void setPlanDesc(String planDesc) {
        this.planDesc = planDesc;
    }
    
    public String getPlanStatus() {
        return planStatus;
    }

    public void setPlanStatus(String planStatus) {
        this.planStatus = planStatus;
    }
    
    public String getDiscardReason() {
        return discardReason;
    }

    public void setDiscardReason(String discardReason) {
        this.discardReason = discardReason;
    }
    
}
