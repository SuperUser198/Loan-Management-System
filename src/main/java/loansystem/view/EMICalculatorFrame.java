package loansystem.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

public class EMICalculatorFrame extends JFrame {
    private JComboBox<String> loanTypeCombo;
    private JTextField loanAmountField;
    private JTextField interestRateField;
    private JTextField tenureField;
    private JLabel emiResultLabel;
    private JLabel principalLabel;
    private JLabel interestLabel;
    private JLabel totalLabel;

    // Modern color palette
    private final Color PRIMARY_COLOR = new Color(0, 102, 204); // Blue
    private final Color SECONDARY_COLOR = new Color(255, 153, 0); // Orange
    private final Color BACKGROUND_COLOR = new Color(245, 247, 250); // Light gray
    private final Color CARD_COLOR = Color.WHITE;
    private final Color TEXT_COLOR = new Color(51, 51, 51);

    public EMICalculatorFrame() {
        setTitle("Loan EMI Calculator");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout(0, 0));

        // Header Panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        headerPanel.setBackground(CARD_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        
        JLabel breadcrumb = new JLabel("Home > Calculators > EMI Calculator");
        breadcrumb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        breadcrumb.setForeground(new Color(102, 102, 102));
        headerPanel.add(breadcrumb);
        
        add(headerPanel, BorderLayout.NORTH);

        // Main Calculator Panel
        JPanel calculatorPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        calculatorPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        calculatorPanel.setBackground(BACKGROUND_COLOR);
        calculatorPanel.setOpaque(true);

        // Left Panel - Inputs (Modern Card)
        JPanel inputPanel = createInputCard();
        calculatorPanel.add(inputPanel);

        // Right Panel - Results (Modern Card)
        JPanel resultPanel = createResultCard();
        calculatorPanel.add(resultPanel);

        add(calculatorPanel, BorderLayout.CENTER);

        // Set initial interest rate
        updateInterestRate();
    }

    private JPanel createInputCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        // Title
        JLabel title = new JLabel("EMI Calculator");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(PRIMARY_COLOR);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(25));

        // Loan Type Dropdown (Modern)
        JPanel loanTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        loanTypePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        loanTypePanel.setBackground(CARD_COLOR);
        
        JLabel loanTypeLabel = new JLabel("Loan Type");
        loanTypeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        loanTypeLabel.setForeground(TEXT_COLOR);
        loanTypeLabel.setPreferredSize(new Dimension(150, 30));
        loanTypePanel.add(loanTypeLabel);
        
        loanTypeCombo = new JComboBox<>(new String[]{"Home Loan", "Education Loan", "Car Loan"});
        styleComboBox(loanTypeCombo);
        loanTypeCombo.addActionListener(e -> updateInterestRate());
        loanTypePanel.add(loanTypeCombo);
        card.add(loanTypePanel);
        card.add(Box.createVerticalStrut(20));

        // Loan Amount Field (changed $ to ₹)
        card.add(createModernField("Loan Amount", "₹", loanAmountField = new JTextField("0"), 15));
        card.add(Box.createVerticalStrut(20));

        // Interest Rate Field (read-only)
        interestRateField = new JTextField();
        interestRateField.setEditable(false);
        card.add(createModernField("Rate of Interest (p.a)", "", interestRateField, 5));

        card.add(Box.createVerticalStrut(20));

        // Tenure Field
        card.add(createModernField("Loan Tenure years", "", tenureField = new JTextField("8"), 5));
        card.add(Box.createVerticalStrut(30));

        // Calculate Button (Modern)
        JButton calculateBtn = new JButton("Calculate EMI");
        styleButton(calculateBtn, PRIMARY_COLOR);
        calculateBtn.addActionListener(new CalculateListener());
        calculateBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(calculateBtn);

        return card;
    }

    private JPanel createResultCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        // Title
        JLabel title = new JLabel("Your personalized amortization details");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(PRIMARY_COLOR);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(25));

        // EMI Result (changed $ to ₹)
        JLabel emiTitle = new JLabel("Per month EMI");
        emiTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emiTitle.setForeground(new Color(102, 102, 102));
        emiTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(emiTitle);
        
        emiResultLabel = new JLabel("₹0/month");
        emiResultLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        emiResultLabel.setForeground(SECONDARY_COLOR);
        emiResultLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(emiResultLabel);
        card.add(Box.createVerticalStrut(30));

        // Divider
        JSeparator divider = new JSeparator();
        divider.setForeground(new Color(230, 230, 230));
        divider.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(divider);
        card.add(Box.createVerticalStrut(30));

        
        card.add(createBreakdownItem("Principal", principalLabel = new JLabel("₹0")));
        card.add(Box.createVerticalStrut(15));
        card.add(createBreakdownItem("Total Interest", interestLabel = new JLabel("₹0")));
        card.add(Box.createVerticalStrut(15));
        card.add(createBreakdownItem("Total Amount", totalLabel = new JLabel("₹0")));
        card.add(Box.createVerticalStrut(40));

        
        JLabel helpTitle = new JLabel("Need help making a decision?");
        helpTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        helpTitle.setForeground(TEXT_COLOR);
        helpTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(helpTitle);
        card.add(Box.createVerticalStrut(15));

        JButton contactBtn = new JButton("Talk to our expert!");
        styleButton(contactBtn, SECONDARY_COLOR);
        contactBtn.addActionListener(e -> showExpertContactDialog());
        contactBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(contactBtn);

        return card;
    }

    private JPanel createModernField(String label, String prefix, JTextField field, int columns) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBackground(CARD_COLOR);
        
        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        fieldLabel.setForeground(TEXT_COLOR);
        fieldLabel.setPreferredSize(new Dimension(150, 30));
        panel.add(fieldLabel);
        
        if (!prefix.isEmpty()) {
            JLabel prefixLabel = new JLabel(prefix);
            prefixLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            prefixLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
            panel.add(prefixLabel);
        }
        
        styleTextField(field, columns);
        panel.add(field);
        
        return panel;
    }

    private JPanel createBreakdownItem(String label, JLabel valueLabel) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_COLOR);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel nameLabel = new JLabel(label);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameLabel.setForeground(TEXT_COLOR);
        
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        valueLabel.setForeground(TEXT_COLOR);
        valueLabel.setHorizontalAlignment(JLabel.RIGHT);
        
        panel.add(nameLabel, BorderLayout.WEST);
        panel.add(valueLabel, BorderLayout.EAST);
        
        return panel;
    }

    private void styleComboBox(JComboBox<String> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(204, 204, 204)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        combo.setPreferredSize(new Dimension(200, 36));
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return this;
            }
        });
    }

    private void styleTextField(JTextField field, int columns) {
        field.setColumns(columns);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(204, 204, 204)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setPreferredSize(new Dimension(150, 36));
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }

    private void updateInterestRate() {
        String loanType = ((String)loanTypeCombo.getSelectedItem()).toLowerCase();
        double interestRate = 0.0;
        
        switch (loanType) {
            case "home loan":
                interestRate = 8.5;
                break;
            case "car loan":
                interestRate = 10.0;
                break;
            case "education loan":
                interestRate = 7.5;
                break;
        }
        
        interestRateField.setText(String.valueOf(interestRate));
    }

    private void showExpertContactDialog() {
        JOptionPane.showMessageDialog(this, 
            "<html><div style='text-align:center;padding:10px;'>" +
            "<h3 style='color:" + String.format("#%06x", PRIMARY_COLOR.getRGB() & 0x00FFFFFF) + "'>Loan Expert Assistance</h3>" +
            "<p>Our financial experts are available 24/7 to help you</p>" +
            "<p><b>Email:</b> support@loansystem.com</p>" +
            "<p><b>Phone:</b> +1 (800) 123-4567</p>" +
            "</div></html>", 
            "Expert Help", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private class CalculateListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            calculateEMI();
        }
    }

    private void calculateEMI() {
        try {
            double principal = Double.parseDouble(loanAmountField.getText());
            double rate = Double.parseDouble(interestRateField.getText()) / 12 / 100;
            int tenure = Integer.parseInt(tenureField.getText()) * 12;
            
            double emi = principal * rate * Math.pow(1 + rate, tenure) / (Math.pow(1 + rate, tenure) - 1);
            double totalPayment = emi * tenure;
            double totalInterest = totalPayment - principal;
            
            DecimalFormat df = new DecimalFormat("#,##0.00");
            
            // Changed $ to ₹ in all result displays
            emiResultLabel.setText("₹" + df.format(emi) + "/month");
            principalLabel.setText("₹" + df.format(principal));
            interestLabel.setText("₹" + df.format(totalInterest));
            totalLabel.setText("₹" + df.format(totalPayment));
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "<html><div style='color:red;'>Please enter valid numbers</div></html>", 
                "Input Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            EMICalculatorFrame frame = new EMICalculatorFrame();
            frame.setVisible(true);
        });
    }
}