<!--
    Fragment displaying the list of active drug orders on the Patient dashboard.
-->

<%
    ui.includeCss("drugorders", "drugorders.css")
%>

<div class="info-section">
    <div class="info-header">
        <i class="icon-medicine"></i>
        <h3>${ ui.message("drugorders.drugorders").toUpperCase() }</h3>
        <i class="icon-pencil edit-action right" title="${ ui.message("Add") }" onclick="location.href='${ui.pageLink("drugorders", "administration", [patientId: patient.patient.id])}';"></i>
    </div>
    <div class="info-body wordBreak" id="dashboardOrderList">
        <% drugorders.each { order -> %>
            ${ order.drugName.getDisplayString().toUpperCase() } <br/>
        <% } %>
    </div>
</div>