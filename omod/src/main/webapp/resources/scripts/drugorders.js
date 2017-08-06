/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* global diagnosis, jq, emr, Event */

var removeFromGroupDialog = null;
var saveDraftOrderDialog = null;

jq(document).ready( function() {
      
    highlight();
    checkSelection();
    
    jq("#planSaveButton").prop("disabled", true);
    jq("#addOrderButton").prop("disabled", true);
    jq("#discardAdminPlan").prop("disabled", true);
    jq("#planDefineButton").prop("disabled", true);
        
    /*
     * If drug or diagnosis is selected, check if other parameters are provided and enable form submission.
     */
    jq('#drugName, #diagnosis').autocomplete({
        select: function () { 
            validate();
            checkFormFields();
        }
    });
    
    /*
     * If a plan is defined (Administrator page), enable the form submission to create a plan.
     */
    jq('#definePlanName').autocomplete({
        select: function () { 
            jq("#planDefineButton").prop("disabled", false); 
            
            if(jq("#definePlanDesc").val() === "")
                document.getElementById("definePlanDesc").style.borderColor = "orangered";
            else
                document.getElementById("definePlanDesc").style.borderColor = "";
        }
    });
    
    jq('#definePlanName').change(function (){ 
        if(jq('#definePlanName').val() !== ""){
            jq("#planDefineButton").prop("disabled", false); 
        } else {
            jq("#planDefineButton").prop("disabled", true); 
            document.getElementById("definePlanDesc").style.borderColor = "";
        }
    });
    
    /*
     * Remove highlights from Med Plan description field when provided.
     */
    jq('#definePlanDesc').change(function (){ 
        if(jq(this).val() === "")
            this.style.borderColor = "orangered";
        else
            this.style.borderColor = "";
    });
    
    /*
     * Remove highlights from Order discontinuation field when reason is provided.
     */
    jq('#nonCodedDiscardReason').change(function (){ 
        if(jq(this).val() === "")
            this.style.borderColor = "orangered";
        else
            this.style.borderColor = "";
    });
    
    /*
     * If orders are selected to be discarded, show the select input widget to specify the reasons to discard.
     */
    if(jq('#groupAction').val() === "DISCARD ORDER GROUP" || jq('#groupAction').val() === "DISCARD MED PLAN" || jq('#groupAction').val() === "DISCONTINUE ORDER"){
        jq("#orderActionButton").prop("disabled", true);
        jq("#discontinueReasonSelect").show();
        jq("#discontinueReasonSelect").css("display", "block");
    }
    
    /*
     * If the admin plan fields are modified, check the value of the remaining mandatory fields.
     * Highlight the unspecified fields. Only when all details are specified, allow to create a medication plan.
     */
    jq("#adminPlan, #adminDrug, #adminRoute, #adminDose, #adminDoseUnits, #adminQuantity, #adminQuantityUnits, #adminDuration, #adminDurationUnits, #adminFrequency").change(function(){
        checkAdminFields();
    });
    
    /*
     * If the create drug order fields are modified, check the value of the remaining mandatory fields.
     * Highlight the unspecified fields. Only when all details are specified, allow to create a drug order.
     */
    jq("#drugName, #route, #dose, #doseUnits, #quantity, #quantityUnits, #duration, #durationUnits, #frequency, #diagnosis, #orderReason, #patientInstrn, #pharmacistInstrn").change(function(){
        validate();
        if(jq("#drugName").val() !== ""){
            checkFormFields();
        }
    });
    
    /*
     * Enable confirm button to select a medication plan if one or more orders from the plan is selected.
     */
    jq('.planDrugName .groupCheckBox').on('change', function() {
        var selected = false;
        jq('.planDrugName .groupCheckBox').each(function() {
            if(this.checked) {
                selected = true; 
                jq(this).parent().next('.drugDetails').first().find('.planOrderReason').prop("readonly", false);                
            } else {
                jq(this).parent().next('.drugDetails').first().find('.planOrderReason').val("");
                jq(this).parent().next('.drugDetails').first().find('.planOrderReason').prop("readonly", true);                
            }
        });
        if(selected){
            jq('#selectPlanButton').removeAttr('disabled');
        } else {
            jq("#selectPlanButton").prop("disabled", true);
        }        
    });
    
    /*
     * Enable confirm button to discard a medication plan if one or more orders from the plan is selected.
     */
    jq('#discardPlanBlock .groupCheckBox').on('change', function() {
        allowPlanDiscard();       
    });
    
    /*
     * Check if a reason to discard the medication plan is provided.
     */
    jq('#discardReason').change(function(){
        allowPlanDiscard(); 
    });
    
    /*
     * Remove the necessity to provide a reason to renew a drug order if it is not selected to be renewed.
     */
    jq('.groupDrugName .groupCheckBox').on('change', function() {
        var selected = false;
        jq('.groupDrugName .groupCheckBox').each(function() {
            if(this.checked) {
                selected = true;
                jq(this).parent().next('.drugDetails').first().find('.reviseOrderReason').prop("readonly", false);                
            } else {
                jq(this).parent().next('.drugDetails').first().find('.reviseOrderReason').val("");
                jq(this).parent().next('.drugDetails').first().find('.reviseOrderReason').prop("readonly", true);
            }
        });   
        if(selected && (jq("#groupOrderAction").text() === "RENEW MED PLAN" || jq("#groupOrderAction").text() === "RENEW ORDER GROUP")){
            jq('#orderActionButton').removeAttr('disabled');
        }
        if(selected && (jq("#groupOrderAction").text() === "DISCARD MED PLAN" || jq("#groupOrderAction").text() === "DISCARD ORDER GROUP" || jq("#groupOrderAction").text() === "DISCONTINUE ORDER")){
            discontinueReason();
        }
        if(!selected){
            jq("#orderActionButton").prop("disabled", true);
        }
    });
    
    document.getElementsByClassName("unchecked").checked = false;
    
    /*
     * Highlight the fields to enter the reasons to order a drug unless they are filled.
     */
    jq('#discardReason, .planOrderReason, .reviseOrderReason').each(function(){
        this.style.borderColor = "orangered";
    });
    
    jq('#discardReason, .planOrderReason, .reviseOrderReason').each(function(){
        jq(this).on('change', function(){
            if(jq(this).val() === "")
                this.style.borderColor = "orangered";
            else
                this.style.borderColor = "";
        });
    });
      
    saveDraftOrderDialog = emr.setupConfirmationDialog({
        selector: '#saveDraftPlan',
        actions: {
            cancel: function() {
            	saveDraftOrderDialog.close();
                clearHighlights();
            }
        }
    });
    
    if(document.getElementById("draftPlanList")){
        jq("#activeOrderWindow").hide();
    }
    /*
     * If draft order plans are present, disable navigating away from the page.
     */
    jq(document).mouseup(function (e){
        if(document.getElementById("draftPlanList")){
            if (!jq("#draftPlanRow").is(e.target) && jq("#draftPlanRow").has(e.target).length === 0 && !jq("#createOrderWindow").is(e.target) && jq("#createOrderWindow").has(e.target).length === 0 && !jq("#showGroupOrderWindow").is(e.target) && jq("#showGroupOrderWindow").has(e.target).length === 0 && e.target.nodeName !== "TD" && e.target.nodeName !== "TH" && e.target.nodeName !== "I" && e.target.nodeName !== "A" && e.target.nodeName !== "INPUT" && e.target.toString() !== "[object HTMLSpanElement]"){
                saveDraftOrderDialog.show();
            }
        }
    });
    
    jq("#pageRedirect").mouseover(function(e){
        if(document.getElementById("draftPlanList")){
            jq("#pageRedirect").off('click');
        } else {
            jq(this).hide();
        }
    }); 
    
    removeFromGroupDialog = emr.setupConfirmationDialog({
        selector: '#removeFromGroupWindow',
        actions: {
            cancel: function() {
            	removeFromGroupDialog.close();
                clearHighlights();
            },
            confirm: function() {
            	jq("#removeFromGroupForm").submit();
            }
        }
    });
    
    var lines = 5;
    // Check the number of rows entered in the given textarea
    jq("textarea").on("keydown", function(e){
        
        var newLines = $(this).val().split("\n").length;
        if(e.keyCode === 13 && newLines >= lines) {
            return false;
        }
    });
});

/*
 * Highlight the selected order in the drug orders table.
 */ 
function highlight(){
    /*
     * Highlight the medication plan selected to be discarded.
     */
    var selectedPlan = jq("#discardPlan").text();
    if(selectedPlan !== ""){
        var jqrowsNo = jq('#medPlansTable tbody tr .planDetails').filter(function () {
            if(jq.trim(jq(this).find('.fields').find('.planName').text()) === selectedPlan.toUpperCase()){
                jq(this).find('.fields').find('.icon-plus-sign').hide();
                jq(this).find('.fields').find('.icon-minus-sign').show();
                jq(this).find('.plansDetailsView').show();
                
                var selectedDrug = jq(".discardDrug");
                if(selectedDrug.size() === 1){
                    jq(this).find('.plansDetailsView').find('.planBlock').find('.planBlockDetails').find('.planItem').each(function(){
                        if(jq(this).text() === selectedDrug.text().toUpperCase()){
                            jq(this).parent().parent().css({"background": "#75b2f0","color": "white"});
                            jq(this).parent().parent().find('.show-details').hide();
                            jq(this).parent().parent().find('.hide-details').show();
                            jq(this).parent().parent().find('.groupItem').show();
                        }
                    });
                } else {
                    jq(this).find('.plansDetailsView').find('.planBlock').each(function(){
                        jq(this).find('.planBlockDetails').find('.groupItem').show();
                        jq(this).find('.planBlockDetails').find('.hide-details').show();
                        jq(this).find('.planBlockDetails').find('.show-details').hide();
                        jq(this).find('.planBlockDetails').css({"background": "#75b2f0","color": "white"});
                    });
                }
            }
        });
    }
    
    var planActioned = jq("#recordedMedPlan").val();
    
    if(planActioned !== null || planActioned !== undefined){
        var jqrowsNo = jq('#medPlansTable tbody tr .planDetails').filter(function () {
            if(jq.trim(jq(this).find('.fields').find('.planName').text()) === planActioned.toUpperCase()){
                jq(this).find('.fields').find('.icon-plus-sign').hide();
                jq(this).find('.fields').find('.icon-minus-sign').show();
                jq(this).find('.plansDetailsView').show();
            }
        });
    }
    
    var selectedAction = jq("#groupOrderAction").text();
    if(selectedAction !== ""){
        var selectedOrder = jq("#groupOrderID").val();
        
        if(selectedAction === "DISCONTINUE ORDER"){
            
            if(jq("#orderStatus").val() === "Active-Plan" || jq("#orderStatus").val() === "Draft-Plan"){
                jq("#activeOrderWindow").hide();
            }
            
            var jqrowsNo1 = jq('#activeOrdersTable tbody .orderRow').filter(function () {
                if(jq.trim(jq(this).children('td').slice(1, 2).text()) === selectedOrder){
                    jq(this).children('td').slice(1, 4).css({"background": "#75b2f0","color": "white"});
                }
            });
            
            var jqrowsNo2 = jq('#activeOrdersTable tbody .groupRow').filter(function () {
                jq(this).children('td').slice(1, 2).find('.groupDrug').each(function(){
                    if(jq.trim(jq(this).find('.groupDrugDetails').find('#groupDrugID').text()) === selectedOrder){
                        jq(this).find('.groupDrugDetails').css({"background": "#75b2f0","color": "white"});
                    }
                });
            });
            
            var jqrowsNo3 = jq('#activePlansTable tbody tr').filter(function () {
                jq(this).children('td').slice(0, 1).find('.plansDetailsView').find('.planDrug').each(function(){
                    if(jq.trim(jq(this).find('.planDrugDetails').find('#planDrugId').text()) === selectedOrder){
                        jq(this).parent().show();
                        jq(this).parent().parent().find('.fields').find('.icon-plus-sign').hide();
                        jq(this).parent().parent().find('.fields').find('.icon-minus-sign').show();
                        jq(this).find('.planDrugDetails').css({"background": "#75b2f0","color": "white"});
                    }
                });
            });
        }
        
        else if(selectedAction === "DISCARD ORDER GROUP"){
            var jqrowsNo = jq('#activeOrdersTable tbody .groupRow').filter(function () {
                if(jq.trim(jq(this).children('td').slice(0, 1).find('#id').text()) === selectedOrder){
                    jq(this).children('td').slice(1, 2).find('.groupDrug').each(function(){
                        jq(this).find('.groupDrugDetails').css({"background": "#75b2f0","color": "white"});
                    });
                }
            });
        }
        
        else if(selectedAction === "DISCARD MED PLAN"){
            jq("#activeOrderWindow").hide();
            var jqrowsNo = jq('#activePlansTable tbody tr').filter(function () {
                if(jq.trim(jq(this).children('td').slice(0, 1).find('#id').text()) === selectedOrder){
                    jq(this).children('td').slice(0, 1).find('.fields').find('.icon-plus-sign').hide();
                    jq(this).children('td').slice(0, 1).find('.fields').find('.icon-minus-sign').show();
                    jq(this).children('td').slice(0, 1).find('.plansDetailsView').find('.planDrug').each(function(){
                        jq(this).find('.planDrugDetails').css({"background": "#75b2f0","color": "white"});
                    });  
                    jq(this).find('.plansDetailsView').show();
                }
            });
        }
        
        else if(selectedAction === "RENEW MED PLAN"){
            jq("#activeOrderWindow").hide();
        }
    }    
}

/*
 * Clear background colors in table records.
 */
function clearHighlights(){
    jq(".orderRow").each(function(){
        jq(this).children('td').slice(1, 4).css({'background-color':'','color':''});
    });
    jq(".groupDrugDetails").each(function(){
        jq(this).css({'background-color':'','color':''});
    });
    jq(".oldOrderRow").each(function(){
        jq(this).children('td').slice(0, 1).css({'background-color':'','color':''});
    });
    jq(".oldGroupDetails").each(function(){
        jq(this).css({'background-color':'','color':''});
    });
    jq(".planDrug").each(function(){
        jq(this).children('div').slice(0, 1).css({'background-color':'','color':''});
    });
    jq('.planBlock').each(function(){
        jq(this).find('.planBlockDetails').css({'background-color':'','color':''});
    });
}

/*
 * If the create drug order fields are modified, check the value of the remaining mandatory fields.
 * Only when all details are specified, allow to create a drug order.
 */
function validate(){
    if(jq("#drugName").val() !== "" && jq("#route").val() !== "" && jq("#dose").val() !== "" && jq("#doseUnits").val() !== "" && jq("#quantity").val() !== "" && jq("#quantityUnits").val() !== "" && jq("#duration").val() !== "" && jq("#durationUnits").val() !== "" && jq("#frequency").val() !== "" && jq("#diagnosis").val() !== ""){
        jq("#addOrderButton").prop("disabled", false);
    } else {
        jq("#addOrderButton").prop("disabled", true);
    }
}

/*
 * Enable group button action when one or more orders from the selected list are checked to be ordered.
 */
function checkSelection(){
    var ordersSelected = false;
    jq('.groupDrugName .groupCheckBox').each(function() {
        if(this.checked) {
            ordersSelected = true;
        }
    });
    if(ordersSelected){
        jq("#orderActionButton").prop("disabled", false);
    } else {
        jq("#orderActionButton").prop("disabled", true);
    }
    
    var plansSelected = false;
    jq('.planDrugName .groupCheckBox').each(function() {
        if(this.checked) {
            plansSelected = true;
        }
    });
    if(plansSelected){
        jq("#selectPlanButton").prop("disabled", false);
    } else {
        jq("#selectPlanButton").prop("disabled", true);
    }
}

/*
 * Display the fragment to select a medication plan.
 */
function showMedicationPlanOrderWindow(){
    var dialogOpen = false;
    var objects = jq('.dialog');
    jq(objects).each(function(){
        if (jq(this).is(':visible')){
            dialogOpen = true;
        }
    });
    if(!dialogOpen){
        jq("#medPlanWindow").show(); 
        jq("#medPlanWindow").css("display", "block");
        jq("#activeOrderWindow").hide();
    }
}

/*
 * Hide the fragment to select a medication plan.
 */
function hideMedicationPlanOrderWindow(){
    jq("#medPlanWindow").hide();
    jq("#activeOrderWindow").show();
}

/*
 * Hide the fragment to order a medication plan.
 */
function hideMedicationPlansWindow(){
    jq("#activeOrderWindow").show();
    jq("#medPlanDetailsWindow").hide();
}

/*
 * Display the fragment to create a individual drug order.
 */
function showSingleOrderDetailsWindow(orderType){
    var dialogOpen = false;
    var objects = jq('.dialog');
    jq(objects).each(function(){
        if (jq(this).is(':visible')){
            dialogOpen = true;
        }
    });
    if(!dialogOpen){
        jq("#orderType").text(orderType);
        jq("#orderAction").val(orderType);
        jq("#confirmOrderWindow").hide();
        jq("#createOrderWindow").show();
        jq("#createOrderWindow").css("display", "block");
    }
}

/*
 * Hide the fragment to create a individual drug order.
 */
function hideIndividualOrderDetailsWindow(){
    if(!document.getElementById("draftPlanList"))
        jq("#activeOrderWindow").show();
    
    jq("#diagnosis").prop("readonly", false);
    jq("#createOrderWindow").hide();
    jq("#orderExists").hide();
    jq("#allergicReason").hide();
    jq("#orderReason").attr("required", false);
    
    jq("#orderId").val("");
    jq("#orderType").text("");
    jq("#orderAction").val("");
    jq("#drugName").val("");
    jq("#orderReason").val("");
    jq("#route").val("");
    jq("#dose").val("");
    jq("#doseUnits").val("");
    jq("#quantity").val("");
    jq("#quantityUnits").val("");
    jq("#duration").val("");
    jq("#durationUnits").val("");
    jq("#frequency").val("");
    jq("#refill").val("0");
    jq("#interval").val("0");
    jq("#diagnosis").val("");
    jq("#patientInstrn").val("");
    jq("#pharmacistInstrn").val("");
    jq("#drugName").prop("readonly", false);
    jq("#addOrderButton").prop("disabled", true);
    
    jq('#createOrderForm input, #createOrderForm select, #createOrderForm textarea').each(function(){
        this.style.borderColor = "";
    });

    clearHighlights();
}

/*
 * Highlight create drug order form fields if they are not filled.
 */
function checkFormFields(){
    if(jq("#orderReason").val() === "")
        document.getElementById("orderReason").style.borderColor = "orangered";
    else
        document.getElementById("orderReason").style.borderColor = "";
    
    if(jq("#route").val() === "")
        document.getElementById("route").style.borderColor = "orangered";
    else
        document.getElementById("route").style.borderColor = "";
    
    if(jq("#dose").val() === "")
        document.getElementById("dose").style.borderColor = "orangered";
    else
        document.getElementById("dose").style.borderColor = "";
    
    if(jq("#doseUnits").val() === "")
        document.getElementById("doseUnits").style.borderColor = "orangered";
    else
        document.getElementById("doseUnits").style.borderColor = "";
    
    if(jq("#quantity").val() === "")
        document.getElementById("quantity").style.borderColor = "orangered";
    else
        document.getElementById("quantity").style.borderColor = "";
    
    if(jq("#quantityUnits").val() === "")
        document.getElementById("quantityUnits").style.borderColor = "orangered";
    else
        document.getElementById("quantityUnits").style.borderColor = "";
    
    if(jq("#duration").val() === "")
        document.getElementById("duration").style.borderColor = "orangered";
    else
        document.getElementById("duration").style.borderColor = "";
    
    if(jq("#durationUnits").val() === "")
        document.getElementById("durationUnits").style.borderColor = "orangered";
    else
        document.getElementById("durationUnits").style.borderColor = "";
    
    if(jq("#frequency").val() === "")
        document.getElementById("frequency").style.borderColor = "orangered";
    else
        document.getElementById("frequency").style.borderColor = "";
    
    if(jq("#diagnosis").val() === "")
        document.getElementById("diagnosis").style.borderColor = "orangered";
    else
        document.getElementById("diagnosis").style.borderColor = "";
    
    if(jq("#patientInstrn").val() === "")
        document.getElementById("patientInstrn").style.borderColor = "orangered";
    else
        document.getElementById("patientInstrn").style.borderColor = "";
    
    if(jq("#pharmacistInstrn").val() === "")
        document.getElementById("pharmacistInstrn").style.borderColor = "orangered";
    else
        document.getElementById("pharmacistInstrn").style.borderColor = "";
}

/*
 * Highlight adminstrator definable med plan form fields if they are not filled.
 */
function checkAdminFields(){
    if(jq("#adminDrug").val() === "")
        document.getElementById("adminDrug").style.borderColor = "orangered";
    else
        document.getElementById("adminDrug").style.borderColor = "";
    
    if(jq("#adminPlan").val() === "")
        document.getElementById("adminPlan").style.borderColor = "orangered";
    else
        document.getElementById("adminPlan").style.borderColor = "";
    
    if(jq("#adminRoute").val() === "")
        document.getElementById("adminRoute").style.borderColor = "orangered";
    else
        document.getElementById("adminRoute").style.borderColor = "";
    
    if(jq("#adminDose").val() === "")
        document.getElementById("adminDose").style.borderColor = "orangered";
    else
        document.getElementById("adminDose").style.borderColor = "";
    
    if(jq("#adminDoseUnits").val() === "")
        document.getElementById("adminDoseUnits").style.borderColor = "orangered";
    else
        document.getElementById("adminDoseUnits").style.borderColor = "";
    
    if(jq("#adminQuantity").val() === "")
        document.getElementById("adminQuantity").style.borderColor = "orangered";
    else
        document.getElementById("adminQuantity").style.borderColor = "";
    
    if(jq("#adminQuantityUnits").val() === "")
        document.getElementById("adminQuantityUnits").style.borderColor = "orangered";
    else
        document.getElementById("adminQuantityUnits").style.borderColor = "";
    
    if(jq("#adminDuration").val() === "")
        document.getElementById("adminDuration").style.borderColor = "orangered";
    else
        document.getElementById("adminDuration").style.borderColor = "";
    
    if(jq("#adminDurationUnits").val() === "")
        document.getElementById("adminDurationUnits").style.borderColor = "orangered";
    else
        document.getElementById("adminDurationUnits").style.borderColor = "";
    
    if(jq("#adminFrequency").val() === "")
        document.getElementById("adminFrequency").style.borderColor = "orangered";
    else
        document.getElementById("adminFrequency").style.borderColor = "";
    
    // Only when all admin plan fields are specified, allow to create a medication plan.
 
    if(jq("#adminPlan").val() !== "" && jq("#adminDrug").val() !== "" && jq("#adminRoute").val() !== "" && jq("#adminDose").val() !== "" && jq("#adminDoseUnits").val() !== "" && jq("#adminQuantity").val() !== "" && jq("#adminQuantityUnits").val() !== "" && jq("#adminDuration").val() !== "" && jq("#adminDurationUnits").val() !== "" && jq("#adminFrequency").val() !== ""){
        
        jq("#planSaveButton").prop("disabled", false);
        
    } else {
        jq("#planSaveButton").prop("disabled", true);
    }
}

/*
 * Display a fragment that displays the details of the selected order.
 */
function showDrugOrderViewWindow(action, startdate, drugname, dose, doseUnits, route, duration, durationUnits, quantity, quantityUnits, frequency, numRefills, interval, orderReason, diagnosis, priority, patientInstrn, pharmacistInstrn, pharmaComments, orderStatus){
    var dialogOpen = false;
    var objects = jq('.dialog');
    jq(objects).each(function(){
        if (jq(this).is(':visible')){
            dialogOpen = true;
        }
    });
    if(!dialogOpen){
        if(orderStatus === "Active-Plan" || orderStatus === "Draft-Plan" || orderStatus === "Non-Active-Plan"){
            jq("#activeOrderWindow").hide();
        }
        
        jq("#activeOrderAction").text(action);
        jq("#order_diagnosis").text(diagnosis);
        jq("#start_date").text(startdate);        
        jq("#order_priority").text(priority);
        jq("#order_refills").text(numRefills);
        jq("#refill_interval").text(interval+" day(s)");
        var order_details = jq('<div>').text(drugname +"\nDose: "+dose+" "+doseUnits+"\nRoute: "+route+"\nQuantity: "+quantity+" "+quantityUnits).text();
        jq("#order_details").html(order_details.replace(/\n/g,'<br/>'));
        jq("#order_duration").text(duration+" "+durationUnits+", "+frequency);

        if(orderReason !== "null"){
            var order_reason = jq('<div>').text("<br/>"+orderReason).text();
            jq("#order_reason").html(order_reason.replace(/newline/g,"<br/>"));
            jq("#allergicOrderReasonView").show();
            jq("#allergicOrderReasonView").css("display", "block");
        }

        if(patientInstrn === "null")
            jq("#patient_instructions").text("-");
        else {
            var patient_instructions = jq('<div>').text("<br/>"+patientInstrn).text();
            jq("#patient_instructions").html(patient_instructions.replace(/newline/g,"<br/>"));
        }
        
        if(pharmacistInstrn === "null")
            jq("#pharmacist_instructions").text("-");
        else {
            var pharmacist_instructions = jq('<div>').text("<br/>"+pharmacistInstrn).text();
            jq("#pharmacist_instructions").html(pharmacist_instructions.replace(/newline/g,"<br/>"));
        }

        if(pharmaComments !== "" && pharmaComments !== null && pharmaComments !== "null" && pharmaComments !== undefined){
            var pharma_comments = jq('<div>').text("<br/>"+pharmaComments).text();
            jq("#pharma_comments").html(pharma_comments.replace(/newline/g,"<br/>"));
            jq("#pharmacistCommentsView").show();
            jq("#pharmacistCommentsView").css("display", "block");
        }

        jq("#viewOrderWindow").show();
        jq("#viewOrderWindow").css("display", "block");
    }
}

/*
 * Hide the fragment that displays the details of the selected order.
 */
function hideDrugOrderViewWindow(){
    if(!document.getElementById("draftPlanList"))
        jq("#activeOrderWindow").show();
    
    jq("#allergicOrderReasonView").hide();
    jq("#pharmacistCommentsView").hide();
    jq("#viewOrderWindow").hide();
    clearHighlights();
}

/*
 * Display the fragment to create a individual drug order with all the field values populated.
 */
function editSingleOrderDetailsWindow(orderType, orderId, name, startDate, dose, doseUnits, route, duration, durationUnits, quantity, quantityUnits, frequency, numRefills, interval, diagnosis, orderReason, priority, patientInstrn, pharmacistInstrn, orderStatus, currentList, allergyList){
    var dialogOpen = false;
    var objects = jq('.dialog');
    jq(objects).each(function(){
        if (jq(this).is(':visible')){
            dialogOpen = true;
        }
    });
    if(!dialogOpen){
        if(orderStatus === "Active-Plan" || orderStatus === "Draft-Plan"){
            jq("#activeOrderWindow").hide();
            jq("#diagnosis").prop("readonly", true);
        }
        checkExisting(name, currentList, allergyList, orderType);
        
        if(orderType === "RENEW DRUG ORDER"){
            document.getElementById("patientInstrn").style.borderColor = "orangered";
            document.getElementById("pharmacistInstrn").style.borderColor = "orangered";
        } 
        
        jq("#orderType").text(orderType);
        jq("#orderAction").val(orderType);
        jq("#orderId").val(orderId);
        jq("#drugName").val(name);
        jq("#route").val(route);
        jq("#dose").val(dose);
        jq("#doseUnits").val(doseUnits);
        jq("#quantity").val(quantity);
        jq("#quantityUnits").val(quantityUnits);
        jq("#duration").val(duration);
        jq("#durationUnits").val(durationUnits);
        jq("#frequency").val(frequency);
        jq("#refill").val(numRefills);
        jq("#interval").val(interval);
        jq("#priority").val(priority);
        jq("#diagnosis").val(diagnosis);
        
        if(orderReason !== "" && orderReason !== "null" && orderType === "EDIT DRUG ORDER"){
            jq("#allergicReason").show();
            jq("#orderReason").val(orderReason.replace(/newline/g,"\n"));
            jq("#orderReason").attr("required", true);
            jq("#orderReason").css("borderColor", "");
            jq("#allergicReason").css("display", "block");
        }
        
        if(patientInstrn === "null" || orderType === "RENEW DRUG ORDER")
            jq("#patientInstrn").val("");
        else
            jq("#patientInstrn").val(patientInstrn.replace(/newline/g,"\n"));
        
        if(pharmacistInstrn === "null" || orderType === "RENEW DRUG ORDER")
            jq("#pharmacistInstrn").val("");
        else
            jq("#pharmacistInstrn").val(pharmacistInstrn.replace(/newline/g,"\n"));
        
        jq("#drugName").prop("readonly", true);
        jq("#addOrderButton").prop("disabled", false);
        
        jq("#createOrderWindow").show();
        jq("#createOrderWindow").css("display", "block");
    }
}

/*
 * Discontinue individual order.
 */
function discardSingleOrder(order){
    jq("#selectedActiveOrder").val(order);
    jq("#activeGroupForm").submit();
}

/*
 * Discontinue individual order from a given plan.
 */
function discardSingleItem(order){
    jq("#selectedActiveItem").val(order);
    jq("#activePlanForm").submit();
}

/*
 * Discontinue medication plan.
 */
function discardMedPlanOrder(plan){
    jq("#selectedActivePlan").val(plan);
    jq("#activePlanForm").submit();
}

/*
 * Create Administrator define plan.
 */
function createStandardPlan(){
    jq("#createPlanForm").submit();
}

/*
 * Confirm discard selected Administrator define plan and associated plan items.
 */
function discardMedPlan(){
    jq("#discardPlanForm").submit();
}

/*
 * Renew non-active medication plans.
 */
function renewMedPlanWindow(plan){
    jq("#selectedNonActivePlan").val(plan);
    jq("#nonActivePlanForm").submit();
}

/*
 * Activate medication plans on draft
 */
function saveMedPlanOrder(planId){
    jq("#activatePlan").val(planId);
    jq("#activePlanForm").submit();
}

/*
 * Auto-complete plan name when typed and submit the selected plan.
 */
function autoCompletePlan(){
    jq("#planName").autocomplete({
        select : function(event, ui){
            jq("#planName").val((ui.item.label).trim());
            jq("#planForm").submit();
        }
    });
}

/*
 * When a drug is entered/selected to be ordered,
 * - Display a note if the drug exists in the active drug order list.
 * - Display a note if the Patient is allergic to the drug.
 */
function autoCompleteDrug(currentOrders, allergies){
    jq("#drugName").change(function(){
        
        var currentOrderList = currentOrders.split(",");
        var orderExists = false;
        jq.each(currentOrderList, function(index, value){
            var drugname = value.replace("[","").replace("]","").trim().toUpperCase();
            var selectedDrug = jq("#drugName").val().trim();
            if(selectedDrug === drugname){
                orderExists = true;
            }
        });
        if(orderExists){
            jq("#orderExists").show();
            jq("#orderExists").css("display", "block");
        } else {
            jq("#orderExists").hide();
            
            /*
             * Check if given drug is listed in the list of allergic drug orders.
             */
            var allergyList = allergies.split(",");
            var isAllergic = false;
            jq.each(allergyList,function(index,value){
                var drugname = value.replace("[","").replace("]","").trim().toUpperCase();
                var selectedDrug = jq("#drugName").val().trim();
                if(selectedDrug === drugname){
                    isAllergic = true;
                } 
            });
            if(isAllergic){
                jq("#allergicReason").show();
                jq("#allergicReason").css("display", "block");
                jq("#orderReason").attr("required", true);
            } else {
                jq("#allergicReason").hide();
                jq("#orderReason").val("");
                jq("#orderReason").attr("required", false);
            }
        }
    });
    validate();
}

/*
 * When a drug order is renewed,
 * - Display a note if the drug exists in the active drug order list.
 */
function checkExisting(drug, currentOrders, allergicDrugs, action){
    if(action === "RENEW DRUG ORDER"){
        var currentOrderList = currentOrders.split(",");
        var orderExists = false;
        jq.each(currentOrderList, function(index, value){
            var order = value.replace("[","").replace("]","").trim();
            if(drug === order){
                orderExists = true;
            }
        });
        if(orderExists){
            jq("#orderExists").show();
            jq("#orderExists").css("display", "block");
        } else {
            jq("#orderExists").hide();
            checkAllergy(drug, allergicDrugs);
        }
    } else {
        checkAllergy(drug, allergicDrugs);
    }
}

/*
 * When a drug order is renewed,
 * Display a note if the Patient is allergic to the drug.
 */
function checkAllergy(drug, allergies){
    var allergyList = allergies.split(",");
    var isAllergic = false;
    jq.each(allergyList, function(index,value){
        var drugname = value.replace("[","").replace("]","").trim();
        if(drug === drugname){
            isAllergic = true;
        } 
    });
    if(isAllergic){
        jq("#orderReason").attr("required", true);
        jq("#orderReason").css("borderColor", "orangered");
        jq("#allergicReason").show();
        jq("#allergicReason").css("display", "block");
    } else {
        jq("#allergicReason").hide();
        jq("#orderReason").attr("required", false);
    }
}

/*
 * Display a fragment to define a medication plan.
 */
function displayPlanCreationWindow(){
    var dialogOpen = false;
    var objects = jq('.dialog');
    jq(objects).each(function(){
        if (jq(this).is(':visible')){
            dialogOpen = true;
        }
    });
    if(!dialogOpen){
        jq("#defineAction").val("definePlan");
        jq("#adminPlanActionType").text("DEFINE MEDICATION PLAN");
        jq("#definePlanWindow").show();
        jq("#definePlanWindow").css("display", "block");        
    }
}

/*
 * Hide the fragment to define a medication plan.
 */
function hideMedPlanDefineWindow(){
    jq("#definePlanWindow").hide();
    jq("#definePlanName").val("");
    jq("#definePlanDesc").val("");
    jq("#definePlanId").val("");
    jq("#defineAction").val("");
    jq("#adminPlanActionType").text("");
    jq("#planDefineButton").prop("disabled", true);
    document.getElementById("definePlanDesc").style.borderColor = "";
}

/*
 * Display a fragment to add a drug to a medication plan.
 */
function addPlanItemWindow(planName, listOfDrugs){
    var dialogOpen = false;
    var objects = jq('.dialog');
    jq(objects).each(function(){
        if (jq(this).is(':visible')){
            dialogOpen = true;
        }
    });
    if(!dialogOpen){
        jq("#createPlanWindow").show();
        jq("#createPlanWindow").css("display", "block");
        
        jq("#adminActionType").text("ADD DRUG TO PLAN");
        jq("#adminPlan").prop("readonly", true);
        jq("#listOfDrugs").val(listOfDrugs);
        jq("#adminPlan").val(planName);
        checkAdminFields();
    }
}

/*
 * Hide the fragment to add a drug to a medication plan.
 */
function hideMedPlanCreateWindow(){
    jq("#createPlanWindow").hide();
    jq("#listOfDrugs").val("");
    jq("#planId").val("");    
    jq("#adminPlan").val("");
    jq("#adminDrug").val("");
    jq("#adminRoute").val("");
    jq("#adminDose").val("");
    jq("#adminDoseUnits").val("");
    jq("#adminQuantity").val("");
    jq("#adminQuantityUnits").val("");
    jq("#adminDuration").val("");
    jq("#adminDurationUnits").val("");
    jq("#adminFrequency").val("");
    jq("#adminDrug").prop("readonly", false);
    jq("#planSaveButton").prop("disabled", true);
        
    jq('#createPlanForm input, #createPlanForm select').each(function(){
        this.style.borderColor = "";
    });
    
    clearHighlights();
}

/*
 * Display a fragment to add a drug to a medication plan with all the fields populated from the selected plan.
 */
function editPlanItemDetails(planId, planName, drugName, dose, doseUnits, route, quantity, quantityUnits, duration, durationUnits, frequency){
    var dialogOpen = false;
    var objects = jq('.dialog');
    jq(objects).each(function(){
        if (jq(this).is(':visible')){
            dialogOpen = true;
        }
    });
    if(!dialogOpen){
        jq("#createPlanWindow").show();
        jq("#createPlanWindow").css("display", "block");
        
        jq("#adminActionType").text("EDIT PLAN");
        jq("#planId").val(planId);
        jq("#adminPlan").val(planName);
        jq("#adminDrug").val(drugName);
        jq("#adminDose").val(dose);
        jq("#adminDoseUnits").val(doseUnits);
        jq("#adminRoute").val(route);
        jq("#adminQuantity").val(quantity);
        jq("#adminQuantityUnits").val(quantityUnits);
        jq("#adminDuration").val(duration);
        jq("#adminDurationUnits").val(durationUnits);
        jq("#adminFrequency").val(frequency);
        
        jq("#adminPlan").prop("readonly", true);
        jq("#adminDrug").prop("readonly", true);
        jq("#planSaveButton").prop("disabled", false);
    }
}

/*
 * Delete Administrator specified medication plan.
 */
function deleteMedPlan(planID){
    jq("#selectedMedPlan").val(planID);
    jq("#adminPageForm").submit();    
}

/*
 * Rename Administrator specified medication plan.
 */
function editMedPlan(id, planName, planDesc){
    var dialogOpen = false;
    var objects = jq('.dialog');
    jq(objects).each(function(){
        if (jq(this).is(':visible')){
            dialogOpen = true;
        }
    });
    if(!dialogOpen){
        jq("#definePlanId").val(id);
        jq("#defineAction").val("editPlan");
        jq("#definePlanName").val(planName);
        jq("#definePlanDesc").val(planDesc.replace(/newline/g,"\n"));
        jq("#definePlanWindow").show();
        jq("#planDefineButton").prop("disabled", false); 
        jq("#definePlanWindow").css("display", "block");
        jq("#adminPlanActionType").text("EDIT MEDICATION PLAN");
    }
}

/*
 * Delete Administrator specified medication plan item.
 */
function deleteMedPlanItem(planID){
    jq("#selectedPlanItem").val(planID);
    jq("#adminPageForm").submit();  
}

/*
 * Allow Administrator to discard the selected med plan.
 */
function allowPlanDiscard(){
    var selected = false;
    jq('#discardPlanBlock .groupCheckBox').each(function() {
        if(this.checked) {
            selected = true; 
        } 
    });
    if((selected || jq("#planDiscard").val() === "discardPlan") && jq("#discardReason").val() !== ""){
        jq('#discardAdminPlan').removeAttr('disabled');
    } else {
        jq("#discardAdminPlan").prop("disabled", true);
    } 
}

/*
 * Hide Administrator specified medication plan discard window.
 */
function hideMedPlanDiscardWindow(){
    jq("#deletePlanWindow").hide();
    jq("#selectedPlanItem").val("");
    jq("#selectedMedPlan").val("");
    jq("#planToDiscard").val("");
    jq("#discardPlan").text("");
    clearHighlights();
}

// Show Group Order window used to discard/renew group of orders.
function showRenewGroupOrderWindow(orderID){
    jq("#selectedNonActiveGroup").val(orderID);
    jq("#nonActiveGroupForm").submit();
}

// Show Group Order window used to discard/renew group of orders.
function showDiscardGroupOrderWindow(orderID){
   jq("#selectedActiveGroup").val(orderID);
   jq("#activeGroupForm").submit();
}

// Hide Group Order window used to discard/renew group of orders.
function hideGroupOrderWindow(){
    if(!document.getElementById("draftPlanList"))
        jq("#activeOrderWindow").show();
    
    jq("#showGroupOrderWindow").hide();
    clearHighlights();
}

// Show window with a form to add an order to an existing order group.
function showAddOrderToGroupWindow(orderType,groupID){
    var dialogOpen = false;
    var objects = jq('.dialog');
    jq(objects).each(function(){
        if (jq(this).is(':visible')){
            dialogOpen = true;
        }
    });
    if(!dialogOpen){
        jq("#orderId").val(groupID);
        jq("#orderType").text(orderType);
        jq("#orderAction").val(orderType);
        jq("#confirmOrderWindow").hide();
        jq("#createOrderWindow").show();
        jq("#createOrderWindow").css("display", "block");
    }
}

// Prevent allowing an order to be discontinued unless a reason is specified.
function discontinueReason(){
    if(document.getElementById("codedDiscardReason").value === "Other"){
        jq("#discontinueReasonText").show();
        jq("#nonCodedDiscardReason").attr("required", true);
        jq("#discontinueReasonText").css("display", "block");
        document.getElementById("nonCodedDiscardReason").style.borderColor = "orangered";
    } else {
        jq("#discontinueReasonText").hide();
        jq("#nonCodedDiscardReason").attr("required", false);
    }
    
    if(document.getElementById("codedDiscardReason").value === ""){
        jq("#orderActionButton").prop("disabled", true);
    } else {
        checkSelection();
    }
}

// Remove selected order from the group.
function removeFromGroup(OrderId){
    jq("#removeFromGroup").val(OrderId);
    removeFromGroupDialog.show();
}

// Save draft medication plan orders.
function saveDraftOrders(){
    jq("#saveDraft").val("saveDraft");
    jq("#saveDraftPlanForm").submit();
}

// Check if drug selected to be added to the plan is already a part of the plan.
function checkListOfDrugs(){
    var listOfDrugs = jq("#listOfDrugs").val().split(" ");
    var givenDrug = jq("#adminDrug").val().toUpperCase();
    jq.each(listOfDrugs, function(index, value){
        if(givenDrug.length > 0){
            if(value === givenDrug){
                alert("Drug is already a part of the plan");
            }
        }
    });
    // Check if all other fields are filled.
    checkAdminFields();
}
