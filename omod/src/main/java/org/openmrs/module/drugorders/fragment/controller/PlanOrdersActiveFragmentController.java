/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.drugorders.fragment.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.openmrs.CareSetting;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.drugorders.api.drugordersService;
import org.openmrs.module.drugorders.api.planordersService;
import org.openmrs.module.drugorders.drugorders;
import org.openmrs.module.drugorders.planorders;
import org.openmrs.module.uicommons.util.InfoErrorMessageUtil;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author harini-parthasarathy
 */
public class PlanOrdersActiveFragmentController {

    /** Logger for this class and subclasses */
    protected final Log log = LogFactory.getLog(getClass());

    public void controller(FragmentModel model, @RequestParam("patientId") Patient patient, HttpSession session,
            @RequestParam(value = "activatePlan", required = false) Integer activatePlan) {

        // Activate saved draft med plan drug orders.
        if (activatePlan != null) {
            // Get the list of med plan related drug orders that are currently in 'Draft'
            // status and set the status to 'Active'.
            List<planorders> planOrders = Context.getService(planordersService.class)
                    .getPlanOrdersByPlanID(activatePlan);

            // Check if Physician has provided instructions to the Patient and to the
            // Pharmacist.
            boolean detailsProvided = true;
            for (planorders order : planOrders) {
                drugorders d = Context.getService(drugordersService.class).getDrugOrderByOrderID(order.getOrderId());
                if (d.getPatientInstructions() == null || d.getPharmacistInstructions() == null) {
                    detailsProvided = false;
                    InfoErrorMessageUtil.flashErrorMessage(session,
                            "Please update instructions on Order Number " + d.getOrderId());
                }
            }

            if (detailsProvided) {
                for (planorders order : planOrders) {
                    drugorders d = Context.getService(drugordersService.class)
                            .getDrugOrderByOrderID(order.getOrderId());
                    d.setOrderStatus("Active-Plan");
                }
            }
        }

        // Store the mapping of plan name to plan ID
        HashMap<Integer, Concept> planName = new HashMap<>();
        // Get the records for CareSetting 'Outpatient'.
        CareSetting careSetting = Context.getOrderService().getCareSettingByName("Outpatient");
        // Get the records for OrderType 'Drug Order'
        OrderType orderType = Context.getOrderService().getOrderTypeByName("Drug Order");
        // Get the list of all Orders for the Patient.
        List<Order> orders = Context.getOrderService().getOrders(patient, careSetting, orderType, true);

        /*
         * =============================================================================
         * ============ To Display the list of medication plan related drug orders, with
         * the status "Active-Plan"
         * =============================================================================
         * ============
         */

        /*
         * Data structure to store the 'Drug Order' object properties for all the active
         * orders made for a medication plan Storing HashMap<Plan-ID, HashMap<Order-ID,
         * DrugOrder>>
         */
        HashMap<Integer, HashMap<Integer, DrugOrder>> ActivePlanMain = new HashMap<>();
        /*
         * Data structure to store the 'drugorders' object properties for all the active
         * orders made for a medication plan Storing HashMap<Plan-ID, HashMap<Order-ID,
         * drugorders>>
         */
        HashMap<Integer, HashMap<Integer, drugorders>> ActivePlanExtn = new HashMap<>();
        // Store the list of Orders records.
        HashMap<Integer, Order> Orders = new HashMap<>();
        // Store the list of Orderer records.
        HashMap<Integer, String> Orderer = new HashMap<>();

        // Retrieve the list of medication plan related drug orders, having the status
        // "Active-Plan"
        List<drugorders> drugorders = new ArrayList<>();
        for (Order order : orders) {
            // Store the list of Order records
            Orders.put(order.getOrderId(), Context.getOrderService().getOrder(order.getOrderId()));
            // Store the list of Orderer name records
            Orderer.put(order.getOrderId(),
                    Context.getOrderService().getOrder(order.getOrderId()).getCreator().getGivenName() + " "
                            + Context.getOrderService().getOrder(order.getOrderId()).getCreator().getFamilyName());

            if (Context.getService(drugordersService.class).getDrugOrderByOrderID(order.getOrderId()).getOrderStatus()
                    .equals("Active-Plan")) {
                drugorders.add(Context.getService(drugordersService.class).getDrugOrderByOrderID(order.getOrderId()));
            }
        }

        for (drugorders order : drugorders) {

            // Retrieve the corresponding planorders record.
            planorders p_order = Context.getService(planordersService.class).getPlanOrderByOrderID(order.getOrderId());

            // If the selected plan related drug orders are not already retrieved, retrieve
            // the orders and store the objects in ActivePlanMain and ActivePlanExtn
            // HashMap.
            if (!ActivePlanMain.containsKey(p_order.getStandardPlanId())) {

                // Store the mapping of plan name to plan ID
                planName.put(p_order.getStandardPlanId(), order.getAssociatedDiagnosis());
                // Storing HashMap<Order-ID, DrugOrder>
                HashMap<Integer, DrugOrder> main = new HashMap<>();
                // Storing HashMap<Order-ID, drugorders>
                HashMap<Integer, drugorders> extn = new HashMap<>();

                // Fetch the references to the related drug orders made as a part of the same
                // plan order.
                List<planorders> plans = Context.getService(planordersService.class)
                        .getPlanOrdersByPlanID(p_order.getId());

                for (planorders plan : plans) {
                    int id = plan.getOrderId();
                    // Select the drug orders that are currently active.
                    if (Context.getService(drugordersService.class).getDrugOrderByOrderID(id).getOrderStatus()
                            .equals("Active-Plan")) {

                        main.put(id, (DrugOrder) Context.getOrderService().getOrder(id));
                        extn.put(id, Context.getService(drugordersService.class).getDrugOrderByOrderID(id));
                    }
                }

                ActivePlanMain.put(p_order.getStandardPlanId(), main);
                ActivePlanExtn.put(p_order.getStandardPlanId(), extn);
            }
        }

        model.addAttribute("ActivePlanMain", ActivePlanMain);
        model.addAttribute("ActivePlanExtn", ActivePlanExtn);
        model.addAttribute("Orders", Orders);
        model.addAttribute("Orderer", Orderer);

        /*
         * =============================================================================
         * =========== To Display the list of medication plan related drug orders, with
         * the status "Draft-Plan"
         * =============================================================================
         * ===========
         */

        /*
         * Data structure to store the 'Drug Order' object properties for all the draft
         * orders made for a medication plan Storing HashMap<Plan-ID, HashMap<Order-ID,
         * DrugOrder>>
         */
        HashMap<Integer, HashMap<Integer, DrugOrder>> DraftPlanMain = new HashMap<>();
        /*
         * Data structure to store the 'drugorders' object properties for all the draft
         * orders made for a medication plan Storing HashMap<Plan-ID, HashMap<Order-ID,
         * drugorders>>
         */
        HashMap<Integer, HashMap<Integer, drugorders>> DraftPlanExtn = new HashMap<>();

        drugorders.clear();
        // Retrieve the list of medication plan related drug orders, with the status
        // "Draft-Plan"
        for (Order order : orders) {
            if (Context.getService(drugordersService.class).getDrugOrderByOrderID(order.getOrderId()).getOrderStatus()
                    .equals("Draft-Plan")) {
                drugorders.add(Context.getService(drugordersService.class).getDrugOrderByOrderID(order.getOrderId()));
            }
        }

        for (drugorders order : drugorders) {
            planorders p_order = Context.getService(planordersService.class).getPlanOrderByOrderID(order.getOrderId());

            // If the selected plan related orders are not already retrieved, retrieve the
            // orders
            if (p_order != null && !DraftPlanMain.containsKey(p_order.getStandardPlanId())) {

                // Store the mapping of plan name to plan ID
                planName.put(p_order.getStandardPlanId(), order.getAssociatedDiagnosis());
                // Storing HashMap<Order-ID, DrugOrder>
                HashMap<Integer, DrugOrder> main = new HashMap<>();
                // Storing HashMap<Order-ID, drugorders>
                HashMap<Integer, drugorders> extn = new HashMap<>();

                // Fetch the references to the related drug orders made as a part of the same
                // plan order.
                List<planorders> plans = Context.getService(planordersService.class)
                        .getPlanOrdersByPlanID(p_order.getId());

                for (planorders plan : plans) {
                    int id = plan.getOrderId();
                    // Select the drug orders that are currently in draft status.
                    if (Context.getService(drugordersService.class).getDrugOrderByOrderID(id).getOrderStatus()
                            .equals("Draft-Plan")) {
                        main.put(id, (DrugOrder) Context.getOrderService().getOrder(id));
                        extn.put(id, Context.getService(drugordersService.class).getDrugOrderByOrderID(id));
                    }
                }

                DraftPlanMain.put(p_order.getStandardPlanId(), main);
                DraftPlanExtn.put(p_order.getStandardPlanId(), extn);
            }
        }

        model.addAttribute("DraftPlanMain", DraftPlanMain);
        model.addAttribute("DraftPlanExtn", DraftPlanExtn);

        model.addAttribute("planName", planName);
    }
}