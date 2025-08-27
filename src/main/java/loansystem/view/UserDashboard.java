package loansystem.view;

import com.formdev.flatlaf.FlatLightLaf;
import loansystem.controller.UserController;
import loansystem.controller.LoanController;
import loansystem.model.User;
import loansystem.model.Loan;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import javax.swing.plaf.basic.BasicProgressBarUI;
import loansystem.view.ProfileDialog;

public class UserDashboard extends JFrame {
    // Color palette
    private static final Color PRIMARY_COLOR = new Color(99, 102, 241); // Indigo
    private static final Color SECONDARY_COLOR = new Color(236, 72, 153); // Pink
    private static final Color BACKGROUND_COLOR = new Color(249, 250, 251); // Light gray
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(31, 41, 55);
    private static final Color TEXT_SECONDARY = new Color(107, 114, 128);
    private static final Color BORDER_COLOR = new Color(229, 231, 235);
    
    private int userId;
    private JLabel lblUsername;
    private JTable loanTable;
    private JPanel topCardsPanel;

    public UserDashboard(int userId) {
        this.userId = userId;
        setTitle("User Dashboard - Loan Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1920, 1080); // More reasonable default size
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);

        try {
            UIManager.setLookAndFeel(new FlatLightLaf()); // Using FlatLaf for modern UI
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Fetch user details
        UserController userController = new UserController();
    
User user = userController.getUserById(userId);
System.out.println("Debug - User salary: " + user.getSalary()); // Add this line
        String fullName = (user != null) ? user.getFirstName() + " " + user.getLastName() : "Unknown User";

        // Top Panel (Navigation Bar)
        JPanel topNavPanel = new JPanel();
        topNavPanel.setLayout(new BoxLayout(topNavPanel, BoxLayout.X_AXIS));
        topNavPanel.setBackground(CARD_BACKGROUND);
        topNavPanel.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER_COLOR),
            new EmptyBorder(15, 30, 15, 30)
        ));

        // App Logo/Title
        JLabel lblAppTitle = new JLabel("Welcome");
        lblAppTitle.setFont(loadCustomFont("Inter-Bold", 20));
        lblAppTitle.setForeground(PRIMARY_COLOR);
        lblAppTitle.setBorder(new EmptyBorder(0, 0, 0, 30));
        topNavPanel.add(lblAppTitle);

        // Navigation Buttons
        // Navigation Buttons
String[] buttonLabels = {"My Loans", "EMI Calculator", "Profile", "FAQ", "Settings"};
for (String label : buttonLabels) {
    JButton button = createModernNavButton(label);
    switch (label) {
        case "EMI Calculator":
            button.addActionListener(e -> new EMICalculatorFrame().setVisible(true));
            break;
        case "Profile":
            button.addActionListener(e -> {
                SwingUtilities.invokeLater(() -> {
                    ProfileDialog profileDialog = new ProfileDialog(UserDashboard.this, userId);
                    profileDialog.setVisible(true);
                });
            });
            break;
        case "FAQ":
            button.addActionListener(e -> showFAQDialog());
            break;
        case "Settings":
            button.addActionListener(e -> JOptionPane.showMessageDialog(
                UserDashboard.this,
                "Settings feature coming soon!",
                "Coming Soon",
                JOptionPane.INFORMATION_MESSAGE
            ));
            break;
        case "My Loans":
            // No action needed for now, just placeholder
            break;
    }
    
    topNavPanel.add(button);
    topNavPanel.add(Box.createHorizontalStrut(10));
}
        // Spacer to push logout to right
        topNavPanel.add(Box.createHorizontalGlue());

        // User Profile with dropdown
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userPanel.setOpaque(false);
        
        lblUsername = new JLabel(fullName);
        lblUsername.setFont(loadCustomFont("Inter-Medium", 14));
        lblUsername.setForeground(TEXT_PRIMARY);

        // Modern avatar with initials
        JLabel avatar = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw circular avatar
                g2.setColor(PRIMARY_COLOR);
                g2.fillOval(0, 0, 36, 36);
                
                // Draw initials
                g2.setColor(Color.WHITE);
                g2.setFont(loadCustomFont("Inter-Bold", 14));
                String initials = getInitials(fullName);
                FontMetrics fm = g2.getFontMetrics();
                int x = (36 - fm.stringWidth(initials)) / 2;
                int y = ((36 - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(initials, x, y);
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(36, 36));

        // Modern dropdown menu button
        // Modern dropdown menu button
        JButton btnMenu = new JButton();
        ImageIcon menuDotsIcon = new ImageIcon(getClass().getResource("/icons/menu-dots.png"));
        Image scaledMenuDots = menuDotsIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        btnMenu.setIcon(new ImageIcon(scaledMenuDots));
        btnMenu.setOpaque(false);
        btnMenu.setContentAreaFilled(false);
        btnMenu.setBorderPainted(false);
        btnMenu.setFocusPainted(false);
        btnMenu.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
btnMenu.addActionListener(e -> showUserMenu(btnMenu));

        userPanel.add(lblUsername);
        userPanel.add(avatar);
        userPanel.add(btnMenu);
        topNavPanel.add(userPanel);

        add(topNavPanel, BorderLayout.NORTH);

        // Main Content Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(20, 30, 30, 30));
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Top Cards Panel (Loan Info + Personal Info)
        topCardsPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        topCardsPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        topCardsPanel.setOpaque(false);

        // Left Card - Loan Information
        JPanel loanInfoCard = createModernCard();
        loanInfoCard.setLayout(new BoxLayout(loanInfoCard, BoxLayout.Y_AXIS));

        LoanController loanController = new LoanController();
        List<Loan> userLoans = loanController.getLoansByUserId(userId);
        Loan latestLoan = !userLoans.isEmpty() ? userLoans.get(userLoans.size() - 1) : null;

        if (latestLoan != null) {
            JLabel lblLoanTitle = new JLabel("LOAN DETAILS");
            lblLoanTitle.setFont(loadCustomFont("Inter-Bold", 16));
            lblLoanTitle.setForeground(TEXT_PRIMARY);
            lblLoanTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
            lblLoanTitle.setBorder(new EmptyBorder(0, 0, 15, 0));

            addDetailRow(loanInfoCard, "Type", latestLoan.getLoanType());
           // addDetailRow(loanInfoCard, "ID", latestLoan.getLoanId());
            addDetailRow(loanInfoCard, "Amount", "₹" + latestLoan.getAmount());
            addDetailRow(loanInfoCard, "Interest Rate", latestLoan.getInterestRate() + "%");
            addDetailRow(loanInfoCard, "Tenure", latestLoan.getTenure() + " years");
            
            // Status with pill-shaped badge
            JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            statusPanel.setOpaque(false);
            statusPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            statusPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
            
            JLabel statusBadge = new JLabel(latestLoan.getStatus()) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    Color bgColor = getStatusColor(latestLoan.getStatus());
                    g2.setColor(bgColor);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                    
                    g2.setColor(Color.WHITE);
                    super.paintComponent(g2);
                    g2.dispose();
                }
            };
            statusBadge.setFont(loadCustomFont("Inter-SemiBold", 12));
            statusBadge.setForeground(Color.WHITE);
            statusBadge.setHorizontalAlignment(SwingConstants.CENTER);
            statusBadge.setBorder(new EmptyBorder(5, 15, 5, 15));
            statusBadge.setPreferredSize(new Dimension(120, 30));
            
            statusPanel.add(statusBadge);
            loanInfoCard.add(statusPanel);
        } else {
            JLabel lblNoLoans = new JLabel("No active loans found", SwingConstants.CENTER);
            lblNoLoans.setFont(loadCustomFont("Inter-Medium", 14));
            lblNoLoans.setForeground(TEXT_SECONDARY);
            loanInfoCard.add(lblNoLoans);
        }

        // Right Card - Personal Information
        JPanel personalInfoCard = createModernCard();
        personalInfoCard.setLayout(new BoxLayout(personalInfoCard, BoxLayout.Y_AXIS));

        JLabel lblPersonalTitle = new JLabel("PERSONAL INFORMATION");
        lblPersonalTitle.setFont(loadCustomFont("Inter-Bold", 16));
        lblPersonalTitle.setForeground(TEXT_PRIMARY);
        lblPersonalTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblPersonalTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        if (user != null) {
            addDetailRow(personalInfoCard, "Name", fullName);
            addDetailRow(personalInfoCard, "Username", user.getUsername());
            addDetailRow(personalInfoCard, "Role", user.getRole());

            if (user.getSalary() > 0) {
                addDetailRow(personalInfoCard, "Monthly Salary", String.format("₹%,.2f", user.getSalary()));
            } else {
                addDetailRow(personalInfoCard, "Monthly Salary", "Not specified");
            }

            if (user.getCreditScore() > 0) {
                addDetailRow(personalInfoCard, "Credit Score", String.valueOf(user.getCreditScore()));
                
                // Credit score progress bar code...
                JPanel scorePanel = new JPanel(new BorderLayout(10, 5));
                scorePanel.setOpaque(false);
                scorePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                scorePanel.setBorder(new EmptyBorder(15, 0, 0, 0));
                
                JProgressBar progressBar = new JProgressBar(300, 850);
                progressBar.setValue(user.getCreditScore());
                progressBar.setForeground(getCreditScoreColor(user.getCreditScore()));
                progressBar.setStringPainted(true);
                progressBar.setFont(loadCustomFont("Inter-Medium", 10));
                progressBar.setBorder(new EmptyBorder(0, 0, 0, 0));
                progressBar.setString(String.valueOf(user.getCreditScore()));
                
                // Custom UI for progress bar
                progressBar.setUI(new GradientProgressBarUI());
                progressBar.setBorder(BorderFactory.createEmptyBorder());
                progressBar.setBackground(new Color(229, 231, 235));
                
                scorePanel.add(progressBar, BorderLayout.CENTER);
                personalInfoCard.add(scorePanel);
            }
        } else {
            JLabel lblNoUser = new JLabel("User information not available", SwingConstants.CENTER);
            lblNoUser.setFont(loadCustomFont("Inter-Medium", 14));
            lblNoUser.setForeground(TEXT_SECONDARY);
            personalInfoCard.add(lblNoUser);
        }

        topCardsPanel.add(loanInfoCard);
        topCardsPanel.add(personalInfoCard);
        mainPanel.add(topCardsPanel, BorderLayout.NORTH);

        // Bottom Card - Loan Table
        JPanel loanTablePanel = createModernCard();
        loanTablePanel.setLayout(new BorderLayout());
        loanTablePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblLoanTableTitle = new JLabel("All Loans", SwingConstants.LEFT);
        lblLoanTableTitle.setFont(loadCustomFont("Inter-Bold", 16));
        lblLoanTableTitle.setForeground(TEXT_PRIMARY);
        lblLoanTableTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        loanTablePanel.add(lblLoanTableTitle, BorderLayout.NORTH);

        // Modern Table Design
        loanTable = new JTable() {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                
                if (c instanceof JComponent) {
                    ((JComponent) c).setBorder(new EmptyBorder(12, 16, 12, 16));
                }
                
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? CARD_BACKGROUND : new Color(249, 250, 251));
                }
                
                if (column == 5) { // Status column
                    String status = getValueAt(row, column).toString();
                    c.setForeground(getStatusColor(status));
                    if (c instanceof JLabel) {
                        ((JLabel) c).setFont(loadCustomFont("Inter-SemiBold", 12));
                    }
                } else {
                    c.setForeground(TEXT_PRIMARY);
                }
                
                return c;
            }
        };
        
        // Table styling
        loanTable.setFont(loadCustomFont("Inter-Regular", 14));
        loanTable.setRowHeight(48);
        loanTable.setShowVerticalLines(false);
        loanTable.setIntercellSpacing(new Dimension(0, 0));
        loanTable.setGridColor(BORDER_COLOR);
        loanTable.setSelectionBackground(new Color(224, 231, 255));
        loanTable.setSelectionForeground(TEXT_PRIMARY);
        loanTable.setFocusable(false);
        
        // Table header styling
        JTableHeader header = loanTable.getTableHeader();
        header.setFont(loadCustomFont("Inter-SemiBold", 13));
        header.setBackground(new Color(243, 244, 246));
        header.setForeground(TEXT_SECONDARY);
        header.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER_COLOR),
            new EmptyBorder(12, 16, 12, 16)
        ));
        header.setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(loanTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(CARD_BACKGROUND);
        loanTablePanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(loanTablePanel, BorderLayout.CENTER);

        // Apply for Loan Button - Modern design with icon

        JButton btnApplyForLoan = new JButton("Apply for New Loan");
        btnApplyForLoan.setFont(loadCustomFont("Inter-SemiBold", 12)); // Slightly smaller font
        btnApplyForLoan.setPreferredSize(new Dimension(160, 36)); // Reduced width and height
        btnApplyForLoan.setMaximumSize(new Dimension(160, 36));
        btnApplyForLoan.setForeground(Color.WHITE);
        btnApplyForLoan.setBackground(PRIMARY_COLOR);
        btnApplyForLoan.setFocusPainted(false);
        btnApplyForLoan.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(79, 70, 229), 1, true),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        btnApplyForLoan.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Smaller, more proportional icon
        ImageIcon icon = new ImageIcon(getClass().getResource("/icons/plus-white.png"));
        Image scaledImage = icon.getImage().getScaledInstance(14, 14, Image.SCALE_SMOOTH);
        btnApplyForLoan.setIcon(new ImageIcon(scaledImage));
        btnApplyForLoan.setIconTextGap(6);
        
        btnApplyForLoan.addActionListener(e -> {
            new LoanApplicationFrame(this, userId).setVisible(true);
            refreshDashboard();
        });
        
        // Enhanced hover and pressed effects
        btnApplyForLoan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnApplyForLoan.setBackground(new Color(79, 70, 229)); // Slightly darker shade
                btnApplyForLoan.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(67, 56, 202), 1, true),
                    BorderFactory.createEmptyBorder(6, 12, 6, 12)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnApplyForLoan.setBackground(PRIMARY_COLOR);
                btnApplyForLoan.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(79, 70, 229), 1, true),
                    BorderFactory.createEmptyBorder(6, 12, 6, 12)
                ));
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnApplyForLoan.setBackground(new Color(67, 56, 202)); // Even darker
            }
        });

        // Button with shadow effect
        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonWrapper.setOpaque(false);
        buttonWrapper.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JPanel shadowPanel = new JPanel(new BorderLayout());
        shadowPanel.setOpaque(false);
        //shadowPanel.setBorder(new DropShadowBorder(Color.BLACK, 6, 0.3f, 12, false, true, true, true));
        shadowPanel.add(btnApplyForLoan, BorderLayout.CENTER);
        
        buttonWrapper.add(shadowPanel);
        mainPanel.add(buttonWrapper, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        // Load initial data
        loadLoanData();
    }

    private void addDetailRow(JPanel parent, String label, String value) {
        JPanel rowPanel = new JPanel(new BorderLayout(10, 0));
        rowPanel.setOpaque(false);
        rowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rowPanel.setBorder(new EmptyBorder(0, 0, 12, 0));

        JLabel lblLabel = new JLabel(label + ":");
        lblLabel.setFont(loadCustomFont("Inter-Medium", 14));
        lblLabel.setForeground(TEXT_SECONDARY);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(loadCustomFont("Inter-SemiBold", 14));
        lblValue.setForeground(TEXT_PRIMARY);

        rowPanel.add(lblLabel, BorderLayout.WEST);
        rowPanel.add(lblValue, BorderLayout.CENTER);
        parent.add(rowPanel);
    }
    private void showFAQDialog() {
        JDialog faqDialog = new JDialog(this, "Frequently Asked Questions", true);
        faqDialog.setSize(500, 400);
        faqDialog.setLocationRelativeTo(this);
        faqDialog.getContentPane().setBackground(BACKGROUND_COLOR);
        faqDialog.setLayout(new BorderLayout(10, 10));
    
        JPanel faqPanel = new JPanel();
        faqPanel.setLayout(new BoxLayout(faqPanel, BoxLayout.Y_AXIS));
        faqPanel.setBackground(CARD_BACKGROUND);
        faqPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
    
        // Sample FAQ content
        String[] faqs = {
            "Q: How do I apply for a loan?\nA: Click 'Apply for New Loan' on the dashboard.",
            "Q: What is the interest rate?\nA: It varies based on your loan type and credit score.",
            "Q: How can I check my EMI?\nA: Use the EMI Calculator from the menu."
        };
    
        for (String faq : faqs) {
            JLabel faqLabel = new JLabel("<html>" + faq.replace("\n", "<br>") + "</html>");
            faqLabel.setFont(loadCustomFont("Inter-Medium", 14));
            faqLabel.setForeground(TEXT_PRIMARY);
            faqLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
            faqPanel.add(faqLabel);
        }
    
        JScrollPane scrollPane = new JScrollPane(faqPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(CARD_BACKGROUND);
    
        faqDialog.add(scrollPane, BorderLayout.CENTER);
    
        JButton closeButton = new JButton("Close");
        closeButton.setFont(loadCustomFont("Inter-SemiBold", 12));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(PRIMARY_COLOR);
        closeButton.setFocusPainted(false);
        closeButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(79, 70, 229), 1, true),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        closeButton.addActionListener(e -> faqDialog.dispose());
    
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 10, 10));
        buttonPanel.add(closeButton);
    
        faqDialog.add(buttonPanel, BorderLayout.SOUTH);
        faqDialog.setVisible(true);
    }

    private JPanel createModernCard() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw rounded rectangle background
                g2.setColor(CARD_BACKGROUND);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                
                // Draw border
                g2.setColor(BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        return panel;
    }

    private JButton createModernNavButton(String text) {
        JButton button = new JButton(text);
        button.setFont(loadCustomFont("Inter-Medium", 14));
        button.setForeground(TEXT_SECONDARY);
        button.setBackground(CARD_BACKGROUND);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(8, 16, 8, 16));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setForeground(PRIMARY_COLOR);
                button.setBackground(new Color(238, 242, 255));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setForeground(TEXT_SECONDARY);
                button.setBackground(CARD_BACKGROUND);
            }
        });
        
        return button;
    }

    private Color getStatusColor(String status) {
        switch (status.toUpperCase()) {
            case "APPROVED":
                return new Color(16, 185, 129); // Emerald
            case "REJECTED":
                return new Color(239, 68, 68); // Red
            case "PENDING":
                return new Color(245, 158, 11); // Amber
            default:
                return TEXT_SECONDARY;
        }
    }

    private Color getCreditScoreColor(int score) {
        if (score >= 800) return new Color(16, 185, 129); // Excellent
        if (score >= 700) return new Color(34, 197, 94); // Good
        if (score >= 600) return new Color(234, 179, 8); // Fair
        return new Color(239, 68, 68); // Poor
    }

    private String getInitials(String fullName) {
        if (fullName == null || fullName.isEmpty()) return "UU";
        String[] parts = fullName.split(" ");
        if (parts.length >= 2) {
            return parts[0].substring(0, 1) + parts[1].substring(0, 1);
        } else if (parts[0].length() >= 2) {
            return parts[0].substring(0, 2);
        }
        return fullName.substring(0, Math.min(2, fullName.length()));
    }

    private Font loadCustomFont(String name, float size) {
        try {
            // Load from resources if using custom font files
            // Font font = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/" + name + ".ttf"));
            // return font.deriveFont(size);
            
            // Fallback to system fonts
            String fontName;
            switch (name) {
                case "Inter-Bold": fontName = "Arial Bold"; break;
                case "Inter-SemiBold": fontName = "Arial Bold"; break;
                case "Inter-Medium": fontName = "Arial"; break;
                default: fontName = "Arial";
            }
            return new Font(fontName, Font.PLAIN, (int)size);
        } catch (Exception e) {
            return new Font("Arial", Font.PLAIN, (int)size);
        }
    }

    private void showUserMenu(Component invoker) {
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setBorder(new EmptyBorder(5, 0, 5, 0));
        
        // Custom menu item design
        JMenuItem profileItem = createMenuItem("Profile", "/icons/user.png");
        JMenuItem settingsItem = createMenuItem("Settings", "/icons/settings.png");
        JMenuItem logoutItem = createMenuItem("Logout", "/icons/logout.png");
        
        profileItem.addActionListener(e -> {
            // Ensure this runs on the EDT
            SwingUtilities.invokeLater(() -> {
                ProfileDialog profileDialog = new ProfileDialog(UserDashboard.this, userId);
                profileDialog.setVisible(true);
            });
        });
        settingsItem.addActionListener(e -> JOptionPane.showMessageDialog(
                UserDashboard.this,
                "Settings feature coming soon!",
                "Coming Soon",
                JOptionPane.INFORMATION_MESSAGE
            ));
        logoutItem.addActionListener(e -> showLogoutConfirmation());
        
        popupMenu.add(profileItem);
        popupMenu.add(settingsItem);
        popupMenu.addSeparator();
        popupMenu.add(logoutItem);
        
        popupMenu.show(invoker, 0, invoker.getHeight());
    }

    private JMenuItem createMenuItem(String text, String iconPath) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(loadCustomFont("Inter-Medium", 12)); // Slightly smaller font
        item.setForeground(TEXT_PRIMARY);
        ImageIcon originalIcon = new ImageIcon(getClass().getResource(iconPath));
        Image scaledImage = originalIcon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        item.setIcon(new ImageIcon(scaledImage));
        
        item.setBorder(new EmptyBorder(6, 12, 6, 12));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        item.setIconTextGap(8); 
        // Hover effect remains the same
        item.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                item.setBackground(new Color(238, 242, 255));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                item.setBackground(Color.WHITE);
            }
        });
        
        return item;
    }

   private void loadLoanData() {
    LoanController loanController = new LoanController();
    List<Loan> userLoans = loanController.getLoansByUserId(userId);

    if (!userLoans.isEmpty()) {
        // Add "EMI" column
        String[] columnNames = {"Loan ID", "Type", "Amount", "Interest Rate", "Tenure", "EMI", "Status"};
        Object[][] data = new Object[userLoans.size()][7];

        for (int i = 0; i < userLoans.size(); i++) {
            Loan loan = userLoans.get(i);
            data[i][0] = loan.getLoanId();
            data[i][1] = loan.getLoanType();
            data[i][2] = String.format("₹%,.2f", loan.getAmount());
            data[i][3] = loan.getInterestRate() + "%";
            data[i][4] = loan.getTenure() + " yrs";
            data[i][5] = (loan.getEmi() != 0.0) ? String.format("₹%,.2f", loan.getEmi()) : "N/A";
            data[i][6] = loan.getStatus();
        }

        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class; // All columns return String values
            }
        };

        loanTable.setModel(tableModel);
        
        // Center-align all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < loanTable.getColumnCount(); i++) {
            loanTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Custom renderer for EMI column (right-align currency)
        loanTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(JLabel.RIGHT);
                label.setFont(loadCustomFont("Inter-SemiBold", 12));
                label.setForeground(new Color(59, 130, 246)); // Blue color for EMI
                return label;
            }
        });
        
        // Custom renderer for status column (keep existing)
        loanTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setFont(loadCustomFont("Inter-SemiBold", 12));
                
                String status = value.toString();
                label.setForeground(getStatusColor(status));
                
                // Create a pill-shaped badge
                label.setBorder(new CompoundBorder(
                    new EmptyBorder(4, 12, 4, 12),
                    new RoundedBorder(getStatusColor(status), 12, 1)
                ));
                
                return label;
            }
        });
    }
}

    private void showLogoutConfirmation() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel iconLabel = new JLabel(new ImageIcon(getClass().getResource("/icons/logout-large.png")));
        iconLabel.setHorizontalAlignment(JLabel.CENTER);
        iconLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JLabel message = new JLabel("<html><center>Are you sure you want to<br>log out of your account?</center></html>");
        message.setFont(loadCustomFont("Inter-Medium", 14));
        message.setHorizontalAlignment(JLabel.CENTER);
        
        panel.add(iconLabel, BorderLayout.NORTH);
        panel.add(message, BorderLayout.CENTER);
        
        Object[] options = {"Cancel", "Log Out"};
        int result = JOptionPane.showOptionDialog(
            this,
            panel,
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null,
            options,
            options[0]
        );
        
        if (result == JOptionPane.NO_OPTION) {
            dispose();
            new LoginFrame();
        }
    }

    private void refreshDashboard() {
        SwingUtilities.invokeLater(() -> {
            dispose();
            new UserDashboard(userId).setVisible(true);
        });
    }

    // Custom border for rounded components
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
            g2.drawRoundRect(x, y, width-1, height-1, radius, radius);
            g2.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius, radius, radius, radius);
        }
        
        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = radius;
            insets.top = insets.bottom = radius;
            return insets;
        }
    }

    // Custom progress bar UI
    private static class GradientProgressBarUI extends BasicProgressBarUI {
        @Override
        protected void paintDeterminate(Graphics g, JComponent c) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            JProgressBar progressBar = (JProgressBar) c;
            int width = progressBar.getWidth();
            int height = progressBar.getHeight();
            int arc = height;
            
            // Background
            g2.setColor(progressBar.getBackground());
            g2.fillRoundRect(0, 0, width, height, arc, arc);
            
            // Progress
            double progressPercentage = (double) progressBar.getValue() / (progressBar.getMaximum() - progressBar.getMinimum());
            int progressWidth = (int) (width * progressPercentage);
            
            if (progressWidth > 0) {
                GradientPaint gp = new GradientPaint(
                    0, 0, progressBar.getForeground(),
                    progressWidth, 0, new Color(
                        progressBar.getForeground().getRed(),
                        progressBar.getForeground().getGreen(),
                        progressBar.getForeground().getBlue(),
                        200
                    )
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, progressWidth, height, arc, arc);
            }
            
            // Border
            g2.setColor(new Color(229, 231, 235));
            g2.drawRoundRect(0, 0, width-1, height-1, arc, arc);
            
            g2.dispose();
        }
    }

    // Drop shadow border for components
    private static class DropShadowBorder extends AbstractBorder {
        private Color shadowColor;
        private int shadowSize;
        private float shadowOpacity;
        private int cornerSize;
        private boolean showTopShadow;
        private boolean showLeftShadow;
        private boolean showBottomShadow;
        private boolean showRightShadow;

        public DropShadowBorder(Color shadowColor, int shadowSize, float shadowOpacity, 
                              int cornerSize, boolean showTopShadow, boolean showLeftShadow,
                              boolean showBottomShadow, boolean showRightShadow) {
            this.shadowColor = shadowColor;
            this.shadowSize = shadowSize;
            this.shadowOpacity = shadowOpacity;
            this.cornerSize = cornerSize;
            this.showTopShadow = showTopShadow;
            this.showLeftShadow = showLeftShadow;
            this.showBottomShadow = showBottomShadow;
            this.showRightShadow = showRightShadow;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Calculate shadow area
            int shadowOffset = shadowSize / 2;
            int shadowX = showLeftShadow ? shadowOffset : 0;
            int shadowY = showTopShadow ? shadowOffset : 0;
            int shadowWidth = width - (showLeftShadow ? shadowOffset : 0) - (showRightShadow ? shadowOffset : 0);
            int shadowHeight = height - (showTopShadow ? shadowOffset : 0) - (showBottomShadow ? shadowOffset : 0);
            
            // Create shadow effect
            for (int i = 0; i < shadowSize; i++) {
                float opacity = shadowOpacity * ((float)(shadowSize - i) / (float)shadowSize);
                g2.setColor(new Color(shadowColor.getRed(), shadowColor.getGreen(), shadowColor.getBlue(), (int)(255 * opacity)));
                g2.fillRoundRect(
                    shadowX - i, 
                    shadowY - i, 
                    shadowWidth + i * 2, 
                    shadowHeight + i * 2, 
                    cornerSize, 
                    cornerSize
                );
            }
            
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(
                showTopShadow ? shadowSize : 0,
                showLeftShadow ? shadowSize : 0,
                showBottomShadow ? shadowSize : 0,
                showRightShadow ? shadowSize : 0
            );
        }
    }
}