<%
    ui.includeCss("drugorders", "pharmacy.css")
    ui.includeJavascript("drugorders", "pharmacy.js")
%>

<div id="removeHold" class="dialog">
    <div class="dialog-header">
        <h3 id="dialog-heading">${ ui.message("Remove Order Hold") }</h3>
    </div>

    <h4 id="heading"><strong>Remove hold on selected Order(s)?</strong></h4><br/>

    <div class="fields">
        <button class="confirm right" id="btn-place" type="button" onclick="removeHoldOnOrders()">Confirm</button>
        <button class="cancel left" id="btn-place" type="button">Cancel</button>
    </div>
</div>