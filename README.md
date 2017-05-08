# OpenMRS-DrugOrders-Pharmacy
E-prescription and Pharmacy software system

This is the code developed for Drug Orders and Pharmacy module.
The module has four pages (Administration page, Drug Orders page, Pharmacy home page and Pharmacy Patient page).

- Path to controller: 

https://github.com/HariniParth/OpenMRS-DrugOrders-Pharmacy/tree/master/omod/src/main/java/org/openmrs/module/drugorders

- Path to view: 

https://github.com/HariniParth/OpenMRS-DrugOrders-Pharmacy/tree/master/omod/src/main/webapp


The Administration page displays the list of defined medication plans or regimens.
It also hosts the Administration fragment which provides forms and blocks to view, create and discard medication plans.
The AdministrationPageController has methods to take appropriate actions when 
 - a new plan is defined
 - a drug is added to a plan
 - a plan is renamed
 - a plan is discarded
 - part of a plan is discarded
 
 ===============================
 
The Drug Orders page displays the list of active and non-active drug orders placed for the Patient.
It also hosts fragments that contains forms and blocks to create a new drug order, select and order medication plans, edit a drug order, view the details of an order, discard drug orders, renew drug orders.
The DrugordersPageController has methods to take appropriate actions to
 - create a drug order
 - create a medication plan order
 - edit a drug order
 - discard/renew a drug order
 - discard/renew a group of drug orders
 - discard/renew a set of drug orders that are made for a med plan
 
 ===============================
  
 The Pharmacy home page displays a search widget to search for a Patient using her/his name.
 It also displays a table listing the drug orders that are currently put on hold or requested to be discarded.
  
 ===============================
  
 The Pharmacy Patient page displays the list of active drug orders (individual, group and medication plan) for a Paient.
 It hosts a fragments that display the details of the selected drug order(s) and a mail fragment with options to send an email to the orderer.
 The PharmacyPageController has methods to take appropriate actions to
  - mark a drug order dispatched
  - put a drug order on hold
  - request a drug order to be discontinued
  
  The MailToOrderer fragment provides methods to send email to the orderer using Google's SMTP service.
  This functionality has been tested by providing personal emailID/passwd.
