package com.sunrisedental.service;

import com.sunrisedental.dao.AppointmentDAO;
import com.sunrisedental.dao.BillDAO;
import com.sunrisedental.dao.TreatmentDAO;
import com.sunrisedental.model.Appointment;
import com.sunrisedental.model.Bill;
import com.sunrisedental.model.Treatment;

public class BillingService {

    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final TreatmentDAO treatmentDAO = new TreatmentDAO();
    private final BillDAO billDAO = new BillDAO();

    public Bill generateBill(int appointmentId) {
        Appointment appt = appointmentDAO.getById(appointmentId);
        if (appt == null) {
            throw new IllegalArgumentException("Appointment not found: " + appointmentId);
        }

        Treatment treatment = treatmentDAO.getById(appt.getTreatmentId());
        if (treatment == null) {
            throw new IllegalArgumentException("Treatment not found for appointment: " + appointmentId);
        }

        // Business rule: total = consultation fee + treatment fee
        Bill bill = new Bill(appointmentId, treatment.getTotalFee());
        billDAO.add(bill);
        return bill;
    }
}