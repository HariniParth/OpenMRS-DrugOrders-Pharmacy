/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.drugorders;

import java.util.Date;
import org.openmrs.Concept;
import java.io.Serializable;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.BaseOpenmrsMetadata;

/**
 * It is a model class. It should extend either {@link BaseOpenmrsObject} or {@link BaseOpenmrsMetadata}.
 */
public class drugorders extends BaseOpenmrsObject implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;
        // Date from which the medication is started
        private Date startDate;
        // Order ID representing the record in orders and drug_order table
        private Integer orderId;
        // Number representing the group of orders to which this order belongs to
        private Integer groupId;
        // Concept ID representing the drug
        private Concept drugName;
        // If the Patient is allergic to the drug ordered
        private Integer isAllergic;
        // Disease to treat
        private Concept associatedDiagnosis;
        // Reason for ordering the drug inspite of the Patient being allergic to it
        private String isAllergicOrderReasons;
        // Instructions provided by the Physician for the Patient
        private String patientInstructions;
        // Instructions provided by the Physician for the Pharmacist
        private String pharmacistInstructions;
        // Indicates if the order is placed on hold
        private Integer onHold;
        // The number of allowed refills at the pharmacy
        private Integer refill;
        // Priority assigned to the drug order
        private Concept priority;
        // Indicates if the order has been requested to be discarded
        private Integer forDiscard;
        // Status of the order - Active/Non-Active
        private String orderStatus;
        // Number of days between dispatch of refills
        private Integer refillInterval;
        // Predefined reason to discontinue the drug order
        private Concept discontinueReason;
        // Free-Text reason to discontinue the drug order
        private String discontinuationReasons;
        // Date on which the drug expires
        private Date drugExpiryDate;
        // Date on which the drug was last dispatched
        private Date lastDispatchDate;
        // Comments from the Pharmacist for the Orderer
        private String commentForOrderer;
        // Comments from the Pharmacist for the Patient
        private String commentForPatient;
             
	
        public drugorders(){
            
        }
        
	@Override
	public Integer getId() {
            return id;
	}
	
	@Override
	public void setId(Integer id) {
            this.id = id;
	}
        
        public Integer getOrderId() {
            return orderId;
        }

        public void setOrderId(Integer orderId) {
            this.orderId = orderId;
        }
    
        public Integer getGroupId() {
            return groupId;
        }

        public void setGroupId(Integer groupId) {
            this.groupId = groupId;
        }
        
        public Concept getDrugName() {
            return drugName;
	}

	public void setDrugName(Concept drugName) {
            this.drugName = drugName;
	}
        
        public Date getStartDate() {
            return startDate;
        }

        public void setStartDate(Date startDate) {
            this.startDate = startDate;
        }
        
        public Integer getIsAllergic() {
            return isAllergic;
	}
	
	public void setIsAllergic(Integer isAllergic) {
            this.isAllergic = isAllergic;
	}
        
        public String getIsAllergicOrderReasons() {
            return isAllergicOrderReasons;
	}

	public void setIsAllergicOrderReasons(String isAllergicOrderReasons) {
            this.isAllergicOrderReasons = isAllergicOrderReasons;
	}
        
        public Concept getAssociatedDiagnosis() {
            return associatedDiagnosis;
	}

	public void setAssociatedDiagnosis(Concept associatedDiagnosis) {
            this.associatedDiagnosis = associatedDiagnosis;
	}
        
        public String getPatientInstructions() {
            return patientInstructions;
	}

	public void setPatientInstructions(String patientInstructions) {
            this.patientInstructions = patientInstructions;
	}
        
        public String getPharmacistInstructions() {
            return pharmacistInstructions;
	}

	public void setPharmacistInstructions(String pharmacistInstructions) {
            this.pharmacistInstructions = pharmacistInstructions;
	} 
        
        public Concept getPriority() {
            return priority;
	}

	public void setPriority(Concept priority) {
            this.priority = priority;
	}
        
        public Integer getRefillInterval() {
            return refillInterval;
	}

	public void setRefillInterval(Integer refillInterval) {
            this.refillInterval = refillInterval;
	}
        
        public Integer getRefill() {
            return refill;
	}

	public void setRefill(Integer refill) {
            this.refill = refill;
	}
        
        public String getOrderStatus() {
            return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
            this.orderStatus = orderStatus;
	}
           
        public Integer getOnHold() {
            return onHold;
        }

        public void setOnHold(Integer onHold) {
            this.onHold = onHold;
        }
        
        public Integer getForDiscard() {
            return forDiscard;
	}
	
	public void setForDiscard(Integer forDiscard) {
            this.forDiscard = forDiscard;
	}
        
        public Concept getDiscontinueReason() {
            return discontinueReason;
	}

	public void setDiscontinueReason(Concept discontinueReason) {
            this.discontinueReason = discontinueReason;
	}
        
        public String getDiscontinuationReasons() {
            return discontinuationReasons;
	}

	public void setDiscontinuationReasons(String discontinuationReasons) {
            this.discontinuationReasons = discontinuationReasons;
	}
                
        public Date getLastDispatchDate() {
            return lastDispatchDate;
        }

        public void setLastDispatchDate(Date lastDispatchDate) {
            this.lastDispatchDate = lastDispatchDate;
        }
        
        public Date getDrugExpiryDate() {
            return drugExpiryDate;
        }

        public void setDrugExpiryDate(Date drugExpiryDate) {
            this.drugExpiryDate = drugExpiryDate;
        }
        
        public String getCommentForOrderer() {
            return commentForOrderer;
	}

	public void setCommentForOrderer(String commentForOrderer) {
            this.commentForOrderer = commentForOrderer;
	}
        
        public String getCommentForPatient() {
            return commentForPatient;
	}

	public void setCommentForPatient(String commentForPatient) {
            this.commentForPatient = commentForPatient;
	}
}