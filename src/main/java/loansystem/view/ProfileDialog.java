package loansystem.view;

import loansystem.controller.UserController;
import loansystem.model.User;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class ProfileDialog extends JDialog {
    private static final Color PRIMARY_COLOR = new Color(79, 82, 221);  // Slightly deeper blue
    private static final Color PRIMARY_LIGHT = new Color(118, 121, 231); // Lighter blue for hover
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(31, 41, 55);
    private static final Color TEXT_SECONDARY = new Color(107, 114, 128);
    private static final Color BORDER_COLOR = new Color(229, 231, 235);
    private static final Color FIELD_BACKGROUND = new Color(245, 246, 248);
    
    private User user;
    private UserController userController;
    private boolean editMode = false;
    
    // Form fields
    private JTextField txtFirstName, txtLastName, txtUsername, txtEmail, txtPhoneNumber, txtAddress;
    private JPasswordField txtPassword, txtConfirmPassword;
    private JFormattedTextField txtSalary;
    private JButton btnEditSave, btnCancel;
    
    public ProfileDialog(JFrame parent, int userId) {
        super(parent, "User Profile", true);
        this.userController = new UserController();
        this.user = userController.getUserById(userId);
        
        initializeUI();
        setupComponents();
        loadUserData();
        
        setSize(650, 700);  // Increased width for better display
        setLocationRelativeTo(parent);
        setResizable(false);
    }
    
    private void initializeUI() {
        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }
    
    private void setupComponents() {
        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(18, 25, 18, 25));
        
        JLabel lblTitle = new JLabel("User Profile");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        
        // Removed the duplicate close button, keeping only one exit option
        
        headerPanel.add(lblTitle, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);
        
        // Main Content Panel - use a scroll pane for better handling on smaller screens
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        contentPanel.setBackground(BACKGROUND_COLOR);
        
        // Avatar Panel - improved circular avatar
        JPanel avatarPanel = new JPanel();
        avatarPanel.setOpaque(false);
        avatarPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        avatarPanel.setBorder(new EmptyBorder(0, 0, 25, 0));
        
        JLabel avatarLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw circular avatar with gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, PRIMARY_COLOR, 
                    100, 100, PRIMARY_LIGHT
                );
                g2.setPaint(gradient);
                g2.fillOval(0, 0, 100, 100);
                
                // Draw avatar border
                g2.setColor(new Color(255, 255, 255, 80));
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(1, 1, 97, 97);
                
                // Draw initials with shadow
                String initials = getInitials(user.getFirstName() + " " + user.getLastName());
                
                // Drop shadow effect
                g2.setColor(new Color(0, 0, 0, 30));
                g2.setFont(new Font("Segoe UI", Font.BOLD, 32));
                FontMetrics fm = g2.getFontMetrics();
                int x = (100 - fm.stringWidth(initials)) / 2;
                int y = ((100 - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(initials, x+1, y+1);
                
                // Main text
                g2.setColor(Color.WHITE);
                g2.drawString(initials, x, y);
                
                g2.dispose();
            }
        };
        avatarLabel.setPreferredSize(new Dimension(100, 100));
        avatarPanel.add(avatarLabel);
        contentPanel.add(avatarPanel);
        
        // Form Panel - improved card design with wider layout
        JPanel formPanel = createModernCard();
        formPanel.setLayout(new GridLayout(0, 1, 0, 15)); // Changed to GridLayout for better alignment
        
        // Personal Information Section
        JLabel lblPersonalInfo = createSectionHeader("Personal Information");
        formPanel.add(lblPersonalInfo);
        
        // First Name with improved layout
        JPanel firstNamePanel = new JPanel(new BorderLayout(10, 0));
        firstNamePanel.setOpaque(false);
        JLabel firstNameLabel = createLabel("First Name");
        firstNameLabel.setPreferredSize(new Dimension(150, 30));
        txtFirstName = createStyledTextField();
        txtFirstName.setEditable(false);
        firstNamePanel.add(firstNameLabel, BorderLayout.WEST);
        firstNamePanel.add(txtFirstName, BorderLayout.CENTER);
        formPanel.add(firstNamePanel);
        
        // Last Name
        JPanel lastNamePanel = new JPanel(new BorderLayout(10, 0));
        lastNamePanel.setOpaque(false);
        JLabel lastNameLabel = createLabel("Last Name");
        lastNameLabel.setPreferredSize(new Dimension(150, 30));
        txtLastName = createStyledTextField();
        txtLastName.setEditable(false);
        lastNamePanel.add(lastNameLabel, BorderLayout.WEST);
        lastNamePanel.add(txtLastName, BorderLayout.CENTER);
        formPanel.add(lastNamePanel);
        
        // Username
        JPanel usernamePanel = new JPanel(new BorderLayout(10, 0));
        usernamePanel.setOpaque(false);
        JLabel usernameLabel = createLabel("Username");
        usernameLabel.setPreferredSize(new Dimension(150, 30));
        txtUsername = createStyledTextField();
        txtUsername.setEditable(false);
        usernamePanel.add(usernameLabel, BorderLayout.WEST);
        usernamePanel.add(txtUsername, BorderLayout.CENTER);
        formPanel.add(usernamePanel);
        
        // Email
        JPanel emailPanel = new JPanel(new BorderLayout(10, 0));
        emailPanel.setOpaque(false);
        JLabel emailLabel = createLabel("Email Address");
        emailLabel.setPreferredSize(new Dimension(150, 30));
        txtEmail = createStyledTextField();
        txtEmail.setEditable(false);
        emailPanel.add(emailLabel, BorderLayout.WEST);
        emailPanel.add(txtEmail, BorderLayout.CENTER);
        formPanel.add(emailPanel);
        
        // Phone Number
        JPanel phonePanel = new JPanel(new BorderLayout(10, 0));
        phonePanel.setOpaque(false);
        JLabel phoneLabel = createLabel("Phone Number");
        phoneLabel.setPreferredSize(new Dimension(150, 30));
        txtPhoneNumber = createStyledTextField();
        txtPhoneNumber.setEditable(false);
        phonePanel.add(phoneLabel, BorderLayout.WEST);
        phonePanel.add(txtPhoneNumber, BorderLayout.CENTER);
        formPanel.add(phonePanel);
        
        // Address
        JPanel addressPanel = new JPanel(new BorderLayout(10, 0));
        addressPanel.setOpaque(false);
        JLabel addressLabel = createLabel("Address");
        addressLabel.setPreferredSize(new Dimension(150, 30));
        txtAddress = createStyledTextField();
        txtAddress.setEditable(false);
        addressPanel.add(addressLabel, BorderLayout.WEST);
        addressPanel.add(txtAddress, BorderLayout.CENTER);
        formPanel.add(addressPanel);
        
      
        
        // Salary
        JPanel salaryPanel = new JPanel(new BorderLayout(10, 0));
        salaryPanel.setOpaque(false);
        JLabel salaryLabel = createLabel("Monthly Salary (₹)");
        salaryLabel.setPreferredSize(new Dimension(150, 30));
        txtSalary = new JFormattedTextField(java.text.NumberFormat.getIntegerInstance());
        txtSalary.setEditable(false);
        txtSalary.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSalary.setBackground(FIELD_BACKGROUND);
        txtSalary.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(0, 0, 0, 0),
            new EmptyBorder(10, 10, 10, 10)
        ));
        salaryPanel.add(salaryLabel, BorderLayout.WEST);
        salaryPanel.add(txtSalary, BorderLayout.CENTER);
        formPanel.add(salaryPanel);
        
        // Account Security Section
        JLabel lblAccountSecurity = createSectionHeader("Account Security");
        formPanel.add(lblAccountSecurity);
        
        // Password
        JPanel passwordPanel = new JPanel(new BorderLayout(10, 0));
        passwordPanel.setOpaque(false);
        JLabel passwordLabel = createLabel("Password");
        passwordLabel.setPreferredSize(new Dimension(150, 30));
        txtPassword = createStyledPasswordField();
        txtPassword.setEditable(false);
        passwordPanel.add(passwordLabel, BorderLayout.WEST);
        passwordPanel.add(txtPassword, BorderLayout.CENTER);
        formPanel.add(passwordPanel);
        
        // Confirm Password
        JPanel confirmPasswordPanel = new JPanel(new BorderLayout(10, 0));
        confirmPasswordPanel.setOpaque(false);
        JLabel confirmPasswordLabel = createLabel("Confirm Password");
        confirmPasswordLabel.setPreferredSize(new Dimension(150, 30));
        txtConfirmPassword = createStyledPasswordField();
        txtConfirmPassword.setEditable(false);
        confirmPasswordPanel.add(confirmPasswordLabel, BorderLayout.WEST);
        confirmPasswordPanel.add(txtConfirmPassword, BorderLayout.CENTER);
        formPanel.add(confirmPasswordPanel);
        
        contentPanel.add(formPanel);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        btnCancel = createButton("Cancel", TEXT_SECONDARY, new Color(233, 236, 239));
        btnCancel.addActionListener(e -> {
            if (editMode) {
                toggleEditMode();
            } else {
                dispose();
            }
        });
        btnCancel.setVisible(false);
        
        btnEditSave = createButton("Edit Profile", Color.WHITE, PRIMARY_COLOR);
        btnEditSave.addActionListener(e -> toggleEditMode());
        
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnEditSave);
        contentPanel.add(buttonPanel);
        
        // Add scrolling capability
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createModernCard() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Card with subtle shadow
                g2.setColor(CARD_BACKGROUND);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Subtle shadow effect
                for (int i = 0; i < 4; i++) {
                    g2.setColor(new Color(0, 0, 0, 5));
                    g2.drawRoundRect(i, i, getWidth() - 2*i - 1, getHeight() - 2*i - 1, 15-i, 15-i);
                }
                
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));
        return panel;
    }
    
    private JLabel createSectionHeader(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(TEXT_PRIMARY);
        label.setBorder(new EmptyBorder(15, 0, 5, 0));
        return label;
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI SemiBold", Font.PLAIN, 13));
        label.setForeground(TEXT_SECONDARY);
        return label;
    }
    
    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBackground(FIELD_BACKGROUND);
        textField.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(0, 0, 0, 0),
            new EmptyBorder(10, 10, 10, 10)
        ));
        return textField;
    }
    
    private JPasswordField createStyledPasswordField() {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBackground(FIELD_BACKGROUND);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(0, 0, 0, 0),
            new EmptyBorder(10, 10, 10, 10)
        ));
        passwordField.setEchoChar('•');
        return passwordField;
    }
    
    private JButton createButton(String text, Color textColor, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(getModel().isPressed() ? bgColor.darker() : 
                             getModel().isRollover() ? new Color(
                                Math.min(bgColor.getRed() + 10, 255),
                                Math.min(bgColor.getGreen() + 10, 255),
                                Math.min(bgColor.getBlue() + 10, 255)) : bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                g2.setFont(getFont());
                g2.setColor(textColor);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(getText(), x, y);
                
                g2.dispose();
            }
        };
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(textColor);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 40));
        return button;
    }
    
    private void loadUserData() {
        if (user != null) {
            // Make sure to fetch fresh data from the database each time
            txtFirstName.setText(user.getFirstName());
            txtLastName.setText(user.getLastName());
            txtUsername.setText(user.getUsername());  // Ensure this is properly populated
            txtEmail.setText(user.getEmail());
            txtPhoneNumber.setText(user.getPhoneNumber());  // Ensure this is properly populated
            txtAddress.setText(user.getAddress());
            txtSalary.setValue((int) Math.round(user.getSalary()));
            txtPassword.setText("********"); // Placeholder
            txtConfirmPassword.setText("********"); // Placeholder
        }
    }
    
    private void toggleEditMode() {
        editMode = !editMode;
        
        if (editMode) {
            // Switch to edit mode
            btnEditSave.setText("Save Changes");
            btnCancel.setVisible(true);
            
            // Enable editing
            txtFirstName.setEditable(true);
            txtLastName.setEditable(true);
            txtEmail.setEditable(true);
            txtPhoneNumber.setEditable(true);
            txtAddress.setEditable(true);
            txtSalary.setEditable(true);
            txtPassword.setEditable(true);
            txtConfirmPassword.setEditable(true);
            
            // Clear password fields for editing
            txtPassword.setText("");
            txtConfirmPassword.setText("");
            
            // Update colors for editable fields
            txtFirstName.setBackground(Color.WHITE);
            txtLastName.setBackground(Color.WHITE);
            txtEmail.setBackground(Color.WHITE);
            txtPhoneNumber.setBackground(Color.WHITE);
            txtAddress.setBackground(Color.WHITE);
            txtSalary.setBackground(Color.WHITE);
            txtPassword.setBackground(Color.WHITE);
            txtConfirmPassword.setBackground(Color.WHITE);
        } else {
            // Save changes
            if (validateAndSave()) {
                btnEditSave.setText("Edit Profile");
                btnCancel.setVisible(false);
                
                // Disable editing
                txtFirstName.setEditable(false);
                txtLastName.setEditable(false);
                txtEmail.setEditable(false);
                txtPhoneNumber.setEditable(false);
                txtAddress.setEditable(false);
                txtSalary.setEditable(false);
                txtPassword.setEditable(false);
                txtConfirmPassword.setEditable(false);
                
                // Reset background colors
                txtFirstName.setBackground(FIELD_BACKGROUND);
                txtLastName.setBackground(FIELD_BACKGROUND);
                txtEmail.setBackground(FIELD_BACKGROUND);
                txtPhoneNumber.setBackground(FIELD_BACKGROUND);
                txtAddress.setBackground(FIELD_BACKGROUND);
                txtSalary.setBackground(FIELD_BACKGROUND);
                txtPassword.setBackground(FIELD_BACKGROUND);
                txtConfirmPassword.setBackground(FIELD_BACKGROUND);
                
                // Reload data to reflect any changes
                user = userController.getUserById(user.getUserId());
                loadUserData();
            }
        }
    }
    
    private boolean validateAndSave() {
        // Validate required fields
        if (txtFirstName.getText().trim().isEmpty() || txtLastName.getText().trim().isEmpty()) {
            showValidationError("First and last names are required");
            return false;
        }
    
        // Validate email format if provided
        if (!txtEmail.getText().trim().isEmpty() && 
            !txtEmail.getText().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            showValidationError("Please enter a valid email address");
            return false;
        }
    
        // Validate phone number if provided
        if (!txtPhoneNumber.getText().trim().isEmpty() && 
            !txtPhoneNumber.getText().matches("^\\d{10,15}$")) {
            showValidationError("Please enter a valid phone number (10-15 digits)");
            return false;
        }
    
        // Update user object with trimmed values
        user.setFirstName(txtFirstName.getText().trim());
        user.setLastName(txtLastName.getText().trim());
        user.setEmail(txtEmail.getText().trim().isEmpty() ? null : txtEmail.getText().trim());
        user.setPhoneNumber(txtPhoneNumber.getText().trim().isEmpty() ? null : txtPhoneNumber.getText().trim());
        user.setAddress(txtAddress.getText().trim().isEmpty() ? null : txtAddress.getText().trim());
    
        // Handle salary
        try {
            Number salaryValue = (Number) txtSalary.getValue();
            user.setSalary(salaryValue != null ? salaryValue.doubleValue() : 0);
        } catch (Exception e) {
            user.setSalary(0.0);
        }
    
        // Check password only if changed (not placeholder)
        String password = new String(txtPassword.getPassword());
        if (!password.isEmpty() && !password.equals("********")) {
            if (password.length() > 50) {
                showValidationError("Password must be 50 characters or less");
                return false;
            }
            
            String confirmPassword = new String(txtConfirmPassword.getPassword());
            if (!password.equals(confirmPassword)) {
                showValidationError("Passwords do not match");
                return false;
            }
            
            if (password.length() < 8) {
                showValidationError("Password must be at least 8 characters long");
                return false;
            }
            
            user.setPassword(password);
        } else {
            // Clear password if not changed
            user.setPassword(null);
        }
    
        // Save to database
        boolean success = userController.updateUser(user);
        
        if (success) {
            showSuccessMessage("Profile updated successfully");
            return true;
        } else {
            showValidationError("Failed to update profile in database. Please try again.");
            return false;
        }
    }
    
    private void showValidationError(String message) {
        // Custom styled error dialog
        JOptionPane.showMessageDialog(this, 
            message, 
            "Validation Error", 
            JOptionPane.ERROR_MESSAGE);
    }
    
    private void showSuccessMessage(String message) {
        // Custom styled success dialog
        JOptionPane.showMessageDialog(this, 
            message, 
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private String getInitials(String name) {
        if (name == null || name.isEmpty()) return "US";
        String[] parts = name.split(" ");
        if (parts.length >= 2) {
            return String.valueOf(parts[0].charAt(0)).toUpperCase() + 
                   String.valueOf(parts[1].charAt(0)).toUpperCase();
        } else if (parts[0].length() >= 2) {
            return parts[0].substring(0, 2).toUpperCase();
        }
        return name.substring(0, Math.min(2, name.length())).toUpperCase();
    }
    
    // Update the UserDashboard class to include the profile button action
    public static void addProfileAction(JButton profileButton, JFrame parent, int userId) {
        profileButton.addActionListener(e -> {
            ProfileDialog profileDialog = new ProfileDialog(parent, userId);
            profileDialog.setVisible(true);
        });
    }
}