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
import org.openmrs.ui.framework.page.PageModel;

/**
 *
 * @author harini-geek
 */
public class SearchBarByPatientFragmentController {
    
    public void controller(PageModel model) {
        
        List<String> allPatientNames = new ArrayList<>();
        
        List<Patient> allPatients = Context.getPatientService().getAllPatients();
        for(Patient patient : allPatients){
            allPatientNames.add(Context.getPersonService().getPerson(patient.getPatientId()).getGivenName()+" "+Context.getPersonService().getPerson(patient.getPatientId()).getFamilyName());
        }
        
        model.addAttribute("allPatientNames", allPatientNames);
    }
        
}