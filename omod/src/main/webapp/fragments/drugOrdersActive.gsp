<%
    ui.includeCss("drugorders", "drugorders.css")
    ui.includeJavascript("drugorders", "dataTables.js")
%>

<!--
    Table displaying the list of active individual and group drug orders.
    Columns display the order ID, drug name and start date.
    Each record can be clicked to view the order details.
    Icons are provided to edit and discard the orders.
    Orders can be grouped or ungrouped.
-->

<div id="activeOrdersTableWrapper">
    <form method="post" name="activeGroupForm" id="activeGroupForm">
        <input type="hidden" id="selectedActiveGroup" name="selectedActiveGroup" />
        <input type="hidden" id="selectedActiveOrder" name="selectedActiveOrder" />
        
        <table id="activeOrdersTable">
            <thead>
                <tr>
                    <th>Group</th>
                    <th>ID</th>
                    <th>Drug Name</th>
                    <th>Start Date</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
                <% if(singleOrdersExtn.size() == 0 && groupOrdersExtn.size() == 0) { %>
                    <tr>
                        <td colspan="5" align="center">No Orders Found</td>
                        <td style="display: none;"></td>
                        <td style="display: none;"></td>
                        <td style="display: none;"></td>
                        <td style="display: none;"></td>
                    </tr>
                <% } %>

                <% singleOrdersExtn.each { singleOrderExtn -> %>
                    <tr class="orderRow <% if(singleOrderExtn.forDiscard == 1) { %> discontinued <% } %> <% if(singleOrderExtn.onHold == 1) { %> onhold <% } %>" title="${ ui.message(singleOrderExtn.commentForOrderer) }">

                        <td><input type="checkbox" name="singleCheckBox" value="${ singleOrderExtn.orderId }" ng-model="groupCheckBox" /></td>

                        <td onclick="showDrugOrderViewWindow('VIEW ORDER','${ singleOrderExtn.startDate.format('yyyy-MM-dd') }','${ singleOrderExtn.drugName.getDisplayString() }','${ drugOrdersMain.get(singleOrderExtn.orderId).dose }','${ drugOrdersMain.get(singleOrderExtn.orderId).doseUnits.getDisplayString() }','${ drugOrdersMain.get(singleOrderExtn.orderId).route.getDisplayString() }','${ drugOrdersMain.get(singleOrderExtn.orderId).duration }','${ drugOrdersMain.get(singleOrderExtn.orderId).durationUnits.getDisplayString() }','${ drugOrdersMain.get(singleOrderExtn.orderId).quantity }','${ drugOrdersMain.get(singleOrderExtn.orderId).quantityUnits.getDisplayString() }','${ drugOrdersMain.get(singleOrderExtn.orderId).frequency }','${ singleOrderExtn.refill }','${ singleOrderExtn.isAllergicOrderReasons }','${ singleOrderExtn.associatedDiagnosis.getDisplayString() }','${ singleOrderExtn.priority.getDisplayString() }','${ singleOrderExtn.patientInstructions }','${ singleOrderExtn.pharmacistInstructions }','${ singleOrderExtn.commentForOrderer }','${ singleOrderExtn.orderStatus }')">${ singleOrderExtn.orderId }</td>
                        
                        <td onclick="showDrugOrderViewWindow('VIEW ORDER','${ singleOrderExtn.startDate.format('yyyy-MM-dd') }','${ singleOrderExtn.drugName.getDisplayString() }','${ drugOrdersMain.get(singleOrderExtn.orderId).dose }','${ drugOrdersMain.get(singleOrderExtn.orderId).doseUnits.getDisplayString() }','${ drugOrdersMain.get(singleOrderExtn.orderId).route.getDisplayString() }','${ drugOrdersMain.get(singleOrderExtn.orderId).duration }','${ drugOrdersMain.get(singleOrderExtn.orderId).durationUnits.getDisplayString() }','${ drugOrdersMain.get(singleOrderExtn.orderId).quantity }','${ drugOrdersMain.get(singleOrderExtn.orderId).quantityUnits.getDisplayString() }','${ drugOrdersMain.get(singleOrderExtn.orderId).frequency }','${ singleOrderExtn.refill }','${ singleOrderExtn.isAllergicOrderReasons }','${ singleOrderExtn.associatedDiagnosis.getDisplayString() }','${ singleOrderExtn.priority.getDisplayString() }','${ singleOrderExtn.patientInstructions }','${ singleOrderExtn.pharmacistInstructions }','${ singleOrderExtn.commentForOrderer }','${ singleOrderExtn.orderStatus }')">
                            <div><strong class="wordBreak">${ singleOrderExtn.drugName.getDisplayString().toUpperCase() }</strong></div>
                            <div class="itemSummary"><em>Click to view details</em></div>
                        </td>

                        <td onclick="showDrugOrderViewWindow('VIEW ORDER','${ singleOrderExtn.startDate.format('yyyy-MM-dd') }','${ singleOrderExtn.drugName.getDisplayString() }','${ drugOrdersMain.get(singleOrderExtn.orderId).dose }','${ drugOrdersMain.get(singleOrderExtn.orderId).doseUnits.getDisplayString() }','${ drugOrdersMain.get(singleOrderExtn.orderId).route.getDisplayString() }','${ drugOrdersMain.get(singleOrderExtn.orderId).duration }','${ drugOrdersMain.get(singleOrderExtn.orderId).durationUnits.getDisplayString() }','${ drugOrdersMain.get(singleOrderExtn.orderId).quantity }','${ drugOrdersMain.get(singleOrderExtn.orderId).quantityUnits.getDisplayString() }','${ drugOrdersMain.get(singleOrderExtn.orderId).frequency }','${ singleOrderExtn.refill }','${ singleOrderExtn.isAllergicOrderReasons }','${ singleOrderExtn.associatedDiagnosis.getDisplayString() }','${ singleOrderExtn.priority.getDisplayString() }','${ singleOrderExtn.patientInstructions }','${ singleOrderExtn.pharmacistInstructions }','${ singleOrderExtn.commentForOrderer }','${ singleOrderExtn.orderStatus }')">${ singleOrderExtn.startDate.format('yyyy-MM-dd') }</td>

                        <td>
                            <span id="button">
                                <i class="icon-edit edit-action" title="${ ui.message("Edit") }" onclick="editSingleOrderDetailsWindow('EDIT DRUG ORDER','${ singleOrderExtn.orderId }','${ singleOrderExtn.drugName.getDisplayString() }','${ singleOrderExtn.startDate }','${ drugOrdersMain.get(singleOrderExtn.orderId).dose }','${ drugOrdersMain.get(singleOrderExtn.orderId).doseUnits.getDisplayString() }','${ drugOrdersMain.get(singleOrderExtn.orderId).route.getDisplayString() }','${ drugOrdersMain.get(singleOrderExtn.orderId).duration }','${ drugOrdersMain.get(singleOrderExtn.orderId).durationUnits.getDisplayString() }','${ drugOrdersMain.get(singleOrderExtn.orderId).quantity }','${ drugOrdersMain.get(singleOrderExtn.orderId).quantityUnits.getDisplayString() }','${ drugOrdersMain.get(singleOrderExtn.orderId).frequency }','${ singleOrderExtn.refill }','${ singleOrderExtn.refillInterval }','${ singleOrderExtn.associatedDiagnosis.getDisplayString() }','${ singleOrderExtn.isAllergicOrderReasons }','${ singleOrderExtn.priority.getDisplayString() }','${ singleOrderExtn.patientInstructions }','${ singleOrderExtn.pharmacistInstructions }','${ singleOrderExtn.orderStatus }','${ currentOrders }','${ allergicDrugs }')"></i>
                                <i class="icon-trash delete-action" title="${ ui.message("Discontinue") }" onclick="discardSingleOrder('${ singleOrderExtn.orderId }')"></i>
                            </span>
                        </td>

                    </tr>
                <% } %>

                <% groupOrdersExtn.each { groupOrderExtn -> %>
                    <% def orderList = "" %>
                    <tr class="groupRow">
    
                        <td>
                            <input type="checkbox" name="groupCheckBox" value="${ groupOrderExtn.key }" ng-model="groupCheckBox" />
                            <span class="hidden" id="id">${ groupOrderExtn.key }</span>
                        </td>
                        
                        <td colspan="4" class="groupDetails">
                            <% groupOrderExtn.value.each { order -> %>
                                <% if(order.orderStatus == "Active-Group") { %>

                                    <div class="groupDrug <% if(order.forDiscard == 1) { %> discontinued <% } %> <% if(order.onHold == 1) { %> onhold <% } %>" title="${ ui.message(order.commentForOrderer) }">
                                        
                                        <div class="groupDrugDetails" onclick="showDrugOrderViewWindow('VIEW ORDER','${ order.startDate.format('yyyy-MM-dd') }','${ order.drugName.getDisplayString() }','${ drugOrdersMain.get(order.orderId).dose }','${ drugOrdersMain.get(order.orderId).doseUnits.getDisplayString() }','${ drugOrdersMain.get(order.orderId).route.getDisplayString() }','${ drugOrdersMain.get(order.orderId).duration }','${ drugOrdersMain.get(order.orderId).durationUnits.getDisplayString() }','${ drugOrdersMain.get(order.orderId).quantity }','${ drugOrdersMain.get(order.orderId).quantityUnits.getDisplayString() }','${ drugOrdersMain.get(order.orderId).frequency }','${ order.refill }','${ order.isAllergicOrderReasons }','${ order.associatedDiagnosis.getDisplayString() }','${ order.priority.getDisplayString() }','${ order.patientInstructions }','${ order.pharmacistInstructions }','${ order.commentForOrderer }','${ order.orderStatus }')">
                                            
                                            <div id="groupDrugID">
                                                ${ order.orderId }
                                            </div>
                                            
                                            <div id="groupDrugName">
                                                <div><strong class="wordBreak">${ order.drugName.getDisplayString().toUpperCase() }</strong></div>
                                                <div class="itemSummary"><em>Click to view details</em></div>
                                            </div>
                                            <% orderList = orderList + order.drugName.getDisplayString() + "," %>

                                            <div id="groupDrugDate">${ order.startDate.format('yyyy-MM-dd') }</div>
                                        </div>

                                        <div class="groupDrugButton">
                                            <i class="icon-edit edit-action" title="${ ui.message("Edit") }" onclick="editSingleOrderDetailsWindow('EDIT DRUG ORDER','${ order.orderId }','${ order.drugName.getDisplayString() }','${ order.startDate }','${ drugOrdersMain.get(order.orderId).dose }','${ drugOrdersMain.get(order.orderId).doseUnits.getDisplayString() }','${ drugOrdersMain.get(order.orderId).route.getDisplayString() }','${ drugOrdersMain.get(order.orderId).duration }','${ drugOrdersMain.get(order.orderId).durationUnits.getDisplayString() }','${ drugOrdersMain.get(order.orderId).quantity }','${ drugOrdersMain.get(order.orderId).quantityUnits.getDisplayString() }','${ drugOrdersMain.get(order.orderId).frequency }','${ order.refill }','${ order.refillInterval }','${ order.associatedDiagnosis.getDisplayString() }','${ order.isAllergicOrderReasons }','${ order.priority.getDisplayString() }','${ order.patientInstructions }','${ order.pharmacistInstructions }','${ order.orderStatus }','${ currentOrders }','${ allergicDrugs }')"></i>
                                            <i class="icon-remove edit-action" title="${ ui.message("Ungroup") }" onclick="removeFromGroup('${ order.orderId }')"></i>
                                            <i class="icon-trash delete-action" title="${ ui.message("Discontinue") }" onclick="discardSingleOrder('${ order.orderId }')"></i>
                                        </div>
                                    </div>

                                <% } %>
                            <% } %><br/>
                            
                            <div class="groupButton">
                                <span id="button">
                                    <i class="icon-plus edit-action" title="${ ui.message("ADD DRUG ORDER") }" onclick="showAddOrderToGroupWindow('CREATE DRUG ORDER','${groupOrderExtn.key}')"></i>
                                    <i class="icon-trash delete-action" title="${ ui.message("Discontinue") }" onclick="showDiscardGroupOrderWindow('${groupOrderExtn.key}')"></i>
                                </span>
                            </div>
                        </td>
                        <td style="display: none;"></td>
                        <td style="display: none;"></td>
                        <td style="display: none;"></td>
                    </tr>
                <% } %>
            </tbody>
        </table><br/>
        
        <div class="pull-left">
            <button type="submit" id="confirmOrderGroup" name="action" value="GroupOrder" ng-disabled="!groupCheckBox">Group Selected</button>
        </div>
    </form>
</div>

<div id="removeFromGroupWindow" class="dialog">
    <form id="removeFromGroupForm" method="post">
        <div class="dialog-header">
            <h3 id="dialog-heading">${ ui.message("Remove From Group") }</h3>
        </div>
        <h4 class="align-center"><strong>Remove Order From Group?</strong></h4><br/>
        <input type="hidden" id="removeFromGroup" name="removeFromGroup" />
        
        <button class="confirm right" id="btn-place" type="button">Confirm</button>
        <button class="cancel left" id="btn-place" type="button">Cancel</button>
    </form>
</div>

<script>
    jq('#activeOrdersTable').dataTable({
        "sPaginationType": "full_numbers",
        "bPaginate": true,
        "bAutoWidth": false,
        "bLengthChange": true,
        "bSort": true,
        "bJQueryUI": true,
        "bInfo": true,
        "bFilter": true,
        "columns": [
            { "width": "10%" },
            { "width": "5%"  },
            { "width": "50%" },
            { "width": "20%" },
            { "width": "15%" }
        ],
        fixedColumns: true

    });
</script>

<script type="text/javascript">    
    <!--Highlight row representing individual drug order when selected to be viewed.-->
    jq(".orderRow td:not(:first-child)").click(function(){
        jq(this).parent().children('td').slice(1, 4).css({"background": "#75b2f0","color": "white"});
    });
    
    <!--Highlight row representing group drug order when selected to be viewed.-->
    jq(".groupDrugDetails").click(function(){
        jq(this).css({"background": "#75b2f0","color": "white"});
    });
    
    <!--Highlight row representing group drug order when corresponding action buttons are clicked.-->
    jq(".groupDrugButton").click(function(){
        jq(this).parent().children('.groupDrugDetails').css({"background": "#75b2f0","color": "white"});
    });
    
    <!--Highlight row representing group drug order when corresponding action buttons are hovered upon.-->
    jq(".groupButton").hover(function(event){
        if(event.type == 'mouseenter'){
            jq(this).parent().children('.groupDrug').children('.groupDrugDetails').css({"background": "#75b2f0","color": "white"});
        } else {
            jq(this).parent().children('.groupDrug').children('.groupDrugDetails').css({"background": "","color": ""});
        }
    });
</script>