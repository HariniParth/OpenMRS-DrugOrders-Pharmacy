<%
    ui.includeCss("drugorders", "drugorders.css")
    ui.includeJavascript("drugorders", "dataTables.js")
    def default_prio = "";
%>

<div id="activePlansTableWrapper">
    <form method="post" name="activePlanForm" id="activePlanForm">
        <input type="hidden" id="selectedActivePlan" name="selectedActivePlan" />
        <input type="hidden" id="selectedActiveItem" name="selectedActiveItem" />
        
        <table id="activePlansTable">
            <thead>
                <tr>
                    <th>Plan Name</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
                <% if(ActivePlanMain.size() == 0) { %>
                    <tr>
                        <td colspan="2" align="center">No Orders Found</td>
                        <td style="display: none;"></td>
                    </tr>
                <% } %>

                <% ActivePlanMain.each { drugOrderMain -> %>
                    <tr>
                        <td>
                            <div class="fields">
                                <span class="viewDetails">
                                    <i class="icon-plus-sign edit-action" title="${ ui.message("View Details") }"></i>
                                    <i class="icon-minus-sign edit-action" title="${ ui.message("Hide Details") }"></i>
                                </span>
                                <strong>${ drugOrderMain.key.getDisplayString().toUpperCase() }</strong>
                            </div><br/>

                            <div class="plansDetailsView">
                                <% drugOrderMain.value.each { drugOrderMn -> %>

                                    <% if(ActivePlanExtension.get(drugOrderMain.key).get(drugOrderMn.key).priority != null) { %>
                                        <% default_prio = ActivePlanExtension.get(drugOrderMain.key).get(drugOrderMn.key).priority.getDisplayString(); %>
                                    <% } %>

                                    <div class="planDrug <% if(ActivePlanExtension.get(drugOrderMain.key).get(drugOrderMn.key).forDiscard == 1) { %> discontinued <% } %> <% if(ActivePlanExtension.get(drugOrderMain.key).get(drugOrderMn.key).onHold == 1) { %> onhold <% } %>" title="${ ui.message(ActivePlanExtension.get(drugOrderMain.key).get(drugOrderMn.key).commentForOrderer) }">

                                        <div class="planDrugDetails" onclick="showDrugOrderViewWindow('VIEW ORDER','${ ui.format(patient.givenName) }','${ ui.format(patient.familyName) }','${ ActivePlanExtension.get(drugOrderMain.key).get(drugOrderMn.key).startDate.format('yyyy-MM-dd') }','${ ActivePlanExtension.get(drugOrderMain.key).get(drugOrderMn.key).drugName.getDisplayString() }','${ drugOrderMn.value.dose }','${ drugOrderMn.value.doseUnits.getDisplayString() }','${ drugOrderMn.value.route.getDisplayString() }','${ drugOrderMn.value.duration }','${ drugOrderMn.value.durationUnits.getDisplayString() }','${ drugOrderMn.value.quantity }','${ drugOrderMn.value.quantityUnits.getDisplayString() }','${ drugOrderMn.value.frequency }','${ ActivePlanExtension.get(drugOrderMain.key).get(drugOrderMn.key).refill }','${ ActivePlanExtension.get(drugOrderMain.key).get(drugOrderMn.key).isAllergicOrderReasons }','${ default_prio }','${ ActivePlanExtension.get(drugOrderMain.key).get(drugOrderMn.key).patientInstructions }','${ ActivePlanExtension.get(drugOrderMain.key).get(drugOrderMn.key).pharmacistInstructions }')">   
                                            <div id="planDrugId">
                                                ${ ActivePlanExtension.get(drugOrderMain.key).get(drugOrderMn.key).orderId }
                                            </div>
                                            
                                            <div id="planDrugName">
                                                <div>
                                                    <strong>${ ActivePlanExtension.get(drugOrderMain.key).get(drugOrderMn.key).drugName.getDisplayString().toUpperCase() }</strong>
                                                </div>
                                                
                                                <span class="itemSummary">
                                                    <div>Start Date: ${ ActivePlanExtension.get(drugOrderMain.key).get(drugOrderMn.key).startDate.format('yyyy-MM-dd') }</div>
                                                    <div><em>Click to view details</em></div>
                                                </span>
                                            </div>
                                        </div>

                                        <div id="button" class="pull-right">
                                            <i class="icon-edit edit-action" title="${ ui.message("Edit") }" onclick="showEditSingleOrderWindow('EDIT DRUG ORDER','PLAN','${ drugOrderMn.value.orderId }','${ ActivePlanExtension.get(drugOrderMain.key).get(drugOrderMn.key).drugName.getDisplayString() }','${ ActivePlanExtension.get(drugOrderMain.key).get(drugOrderMn.key).startDate }','${ drugOrderMn.value.dose }','${ drugOrderMn.value.doseUnits.getDisplayString() }','${ drugOrderMn.value.route.getDisplayString() }','${ drugOrderMn.value.duration }','${ drugOrderMn.value.durationUnits.getDisplayString() }','${ drugOrderMn.value.quantity }','${ drugOrderMn.value.quantityUnits.getDisplayString() }','${ drugOrderMn.value.frequency }','${ ActivePlanExtension.get(drugOrderMain.key).get(drugOrderMn.key).refill }','${ ActivePlanExtension.get(drugOrderMain.key).get(drugOrderMn.key).refillInterval }','${drugOrderMain.key.getDisplayString()}','${ ActivePlanExtension.get(drugOrderMain.key).get(drugOrderMn.key).isAllergicOrderReasons }','${ default_prio }','${ ActivePlanExtension.get(drugOrderMain.key).get(drugOrderMn.key).patientInstructions }','${ ActivePlanExtension.get(drugOrderMain.key).get(drugOrderMn.key).pharmacistInstructions }')"></i>
                                            <i class="icon-trash delete-action" title="${ ui.message("Discontinue") }" onclick="discardSingleItem('${ drugOrderMn.value.orderId }')"></i>
                                        </div><br/>
                                    </div>

                                <% } %>
                            </div>
                        </td>   

                        <td class="planDiscardButton">
                            <span id="button">
                                <i class="icon-trash delete-action" title="${ ui.message("Discontinue") }" onclick="discardMedPlanOrder('${drugOrderMain.key.getDisplayString()}')"></i>
                            </span>
                        </td>
                    </tr>
                <% } %>

            </tbody>
        </table>
    </form>
</div>

<script>
    jq('#activePlansTable').dataTable({
        "sPaginationType": "full_numbers",
        "bPaginate": true,
        "bAutoWidth": false,
        "bLengthChange": true,
        "bSort": true,
        "bJQueryUI": true,
        "bInfo": true,
        "bFilter": true,
        "columns": [
            { "width": "85%" },
            { "width": "15%" }
        ],
        fixedColumns: true

    });
</script>

<script type="text/javascript">
    jq(".icon-plus-sign").click(function(){
        jq(this).parent().parent().nextAll(".plansDetailsView").first().show();
        jq(this).hide();
        jq(this).next(".icon-minus-sign").show();
    });
</script>

<script type="text/javascript">
    jq(".icon-minus-sign").click(function(){
        jq(this).parent().parent().nextAll(".plansDetailsView").first().hide();
        jq(this).hide();
        jq(this).prev(".icon-plus-sign").show();
    });
</script>

<script type="text/javascript">    
    jq(".planDrug").click(function(){
        jq(this).children('div').slice(0, 1).css({"background": "#75b2f0","color": "white"});
    });
</script>

<script type="text/javascript">    
    jq(".planDiscardButton > span > i").hover(function(event){
        if(event.type == 'mouseenter'){
            jq(this).parent().parent().parent().children('td').slice(0, 1).children(".orderDetails").children(".planDrug").css({"background": "#75b2f0","color": "white"});
        } else {
            jq(this).parent().parent().parent().children('td').slice(0, 1).children(".orderDetails").children(".planDrug").css({"background": "","color": ""});
        }
    });
</script>