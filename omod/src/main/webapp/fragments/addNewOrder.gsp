<%
    ui.includeCss("drugorders", "drugorders.css")
%>

<div id="medPlanWindow" class="dialog">
    <div class="dialog-header">
        <h3 id="dialog-heading">${ ui.message("SELECT MEDICATION PLAN") }</h3>
    </div><br/>
    
    <div class="addMedicationPlanWindow">
        <form method="post" id="diseaseForm">
            <div class="fields"><label><strong>Enter Plan Name </strong></label><br/>
                <input type="text" id="diseaseName" name="diseaseName" oninput="autoCompletePlan()" />
            </div><br/><br/>            
            <button class="cancel pull-right" id="btn-place" type="button" onclick="hideMedicationPlanOrderWindow()">${ ui.message("Cancel") }</button>
        </form>
    </div>
</div>

<script type="text/javascript">
    jq( function() {
        jq( "#diseaseName" ).autocomplete({
            source: function( request, response ) {
                var results = [];
                jq.getJSON('${ ui.actionLink("getPlanNameSuggestions") }',
                    {
                      'query': request.term, 
                    })
                .success(function(data) {
                    for (index in data) {
                        var item = data[index];
                        results.push(item.name);
                        }
                    response( results );
                })
                .error(function(xhr, status, err) {
                    alert('AJAX error ' + err);
                });
            }
        } )
    });
</script>
        

<% if(medplans.size() > 0) { %>
    <div id="medPlanDetailsWindow" class="dialog">
        <form method="post">    
            
            <div class="dialog-header">
                <h3 id="dialog-heading">${ ui.message("STANDARD MEDICATION PLAN") }</h3>
            </div>
            
            <input type="hidden" id="selectedPlan" name="selectedPlan" value="${ diseaseName }" />
            <h5 class="align-center"><strong>${ diseaseName.toUpperCase() }</strong></h5><br/>
            
            <div id="medPlansBlock" class="fields">
                <% medplans.each { medplan -> %>
                    <div class="planDrugName">
                        <input type="checkbox" class="groupCheckBox" name="groupCheckBox" value="${ medplan.drugId }" checked="true" />
                        <i class="icon-plus-sign  edit-action" title="${ ui.message("Show") }"></i>
                        <i class="icon-minus-sign edit-action" title="${ ui.message("Hide") }"></i>
                        <strong>${ medplan.drugId.getDisplayString() }</strong>
                    </div>
                        
                    <div class="drugDetails">
                        ${ medplan.dose } ${ medplan.doseUnits.getDisplayString() }, ${ medplan.quantity } ${ medplan.quantityUnits.getDisplayString() } <br/>
                        
                        <% if(allergicDrugs.contains(medplan.drugId.getDisplayString())) { %>
                            <br/> NOTE: Patient is allergic to this drug <br/>
                            Enter reasons to order this drug <br/>
                            <input type="textarea" class="allergicPlanItemOrderReason" name="allergicPlanItemOrderReason" />
                        <% } %>
                    </div><br/>
                        
                    <div class="groupBlock">
                        <div id="view_order_detail">
                            <div id="order_label">Dose</div>
                            <div id="order_value">${ medplan.dose }</div>
                        </div>

                        <div id="view_order_detail">
                            <div id="order_label">Dose units</div>
                            <div id="order_value">${ medplan.doseUnits.getDisplayString() }</div>
                        </div>

                        <div id="view_order_detail">
                            <div id="order_label">Route</div>
                            <div id="order_value">${ medplan.route.getDisplayString() }</div>
                        </div>

                        <div id="view_order_detail">
                            <div id="order_label">Quantity</div>
                            <div id="order_value">${ medplan.quantity }</div>
                        </div>

                        <div id="view_order_detail">
                            <div id="order_label">Qnty units</div>
                            <div id="order_value">${ medplan.quantityUnits.getDisplayString() }</div>
                        </div>

                        <div id="view_order_detail">
                            <div id="order_label">Duration</div>
                            <div id="order_value">${ medplan.duration }</div>
                        </div>

                        <div id="view_order_detail">
                            <div id="order_label">Durn units</div>
                            <div id="order_value">${ medplan.durationUnits.getDisplayString() }</div>
                        </div>

                        <div id="view_order_detail">
                            <div id="order_label">Frequency</div>
                            <div id="order_value">${ medplan.frequency }</div>
                        </div>
                    </div>
                <% } %>
            </div>

            <input type="hidden" id="selectMedPlan" name="action" value="selectMedPlan" />
            <button class="confirm pull-right" id="selectPlanButton" type="submit">${ ui.message("Select") }</button>
            <button class="cancel" id="btn-place" type="button" onclick="hideMedicationPlansWindow()">${ ui.message("Cancel") }</button>
        </form>
    </div>
<% } %>

<script type="text/javascript">
    jq(".icon-plus-sign").click(function(){
        jq(this).parent().nextAll(".groupBlock").first().show();
        jq(this).hide();
        jq(this).next(".icon-minus-sign").show();
    });
</script>

<script type="text/javascript">
    jq(".icon-minus-sign").click(function(){
        jq(this).parent().nextAll(".groupBlock").first().hide();
        jq(this).hide();
        jq(this).prev(".icon-plus-sign").show();
    });
</script>