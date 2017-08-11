<%
    ui.includeCss("drugorders", "drugorders.css")
    ui.includeJavascript("drugorders", "drugorders.js")
%>

<!--
    Form to define a medication plan by entering the plan name and description.
-->

<div id="definePlanWindow" class="dialog">
    <div class="dialog-header">
        <h3 id="adminPlanActionType"></h3>
    </div><br/>
    
    <div class="fields">
        <form id="definePlanForm" method="post">
            <input id="definePlanId" name="definePlanId" type="hidden" />
            
            <div id="view_order_detail">
                <label><strong>Enter Plan Name</strong></label>
                <input type="text" id="definePlanName" name="definePlanName" title="Enter diagnosis name" placeholder="Enter Diagnosis Name" />
            </div><br/>
            
            <div id="view_order_detail">
                <label><strong>Enter Description</strong></label>
                <textarea maxlength="225" id="definePlanDesc" name="definePlanDesc" placeholder="Enter a description of the plan" required="required"></textarea>
            </div><br/>
            
            <input type="hidden" name="action" id="defineAction" />
            <button class="confirm right" id="planDefineButton" type="submit">${ ui.message("Confirm") }</button>
            <button class="cancel left" type="button" onclick="hideMedPlanDefineWindow()">${ ui.message("Cancel") }</button><br/><br/>
        </form>
    </div>
</div>

<!--
    Form to add drugs to a medication plan.
    For each drug, the standard (general) composition and consumption instructions are specified.
    This includes drug name, dose, quantity, duration and frequency.
-->
            
<div id="createPlanWindow" class="dialog">
    <div class="dialog-header">
        <h3 id="adminActionType"></h3>
    </div><br/>
    
    <div class="fields">
        <form id="createPlanForm" method="post">
            
            <div id="disease_field">
                <label><strong>Enter Plan Name</strong></label>
                <input id="adminPlan" type="text" name="adminPlan" />
            </div><br/>
            
            <p><strong>Specify Standard Formulation</strong></p>
            
            <input type="hidden" id="listOfDrugs">
            
            <div id="view_order_detail">
                <div id="order_label">
                    <label id="label"><strong>Drug Name</strong></label>
                </div>
                <div id="order_value">
                    <input id="adminDrug" type="text" name="adminDrug" oninput="checkListOfDrugs()" title="Enter Drug Name" />
                </div>
            </div>
            
            <br/><br/>

            <div id="view_order_detail">
                <div id="order_label">
                    <label><strong>Route</strong></label>
                </div>
                <div id="order_value">
                    <select id="adminRoute" name="adminRoute">
                        <option value="">Choose option</option>
                        <% routes.each { route -> %>
                            <option value="${ route.getDisplayString() }">${ route.getDisplayString() }</option>
                        <% } %>
                    </select>
                </div>
            </div>
            
            <br/><br/>
            
            <div id="view_order_detail">
                <div id="order_label">
                    <label><strong>Dose</strong></label>
                </div>
                <div id="order_value">
                    <input type="number" id="adminDose" name="adminDose" step="0.1" min="0" title="Specify integer/double dose value" placeholder="Enter Dose" onkeyup="checkAdminFields()" />
                </div>
            </div>

            <br/><br/>
            
            <div id="view_order_detail">
                <div id="order_label">
                    <label><strong>Dose Units</strong></label>
                </div>
                <div id="order_value">
                    <select id="adminDoseUnits" name="adminDoseUnits">
                        <option value="">Choose option</option>
                        <% doses.each { dose -> %>
                            <option value="${ dose.getDisplayString() }">${ dose.getDisplayString() }</option>
                        <% } %>
                    </select>
                </div>
            </div>

            <br/><br/>
            
            <div id="view_order_detail">
                <div id="order_label">
                    <label><strong>Quantity</strong></label>
                </div>
                <div id="order_value">
                    <input type="number" id="adminQuantity" name="adminQuantity" step="0.1" min="0" title="Specify integer/double quantity value" placeholder="Enter Quantity" onkeyup="checkAdminFields()" />
                </div>
            </div>

            <br/><br/>
            
            <div id="view_order_detail">
                <div id="order_label">
                    <label><strong>Qnty Units</strong></label>
                </div>
                <div id="order_value">
                    <select id="adminQuantityUnits" name="adminQuantityUnits" >
                        <option value="">Choose option</option>
                        <% quantities.each { quantity -> %>
                            <option value="${ quantity.getDisplayString() }">${ quantity.getDisplayString() }</option>
                        <% } %>
                    </select>
                </div>
            </div>

            <br/><br/>
            
            <div id="view_order_detail">
                <div id="order_label">
                    <label><strong>Duration</strong></label>
                </div>
                <div id="order_value">
                    <input type="number" id="adminDuration" name="adminDuration" min="1" step="1" onkeypress="return event.charCode >= 48 && event.charCode <= 57" title="Specify integer duration value" placeholder="Enter Duration" onkeyup="checkAdminFields()" />
                </div>
            </div>

            <br/><br/>
            
            <div id="view_order_detail">
                <div id="order_label">
                    <label><strong>Durn Units</strong></label>
                </div>
                <div id="order_value">
                    <select id="adminDurationUnits" name="adminDurationUnits">
                        <option value="">Choose option</option>
                        <% durations.each { duration -> %>
                            <option value="${ duration.getDisplayString() }">${ duration.getDisplayString() }</option>
                        <% } %>
                    </select>
                </div>
            </div>

            <br/><br/>
            
            <div id="view_order_detail">
                <div id="order_label">
                    <label><strong>Frequency</strong></label>
                </div>
                <div id="order_value">
                    <select id="adminFrequency" name="adminFrequency">
                        <option value="">Choose option</option>
                        <% frequencies.each { frequency -> %>
                            <option value="${ frequency.getConcept().getDisplayString() }">${ frequency.getConcept().getDisplayString() }</option>
                        <% } %>
                    </select>
                </div>
            </div>

            <br/><br/><br/>

            <input type="hidden" name="action" value="extendPlan" />
            <button class="confirm right" id="planSaveButton" type="button" onclick="createStandardPlan()">${ ui.message("Confirm") }</button>
            <button class="cancel left" type="button" onclick="hideMedPlanCreateWindow()">${ ui.message("Cancel") }</button><br/><br/>
        </form>
    </div>
</div>

<!--
    Form to discard one, some or all orders defined in a standard medication plan.
    User can check/uncheck the check-box corresponding to each drug to select it to be discarded.
    A reason is provided for discarding the plan.
-->

<% if(selectedPlan.size() > 0) { %>

    <div id="deletePlanWindow" class="dialog">
        <div class="dialog-header">
            <h3>
                <% if(selectedMedPlan != null) { %>
                    ${ ui.message("DISCARD PLAN") }
                <% } else { %>
                    ${ ui.message("DISCARD DRUG") }
                <% } %>
            </h3>
        </div>

        <div>
            <form method="post" id="discardPlanForm">
                
                <% selectedPlan.each { discardPlans -> %>
                    <% discardPlans.value.each { discardPlan -> %>
                        <h4 class="align-center"><strong>Selected Drug(s)</strong></h4>
                        <h6 class="align-center"><strong id="discardPlan">${ discardPlan.key.getDisplayString().toUpperCase() }</strong></h6><br/>
                        
                        <div class="fields" id="discardPlanBlock">
                            
                            <% discardPlan.value.each { plan -> %>
                                
                                <input type="checkbox" class="groupCheckBox" name="groupCheckBox" value="${ plan.id }" checked="true" />
                                <i class="icon-plus-sign  edit-action" title="${ ui.message("Show") }"></i>
                                <i class="icon-minus-sign edit-action" title="${ ui.message("Hide") }"></i>
                                <strong class="discardDrug">${ plan.drugId.getDisplayString().toUpperCase() }</strong><br/><br/>

                                <div class="groupBlock">                
                                    <div id="view_order_detail">
                                        <div id="order_label">Dose</div>
                                        <div id="order_value">${ plan.dose }</div>
                                    </div>

                                    <div id="view_order_detail">
                                        <div id="order_label">Dose units</div>
                                        <div id="order_value">${ plan.doseUnits.getDisplayString() }</div>
                                    </div>

                                    <div id="view_order_detail">
                                        <div id="order_label">Route</div>
                                        <div id="order_value">${ plan.route.getDisplayString() }</div>
                                    </div>

                                    <div id="view_order_detail">
                                        <div id="order_label">Quantity</div>
                                        <div id="order_value">${ plan.quantity }</div>
                                    </div>

                                    <div id="view_order_detail">
                                        <div id="order_label">Qnty units</div>
                                        <div id="order_value">${ plan.quantityUnits.getDisplayString() }</div>
                                    </div>

                                    <div id="view_order_detail">
                                        <div id="order_label">Duration</div>
                                        <div id="order_value">${ plan.duration }</div>
                                    </div>

                                    <div id="view_order_detail">
                                        <div id="order_label">Durn units</div>
                                        <div id="order_value">${ plan.durationUnits.getDisplayString() }</div>
                                    </div>

                                    <div id="view_order_detail">
                                        <div id="order_label">Frequency</div>
                                        <div id="order_value">${ plan.frequency }</div>
                                    </div>
                                </div>
                            <% } %>
                        </div>
                    <% } %>    
                    <input id="planToDiscard" name="planToDiscard" value="${ discardPlans.key }" type="hidden" /> 
                <% } %>
                
                <% if(selectedMedPlan != null) { %>
                    <input id="planDiscard" name="action" value="discardPlan" type="hidden" />
                <% } else { %>
                    <input name="action" value="discardDrug" type="hidden" /> 
                <% } %> <br/>
                
                <div class="fields">
                    <p><strong>Enter Reason To Discard</strong></p>
                    <textarea maxlength="225" id="discardReason" name="discardReason" placeholder="Enter the reason to discard" required="required" onkeyup="allowPlanDiscard()"></textarea>
                </div><br/>

                <div id="btn-place">
                    <button class="confirm right" id="discardAdminPlan" type="button" onclick="discardMedPlan()">${ ui.message("Confirm") }</button>
                    <button class="cancel" type="button" onclick="hideMedPlanDiscardWindow()">${ ui.message("Cancel") }</button>
                </div>
            </form>
        </div>
    </div>
    
    <script type="text/javascript">
        jq("#deletePlanWindow").show();
    </script>
<% } %>

<script type="text/javascript">
    jq( function() {
        jq( "#definePlanName" ).autocomplete({
            source: function( request, response ) {
                var results = [];
                jq.getJSON('${ ui.actionLink("getPlanNameSuggestions") }',
                    {
                      'query': request.term, 
                    })
                .success(function(data) {
                    for (index in data) {
                        var item = data[index];
                        results.push(item.name.toUpperCase());
                    }
                    response( results );
                })
                .error(function(xhr, status, err) {
                    console.log(err);
                });
            },
            response: function(event, ui) {
                if (ui.content.length === 0) {
                    alert("No concept found! Please create a Diagnosis class concept for this plan!");
                }
            }
        } ),
        
        jq( "#adminDrug" ).autocomplete({
            source: function( request, response ) {
                var results = [];
                jq.getJSON('${ ui.actionLink("getDrugNameSuggestions") }',
                    {
                      'query': request.term, 
                    })
                .success(function(data) {
                    for (index in data) {
                        var item = data[index];
                        results.push(item.name.toUpperCase());
                    }
                    response( results );
                })
                .error(function(xhr, status, err) {
                    console.log(err);
                });
            },
            select: function(event, ui) {
                var listOfDrugs = jq("#listOfDrugs").val().split(" ");
                jq.each(listOfDrugs, function(index, value){
                    if(value === ui.item.label.toUpperCase()){
                        alert("Drug is already a part of the plan");
                    }
                });
            },
            response: function(event, ui) {
                if (ui.content.length === 0) {
                    alert("No concept found! Please create a Drug class concept for this drug!");
                }
            }
        } )
    });
    
</script>

<script type="text/javascript">    
    <!--Show the details of the given drug order-->
    jq(".icon-plus-sign").click(function(){
        jq(this).nextAll(".groupBlock").first().show();
        jq(this).hide();
        jq(this).next(".icon-minus-sign").show();
    });
    
    <!--Hide the details of the given drug order-->
    jq(".icon-minus-sign").click(function(){
        jq(this).nextAll(".groupBlock").first().hide();
        jq(this).hide();
        jq(this).prev(".icon-plus-sign").show();
    });
</script>