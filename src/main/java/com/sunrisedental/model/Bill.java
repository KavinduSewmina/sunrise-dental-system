package com.sunrisedental.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Bill {
    private int billId;
    private int appointmentId;
    private BigDecimal totalAmount;
    private Timestamp billDate;
    private String paymentStatus;

    public Bill() {}

    public Bill(int appointmentId, BigDecimal totalAmount) {
        this.appointmentId = appointmentId;
        this.totalAmount = totalAmount;
        this.paymentStatus = "UNPAID";
    }

    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }

    public int getAppointmentId() { return appointmentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public Timestamp getBillDate() { return billDate; }
    public void setBillDate(Timestamp billDate) { this.billDate = billDate; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
}