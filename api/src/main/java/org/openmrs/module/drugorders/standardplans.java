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
    // Standard formulation - Dose
    private Double dose;
    // Standard formulation - Route
    private Concept route;
    // Standard formulation - Drug Concept
    private Concept drugId;
    // Standard formulation - Plan ID
    private Integer planId;
    // Standard formulation - Quantity
    private Double quantity;
    // Standard formulation - Duration
    private Integer duration;    
    // Standard formulation - Dose units
    private Concept doseUnits;
    // Standard formulation - Status of the plan
    private String planStatus;
    // Reason for discarding this medication plan
    private String discardReason;
    // Standard formulation - Duration units
    private Concept durationUnits;
    // Standard formulation - Quantity units
    private Concept quantityUnits;
    // Standard formulation - Frequency
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
    
    public String getPlanStatus() {
        return planStatus;
    }

    public void setPlanStatus(String planStatus) {
        this.planStatus = planStatus;
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

    public String getDiscardReason() {
        return discardReason;
    }

    public void setDiscardReason(String discardReason) {
        this.discardReason = discardReason;
    }
    
    public Concept getDurationUnits() {
        return durationUnits;
    }

    public void setDurationUnits(Concept durationUnits) {
        this.durationUnits = durationUnits;
    }
        
}