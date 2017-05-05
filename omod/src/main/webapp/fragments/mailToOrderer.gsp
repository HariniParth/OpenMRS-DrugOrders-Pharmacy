<%
    ui.includeCss("drugorders", "pharmacy.css")
    ui.includeJavascript("drugorders", "pharmacy.js")
%>

<% if(groupAction == "On Hold" || groupAction == "Discard") { %>
    <div id="mailWindow" class="dialog">
        <form method="post" id="mailOrdererForm">
            <div class="dialog-header">
                <h3 id="dialog-heading">${ ui.message("Mail To Orderer") }</h3>
            </div>

            <input type="hidden" id="drugNames" value="${ drugNames }" />
            <table border="0">
                <tr>
                    <td>From:</td>
                    <td><input type="text" id="sender"  name="sender" value="${ sender }" readonly="true" /></td>
                </tr>
                <tr>
                    <td>To:</td>
                    <td><input type="text" id="recipient" name="recipient" value="${ recipient }" readonly="true" /></td>
                </tr>
                <tr>
                    <td>Subject:</td>
                    <td><input type="text" id="subject" name="subject" value="Order ID(s): ${ orderList } - Order Status: ${ groupAction }" readonly="true" /></td>
                </tr>
                <tr>
                    <td>Message:</td>
                    <td><textarea rows="10" id="message" name="message">This is to inform you that the following Order(s) Status is set to ${ groupAction }&#10;&#10;Patient ID: ${ patientID }&#10;Patient Name: ${ patientName }&#10;&#10;Orders:&#10;&#10;${ orderDetails }&#10;Comments: ${ groupComments }</textarea></td>
                </tr>
            </table><br/>
            
            <div class="fields">
                <input class="confirm right" id="btn-place" type="submit" name="action" value="Send" />
                <input class="cancel left" id="btn-place" type="button" value="Cancel" onclick="closeMailWindow()" />
            </div>
        </form>
    </div>
<% } else if(ordererName != "") { %>
    <div id="mailWindow" class="dialog">
        <form method="post" id="mailOrdererForm">
            <div class="dialog-header">
                <h3 id="dialog-heading">${ ui.message("Mail To Orderer") }</h3>
            </div>

            <input type="hidden" id="drugNames" value="${ orderName };" />
            <table border="0">
                <tr>
                    <td>From:</td>
                    <td><input type="text" id="sender"  name="sender" value="${ sender }" readonly="true" /></td>
                </tr>
                <tr>
                    <td>To:</td>
                    <td><input type="text" id="recipient" name="recipient" value="${ ordererName }" readonly="true" /></td>
                </tr>
                <tr>
                    <td>Subject:</td>
                    <td><input type="text" id="subject" name="subject" value="Order ID(s): ${ orderNumber }" /></td>
                </tr>
                <tr>
                    <td>Message:</td>
                    <td><textarea rows="10" id="message" name="message"></textarea></td>
                </tr>
            </table><br/>
            <div class="fields">
                <input class="confirm right" id="btn-place" type="submit" name="action" value="Send" />
                <input class="cancel left" id="btn-place" type="button" value="Cancel" onclick="closeMailWindow()" />
            </div>
        </form>
    </div>
<% } %>

<script type="text/javascript">
    jq(function(){
        jq("#mailOrdererForm").submit(function(){
            var sender = jq("#sender").val();
            var recipient = jq("#recipient").val();
            var subject = jq("#subject").val();
            var message = jq("#message").val();
            
            jq.ajax({
                type: "POST",
                url: "${ ui.actionLink('contactOrderer') }",
                data: {
                    'sender': sender,
                    'recipient': recipient,
                    'subject': subject,
                    'message': message
                }
            });
        });
    });
</script>