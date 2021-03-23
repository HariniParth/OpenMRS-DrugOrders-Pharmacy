<%
    def list_of_drugs = "";
    ui.decorateWith("appui", "standardEmrPage");
    ui.includeCss("drugorders", "drugorders.css");
    ui.includeJavascript("drugorders", "drugorders.js");
    ui.includeJavascript("drugorders", "dataTables.js");
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
   <div>
        <h3>
            <strong>${ ui.message("NAME OF THE CLINIC ") }</strong>
        </h3>
    <div>
    
    <div id="planList">
        <div id="line-break"></div>
        <h3>
            <i class="icon-medicine"></i>
            <strong>${ ui.message("AVAILABLE MEDICATION PLANS") }</strong>
            <i class="icon-plus edit-action right" title="${ ui.message("CREATE MEDICATION PLAN") }" onclick="displayPlanCreationWindow()"></i>
        </h3>
        <div id="line-break"></div><br/><br/>
        
        <!--
            Table displaying the list of active defined medication plans.
            Each row displays the name of a plan and the list of active drugs prescribed in the plan.
        -->
            
        <form method="post" id="adminPageForm">
            <input type="hidden" id="recordedMedPlan" value="${ recordedMedPlan }"/>
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
                                <!-- Existing list of drugs in the medication plan -->
                                <% list_of_drugs = ""; %>
                                
                                <% if(newPlan.planStatus == "Active") { %>
                                    <tr>
                                        <td class="planDetails">
                                            <div class="fields">
                                                <i class="icon-plus-sign edit-action" title="${ ui.message("View Details") }"></i>
                                                <i class="icon-minus-sign edit-action" title="${ ui.message("Hide Details") }"></i>
                                                <span class="planName"><strong class="wordBreak">${ newPlan.planName.getDisplayString().toUpperCase() }</strong></span>
                                            </div><br/>

                                            <div class="desc"><span class="wordBreak">${ newPlan.planDesc.replace("newline","<br/>") }</span></div><br/>

                                            <div class="plansDetailsView">
                                                <% medPlan = allMedicationPlans.get(newPlan.planName) %>
                                                
                                                <% if(medPlan.size() > 0) { %>
                                                    <% medPlan.each { med -> %>
                                                        <div class="planBlock">
                                                            <div class="planBlockDetails">
                                                                <div>
                                                                    <i class="show-details icon-plus-sign edit-action" title="${ ui.message("View Details") }"></i>
                                                                    <i class="hide-details icon-minus-sign icon-minus-sign edit-action" title="${ ui.message("Hide Details") }"></i>
                                                                    <span class="planItem"><strong class="wordBreak">${ med.drugId.getDisplayString().toUpperCase() }</strong></span>
                                                                </div><br/>
                                                                
                                                                <% list_of_drugs = list_of_drugs + med.drugId.getDisplayString().toUpperCase() + " ";  %>

                                                                <div class="groupItem">
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

                                                            <!--
                                                                Icons to edit and discard each plan item is provided.
                                                            -->
                                                            <div id="button" class="pull-right">
                                                                <i class="icon-trash delete-action" title="${ ui.message("Discard") }" onclick="deleteMedPlanItem('${ med.id }')"></i>
                                                                <i class="icon-edit edit-action" title="${ ui.message("Edit") }" onclick="editPlanItemDetails('${ med.id }','${ newPlan.planName.getDisplayString().toUpperCase() }','${ med.drugId.getDisplayString().toUpperCase() }','${ med.dose }','${ med.doseUnits.getDisplayString() }','${ med.route.getDisplayString() }','${ med.quantity }','${ med.quantityUnits.getDisplayString() }','${ med.duration }','${ med.durationUnits.getDisplayString() }','${ med.frequency }')"></i>
                                                            </div><br/>
                                                        </div>
                                                    <% } %><br/>
                                                <% } %>
                                            </div>
                                        </td>
                                        
                                        <!--
                                            Icons to update, rename and discard each plan is provided.
                                        -->
                                                
                                        <td class="planButtons">
                                            <span>
                                                <i class="icon-trash delete-action" title="${ ui.message("Discard Plan") }" onclick="deleteMedPlan('${ newPlan.id }')"></i>
                                                <i class="icon-edit edit-action" title="${ ui.message("Edit Plan") }" onclick="editMedPlan('${ newPlan.id }','${ newPlan.planName.getDisplayString().toUpperCase() }','${ newPlan.planDesc }')"></i>
                                                <i class="icon-plus edit-action" title="${ ui.message("Add Drug To Plan") }" onclick="addPlanItemWindow('${ newPlan.planName.getDisplayString().toUpperCase() }','${ list_of_drugs }')"></i>
                                            </span>
                                        </td>
                                    </tr>
                                <% } %>
                            <% } %>
                        </div>
                    <% } %>            
                    
                </tbody>
            </table>
            <br/>
               <div class='print-button'>
	           <svg class='icon-x'>
	           <use xlink:href='#icon-print'>
	           </use></svg> 
	          <span>Print This page</span>
</div>
        </form>
    </div>

    <div id="planExecute"> 
        ${ ui.includeFragment("drugorders", "administrationActions") }
    </div>
    
</div>
<script>
  jq('.print-button').on('click', function() {  
  window.print();  
  return false; // why false?
});
</script>

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
    <!--Show the list of drugs defined in the given medication plan.-->
    jq(".icon-plus-sign").click(function(){
        jq(this).parent().nextAll(".plansDetailsView").first().show();
        jq(this).hide();
        jq(this).next(".icon-minus-sign").show();
    });
    
    <!--Hide the list of drugs defined in the given medication plan.-->
    jq(".icon-minus-sign").click(function(){
        jq(this).parent().nextAll(".plansDetailsView").first().hide();
        jq(this).hide();
        jq(this).prev(".icon-plus-sign").show();
    });
    
    <!--Show the details of the standard drug order defined in the given medication plan.-->
    jq(".show-details").click(function(){
        jq(this).parent().nextAll(".groupItem").first().show();
        jq(this).hide();
        jq(this).next(".hide-details").show();
    });
    
    <!--Hide the details of the standard drug order defined in the given medication plan.-->
    jq(".hide-details").click(function(){
        jq(this).parent().nextAll(".groupItem").first().hide();
        jq(this).hide();
        jq(this).prev(".show-details").show();
    });
    
    <!--Highlight the given standard drug order when clicked to be edited-->
    jq(".planBlock > #button").click(function(){
        jq(this).parent().children('div').slice(0, 1).css({"background": "#75b2f0","color": "white"});
        jq(this).parent().find('.planBlockDetails').find('.show-details').hide();
        jq(this).parent().find('.planBlockDetails').find('.hide-details').show();
        jq(this).parent().find('.planBlockDetails').find('.groupItem').show();
    });
</script>