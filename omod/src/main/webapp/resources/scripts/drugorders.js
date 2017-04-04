/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* global diagnosis, jq, emr */

var removeFromGroupDialog = null;

$(document).ready( function() {
    
    $(document).mouseup(function (e){
        var objects = $('.dialog');
        if(e.target.nodeName !== "A" && e.target.nodeName !== "TD"){
            $(objects).each(function(){
                if (!$(this).is(e.target) && $(this).has(e.target).length === 0){
                    $(this).hide();
                    clearHighlights();

                    var ID = $(this).attr("id");

                    if(ID === "showOrderWindow")
                        hideDrugOrderViewWindow();
                    else if(ID === "definePlanWindow")
                        hideMedPlanDefineWindow();
                    else if(ID === "createPlanWindow")
                        hideMedPlanCreateWindow();
                    else if(ID === "deletePlanWindow")
                        hideMedPlanDeleteWindow();
                    else if(ID === "createOrderWindow")
                        hideIndividualOrderDetailsWindow();
                    else if(ID === "showGroupOrderWindow")
                        hideGroupOrderWindow();
                }
            }); 
        }  
    });

    highlight();
    $("#planSaveButton").prop("disabled", true);
    $("#addOrderButton").prop("disabled", true);
    $("#planDefineButton").prop("disabled", true);
    
    $('#definePlanName').autocomplete({
        select: function () { 
            $("#planDefineButton").prop("disabled", false); 
        }
    });
    
    $('#definePlanName').change(function (){ 
        if($('#definePlanName').val() !== ""){
            $("#planDefineButton").prop("disabled", false); 
        } else {
            $("#planDefineButton").prop("disabled", true); 
        }
    });
    
    if($('#groupAction').val() === "DISCARD ORDER GROUP" || $('#groupAction').val() === "DISCARD MED PLAN" || $('#groupAction').val() === "DISCONTINUE ORDER"){
        $("#orderActionButton").prop("disabled", true);
        jq("#discontinueReasonSelect").show();
        document.getElementById("discontinueReasonSelect").style.display = 'block';
    }
    
    $("#discontinueReasonCoded, #discontinueReasonNonCoded").change(function(){
        enableOrderDiscard();
    });
    
    $("#adminPlanName, #adminDrugName, #adminRoute, #adminDose, #adminDoseUnits, #adminQuantity, #adminQuantityUnits, #adminDuration, #adminDurationUnits, #adminFrequency").change(function(){
        adminRecord();
        if($("#adminPlanName").val() !== "" || $("#adminDrugName").val() !== ""){
            checkAdminFields();
        }
    });
    
    $("#drugNameEntered, #drugRoute, #drugDose, #drugDoseUnits, #drugQuantity, #quantityUnits, #drugDuration, #durationUnits, #drugFrequency, #associatedDiagnosis").change(function(){
        validate();
        if($("#drugNameEntered").val() !== ""){
            checkFormFields();
        }
    });
    
    $('.planDrugName .groupCheckBox').on('change', function() {
        var selected = false;
        $('.planDrugName .groupCheckBox').each(function() {
            if(this.checked) {
                selected = true;               
            }
        });
        if(selected){
            $('#selectPlanButton').removeAttr('disabled');
        } else {
            $("#selectPlanButton").prop("disabled", true);
        }        
    });
    
    $('.allergicPlanItemOrderReason').each(function(){
        this.style.borderColor = "orangered";
    });
    
    $('.allergicPlanItemOrderReason').each(function(){
        $(this).on('change', function(){
            if($(this).val() === "")
                this.style.borderColor = "orangered";
            else
                this.style.borderColor = "";
        });
    });
    
    removeFromGroupDialog = emr.setupConfirmationDialog({
        selector: '#removeFromGroupWindow',
        actions: {
            cancel: function() {
            	removeFromGroupDialog.close();
            },
            confirm: function() {
            	$("#removeFromGroupForm").submit();
            }
        }
    });
});

function highlight(){
    var selectedPlan = $("#discardPlan").text().toUpperCase();
    var selectedDrug = $(".discardDrug");
    
    if(selectedPlan !== ""){
        var $rowsNo = $('#medPlansTable tbody tr .planDetails').filter(function () {
            if($.trim($(this).find('.fields').find('.planName').text()) === selectedPlan){
                $(this).find('.fields').find('.icon-plus-sign').hide();
                $(this).find('.fields').find('.icon-minus-sign').show();
                $(this).find('.plansDetailsView').show();
                
                if(selectedDrug.size() === 1){
                    $(this).find('.plansDetailsView').find('.planBlock').find('.planBlockDetails').find('.planItem').each(function(){
                        if($(this).text() === selectedDrug.text().toUpperCase()){
                            $(this).parent().parent().css({"background": "#75b2f0","color": "white"});
                        }
                    });
                }
            }
        });
    }
}

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
    jq(".planBlock").each(function(){
        jq(this).children('div').slice(0, 1).css({'background-color':'','color':''});
    });
}

function validate(){
    if($("#drugNameEntered").val() !== "" && $("#drugRoute").val() !== "" && $("#drugDose").val() !== "" && $("#drugDoseUnits").val() !== "" && $("#drugQuantity").val() !== "" && $("#quantityUnits").val() !== "" && $("#drugDuration").val() !== "" && $("#durationUnits").val() !== "" && $("#drugFrequency").val() !== "" && $("#associatedDiagnosis").val() !== ""){
        $("#addOrderButton").prop("disabled", false);
    } else {
        $("#addOrderButton").prop("disabled", true);
    }
}

function adminRecord(){
    if($("#adminPlanName").val() !== "" && $("#adminDrugName").val() !== "" && $("#adminRoute").val() !== "" && $("#adminDose").val() !== "" && $("#adminDoseUnits").val() !== "" && $("#adminQuantity").val() !== "" && $("#adminQuantityUnits").val() !== "" && $("#adminDuration").val() !== "" && $("#adminDurationUnits").val() !== "" && $("#adminFrequency").val() !== ""){
        $("#planSaveButton").prop("disabled", false);
    } else {
        $("#planSaveButton").prop("disabled", true);
    }
}

function showMedicationPlanOrderWindow(){
    jq("#medPlanWindow").show();
    document.getElementById("medPlanWindow").style.display = 'block';
}

function hideMedicationPlanOrderWindow(){
    jq("#medPlanWindow").hide();
}

function hideMedicationPlansWindow(){
    jq("#medPlanDetailsWindow").hide();
}

function showIndividualOrderDetailsWindow(orderType){
    $("#orderType").text(orderType);
    $("#orderAction").val(orderType);
    jq("#confirmOrderWindow").hide();
    jq("#createOrderWindow").show();
    document.getElementById("createOrderWindow").style.display = 'block';
}

function hideIndividualOrderDetailsWindow(){
    jq("#createOrderWindow").hide();
    jq("#allergicDrugOrderReasonField").hide();
    $("#orderType").text("");
    $("#orderAction").val("");
    $("#drugNameEntered").val("");
    $("#allergicOrderReason").val("");
    $("#drugRoute").val("");
    $("#drugDose").val("");
    $("#drugDoseUnits").val("");
    $("#drugQuantity").val("");
    $("#quantityUnits").val("");
    $("#drugDuration").val("");
    $("#durationUnits").val("");
    $("#drugFrequency").val("");
    $("#refill").val("0");
    $("#refillInterval").val("0");
    $("#associatedDiagnosis").val("");
    $("#patientInstructions").val("");
    $("#pharmacistInstructions").val("");
    $("#addOrderButton").prop("disabled", true);
    
    $('#createOrderForm input, #createOrderForm select').each(function(){
        this.style.borderColor = "";
    });

    clearHighlights();
}

function checkFormFields(){
    if($("#allergicOrderReason").val() === "")
        document.getElementById("allergicOrderReason").style.borderColor = "orangered";
    else
        document.getElementById("allergicOrderReason").style.borderColor = "";
    
    if($("#drugRoute").val() === "")
        document.getElementById("drugRoute").style.borderColor = "orangered";
    else
        document.getElementById("drugRoute").style.borderColor = "";
    
    if($("#drugDose").val() === "")
        document.getElementById("drugDose").style.borderColor = "orangered";
    else
        document.getElementById("drugDose").style.borderColor = "";
    
    if($("#drugDoseUnits").val() === "")
        document.getElementById("drugDoseUnits").style.borderColor = "orangered";
    else
        document.getElementById("drugDoseUnits").style.borderColor = "";
    
    if($("#drugQuantity").val() === "")
        document.getElementById("drugQuantity").style.borderColor = "orangered";
    else
        document.getElementById("drugQuantity").style.borderColor = "";
    
    if($("#quantityUnits").val() === "")
        document.getElementById("quantityUnits").style.borderColor = "orangered";
    else
        document.getElementById("quantityUnits").style.borderColor = "";
    
    if($("#drugDuration").val() === "")
        document.getElementById("drugDuration").style.borderColor = "orangered";
    else
        document.getElementById("drugDuration").style.borderColor = "";
    
    if($("#durationUnits").val() === "")
        document.getElementById("durationUnits").style.borderColor = "orangered";
    else
        document.getElementById("durationUnits").style.borderColor = "";
    
    if($("#drugFrequency").val() === "")
        document.getElementById("drugFrequency").style.borderColor = "orangered";
    else
        document.getElementById("drugFrequency").style.borderColor = "";
    
    if($("#associatedDiagnosis").val() === "")
        document.getElementById("associatedDiagnosis").style.borderColor = "orangered";
    else
        document.getElementById("associatedDiagnosis").style.borderColor = "";
    
}

function checkAdminFields(){
    if($("#adminDrugName").val() === "")
        document.getElementById("adminDrugName").style.borderColor = "orangered";
    else
        document.getElementById("adminDrugName").style.borderColor = "";
    
    if($("#adminPlanName").val() === "")
        document.getElementById("adminPlanName").style.borderColor = "orangered";
    else
        document.getElementById("adminPlanName").style.borderColor = "";
    
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
}

function showDrugOrderViewWindow(action,givenName,lastName,startdate,drugname,dose,doseUnits,route,duration,durationUnits,quantity,quantityUnits,frequency,numRefills,allergicOrderReason,priority,patientinstructions,pharmacistinstructions,pharmacomments){
    
    $("#activeOrderAction").text(action);
    $("#patient_name").text(givenName+" "+lastName);
    $("#start_date").text(startdate);
    $("#order_priority").text(priority);
    $("#order_refills").text(numRefills);
    $("#order_details").text(drugname +" "+dose+" "+doseUnits+" "+route+" "+duration+" "+durationUnits+" "+quantity+" "+quantityUnits+" "+frequency);
    
    if(allergicOrderReason !== "" && allergicOrderReason !== "null"){
        $("#order_reason").text(allergicOrderReason);
        jq("#allergicOrderReasonView").show();
        document.getElementById("allergicOrderReasonView").style.display = 'block';
    }
    
    $("#patient_instructions").text(patientinstructions);
    $("#pharmacist_instructions").text(pharmacistinstructions);
    
    if(pharmacomments !== "" && pharmacomments !== null && pharmacomments !== "null" && pharmacomments !== undefined){
        $("#pharma_comments").text(pharmacomments);
        jq("#pharmacistCommentsView").show();
        document.getElementById("pharmacistCommentsView").style.display = 'block';
    }
    
    jq("#showOrderWindow").show();
    document.getElementById("showOrderWindow").style.display = 'block';
}

function hideDrugOrderViewWindow(){
    jq("#showOrderWindow").hide();    
    clearHighlights();
}

function showEditSingleOrderWindow(orderType,orderClass,orderId,drugName,startDate,dose,doseUnits,route,duration,durationUnits,quantity,quantityUnits,frequency,numRefills,refillInterval,associateddiagnosis,allergicOrderReasons,priority,patientinstructions,pharmacistinstructions){
    $("#orderType").text(orderType);
    $("#orderAction").val(orderType);
    $("#orderClass").val(orderClass);
    $("#order_id").val(orderId);
    $("#drugNameEntered").val(drugName);
    $("#drugRoute").val(route);
    $("#drugDose").val(dose);
    $("#drugDoseUnits").val(doseUnits);
    $("#drugQuantity").val(quantity);
    $("#quantityUnits").val(quantityUnits);
    $("#drugDuration").val(duration);
    $("#durationUnits").val(durationUnits);
    $("#drugFrequency").val(frequency);
    $("#refill").val(numRefills);
    $("#refillInterval").val(refillInterval);
    $("#orderPriority").val(priority);
    $("#associatedDiagnosis").val(associateddiagnosis);
    if(allergicOrderReasons !== "" && allergicOrderReasons !== "null"){
        $("#allergicOrderReason").val(allergicOrderReasons);
        jq("#allergicDrugOrderReasonField").show();
        document.getElementById("allergicDrugOrderReasonField").style.display = 'block';
    }
    $("#patientInstructions").val(patientinstructions);
    $("#pharmacistInstructions").val(pharmacistinstructions);
    $("#addOrderButton").prop("disabled", false);
    jq("#createOrderWindow").show();
    document.getElementById("createOrderWindow").style.display = 'block';
}

function showRenewOrderWindow(orderType,orderId,drugName,dose,doseUnits,route,duration,durationUnits,quantity,quantityUnits,frequency,numRefills,refillInterval,associateddiagnosis,priority,patientinstructions,pharmacistinstructions){
    $("#orderType").text(orderType);
    $("#orderAction").val(orderType);
    $("#order_id").val(orderId);
    $("#drugNameEntered").val(drugName);
    $("#drugRoute").val(route);
    $("#drugDose").val(dose);
    $("#drugDoseUnits").val(doseUnits);
    $("#drugQuantity").val(quantity);
    $("#quantityUnits").val(quantityUnits);
    $("#drugDuration").val(duration);
    $("#durationUnits").val(durationUnits);
    $("#drugFrequency").val(frequency);
    $("#orderPriority").val(priority);
    $("#refill").val(numRefills);
    $("#refillInterval").val(refillInterval);
    $("#associatedDiagnosis").val(associateddiagnosis);
    $("#patientInstructions").val(patientinstructions);
    $("#pharmacistInstructions").val(pharmacistinstructions);
    $("#addOrderButton").prop("disabled", false);
    jq("#createOrderWindow").show();
    document.getElementById("createOrderWindow").style.display = 'block';
}

function discardSingleOrder(order){
    $("#selectedActiveOrder").val(order);
    $("#activeGroupForm").submit();
}

function discardSingleItem(order){
    $("#selectedActiveItem").val(order);
    $("#activePlanForm").submit();
}

function discardMedPlanOrder(plan){
    $("#selectedActivePlan").val(plan);
    $("#activePlanForm").submit();
}

function renewMedPlanWindow(plan){
    $("#selectedNonActivePlan").val(plan);
    $("#nonActivePlanForm").submit();
}

function autoCompletePlan(){
    $("#diseaseName").autocomplete({
        select : function(event, ui){
            $("#diseaseName").val((ui.item.label).trim());
            $("#diseaseForm").submit();
        }
    });
}

function autoCompleteDrug(drug, allergies){
    $("#drugNameEntered").autocomplete({
       select : function( event , ui ) {
            var allergyList = allergies.split(",");
            var isAllergic = false;
            $.each(allergyList,function(index,value){
                var drugname = value.replace("[","").replace("]","").trim();
                var selectedDrug = (ui.item.label).trim();
                if(selectedDrug === drugname){
                    isAllergic = true;
                } 
            });
            if(isAllergic){
                jq("#allergicDrugOrderReasonField").show();
                document.getElementById("allergicDrugOrderReasonField").style.display = 'block';
            } else {
                jq("#allergicDrugOrderReasonField").hide();
            }
            validate();
        }
    });
}

function displayPlanCreationWindow(){
    $("#defineAction").val("definePlan");
    jq("#definePlanWindow").show();
    document.getElementById("definePlanWindow").style.display = 'block';
}

function hideMedPlanDefineWindow(){
    jq("#definePlanWindow").hide();
    $("#definePlanName").val("");
    $("#definePlanDesc").val("");
    $("#defineAction").val("");
    $("#definePlanId").val("");
}

function addPlanItemWindow(diseaseName){
    jq("#createPlanWindow").show();
    document.getElementById("createPlanWindow").style.display = 'block';
    $("#adminActionType").text("ADD DRUG TO PLAN");
    $("#adminPlanName").val(diseaseName);
    checkAdminFields();
}

function hideMedPlanCreateWindow(){
    jq("#createPlanWindow").hide();
    $("#planId").val("");
    $("#adminPlanName").val("");
    $("#adminDrugName").val("");
    $("#adminRoute").val("");
    $("#adminDose").val("");
    $("#adminDoseUnits").val("");
    $("#adminQuantity").val("");
    $("#adminQuantityUnits").val("");
    $("#adminDuration").val("");
    $("#adminDurationUnits").val("");
    $("#adminFrequency").val("");
    $("#planSaveButton").prop("disabled", true);
        
    $('#createPlanForm input, #createPlanForm select').each(function(){
        this.style.borderColor = "";
    });
}

function editPlanItemDetails(planid,diseaseName,drugName,dose,doseunits,route,quantity,quantityunits,duration,durationunits,frequency){
    jq("#createPlanWindow").show();
    document.getElementById("createPlanWindow").style.display = 'block';
    $("#adminActionType").text("EDIT PLAN");
    $("#planId").val(planid);
    $("#adminPlanName").val(diseaseName);
    $("#adminDrugName").val(drugName);
    $("#adminDose").val(dose);
    $("#adminDoseUnits").val(doseunits);
    $("#adminRoute").val(route);
    $("#adminQuantity").val(quantity);
    $("#adminQuantityUnits").val(quantityunits);
    $("#adminDuration").val(duration);
    $("#adminDurationUnits").val(durationunits);
    $("#adminFrequency").val(frequency);
    $("#planSaveButton").prop("disabled", false);
}

function deleteMedPlan(planName){
    $("#selectedMedPlan").val(planName);
    $("#adminPageForm").submit();    
}

function renameMedPlan(id, planName, planDesc){
    $("#definePlanId").val(id);
    $("#defineAction").val("renamePlan");
    $("#definePlanName").val(planName);
    $("#definePlanDesc").val(planDesc);
    jq("#definePlanWindow").show();
    document.getElementById("definePlanWindow").style.display = 'block';
}

function deleteMedPlanItem(planID){
    $("#selectedPlanItem").val(planID);
    $("#adminPageForm").submit();  
}

function hideMedPlanDeleteWindow(){
    jq("#deletePlanWindow").hide();
    $("#selectedPlanItem").val("");
    $("#selectedMedPlan").val("");
}

function showRenewGroupOrderWindow(orderID){
    $("#selectedNonActiveGroup").val(orderID);
    $("#nonActiveGroupForm").submit();
}

function showDiscardGroupOrderWindow(orderID){
   $("#selectedActiveGroup").val(orderID);
   $("#activeGroupForm").submit();
}

function hideGroupOrderWindow(){
    jq("#showGroupOrderWindow").hide();
    jq(".oldGroupRow").each(function(){
        jq(this).children('td').slice(0, 1).css({'background-color':'','color':''});
    });
}

function showAddOrderToGroupWindow(orderType,groupID){
    $("#order_id").val(groupID);
    $("#orderType").text(orderType);
    $("#orderAction").val(orderType);
    jq("#confirmOrderWindow").hide();
    jq("#createOrderWindow").show();
    document.getElementById("createOrderWindow").style.display = 'block';
}

function discontinueReason(){
    if(document.getElementById("discontinueReasonCoded").value === "Other"){
        jq("#discontinueReasonText").show();
        document.getElementById("discontinueReasonText").style.display = 'block';
    } else {
        jq("#discontinueReasonText").hide();
    }
    if(document.getElementById("discontinueReasonCoded").value === ""){
        $("#orderActionButton").prop("disabled", true);
    } else {
        $("#orderActionButton").prop("disabled", false);
    }
}

function removeFromGroup(OrderId){
    $("#removeFromGroup").val(OrderId);
    removeFromGroupDialog.show();
}