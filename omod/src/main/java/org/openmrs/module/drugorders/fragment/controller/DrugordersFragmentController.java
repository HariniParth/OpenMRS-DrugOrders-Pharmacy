/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.drugorders.fragment.controller;

import java.util.ArrayList;
import java.util.List;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.drugorders.api.drugordersService;
import org.openmrs.module.drugorders.drugorders;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;

/**
 *
 * @author harini-geek
 */
public class DrugordersFragmentController {
    
    public void controller(FragmentModel model, @FragmentParam("patientId") Patient patient){
        
        List<drugorders> drugOrders = new ArrayList<>();
        
        drugOrders.addAll(getActiveOrders(patient, "Active"));
        drugOrders.addAll(getActiveOrders(patient, "Active-Plan"));
        drugOrders.addAll(getActiveOrders(patient, "Active-Group"));
        
        model.addAttribute("drugorders", drugOrders);
    }
    
    /*
      Get the list of drug orders for this Patient and in this status
    */
    private List<drugorders> getActiveOrders(Patient patient, String status){
        return Context.getService(drugordersService.class).getDrugOrdersByPatientAndStatus(patient, status);
    }
}