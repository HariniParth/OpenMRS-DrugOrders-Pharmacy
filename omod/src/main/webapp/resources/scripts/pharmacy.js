/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* global jq, emr */

var removeFromHoldDialog = null;
var contactOrdererDialog = null;

$(document).ready( function() {
       
    removeFromHoldDialog = emr.setupConfirmationDialog({
        selector: '#removeHold',
        actions: {
            cancel: function() {
            	removeFromHoldDialog.close();
            }
        }
    });
    
    contactOrdererDialog = emr.setupConfirmationDialog({
        selector: '#contactOrderer'
    });
        
    highlight();
    
    $('.groupCheckBox').on('change', function() {
        enableConfirmBtn();
    }); 
    
});

/*
 * Display the fragment to remove the hold on selected orders.
 */
function showRemoveOrderHoldWindow(){
    removeFromHoldDialog.show();
}

/*
 * Display the fragment showing Orderer's contact info
 */
function showOrdererContact(ordererName, orderID, orderName){
    $("#orderName").val(orderName);
    $("#orderNumber").val(orderID);
    $("#ordererName").val(ordererName);
    $("#ordererEmail").text(ordererName);
    $("#ordererPhone").text(ordererName);
    contactOrdererDialog.show();
}

/*
 * Submit form to remove hold on orders.
 */
function removeHoldOnOrders(){
    $("#nonActiveTableForm").submit();
}

/*
 * Enable submission of order action if one or more drugs are selected.
 */
function enableConfirmBtn(){
    
    var selected = false;
    $('.groupCheckBox').each(function() {
        if(this.checked) {
            selected = true;  
            $(this).parent().parent().next('.drugDetails').find('.dispatchFields').find('.fields').first().find('#order_value').find('.commentForPatient').prop("readonly", false);
            $(this).parent().parent().next('.drugDetails').find('.dispatchFields').find('.fields').last().find('#order_value').find('.drugExpiryDate').prop("disabled", false);
        } else {
            $(this).parent().parent().next('.drugDetails').find('.dispatchFields').find('.fields').first().find('#order_value').find('.commentForPatient').prop("readonly", true);
            $(this).parent().parent().next('.drugDetails').find('.dispatchFields').find('.fields').last().find('#order_value').find('.drugExpiryDate').prop("disabled", true);
        }
    });
    
    if(selected){
        $('#confirmBtn1').removeAttr('disabled'); 
        $('#confirmBtn2').removeAttr('disabled'); 
    } else {
        $("#confirmBtn1").prop("disabled", true);
        $("#confirmBtn2").prop("disabled", true);
    } 
}

/*
 * Highlight selected orders in the active drug order table.
 */
function highlight(){
    var pharmaPlan = $("#pharmaPlan").val();
    var pharmaGroup = $("#pharmaGroup").val();
    var pharmaSingle = $("#pharmaSingle").val();
    
    var currentSelected;
    
    /*
     * Highlight selected group or plan orders
     */
    if(pharmaPlan !== "" || pharmaGroup !== ""){
        if(pharmaGroup !== "")
            currentSelected = pharmaGroup;
        else
            currentSelected = pharmaPlan;
        
        var $rows2 = $('#currentGroupOrdersTable tbody .groupRow').filter(function () {
            var givenGroup = $.trim($(this).find('td').eq(0).text());
            if(givenGroup === currentSelected){
                $(this).children('td').slice(1, 2).css({"background": "#75b2f0","color": "white"});
            }
        });
    }       
     
    /*
     * Highlight selected individual order
     */
    else if(pharmaSingle !== ""){
        currentSelected = pharmaSingle;
        
        var $rows1 = $('#currentGroupOrdersTable tbody .singleRow').filter(function () {
            var givenOrder = $.trim($(this).find('td').eq(0).text());
            if(givenOrder === currentSelected){
                $(this).children('td').slice(1, 6).css({"background": "#75b2f0","color": "white"});
            }
        });
    }
    
    /*
     * Highlight orders referred to by the mail fragment
     */
    if(document.getElementById("drugNames")){
        var drugNames = $("#drugNames").val().split(";");
        if(drugNames !== ""){
            $.each(drugNames, function(index, value){
                var $rows2 = $('#currentGroupOrdersTable tbody .groupRow').filter(function () {
                    var $rows3 = $(this).find('td').eq(1).find('.groupElement').each(function(){
                        var drug = $.trim($(this).find('.d1').find('.e1').find('.g1').find('.c1').find('.wordBreak').text());
                        if(drug === value){
                            $(this).parent().parent().children('td').slice(1, 2).css({"background": "#75b2f0","color": "white"});
                        }
                    });
                });

                var $rows1 = $('#currentGroupOrdersTable tbody .singleRow').filter(function () {
                    var drug = $.trim($(this).find('td').eq(1).find('.wordBreak').text());
                    if(drug === value){
                        $(this).children('td').slice(1, 6).css({"background": "#75b2f0","color": "white"});
                    }
                });
            });
        }  
    }       
}

/*
 * Clear the highlights on the active drug order table.
 */
function clearHighlights(){
    
    jq(".groupRow").each(function(){
        jq(this).children('td').slice(1, 2).css({'background-color':'','color':''});
    });
    jq(".singleRow").each(function(){
        jq(this).children('td').slice(1, 6).css({'background-color':'','color':''});
    });
    jq(".orderRow").each(function(){
        jq(this).css({'background-color':'','color':''});
    });
}

/*
 * Provide auto-complete options when Pharmacist starts typing the Patient's name.
 */
function autoCompletePatientName(patientNameList){
    var list = patientNameList.replace("[","").replace("]","").split(',');
    $("#patient_full_name").autocomplete({
        source : list,
        select : function( event , ui ) {
            $("#patient_full_name").val(ui.item.label);
            $("#searchByPatient").submit();
        },
        response: function(event, ui) {
            if (ui.content.length === 0) {
                alert("Cannot find patient!");
            }
        }
    });
}

/*
 * Display details of selected plan order.
 */
function selectedPlanOrder(planID){
    $("#planID").val(planID);
    $("#orderNumber").val("");
    $("#ordererName").val("");
    $("#groupOrdersForm").submit();
}

/*
 * Display details of selected group order.
 */
function selectedGroupOrder(groupID){
    $("#groupID").val(groupID);
    $("#orderNumber").val("");
    $("#ordererName").val("");
    $("#groupOrdersForm").submit();
}

/*
 * Display details of selected single order.
 */
function selectedSingleOrder(orderID){
    $("#orderID").val(orderID);
    $("#orderNumber").val("");
    $("#ordererName").val("");
    $("#groupOrdersForm").submit();
}

/*
 * Show appropriate fields when one of the actions (Dispatch, On-Hold, Discard) is selected.
 */
function showPharmaConfirmationSection(action){
    jq("#statusLabel").show();
    jq("#statusLabel").css("display", "block");
    
    $("#selectedAction").text(action);
    $("#pharmaGroupAction").val(action);
    jq("#pharmaGroupButtons").hide();
    
    if(action === "On Hold" || action === "Discard"){
        jq("#pharmaGroupActionButtons").show();
        jq("#groupComments").attr("required", true);
        jq("#pharmaGroupActionButtons").css("display", "block");
        
        $('.commentForPatient').each(function() {
            $(this).attr("required", false);
        });
        
        $('.drugExpiryDate').each(function() {
            $(this).attr("required", false);
        });
    }
    
    if(action === "Dispatch"){
        $('.commentForPatient').each(function() {
            $(this).attr("required", true);
        });
        
        $('.drugExpiryDate').each(function() {
            $(this).attr("required", true);
        });
        
        jq(".print").show();
        jq(".print").css("display", "block");
        jq("#confirmButtons").show();
        jq(".dispatchFields").show();
        jq(".dispatchFields").css("display", "block");
    }
}

/*
 * Hide fields to enter details to dispatch / put on hold / discard a record.
 */
function showPharmaOrderViewSection(){
    jq(".print").hide();
    jq("#statusLabel").hide();
    jq("#confirmButtons").hide();
    jq(".dispatchFields").hide();
    jq("#selectedAction").text(""); 
    jq("#groupComments").val("");
    jq("#pharmaGroupActionButtons").hide();
    jq("#groupComments").attr("required", false);
    
    $('.commentForPatient').each(function() {
        $(this).val("");
    });
    
    $('.drugExpiryDate').each(function() {
        $(this).val("");
    });
    
    jq("#pharmaGroupButtons").show();
    jq("#pharmaGroupButtons").css("display", "block");
}

/*
 * Clear the Patient name search field.
 */
function clearPatientTableFilters(){
    $("#patient_full_name").val("");
    $("#searchByPatient").submit();
}

/*
 * Hide fragment displaying the details of selected order. (Clear selection)
 */
function closePharmaGroupView(){
    jq("#pharmaGroupView").hide();
    $("#pharmaSingle").val();
    $("#pharmaGroup").val();
    $("#pharmaPlan").val();
    clearHighlights();
}

/*
 * CLose mail fragment.
 */
function closeMailWindow(){
    jq("#mailWindow").hide();
    clearHighlights();
}

/*
 * Create a email with order and Patient information to send to the orderer.
 */
function emailLink(orderList, patientID, patientName, patientDOB, patientAddr, orderDetails){
    var checked = 0;
    var message = "";
    var selected = "";
    var notSelected = "";
    
    var list = orderList.trim().split(" ");
    $(".groupCheckBox").each(function(){
        if(this.checked){
            checked++;
            var drug = $(this).nextAll('.checkedDrug').text();
            selected = selected + drug + ", ";
        } else {
            var drug = $(this).nextAll('.checkedDrug').text();
            notSelected = notSelected + drug + ", ";
        }
    });
    
    selected = selected.substring(0, selected.length - 2);
    notSelected = notSelected.substring(0, notSelected.length - 2);
    
    if(list.length !== checked)
        message = "Please note that the remaining Order(s) for "+notSelected+" are available for sale. Please confirm if these can be dispensed.";
    
    var comments = $("#groupComments").val();
    var selectedAction = $("#selectedAction").text();
    document.getElementById("emailLink").href = "mailto:<Recipient Address<>>?Subject=Order Status Alert!!! "+selectedAction+"&body=This is to inform you that the following Order(s) for "+selected+" cannot be dispatched.%0AStatus Set: "+selectedAction+"%0A%0APatient ID:   "+patientID+"%0APatient Name: "+patientName+"%0APatient DOB:  "+patientDOB+"%0APatient Addr: "+patientAddr+"%0A%0A"+orderDetails+"%0AComments: "+comments+"%0A%0A"+message;
}