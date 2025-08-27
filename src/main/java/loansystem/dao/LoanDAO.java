package loansystem.dao;

import loansystem.model.Loan;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LoanDAO {
    // Fetch all loans for a specific user
    public List<Loan> getLoansByUserId(int userId) {
        List<Loan> loans = new ArrayList<>();
        String query = "SELECT * FROM Loans WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Loan loan = new Loan();
                loan.setLoanId(rs.getInt("loan_id"));
                loan.setUserId(rs.getInt("user_id"));
                loan.setLoanType(rs.getString("loan_type"));
                loan.setAmount(rs.getDouble("amount"));
                loan.setInterestRate(rs.getDouble("interest_rate"));
                loan.setTenure(rs.getInt("tenure"));
                loan.setStatus(rs.getString("status"));
                loan.setEmi(rs.getDouble("emi"));
                loan.setApplicationDate(rs.getTimestamp("application_date")); // Added
                loans.add(loan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loans;
    }

    // Fetch all loans (for admin)
    public List<Loan> getAllLoans() {
        List<Loan> loans = new ArrayList<>();
        String query = "SELECT * FROM Loans";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Loan loan = new Loan();
                loan.setLoanId(rs.getInt("loan_id"));
                loan.setUserId(rs.getInt("user_id"));
                loan.setLoanType(rs.getString("loan_type"));
                loan.setAmount(rs.getDouble("amount"));
                loan.setInterestRate(rs.getDouble("interest_rate"));
                loan.setTenure(rs.getInt("tenure"));
                loan.setStatus(rs.getString("status"));
                loan.setApplicationDate(rs.getTimestamp("application_date")); // Added
                loans.add(loan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loans;
    }

    // Apply for a new loan
    public boolean insertLoan(Loan loan) {
        String query = "INSERT INTO Loans (user_id, loan_type, amount, interest_rate, tenure, status, application_date) VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, loan.getUserId());
            stmt.setString(2, loan.getLoanType());
            stmt.setDouble(3, loan.getAmount());
            stmt.setDouble(4, loan.getInterestRate());
            stmt.setInt(5, loan.getTenure());
            stmt.setString(6, loan.getStatus());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void updateLoanStatus(int loanId, String status, int adminId) throws SQLException {
        String sql = "UPDATE Loans SET status = ?, processed_by = ?, processed_date = CURRENT_TIMESTAMP WHERE loan_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, adminId);
            stmt.setInt(3, loanId);
            stmt.executeUpdate();
        }
    }
}