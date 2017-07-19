<%
    ui.includeCss("drugorders", "pharmacy.css")
    ui.includeJavascript("drugorders", "pharmacy.js")
%>

<!--
    Fragment displaying Orderer Email and Phone Number
-->

<div id="contactOrderer" class="dialog">
    <div class="dialog-header">
        <h3 id="dialog-heading">${ ui.message("Contact Orderer") }</h3>
    </div>
    <br/>

    <div class="fields" id="view_order_detail">
        <div id="order_label">Email: </div>
        <div id="order_value"><a href="mailto:<Recipient Address<>>?Subject=&body=" target="_top"><text id="ordererEmail"></text></a></div>
    </div>
    
    <div class="fields" id="view_order_detail">
        <div id="order_label">Phone: </div>
        <div id="order_value"><text id="ordererPhone"></text></div>
    </div>
</div>