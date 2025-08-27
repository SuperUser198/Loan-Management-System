package loansystem.controller;

import loansystem.model.Bank;

import java.sql.SQLException;
import java.util.List;

import loansystem.dao.BankDAO;

public class BankController {
    private BankDAO bankDao;
    
    public BankController(BankDAO bankDao) {
        this.bankDao = bankDao;
    }
    
     public BankController() {
        this.bankDao = new BankDAO(); // Make sure BankDAO has a default constructor
    }
     
     public List<Bank> getAllBanks() {
        try {
            return bankDao.getAllBanks();
        } catch (SQLException e) {
            System.err.println("Error fetching all banks: " + e.getMessage());
            // You could also throw a custom exception here
            return List.of(); // Return empty list on error
        }
    }
    
    public boolean updateBank(Bank bank) {
        try {
            // Validate bank data before updating
            if (bank == null || bank.getBankName() == null || bank.getBankName().trim().isEmpty()) {
                return false;
            }
            
            if (bank.getHomeLoanRate() <= 0 || bank.getCarLoanRate() <= 0 || bank.getEducationLoanRate() <= 0) {
                return false;
            }

            return bankDao.updateBank(bank);
        } catch (SQLException e) {
            System.err.println("Error updating bank: " + e.getMessage());
            return false;
        }
    }


   public boolean addBank(Bank bank) {
        try {
            // Validate bank data before adding
            if (bank == null || bank.getBankName() == null || bank.getBankName().trim().isEmpty()) {
                return false;
            }
            
            if (bank.getHomeLoanRate() <= 0 || bank.getCarLoanRate() <= 0 || bank.getEducationLoanRate() <= 0) {
                return false;
            }

            return bankDao.addBank(bank);
        } catch (SQLException e) {
            System.err.println("Error adding bank: " + e.getMessage());
            return false;
        }
    }
    public Bank getBankById(int bankId) throws SQLException {
        // Assuming you have a BankDAO or similar to interact with the database
        return bankDao.findBankById(bankId);
    }

    public boolean deleteBank(int bankId) {
        try {
            return bankDao.deleteBank(bankId);
        } catch (SQLException e) {
            System.err.println("Error deleting bank: " + e.getMessage());
            return false;
        }
    }
}