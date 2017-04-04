<%
    ui.decorateWith("appui", "standardEmrPage");
    ui.includeCss("drugorders", "drugorders.css")
    ui.includeJavascript("drugorders", "drugorders.js")
    ui.includeJavascript("drugorders", "dataTables.js")
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("drugorders.administration") }", link: '/' + OPENMRS_CONTEXT_PATH + '/admin/index.htm' },
        { label: "${ ui.message("drugorders.administrators") }" }
    ];

</script>

<br/>

<div class="info-body">
    
    <div id="planList">
        <div id="line-break"></div>
        <h3>
            <i class="icon-medicine"></i>
            <strong>${ ui.message("AVAILABLE MEDICATION PLANS") }</strong>
            <i class="icon-plus edit-action right" title="${ ui.message("CREATE MEDICATION PLAN") }" onclick="displayPlanCreationWindow()"></i>
        </h3>
        <div id="line-break"></div><br/><br/>
        
        <form method="post" id="adminPageForm">
            <input type="hidden" id="selectedMedPlan" name="selectedMedPlan" />
            <input type="hidden" id="selectedPlanItem" name="selectedPlanItem" />
            
            <table id="medPlansTable">
                <thead>
                    <tr>
                        <th>Plan Name</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <% if(newPlans.size() == 0) { %>
                        <tr><td colspan="2" align="center">No Plans Found</td></tr>
                    <% } %>

                    <% if(newPlans.size() > 0) { %>
                        <div>
                            <% newPlans.each { newPlan -> %>
                                <tr>
                                    <td class="planDetails">
                                        <div class="fields">
                                            <i class="icon-plus-sign edit-action" title="${ ui.message("View Details") }"></i>
                                            <i class="icon-minus-sign edit-action" title="${ ui.message("Hide Details") }"></i>
                                            <strong class="planName">${ newPlan.planName.getDisplayString().toUpperCase() }</strong>
                                        </div>

                                        <div class="desc">${ newPlan.planDesc }</div><br/>
                                        
                                        <div class="plansDetailsView">
                                            <% medPlan = allMedicationPlans.get(newPlan.planName) %>
                                            <% if(medPlan.size() > 0) { %>
                                                <% medPlan.each { med -> %>
                                                    <div class="planBlock">
                                                        <div class="planBlockDetails">
                                                            <div>
                                                                <i class="show-details icon-plus-sign edit-action" title="${ ui.message("View Details") }"></i>
                                                                <i class="hide-details icon-minus-sign icon-minus-sign edit-action" title="${ ui.message("Hide Details") }"></i>
                                                                <strong class="planItem">${ med.drugId.getDisplayString().toUpperCase() }</strong>
                                                            </div><br/>

                                                            <div class="groupBlock">
                                                                <div id="view_order_detail">
                                                                    <div id="order_label">Dose</div>
                                                                    <div id="order_value">${ med.dose }</div>
                                                                </div>

                                                                <div id="view_order_detail">
                                                                    <div id="order_label">Dose units</div>
                                                                    <div id="order_value">${ med.doseUnits.getDisplayString() }</div>
                                                                </div>

                                                                <div id="view_order_detail">
                                                                    <div id="order_label">Route</div>
                                                                    <div id="order_value">${ med.route.getDisplayString() }</div>
                                                                </div>

                                                                <div id="view_order_detail">
                                                                    <div id="order_label">Quantity</div>
                                                                    <div id="order_value">${ med.quantity }</div>
                                                                </div>

                                                                <div id="view_order_detail">
                                                                    <div id="order_label">Qnty units</div>
                                                                    <div id="order_value">${ med.quantityUnits.getDisplayString() }</div>
                                                                </div>

                                                                <div id="view_order_detail">
                                                                    <div id="order_label">Duration</div>
                                                                    <div id="order_value">${ med.duration }</div>
                                                                </div>

                                                                <div id="view_order_detail">
                                                                    <div id="order_label">Durn units</div>
                                                                    <div id="order_value">${ med.durationUnits.getDisplayString() }</div>
                                                                </div>

                                                                <div id="view_order_detail">
                                                                    <div id="order_label">Frequency</div>
                                                                    <div id="order_value">${ med.frequency }</div>
                                                                </div>
                                                            </div>
                                                        </div>

                                                        <div id="button" class="pull-right">
                                                            <i class="icon-trash delete-action" title="${ ui.message("Discard") }" onclick="deleteMedPlanItem('${ med.id }')"></i>
                                                            <i class="icon-edit edit-action" title="${ ui.message("Edit") }" onclick="editPlanItemDetails('${ med.id }','${ newPlan.planName.getDisplayString() }','${ med.drugId.getDisplayString() }','${ med.dose }','${ med.doseUnits.getDisplayString() }','${ med.route.getDisplayString() }','${ med.quantity }','${ med.quantityUnits.getDisplayString() }','${ med.duration }','${ med.durationUnits.getDisplayString() }','${ med.frequency }')"></i>
                                                        </div><br/>
                                                    </div>
                                                <% } %><br/>
                                            <% } %>
                                        </div>
                                    </td>
                                    <td class="planButtons">
                                        <span>
                                            <i class="icon-trash delete-action" title="${ ui.message("Discard Plan") }" onclick="deleteMedPlan('${ newPlan.planName.getDisplayString() }')"></i>
                                            <i class="icon-edit edit-action" title="${ ui.message("Rename Plan") }" onclick="renameMedPlan('${ newPlan.id }','${ newPlan.planName.getDisplayString() }','${ newPlan.planDesc }')"></i>
                                            <i class="icon-plus edit-action" title="${ ui.message("Add Drug To Plan") }" onclick="addPlanItemWindow('${ newPlan.planName.getDisplayString() }')"></i>
                                        </span>
                                    </td>
                                </tr>
                            <% } %>
                        </div>
                    <% } %>            
                    
                </tbody>
            </table>
        </form>
    </div>

    <div id="planExecute"> 
        ${ ui.includeFragment("drugorders", "administration") }
    </div>
    
</div>

<script>
    jq('#medPlansTable').dataTable({
        "sPaginationType": "full_numbers",
        "bPaginate": true,
        "bAutoWidth": false,
        "bLengthChange": true,
        "bSort": true,
        "bJQueryUI": true,
        "bInfo": true,
        "columns": [
            { "width": "82%" },
            { "width": "18%" }
        ],
        fixedColumns: true

    });
</script>

<script type="text/javascript">
    jq(".icon-plus-sign").click(function(){
        jq(this).parent().nextAll(".plansDetailsView").first().show();
        jq(this).hide();
        jq(this).next(".icon-minus-sign").show();
    });
</script>

<script type="text/javascript">
    jq(".icon-minus-sign").click(function(){
        jq(this).parent().nextAll(".plansDetailsView").first().hide();
        jq(this).hide();
        jq(this).prev(".icon-plus-sign").show();
    });
</script>

<script type="text/javascript">
    jq(".show-details").click(function(){
        jq(this).parent().nextAll(".groupBlock").first().show();
        jq(this).hide();
        jq(this).next(".hide-details").show();
    });
</script>

<script type="text/javascript">
    jq(".hide-details").click(function(){
        jq(this).parent().nextAll(".groupBlock").first().hide();
        jq(this).hide();
        jq(this).prev(".show-details").show();
    });
</script>

<script type="text/javascript">    
    jq(".planBlock > #button").click(function(){
        jq(this).parent().children('div').slice(0, 1).css({"background": "#75b2f0","color": "white"});
    });
</script>