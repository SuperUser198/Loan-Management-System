package loansystem.model;

public class Bank {
    private int bankId;
    private String bankName;
    private double homeLoanRate;
    private double carLoanRate;
    private double educationLoanRate;
    private String createdAt;
    private String updatedAt;

    // Constructors
    public Bank() {}

    public Bank(String bankName, double homeLoanRate, double carLoanRate, double educationLoanRate) {
        this.bankName = bankName;
        this.homeLoanRate = homeLoanRate;
        this.carLoanRate = carLoanRate;
        this.educationLoanRate = educationLoanRate;
    }
    public Bank(int bankId, String bankName, double homeLoanRate, double carLoanRate, double educationLoanRate) {
        this.bankId = bankId;
        this.bankName = bankName;
        this.homeLoanRate = homeLoanRate;
        this.carLoanRate = carLoanRate;
        this.educationLoanRate = educationLoanRate;
    }

    // Getters and Setters
    public int getBankId() {
        return bankId;
    }

    public void setBankId(int bankId) {
        this.bankId = bankId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public double getHomeLoanRate() {
        return homeLoanRate;
    }

    public void setHomeLoanRate(double homeLoanRate) {
        this.homeLoanRate = homeLoanRate;
    }

    public double getCarLoanRate() {
        return carLoanRate;
    }

    public void setCarLoanRate(double carLoanRate) {
        this.carLoanRate = carLoanRate;
    }

    public double getEducationLoanRate() {
        return educationLoanRate;
    }

    public void setEducationLoanRate(double educationLoanRate) {
        this.educationLoanRate = educationLoanRate;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return bankName;
    }
}