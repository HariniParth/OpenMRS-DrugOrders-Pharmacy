<%
    ui.includeCss("drugorders", "pharmacy.css")
    def list_of_orders = "";
    def provider_name = "";
    def allergic_order = "";
    def last_dispatch_date = "";
%>

<% if(!planID.equals("") || !groupID.equals("") || !orderID.equals("")) { %>
    <div id="pharmaGroupView" class="dialog">

        <form method="post" id="groupActionForm">

            <div class="dialog-header">
                <h3 id="dialog-heading">${ ui.message("Record Action") }</h3>
            </div>

            <h4 id="heading"><strong>Selected Order(s)</strong></h4><br/>
            
            <div class="group">
                <% groupOrderMain.each { groupOrder -> %>
                    <% list_of_orders = list_of_orders + groupOrder.key + "," %>
                    
                    <div class="groupItem">
                                
                        <div id="view_order_detail">
                            <span id="drugName">
                                <h5><input type="checkbox" class="groupCheckBox" name="groupCheckBox" value="${ groupOrder.key }" ng-model="groupCheckBox" checked="checked" /></span>
                                    <i class="icon-plus-sign edit-action" title="${ ui.message("Show") }"></i>
                                    <i class="icon-minus-sign edit-action" title="${ ui.message("Hide") }"></i>
                                    <strong>${ groupOrderExtn.get(groupOrder.key).drugName.getDisplayString() }</strong>
                                </h5>
                            </span>
                        </div>

                        <div class="fields" id="drugDetails">Dose: ${ groupOrder.value.dose } ${ groupOrder.value.doseUnits.getDisplayString() }, Quantity: ${ groupOrder.value.quantity } ${ groupOrder.value.quantityUnits.getDisplayString() }</div>
                        
                        <div class="additionalInformation">
                            
                            <div class="fields" id="view_order_detail">
                                <div id="order_label">Details</div>
                                <div id="order_value">Route: ${ groupOrder.value.route.getDisplayString() }, Duration: ${ groupOrder.value.duration } ${ groupOrder.value.durationUnits.getDisplayString() }, Frequency: ${ groupOrder.value.frequency }</div>
                            </div>
                            
                            <div class="fields" id="view_order_detail">
                                <div id="order_label">Diagnosis</div>
                                <div id="order_value">${ groupOrderExtn.get(groupOrder.key).associatedDiagnosis.getDisplayString() }</div>
                            </div>
                            
                            <% if(groupOrderExtn.get(groupOrder.key).isAllergicOrderReasons != null) { %>
                                <% allergic_order = groupOrderExtn.get(groupOrder.key).isAllergicOrderReasons; %>
                            <% } else { %>
                                <% allergic_order = "NA"; %>
                            <% } %>
                            
                            <div class="fields" id="view_order_detail">
                                <div id="order_label">Allergy</div>
                                <div id="order_value">${ allergic_order }</div>
                            </div>
                            
                            <div class="fields" id="view_order_detail">
                                <div id="order_label">Refills</div>
                                <div id="order_value">${ groupOrderExtn.get(groupOrder.key).refill }</div>
                            </div>
                            
                            <div class="fields" id="view_order_detail">
                                <div id="order_label">Interval</div>
                                <div id="order_value">${ groupOrderExtn.get(groupOrder.key).refillInterval } (days)</div>
                            </div>
                                                        
                            <% if(groupOrderExtn.get(groupOrder.key).lastDispatchDate != null) { %>
                                <% last_dispatch_date = groupOrderExtn.get(groupOrder.key).lastDispatchDate.format('yyyy-MM-dd'); %>
                            <% } else { %>
                                <% last_dispatch_date = "NA"; %>
                            <% } %>

                            <div class="fields" id="view_order_detail">
                                <div id="order_label">Last Refill</div>
                                <div id="order_value">${ last_dispatch_date }</div>
                            </div>
                            
                            <div class="fields" id="view_order_detail">
                                <div id="order_label">Instructions</div>
                                <div id="order_value">-</div>
                            </div>

                            <div class="fields" id="view_order_detail">
                                <div id="order_label">Patient</div>
                                <div id="order_value">${ groupOrderExtn.get(groupOrder.key).patientInstructions }</div>
                            </div>

                            <div class="fields" id="view_order_detail">
                                <div id="order_label">Pharmacy</div>
                                <div id="order_value">${ groupOrderExtn.get(groupOrder.key).pharmacistInstructions }</div>
                            </div><br/>
                            
                        </div>
                        
                        <div class="dispatchFields">
                            <div class="fields" id="view_order_detail">
                                <div id="order_label"><label>Expiry<span id="asterisk">*</span></label></div>
                                <div id="order_value">${ ui.includeFragment("uicommons", "field/datetimepicker", [ class: 'drugExpiryDate', label: '', formFieldName: 'drugExpiryDate', useTime: '', defaultDate: expiryDate ]) }</div>
                            </div>
                                
                            <br/><br/>
                            
                            <div class="fields" id="view_order_detail">
                                <div id="order_label"><label>Note<span id="asterisk">*</span></label></div>
                                <div id="order_value"><input type="text" class="commentForPatient" value=" " name="commentForPatient"></div>
                            </div>
                            
                            <br/><br/>
                        </div>
                    </div>
                    <% provider_name = provider.get(groupOrder.key) %>
                <% } %>
            </div>
            
            <input type="hidden" id="pharmaPlan" value="${ planID }" />
            <input type="hidden" id="pharmaGroup" value="${ groupID }" />
            <input type="hidden" id="pharmaSingle" value="${ orderID }" />
            
            <label class="fields" id="statusLabel"><br/>Order Status: <span id="selectedAction"></span></label>
            
            <div class="fields" id="printLabel">
                Click <a href="#" onclick="submitDispatch()">here</a> to Print Label<br/><br/>
            </div>
            
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
                <div class="fields" id="btn-place">
                    <input class="confirm right" id="confirmBtn1" type="submit" name="action" value="Confirm" />
                    <input class="cancel left" type="button" value="Back" onclick="showPharmaOrderViewSection()" />
                </div>
            </div>
                    
            <span id="pharmaGroupActionButtons">
                <div class="fields">
                    <label>Comments</label>
                </div>
                <div class="fields">
                    <input type="textarea" maxlength="100" id="groupComments" name="groupComments" placeholder="Enter Comments for the Orderer"/><br/>
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
    jq("#pharmaGroupView .icon-plus-sign").hide();
    jq("#pharmaGroupView .icon-minus-sign").show();
    
    jq(".icon-plus-sign").click(function(){
        jq(this).parent().parent().parent().nextAll(".additionalInformation").first().show();
        jq(this).hide();
        jq(this).next(".icon-minus-sign").show();
    });
    
    jq(".icon-minus-sign").click(function(){
        jq(this).parent().parent().parent().nextAll(".additionalInformation").first().hide();
        jq(this).hide();
        jq(this).prev(".icon-plus-sign").show();
    });
</script>