<%
    ui.includeCss("drugorders", "drugorders.css")
    ui.includeJavascript("drugorders", "dataTables.js")
%>

<!--
    Table displaying the list of non-active individual and group drug orders.
    Columns display the order ID and drug name
    Each record can be clicked to view the order details.
    Icons are provided to renew the orders.
-->

<div id="nonActiveOrdersTableWrapper">
    <form method="post" name="nonActiveGroupForm" id="nonActiveGroupForm">
        <input type="hidden" id="selectedNonActiveGroup" name="selectedNonActiveGroup" />
        
        <table id="nonActiveOrdersTable">
            <thead>
                <tr>
                    <th>ID &nbsp; &nbsp; Drug Name</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
                <% if(singleOrdersExtn.size() == 0 && groupOrdersExtn.size() == 0) { %>
                    <tr>
                        <td colspan="2" align="center">No Orders Found</td>
                        <td style="display: none;"></td>
                    </tr>
                <% } %>

                <% groupOrdersExtn.each { groupOrderExtn -> %>

                    <tr class="oldGroupRow">
                        <td>
                            <% groupOrderExtn.value.each { groupOrder -> %>

                                <div class="oldGroupDetails" onclick="showDrugOrderViewWindow('VIEW ORDER','${ groupOrder.startDate.format('yyyy-MM-dd') }','${ groupOrder.drugName.getDisplayString() }','${ drugOrdersMain.get(groupOrder.orderId).dose }','${ drugOrdersMain.get(groupOrder.orderId).doseUnits.getDisplayString() }','${ drugOrdersMain.get(groupOrder.orderId).route.getDisplayString() }','${ drugOrdersMain.get(groupOrder.orderId).duration }','${ drugOrdersMain.get(groupOrder.orderId).durationUnits.getDisplayString() }','${ drugOrdersMain.get(groupOrder.orderId).quantity }','${ drugOrdersMain.get(groupOrder.orderId).quantityUnits.getDisplayString() }','${ drugOrdersMain.get(groupOrder.orderId).frequency }','${ groupOrder.refill }','${ groupOrder.refillInterval }','${ groupOrder.isAllergicOrderReasons }','${ groupOrder.associatedDiagnosis.getDisplayString() }','${ groupOrder.priority.getDisplayString() }','${ groupOrder.patientInstructions }','${ groupOrder.pharmacistInstructions }','${ groupOrder.commentForOrderer }','${ groupOrder.orderStatus }')">
                                    <div id="oldOrderId">
                                        ${ groupOrder.orderId }
                                    </div>
                                    <div id="oldOrderName">
                                        <div><strong class="wordBreak">${ groupOrder.drugName.getDisplayString().toUpperCase() }</strong></div>
                                        <div class="itemSummary"><em>Click to view details</em></div>
                                    </div>                                    
                                </div>
                                
                            <% } %>
                        </td>
                        <td class="renewGroupButton">
                            <span id="button">
                                <i class="icon-edit edit-action" title="${ ui.message("Renew") }" onclick="showRenewGroupOrderWindow('${groupOrderExtn.key}')"></i>
                            </span>
                        </td>
                    </tr>

                <% } %>

                <% singleOrdersExtn.each { singleOrderExtn -> %>

                    <tr class="oldOrderRow">
                        <td class="oldOrderDetails" onclick="showDrugOrderViewWindow('VIEW ORDER','${ singleOrderExtn.startDate.format('yyyy-MM-dd') }','${ singleOrderExtn.drugName.getDisplayString() }','${ drugOrdersMain.get(singleOrderExtn.orderId).dose }','${ drugOrdersMain.get(singleOrderExtn.orderId).doseUnits.getDisplayString() }','${ drugOrdersMain.get(singleOrderExtn.orderId).route.getDisplayString() }','${ drugOrdersMain.get(singleOrderExtn.orderId).duration }','${ drugOrdersMain.get(singleOrderExtn.orderId).durationUnits.getDisplayString() }','${ drugOrdersMain.get(singleOrderExtn.orderId).quantity }','${ drugOrdersMain.get(singleOrderExtn.orderId).quantityUnits.getDisplayString() }','${ drugOrdersMain.get(singleOrderExtn.orderId).frequency }','${ singleOrderExtn.refill }','${ singleOrderExtn.refillInterval }','${ singleOrderExtn.isAllergicOrderReasons }','${ singleOrderExtn.associatedDiagnosis.getDisplayString() }','${ singleOrderExtn.priority.getDisplayString() }','${ singleOrderExtn.patientInstructions }','${ singleOrderExtn.pharmacistInstructions }','${ singleOrderExtn.commentForOrderer }','${ singleOrderExtn.orderStatus }')">
                            <div id="oldOrderId">
                                ${ singleOrderExtn.orderId }
                            </div>
                            
                            <div id="oldOrderName">
                                <div><strong class="wordBreak">${ singleOrderExtn.drugName.getDisplayString().toUpperCase() }</strong></div>
                                <div class="itemSummary"><em>Click to view details</em></div>
                            </div>
                        </td>
                        <td>
                            <span id="button">
                                <i class="icon-edit edit-action" title="${ ui.message("Renew") }" onclick="editSingleOrderDetailsWindow('RENEW DRUG ORDER','${ singleOrderExtn.orderId }','${ singleOrderExtn.drugName.getDisplayString().toUpperCase() }',null,'${ drugOrdersMain.get(singleOrderExtn.orderId).dose }','${ drugOrdersMain.get(singleOrderExtn.orderId).doseUnits.getDisplayString() }','${ drugOrdersMain.get(singleOrderExtn.orderId).route.getDisplayString() }','${ drugOrdersMain.get(singleOrderExtn.orderId).duration }','${ drugOrdersMain.get(singleOrderExtn.orderId).durationUnits.getDisplayString() }','${ drugOrdersMain.get(singleOrderExtn.orderId).quantity }','${ drugOrdersMain.get(singleOrderExtn.orderId).quantityUnits.getDisplayString() }','${ drugOrdersMain.get(singleOrderExtn.orderId).frequency }','${ singleOrderExtn.refill }','${ singleOrderExtn.refillInterval }','${ singleOrderExtn.associatedDiagnosis.getDisplayString().toUpperCase() }','${ singleOrderExtn.isAllergicOrderReasons }','${ singleOrderExtn.priority.getDisplayString() }','${ singleOrderExtn.patientInstructions }','${ singleOrderExtn.pharmacistInstructions }','${ singleOrderExtn.orderStatus }','${ currentOrders }','${ allergicDrugs }')"></i>
                            </span>
                        </td>
                    </tr>
                <% } %>
            </tbody>
        </table> <br/><br/>
    </form>
</div>

<script>
    jq('#nonActiveOrdersTable').dataTable({
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
    <!--Highlight row representing individual drug order when selected to be viewed.-->
    jq(".oldOrderRow").click(function(){
        jq(this).children('td').slice(0, 1).css({"background": "#75b2f0","color": "white"});
    });
    
    <!--Highlight row representing group drug order when selected to be viewed.-->
    jq(".oldGroupDetails").click(function(){
        jq(this).css({"background": "#75b2f0","color": "white"});
    });
    
    <!--Highlight row representing group drug order when corresponding action buttons are clicked.-->
    jq(".renewGroupButton").click(function(){
        jq(this).parent().children('td').slice(0, 1).css({"background": "#75b2f0","color": "white"});
    });
    
    <!--Highlight row representing group drug order when corresponding action buttons are hovered upon.-->
    jq(".renewGroupButton > span > i").hover(function(event){
        if(event.type == 'mouseenter'){
            jq(this).parent().parent().parent().children('td').slice(0, 1).children('.oldGroupDetails').css({"background": "#75b2f0","color": "white"});
        } else {
            jq(this).parent().parent().parent().children('td').slice(0, 1).children('.oldGroupDetails').css({"background": "","color": ""});
        }
    });
</script>