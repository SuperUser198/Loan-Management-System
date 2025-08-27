package loansystem.view;

import loansystem.controller.*;
import loansystem.model.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import loansystem.dao.BankDAO;
import loansystem.dao.DBConnection;
import loansystem.dao.UserAdminDAO;

public class AdminDashboard extends JFrame {
    // Modern color palette
    private static final Color PRIMARY_COLOR = new Color(79, 70, 229); // Indigo
    private static final Color SECONDARY_COLOR = new Color(236, 72, 153); // Pink
    private static final Color BACKGROUND_COLOR = new Color(249, 250, 251); // Light gray
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(31, 41, 55);
    private static final Color TEXT_SECONDARY = new Color(107, 114, 128);
    private static final Color BORDER_COLOR = new Color(229, 231, 235);

    private int currentAdminId;
    private JPanel mainPanel;
    private LoanController loanController;
    private UserController userController;
    private BankController bankController;
    private AdminController adminController;

    public AdminDashboard(int adminId) {
        this.currentAdminId = adminId;
        setTitle("Admin Dashboard - Loan Management System");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);

        try {
            Connection connection = DBConnection.getConnection();
            this.loanController = new LoanController();
            this.userController = new UserController();
            this.bankController = new BankController(new BankDAO(connection));
            this.adminController = new AdminController(new UserAdminDAO(connection));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Database connection error: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            dispose();
            new LoginFrame().setVisible(true);
            return;
        }

        initializeUI();
    }

    private void initializeUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            JFrame.setDefaultLookAndFeelDecorated(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Modernized Menu Bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(17, 24, 39)); // Dark slate background
        menuBar.setBorder(null);
        menuBar.setPreferredSize(new Dimension(0, 50));
        menuBar.setOpaque(true);

        // Loans Menu
        JMenu loanMenu = createModernMenu("Loans");
        JMenuItem viewLoansItem = createModernMenuItem("View All Loans", "/icons/loan.png");
        viewLoansItem.addActionListener(e -> openAllLoansDialog());
        JMenuItem pendingLoansItem = createModernMenuItem("Manage Pending Loans", "/icons/pending.png");
        pendingLoansItem.addActionListener(e -> openPendingLoansManagement());
        loanMenu.add(viewLoansItem);
        loanMenu.add(new JSeparator());
        loanMenu.add(pendingLoansItem);

        // Users Menu
        JMenu userMenu = createModernMenu("Users");
        JMenuItem manageUsersItem = createModernMenuItem("Manage Users", "/icons/user.png");
        manageUsersItem.addActionListener(e -> openUserManagement());
        userMenu.add(manageUsersItem);

        // Banks Menu
        JMenu bankMenu = createModernMenu("Banks");
        JMenuItem manageBanksItem = createModernMenuItem("Manage Banks", "/icons/bank.png");
        manageBanksItem.addActionListener(e -> openBankManagement());
        bankMenu.add(manageBanksItem);

        // Help Menu
        JMenu helpMenu = createModernMenu("Help");
        JMenuItem aboutItem = createModernMenuItem("About", "/icons/info.png");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);

        // Add menus to the left
        menuBar.add(loanMenu);
        menuBar.add(userMenu);
        menuBar.add(bankMenu);
        menuBar.add(helpMenu);

        // Add spacing
        menuBar.add(Box.createHorizontalGlue());

        // Logout Button
        JButton logoutButton = new JButton("Logout") {
            @Override
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(239, 68, 68)); // Red background
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                super.paintComponent(g2); // Paint text on top
                g2.dispose();
            }
        };
        logoutButton.setForeground(Color.WHITE); // White text
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 14));
        logoutButton.setBorder(new RoundedBorder(new Color(239, 68, 68), 12, 1));
        logoutButton.setFocusPainted(false); // No focus ring
        logoutButton.setContentAreaFilled(false); // Disable default background painting
        logoutButton.setOpaque(false); // Let custom painting handle it
        logoutButton.setPreferredSize(new Dimension(100, 35));
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        // Remove hover effect
        logoutButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Do nothing to avoid hover highlight
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Do nothing
            }
        });
        menuBar.add(logoutButton);

        setJMenuBar(menuBar);

        addAdminProfileCard();
        addStatisticsCards();
        addRecentActivitySection();

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getVerticalScrollBar().setBackground(BACKGROUND_COLOR);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane);
        setVisible(true);
    }

    private void addAdminProfileCard() {
        try {
            User adminUser = adminController.getUserDetails(currentAdminId);
            if (adminUser != null) {
                JPanel profileCard = createModernCard();
                profileCard.setLayout(new BorderLayout(20, 20));
                profileCard.setPreferredSize(new Dimension(0, 250));

                JPanel leftPanel = new JPanel();
                leftPanel.setOpaque(false);
                leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
                leftPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

                JPanel avatarPanel = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(PRIMARY_COLOR);
                        g2.fillOval(0, 0, 100, 100);
                        String initials = getInitials(adminUser.getFirstName() + " " + adminUser.getLastName());
                        g2.setColor(Color.WHITE);
                        g2.setFont(new Font("Arial", Font.BOLD, 32));
                        FontMetrics fm = g2.getFontMetrics();
                        int x = (100 - fm.stringWidth(initials)) / 2;
                        int y = (100 - fm.getHeight()) / 2 + fm.getAscent();
                        g2.drawString(initials, x, y);
                        g2.dispose();
                    }

                    @Override
                    public Dimension getPreferredSize() {
                        return new Dimension(100, 100);
                    }
                };
                avatarPanel.setOpaque(false);

                JLabel nameLabel = new JLabel(adminUser.getFirstName() + " " + adminUser.getLastName());
                nameLabel.setFont(new Font("Arial", Font.BOLD, 24));
                nameLabel.setForeground(TEXT_PRIMARY);

                JLabel roleLabel = new JLabel("Administrator");
                roleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
                roleLabel.setForeground(PRIMARY_COLOR);
                roleLabel.setBorder(new EmptyBorder(10, 0, 0, 0));

                leftPanel.add(avatarPanel);
                leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                leftPanel.add(nameLabel);
                leftPanel.add(roleLabel);

                JPanel rightPanel = new JPanel();
                rightPanel.setOpaque(false);
                rightPanel.setLayout(new GridLayout(0, 2, 20, 15));
                rightPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

                addDetailField(rightPanel, "Username:", adminUser.getUsername());
                addDetailField(rightPanel, "Email:", adminUser.getEmail());
                addDetailField(rightPanel, "Phone:", adminUser.getPhoneNumber());
                addDetailField(rightPanel, "Address:", adminUser.getAddress());
                addDetailField(rightPanel, "Status:", "Active");
                addDetailField(rightPanel, "Last Login:", adminUser.getLastLogin() != null ? 
                    adminUser.getLastLogin().toString() : "Never");

                profileCard.add(leftPanel, BorderLayout.WEST);
                profileCard.add(rightPanel, BorderLayout.CENTER);

                mainPanel.add(profileCard);
                mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addStatisticsCards() {
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 30, 30));
        cardsPanel.setOpaque(false);
        cardsPanel.setBorder(new EmptyBorder(0, 0, 30, 0));

        try {
            int totalUsers = adminController.getAllUsers().size() - 1;
            cardsPanel.add(createStatCard("Total Users", String.valueOf(totalUsers), 
                "/icons/users-group.png", PRIMARY_COLOR));

            int pendingLoans = loanController.getAllLoans().stream()
                .filter(loan -> "pending".equalsIgnoreCase(loan.getStatus()))
                .toList().size();
            cardsPanel.add(createStatCard("Pending Loans", String.valueOf(pendingLoans), 
                "/icons/pending.png", new Color(234, 179, 8)));

            int activeLoans = loanController.getAllLoans().stream()
                .filter(loan -> "approved".equalsIgnoreCase(loan.getStatus()))
                .toList().size();
            cardsPanel.add(createStatCard("Active Loans", String.valueOf(activeLoans), 
                "/icons/loan-active.png", new Color(16, 185, 129)));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        mainPanel.add(cardsPanel);
    }

    private void addRecentActivitySection() {
        JPanel sectionPanel = createModernCard();
        sectionPanel.setLayout(new BorderLayout());
        sectionPanel.setPreferredSize(new Dimension(0, 400));

        JLabel sectionTitle = new JLabel("Recent Loan Applications");
        sectionTitle.setFont(new Font("Arial", Font.BOLD, 20));
        sectionTitle.setForeground(TEXT_PRIMARY);
        sectionTitle.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTable recentLoansTable = new JTable();
        recentLoansTable.setModel(createRecentLoansTableModel());
        styleTable(recentLoansTable);
        recentLoansTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(recentLoansTable);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());

        sectionPanel.add(sectionTitle, BorderLayout.NORTH);
        sectionPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(sectionPanel);
    }

    private DefaultTableModel createRecentLoansTableModel() {
        String[] columns = {"ID", "User", "Type", "Amount", "Status", "Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try {
            List<Loan> loans = loanController.getAllLoans();
            loans.sort((a, b) -> {
                Timestamp dateA = a.getApplicationDate();
                Timestamp dateB = b.getApplicationDate();
                if (dateA == null && dateB == null) return 0;
                if (dateA == null) return 1;
                if (dateB == null) return -1;
                return dateB.compareTo(dateA);
            });

            List<Loan> recentLoans = loans.subList(0, Math.min(5, loans.size()));
            for (Loan loan : recentLoans) {
                User user = userController.getUserById(loan.getUserId());
                String username = user != null ? user.getUsername() : "Unknown";
                String dateStr = (loan.getApplicationDate() != null) ? loan.getApplicationDate().toString() : "Not Available";
                model.addRow(new Object[]{
                    loan.getLoanId(),
                    username,
                    loan.getLoanType(),
                    String.format("₹%,.2f", loan.getAmount()),
                    loan.getStatus(),
                    dateStr
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return model;
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

        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(JLabel.CENTER);
                String status = value.toString().toLowerCase();
                label.setFont(new Font("Arial", Font.BOLD, 14));
                label.setForeground(getStatusTextColor(status));
                label.setOpaque(false);
                return label;
            }
        });
    }

    private JPanel createStatCard(String title, String value, String iconPath, Color color) {
        JPanel card = createModernCard();
        card.setLayout(new BorderLayout(15, 15));
        card.setPreferredSize(new Dimension(0, 150));

        ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
        JLabel iconLabel = new JLabel(new ImageIcon(icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
        iconLabel.setBorder(new EmptyBorder(20, 20, 0, 0));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        titleLabel.setForeground(TEXT_SECONDARY);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 32));
        valueLabel.setForeground(color);

        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        textPanel.add(valueLabel);

        card.add(iconLabel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    private JMenu createModernMenu(String text) {
        JMenu menu = new JMenu(text);
        menu.setFont(new Font("Arial", Font.PLAIN, 16));
        menu.setForeground(Color.BLACK);
        menu.setOpaque(false);
        menu.setBackground(new Color(17, 24, 39)); // Dark slate for menu header
        menu.getPopupMenu().setBackground(Color.WHITE); // White dropdown background
        return menu;
    }

    private JMenuItem createModernMenuItem(String text, String iconPath) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(new Font("Arial", Font.PLAIN, 16));
        item.setForeground(Color.BLACK);
        item.setBackground(Color.WHITE); // White background for dropdown items
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource(iconPath));
            Image scaledImage = originalIcon.getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH);
            item.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            System.err.println("Icon not found: " + iconPath);
        }
        item.setIconTextGap(15);
        item.setOpaque(true);
        return item;
    }

    private JPanel createModernCard() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BACKGROUND);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        return panel;
    }

    private void addDetailField(JPanel panel, String label, String value) {
        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        lblLabel.setForeground(TEXT_SECONDARY);

        JLabel lblValue = new JLabel(value != null ? value : "N/A");
        lblValue.setFont(new Font("Arial", Font.PLAIN, 14));
        lblValue.setForeground(TEXT_PRIMARY);

        panel.add(lblLabel);
        panel.add(lblValue);
    }

    private String getInitials(String name) {
        if (name == null || name.isEmpty()) return "??";
        String[] parts = name.split(" ");
        if (parts.length >= 2) {
            return parts[0].substring(0, 1) + parts[1].substring(0, 1);
        }
        return name.substring(0, Math.min(2, name.length())).toUpperCase();
    }

    private Color getStatusTextColor(String status) {
        switch (status.toLowerCase()) {
            case "approved": return new Color(16, 185, 129); // Green
            case "pending": return new Color(234, 179, 8);  // Yellow
            case "rejected": return new Color(239, 68, 68); // Red
            default: return TEXT_SECONDARY;
        }
    }

    private void refreshLoanView() {
        mainPanel.removeAll();
        addAdminProfileCard();
        addStatisticsCards();
        addRecentActivitySection();
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void openUserManagement() {
        try {
            new UserManagementFrame(adminController, currentAdminId).setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error opening user management: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openBankManagement() {
        try {
            new BankManagementFrame(bankController).setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error opening bank management: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
            "Loan Management System\nAdmin Dashboard\nVersion 2.0",
            "About",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void openAllLoansDialog() {
        try {
            List<Loan> allLoans = loanController.getAllLoans();
            if (allLoans.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "No loans found",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JDialog dialog = new JDialog(this, "All Loans", true);
            dialog.setSize(1000, 600);
            dialog.setLocationRelativeTo(this);
            dialog.getContentPane().setBackground(BACKGROUND_COLOR);
            dialog.setLayout(new BorderLayout(15, 15));

            JLabel titleLabel = new JLabel("All Loan Applications");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
            titleLabel.setForeground(TEXT_PRIMARY);
            titleLabel.setBorder(new EmptyBorder(20, 20, 20, 20));
            dialog.add(titleLabel, BorderLayout.NORTH);

            String[] columns = {"ID", "User", "Type", "Amount", "Tenure", "Status", "Date"};
            DefaultTableModel model = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            for (Loan loan : allLoans) {
                User user = userController.getUserById(loan.getUserId());
                String dateStr = (loan.getApplicationDate() != null) ? loan.getApplicationDate().toString() : "Not Available";
                model.addRow(new Object[]{
                    loan.getLoanId(),
                    user != null ? user.getUsername() : "Unknown",
                    loan.getLoanType(),
                    String.format("₹%,.2f", loan.getAmount()),
                    loan.getTenure() + " months",
                    loan.getStatus(),
                    dateStr
                });
            }

            JTable table = new JTable(model);
            styleTable(table);
            table.getTableHeader().setReorderingAllowed(false);
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
            scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
            dialog.add(scrollPane, BorderLayout.CENTER);

            dialog.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading all loans: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openPendingLoansManagement() {
        try {
            List<Loan> pendingLoans = loanController.getAllLoans().stream()
                .filter(loan -> "pending".equalsIgnoreCase(loan.getStatus()))
                .collect(Collectors.toList());

            if (pendingLoans.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "No pending loans found",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JDialog dialog = new JDialog(this, "Manage Pending Loans", true);
            dialog.setSize(1000, 600);
            dialog.setLocationRelativeTo(this);
            dialog.getContentPane().setBackground(BACKGROUND_COLOR);
            dialog.setLayout(new BorderLayout(15, 15));

            JPanel titlePanel = new JPanel();
            titlePanel.setOpaque(false);
            titlePanel.setBorder(new EmptyBorder(20, 20, 20, 20));
            JLabel titleLabel = new JLabel("Pending Loan Applications");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
            titleLabel.setForeground(TEXT_PRIMARY);
            titlePanel.add(titleLabel);
            dialog.add(titlePanel, BorderLayout.NORTH);

            String[] columns = {"ID", "User", "Type", "Amount", "Tenure", "Date", "Actions"};
            DefaultTableModel model = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 6;
                }
            };

            for (Loan loan : pendingLoans) {
                User user = userController.getUserById(loan.getUserId());
                String dateStr = (loan.getApplicationDate() != null) ? loan.getApplicationDate().toString() : "Not Available";
                model.addRow(new Object[]{
                    loan.getLoanId(),
                    user != null ? user.getUsername() : "Unknown",
                    loan.getLoanType(),
                    String.format("₹%,.2f", loan.getAmount()),
                    loan.getTenure() + " months",
                    dateStr,
                    "Manage"
                });
            }

            JTable table = new JTable(model);
            table.getColumnModel().getColumn(6).setCellRenderer(new ModernButtonRenderer());
            table.getColumnModel().getColumn(6).setCellEditor(new ModernButtonEditor(new JCheckBox(), dialog));
            styleTable(table);
            table.getTableHeader().setReorderingAllowed(false);

            table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    c.setBackground(isSelected ? new Color(229, 231, 235) : CARD_BACKGROUND);
                    setHorizontalAlignment(JLabel.CENTER);
                    return c;
                }
            });

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
            scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
            dialog.add(scrollPane, BorderLayout.CENTER);

            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            bottomPanel.setOpaque(false);
            bottomPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

            // Refresh Button
            JButton refreshButton = new JButton("Refresh") {
                @Override
                public void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(234, 179, 8)); // Yellow background
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    super.paintComponent(g2);
                    g2.dispose();
                }
            };
            refreshButton.setForeground(Color.WHITE);
            refreshButton.setFont(new Font("Arial", Font.PLAIN, 14));
            refreshButton.setBorder(new RoundedBorder(new Color(234, 179, 8), 12, 1));
            refreshButton.setFocusPainted(false);
            refreshButton.setContentAreaFilled(false); // Disable default painting
            refreshButton.setOpaque(false); // Custom painting handles it
            refreshButton.setPreferredSize(new Dimension(120, 40));
            refreshButton.addActionListener(e -> {
                dialog.dispose();
                openPendingLoansManagement();
            });

            bottomPanel.add(refreshButton);
            dialog.add(bottomPanel, BorderLayout.SOUTH); // Fixed typo: should be bottomPanel

            dialog.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading pending loans: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // Modern Button Renderer
    class ModernButtonRenderer extends JButton implements TableCellRenderer {
        public ModernButtonRenderer() {
            setOpaque(true);
            setBackground(new Color(79, 70, 229));
            setForeground(Color.WHITE);
            setFont(new Font("Arial", Font.BOLD, 14));
            setBorder(new RoundedBorder(new Color(79, 70, 229), 15, 2));
            setFocusPainted(false);
            setPreferredSize(new Dimension(100, 35));
            setText("Manage");
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    // Modern Button Editor
    class ModernButtonEditor extends DefaultCellEditor {
        private JButton manageButton;
        private JDialog parentDialog;
        private int currentLoanId;

        public ModernButtonEditor(JCheckBox checkBox, JDialog dialog) {
            super(checkBox);
            this.parentDialog = dialog;

            manageButton = new JButton("Manage");
            manageButton.setBackground(new Color(79, 70, 229));
            manageButton.setForeground(Color.WHITE);
            manageButton.setFont(new Font("Arial", Font.BOLD, 14));
            manageButton.setBorder(new RoundedBorder(new Color(79, 70, 229), 15, 2));
            manageButton.setFocusPainted(false);
            manageButton.setPreferredSize(new Dimension(100, 35));
            manageButton.addActionListener(e -> {
                showActionDialog(currentLoanId);
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentLoanId = (int) table.getValueAt(row, 0);
            return manageButton;
        }

        private void showActionDialog(int loanId) {
            JDialog actionDialog = new JDialog(parentDialog, "Loan Action", true);
            actionDialog.setSize(450, 250);
            actionDialog.setLocationRelativeTo(parentDialog);
            actionDialog.getContentPane().setBackground(BACKGROUND_COLOR);
            actionDialog.setLayout(new BorderLayout(15, 15));

            JLabel titleLabel = new JLabel("Manage Loan #" + loanId);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            titleLabel.setForeground(TEXT_PRIMARY);
            titleLabel.setBorder(new EmptyBorder(15, 15, 15, 15));
            actionDialog.add(titleLabel, BorderLayout.NORTH);

            JPanel contentPanel = new JPanel(new GridLayout(1, 2, 25, 15));
            contentPanel.setOpaque(false);
            contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

            // Approve Button
            JButton approveButton = new JButton("Approve") {
                @Override
                public void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(16, 185, 129)); // Green background
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    super.paintComponent(g2);
                    g2.dispose();
                }
            };
            approveButton.setForeground(Color.WHITE);
            approveButton.setFont(new Font("Arial", Font.PLAIN, 16));
            approveButton.setBorder(new RoundedBorder(new Color(16, 185, 129), 12, 1));
            approveButton.setFocusPainted(false);
            approveButton.setContentAreaFilled(false); // Disable default painting
            approveButton.setOpaque(false); // Custom painting handles it
            approveButton.setPreferredSize(new Dimension(120, 45));
            approveButton.addActionListener(e -> {
                try {
                    loanController.updateLoanStatus(loanId, "approved", currentAdminId);
                    JOptionPane.showMessageDialog(actionDialog,
                        "Loan approved successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    actionDialog.dispose();
                    parentDialog.dispose();
                    refreshLoanView();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(actionDialog,
                        "Error approving loan: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            });

            // Reject Button
            JButton rejectButton = new JButton("Reject") {
                @Override
                public void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(239, 68, 68)); // Red background
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    super.paintComponent(g2);
                    g2.dispose();
                }
            };
            rejectButton.setForeground(Color.WHITE);
            rejectButton.setFont(new Font("Arial", Font.PLAIN, 16));
            rejectButton.setBorder(new RoundedBorder(new Color(239, 68, 68), 12, 1));
            rejectButton.setFocusPainted(false);
            rejectButton.setContentAreaFilled(false); // Disable default painting
            rejectButton.setOpaque(false); // Custom painting handles it
            rejectButton.setPreferredSize(new Dimension(120, 45));
            rejectButton.addActionListener(e -> {
                try {
                    loanController.updateLoanStatus(loanId, "rejected", currentAdminId);
                    JOptionPane.showMessageDialog(actionDialog,
                        "Loan rejected successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    actionDialog.dispose();
                    parentDialog.dispose();
                    refreshLoanView();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(actionDialog,
                        "Error rejecting loan: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            });

            contentPanel.add(approveButton);
            contentPanel.add(rejectButton);

            actionDialog.add(contentPanel, BorderLayout.CENTER);
            actionDialog.setVisible(true);
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
}