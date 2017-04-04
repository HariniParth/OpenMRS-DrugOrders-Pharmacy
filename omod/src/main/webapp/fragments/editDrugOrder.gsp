<%
    ui.includeCss("drugorders", "drugorders.css")
    def selectedDisease = "";
    def allergic_order = "";
%>

<div id="showOrderWindow" class="dialog">

    <div class="dialog-header">
        <h3 id="dialog-heading"><label id="activeOrderAction"></label></h3>
    </div><br/>
    
    <div id="singleOrderView">
        <div class="fields" id="view_order_detail">
            <div id="order_label">
                <label><strong>Order</strong></label>
            </div>
            <div id="order_value">
                <label id="order_details"></label>
            </div>
        </div><br/>

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
        
        <div id="allergicOrderReasonView">
            <div class="fields" id="view_order_detail">
                <div id="order_label">
                    <label><strong>Allergy Note</strong></label>
                </div>
                <div id="order_value">
                    <label id="order_reason"></label>
                </div>
            </div><br/>
        </div>
        
        <div class="fields" id="view_order_detail">
            <label><strong>Instructions from Physician for</strong></label>
        </div><br/>

        <div id="patientInstructions">
            <div class="fields" id="view_order_detail">
                <div id="order_label">
                    <label><strong>Patient</strong></label>
                </div>
                <div id="order_value">
                    <label id="patient_instructions"></label>
                </div>
            </div><br/>
        </div>

        <div id="pharmacistInstructions">
            <div class="fields" id="view_order_detail">
                <div id="order_label">
                    <label><strong>Pharmacist</strong></label>
                </div>
                <div id="order_value">
                    <label id="pharmacist_instructions"></label>
                </div>
            </div><br/>
        </div>
        
        <div id="pharmacistCommentsView">
            <div class="fields" id="view_order_detail">
                <label><strong>Pharmacist's Comments for</strong></label><br/><br/>
                <div id="order_label">
                    <label><strong>Physician</strong></label>
                </div>
                <div id="order_value">
                    <label id="pharma_comments"></label>
                </div>
            </div><br/><br/>
        </div>

        <div id="btn-place">
            <button class="cancel right" onclick="hideDrugOrderViewWindow()">${ ui.message("Close") }</button>
        </div>
        
    </div>
</div>
    

<% if(groupMain.size() > 0) { %>

    <div id="showGroupOrderWindow" class="dialog">
        <form method="post">
            
            <div class="dialog-header">
                <span id="dialog-heading"><h3 id="groupOrderAction">${ groupOrderAction }</h3></span>
            </div>
            <h4 class="align-center"><strong>Selected Order(s)</strong></h4><br/>

            <div class="fields" id="groupOrderBlock">
                <% groupMain.each { order -> %>
                    <div class="groupDrugName" id="view_order_detail">
                        <input type="checkbox" class="groupCheckBox" name="groupCheckBox" value="${ order.key }" checked="true" />
                        <i class="icon-plus-sign  edit-action" title="${ ui.message("Show") }"></i>
                        <i class="icon-minus-sign edit-action" title="${ ui.message("Hide") }"></i>
                        <strong>${ groupExtn.get(order.key).drugName.getDisplayString() }</strong>
                    </div>
                    
                    <div class="drugDetails">${ order.value.dose } ${ order.value.doseUnits.getDisplayString() }, ${ order.value.quantity } ${ order.value.quantityUnits.getDisplayString() }</div><br/>
                        
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
            
            <div id="view_order_detail" class="fields">
                <div id="discontinueReasonSelect">
                    <label><strong>Select the reason to discontinue</strong></label>
                
                    <select id="discontinueReasonCoded" name="discontinueReasonCoded" onchange="discontinueReason()">
                        <option value="">Choose option</option>
                        <% discontinueReasons.each { discontinueReason -> %>
                            <option value="${ discontinueReason.getDisplayString() }">${ discontinueReason.getDisplayString() }</option>
                        <% } %>
                    </select>
                </div>
                
                <div id="discontinueReasonText">
                    <label><strong>Enter the reason to discontinue</strong></label>
                    <input type="textarea" maxlength="30" id="discontinueReasonNonCoded" name="discontinueReasonNonCoded" />
                </div><br/>
            </div>
            
            <div id="btn-place">
                <button class="confirm pull-right" type="submit" id="orderActionButton">Confirm</button>
                <button class="cancel pull-left" type="button" onclick="hideGroupOrderWindow()">Cancel</button>
            </div><br/>
        </form>
    </div>    
<% } %>

<script type="text/javascript">
    jq(".icon-plus-sign").click(function(){
        jq(this).parent().nextAll(".groupBlock").first().show();
        jq(this).hide();
        jq(this).nextAll(".icon-minus-sign").show();
    });
</script>

<script type="text/javascript">
    jq(".icon-minus-sign").click(function(){
        jq(this).parent().nextAll(".groupBlock").first().hide();
        jq(this).hide();
        jq(this).prevAll(".icon-plus-sign").show();
    });
</script>