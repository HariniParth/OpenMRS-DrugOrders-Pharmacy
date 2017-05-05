<%
    ui.includeCss("drugorders", "pharmacy.css")
    ui.includeJavascript("drugorders", "dataTables.js")
    def last_dispatch_date = "";
%>

<form method="post" id="groupOrdersForm">
    
    <input type="hidden" id="planID" name="planID" />
    <input type="hidden" id="orderID" name="orderID" />
    <input type="hidden" id="groupID" name="groupID" />
    <input type="hidden" name="orderName" id="orderName" />
    <input type="hidden" name="orderNumber" id="orderNumber" />
    <input type="hidden" name="ordererName" id="ordererName" />
    
    <div id="activeOrdersWindow">
        <table id="currentGroupOrdersTable">
            <thead>
                <tr>
                    <th>Plan Name</th>
                    <th>Drug(s)</th>
                    <th>Start Date</th>
                    <th>Refills</th>
                    <th>Last Dispatch</th>
                    <th>Orderer</th>
                </tr>
            </thead>
            <tbody>
                <% patientPlanOrders.each { patientPlanOrder -> %>
                    <tr class="groupRow">
                        <td><span class="wordBreak">${ planName.get(patientPlanOrder.key).getDisplayString() }</span></td>
                        <td colspan="5">
                            <% patientPlanOrder.value.each { order -> %>
                                
                                <div class="groupElement <% if(order.forDiscard == 1) { %> forDiscard <% } %> <% if(order.onHold == 1) { %> onHold <% } %>" onclick="selectedPlanOrder('${ patientPlanOrder.key }')" title="${ ui.message(order.commentForOrderer) }">
                                    
                                    <div class="d1">
                                        <div class="e1">
                                            <div class="g1">
                                                <div class="c1"><span class="wordBreak">${ order.drugName.getDisplayString().toUpperCase() }</span></div>
                                                <div class="c2"></div>
                                            </div>
                                            <div class="g2">
                                                <div class="c3">${ order.startDate.format('yyyy-MM-dd') }</div>
                                                <div class="c4"></div>
                                            </div>
                                        </div>

                                        <div class="e2">
                                            <div class="g3">${ order.refill }</div>

                                            <% if(order.lastDispatchDate != null) { %>
                                                <% last_dispatch_date = order.lastDispatchDate.format('yyyy-MM-dd'); %>
                                            <% } else { %>
                                                <% last_dispatch_date = "NA"; %>
                                            <% } %>
                                            <div class="g4">${ last_dispatch_date }</div>
                                        </div>
                                    </div>
                                </div>
                                
                                <div class="ordererID">
                                    <div class="g5"></div>
                                    <div class="g6" onclick="showOrdererContact('${ OrdererName.get(order.orderId) }','${ order.orderId }','${ order.drugName.getDisplayString().toUpperCase() }')"><span class="wordBreak">${ OrdererName.get(order.orderId) }</span></div>
                                </div>
                            
                            <% } %>
                        </td>
                        <td style="display: none;"></td>
                        <td style="display: none;"></td>
                        <td style="display: none;"></td>
                        <td style="display: none;"></td>
                    </tr>
                <% } %>
                
                <% patientGroupOrders.each { patientGroupOrder -> %>
                    <tr class="groupRow">
                        <td><span class="hidden">${ patientGroupOrder.key }</span></td>
                        <td colspan="5">
                            <% patientGroupOrder.value.each { order -> %>
                                
                                <div class="groupElement <% if(order.forDiscard == 1) { %> forDiscard <% } %> <% if(order.onHold == 1) { %> onHold <% } %>" onclick="selectedGroupOrder('${ patientGroupOrder.key }')" title="${ ui.message(order.commentForOrderer) }">
                                    
                                    <div class="d1">
                                        <div class="e1">
                                            <div class="g1">
                                                <div class="c1"><span class="wordBreak">${ order.drugName.getDisplayString().toUpperCase() }</span></div>
                                                <div class="c2"></div>
                                            </div>
                                            <div class="g2">
                                                <div class="c3">${ order.startDate.format('yyyy-MM-dd') }</div>
                                                <div class="c4"></div>
                                            </div>
                                        </div>
                                        <div class="e2">
                                            <div class="g3">${ order.refill }</div>

                                            <% if(order.lastDispatchDate != null) { %>
                                                <% last_dispatch_date = order.lastDispatchDate.format('yyyy-MM-dd'); %>
                                            <% } else { %>
                                                <% last_dispatch_date = "NA"; %>
                                            <% } %>
                                            <div class="g4">${ last_dispatch_date }</div>
                                        </div>
                                    </div>
                                </div>
                                
                                <div class="ordererID">
                                    <div class="g5"></div>
                                    <div class="g6" onclick="showOrdererContact('${ OrdererName.get(order.orderId) }','${ order.orderId }','${ order.drugName.getDisplayString().toUpperCase() }')"><span class="wordBreak">${ OrdererName.get(order.orderId) }</span></div>
                                </div><br/><br/>
                            
                            <% } %>
                        </td>
                        <td style="display: none;"></td>
                        <td style="display: none;"></td>
                        <td style="display: none;"></td>
                        <td style="display: none;"></td>
                    </tr>
                <% } %>

                <% patientSingleOrders.each { patientSingleOrder -> %>
                    <tr class="singleRow <% if(patientSingleOrder.forDiscard == 1) { %> forDiscard <% } %> <% if(patientSingleOrder.onHold == 1) { %> onHold <% } %>">
                        
                        <td><span class="hidden">${ patientSingleOrder.orderId }</span></td>
                        <td onclick="selectedSingleOrder('${ patientSingleOrder.orderId }')" title="${ ui.message(patientSingleOrder.commentForOrderer) }"><span class="wordBreak">${ patientSingleOrder.drugName.getDisplayString().toUpperCase() }</span></td>
                        <td onclick="selectedSingleOrder('${ patientSingleOrder.orderId }')" title="${ ui.message(patientSingleOrder.commentForOrderer) }">${ patientSingleOrder.startDate.format('yyyy-MM-dd') }</td>
                        <td onclick="selectedSingleOrder('${ patientSingleOrder.orderId }')" title="${ ui.message(patientSingleOrder.commentForOrderer) }">${ patientSingleOrder.refill }</td>

                        <% if(patientSingleOrder.lastDispatchDate != null) { %>
                            <% last_dispatch_date = patientSingleOrder.lastDispatchDate.format('yyyy-MM-dd'); %>
                        <% } else { %>
                            <% last_dispatch_date = "NA"; %>
                        <% } %>
                        <td onclick="selectedSingleOrder('${ patientSingleOrder.orderId }')" title="${ ui.message(patientSingleOrder.commentForOrderer) }">${ last_dispatch_date }</td>
                        
                        <td class="g6" onclick="showOrdererContact('${ OrdererName.get(patientSingleOrder.orderId) }','${ patientSingleOrder.orderId }','${ patientSingleOrder.drugName.getDisplayString().toUpperCase() }')"><span class="wordBreak">${ OrdererName.get(patientSingleOrder.orderId) }</span></td>
                    </tr> 
                <% } %>

            </tbody>
        </table>
    </div>
</form>

<script type="text/javascript">
    
    jq('#currentGroupOrdersTable').dataTable({
        "sPaginationType": "full_numbers",
        "bPaginate": true,
        "bAutoWidth": false,
        "bLengthChange": true,
        "bSort": true,
        "bJQueryUI": true,
        "bInfo": true,
        "bFilter": true,
        "columns": [
            { "width": "15%" },
            { "width": "23%" },
            { "width": "16%" },
            { "width": "10%" },
            { "width": "16%" },
            { "width": "20%" }
        ],
        fixedColumns: true

    });
</script>