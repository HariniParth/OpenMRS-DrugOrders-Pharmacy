/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.drugorders.fragment.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.openmrs.module.drugorders.api.drugordersService;
import org.openmrs.module.drugorders.drugorders;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author harini-geek
 */
public class MailToOrdererFragmentController {
    
    protected UserContext userContext;
    
    public void controller(FragmentModel model, @RequestParam("patientId") Patient patient,
                            @RequestParam(value = "pharmaGroupAction", required = false) String groupAction,
                            @RequestParam(value = "groupComments", required = false) String groupComments,
                            @RequestParam(value = "groupCheckBox", required=false) long[] groupCheckBox,
                            @RequestParam(value = "ordererName", required = false) String ordererName,
                            @RequestParam(value = "orderNumber", required = false) String orderNumber,
                            @RequestParam(value = "orderName", required = false) String orderName) throws ParseException{
       
        model.addAttribute("groupAction", groupAction);
        model.addAttribute("ordererName", ordererName);
        model.addAttribute("orderNumber", orderNumber);
        model.addAttribute("orderName", orderName);
        model.addAttribute("patientID", patient.getPatientId());
        model.addAttribute("patientName", patient.getGivenName()+" "+patient.getFamilyName());
        
        String sender = "";
        String recipient = "";        
        String orderList = "";    
        String drugNames = "";
        String orderDetails = "";
        
        /*
          If the check-boxes corresponding to one or more orders are checked to be put on hold or discarded,
          fetch the details of the selected orders to be updated in the mail fragment.
        */
        for(int i=0;i<groupCheckBox.length;i++){   
            // Retrieve the ID of each order that is saved in the check-box corresponding to the order
            int orderID = Integer.parseInt(Long.toString(groupCheckBox[i]));
            // Store the string of order IDs.
            orderList = orderList.concat(Long.toString(groupCheckBox[i])+" ");
            // Retrieve the corresponding order record.
            Order order = Context.getOrderService().getOrder(orderID);
            // Retrieve the corresponding drug order record.
            drugorders drugorder = Context.getService(drugordersService.class).getDrugOrderByOrderID(orderID);
            // Store the string of the name of the drugs.
            drugNames = drugNames.concat(drugorder.getDrugName().getDisplayString().toUpperCase()+";");
            // Store the details of the selected orders.
            orderDetails = orderDetails.concat("Order ID: "+Integer.toString(drugorder.getOrderId())+"\nDrug: "+drugorder.getDrugName().getDisplayString().toUpperCase()+"\nStart Date: "+drugorder.getStartDate().toString()+"\n\n");
            // Set recipient of the mail to be the orderer.
            if(recipient.equals(""))
                recipient = order.getOrderer().getName();
        }
        
        // Sender of the email is the given user (Pharmacist)
        userContext = Context.getUserContext();
        if(sender.equals(""))
            sender = userContext.getAuthenticatedUser().getGivenName() + " " + userContext.getAuthenticatedUser().getFamilyName();
        
        model.addAttribute("sender", sender);
        model.addAttribute("recipient", recipient);
        model.addAttribute("orderList", orderList);
        model.addAttribute("drugNames", drugNames);
        model.addAttribute("orderDetails", orderDetails);
        model.addAttribute("groupComments", groupComments);
    }
    
    public void contactOrderer(@RequestParam(value = "sender", required = false) String sender,
                                @RequestParam(value = "recipient", required = false) String recipient,
                                @RequestParam(value = "subject", required = false) String subject,
                                @RequestParam(value = "message", required = false) String message){
        
        final String username = "";
        final String password = "";

        /*
          Use Google's SMTP service to send emails.
          Specify the values of the following properties to enable mailing.
        */
        Properties props = new Properties();
        props.put("mail.smtp.auth", "");
        props.put("mail.smtp.starttls.enable", "");
        props.put("mail.smtp.host", "");
        props.put("mail.smtp.port", "");
        
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        
        try {
            Message mail = new MimeMessage(session);
            mail.setFrom(new InternetAddress(""));
            mail.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            mail.setSubject(subject);
            mail.setText(message);
            Transport.send(mail);
        }
        catch (MessagingException e) {
                throw new RuntimeException(e);
        }
        
    }
}
