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
    private String planDesc;
    private Concept planName;    
    
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
    
}
