<%
    ui.includeCss("drugorders", "drugorders.css")
    ui.includeJavascript("drugorders", "drugorders.js")
%>

<!--
    Fragment to confirm saving of med plan orders in draft status.
-->

<div id="saveDraftPlan" class="dialog">
    <form method="post" id="saveDraftPlanForm">
        <div class="dialog-header">
            <h3 id="dialog-heading">${ ui.message("Save Draft Orders") }</h3>
        </div>

        <h4 id="heading"><strong>Save draft med plan orders?</strong></h4><br/>
        <input type="hidden" name="action" id="saveDraft" />

        <div class="fields">
            <button class="confirm right" id="btn-place" type="button" onclick="saveDraftOrders()">Confirm</button>
            <button class="cancel left" id="btn-place" type="button">Cancel</button>
        </div>
    </form>
</div>