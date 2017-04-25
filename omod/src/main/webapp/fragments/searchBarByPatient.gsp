<%
    ui.includeCss("drugorders", "pharmacy.css")
%>

<div id="patient-search-bar">
    <form method="post" id="searchByPatient">
        <p class="fields"><strong>FIND PATIENT</strong>
            <input id="patient_full_name" type="text" name="patient_full_name" placeholder="Enter Patient Name" oninput="autoCompletePatientName('${allPatientNames}')" />
            <span id="clear" onclick="clearPatientTableFilters()">Clear Filters</span>
        </p>
    </form>
</div>