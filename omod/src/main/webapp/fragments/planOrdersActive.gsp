<%
    ui.includeCss("drugorders", "drugorders.css")
    ui.includeJavascript("drugorders", "dataTables.js")
    def default_prio = "";
%>

<!--
    Table displaying the list of active medication plan related drug orders.
    Columns display the order ID, drug name and start date.
    Each record can be clicked to view the order details.
    Icons are provided to edit and discard the orders.
-->

<div id="activePlansTableWrapper">
    <form method="post" name="activePlanForm" id="activePlanForm">
        <input type="hidden" id="selectedActivePlan" name="selectedActivePlan" />
        <input type="hidden" id="selectedActiveItem" name="selectedActiveItem" />
        <input type="hidden" id="activatePlan" name="activatePlan" />
        
        <table id="activePlansTable">
            <thead>
                <tr>
                    <th>Plan Name</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
                <% if(ActivePlanMain.size() == 0 && DraftPlanMain.size() == 0) { %>
                    <tr>
                        <td colspan="2" align="center">No Orders Found</td>
                        <td style="display: none;"></td>
                    </tr>
                <% } %>

                <% if(DraftPlanMain.size() > 0) { %>
                    <div id="draftPlanList">
                        <% DraftPlanMain.each { drugOrderMain -> %>
                            <tr id="draftPlanRow">
                                <td>
                                    <div class="fields">
                                        <i class="icon-plus-sign edit-action" title="${ ui.message("View Details") }"></i>
                                        <i class="icon-minus-sign edit-action" title="${ ui.message("Hide Details") }"></i>
                                        <strong class="wordBreak">${ DraftPlanExtn.get(drugOrderMain.key).entrySet().iterator().next().getValue().associatedDiagnosis.getDisplayString().toUpperCase() }</strong>
                                    </div><br/>

                                    <span class="hidden" id="id">${ drugOrderMain.key }</span>
                                    
                                    <div class="plansDetailsView">
                                        <% drugOrderMain.value.each { drugOrderMn -> %>

                                            <% if(DraftPlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).priority != null) { %>
                                                <% default_prio = DraftPlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).priority.getDisplayString(); %>
                                            <% } %>

                                            <div class="planDrug">

                                                <div class="planDrugDetails" onclick="showDrugOrderViewWindow('VIEW ORDER','${ DraftPlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).startDate.format('yyyy-MM-dd') }','${ DraftPlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).drugName.getDisplayString() }','${ drugOrderMn.value.dose }','${ drugOrderMn.value.doseUnits.getDisplayString() }','${ drugOrderMn.value.route.getDisplayString() }','${ drugOrderMn.value.duration }','${ drugOrderMn.value.durationUnits.getDisplayString() }','${ drugOrderMn.value.quantity }','${ drugOrderMn.value.quantityUnits.getDisplayString() }','${ drugOrderMn.value.frequency }','${ DraftPlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).refill }','${ DraftPlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).refillInterval }','${ DraftPlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).isAllergicOrderReasons }','${ DraftPlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).associatedDiagnosis.getDisplayString() }','${ default_prio }','${ DraftPlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).patientInstructions }','${ DraftPlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).pharmacistInstructions }','${ DraftPlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).commentForOrderer }','${ DraftPlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).orderStatus }')">   
                                                    <div id="planDrugId">
                                                        ${ DraftPlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).orderId }
                                                    </div>

                                                    <div id="planDrugName">
                                                        <div>
                                                            <strong class="wordBreak">${ DraftPlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).drugName.getDisplayString().toUpperCase() }</strong>
                                                        </div>

                                                        <span class="itemSummary">
                                                            <div>Start Date: ${ DraftPlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).startDate.format('yyyy-MM-dd') }</div>
                                                            <div><em>Click to view details</em></div>
                                                        </span>
                                                    </div>
                                                </div>

                                                <div id="button" class="pull-right">
                                                    <i class="icon-edit edit-action" title="${ ui.message("Edit") }" onclick="editSingleOrderDetailsWindow('EDIT DRUG ORDER','${ drugOrderMn.value.orderId }','${ DraftPlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).drugName.getDisplayString().toUpperCase() }','${ DraftPlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).startDate }','${ drugOrderMn.value.dose }','${ drugOrderMn.value.doseUnits.getDisplayString() }','${ drugOrderMn.value.route.getDisplayString() }','${ drugOrderMn.value.duration }','${ drugOrderMn.value.durationUnits.getDisplayString() }','${ drugOrderMn.value.quantity }','${ drugOrderMn.value.quantityUnits.getDisplayString() }','${ drugOrderMn.value.frequency }','${ DraftPlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).refill }','${ DraftPlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).refillInterval }','${ DraftPlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).associatedDiagnosis.getDisplayString().toUpperCase() }','${ DraftPlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).isAllergicOrderReasons }','${ default_prio }','${ DraftPlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).patientInstructions }','${ DraftPlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).pharmacistInstructions }','${ DraftPlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).orderStatus }','${ currentOrders }','${ allergicDrugs }')"></i>
                                                    <i class="icon-trash delete-action" title="${ ui.message("Discontinue") }" onclick="discardSingleItem('${ drugOrderMn.value.orderId }')"></i>
                                                </div><br/>
                                            </div>
                                        <% } %>
                                    </div>
                                </td>   

                                <td class="planDiscardButton">
                                    <span id="button">
                                        <i class="icon-save edit-action" title="${ ui.message("Save") }" onclick="saveMedPlanOrder('${ drugOrderMain.key }')"></i>
                                        <i class="icon-trash delete-action" title="${ ui.message("Discontinue") }" onclick="discardMedPlanOrder('${drugOrderMain.key }')"></i>
                                    </span>
                                </td>
                            </tr>
                        <% } %>
                    </div>
                <% } %>
                
                <% if(ActivePlanMain.size() > 0) { %>
                    <% ActivePlanMain.each { drugOrderMain -> %>
                        <tr>
                            <td>
                                <div class="fields">
                                    <i class="icon-plus-sign edit-action" title="${ ui.message("View Details") }"></i>
                                    <i class="icon-minus-sign edit-action" title="${ ui.message("Hide Details") }"></i>
                                    <strong class="wordBreak">${ ActivePlanExtn.get(drugOrderMain.key).entrySet().iterator().next().getValue().associatedDiagnosis.getDisplayString().toUpperCase() }</strong>
                                </div><br/>
                                
                                <span class="hidden" id="id">${ drugOrderMain.key }</span>
                                
                                <div class="plansDetailsView">
                                    <% drugOrderMain.value.each { drugOrderMn -> %>

                                        <% if(ActivePlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).priority != null) { %>
                                            <% default_prio = ActivePlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).priority.getDisplayString(); %>
                                        <% } %>

                                        <div class="planDrug <% if(ActivePlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).forDiscard == 1) { %> discontinued <% } %> <% if(ActivePlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).onHold == 1) { %> onhold <% } %>" title="${ ui.message(ActivePlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).commentForOrderer) }">

                                            <div class="planDrugDetails" onclick="showDrugOrderViewWindow('VIEW ORDER','${ ActivePlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).startDate.format('yyyy-MM-dd') }','${ ActivePlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).drugName.getDisplayString() }','${ drugOrderMn.value.dose }','${ drugOrderMn.value.doseUnits.getDisplayString() }','${ drugOrderMn.value.route.getDisplayString() }','${ drugOrderMn.value.duration }','${ drugOrderMn.value.durationUnits.getDisplayString() }','${ drugOrderMn.value.quantity }','${ drugOrderMn.value.quantityUnits.getDisplayString() }','${ drugOrderMn.value.frequency }','${ ActivePlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).refill }','${ ActivePlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).refillInterval }','${ ActivePlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).isAllergicOrderReasons }','${ ActivePlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).associatedDiagnosis.getDisplayString() }','${ default_prio }','${ ActivePlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).patientInstructions }','${ ActivePlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).pharmacistInstructions }','${ ActivePlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).commentForOrderer }','${ ActivePlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).orderStatus }')">   
                                                <div id="planDrugId">
                                                    ${ ActivePlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).orderId }
                                                </div>

                                                <div id="planDrugName">
                                                    <div>
                                                        <strong class="wordBreak">${ ActivePlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).drugName.getDisplayString().toUpperCase() }</strong>
                                                    </div>

                                                    <span class="itemSummary">
                                                        <div>Start Date: ${ ActivePlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).startDate.format('yyyy-MM-dd') }</div>
                                                        <div><em>Click to view details</em></div>
                                                    </span>
                                                </div>
                                            </div>

                                            <div id="button" class="pull-right">
                                                <i class="icon-edit edit-action" title="${ ui.message("Edit") }" onclick="editSingleOrderDetailsWindow('EDIT DRUG ORDER','${ drugOrderMn.value.orderId }','${ ActivePlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).drugName.getDisplayString().toUpperCase() }','${ ActivePlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).startDate }','${ drugOrderMn.value.dose }','${ drugOrderMn.value.doseUnits.getDisplayString() }','${ drugOrderMn.value.route.getDisplayString() }','${ drugOrderMn.value.duration }','${ drugOrderMn.value.durationUnits.getDisplayString() }','${ drugOrderMn.value.quantity }','${ drugOrderMn.value.quantityUnits.getDisplayString() }','${ drugOrderMn.value.frequency }','${ ActivePlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).refill }','${ ActivePlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).refillInterval }','${ ActivePlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).associatedDiagnosis.getDisplayString().toUpperCase() }','${ ActivePlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).isAllergicOrderReasons }','${ default_prio }','${ ActivePlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).patientInstructions }','${ ActivePlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).pharmacistInstructions }','${ ActivePlanExtn.get(drugOrderMain.key).get(drugOrderMn.key).orderStatus }','${ currentOrders }','${ allergicDrugs }')"></i>
                                                <i class="icon-trash delete-action" title="${ ui.message("Discontinue") }" onclick="discardSingleItem('${ drugOrderMn.value.orderId }')"></i>
                                            </div><br/>
                                        </div>

                                    <% } %>
                                </div>
                            </td>   

                            <td class="planDiscardButton">
                                <span id="button">
                                    <i class="icon-trash delete-action" title="${ ui.message("Discontinue") }" onclick="discardMedPlanOrder('${ drugOrderMain.key }')"></i>
                                </span>
                            </td>
                        </tr>
                    <% } %>
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
    <!--Display details of selected medication plan orders by default-->
    jq(".icon-plus-sign").hide();
    jq(".icon-minus-sign").show();
    jq(".plansDetailsView").show();
        
    <!--Display details of selected medication plan orders when 'show' icon is clicked-->
    jq(".icon-plus-sign").click(function(){
        jq(this).parent().nextAll(".plansDetailsView").first().show();
        jq(this).hide();
        jq(this).next(".icon-minus-sign").show();
    });
    
    <!--Close details of selected medication plan orders when 'hide' icon is clicked-->
    jq(".icon-minus-sign").click(function(){
        jq(this).parent().nextAll(".plansDetailsView").first().hide();
        jq(this).hide();
        jq(this).prev(".icon-plus-sign").show();
    });
    
    <!--Highlight row representing individual drug order when selected to be viewed.-->
    jq(".planDrug").click(function(){
        jq(this).children('div').slice(0, 1).css({"background": "#75b2f0","color": "white"});
    });
    
    <!--Highlight row representing group drug order when corresponding action buttons are hovered upon.-->
    jq(".planDiscardButton > span > i").hover(function(event){
        if(event.type == 'mouseenter'){
            jq(this).parent().parent().parent().children('td').slice(0, 1).children(".orderDetails").children(".planDrug").css({"background": "#75b2f0","color": "white"});
        } else {
            jq(this).parent().parent().parent().children('td').slice(0, 1).children(".orderDetails").children(".planDrug").css({"background": "","color": ""});
        }
    });
</script>