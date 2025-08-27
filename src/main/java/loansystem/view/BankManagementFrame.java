package loansystem.view;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;
import loansystem.controller.BankController;
import loansystem.model.Bank;

public class BankManagementFrame extends JFrame {
    private static final Color PRIMARY_COLOR = new Color(79, 70, 229); // Indigo
    private static final Color BACKGROUND_COLOR = new Color(249, 250, 251); // Light gray
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(31, 41, 55); // Dark gray
    private static final Color TEXT_SECONDARY = new Color(107, 114, 128);
    private static final Color BORDER_COLOR = new Color(229, 231, 235);

    private BankController bankController;
    private JTable bankTable;
    private DefaultTableModel tableModel;

    public BankManagementFrame(BankController bankController) {
        this.bankController = bankController;
        initializeUI();
        loadBanks();
    }

    private void initializeUI() {
        setTitle("Bank Management");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("Bank Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Bank Name", "Home Loan Rate", "Car Loan Rate", "Education Loan Rate", "Created At"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bankTable = new JTable(tableModel);
        styleTable(bankTable);

        JScrollPane scrollPane = new JScrollPane(bankTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(CARD_BACKGROUND);
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setOpaque(false);

        JButton addButton = createModernButton("Add Bank", PRIMARY_COLOR);
        JButton editButton = createModernButton("Edit Bank", new Color(59, 130, 246));
        JButton deleteButton = createModernButton("Delete Bank", new Color(239, 68, 68));
        JButton refreshButton = createModernButton("Refresh", new Color(16, 185, 129));

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> showAddBankDialog());
        editButton.addActionListener(e -> showEditBankDialog());
        deleteButton.addActionListener(e -> deleteSelectedBank());
        refreshButton.addActionListener(e -> loadBanks());

        add(mainPanel);
    }

    private void styleTable(JTable table) {
        table.setRowHeight(50);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(229, 231, 235));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setFont(new Font("Arial", Font.PLAIN, 14));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(new Color(243, 244, 246));
        header.setForeground(TEXT_SECONDARY);
        header.setBorder(BorderFactory.createEmptyBorder());

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                setBorder(new EmptyBorder(0, 15, 0, 15));
                return this;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? CARD_BACKGROUND : new Color(249, 250, 251));
                }
                return c;
            }
        });
    }

    private JButton createModernButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            private Color originalColor = bgColor;
            private Color hoverColor = bgColor.brighter();

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isEnabled() && getModel().isRollover() ? hoverColor : originalColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setBorder(new RoundedBorder(bgColor.darker(), 12, 1));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(120, 40));
        button.setRolloverEnabled(true);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.repaint();
            }
        });
        return button;
    }

    private void loadBanks() {
        try {
            List<Bank> banks = bankController.getAllBanks();
            tableModel.setRowCount(0);

            for (Bank bank : banks) {
                Object[] rowData = {
                    bank.getBankId(),
                    bank.getBankName(),
                    bank.getHomeLoanRate() + "%",
                    bank.getCarLoanRate() + "%",
                    bank.getEducationLoanRate() + "%",
                    bank.getCreatedAt()
                };
                tableModel.addRow(rowData);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading banks: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddBankDialog() {
        JDialog dialog = new JDialog(this, "Add New Bank", true);
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(15, 15));
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel panel = new JPanel(new GridLayout(5, 2, 15, 15));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField nameField = createModernTextField();
        JTextField homeRateField = createModernTextField();
        JTextField carRateField = createModernTextField();
        JTextField eduRateField = createModernTextField();

        nameField.setEditable(true);
        homeRateField.setEditable(true);
        carRateField.setEditable(true);
        eduRateField.setEditable(true);

        panel.add(createModernLabel("Bank Name:"));
        panel.add(nameField);
        panel.add(createModernLabel("Home Loan Rate (%):"));
        panel.add(homeRateField);
        panel.add(createModernLabel("Car Loan Rate (%):"));
        panel.add(carRateField);
        panel.add(createModernLabel("Education Loan Rate (%):"));
        panel.add(eduRateField);

        JButton saveButton = createModernButton("Save", PRIMARY_COLOR);
        JButton cancelButton = createModernButton("Cancel", TEXT_SECONDARY);

        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                double homeRate = Double.parseDouble(homeRateField.getText().trim());
                double carRate = Double.parseDouble(carRateField.getText().trim());
                double eduRate = Double.parseDouble(eduRateField.getText().trim());

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Bank name cannot be empty", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Bank newBank = new Bank();
                newBank.setBankName(name);
                newBank.setHomeLoanRate(homeRate);
                newBank.setCarLoanRate(carRate);
                newBank.setEducationLoanRate(eduRate);
                boolean success = bankController.addBank(newBank);
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Bank added successfully", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadBanks();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to add bank (check rates > 0)", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid numbers for rates", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error adding bank: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setOpaque(false);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
        nameField.requestFocusInWindow();
    }

    private void showEditBankDialog() {
        int selectedRow = bankTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a bank to edit", 
                "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bankId = (int) tableModel.getValueAt(selectedRow, 0);
        try {
            Bank bank = bankController.getBankById(bankId);
            if (bank == null) {
                JOptionPane.showMessageDialog(this, "Selected bank not found", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JDialog dialog = new JDialog(this, "Edit Bank", true);
            dialog.setSize(450, 350);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout(15, 15));
            dialog.getContentPane().setBackground(BACKGROUND_COLOR);

            JPanel panel = new JPanel(new GridLayout(5, 2, 15, 15));
            panel.setOpaque(false);
            panel.setBorder(new EmptyBorder(20, 20, 20, 20));

            JTextField nameField = createModernTextField(bank.getBankName());
            JTextField homeRateField = createModernTextField(String.valueOf(bank.getHomeLoanRate()));
            JTextField carRateField = createModernTextField(String.valueOf(bank.getCarLoanRate()));
            JTextField eduRateField = createModernTextField(String.valueOf(bank.getEducationLoanRate()));

            nameField.setEditable(true);
            homeRateField.setEditable(true);
            carRateField.setEditable(true);
            eduRateField.setEditable(true);

            panel.add(createModernLabel("Bank Name:"));
            panel.add(nameField);
            panel.add(createModernLabel("Home Loan Rate (%):"));
            panel.add(homeRateField);
            panel.add(createModernLabel("Car Loan Rate (%):"));
            panel.add(carRateField);
            panel.add(createModernLabel("Education Loan Rate (%):"));
            panel.add(eduRateField);

            JButton saveButton = createModernButton("Save", PRIMARY_COLOR);
            JButton cancelButton = createModernButton("Cancel", TEXT_SECONDARY);

            saveButton.addActionListener(e -> {
                try {
                    String name = nameField.getText().trim();
                    double homeRate = Double.parseDouble(homeRateField.getText().trim());
                    double carRate = Double.parseDouble(carRateField.getText().trim());
                    double eduRate = Double.parseDouble(eduRateField.getText().trim());

                    if (name.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "Bank name cannot be empty", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    bank.setBankName(name);
                    bank.setHomeLoanRate(homeRate);
                    bank.setCarLoanRate(carRate);
                    bank.setEducationLoanRate(eduRate);

                    boolean success = bankController.updateBank(bank);
                    if (success) {
                        JOptionPane.showMessageDialog(dialog, "Bank updated successfully", 
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadBanks();
                        dialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Failed to update bank (check rates > 0)", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Please enter valid numbers for rates", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Error updating bank: " + ex.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            cancelButton.addActionListener(e -> dialog.dispose());

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
            buttonPanel.setOpaque(false);
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);

            dialog.add(panel, BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            dialog.setVisible(true);
            nameField.requestFocusInWindow();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading bank details: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedBank() {
        int selectedRow = bankTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a bank to delete", 
                "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bankId = (int) tableModel.getValueAt(selectedRow, 0);
        String bankName = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete " + bankName + "?", 
            "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = bankController.deleteBank(bankId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Bank deleted successfully", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadBanks();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete bank", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting bank: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JTextField createModernTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        field.setBackground(Color.WHITE);
        field.setForeground(Color.BLACK);
        field.setCaretColor(Color.BLACK);
        field.setOpaque(true);
        return field;
    }

    private JTextField createModernTextField(String text) {
        JTextField field = createModernTextField();
        field.setText(text);
        return field;
    }

    private JLabel createModernLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(TEXT_SECONDARY);
        return label;
    }

    // Rounded Border Class
    private static class RoundedBorder extends AbstractBorder {
        private Color color;
        private int radius;
        private int thickness;

        public RoundedBorder(Color color, int radius, int thickness) {
            this.color = color;
            this.radius = radius;
            this.thickness = thickness;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius, radius, radius, radius);
        }
    }

    // Modern ScrollBar UI
    private static class ModernScrollBarUI extends BasicScrollBarUI {
        private static final int SCROLLBAR_WIDTH = 10;

        @Override
        protected void configureScrollBarColors() {
            this.trackColor = BACKGROUND_COLOR;
            this.thumbColor = new Color(200, 200, 200);
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(trackColor);
            g2.fillRoundRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height, SCROLLBAR_WIDTH, SCROLLBAR_WIDTH);
            g2.dispose();
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, SCROLLBAR_WIDTH, SCROLLBAR_WIDTH);
            g2.dispose();
        }

        @Override
        public Dimension getPreferredSize(JComponent c) {
            return new Dimension(SCROLLBAR_WIDTH, SCROLLBAR_WIDTH);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            BankController controller = new BankController(); // Use default constructor
            BankManagementFrame frame = new BankManagementFrame(controller);
            frame.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error starting application: " + e.getMessage(), 
                "Startup Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}