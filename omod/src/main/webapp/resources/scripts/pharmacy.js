/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* global jq, emr */

var removeFromHoldDialog = null;
var contactOrdererDialog = null;

jq(document).ready( function() {
       
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
    
    jq('.groupCheckBox').on('change', function() {
        enableConfirmBtn();
    }); 
    
    var lines = 16;
    jq("textarea").on("keydown", function(e){
        // Check the number of rows entered in the given textarea
        var newLines = $(this).val().split("\n").length;
        if(e.keyCode === 13 && newLines >= lines) {
            alert("Field next line limit reached!");
            return false;
        }
        
        if(jq(this).val().length >= 912 && e.keyCode !== 8 && e.keyCode !== 46)
            alert("Field length limit reached!");
        
        /*
        * Disable typing whitespaces before entering any data in the textarea fields. 
        * This will not allow empty spaces to be submitted.
        */
        if(e.keyCode === 32)
            if(jq(this).val().trim() === "")
                return false;
    });
    
    /*
     * Highlight the fields to enter the reasons to order a drug unless they are filled.
     */
    jq('#groupComments, .commentForPatient, .drugExpiryDate').each(function(){
        this.style.borderColor = "orangered";
    });
    
    jq('#groupComments, .commentForPatient, .drugExpiryDate').each(function(){
        jq(this).on('change', function(){
            if(jq(this).val().trim() === "")
                this.style.borderColor = "orangered";
            else
                this.style.borderColor = "";
        });
    });
    
    jq('.drugExpiryDate').datepicker({
        minDate:  new Date(),
        nextText: "===>",
        prevText: "===<",
        changeYear: true,
        changeMonth: true
    });
    
    jq('.drugExpiryDate').on('keydown keyup', function(e){
        e.preventDefault();
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
    jq("#orderName").val(orderName);
    jq("#orderNumber").val(orderID);
    jq("#ordererName").val(ordererName);
    jq("#ordererEmail").text(ordererName);
    jq("#ordererPhone").text(ordererName);
    contactOrdererDialog.show();
}

/*
 * Submit form to remove hold on orders.
 */
function removeHoldOnOrders(){
    jq("#nonActiveTableForm").submit();
}

/*
 * Enable submission of order action if one or more drugs are selected.
 */
function enableConfirmBtn(){
    
    var selected = false;
    jq('.groupCheckBox').each(function() {
        if(this.checked) {
            selected = true;  
            jq(this).parent().parent().next('.drugDetails').find('.dispatchFields').find('.fields').find('.commentForPatient').prop("readonly", false);
            jq(this).parent().parent().next('.drugDetails').find('.dispatchFields').find('.fields').find('.commentForPatient').css("borderColor", "orangered");
            
            jq(this).parent().parent().next('.drugDetails').find('.dispatchFields').find('.fields').last().find('#order_value').find('.drugExpiryDate').prop("disabled", false);
            jq(this).parent().parent().next('.drugDetails').find('.dispatchFields').find('.fields').last().find('#order_value').find('.drugExpiryDate').css("borderColor", "orangered");
        } else {
            jq(this).parent().parent().next('.drugDetails').find('.dispatchFields').find('.fields').find('.commentForPatient').val("");
            jq(this).parent().parent().next('.drugDetails').find('.dispatchFields').find('.fields').find('.commentForPatient').prop("readonly", true);
            jq(this).parent().parent().next('.drugDetails').find('.dispatchFields').find('.fields').find('.commentForPatient').css("borderColor", "");
            
            jq(this).parent().parent().next('.drugDetails').find('.dispatchFields').find('.fields').last().find('#order_value').find('.drugExpiryDate').val("");
            jq(this).parent().parent().next('.drugDetails').find('.dispatchFields').find('.fields').last().find('#order_value').find('.drugExpiryDate').prop("disabled", true);
            jq(this).parent().parent().next('.drugDetails').find('.dispatchFields').find('.fields').last().find('#order_value').find('.drugExpiryDate').css("borderColor", "");
        }
    });
    
    if(selected){
        jq('#confirmBtn1').removeAttr('disabled'); 
        jq('#confirmBtn2').removeAttr('disabled'); 
        jq("#groupComments").prop("readonly", false);
        jq("#groupComments").css("borderColor", "orangered");
    } else {
        jq("#confirmBtn1").prop("disabled", true);
        jq("#confirmBtn2").prop("disabled", true);
        jq("#groupComments").prop("readonly", true);
        jq("#groupComments").css("borderColor", "");
    } 
}

/*
 * Highlight selected orders in the active drug order table.
 */
function highlight(){
    var pharmaPlan = jq("#pharmaPlan").val();
    var pharmaGroup = jq("#pharmaGroup").val();
    var pharmaSingle = jq("#pharmaSingle").val();
    
    var currentSelected;
    
    /*
     * Highlight selected group or plan orders
     */
    if(pharmaPlan !== "" || pharmaGroup !== ""){
        if(pharmaGroup !== "")
            currentSelected = pharmaGroup;
        else
            currentSelected = pharmaPlan;
        
        var jqrows2 = jq('#currentGroupOrdersTable tbody .groupRow').filter(function () {
            var givenGroup = jq.trim(jq(this).find('td').eq(0).text());
            if(givenGroup === currentSelected){
                jq(this).children('td').slice(1, 2).css({"background": "#75b2f0","color": "white"});
            }
        });
    }       
     
    /*
     * Highlight selected individual order
     */
    else if(pharmaSingle !== ""){
        currentSelected = pharmaSingle;
        
        var jqrows1 = jq('#currentGroupOrdersTable tbody .singleRow').filter(function () {
            var givenOrder = jq.trim(jq(this).find('td').eq(0).text());
            if(givenOrder === currentSelected){
                jq(this).children('td').slice(1, 6).css({"background": "#75b2f0","color": "white"});
            }
        });
    }
    
    /*
     * Highlight orders referred to by the mail fragment
     */
    if(document.getElementById("drugNames")){
        var drugNames = jq("#drugNames").val().split(";");
        if(drugNames !== ""){
            jq.each(drugNames, function(index, value){
                var jqrows2 = jq('#currentGroupOrdersTable tbody .groupRow').filter(function () {
                    var jqrows3 = jq(this).find('td').eq(1).find('.groupElement').each(function(){
                        var drug = jq.trim(jq(this).find('.d1').find('.e1').find('.g1').find('.c1').find('.wordBreak').text());
                        if(drug === value){
                            jq(this).parent().parent().children('td').slice(1, 2).css({"background": "#75b2f0","color": "white"});
                        }
                    });
                });

                var jqrows1 = jq('#currentGroupOrdersTable tbody .singleRow').filter(function () {
                    var drug = jq.trim(jq(this).find('td').eq(1).find('.wordBreak').text());
                    if(drug === value){
                        jq(this).children('td').slice(1, 6).css({"background": "#75b2f0","color": "white"});
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
    jq("#patient_full_name").autocomplete({
        source : list,
        select : function( event , ui ) {
            jq("#patient_full_name").val(ui.item.label);
            jq("#searchByPatient").submit();
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
    jq("#planID").val(planID);
    jq("#orderNumber").val("");
    jq("#ordererName").val("");
    jq("#groupOrdersForm").submit();
}

/*
 * Display details of selected group order.
 */
function selectedGroupOrder(groupID){
    jq("#groupID").val(groupID);
    jq("#orderNumber").val("");
    jq("#ordererName").val("");
    jq("#groupOrdersForm").submit();
}

/*
 * Display details of selected single order.
 */
function selectedSingleOrder(orderID){
    jq("#orderID").val(orderID);
    jq("#orderNumber").val("");
    jq("#ordererName").val("");
    jq("#groupOrdersForm").submit();
}

/*
 * Show appropriate fields when one of the actions (Dispatch, On-Hold, Discard) is selected.
 */
function showPharmaConfirmationSection(action){
    jq("#statusLabel").show();
    jq("#statusLabel").css("display", "block");
    
    jq("#selectedAction").text(action);
    jq("#pharmaGroupAction").val(action);
    jq("#pharmaGroupButtons").hide();
    
    if(action === "On Hold" || action === "Discard"){
        jq("#pharmaGroupActionButtons").show();
        jq("#groupComments").attr("required", true);
        jq("#pharmaGroupActionButtons").css("display", "block");
        
        jq('.commentForPatient').each(function() {
            jq(this).attr("required", false);
        });
        
        jq('.drugExpiryDate').each(function() {
            jq(this).attr("required", false);
        });
    }
    
    if(action === "Dispatch"){
        jq('.commentForPatient').each(function() {
            jq(this).attr("required", true);
        });
        
        jq('.drugExpiryDate').each(function() {
            jq(this).attr("required", true);
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
    
    jq('.commentForPatient').each(function() {
        jq(this).val("");
    });
    
    jq('.drugExpiryDate').each(function() {
        jq(this).val("");
    });
    
    jq("#pharmaGroupButtons").show();
    jq("#pharmaGroupButtons").css("display", "block");
}

/*
 * Clear the Patient name search field.
 */
function clearPatientTableFilters(){
    jq("#patient_full_name").val("");
    jq("#searchByPatient").submit();
}

/*
 * Hide fragment displaying the details of selected order. (Clear selection)
 */
function closePharmaGroupView(){
    jq("#pharmaGroupView").hide();
    jq("#pharmaSingle").val();
    jq("#pharmaGroup").val();
    jq("#pharmaPlan").val();
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
    jq(".groupCheckBox").each(function(){
        if(this.checked){
            checked++;
            var drug = jq(this).nextAll('.checkedDrug').text();
            selected = selected + drug + ", ";
        } else {
            var drug = jq(this).nextAll('.checkedDrug').text();
            notSelected = notSelected + drug + ", ";
        }
    });
    
    selected = selected.substring(0, selected.length - 2);
    notSelected = notSelected.substring(0, notSelected.length - 2);
    
    if(list.length !== checked)
        message = "Please note that the remaining Order(s) for "+notSelected+" are available for sale. Please confirm if these can be dispensed.";
    
    var comments = jq("#groupComments").val();
    var selectedAction = jq("#selectedAction").text();
    document.getElementById("emailLink").href = "mailto:<Recipient Address<>>?Subject=Order Status Alert!!! "+selectedAction+"&body=This is to inform you that the following Order(s) for "+selected+" cannot be dispatched.%0AStatus Set: "+selectedAction+"%0A%0APatient ID:   "+patientID+"%0APatient Name: "+patientName+"%0APatient DOB:  "+patientDOB+"%0APatient Addr: "+patientAddr+"%0A%0A"+orderDetails+"%0AComments: "+comments+"%0A%0A"+message;
}