/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.drugorders;

import java.io.Serializable;
import org.openmrs.BaseOpenmrsObject;

/**
 *
 * @author harini-geek
 */
public class planorders extends BaseOpenmrsObject implements Serializable{
    
    private Integer id;
    // Plan ID number
    private Integer standardPlanId;
    // Drug order ID number
    private Integer orderId;  
    
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
    
    public Integer getStandardPlanId() {
        return standardPlanId;
    }

    public void setStandardPlanId(Integer standardPlanId) {
        this.standardPlanId = standardPlanId;
    }
    
    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }
    
}
