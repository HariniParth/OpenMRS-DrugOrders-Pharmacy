<%
    ui.decorateWith("appui", "standardEmrPage");
    ui.includeCss("drugorders", "pharmacy.css")
    ui.includeJavascript("drugorders", "pharmacy.js")
    ui.includeJavascript("drugorders", "dataTables.js")
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("Pharmacy") }"},
    ];
</script>

${ ui.includeFragment("drugorders", "searchBarByPatient") } <br/>

<div id="patientTableWrapper">
    <table id="patientTable">
        <thead>
            <tr>
                <th>Name</th>
                <th>Birth Date</th>
                <th>Age</th>
                <th>Gender</th>
                <th>Address</th>
            </tr>
        </thead>
        
        <tbody>
            <% if(patient != "") { %>
                <tr class="patientRow" onclick="location.href='${ ui.pageLink("drugorders", "pharmacyPatient", [patientId: patient.patientId]) }';">     
                    <td><span class="wordBreak">${ patient.givenName } ${ patient.familyName }</span></td>
                    <td>${ patient.birthdate.format('yyyy-MM-dd') }</td>
                    <td>${ patient.age }</td>
                    <td>${ patient.gender }</td>
                    <td><span class="wordBreak">${ patient.addresses.address1[0] } ${ patient.addresses.cityVillage[0] } ${ patient.addresses.stateProvince[0] } - ${ patient.addresses.postalCode[0] } ${ patient.addresses.country[0] }</span></td>
                </tr>
            <% } %>
        </tbody>
    </table>
</div>

${ ui.includeFragment("drugorders", "removeFromHold") } <br/><br/>

<p><strong>ORDERS Hold/Discard</strong></p><br/>

<div id="nonActiveTableWrapper">
    <form method="post" name="nonActiveTableForm" id="nonActiveTableForm">
        <table id="nonActiveTable">
            <thead>
                <tr>
                    <th>Select</th>
                    <th>Patient</th>
                    <th>Drug(s)</th>
                    <th>Start Date</th>
                    <th>Status</th>
                    <th>Provider</th>
                </tr>
            </thead>
            <tbody>
                <% patientPlans.each { order -> %>
                    <% def name = "" %>
                    <tr class="setHome">
                        <td class="selectGroup"><input type="checkbox" name="homeCheckbox" value="PLAN ${ order.key }" /></td>
                        <td colspan="5">
                            <% order.value.each { o -> %>                        
                                <div class="groupHome <% if(o.forDiscard == 1) { %> forDiscard <% } %> <% if(o.onHold == 1) { %> onHold <% } %>">
                                    <div class="l11">
                                        <div class="l21">
                                            <% if(name == "") { %>
                                                <% name = patientName.get(o.patientId.toInteger()) %>
                                                <span class="wordBreak">${ name }</span>
                                            <% } %>
                                        </div>
                                        <div class="l22">
                                            <div class="l31">
                                                <span class="wordBreak">${ o.drugName.getDisplayString().toUpperCase() }</span>
                                            </div>
                                            <div class="l32">
                                                ${ o.startDate.format('yyyy-MM-dd') }
                                            </div>
                                        </div>
                                    </div>
                                    <div class="l12">
                                        <div class="l23">
                                            <% if(o.onHold == 1) { %>
                                                <span class="comments" title="${ o.commentForOrderer }"> ON HOLD</span>
                                            <% } else if(o.forDiscard == 1) { %>
                                                <span class="comments" title="${ o.commentForOrderer }"> DISCARD</span>
                                            <% } else { %>
                                                <span class="comments"> ACTIVE</span>
                                            <% } %>
                                        </div>
                                        <div class="l24">
                                            <span class="wordBreak">${ ordererName.get(o.orderId) }</span>
                                        </div>
                                    </div>
                                </div>
                            <% } %>
                        </td>
                        <td style="display: none;"></td>
                        <td style="display: none;"></td>
                        <td style="display: none;"></td>
                        <td style="display: none;"></td>
                    </tr>
                <% } %>

                <% patientGroups.each { order -> %>
                    <% def name = "" %>
                    <tr class="setHome">
                        <td class="selectGroup"><input type="checkbox" name="homeCheckbox" value="GROUP ${ order.key }" /></td>
                        <td colspan="5">
                            <% order.value.each { o -> %>                            
                                <div class="groupHome <% if(o.forDiscard == 1) { %> forDiscard <% } %> <% if(o.onHold == 1) { %> onHold <% } %>">
                                    <div class="l11">
                                        <div class="l21">
                                            <% if(name == "") { %>
                                                <% name = patientName.get(o.patientId.toInteger()) %>
                                                <span class="wordBreak">${ name }</span>
                                            <% } %>
                                        </div>
                                        <div class="l22">
                                            <div class="l31">
                                                <span class="wordBreak">${ o.drugName.getDisplayString().toUpperCase() }</span>
                                            </div>
                                            <div class="l32">
                                                ${ o.startDate.format('yyyy-MM-dd') }
                                            </div>
                                        </div>
                                    </div>
                                    <div class="l12">
                                        <div class="l23">
                                            <% if(o.onHold == 1) { %>
                                                <span class="comments" title="${ o.commentForOrderer }"> ON HOLD</span>
                                            <% } else if(o.forDiscard == 1) { %>
                                                <span class="comments" title="${ o.commentForOrderer }"> DISCARD</span>
                                            <% } else { %>
                                                <span class="comments"> ACTIVE</span>
                                            <% } %>
                                        </div>
                                        <div class="l24">
                                            <span class="wordBreak">${ ordererName.get(o.orderId) }</span>
                                        </div>
                                    </div>
                                </div>
                            <% } %>
                        </td>
                        <td style="display: none;"></td>
                        <td style="display: none;"></td>
                        <td style="display: none;"></td>
                        <td style="display: none;"></td>
                    </tr>
                <% } %>

                <% patientSingles.each { order -> %>
                    <tr class="singleHome <% if(order.forDiscard == 1) { %> forDiscard <% } %> <% if(order.onHold == 1) { %> onHold <% } %>">
                        <td class="selectSingle"><input type="checkbox" name="homeCheckbox" value="SINGLE ${ order.orderId }" /></td>
                        <td><span class="wordBreak">${ patientName.get(order.patientId.toInteger()) }</span></td>
                        <td><span class="wordBreak">${ order.drugName.getDisplayString().toUpperCase()}</span></td>
                        <td>${ order.startDate.format('yyyy-MM-dd') }</td>
                        <td>
                            <% if(order.onHold == 1) { %>
                                <span class="comments" title="${ order.commentForOrderer }"> ON HOLD</span>
                            <% } else if(order.forDiscard == 1) { %>
                                <span class="comments" title="${ order.commentForOrderer }"> DISCARD</span>
                            <% } else { %>
                                <span class="comments"> ACTIVE</span>
                            <% } %>
                        </td>
                        <td><span class="wordBreak">${ ordererName.get(order.orderId) }</span></td>
                    </tr>
                <% } %>
            </tbody>
        </table> <br/><br/>

        <div class="pull-right">
            <button type="button" onclick="showRemoveOrderHoldWindow()">Remove Hold</button>
        </div>
    </form>
</div>

<script>
    jq('#patientTable').dataTable({
        "sPaginationType": "full_numbers",
        "bPaginate": true,
        "bAutoWidth": false,
        "bLengthChange": true,
        "bSort": true,
        "bJQueryUI": true,
        "bInfo": true,
        "bFilter": false,
        "columns": [
            { "width": "20%" },
            { "width": "15%" },
            { "width": "5%"  },
            { "width": "10%" },
            { "width": "50%" }
        ],
        fixedColumns: true

    });
</script>

<script>
    jq('#nonActiveTable').dataTable({
        "sPaginationType": "full_numbers",
        "bPaginate": true,
        "bAutoWidth": false,
        "bLengthChange": true,
        "bSort": true,
        "bJQueryUI": true,
        "bInfo": true,
        "bFilter": false,
        "columns": [
            { "width": "5%"  },
            { "width": "20%" },
            { "width": "33%" },
            { "width": "12%" },
            { "width": "10%" },
            { "width": "20%" }
        ],
        fixedColumns: true

    });
</script>