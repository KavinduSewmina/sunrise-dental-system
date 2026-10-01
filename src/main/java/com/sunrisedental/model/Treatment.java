package com.sunrisedental.model;

import java.math.BigDecimal;

public class Treatment {
    private int treatmentId;
    private String treatmentName;
    private BigDecimal consultationFee;
    private BigDecimal treatmentFee;

    public Treatment() {}

    public Treatment(String treatmentName, BigDecimal consultationFee, BigDecimal treatmentFee) {
        this.treatmentName = treatmentName;
        this.consultationFee = consultationFee;
        this.treatmentFee = treatmentFee;
    }

    public int getTreatmentId() { return treatmentId; }
    public void setTreatmentId(int treatmentId) { this.treatmentId = treatmentId; }

    public String getTreatmentName() { return treatmentName; }
    public void setTreatmentName(String treatmentName) { this.treatmentName = treatmentName; }

    public BigDecimal getConsultationFee() { return consultationFee; }
    public void setConsultationFee(BigDecimal consultationFee) { this.consultationFee = consultationFee; }

    public BigDecimal getTreatmentFee() { return treatmentFee; }
    public void setTreatmentFee(BigDecimal treatmentFee) { this.treatmentFee = treatmentFee; }

    // Convenience method — useful later for bill calculation
    public BigDecimal getTotalFee() {
        return consultationFee.add(treatmentFee);
    }
}