/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* global diagnosis, jq, emr, Event */

var removeFromGroupDialog = null;
var saveDraftOrderDialog = null;

$(document).ready( function() {
      
    highlight();
    checkSelection();
    
    $("#planSaveButton").prop("disabled", true);
    $("#addOrderButton").prop("disabled", true);
    $("#discardAdminPlan").prop("disabled", true);
    $("#planDefineButton").prop("disabled", true);
        
    /*
     * If drug or diagnosis is selected, check if other parameters are provided and enable form submission.
     */
    $('#drugName, #diagnosis').autocomplete({
        select: function () { 
            validate();
            checkFormFields();
        }
    });
    
    /*
     * If a plan is defined (Administrator page), enable the form submission to create a plan.
     */
    $('#definePlanName').autocomplete({
        select: function () { 
            $("#planDefineButton").prop("disabled", false); 
            
            if($("#definePlanDesc").val() === "")
                document.getElementById("definePlanDesc").style.borderColor = "orangered";
            else
                document.getElementById("definePlanDesc").style.borderColor = "";
        }
    });
    
    $('#definePlanName').change(function (){ 
        if($('#definePlanName').val() !== ""){
            $("#planDefineButton").prop("disabled", false); 
        } else {
            $("#planDefineButton").prop("disabled", true); 
            document.getElementById("definePlanDesc").style.borderColor = "";
        }
    });
    
    /*
     * Remove highlights from Med Plan description field when provided.
     */
    $('#definePlanDesc').change(function (){ 
        if($(this).val() === "")
            this.style.borderColor = "orangered";
        else
            this.style.borderColor = "";
    });
    
    /*
     * Remove highlights from Order discontinuation field when reason is provided.
     */
    $('#nonCodedDiscardReason').change(function (){ 
        if($(this).val() === "")
            this.style.borderColor = "orangered";
        else
            this.style.borderColor = "";
    });
    
    /*
     * If orders are selected to be discarded, show the select input widget to specify the reasons to discard.
     */
    if($('#groupAction').val() === "DISCARD ORDER GROUP" || $('#groupAction').val() === "DISCARD MED PLAN" || $('#groupAction').val() === "DISCONTINUE ORDER"){
        $("#orderActionButton").prop("disabled", true);
        jq("#discontinueReasonSelect").show();
        jq("#discontinueReasonSelect").css("display", "block");
    }
    
    /*
     * If the admin plan fields are modified, check the value of the remaining mandatory fields.
     * Highlight the unspecified fields. Only when all details are specified, allow to create a medication plan.
     */
    $("#adminPlan, #adminDrug, #adminRoute, #adminDose, #adminDoseUnits, #adminQuantity, #adminQuantityUnits, #adminDuration, #adminDurationUnits, #adminFrequency").change(function(){
        checkAdminFields();
    });
    
    /*
     * If the create drug order fields are modified, check the value of the remaining mandatory fields.
     * Highlight the unspecified fields. Only when all details are specified, allow to create a drug order.
     */
    $("#drugName, #route, #dose, #doseUnits, #quantity, #quantityUnits, #duration, #durationUnits, #frequency, #diagnosis, #orderReason, #patientInstrn, #pharmacistInstrn").change(function(){
        validate();
        if($("#drugName").val() !== ""){
            checkFormFields();
        }
    });
    
    /*
     * Enable confirm button to select a medication plan if one or more orders from the plan is selected.
     */
    $('.planDrugName .groupCheckBox').on('change', function() {
        var selected = false;
        $('.planDrugName .groupCheckBox').each(function() {
            if(this.checked) {
                selected = true; 
                $(this).parent().next('.drugDetails').first().find('.planOrderReason').prop("readonly", false);                
            } else {
                $(this).parent().next('.drugDetails').first().find('.planOrderReason').val("");
                $(this).parent().next('.drugDetails').first().find('.planOrderReason').prop("readonly", true);                
            }
        });
        if(selected){
            $('#selectPlanButton').removeAttr('disabled');
        } else {
            $("#selectPlanButton").prop("disabled", true);
        }        
    });
    
    /*
     * Enable confirm button to discard a medication plan if one or more orders from the plan is selected.
     */
    $('#discardPlanBlock .groupCheckBox').on('change', function() {
        allowPlanDiscard();       
    });
    
    /*
     * Check if a reason to discard the medication plan is provided.
     */
    $('#discardReason').change(function(){
        allowPlanDiscard(); 
    });
    
    /*
     * Remove the necessity to provide a reason to renew a drug order if it is not selected to be renewed.
     */
    $('.groupDrugName .groupCheckBox').on('change', function() {
        var selected = false;
        $('.groupDrugName .groupCheckBox').each(function() {
            if(this.checked) {
                selected = true;
                $(this).parent().next('.drugDetails').first().find('.reviseOrderReason').prop("readonly", false);                
            } else {
                $(this).parent().next('.drugDetails').first().find('.reviseOrderReason').val("");
                $(this).parent().next('.drugDetails').first().find('.reviseOrderReason').prop("readonly", true);
            }
        });   
        if(selected && ($("#groupOrderAction").text() === "RENEW MED PLAN" || $("#groupOrderAction").text() === "RENEW ORDER GROUP")){
            $('#orderActionButton').removeAttr('disabled');
        }
        if(selected && ($("#groupOrderAction").text() === "DISCARD MED PLAN" || $("#groupOrderAction").text() === "DISCARD ORDER GROUP" || $("#groupOrderAction").text() === "DISCONTINUE ORDER")){
            discontinueReason();
        }
        if(!selected){
            $("#orderActionButton").prop("disabled", true);
        }
    });
    
    document.getElementsByClassName("unchecked").checked = false;
    
    /*
     * Highlight the fields to enter the reasons to order a drug unless they are filled.
     */
    $('#discardReason, .planOrderReason, .reviseOrderReason').each(function(){
        this.style.borderColor = "orangered";
    });
    
    $('#discardReason, .planOrderReason, .reviseOrderReason').each(function(){
        $(this).on('change', function(){
            if($(this).val() === "")
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
    $(document).mouseup(function (e){
        if(document.getElementById("draftPlanList")){
            if (!$("#draftPlanRow").is(e.target) && $("#draftPlanRow").has(e.target).length === 0 && !$("#createOrderWindow").is(e.target) && $("#createOrderWindow").has(e.target).length === 0 && !$("#showGroupOrderWindow").is(e.target) && $("#showGroupOrderWindow").has(e.target).length === 0 && e.target.nodeName !== "TD" && e.target.nodeName !== "TH" && e.target.nodeName !== "I" && e.target.nodeName !== "A" && e.target.nodeName !== "INPUT" && e.target.toString() !== "[object HTMLSpanElement]"){
                saveDraftOrderDialog.show();
            }
        }
    });
    
    $("#pageRedirect").mouseover(function(e){
        if(document.getElementById("draftPlanList")){
            $("#pageRedirect").off('click');
        } else {
            $(this).hide();
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
            	$("#removeFromGroupForm").submit();
            }
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
    var selectedPlan = $("#discardPlan").text();
    if(selectedPlan !== ""){
        var $rowsNo = $('#medPlansTable tbody tr .planDetails').filter(function () {
            if($.trim($(this).find('.fields').find('.planName').text()) === selectedPlan.toUpperCase()){
                $(this).find('.fields').find('.icon-plus-sign').hide();
                $(this).find('.fields').find('.icon-minus-sign').show();
                $(this).find('.plansDetailsView').show();
                
                var selectedDrug = $(".discardDrug");
                if(selectedDrug.size() === 1){
                    $(this).find('.plansDetailsView').find('.planBlock').find('.planBlockDetails').find('.planItem').each(function(){
                        if($(this).text() === selectedDrug.text().toUpperCase()){
                            $(this).parent().parent().css({"background": "#75b2f0","color": "white"});
                            $(this).parent().parent().find('.show-details').hide();
                            $(this).parent().parent().find('.hide-details').show();
                            $(this).parent().parent().find('.groupItem').show();
                        }
                    });
                } else {
                    $(this).find('.plansDetailsView').find('.planBlock').each(function(){
                        $(this).find('.planBlockDetails').find('.groupItem').show();
                        $(this).find('.planBlockDetails').find('.hide-details').show();
                        $(this).find('.planBlockDetails').find('.show-details').hide();
                        $(this).find('.planBlockDetails').css({"background": "#75b2f0","color": "white"});
                    });
                }
            }
        });
    }
    
    var planActioned = $("#recordedMedPlan").val();
    
    if(planActioned !== null || planActioned !== undefined){
        var $rowsNo = $('#medPlansTable tbody tr .planDetails').filter(function () {
            if($.trim($(this).find('.fields').find('.planName').text()) === planActioned.toUpperCase()){
                $(this).find('.fields').find('.icon-plus-sign').hide();
                $(this).find('.fields').find('.icon-minus-sign').show();
                $(this).find('.plansDetailsView').show();
            }
        });
    }
    
    var selectedAction = $("#groupOrderAction").text();
    if(selectedAction !== ""){
        var selectedOrder = $("#groupOrderID").val();
        
        if(selectedAction === "DISCONTINUE ORDER"){
            
            if($("#orderStatus").val() === "Active-Plan" || $("#orderStatus").val() === "Draft-Plan"){
                jq("#activeOrderWindow").hide();
            }
            
            var $rowsNo1 = $('#activeOrdersTable tbody .orderRow').filter(function () {
                if($.trim($(this).children('td').slice(1, 2).text()) === selectedOrder){
                    $(this).children('td').slice(1, 4).css({"background": "#75b2f0","color": "white"});
                }
            });
            
            var $rowsNo2 = $('#activeOrdersTable tbody .groupRow').filter(function () {
                $(this).children('td').slice(1, 2).find('.groupDrug').each(function(){
                    if($.trim($(this).find('.groupDrugDetails').find('#groupDrugID').text()) === selectedOrder){
                        $(this).find('.groupDrugDetails').css({"background": "#75b2f0","color": "white"});
                    }
                });
            });
            
            var $rowsNo3 = $('#activePlansTable tbody tr').filter(function () {
                $(this).children('td').slice(0, 1).find('.plansDetailsView').find('.planDrug').each(function(){
                    if($.trim($(this).find('.planDrugDetails').find('#planDrugId').text()) === selectedOrder){
                        $(this).parent().show();
                        $(this).parent().parent().find('.fields').find('.icon-plus-sign').hide();
                        $(this).parent().parent().find('.fields').find('.icon-minus-sign').show();
                        $(this).find('.planDrugDetails').css({"background": "#75b2f0","color": "white"});
                    }
                });
            });
        }
        
        else if(selectedAction === "DISCARD ORDER GROUP"){
            var $rowsNo = $('#activeOrdersTable tbody .groupRow').filter(function () {
                if($.trim($(this).children('td').slice(0, 1).find('#id').text()) === selectedOrder){
                    $(this).children('td').slice(1, 2).find('.groupDrug').each(function(){
                        $(this).find('.groupDrugDetails').css({"background": "#75b2f0","color": "white"});
                    });
                }
            });
        }
        
        else if(selectedAction === "DISCARD MED PLAN"){
            jq("#activeOrderWindow").hide();
            var $rowsNo = $('#activePlansTable tbody tr').filter(function () {
                if($.trim($(this).children('td').slice(0, 1).find('#id').text()) === selectedOrder){
                    $(this).children('td').slice(0, 1).find('.fields').find('.icon-plus-sign').hide();
                    $(this).children('td').slice(0, 1).find('.fields').find('.icon-minus-sign').show();
                    $(this).children('td').slice(0, 1).find('.plansDetailsView').find('.planDrug').each(function(){
                        $(this).find('.planDrugDetails').css({"background": "#75b2f0","color": "white"});
                    });  
                    $(this).find('.plansDetailsView').show();
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
        $(this).find('.planBlockDetails').css({'background-color':'','color':''});
    });
}

/*
 * If the create drug order fields are modified, check the value of the remaining mandatory fields.
 * Only when all details are specified, allow to create a drug order.
 */
function validate(){
    if($("#drugName").val() !== "" && $("#route").val() !== "" && $("#dose").val() !== "" && $("#doseUnits").val() !== "" && $("#quantity").val() !== "" && $("#quantityUnits").val() !== "" && $("#duration").val() !== "" && $("#durationUnits").val() !== "" && $("#frequency").val() !== "" && $("#diagnosis").val() !== ""){
        $("#addOrderButton").prop("disabled", false);
    } else {
        $("#addOrderButton").prop("disabled", true);
    }
}

/*
 * Enable group button action when one or more orders from the selected list are checked to be ordered.
 */
function checkSelection(){
    var ordersSelected = false;
    $('.groupDrugName .groupCheckBox').each(function() {
        if(this.checked) {
            ordersSelected = true;
        }
    });
    if(ordersSelected){
        $("#orderActionButton").prop("disabled", false);
    } else {
        $("#orderActionButton").prop("disabled", true);
    }
    
    var plansSelected = false;
    $('.planDrugName .groupCheckBox').each(function() {
        if(this.checked) {
            plansSelected = true;
        }
    });
    if(plansSelected){
        $("#selectPlanButton").prop("disabled", false);
    } else {
        $("#selectPlanButton").prop("disabled", true);
    }
}

/*
 * Display the fragment to select a medication plan.
 */
function showMedicationPlanOrderWindow(){
    var dialogOpen = false;
    var objects = $('.dialog');
    $(objects).each(function(){
        if ($(this).is(':visible')){
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
    var objects = $('.dialog');
    $(objects).each(function(){
        if ($(this).is(':visible')){
            dialogOpen = true;
        }
    });
    if(!dialogOpen){
        $("#orderType").text(orderType);
        $("#orderAction").val(orderType);
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
    
    $("#diagnosis").prop("readonly", false);
    jq("#createOrderWindow").hide();
    jq("#orderExists").hide();
    jq("#allergicReason").hide();
    $("#orderReason").attr("required", false);
    
    $("#orderId").val("");
    $("#orderType").text("");
    $("#orderAction").val("");
    $("#drugName").val("");
    $("#orderReason").val("");
    $("#route").val("");
    $("#dose").val("");
    $("#doseUnits").val("");
    $("#quantity").val("");
    $("#quantityUnits").val("");
    $("#duration").val("");
    $("#durationUnits").val("");
    $("#frequency").val("");
    $("#refill").val("0");
    $("#interval").val("0");
    $("#diagnosis").val("");
    $("#patientInstrn").val("");
    $("#pharmacistInstrn").val("");
    $("#drugName").prop("readonly", false);
    $("#addOrderButton").prop("disabled", true);
    
    $('#createOrderForm input, #createOrderForm select, #createOrderForm textarea').each(function(){
        this.style.borderColor = "";
    });

    clearHighlights();
}

/*
 * Highlight create drug order form fields if they are not filled.
 */
function checkFormFields(){
    if($("#orderReason").val() === "")
        document.getElementById("orderReason").style.borderColor = "orangered";
    else
        document.getElementById("orderReason").style.borderColor = "";
    
    if($("#route").val() === "")
        document.getElementById("route").style.borderColor = "orangered";
    else
        document.getElementById("route").style.borderColor = "";
    
    if($("#dose").val() === "")
        document.getElementById("dose").style.borderColor = "orangered";
    else
        document.getElementById("dose").style.borderColor = "";
    
    if($("#doseUnits").val() === "")
        document.getElementById("doseUnits").style.borderColor = "orangered";
    else
        document.getElementById("doseUnits").style.borderColor = "";
    
    if($("#quantity").val() === "")
        document.getElementById("quantity").style.borderColor = "orangered";
    else
        document.getElementById("quantity").style.borderColor = "";
    
    if($("#quantityUnits").val() === "")
        document.getElementById("quantityUnits").style.borderColor = "orangered";
    else
        document.getElementById("quantityUnits").style.borderColor = "";
    
    if($("#duration").val() === "")
        document.getElementById("duration").style.borderColor = "orangered";
    else
        document.getElementById("duration").style.borderColor = "";
    
    if($("#durationUnits").val() === "")
        document.getElementById("durationUnits").style.borderColor = "orangered";
    else
        document.getElementById("durationUnits").style.borderColor = "";
    
    if($("#frequency").val() === "")
        document.getElementById("frequency").style.borderColor = "orangered";
    else
        document.getElementById("frequency").style.borderColor = "";
    
    if($("#diagnosis").val() === "")
        document.getElementById("diagnosis").style.borderColor = "orangered";
    else
        document.getElementById("diagnosis").style.borderColor = "";
    
    if($("#patientInstrn").val() === "")
        document.getElementById("patientInstrn").style.borderColor = "orangered";
    else
        document.getElementById("patientInstrn").style.borderColor = "";
    
    if($("#pharmacistInstrn").val() === "")
        document.getElementById("pharmacistInstrn").style.borderColor = "orangered";
    else
        document.getElementById("pharmacistInstrn").style.borderColor = "";
}

/*
 * Highlight adminstrator definable med plan form fields if they are not filled.
 */
function checkAdminFields(){
    if($("#adminDrug").val() === "")
        document.getElementById("adminDrug").style.borderColor = "orangered";
    else
        document.getElementById("adminDrug").style.borderColor = "";
    
    if($("#adminPlan").val() === "")
        document.getElementById("adminPlan").style.borderColor = "orangered";
    else
        document.getElementById("adminPlan").style.borderColor = "";
    
    if($("#adminRoute").val() === "")
        document.getElementById("adminRoute").style.borderColor = "orangered";
    else
        document.getElementById("adminRoute").style.borderColor = "";
    
    if($("#adminDose").val() === "")
        document.getElementById("adminDose").style.borderColor = "orangered";
    else
        document.getElementById("adminDose").style.borderColor = "";
    
    if($("#adminDoseUnits").val() === "")
        document.getElementById("adminDoseUnits").style.borderColor = "orangered";
    else
        document.getElementById("adminDoseUnits").style.borderColor = "";
    
    if($("#adminQuantity").val() === "")
        document.getElementById("adminQuantity").style.borderColor = "orangered";
    else
        document.getElementById("adminQuantity").style.borderColor = "";
    
    if($("#adminQuantityUnits").val() === "")
        document.getElementById("adminQuantityUnits").style.borderColor = "orangered";
    else
        document.getElementById("adminQuantityUnits").style.borderColor = "";
    
    if($("#adminDuration").val() === "")
        document.getElementById("adminDuration").style.borderColor = "orangered";
    else
        document.getElementById("adminDuration").style.borderColor = "";
    
    if($("#adminDurationUnits").val() === "")
        document.getElementById("adminDurationUnits").style.borderColor = "orangered";
    else
        document.getElementById("adminDurationUnits").style.borderColor = "";
    
    if($("#adminFrequency").val() === "")
        document.getElementById("adminFrequency").style.borderColor = "orangered";
    else
        document.getElementById("adminFrequency").style.borderColor = "";
    
    // Only when all admin plan fields are specified, allow to create a medication plan.
 
    if($("#adminPlan").val() !== "" && $("#adminDrug").val() !== "" && $("#adminRoute").val() !== "" && $("#adminDose").val() !== "" && $("#adminDoseUnits").val() !== "" && $("#adminQuantity").val() !== "" && $("#adminQuantityUnits").val() !== "" && $("#adminDuration").val() !== "" && $("#adminDurationUnits").val() !== "" && $("#adminFrequency").val() !== ""){
        
        $("#planSaveButton").prop("disabled", false);
        
    } else {
        $("#planSaveButton").prop("disabled", true);
    }
}

/*
 * Display a fragment that displays the details of the selected order.
 */
function showDrugOrderViewWindow(action, startdate, drugname, dose, doseUnits, route, duration, durationUnits, quantity, quantityUnits, frequency, numRefills, interval, orderReason, diagnosis, priority, patientInstrn, pharmacistInstrn, pharmaComments, orderStatus){
    var dialogOpen = false;
    var objects = $('.dialog');
    $(objects).each(function(){
        if ($(this).is(':visible')){
            dialogOpen = true;
        }
    });
    if(!dialogOpen){
        if(orderStatus === "Active-Plan" || orderStatus === "Draft-Plan" || orderStatus === "Non-Active-Plan"){
            jq("#activeOrderWindow").hide();
        }
        
        $("#activeOrderAction").text(action);
        $("#order_diagnosis").text(diagnosis);
        $("#start_date").text(startdate);        
        $("#order_priority").text(priority);
        $("#order_refills").text(numRefills);
        $("#refill_interval").text(interval+" day(s)");
        var order_details = $('<div>').text(drugname +"\nDose: "+dose+" "+doseUnits+"\nRoute: "+route+"\nQuantity: "+quantity+" "+quantityUnits).text();
        $("#order_details").html(order_details.replace(/\n/g,'<br/>'));
        $("#order_duration").text(duration+" "+durationUnits+", "+frequency);

        if(orderReason !== "null"){
            $("#order_reason").text(orderReason.replace(/newline/g,"\n"));
            jq("#allergicOrderReasonView").show();
            jq("#allergicOrderReasonView").css("display", "block");
        }

        if(patientInstrn === "null")
            $("#patient_instructions").text("-");
        else
            $("#patient_instructions").text(patientInstrn.replace(/newline/g,"\n"));
        
        if(pharmacistInstrn === "null")
            $("#pharmacist_instructions").text("-");
        else
            $("#pharmacist_instructions").text(pharmacistInstrn.replace(/newline/g,"\n"));

        if(pharmaComments !== "" && pharmaComments !== null && pharmaComments !== "null" && pharmaComments !== undefined){
            $("#pharma_comments").text(pharmaComments.replace(/newline/g,"\n"));
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
    var objects = $('.dialog');
    $(objects).each(function(){
        if ($(this).is(':visible')){
            dialogOpen = true;
        }
    });
    if(!dialogOpen){
        if(orderStatus === "Active-Plan" || orderStatus === "Draft-Plan"){
            jq("#activeOrderWindow").hide();
            $("#diagnosis").prop("readonly", true);
        }
        checkExisting(name, currentList, allergyList, orderType);
        
        if(orderType === "RENEW DRUG ORDER"){
            document.getElementById("patientInstrn").style.borderColor = "orangered";
            document.getElementById("pharmacistInstrn").style.borderColor = "orangered";
        } 
        
        $("#orderType").text(orderType);
        $("#orderAction").val(orderType);
        $("#orderId").val(orderId);
        $("#drugName").val(name);
        $("#route").val(route);
        $("#dose").val(dose);
        $("#doseUnits").val(doseUnits);
        $("#quantity").val(quantity);
        $("#quantityUnits").val(quantityUnits);
        $("#duration").val(duration);
        $("#durationUnits").val(durationUnits);
        $("#frequency").val(frequency);
        $("#refill").val(numRefills);
        $("#interval").val(interval);
        $("#priority").val(priority);
        $("#diagnosis").val(diagnosis);
        
        if(orderReason !== "" && orderReason !== "null" && orderType === "EDIT DRUG ORDER"){
            jq("#allergicReason").show();
            jq("#orderReason").val(orderReason.replace(/newline/g,"\n"));
            jq("#orderReason").attr("required", true);
            jq("#orderReason").css("borderColor", "");
            jq("#allergicReason").css("display", "block");
        }
        
        if(patientInstrn === "null" || orderType === "RENEW DRUG ORDER")
            $("#patientInstrn").val("");
        else
            $("#patientInstrn").val(patientInstrn.replace(/newline/g,"\n"));
        
        if(pharmacistInstrn === "null" || orderType === "RENEW DRUG ORDER")
            $("#pharmacistInstrn").val("");
        else
            $("#pharmacistInstrn").val(pharmacistInstrn.replace(/newline/g,"\n"));
        
        $("#drugName").prop("readonly", true);
        $("#addOrderButton").prop("disabled", false);
        
        jq("#createOrderWindow").show();
        jq("#createOrderWindow").css("display", "block");
    }
}

/*
 * Discontinue individual order.
 */
function discardSingleOrder(order){
    $("#selectedActiveOrder").val(order);
    $("#activeGroupForm").submit();
}

/*
 * Discontinue individual order from a given plan.
 */
function discardSingleItem(order){
    $("#selectedActiveItem").val(order);
    $("#activePlanForm").submit();
}

/*
 * Discontinue medication plan.
 */
function discardMedPlanOrder(plan){
    $("#selectedActivePlan").val(plan);
    $("#activePlanForm").submit();
}

/*
 * Create Administrator define plan.
 */
function createStandardPlan(){
    $("#createPlanForm").submit();
}

/*
 * Confirm discard selected Administrator define plan and associated plan items.
 */
function discardMedPlan(){
    $("#discardPlanForm").submit();
}

/*
 * Renew non-active medication plans.
 */
function renewMedPlanWindow(plan){
    $("#selectedNonActivePlan").val(plan);
    $("#nonActivePlanForm").submit();
}

/*
 * Activate medication plans on draft
 */
function saveMedPlanOrder(planId){
    $("#activatePlan").val(planId);
    $("#activePlanForm").submit();
}

/*
 * Auto-complete plan name when typed and submit the selected plan.
 */
function autoCompletePlan(){
    $("#planName").autocomplete({
        select : function(event, ui){
            $("#planName").val((ui.item.label).trim());
            $("#planForm").submit();
        }
    });
}

/*
 * When a drug is entered/selected to be ordered,
 * - Display a note if the drug exists in the active drug order list.
 * - Display a note if the Patient is allergic to the drug.
 */
function autoCompleteDrug(currentOrders, allergies){
    $("#drugName").change(function(){
        
        var currentOrderList = currentOrders.split(",");
        var orderExists = false;
        $.each(currentOrderList, function(index, value){
            var drugname = value.replace("[","").replace("]","").trim().toUpperCase();
            var selectedDrug = $("#drugName").val().trim();
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
            $.each(allergyList,function(index,value){
                var drugname = value.replace("[","").replace("]","").trim().toUpperCase();
                var selectedDrug = $("#drugName").val().trim();
                if(selectedDrug === drugname){
                    isAllergic = true;
                } 
            });
            if(isAllergic){
                jq("#allergicReason").show();
                jq("#allergicReason").css("display", "block");
                $("#orderReason").attr("required", true);
            } else {
                jq("#allergicReason").hide();
                $("#orderReason").val("");
                $("#orderReason").attr("required", false);
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
        $.each(currentOrderList, function(index, value){
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
    $.each(allergyList, function(index,value){
        var drugname = value.replace("[","").replace("]","").trim();
        if(drug === drugname){
            isAllergic = true;
        } 
    });
    if(isAllergic){
        $("#orderReason").attr("required", true);
        jq("#orderReason").css("borderColor", "orangered");
        jq("#allergicReason").show();
        jq("#allergicReason").css("display", "block");
    } else {
        jq("#allergicReason").hide();
        $("#orderReason").attr("required", false);
    }
}

/*
 * Display a fragment to define a medication plan.
 */
function displayPlanCreationWindow(){
    var dialogOpen = false;
    var objects = $('.dialog');
    $(objects).each(function(){
        if ($(this).is(':visible')){
            dialogOpen = true;
        }
    });
    if(!dialogOpen){
        $("#defineAction").val("definePlan");
        $("#adminPlanActionType").text("DEFINE MEDICATION PLAN");
        jq("#definePlanWindow").show();
        jq("#definePlanWindow").css("display", "block");        
    }
}

/*
 * Hide the fragment to define a medication plan.
 */
function hideMedPlanDefineWindow(){
    jq("#definePlanWindow").hide();
    $("#definePlanName").val("");
    $("#definePlanDesc").val("");
    $("#definePlanId").val("");
    $("#defineAction").val("");
    $("#adminPlanActionType").text("");
    $("#planDefineButton").prop("disabled", true);
    document.getElementById("definePlanDesc").style.borderColor = "";
}

/*
 * Display a fragment to add a drug to a medication plan.
 */
function addPlanItemWindow(planName, listOfDrugs){
    var dialogOpen = false;
    var objects = $('.dialog');
    $(objects).each(function(){
        if ($(this).is(':visible')){
            dialogOpen = true;
        }
    });
    if(!dialogOpen){
        jq("#createPlanWindow").show();
        jq("#createPlanWindow").css("display", "block");
        
        $("#adminActionType").text("ADD DRUG TO PLAN");
        $("#adminPlan").prop("readonly", true);
        $("#listOfDrugs").val(listOfDrugs);
        $("#adminPlan").val(planName);
        checkAdminFields();
    }
}

/*
 * Hide the fragment to add a drug to a medication plan.
 */
function hideMedPlanCreateWindow(){
    jq("#createPlanWindow").hide();
    $("#listOfDrugs").val("");
    $("#planId").val("");    
    $("#adminPlan").val("");
    $("#adminDrug").val("");
    $("#adminRoute").val("");
    $("#adminDose").val("");
    $("#adminDoseUnits").val("");
    $("#adminQuantity").val("");
    $("#adminQuantityUnits").val("");
    $("#adminDuration").val("");
    $("#adminDurationUnits").val("");
    $("#adminFrequency").val("");
    $("#adminDrug").prop("readonly", false);
    $("#planSaveButton").prop("disabled", true);
        
    $('#createPlanForm input, #createPlanForm select').each(function(){
        this.style.borderColor = "";
    });
    
    clearHighlights();
}

/*
 * Display a fragment to add a drug to a medication plan with all the fields populated from the selected plan.
 */
function editPlanItemDetails(planId, planName, drugName, dose, doseUnits, route, quantity, quantityUnits, duration, durationUnits, frequency){
    var dialogOpen = false;
    var objects = $('.dialog');
    $(objects).each(function(){
        if ($(this).is(':visible')){
            dialogOpen = true;
        }
    });
    if(!dialogOpen){
        jq("#createPlanWindow").show();
        jq("#createPlanWindow").css("display", "block");
        
        $("#adminActionType").text("EDIT PLAN");
        $("#planId").val(planId);
        $("#adminPlan").val(planName);
        $("#adminDrug").val(drugName);
        $("#adminDose").val(dose);
        $("#adminDoseUnits").val(doseUnits);
        $("#adminRoute").val(route);
        $("#adminQuantity").val(quantity);
        $("#adminQuantityUnits").val(quantityUnits);
        $("#adminDuration").val(duration);
        $("#adminDurationUnits").val(durationUnits);
        $("#adminFrequency").val(frequency);
        
        $("#adminPlan").prop("readonly", true);
        $("#adminDrug").prop("readonly", true);
        $("#planSaveButton").prop("disabled", false);
    }
}

/*
 * Delete Administrator specified medication plan.
 */
function deleteMedPlan(planID){
    $("#selectedMedPlan").val(planID);
    $("#adminPageForm").submit();    
}

/*
 * Rename Administrator specified medication plan.
 */
function editMedPlan(id, planName, planDesc){
    var dialogOpen = false;
    var objects = $('.dialog');
    $(objects).each(function(){
        if ($(this).is(':visible')){
            dialogOpen = true;
        }
    });
    if(!dialogOpen){
        $("#definePlanId").val(id);
        $("#defineAction").val("editPlan");
        $("#definePlanName").val(planName);
        $("#definePlanDesc").val(planDesc.replace(/newline/g,"\n"));
        jq("#definePlanWindow").show();
        $("#planDefineButton").prop("disabled", false); 
        jq("#definePlanWindow").css("display", "block");
        $("#adminPlanActionType").text("EDIT MEDICATION PLAN");
    }
}

/*
 * Delete Administrator specified medication plan item.
 */
function deleteMedPlanItem(planID){
    $("#selectedPlanItem").val(planID);
    $("#adminPageForm").submit();  
}

/*
 * Allow Administrator to discard the selected med plan.
 */
function allowPlanDiscard(){
    var selected = false;
    $('#discardPlanBlock .groupCheckBox').each(function() {
        if(this.checked) {
            selected = true; 
        } 
    });
    if((selected || $("#planDiscard").val() === "discardPlan") && $("#discardReason").val() !== ""){
        $('#discardAdminPlan').removeAttr('disabled');
    } else {
        $("#discardAdminPlan").prop("disabled", true);
    } 
}

/*
 * Hide Administrator specified medication plan discard window.
 */
function hideMedPlanDiscardWindow(){
    jq("#deletePlanWindow").hide();
    $("#selectedPlanItem").val("");
    $("#selectedMedPlan").val("");
    $("#planToDiscard").val("");
    $("#discardPlan").text("");
    clearHighlights();
}

// Show Group Order window used to discard/renew group of orders.
function showRenewGroupOrderWindow(orderID){
    $("#selectedNonActiveGroup").val(orderID);
    $("#nonActiveGroupForm").submit();
}

// Show Group Order window used to discard/renew group of orders.
function showDiscardGroupOrderWindow(orderID){
   $("#selectedActiveGroup").val(orderID);
   $("#activeGroupForm").submit();
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
    var objects = $('.dialog');
    $(objects).each(function(){
        if ($(this).is(':visible')){
            dialogOpen = true;
        }
    });
    if(!dialogOpen){
        $("#orderId").val(groupID);
        $("#orderType").text(orderType);
        $("#orderAction").val(orderType);
        jq("#confirmOrderWindow").hide();
        jq("#createOrderWindow").show();
        jq("#createOrderWindow").css("display", "block");
    }
}

// Prevent allowing an order to be discontinued unless a reason is specified.
function discontinueReason(){
    if(document.getElementById("codedDiscardReason").value === "Other"){
        jq("#discontinueReasonText").show();
        $("#nonCodedDiscardReason").attr("required", true);
        jq("#discontinueReasonText").css("display", "block");
        document.getElementById("nonCodedDiscardReason").style.borderColor = "orangered";
    } else {
        jq("#discontinueReasonText").hide();
        $("#nonCodedDiscardReason").attr("required", false);
    }
    
    if(document.getElementById("codedDiscardReason").value === ""){
        $("#orderActionButton").prop("disabled", true);
    } else {
        checkSelection();
    }
}

// Remove selected order from the group.
function removeFromGroup(OrderId){
    $("#removeFromGroup").val(OrderId);
    removeFromGroupDialog.show();
}

// Save draft medication plan orders.
function saveDraftOrders(){
    $("#saveDraft").val("saveDraft");
    $("#saveDraftPlanForm").submit();
}

function checkListOfDrugs(){
    var listOfDrugs = jq("#listOfDrugs").val().split(" ");
    var givenDrug = $("#adminDrug").val().toUpperCase();
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