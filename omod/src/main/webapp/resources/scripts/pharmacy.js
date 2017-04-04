/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* global jq, emr */

var removeFromHoldDialog = null;

$(document).ready( function() {
    
    $(document).mouseup(function (e){
        if(e.target.nodeName !== "TD"){
            var objects = $('.dialog');
            $(objects).each(function(){
                if (!$(this).is(e.target) && $(this).has(e.target).length === 0){
                    $(this).hide();
                    clearHighlights();
                }
                else if(document.getElementById('pharma_order_details')){
                    highlight();
                }
                else if(document.getElementsByClassName('group_order_order_details')){
                    highlightGroup();
                }
            });
        }        
    });
    
    if(document.getElementById('pharma_order_details')){
        highlight();
    }
    
    if(document.getElementsByClassName('group_order_order_details')){
        highlightGroup();
    }
    
    removeFromHoldDialog = emr.setupConfirmationDialog({
        selector: '#removeHold',
        actions: {
            cancel: function() {
            	removeFromHoldDialog.close();
            }
        }
    });
    
    $("#confirmBtn1").prop("disabled", true);
    $("#confirmBtn2").prop("disabled", true);
    
    $('.groupCheckBox').on('change', function() {
        enableConfirmBtn();
    }); 
    
});

function showRemoveOrderHoldWindow(){
    removeFromHoldDialog.show();
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
    var selectedOrder = $("#pharma_order_details").val().replace(/(\w+).*/,"$1").toUpperCase();
    
    if(selectedOrder !== undefined){
        var $rowsNo = $('#currentDrugOrdersTable tbody tr').filter(function () {
            if($.trim($(this).find('td').eq(1).text()) === selectedOrder){
                $(this).css({"background": "#75b2f0","color": "white"});
            }
        });
    }
}

function highlightGroup(){
    var $rowsN1 = $('#currentGroupOrdersTable tbody .singleRow').filter(function () {
            
        var givenDrug = $.trim($(this).find('td').eq(1).text());
        var matching = false;

        $(".group_order_order_details").each(function(){
            var selectedDrug = $(this).val().split(",")[0].toUpperCase();

            if(selectedDrug === givenDrug)
                matching = true;
        });

        if(matching){
            $(this).css({"background": "#75b2f0","color": "white"});
        }
    });
    
    var $rowsN2 = $('#currentGroupOrdersTable tbody .groupRow').filter(function () {
            
        var givenDrug = $.trim($(this).find('td').eq(1).find('div').children('div').first().children('div').first().text());
        var matching = false;

        $(".group_order_order_details").each(function(){
            var selectedDrug = $(this).val().split(",")[0].toUpperCase();

            if(selectedDrug === givenDrug)
                matching = true;
        });

        if(matching){
            $(this).css({"background": "#75b2f0","color": "white"});
        }
    });
}

function clearHighlights(){
    
    jq(".groupRow").each(function(){
        jq(this).css({'background-color':'','color':''});
    });
    jq(".singleRow").each(function(){
        jq(this).css({'background-color':'','color':''});
    });
    jq(".orderRow").each(function(){
        jq(this).css({'background-color':'','color':''});
    });
}

function closeAllOrdersWindow(){
    jq("#allOrdersTableWrapper").hide();
}

function autoCompletePatientName(patientNameList){
    var list = patientNameList.replace("[","").replace("]","").split(',');
    console.log(list);
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
    $("#groupOrdersForm").submit();
}

function selectedGroupOrder(groupID){
    $("#groupID").val(groupID);
    $("#groupOrdersForm").submit();
}

function selectedSingleOrder(orderID){
    $("#orderID").val(orderID);
    $("#groupOrdersForm").submit();
}

function selectedOrder(orderID){
    $("#singleID").val(orderID);
    $("#individualOrderForm").submit();
}

function associatedOrder(orderID){
    $("#assocID").val(orderID);
    $("#associatedOrderForm").submit();
}

function otherOrder(orderID){
    $("#otherID").val(orderID);
    $("#otherOrderForm").submit();
}

function showPharmaConfirmationSection(action){
    jq("#statusLabel").show();
    document.getElementById("statusLabel").style.display = 'block';
    
    $("#selectedAction").text(action);
    $("#pharmaGroupAction").val(action);
    $("#pharmaSingleAction").val(action);
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
    $("#pharmaSingleAction").text("");
    
    jq("#printLabel").hide();
    jq("#statusLabel").hide();
    jq("#confirmButtons").hide();
    jq(".dispatchFields").hide();
    jq("#pharmaGroupActionButtons").hide();
    
    jq("#pharmaGroupButtons").show();
    document.getElementById("pharmaGroupButtons").style.display = 'block';
}

function clearPatientTableFIlters(){
    $("#patient_full_name").val("");
    $("#searchByPatient").submit();
}

function submitDispatch(){
    $("#groupActionForm").submit();
}

function confirmDispatch(){
    $("#orderActionForm").submit();
}

function closePharmaGroupView(){
    jq("#pharmaGroupView").hide();
    clearHighlights();
}

function closePharmaOrderView(){
    jq("#pharmaOrderView").hide();
    clearHighlights();
}

function closeMailWindow(){
    jq("#mailWindow").hide();
}