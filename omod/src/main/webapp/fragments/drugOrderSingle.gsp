<%
    ui.includeCss("drugorders", "drugorders.css")
    ui.includeJavascript("drugorders", "dataTables.js")
%>

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
                <% if(existingDrugOrdersExtension.size() == 0 && existingDrugOrderGroups.size() == 0) { %>
                    <tr>
                        <td colspan="5" align="center">No Orders Found</td>
                        <td style="display: none;"></td>
                        <td style="display: none;"></td>
                        <td style="display: none;"></td>
                        <td style="display: none;"></td>
                    </tr>
                <% } %>

                <% existingDrugOrdersExtension.each { existingDrugOrderExtension -> %>
                    <tr class="orderRow <% if(existingDrugOrderExtension.forDiscard == 1) { %> discontinued <% } %> <% if(existingDrugOrderExtension.onHold == 1) { %> onhold <% } %>" title="${ ui.message(existingDrugOrderExtension.commentForOrderer) }">

                        <td><input type="checkbox" name="singleCheckBox" value="${ existingDrugOrderExtension.orderId }" ng-model="groupCheckBox" /></td>

                        <td class="orderInfo" onclick="showDrugOrderViewWindow('VIEW ORDER','${ ui.format(patient.givenName) }','${ ui.format(patient.familyName) }','${ existingDrugOrderExtension.startDate.format('yyyy-MM-dd') }','${ existingDrugOrderExtension.drugName.getDisplayString() }','${ existingDrugOrdersMain.get(existingDrugOrderExtension.orderId).dose }','${ existingDrugOrdersMain.get(existingDrugOrderExtension.orderId).doseUnits.getDisplayString() }','${ existingDrugOrdersMain.get(existingDrugOrderExtension.orderId).route.getDisplayString() }','${ existingDrugOrdersMain.get(existingDrugOrderExtension.orderId).duration }','${ existingDrugOrdersMain.get(existingDrugOrderExtension.orderId).durationUnits.getDisplayString() }','${ existingDrugOrdersMain.get(existingDrugOrderExtension.orderId).quantity }','${ existingDrugOrdersMain.get(existingDrugOrderExtension.orderId).quantityUnits.getDisplayString() }','${ existingDrugOrdersMain.get(existingDrugOrderExtension.orderId).frequency }','${ existingDrugOrderExtension.refill }','${ existingDrugOrderExtension.isAllergicOrderReasons }','${ existingDrugOrderExtension.priority.getDisplayString() }','${ existingDrugOrderExtension.patientInstructions }','${ existingDrugOrderExtension.pharmacistInstructions }','${ existingDrugOrderExtension.commentForOrderer }')">${ existingDrugOrderExtension.orderId }</td>
                        
                        <td class="orderInfo" onclick="showDrugOrderViewWindow('VIEW ORDER','${ ui.format(patient.givenName) }','${ ui.format(patient.familyName) }','${ existingDrugOrderExtension.startDate.format('yyyy-MM-dd') }','${ existingDrugOrderExtension.drugName.getDisplayString() }','${ existingDrugOrdersMain.get(existingDrugOrderExtension.orderId).dose }','${ existingDrugOrdersMain.get(existingDrugOrderExtension.orderId).doseUnits.getDisplayString() }','${ existingDrugOrdersMain.get(existingDrugOrderExtension.orderId).route.getDisplayString() }','${ existingDrugOrdersMain.get(existingDrugOrderExtension.orderId).duration }','${ existingDrugOrdersMain.get(existingDrugOrderExtension.orderId).durationUnits.getDisplayString() }','${ existingDrugOrdersMain.get(existingDrugOrderExtension.orderId).quantity }','${ existingDrugOrdersMain.get(existingDrugOrderExtension.orderId).quantityUnits.getDisplayString() }','${ existingDrugOrdersMain.get(existingDrugOrderExtension.orderId).frequency }','${ existingDrugOrderExtension.refill }','${ existingDrugOrderExtension.isAllergicOrderReasons }','${ existingDrugOrderExtension.priority.getDisplayString() }','${ existingDrugOrderExtension.patientInstructions }','${ existingDrugOrderExtension.pharmacistInstructions }','${ existingDrugOrderExtension.commentForOrderer }')">
                            <div><strong>${ existingDrugOrderExtension.drugName.getDisplayString().toUpperCase() }</strong></div>
                            <div class="itemSummary"><em>Click to view details</em></div>
                        </td>

                        <td class="orderInfo" onclick="showDrugOrderViewWindow('VIEW ORDER','${ ui.format(patient.givenName) }','${ ui.format(patient.familyName) }','${ existingDrugOrderExtension.startDate.format('yyyy-MM-dd') }','${ existingDrugOrderExtension.drugName.getDisplayString() }','${ existingDrugOrdersMain.get(existingDrugOrderExtension.orderId).dose }','${ existingDrugOrdersMain.get(existingDrugOrderExtension.orderId).doseUnits.getDisplayString() }','${ existingDrugOrdersMain.get(existingDrugOrderExtension.orderId).route.getDisplayString() }','${ existingDrugOrdersMain.get(existingDrugOrderExtension.orderId).duration }','${ existingDrugOrdersMain.get(existingDrugOrderExtension.orderId).durationUnits.getDisplayString() }','${ existingDrugOrdersMain.get(existingDrugOrderExtension.orderId).quantity }','${ existingDrugOrdersMain.get(existingDrugOrderExtension.orderId).quantityUnits.getDisplayString() }','${ existingDrugOrdersMain.get(existingDrugOrderExtension.orderId).frequency }','${ existingDrugOrderExtension.refill }','${ existingDrugOrderExtension.isAllergicOrderReasons }','${ existingDrugOrderExtension.priority.getDisplayString() }','${ existingDrugOrderExtension.patientInstructions }','${ existingDrugOrderExtension.pharmacistInstructions }','${ existingDrugOrderExtension.commentForOrderer }')">${ existingDrugOrderExtension.startDate.format('yyyy-MM-dd') }</td>

                        <td>
                            <span id="button">
                                <i class="icon-edit edit-action" title="${ ui.message("Edit") }" onclick="showEditSingleOrderWindow('EDIT DRUG ORDER','SINGLE','${ existingDrugOrderExtension.orderId }','${ existingDrugOrderExtension.drugName.getDisplayString() }','${ existingDrugOrderExtension.startDate }','${ existingDrugOrdersMain.get(existingDrugOrderExtension.orderId).dose }','${ existingDrugOrdersMain.get(existingDrugOrderExtension.orderId).doseUnits.getDisplayString() }','${ existingDrugOrdersMain.get(existingDrugOrderExtension.orderId).route.getDisplayString() }','${ existingDrugOrdersMain.get(existingDrugOrderExtension.orderId).duration }','${ existingDrugOrdersMain.get(existingDrugOrderExtension.orderId).durationUnits.getDisplayString() }','${ existingDrugOrdersMain.get(existingDrugOrderExtension.orderId).quantity }','${ existingDrugOrdersMain.get(existingDrugOrderExtension.orderId).quantityUnits.getDisplayString() }','${ existingDrugOrdersMain.get(existingDrugOrderExtension.orderId).frequency }','${ existingDrugOrderExtension.refill }','${ existingDrugOrderExtension.refillInterval }','${ existingDrugOrderExtension.associatedDiagnosis.getDisplayString() }','${ existingDrugOrderExtension.isAllergicOrderReasons }','${ existingDrugOrderExtension.priority.getDisplayString() }','${ existingDrugOrderExtension.patientInstructions }','${ existingDrugOrderExtension.pharmacistInstructions }')"></i>
                                <i class="icon-trash delete-action" title="${ ui.message("Discontinue") }" onclick="discardSingleOrder('${ existingDrugOrderExtension.orderId }')"></i>
                            </span>
                        </td>

                    </tr>
                <% } %>

                <% existingDrugOrderGroups.each { existingDrugOrder -> %>
                    <% def orderList = "" %>
                    <tr class="groupRow">
    
                        <td><input type="checkbox" name="groupCheckBox" value="${ existingDrugOrder.key }" ng-model="groupCheckBox" /></td>
                        <td colspan="4" class="groupDetails">
                            <% existingDrugOrder.value.each { existingOrder -> %>
                                <% if(existingOrder.orderStatus == "Active-Group") { %>

                                    <div class="groupDrug <% if(existingOrder.forDiscard == 1) { %> discontinued <% } %> <% if(existingOrder.onHold == 1) { %> onhold <% } %>" title="${ ui.message(existingOrder.commentForOrderer) }">
                                        
                                        <div class="groupDrugDetails" onclick="showDrugOrderViewWindow('VIEW ORDER','${ ui.format(patient.givenName) }','${ ui.format(patient.familyName) }','${ existingOrder.startDate.format('yyyy-MM-dd') }','${ existingOrder.drugName.getDisplayString() }','${ existingDrugOrdersMain.get(existingOrder.orderId).dose }','${ existingDrugOrdersMain.get(existingOrder.orderId).doseUnits.getDisplayString() }','${ existingDrugOrdersMain.get(existingOrder.orderId).route.getDisplayString() }','${ existingDrugOrdersMain.get(existingOrder.orderId).duration }','${ existingDrugOrdersMain.get(existingOrder.orderId).durationUnits.getDisplayString() }','${ existingDrugOrdersMain.get(existingOrder.orderId).quantity }','${ existingDrugOrdersMain.get(existingOrder.orderId).quantityUnits.getDisplayString() }','${ existingDrugOrdersMain.get(existingOrder.orderId).frequency }','${ existingOrder.refill }','${ existingOrder.isAllergicOrderReasons }','${ existingOrder.priority.getDisplayString() }','${ existingOrder.patientInstructions }','${ existingOrder.pharmacistInstructions }','${ existingOrder.commentForOrderer }')">
                                            
                                            <div class="orderInfo" id="groupDrugID">
                                                ${ existingOrder.orderId }
                                            </div>
                                            
                                            <div class="orderInfo" id="groupDrugName">
                                                <div><strong>${ existingOrder.drugName.getDisplayString().toUpperCase() }</strong></div>
                                                <div class="itemSummary"><em>Click to view details</em></div>
                                            </div>
                                            <% orderList = orderList + existingOrder.drugName.getDisplayString() + "," %>

                                            <div class="orderInfo" id="groupDrugDate">${ existingOrder.startDate.format('yyyy-MM-dd') }</div>
                                        </div>

                                        <div class="groupDrugButton">
                                            <i class="icon-edit edit-action" title="${ ui.message("Edit") }" onclick="showEditSingleOrderWindow('EDIT DRUG ORDER','GROUP','${ existingOrder.orderId }','${ existingOrder.drugName.getDisplayString() }','${ existingOrder.startDate }','${ existingDrugOrdersMain.get(existingOrder.orderId).dose }','${ existingDrugOrdersMain.get(existingOrder.orderId).doseUnits.getDisplayString() }','${ existingDrugOrdersMain.get(existingOrder.orderId).route.getDisplayString() }','${ existingDrugOrdersMain.get(existingOrder.orderId).duration }','${ existingDrugOrdersMain.get(existingOrder.orderId).durationUnits.getDisplayString() }','${ existingDrugOrdersMain.get(existingOrder.orderId).quantity }','${ existingDrugOrdersMain.get(existingOrder.orderId).quantityUnits.getDisplayString() }','${ existingDrugOrdersMain.get(existingOrder.orderId).frequency }','${ existingOrder.refill }','${ existingOrder.refillInterval }','${ existingOrder.associatedDiagnosis.getDisplayString() }','${ existingOrder.isAllergicOrderReasons }','${ existingOrder.priority.getDisplayString() }','${ existingOrder.patientInstructions }','${ existingOrder.pharmacistInstructions }')"></i>
                                            <i class="icon-remove edit-action" title="${ ui.message("Ungroup") }" onclick="removeFromGroup('${ existingOrder.orderId }')"></i>
                                            <i class="icon-trash delete-action" title="${ ui.message("Discontinue") }" onclick="discardSingleOrder('${ existingOrder.orderId }')"></i>
                                        </div>
                                    </div>

                                <% } %>
                            <% } %><br/>
                            
                            <div class="groupButton">
                                <span id="button">
                                    <i class="icon-plus edit-action" title="${ ui.message("ADD DRUG ORDER") }" onclick="showAddOrderToGroupWindow('CREATE DRUG ORDER','${existingDrugOrder.key}')"></i>
                                    <i class="icon-trash delete-action" title="${ ui.message("Discontinue") }" onclick="showDiscardGroupOrderWindow('${existingDrugOrder.key}')"></i>
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
    jq(".orderRow td:not(:first-child)").click(function(){
        jq(this).parent().children('td').slice(1, 4).css({"background": "#75b2f0","color": "white"});
    });
</script>
    
<script type="text/javascript">    
    jq(".groupDrugDetails").click(function(){
        jq(this).css({"background": "#75b2f0","color": "white"});
    });
</script>
    
<script type="text/javascript">    
    jq(".groupDrugButton").click(function(){
        jq(this).parent().children('.groupDrugDetails').css({"background": "#75b2f0","color": "white"});
    });
</script>
    
<script type="text/javascript">    
    jq(".groupButton").hover(function(event){
        if(event.type == 'mouseenter'){
            jq(this).parent().children('.groupDrug').children('.groupDrugDetails').css({"background": "#75b2f0","color": "white"});
        } else {
            jq(this).parent().children('.groupDrug').children('.groupDrugDetails').css({"background": "","color": ""});
        }
    });
    
</script>