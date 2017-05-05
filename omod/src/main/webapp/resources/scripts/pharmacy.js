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

function showRemoveOrderHoldWindow(){
    removeFromHoldDialog.show();
}

function showOrdererContact(ordererName, orderID){
    $("#orderNumber").val(orderID);
    $("#ordererName").val(ordererName);
    $("#ordererEmail").text(ordererName);
    $("#ordererPhone").text(ordererName);
    contactOrdererDialog.show();
}

function contactOrderer(){
    $("#groupOrdersForm").submit();
}

function removeHoldOnOrders(){
    $("#nonActiveTableForm").submit();
}

function enableConfirmBtn(){
    
    var selected = false;
    $('.groupCheckBox').each(function() {
        if(this.checked) {
            selected = true;               
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

function highlight(){
    var pharmaPlan = $("#pharmaPlan").val();
    var pharmaGroup = $("#pharmaGroup").val();
    var pharmaSingle = $("#pharmaSingle").val();
    
    var currentSelected;
    
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
     
    else if(pharmaSingle !== ""){
        currentSelected = pharmaSingle;
        
        var $rows1 = $('#currentGroupOrdersTable tbody .singleRow').filter(function () {
            var givenOrder = $.trim($(this).find('td').eq(0).text());
            if(givenOrder === currentSelected){
                $(this).children('td').slice(1, 6).css({"background": "#75b2f0","color": "white"});
            }
        });
    }
}

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

function autoCompletePatientName(patientNameList){
    var list = patientNameList.replace("[","").replace("]","").split(',');
    $("#patient_full_name").autocomplete({
        source : list,
        select : function( event , ui ) {
            $("#patient_full_name").val(ui.item.label);
            $("#searchByPatient").submit();
        }
    });
}

function selectedPlanOrder(planID){
    $("#planID").val(planID);
    $("#orderNumber").val("");
    $("#ordererName").val("");
    $("#groupOrdersForm").submit();
}

function selectedGroupOrder(groupID){
    $("#groupID").val(groupID);
    $("#orderNumber").val("");
    $("#ordererName").val("");
    $("#groupOrdersForm").submit();
}

function selectedSingleOrder(orderID){
    $("#orderID").val(orderID);
    $("#orderNumber").val("");
    $("#ordererName").val("");
    $("#groupOrdersForm").submit();
}

function showPharmaConfirmationSection(action){
    jq("#statusLabel").show();
    document.getElementById("statusLabel").style.display = 'block';
    
    $("#selectedAction").text(action);
    $("#pharmaGroupAction").val(action);
    jq("#pharmaGroupButtons").hide();
    
    if(action === "On Hold" || action === "Discard"){        
        jq("#pharmaGroupActionButtons").show();
        document.getElementById("pharmaGroupActionButtons").style.display = 'block';
    }
    
    if(action === "Dispatch"){
        jq("#printLabel").show();
        jq("#confirmButtons").show();
        document.getElementById("printLabel").style.display = 'block';
        jq(".dispatchFields").show();
        document.getElementsByClassName("dispatchFields").style.display = 'block';
    }
}

function showPharmaOrderViewSection(){
    
    $("#selectedAction").text("");    
    jq("#printLabel").hide();
    jq("#statusLabel").hide();
    jq("#confirmButtons").hide();
    jq(".dispatchFields").hide();
    jq("#pharmaGroupActionButtons").hide();
    
    jq("#pharmaGroupButtons").show();
    document.getElementById("pharmaGroupButtons").style.display = 'block';
}

function clearPatientTableFilters(){
    $("#patient_full_name").val("");
    $("#searchByPatient").submit();
}

function closePharmaGroupView(){
    jq("#pharmaGroupView").hide();
    $("#pharmaSingle").val();
    $("#pharmaGroup").val();
    $("#pharmaPlan").val();
    clearHighlights();
}

function closeMailWindow(){
    jq("#mailWindow").hide();
}