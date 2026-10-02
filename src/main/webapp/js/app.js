// ---- Session guard: only logged-in staff may use the system ----
const staff = JSON.parse(sessionStorage.getItem('staff') || 'null');
if (!staff) window.location.href = 'login.html';
document.getElementById('who').textContent = staff ? staff.fullName + ' (' + staff.role + ')  ' : '';

const msgBox = document.getElementById('msg');
const $ = id => document.getElementById(id);

function notify(text, type) {
    msgBox.textContent = text;
    msgBox.className = 'msg ' + type;
}

function show(id) {
    ['register', 'search', 'bill', 'report', 'help'].forEach(s => $(s).classList.add('hidden'));
    $(id).classList.remove('hidden');
    msgBox.className = 'msg';
    if (id === 'report') loadReport();
}

function exitSystem() {
    if (confirm('Are you sure you want to log out and exit?')) {
        sessionStorage.clear();
        window.location.href = 'login.html';
    }
}

// ---- Load dropdowns and set date restrictions ----
async function init() {
    try {
        const [dentists, treatments] = await Promise.all([Api.dentists(), Api.treatments()]);
        $('dentist').innerHTML = dentists.map(d =>
            `<option value="${d.dentistId}">${d.dentistName} (${d.specialization})</option>`).join('');
        $('treatment').innerHTML = treatments.map(t =>
            `<option value="${t.treatmentId}">${t.treatmentName}</option>`).join('');
    } catch (e) {
        notify('Could not load dentists/treatments. Is the server running?', 'error');
    }
    $('aDate').min = new Date().toISOString().split('T')[0]; // no past dates
}
init();

// ---- Validation ----
function validateRegistration() {
    const name = $('pName').value.trim();
    const contact = $('pContact').value.trim();
    const address = $('pAddress').value.trim();
    const date = $('aDate').value;
    const time = $('aTime').value;

    if (!/^[A-Za-z .'-]{2,100}$/.test(name)) return 'Patient name must contain letters only (2-100 characters).';
    if (!/^0\d{9}$/.test(contact)) return 'Contact number must be 10 digits starting with 0.';
    if (address.length < 5) return 'Please enter a valid address.';
    if (!$('dentist').value || !$('treatment').value) return 'Please select a dentist and a treatment.';
    if (!date) return 'Please select an appointment date.';
    if (date < new Date().toISOString().split('T')[0]) return 'Appointment date cannot be in the past.';
    if (!time) return 'Please select an appointment time.';
    if (time < '08:00' || time > '17:00') return 'Appointments are only available between 08:00 and 17:00.';
    return null;
}

// ---- 2. Register ----
async function registerAppointment() {
    const error = validateRegistration();
    if (error) return notify(error, 'error');

    try {
        const patient = await Api.addPatient({
            patientName: $('pName').value.trim(),
            address: $('pAddress').value.trim(),
            contactNumber: $('pContact').value.trim()
        });

        const appt = await Api.addAppointment({
            patientId: patient.patientId,
            dentistId: parseInt($('dentist').value),
            treatmentId: parseInt($('treatment').value),
            appointmentDate: $('aDate').value,
            appointmentTime: $('aTime').value + ':00',
            status: 'SCHEDULED',
            createdBy: staff.staffId      // must be a real staff id (FK in the database)
        });

        notify('Appointment registered successfully. Appointment Number: ' + appt.appointmentNumber, 'success');
        ['pName', 'pContact', 'pAddress', 'aDate', 'aTime'].forEach(id => $(id).value = '');
    } catch (e) {
        notify('Registration failed: ' + e.message, 'error');
    }
}

// ---- Helper: fetch full details for an appointment ----
async function loadFullDetails(number) {
    const appt = await Api.appointmentByNumber(number);
    const [patient, dentist, treatment] = await Promise.all([
        Api.patient(appt.patientId), Api.dentist(appt.dentistId), Api.treatment(appt.treatmentId)
    ]);
    return { appt, patient, dentist, treatment };
}

// ---- 3. Display ----
async function searchAppointment() {
    const number = $('searchNo').value.trim().toUpperCase();
    if (!/^APT-[A-Z0-9]{8}$/.test(number)) return notify('Enter a valid appointment number (e.g. APT-1A2B3C4D).', 'error');

    try {
        const { appt, patient, dentist, treatment } = await loadFullDetails(number);
        $('searchResult').innerHTML = `
            <table>
                <tr><th>Appointment No</th><td>${appt.appointmentNumber}</td></tr>
                <tr><th>Patient Name</th><td>${patient.patientName}</td></tr>
                <tr><th>Address</th><td>${patient.address}</td></tr>
                <tr><th>Contact</th><td>${patient.contactNumber}</td></tr>
                <tr><th>Dentist</th><td>${dentist.dentistName}</td></tr>
                <tr><th>Treatment</th><td>${treatment.treatmentName}</td></tr>
                <tr><th>Date</th><td>${appt.appointmentDate}</td></tr>
                <tr><th>Time</th><td>${appt.appointmentTime}</td></tr>
                <tr><th>Status</th><td>${appt.status}</td></tr>
            </table>`;
        msgBox.className = 'msg';
    } catch (e) {
        $('searchResult').innerHTML = '';
        notify('Appointment not found.', 'error');
    }
}

// ---- 4. Bill ----
async function makeBill() {
    const number = $('billNo').value.trim().toUpperCase();
    if (!/^APT-[A-Z0-9]{8}$/.test(number)) return notify('Enter a valid appointment number.', 'error');

    try {
        const { appt, patient, dentist, treatment } = await loadFullDetails(number);

        let bill;
        try { bill = await Api.billByAppointment(appt.appointmentId); }   // already billed?
        catch (e) { bill = await Api.generateBill(appt.appointmentId); }

        $('receipt').innerHTML = `
            <div id="printArea">
                <h3>Sunrise Dental Clinic - Receipt</h3>
                <p>Bill No: ${bill.billId} | Appointment: ${appt.appointmentNumber}</p>
                <p>Patient: ${patient.patientName} | Dentist: ${dentist.dentistName}</p>
                <table>
                    <tr><th>Consultation Fee</th><td>Rs. ${Number(treatment.consultationFee).toFixed(2)}</td></tr>
                    <tr><th>${treatment.treatmentName}</th><td>Rs. ${Number(treatment.treatmentFee).toFixed(2)}</td></tr>
                    <tr><th>Total</th><td><b>Rs. ${Number(bill.totalAmount).toFixed(2)}</b></td></tr>
                </table>
            </div>
            <button onclick="window.print()">Print Receipt</button>`;
        msgBox.className = 'msg';
    } catch (e) {
        notify('Could not generate bill: ' + e.message, 'error');
    }
}

// ---- Reports ----
async function loadReport() {
    try {
        const list = await Api.allAppointments();
        if (!list.length) { $('reportTable').innerHTML = '<p>No appointments yet.</p>'; return; }
        $('reportTable').innerHTML = '<table><tr><th>Number</th><th>Date</th><th>Time</th><th>Status</th></tr>' +
            list.map(a => `<tr><td>${a.appointmentNumber}</td><td>${a.appointmentDate}</td>
                           <td>${a.appointmentTime}</td><td>${a.status}</td></tr>`).join('') + '</table>';
    } catch (e) {
        notify('Could not load report.', 'error');
    }
}