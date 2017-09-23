<%
    ui.includeCss("drugorders", "drugorders.css")
    def allergic_order = "";
%>

<!--
    Fragment displaying the details of the selected drug order.
    This includes drug formulations, start date, refill, priority and instructions.
-->

<div id="viewOrderWindow" class="dialog">

    <div class="dialog-header">
        <h3>VIEW ORDER DETAILS</h3>
    </div>
    <h4 class="align-center"><strong>Selected Order</strong></h4><br/>
    
    <div id="singleOrderView">
        <div class="fields" id="view_order_detail">
            <div id="order_label">
                <label><strong>Drug</strong></label>
            </div>
            <div id="order_value">
                <label id="order_details"></label>
            </div>
        </div><br/><br/><br/><br/><br/>

        <div class="fields" id="view_order_detail">
            <div id="order_label">
                <label><strong>Start Date</strong></label>
            </div>
            <div id="order_value">
                <label id="start_date"></label>
            </div>
        </div>
        
        <br/><br/>
        
        <div class="fields" id="view_order_detail">
            <div id="order_label">
                <label><strong>Duration</strong></label>
            </div>
            <div id="order_value">
                <label id="order_duration"></label>
            </div>
        </div>
        
        <br/><br/>
        
        <div class="fields" id="view_order_detail">
            <div id="order_label">
                <label><strong>Diagnosis</strong></label>
            </div>
            <div id="order_value">
                <label id="order_diagnosis"></label>
            </div>
        </div>
        
        <br/><br/>
        
        <div class="fields" id="view_order_detail">
            <div id="order_label">
                <label><strong>Priority</strong></label>
            </div>
            <div id="order_value">
                <label id="order_priority"></label>
            </div>
        </div>
        
        <br/><br/>
        
        <div class="fields" id="view_order_detail">
            <div id="order_label">
                <label><strong>Refills</strong></label>
            </div>
            <div id="order_value">
                <label id="order_refills"></label>
            </div>
        </div>
        
        <br/><br/>
        
        <div class="fields" id="view_order_detail">
            <div id="order_label">
                <label><strong>Interval</strong></label>
            </div>
            <div id="order_value">
                <label id="refill_interval"></label>
            </div>
        </div>
        
        <br/><br/>
        
        <div id="allergicOrderReasonView">
            <div class="fields" id="view_order_detail">
                <label><strong>Allergic Drug Order Note</strong></label>
                <label id="order_reason"></label>
            </div><br/>
        </div>
        
        <div class="fields" id="view_order_detail">
            <label><strong>Instructions for the Patient</strong></label>
            <label id="patient_instructions"></label>
        </div><br/>

        <div class="fields" id="view_order_detail">
            <label><strong>Instructions for the Pharmacist</strong></label>
            <label id="pharmacist_instructions"></label>
        </div><br/>
        
        <div id="pharmacistCommentsView">
            <div class="fields" id="view_order_detail">
                <label><strong>Pharmacist's Comments</strong></label>
                <label id="pharma_comments"></label>
            </div><br/>
        </div>
        
        --------------------------------------------------------------
        
        <div class="fields" id="view_order_detail">
            <div id="order_label">
                <label><strong>Orderer</strong></label>
            </div>
            <div id="order_value">
                <label id="orderer"></label>
            </div><br/>
        </div><br/>
    </div><br/>
    <button class="cancel right" id="btn-place" onclick="hideDrugOrderViewWindow()">${ ui.message("Close") }</button>
</div>
    
<!--
    Form that displays the details of the orders selected to be discontinued or renewed.
    This includes individual, group and plan orders that are selected to be discontinued or renewed.
    The form provides check-boxes corresponding to each drug to select it to be discontinued or renewed.
    The Physician then enters reasons for discarding the order.
-->
    
<% if(groupMain.size() > 0) { %>

    <div id="showGroupOrderWindow" class="dialog">
        <form method="post">
            
            <div class="dialog-header">
                <h3 id="groupOrderAction">${ groupOrderAction }</h3>
            </div>
            <h4 class="align-center"><strong>Selected Order(s)</strong></h4><br/>

            <div class="fields" id="groupOrderBlock">
                <% groupMain.each { order -> %>
                    <div class="groupDrugName" id="view_order_detail">
                        <!--
                            Disable options to renew a drug order if an active order for that drug exists.
                        -->
                
                        <% if((groupOrderAction == "RENEW MED PLAN" || groupOrderAction == "RENEW ORDER GROUP") && currentOrders.contains(groupExtn.get(order.key).drugName.getDisplayString().toUpperCase())) { %>
                            <input type="checkbox" class="unchecked" disabled="disabled" />
                        <% } else { %>
                            <input type="checkbox" class="groupCheckBox" name="groupCheckBox" value="${ order.key }" checked="true" />
                        <% } %>
                        
                        <i class="icon-plus-sign  edit-action" title="${ ui.message("Show") }"></i>
                        <i class="icon-minus-sign edit-action" title="${ ui.message("Hide") }"></i>
                        <strong>${ groupExtn.get(order.key).drugName.getDisplayString().toUpperCase() }</strong>
                    </div>
                    
                    <div class="drugDetails">
                        ${ order.value.dose } ${ order.value.doseUnits.getDisplayString() }, ${ order.value.quantity } ${ order.value.quantityUnits.getDisplayString() } <br/>
                        
                        <!--
                            Display a note if an active order for that drug exists.
                        -->
                        
                        <% if((groupOrderAction == "RENEW MED PLAN" || groupOrderAction == "RENEW ORDER GROUP") && currentOrders.contains(groupExtn.get(order.key).drugName.getDisplayString().toUpperCase())) { %>
                            <div id="view_order_detail">
                                <label>Note: Drug is currently prescribed to this patient.</label>
                                <label>Cannot place multiple orders for the same drug.</label>
                            </div>
                        <% } %>
                            
                        <!--
                            Display a note if Patient is allergic to the drug.
                            Display a field to enter the reason to order the allergic drug.
                        -->
                        
                        <% if((groupOrderAction == "RENEW MED PLAN" || groupOrderAction == "RENEW ORDER GROUP") && allergicDrugs.contains(groupExtn.get(order.key).drugName.getDisplayString().toUpperCase()) && !currentOrders.contains(groupExtn.get(order.key).drugName.getDisplayString().toUpperCase())) { %>
                            <br/> NOTE: Patient is allergic to this drug <br/>
                            Enter reasons to order this drug <br/>
                            <textarea maxlength="912" class="reviseOrderReason" name="reviseOrderReason" placeholder="Enter the reason to order" required="required"></textarea>
                        <% } %>
                    </div><br/>
                        
                    <div class="groupBlock">
                        <div id="view_order_detail">
                            <div id="order_label">Dose</div>
                            <div id="order_value">${ order.value.dose }</div>
                        </div>
                        
                        <div id="view_order_detail">
                            <div id="order_label">Dose units</div>
                            <div id="order_value">${ order.value.doseUnits.getDisplayString() }</div>
                        </div>
                        
                        <div id="view_order_detail">
                            <div id="order_label">Route</div>
                            <div id="order_value">${ order.value.route.getDisplayString() }</div>
                        </div>
                        
                        <div id="view_order_detail">
                            <div id="order_label">Quantity</div>
                            <div id="order_value">${ order.value.quantity }</div>
                        </div>
                        
                        <div id="view_order_detail">
                            <div id="order_label">Qnty units</div>
                            <div id="order_value">${ order.value.quantityUnits.getDisplayString() }</div>
                        </div>
                        
                        <div id="view_order_detail">
                            <div id="order_label">Duration</div>
                            <div id="order_value">${ order.value.duration }</div>
                        </div>
                        
                        <div id="view_order_detail">
                            <div id="order_label">Durn units</div>
                            <div id="order_value">${ order.value.durationUnits.getDisplayString() }</div>
                        </div>
                        
                        <div id="view_order_detail">
                            <div id="order_label">Frequency</div>
                            <div id="order_value">${ order.value.frequency }</div>
                        </div>
                        
                        <div id="view_order_detail">
                            <div id="order_label">Diagnosis</div>
                            <div id="order_value">${ groupExtn.get(order.key).associatedDiagnosis.getDisplayString() }</div>
                        </div>
                        
                        <% if(groupExtn.get(order.key).isAllergicOrderReasons != null) { %>
                            <% allergic_order = groupExtn.get(order.key).isAllergicOrderReasons; %>
                        <% } else { %>
                            <% allergic_order = "NA"; %>
                        <% } %>
                            
                        <div id="view_order_detail">
                            <div id="order_label">Allergy</div>
                            <div id="order_value">${ allergic_order }</div>
                        </div>
                    </div>
                <% } %>            
            </div>
            
            <input type="hidden" id="groupOrderID" name="groupOrderID" value="${ group }" />
            <input type="hidden" id="groupAction" name="action" value="${ groupOrderAction }" />
            
            <% if(groupOrderAction == "DISCONTINUE ORDER") { %>
                <input type="hidden" id="orderStatus" name="orderStatus" value="${ orderStatus }" />
            <% } %>
            
            <div id="view_order_detail" class="fields">
                <div id="discontinueReasonSelect">
                    <label><strong>Select the reason to discontinue</strong></label>
                
                    <select id="codedDiscardReason" name="codedDiscardReason" onchange="discontinueReason()">
                        <option value="">Choose option</option>
                        <% discontinueReasons.each { discontinueReason -> %>
                            <option value="${ discontinueReason.getDisplayString() }">${ discontinueReason.getDisplayString() }</option>
                        <% } %>
                    </select>
                </div>
                
                <div id="discontinueReasonText">
                    <label><strong>Enter the reason to discontinue</strong></label>
                    <textarea maxlength="912" id="nonCodedDiscardReason" name="nonCodedDiscardReason" placeholder="Enter the reason to discontinue"></textarea>
                </div><br/>
            </div>
            
            <div id="btn-place">
                <button class="confirm pull-right" type="submit" id="orderActionButton">Confirm</button>
                <button class="cancel pull-left" type="button" onclick="hideGroupOrderWindow()">Cancel</button>
            </div><br/>
        </form>
    </div>    
    
    <script type="text/javascript">
        jq("#showGroupOrderWindow").show();
        
        <!--Show the details of the given drug order-->
        jq(".icon-plus-sign").click(function(){
            jq(this).parent().nextAll(".groupBlock").first().show();
            jq(this).hide();
            jq(this).nextAll(".icon-minus-sign").show();
        });

        <!--Hide the details of the given drug order-->
        jq(".icon-minus-sign").click(function(){
            jq(this).parent().nextAll(".groupBlock").first().hide();
            jq(this).hide();
            jq(this).prevAll(".icon-plus-sign").show();
        });
    </script>
<% } %>