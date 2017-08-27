<%
    ui.includeCss("drugorders", "pharmacy.css")
    def order_id = "";
    def allergic_order = "";
    def last_dispatch_date = "";
    def patient_instr = "";
    def pharma_instr = "";
%>

<!--
    Pharmacy fragment displaying the details of the selected individual, group or plan drug orders.
    The fragment provides buttons (options) to Dispatch orders, put orders On-Hold and Discard orders.
    The fragment displays fields to enter comments based on the selected action.
-->

<% if(!planID.equals("") || !groupID.equals("") || !orderID.equals("")) { %>
    <div id="pharmaGroupView" class="dialog">

        <form method="post" id="groupActionForm">

            <div class="dialog-header">
                <h3>${ ui.message("Record Action") }</h3>
            </div>

            <h4 id="heading"><strong>Selected Order(s)</strong></h4><br/>
            
            <div class="group">
                <% groupOrderMain.each { groupOrder -> %>   
                    <% if(order_id == "") { %>
                        <% order_id = groupOrder.key; %>
                    <% } %>
                    
                    <div class="groupItem">
                                
                        <div id="view_order_detail">
                            <h5 id="nameField">
                                <input type="checkbox" class="groupCheckBox" name="groupCheckBox" value="${ groupOrder.key }" ng-model="groupCheckBox" checked="checked" /></span>
                                <i class="icon-plus-sign edit-action" title="${ ui.message("Show") }"></i>
                                <i class="icon-minus-sign edit-action" title="${ ui.message("Hide") }"></i>
                                <span class="checkedDrug"><strong>${ groupOrderExtn.get(groupOrder.key).drugName.getDisplayString().toUpperCase() }</strong></span>
                            </h5>
                        </div>

                        <div class="drugDetails">
                            
                            <span class="fields">Dose: ${ groupOrder.value.dose } ${ groupOrder.value.doseUnits.getDisplayString() }, Quantity: ${ groupOrder.value.quantity } ${ groupOrder.value.quantityUnits.getDisplayString() }</span><br/><br/>
                        
                            <% if(groupOrderExtn.get(groupOrder.key).pharmacistInstructions != null && groupOrderExtn.get(groupOrder.key).pharmacistInstructions != "null") { %>
                                <% pharma_instr = groupOrderExtn.get(groupOrder.key).pharmacistInstructions; %>
                            <% } else { %>
                                <% pharma_instr = "-"; %>
                            <% } %>

                            <div class="fields" id="view_order_detail">
                                <label><strong>Instructions for the Pharmacist:</strong></label>
                                ${ pharma_instr.replace("newline","<br/>") }
                            </div>
                            
                            <% if(groupOrderExtn.get(groupOrder.key).patientInstructions != null && groupOrderExtn.get(groupOrder.key).patientInstructions != "null") { %>
                                <% patient_instr = groupOrderExtn.get(groupOrder.key).patientInstructions; %>
                            <% } else { %>
                                <% patient_instr = "-"; %>
                            <% } %>

                            <div class="fields" id="view_order_detail">
                                <label><strong>Instructions for the Patient:</strong></label>
                                ${ patient_instr.replace("newline","<br/>") }
                            </div>
                                
                            <!--
                                Display fields to enter drug expiry date and a note for the Patient if orders are selected to be dispatched.
                            -->
                            <div class="dispatchFields"><br/>
                                <div class="fields" id="view_order_detail">
                                    <label>Note<span id="asterisk">*</span></label>
                                    <textarea maxlength="912" class="commentForPatient" name="commentForPatient" placeholder="Enter notes for Patient" required="required"></textarea>
                                </div><br/>
                                
                                <div class="fields" id="view_order_detail">
                                    <div id="order_label"><label>Expiry<span id="asterisk">*</span></label></div>
                                    <div id="order_value"><input type="text" class="drugExpiryDate" name="drugExpiryDate" placeholder="MM/DD/YYYY" pattern="[0-9]{2}/[0-9]{2}/[0-9]{4}" required="required" ></div>
                                </div><br/><br/><br/>
                            </div>
                            
                            <div class="additionalInformation"><br/>
                                <div class="fields" id="view_order_detail">
                                    <div id="order_label">Route</div>
                                    <div id="order_value">${ groupOrder.value.route.getDisplayString() } </div>
                                </div><br/>
                                
                                <div class="fields" id="view_order_detail">
                                    <div id="order_label">Duration</div>
                                    <div id="order_value">${ groupOrder.value.duration } ${ groupOrder.value.durationUnits.getDisplayString() }</div>
                                </div><br/>
                                
                                <div class="fields" id="view_order_detail">
                                    <div id="order_label">Frequency</div>
                                    <div id="order_value">${ groupOrder.value.frequency }</div>
                                </div><br/>

                                <div class="fields" id="view_order_detail">
                                    <div id="order_label">Diagnosis</div>
                                    <div id="order_value">${ groupOrderExtn.get(groupOrder.key).associatedDiagnosis.getDisplayString() }</div>
                                </div><br/>

                                <div class="fields" id="view_order_detail">
                                    <div id="order_label">Refills</div>
                                    <div id="order_value">${ groupOrderExtn.get(groupOrder.key).refill }</div>
                                </div><br/>

                                <div class="fields" id="view_order_detail">
                                    <div id="order_label">Interval</div>
                                    <div id="order_value">${ groupOrderExtn.get(groupOrder.key).refillInterval } (days)</div>
                                </div><br/>

                                <% if(groupOrderExtn.get(groupOrder.key).lastDispatchDate != null) { %>
                                    <% last_dispatch_date = groupOrderExtn.get(groupOrder.key).lastDispatchDate.format('yyyy-MM-dd'); %>
                                <% } else { %>
                                    <% last_dispatch_date = "NA"; %>
                                <% } %>

                                <div class="fields" id="view_order_detail">
                                    <div id="order_label">Last Refill</div>
                                    <div id="order_value">${ last_dispatch_date }</div>
                                </div><br/>
                                
                                <% if(groupOrderExtn.get(groupOrder.key).isAllergicOrderReasons != null) { %>
                                    <% allergic_order = groupOrderExtn.get(groupOrder.key).isAllergicOrderReasons; %>
                                <% } else { %>
                                    <% allergic_order = "NA"; %>
                                <% } %>

                                <div class="fields" id="view_order_detail">
                                    <label>Allergic Drug Order Note: </label>
                                    ${ allergic_order.replace("newline","<br/>") }
                                </div><br/>
                                
                            </div><br/>
                            <span class="hidden" id="order">${ groupOrder.key }</span>
                            <span class="print">Print Label</span><br/>
                        
                            <script type="text/javascript">
                                jq( function() {
                                    jq(".print").unbind();
                                    jq(".print").on('click', function (){
                                        var order = jq(this).prev('#order').text();
                                        var date = jq(this).parent().find('.dispatchFields').first().find('.fields').last().find('#order_value').find('.drugExpiryDate').val();
                                        var comment = jq(this).parent().find('.dispatchFields').first().find('.fields').find('.commentForPatient').val();
                                        
                                        jq.getJSON('${ ui.actionLink("printLabel") }',
                                        {
                                          'comment': comment,
                                          'order': order,
                                          'date': date
                                        });
                                    });
                                });
                            </script>
                        </div>
                    </div>
                <% } %>
            </div>
            
            <input type="hidden" id="pharmaPlan" value="${ planID }" />
            <input type="hidden" id="pharmaGroup" value="${ groupID }" />
            <input type="hidden" id="pharmaSingle" value="${ orderID }" />
            
            <label class="fields" id="statusLabel"><br/>Order Status: <span id="selectedAction"></span></label>
            
            <span id="pharmaGroupButtons">
                <div class="fields" id="btn-place">
                    <input class="confirm right" type="button" value="Dispatch" onclick="showPharmaConfirmationSection('Dispatch')" /> <br/><br/>
                    <input class="confirm right" type="button" value="On Hold" onclick="showPharmaConfirmationSection('On Hold')" /> <br/><br/>
                    <input class="confirm right" type="button" value="Discard" onclick="showPharmaConfirmationSection('Discard')" />
                    <input class="cancel left"   type="button" value="Cancel" onclick="closePharmaGroupView()"  />
                </div>
            </span>
            
            <input type="hidden" id="pharmaGroupAction" name="pharmaGroupAction" />
            
            <div id="confirmButtons">
                <br/>
                <div class="fields" id="btn-place">
                    <input class="confirm right" id="confirmBtn1" type="submit" name="action" value="Confirm" />
                    <input class="cancel left" type="button" value="Back" onclick="showPharmaOrderViewSection()" />
                </div>
            </div>
                    
            <!--
                Display fields to enter comments for the Orderer when orders are selected to be put on hold or discarded.
            -->
            
            <span id="pharmaGroupActionButtons">
                <div class="fields">
                    <label>Comments</label>
                </div>
                <div class="fields">
                    <textarea maxlength="912" id="groupComments" name="groupComments" placeholder="Enter comments for the Orderer"></textarea><br/>
                    <a href="#" id="emailLink" onclick="emailLink('${ orderList }','${ patientID }','${ patientName }','${ patientDOB.format('yyyy-MM-dd') }','${ patientAddr }','${ orderDetails }')" target="_top">Send a message to the Orderer</a> <br/><br/>
                    
                    <div id="btn-place">
                        <input class="confirm right" id="confirmBtn2" type="submit" name="action" value="Confirm" />
                        <input class="cancel" value="Back" type="button" onclick="showPharmaOrderViewSection()" />
                    </div>
                </div>
            </span>            
        </form>
    </div>
<% } %>

<script type="text/javascript">    
    <!--Show the details of the given drug order-->
    jq(".icon-plus-sign").click(function(){
        jq(this).parent().parent().nextAll(".drugDetails").first().children(".additionalInformation").show();
        jq(this).hide();
        jq(this).next(".icon-minus-sign").show();
    });
    
    <!--Hide the details of the given drug order-->
    jq(".icon-minus-sign").click(function(){
        jq(this).parent().parent().nextAll(".drugDetails").first().children(".additionalInformation").hide();
        jq(this).hide();
        jq(this).prev(".icon-plus-sign").show();
    });
    
    jq('.drugExpiryDate').datepicker({
        minDate:  new Date()
    });
    
    jq('.drugExpiryDate').on('keydown keyup', function(e){
        e.preventDefault();
    });
</script>