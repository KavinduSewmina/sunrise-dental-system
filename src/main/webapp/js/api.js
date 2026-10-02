// Context root is the first path segment, e.g. /SunriseDental/login.html -> /SunriseDental
const CTX = '/' + window.location.pathname.split('/')[1];
const API = CTX + '/api';

async function request(method, path, body) {
    const options = { method, headers: { 'Content-Type': 'application/json' } };
    if (body) options.body = JSON.stringify(body);
    const res = await fetch(API + path, options);
    if (!res.ok) {
        let msg = 'Request failed (' + res.status + ')';
        try { const err = await res.json(); if (err.message) msg = err.message; } catch (e) {}
        throw new Error(msg);
    }
    const text = await res.text();
    return text ? JSON.parse(text) : null;
}

const Api = {
    login: (username, password) => request('POST', '/auth/login', { username, password }),
    dentists: () => request('GET', '/dentists'),
    dentist: (id) => request('GET', '/dentists/' + id),
    treatments: () => request('GET', '/treatments'),
    treatment: (id) => request('GET', '/treatments/' + id),
    addPatient: (p) => request('POST', '/patients', p),
    patient: (id) => request('GET', '/patients/' + id),
    addAppointment: (a) => request('POST', '/appointments', a),
    appointmentByNumber: (n) => request('GET', '/appointments/number/' + encodeURIComponent(n)),
    allAppointments: () => request('GET', '/appointments'),
    generateBill: (appointmentId) => request('POST', '/bills/generate/' + appointmentId),
    billByAppointment: (appointmentId) => request('GET', '/bills/appointment/' + appointmentId)
};