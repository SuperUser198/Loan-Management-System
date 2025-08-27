package loansystem.model;

import java.sql.Timestamp;

public class Loan {
    private int loanId;
    private int userId;
    private Integer bankId=-1; // Can be null if not assigned to a bank
    private String loanType; // 'home', 'car', 'education'
    private double amount;
    private double interestRate;
    private int tenure; // In months
    private String status; // 'pending', 'approved', 'rejected'
    private Double emi; // Calculated EMI amount
    private Timestamp applicationDate;
    private Timestamp processedDate;
    private Integer processedBy; // Admin user ID who processed the loan
    private String remarks; // Admin remarks for approval/rejection

    // Constructors
    public Loan() {}

    public Loan(int userId, String loanType, double amount, double interestRate, int tenure, String status) {
        this.userId = userId;
        this.loanType = loanType;
        this.amount = amount;
        this.interestRate = interestRate;
        this.tenure = tenure;
        this.status = status;
    }

    // Getters and Setters
    public int getLoanId() {
        return loanId;
    }

    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Integer getBankId() {
        return bankId != null ? bankId : -1;
    }

    public void setBankId(Integer bankId) {
        this.bankId = bankId != null ? bankId : -1;
    }
    public String getLoanType() {
        return loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public int getTenure() {
        return tenure;
    }

    public void setTenure(int tenure) {
        this.tenure = tenure;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getEmi() {
        return emi;
    }

    public void setEmi(Double emi) {
        this.emi = emi;
    }

    public Timestamp getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(Timestamp applicationDate) {
        this.applicationDate = applicationDate;
    }

    public Timestamp getProcessedDate() {
        return processedDate;
    }

    public void setProcessedDate(Timestamp processedDate) {
        this.processedDate = processedDate;
    }

    public Integer getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(Integer processedBy) {
        this.processedBy = processedBy;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    // Helper method to calculate EMI
    public double calculateEMI() {
        if (interestRate <= 0 || tenure <= 0 || amount <= 0) {
            return 0;
        }
        double monthlyRate = interestRate / 1200; // Convert annual rate to monthly and percentage to decimal
        int months = tenure;
        return (amount * monthlyRate * Math.pow(1 + monthlyRate, months)) / 
               (Math.pow(1 + monthlyRate, months) - 1);
    }
}