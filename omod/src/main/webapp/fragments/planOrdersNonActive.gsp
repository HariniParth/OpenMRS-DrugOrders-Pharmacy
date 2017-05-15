<%
    ui.includeCss("drugorders", "drugorders.css")
    def default_prio = "";
%>

<!--
    Table displaying the list of non-active medication plan related drug orders.
    Columns display the order ID and drug name
    Each record can be clicked to view the order details.
    Icons are provided to renew the orders.
-->

<div id="nonActivePlansTableWrapper">
    <form method="post" name="nonActivePlanForm" id="nonActivePlanForm">
        <input type="hidden" id="selectedNonActivePlan" name="selectedNonActivePlan" />
        
        <table id="nonActivePlansTable">
            <thead>
                <tr>
                    <th>Plan Name</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
                <% if(NonActivePlanMain.size() == 0) { %>
                    <tr>
                        <td colspan="2" align="center">No Orders Found</td>
                        <td style="display: none;"></td>
                    </tr>
                <% } %>

                <% NonActivePlanMain.each { planOrderMain -> %>
                    <% planOrderMain.value.each { planMain -> %>
                        <tr>
                            <td>
                                <div class="fields">
                                    <span class="viewDetails">
                                        <i class="icon-plus-sign edit-action" title="${ ui.message("View Details") }"></i>
                                        <i class="icon-minus-sign edit-action" title="${ ui.message("Hide Details") }"></i>
                                    </span>
                                    <strong class="wordBreak">${ planMain.key.getDisplayString().toUpperCase() }</strong>
                                </div><br/>
                                
                                <div class="plansDetailsView">
                                    <% planMain.value.each { orderMain -> %>
                                        <div class="planDrug">
                                            <% if(NonActivePlanExtn.get(planOrderMain.key).get(planMain.key).get(orderMain.key).priority != null) { %>
                                                <% default_prio = NonActivePlanExtn.get(planOrderMain.key).get(planMain.key).get(orderMain.key).priority.getDisplayString(); %>
                                            <% } %>

                                            <div class="planDetails">
                                                <div id="planDrugId">
                                                    ${ NonActivePlanExtn.get(planOrderMain.key).get(planMain.key).get(orderMain.key).orderId }
                                                </div>
                                                
                                                <div id="planDrugName" onclick="showDrugOrderViewWindow('VIEW ORDER','${ NonActivePlanExtn.get(planOrderMain.key).get(planMain.key).get(orderMain.key).startDate.format('yyyy-MM-dd') }','${ NonActivePlanExtn.get(planOrderMain.key).get(planMain.key).get(orderMain.key).drugName.getDisplayString() }','${ orderMain.value.dose }','${ orderMain.value.doseUnits.getDisplayString() }','${ orderMain.value.route.getDisplayString() }','${ orderMain.value.duration }','${ orderMain.value.durationUnits.getDisplayString() }','${ orderMain.value.quantity }','${ orderMain.value.quantityUnits.getDisplayString() }','${ orderMain.value.frequency }','${ NonActivePlanExtn.get(planOrderMain.key).get(planMain.key).get(orderMain.key).refill }','${ NonActivePlanExtn.get(planOrderMain.key).get(planMain.key).get(orderMain.key).isAllergicOrderReasons }','${ NonActivePlanExtn.get(planOrderMain.key).get(planMain.key).get(orderMain.key).associatedDiagnosis.getDisplayString() }','${ default_prio }','${ NonActivePlanExtn.get(planOrderMain.key).get(planMain.key).get(orderMain.key).patientInstructions }','${ NonActivePlanExtn.get(planOrderMain.key).get(planMain.key).get(orderMain.key).pharmacistInstructions }','${ NonActivePlanExtn.get(planOrderMain.key).get(planMain.key).get(orderMain.key).commentForOrderer }','${ NonActivePlanExtn.get(planOrderMain.key).get(planMain.key).get(orderMain.key).orderStatus }')">   
                                                    <div><strong class="wordBreak">${ NonActivePlanExtn.get(planOrderMain.key).get(planMain.key).get(orderMain.key).drugName.getDisplayString().toUpperCase() }</strong></div>
                                                    <div class="itemSummary"><em>Click to view details</em></div>
                                                </div><br/>
                                            </div>
                                        </div>
                                    <% } %>
                                </div>
                            </td>
                            <td class="planRenewButton">
                                <span id="button" class="pull-right">
                                    <i class="icon-edit edit-action" title="${ ui.message("Renew") }" onclick="renewMedPlanWindow('${ planOrderMain.key }')"></i>
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
    jq('#nonActivePlansTable').dataTable({
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
    <!--Display details of selected medication plan orders when 'show' icon is clicked-->
    jq(".icon-plus-sign").click(function(){
        jq(this).parent().parent().nextAll(".plansDetailsView").first().show();
        jq(this).hide();
        jq(this).next(".icon-minus-sign").show();
    });
    
    <!--Close details of selected medication plan orders when 'hide' icon is clicked-->
    jq(".icon-minus-sign").click(function(){
        jq(this).parent().parent().nextAll(".plansDetailsView").first().hide();
        jq(this).hide();
        jq(this).prev(".icon-plus-sign").show();
    });
    
    <!--Highlight row representing individual drug order when selected to be viewed.-->
    jq(".planDetails").click(function(){
        jq(this).css({"background": "#75b2f0","color": "white"});
    });
    
    <!--Highlight row representing group drug order when corresponding action buttons are hovered upon.-->
    jq(".planRenewButton > span > i").hover(function(event){
        if(event.type == 'mouseenter'){
            jq(this).parent().parent().parent().children('td').slice(0, 1).children(".planDrug").children(".planDetails").css({"background": "#75b2f0","color": "white"});
        } else {
            jq(this).parent().parent().parent().children('td').slice(0, 1).children(".planDrug").children(".planDetails").css({"background": "","color": ""});
        }
    });
</script>