<%
    ui.includeCss("drugorders", "pharmacy.css")
    ui.includeJavascript("drugorders", "pharmacy.js")
%>

<!--
    Mail fragment with fields displaying the Sender name, Receiver name, order ID in subject line and comments.
-->

<% if(groupAction == "On Hold" || groupAction == "Discard") { %>

    <a href="mailto:<${ recipient }>?Subject=Order ID(s): ${ orderList } - Order Status: ${ groupAction }&body=This is to inform you that the following Order(s) Status is set to ${ groupAction }%0A%0APatient ID: ${ patientID }%0A%0APatient Name: ${ patientName }%0A%0A%0A${ orderDetails }%0AComments: ${ groupComments }" target="_top">Send Mail</a> 
    
<% } else if(ordererName != "") { %>

    <a href="mailto:<${ ordererName }>?Subject=Order ID(s): ${ orderNumber }&body=" target="_top">Send Mail</a> 

<% } %>