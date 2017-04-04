/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.drugorders;

import org.openmrs.Concept;
import java.io.Serializable;
import org.openmrs.OrderFrequency;
import org.openmrs.BaseOpenmrsObject;

/**
 *
 * @author harini-geek
 */

public class standardplans extends BaseOpenmrsObject implements Serializable{
    
    private Integer id;
    private Double dose;
    private Concept route;
    private Concept drugId;
    private Integer planId;
    private Double quantity;
    private Integer duration;
    private Concept doseUnits;
    private Concept durationUnits;
    private Concept quantityUnits;
    private OrderFrequency frequency;
    
    public standardplans(){
        
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

    public Concept getDrugId() {
        return drugId;
    }

    public void setDrugId(Concept drugId) {
        this.drugId = drugId;
    }

    public Concept getRoute() {
        return route;
    }

    public void setRoute(Concept route) {
        this.route = route;
    }

    public OrderFrequency getFrequency() {
        return frequency;
    }

    public void setFrequency(OrderFrequency frequency) {
        this.frequency = frequency;
    }

    public Double getDose() {
        return dose;
    }

    public void setDose(Double dose) {
        this.dose = dose;
    }

    public Concept getDoseUnits() {
        return doseUnits;
    }

    public void setDoseUnits(Concept doseUnits) {
        this.doseUnits = doseUnits;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Concept getQuantityUnits() {
        return quantityUnits;
    }

    public void setQuantityUnits(Concept quantityUnits) {
        this.quantityUnits = quantityUnits;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Concept getDurationUnits() {
        return durationUnits;
    }

    public void setDurationUnits(Concept durationUnits) {
        this.durationUnits = durationUnits;
    }
        
}