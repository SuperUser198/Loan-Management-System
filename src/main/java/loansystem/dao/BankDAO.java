package loansystem.dao;

import loansystem.model.Bank;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BankDAO {
    private Connection connection;

    public BankDAO(Connection connection) {
        this.connection = connection;
    }
    
     public BankDAO() {
        try {
            this.connection = DBConnection.getConnection();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get database connection", e);
        }
    }

    // Add a new bank
    public boolean addBank(Bank bank) throws SQLException {
        String sql = "INSERT INTO Banks (bank_name, home_loan_rate, car_loan_rate, education_loan_rate) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, bank.getBankName());
            statement.setDouble(2, bank.getHomeLoanRate());
            statement.setDouble(3, bank.getCarLoanRate());
            statement.setDouble(4, bank.getEducationLoanRate());
            
            return statement.executeUpdate() > 0;
        }
    }
    public Bank findBankById(int bankId) throws SQLException {
        String sql = "SELECT * FROM Banks WHERE bank_id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bankId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Bank(
                        resultSet.getInt("bank_id"),
                        resultSet.getString("bank_name"),
                        resultSet.getDouble("home_loan_rate"),
                        resultSet.getDouble("car_loan_rate"),
                        resultSet.getDouble("education_loan_rate")
                    );
                }
            }
        }
        return null;
    }
    // Update existing bank
    public boolean updateBank(Bank bank) throws SQLException {
        String sql = "UPDATE Banks SET bank_name = ?, home_loan_rate = ?, car_loan_rate = ?, education_loan_rate = ? WHERE bank_id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, bank.getBankName());
            statement.setDouble(2, bank.getHomeLoanRate());
            statement.setDouble(3, bank.getCarLoanRate());
            statement.setDouble(4, bank.getEducationLoanRate());
            statement.setInt(5, bank.getBankId());
            
            return statement.executeUpdate() > 0;
        }
    }

    // Delete a bank
    public boolean deleteBank(int bankId) throws SQLException {
        String sql = "DELETE FROM Banks WHERE bank_id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bankId);
            return statement.executeUpdate() > 0;
        }
    }

    // Get all banks
    public List<Bank> getAllBanks() throws SQLException {
        List<Bank> banks = new ArrayList<>();
        String sql = "SELECT * FROM Banks ORDER BY bank_name";
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            while (resultSet.next()) {
                Bank bank = new Bank();
                bank.setBankId(resultSet.getInt("bank_id"));
                bank.setBankName(resultSet.getString("bank_name"));
                bank.setHomeLoanRate(resultSet.getDouble("home_loan_rate"));
                bank.setCarLoanRate(resultSet.getDouble("car_loan_rate"));
                bank.setEducationLoanRate(resultSet.getDouble("education_loan_rate"));
                bank.setCreatedAt(resultSet.getString("created_at"));
                bank.setUpdatedAt(resultSet.getString("updated_at"));
                
                banks.add(bank);
            }
        }
        return banks;
    }

    // Get bank by ID
    public Bank getBankById(int bankId) throws SQLException {
        String sql = "SELECT * FROM Banks WHERE bank_id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bankId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Bank bank = new Bank();
                    bank.setBankId(resultSet.getInt("bank_id"));
                    bank.setBankName(resultSet.getString("bank_name"));
                    bank.setHomeLoanRate(resultSet.getDouble("home_loan_rate"));
                    bank.setCarLoanRate(resultSet.getDouble("car_loan_rate"));
                    bank.setEducationLoanRate(resultSet.getDouble("education_loan_rate"));
                    return bank;
                }
            }
        }
        return null;
    }
}