package loansystem.view;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.SQLException;
import loansystem.controller.AdminController;
import loansystem.dao.DBConnection;
import loansystem.dao.UserAdminDAO;
import loansystem.model.User;
import java.util.List;

public class UserManagementFrame extends JFrame {
    private static final Color PRIMARY_COLOR = new Color(79, 70, 229); // Indigo
    private static final Color BACKGROUND_COLOR = new Color(249, 250, 251); // Light gray
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(31, 41, 55); // Dark gray
    private static final Color TEXT_SECONDARY = new Color(107, 114, 128);
    private static final Color BORDER_COLOR = new Color(229, 231, 235);

    private AdminController adminController;
    private JTable userTable;
    private DefaultTableModel tableModel;
    private int currentAdminId;

    public UserManagementFrame(AdminController adminController, int currentAdminId) {
        this.adminController = adminController;
        this.currentAdminId = currentAdminId;
        initializeUI();
        loadUsers();
    }

    private void initializeUI() {
        setTitle("User Management");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("User Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Username", "Role", "Name", "Email", "Phone", "Credit Score", "Salary", "Status", "Created At"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: return Integer.class;  // ID
                    case 6: return Integer.class;  // Credit Score
                    case 7: return Double.class;   // Salary
                    default: return String.class;
                }
            }
        };
        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setAutoCreateRowSorter(true);
        styleTable(userTable);

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(CARD_BACKGROUND);
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setOpaque(false);

        JButton viewDetailsButton = createModernButton("View Details", new Color(59, 130, 246));
        JButton activateButton = createModernButton("Activate", new Color(16, 185, 129));
        JButton suspendButton = createModernButton("Suspend", new Color(251, 191, 36));
        JButton blockButton = createModernButton("Block", new Color(239, 68, 68));
        JButton deleteButton = createModernButton("Delete", new Color(124, 58, 237));
        JButton refreshButton = createModernButton("Refresh", PRIMARY_COLOR);

        buttonPanel.add(viewDetailsButton);
        buttonPanel.add(activateButton);
        buttonPanel.add(suspendButton);
        buttonPanel.add(blockButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        viewDetailsButton.addActionListener(e -> showUserDetails());
        activateButton.addActionListener(e -> updateUserStatus("active"));
        suspendButton.addActionListener(e -> updateUserStatus("suspended"));
        blockButton.addActionListener(e -> updateUserStatus("blocked"));
        deleteButton.addActionListener(e -> deleteUser());
        refreshButton.addActionListener(e -> loadUsers());

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

    private void loadUsers() {
        try {
            List<User> users = adminController.getAllUsers();
            tableModel.setRowCount(0);

            for (User user : users) {
                Double salary = user.getSalary() != null ? user.getSalary() : 0.0;
                Object[] rowData = {
                    user.getUserId(),
                    user.getUsername(),
                    user.getRole(),
                    (user.getFirstName() != null ? user.getFirstName() : "") + " " + 
                    (user.getLastName() != null ? user.getLastName() : ""),
                    user.getEmail() != null ? user.getEmail() : "",
                    user.getPhoneNumber() != null ? user.getPhoneNumber() : "",
                    user.getCreditScore(),
                    salary,
                    user.getAccountStatus(),
                    user.getCreatedAt() != null ? user.getCreatedAt().toString() : ""
                };
                tableModel.addRow(rowData);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void showUserDetails() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to view details", 
                "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        try {
            User user = adminController.getUserDetails(userId);
            if (user == null) {
                JOptionPane.showMessageDialog(this, "Selected user not found", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JDialog dialog = new JDialog(this, "User Details", true);
            dialog.setSize(500, 600);
            dialog.setLocationRelativeTo(this);
            dialog.getContentPane().setBackground(BACKGROUND_COLOR);

            JPanel panel = new JPanel(new GridLayout(0, 2, 15, 15));
            panel.setOpaque(false);
            panel.setBorder(new EmptyBorder(20, 20, 20, 20));

            panel.add(createModernLabel("User ID:"));
            panel.add(new JLabel(String.valueOf(user.getUserId())));
            panel.add(createModernLabel("Username:"));
            panel.add(new JLabel(user.getUsername()));
            panel.add(createModernLabel("Role:"));
            panel.add(new JLabel(user.getRole()));
            panel.add(createModernLabel("First Name:"));
            panel.add(new JLabel(user.getFirstName() != null ? user.getFirstName() : "N/A"));
            panel.add(createModernLabel("Last Name:"));
            panel.add(new JLabel(user.getLastName() != null ? user.getLastName() : "N/A"));
            panel.add(createModernLabel("Email:"));
            panel.add(new JLabel(user.getEmail() != null ? user.getEmail() : "N/A"));
            panel.add(createModernLabel("Phone:"));
            panel.add(new JLabel(user.getPhoneNumber() != null ? user.getPhoneNumber() : "N/A"));
            panel.add(createModernLabel("Address:"));
            panel.add(new JLabel(user.getAddress() != null ? user.getAddress() : "N/A"));
            panel.add(createModernLabel("Credit Score:"));
            panel.add(new JLabel(String.valueOf(user.getCreditScore())));
            panel.add(createModernLabel("Salary:"));
            panel.add(new JLabel(user.getSalary() != null ? String.format("%,.2f", user.getSalary()) : "N/A"));
            panel.add(createModernLabel("Status:"));
            panel.add(new JLabel(user.getAccountStatus()));
            panel.add(createModernLabel("Created At:"));
            panel.add(new JLabel(user.getCreatedAt() != null ? user.getCreatedAt().toString() : "N/A"));
            panel.add(createModernLabel("Last Login:"));
            panel.add(new JLabel(user.getLastLogin() != null ? user.getLastLogin().toString() : "Never"));

            JButton closeButton = createModernButton("Close", TEXT_SECONDARY);
            closeButton.addActionListener(e -> dialog.dispose());

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
            buttonPanel.setOpaque(false);
            buttonPanel.add(closeButton);

            dialog.add(panel, BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            dialog.setVisible(true);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading user details: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateUserStatus(String status) {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to update", 
                "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String username = (String) tableModel.getValueAt(selectedRow, 1);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 8);

        if (currentStatus.equals(status)) {
            JOptionPane.showMessageDialog(this, "User already has this status", 
                "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to " + status + " user " + username + "?", 
            "Confirm Status Change", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = adminController.updateUserStatus(userId, status);
                if (success) {
                    JOptionPane.showMessageDialog(this, "User status updated to " + status, 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadUsers();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update user status", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete", 
                "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String username = (String) tableModel.getValueAt(selectedRow, 1);

        if (userId == currentAdminId) {
            JOptionPane.showMessageDialog(this, "You cannot delete your own account", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to permanently delete user " + username + "?", 
            "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = adminController.deleteUser(userId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "User deleted successfully", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadUsers();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete user", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting user: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
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
            // Properly instantiate dependencies using DBConnection
            Connection conn = DBConnection.getConnection();
            UserAdminDAO userAdminDao = new UserAdminDAO(conn);
            AdminController adminController = new AdminController(userAdminDao);
            int currentAdminId = 1; // Replace with actual admin ID from your login system
            new UserManagementFrame(adminController, currentAdminId).setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error starting application: " + e.getMessage(), 
                "Startup Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}