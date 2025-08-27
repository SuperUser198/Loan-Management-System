package loansystem.controller;

import loansystem.dao.LoanDAO;
import loansystem.model.Loan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import loansystem.dao.DBConnection;

public class LoanController {
    private LoanDAO loanDAO;

    public LoanController() {
        this.loanDAO = new LoanDAO();
    }

    // Fetch all loans for a specific user
    public List<Loan> getLoansByUserId(int userId) {
        return loanDAO.getLoansByUserId(userId);
    }

    // Fetch all loans (for admin)
    public List<Loan> getAllLoans() {
        return loanDAO.getAllLoans();
    }

    // public boolean applyForLoan(Loan loan) {
    //     try {
    //         return loanDAO.insertLoan(loan); // Actually inserts the loan into DB
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         return false;
    //     }
    // }
    

    public void updateLoanStatus(int loanId, String status, int adminId) throws SQLException {
        loanDAO.updateLoanStatus(loanId, status, adminId);
    }
    public boolean applyForLoan(Loan loan) {
    // Calculate EMI
    double emi = calculateEMI(loan.getAmount(), loan.getInterestRate(), loan.getTenure());
    loan.setEmi(emi);
    
    String sql = "INSERT INTO Loans (user_id, loan_type, amount, interest_rate, tenure, status, emi) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?)";
    
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setInt(1, loan.getUserId());
        stmt.setString(2, loan.getLoanType());
        stmt.setDouble(3, loan.getAmount());
        stmt.setDouble(4, loan.getInterestRate());
        stmt.setInt(5, loan.getTenure());
        stmt.setString(6, loan.getStatus());
        stmt.setDouble(7, loan.getEmi());
        
        return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

private double calculateEMI(double principal, double rate, int years) {
    double monthlyRate = rate / 1200; // Convert annual rate to monthly and percentage to decimal
    int months = years * 12;
    return (principal * monthlyRate * Math.pow(1 + monthlyRate, months)) / 
           (Math.pow(1 + monthlyRate, months) - 1);
}
public void approveLoan(int loanId, int adminId) throws SQLException {
    // Update loan status to approved and set processed_by and processed_date
    loanDAO.updateLoanStatus(loanId, "approved", adminId);
}

public void rejectLoan(int loanId, int adminId) throws SQLException {
    // Update loan status to rejected and set processed_by and processed_date
    loanDAO.updateLoanStatus(loanId, "rejected", adminId);
}

}