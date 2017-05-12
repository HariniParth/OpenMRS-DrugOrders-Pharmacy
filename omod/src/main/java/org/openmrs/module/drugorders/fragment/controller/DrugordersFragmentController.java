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
        
        // Retrieve the list of active individual drug orders.
        drugOrders.addAll(getActiveOrders(patient, "Active"));
        // Retrieve the list of active group drug orders.
        drugOrders.addAll(getActiveOrders(patient, "Active-Plan"));
        // Retrieve the list of active med plan drug orders.
        drugOrders.addAll(getActiveOrders(patient, "Active-Group"));
        
        model.addAttribute("drugorders", drugOrders);
    }
    
    private List<drugorders> getActiveOrders(Patient patient, String status){
        return Context.getService(drugordersService.class).getDrugOrdersByPatientAndStatus(patient, status);
    }
}