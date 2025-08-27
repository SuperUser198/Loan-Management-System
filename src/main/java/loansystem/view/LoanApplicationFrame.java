package loansystem.view;

import loansystem.controller.BankController;
import loansystem.controller.LoanController;
import loansystem.controller.UserController;
import loansystem.model.Bank;
import loansystem.model.Loan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoanApplicationFrame extends JDialog {
    private int userId;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JComboBox<String> loanTypeComboBox;
    private JComboBox<Bank> bankComboBox;
    private JTextPane termsTextPane;
    private JCheckBox termsCheckBox;
    private JTextField amountField, tenureField, salaryField;
    private JButton nextButton, backButton, submitButton;
    
    private String selectedLoanType;
    private BankController bankController;
    private static final Map<String, String> termsAndConditions = new HashMap<>();

    static {
        termsAndConditions.put("Home", "<html><body style='width: 500px; padding: 10px; font-family: Segoe UI;'>"
            + "<h2 style='color: #2c3e50;'>Home Loan Terms & Conditions</h2>"
            + "<p>Specific terms will be shown based on selected bank</p>"
            + "</body></html>");

        termsAndConditions.put("Car", "<html><body style='width: 500px; padding: 10px; font-family: Segoe UI;'>"
            + "<h2 style='color: #2c3e50;'>Car Loan Terms & Conditions</h2>"
            + "<p>Specific terms will be shown based on selected bank</p>"
            + "</body></html>");

        termsAndConditions.put("Education", "<html><body style='width: 500px; padding: 10px; font-family: Segoe UI;'>"
            + "<h2 style='color: #2c3e50;'>Education Loan Terms & Conditions</h2>"
            + "<p>Specific terms will be shown based on selected bank</p>"
            + "</body></html>");
    }

    public LoanApplicationFrame(JFrame parent, int userId) {
        super(parent, "Apply for Loan", true);
        this.userId = userId;
        
        try {
            // Initialize controllers first
            this.bankController = new BankController();
            
            // Initialize UI components
            initializeComponents();
            setupLayout();
            
            // Load initial data
            loadBanksForSelectedLoanType();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent, 
                "Failed to initialize loan application: " + e.getMessage(),
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }

    private void initializeComponents() {
        // Initialize buttons early to prevent null issues
        this.nextButton = new JButton("Continue");
        this.backButton = new JButton("Back");
        this.submitButton = new JButton("Submit Application");
        
        // Style buttons
        styleButton(nextButton, new Color(46, 204, 113));
        styleButton(backButton, new Color(231, 76, 60));
        styleButton(submitButton, new Color(46, 204, 113));
        
        // Initialize other components
        this.loanTypeComboBox = new JComboBox<>(new String[]{"Home", "Car", "Education"});
        this.bankComboBox = new JComboBox<>();
        this.termsTextPane = new JTextPane();
        this.termsCheckBox = new JCheckBox("I agree to the Terms & Conditions");
        this.amountField = new JTextField();
        this.tenureField = new JTextField();
        this.salaryField = new JTextField();
        
        // Style components
        styleComboBox(loanTypeComboBox);
        styleComboBox(bankComboBox);
        styleTextField(amountField);
        styleTextField(tenureField);
        styleTextField(salaryField);
    }

    private void setupLayout() {
        setSize(900, 650);
        setLocationRelativeTo(getParent());
        setResizable(false);
        getContentPane().setBackground(new Color(245, 245, 245));

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        cardPanel.setBackground(new Color(245, 245, 245));

        addStep1();
        addStep2();
        addStep3();
        
        add(cardPanel, BorderLayout.CENTER);
    }

    private void addStep1() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        JLabel titleLabel = new JLabel("Select Loan Type & Bank", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(44, 62, 80));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 20, 0);
        
        centerPanel.add(new JLabel("Loan Type:"), gbc);
        centerPanel.add(loanTypeComboBox, gbc);
        
        centerPanel.add(new JLabel("Select Bank:"), gbc);
        centerPanel.add(bankComboBox, gbc);
        
        loanTypeComboBox.addActionListener(e -> loadBanksForSelectedLoanType());

        nextButton.addActionListener(e -> {
            selectedLoanType = (String) loanTypeComboBox.getSelectedItem();
            Bank selectedBank = (Bank) bankComboBox.getSelectedItem();
            
            if (selectedBank == null) {
                JOptionPane.showMessageDialog(this, 
                    "Please select a bank", 
                    "Warning", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            updateTermsWithBankDetails(selectedBank);
            cardLayout.next(cardPanel);
        });
        centerPanel.add(nextButton, gbc);

        panel.add(centerPanel, BorderLayout.CENTER);
        cardPanel.add(panel, "Step1");
    }

    private void addStep2() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel("Terms & Conditions", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(44, 62, 80));
        panel.add(titleLabel, BorderLayout.NORTH);

        termsTextPane.setContentType("text/html");
        termsTextPane.setEditable(false);
        termsTextPane.setBackground(new Color(250, 250, 250));
        termsTextPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(termsTextPane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        termsCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        bottomPanel.add(termsCheckBox, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);

        backButton.addActionListener(e -> cardLayout.previous(cardPanel));
        buttonPanel.add(backButton);

        JButton nextStepButton = new JButton("Continue");
        styleButton(nextStepButton, new Color(46, 204, 113));
        nextStepButton.addActionListener(e -> {
            if (!termsCheckBox.isSelected()) {
                JOptionPane.showMessageDialog(this, 
                    "You must agree to the terms and conditions to proceed.", 
                    "Warning", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            cardLayout.next(cardPanel);
        });
        buttonPanel.add(nextStepButton);

        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        cardPanel.add(panel, "Step2");
    }

    private void addStep3() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        JLabel titleLabel = new JLabel("Loan Application Form", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(44, 62, 80));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(20, 50, 30, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 15);
        
        addFormField(formPanel, "Amount (₹):", amountField, gbc);
        gbc.gridy++;
        addFormField(formPanel, "Tenure (years):", tenureField, gbc);
        gbc.gridy++;
        addFormField(formPanel, "Monthly Salary (₹):", salaryField, gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        backButton.addActionListener(e -> cardLayout.previous(cardPanel));
        buttonPanel.add(backButton);

        submitButton.addActionListener(this::submitLoanApplication);
        buttonPanel.add(submitButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        cardPanel.add(panel, "Step3");
    }

    private void addFormField(JPanel panel, String label, JTextField field, GridBagConstraints gbc) {
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(jLabel, gbc);
        
        gbc.gridx++;
        panel.add(field, gbc);
        gbc.gridx = 0;
    }

    private void loadBanksForSelectedLoanType() {
        try {
            List<Bank> banks = bankController.getAllBanks();
            
            bankComboBox.removeAllItems();
            for (Bank bank : banks) {
                bankComboBox.addItem(bank);
            }
            
            if (banks.isEmpty()) {
                bankComboBox.addItem(new Bank("No banks available", 0, 0, 0));
                bankComboBox.setEnabled(false);
                nextButton.setEnabled(false);
            } else {
                bankComboBox.setEnabled(true);
                nextButton.setEnabled(true);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error loading banks: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTermsWithBankDetails(Bank bank) {
        String loanType = (String) loanTypeComboBox.getSelectedItem();
        double rate = 0;
        
        switch (loanType) {
            case "Home":
                rate = bank.getHomeLoanRate();
                break;
            case "Car":
                rate = bank.getCarLoanRate();
                break;
            case "Education":
                rate = bank.getEducationLoanRate();
                break;
        }
        
        String terms = "<html><body style='width: 500px; padding: 10px; font-family: Segoe UI;'>"
            + "<h2 style='color: #2c3e50;'>" + loanType + " Loan Terms & Conditions</h2>"
            + "<p><b>Bank:</b> " + bank.getBankName() + "</p>"
            + "<p><b>Interest Rate:</b> " + rate + "%</p>"
            + "<ul style='line-height: 1.6;'>";
        
        switch (loanType) {
            case "Home":
                terms += "<li><b>Maximum Tenure:</b> 30 years</li>"
                      + "<li>Minimum amount: ₹500,000</li>"
                      + "<li>Property documents required</li>";
                break;
            case "Car":
                terms += "<li><b>Maximum Tenure:</b> 7 years</li>"
                      + "<li>Minimum amount: ₹300,000</li>"
                      + "<li>Vehicle details required</li>";
                break;
            case "Education":
                terms += "<li><b>Maximum Tenure:</b> 15 years</li>"
                      + "<li>Minimum amount: ₹100,000</li>"
                      + "<li>Proof of admission required</li>";
                break;
        }
        
        terms += "</ul></body></html>";
        termsTextPane.setText(terms);
    }

    private void submitLoanApplication(ActionEvent e) {
        try {
            Bank selectedBank = (Bank) bankComboBox.getSelectedItem();
            // Ensure we have a bank selected
            if (selectedBank == null) {
                JOptionPane.showMessageDialog(this, 
                    "Please select a valid bank", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            double amount = Double.parseDouble(amountField.getText());
            int tenure = Integer.parseInt(tenureField.getText());
            double salary = Double.parseDouble(salaryField.getText());

            UserController userController = new UserController();
            if (!userController.updateUserSalary(userId, salary)) {
                JOptionPane.showMessageDialog(this, 
                    "Failed to update salary information", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            double interestRate;
            String loanType = (String) loanTypeComboBox.getSelectedItem();
            
            switch (loanType) {
                case "Home":
                    interestRate = selectedBank.getHomeLoanRate();
                    break;
                case "Car":
                    interestRate = selectedBank.getCarLoanRate();
                    break;
                case "Education":
                    interestRate = selectedBank.getEducationLoanRate();
                    break;
                default:
                    interestRate = 10.0;
            }

            double emi = calculateEMI(amount, interestRate, tenure);
            
            if (emi > (salary * 0.5)) {
                JOptionPane.showMessageDialog(this, 
                    "EMI of ₹" + String.format("%,.2f", emi) + " exceeds 50% of your salary.\n" +
                    "Please consider a smaller loan amount or longer tenure.",
                    "EMI Too High",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                "Bank: " + selectedBank.getBankName() + "\n" +
                "Loan Type: " + loanType + "\n" +
                "Interest Rate: " + interestRate + "%\n" +
                "Your estimated EMI will be: ₹" + String.format("%,.2f", emi) + "\n" +
                "Do you want to proceed with the application?",
                "Confirm Loan Application",
                JOptionPane.YES_NO_OPTION);
                
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

        Loan loan = new Loan(userId, loanType, amount, interestRate, tenure, "pending");
        loan.setEmi(emi);
        loan.setBankId(selectedBank.getBankId()); // This will now always set a value

        LoanController loanController = new LoanController();
        if (loanController.applyForLoan(loan)) {
                JOptionPane.showMessageDialog(this,
                    "Loan application submitted successfully to " + selectedBank.getBankName() + "!\n" +
                    "Your EMI: ₹" + String.format("%,.2f", emi),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new UserDashboard(userId).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to submit loan application",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Please enter valid numbers in all fields",
                "Invalid Input",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private double calculateEMI(double amount, double interestRate, int tenure) {
        double monthlyRate = interestRate / 1200;
        int months = tenure * 12;
        return (amount * monthlyRate * Math.pow(1 + monthlyRate, months)) / 
               (Math.pow(1 + monthlyRate, months) - 1);
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 1),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(200, 30));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setBackground(Color.WHITE);
        comboBox.setPreferredSize(new Dimension(200, 30));
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }
}