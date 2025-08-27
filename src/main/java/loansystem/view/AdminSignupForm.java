package loansystem.view;

import loansystem.controller.UserController;
import loansystem.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.regex.Pattern;

public class AdminSignupForm extends JFrame {
    private JTextField firstNameField, lastNameField, usernameField, emailField, phoneField, addressField;
    private JPasswordField passwordField;
    private JButton signupButton;
    private JCheckBox rememberMeCheckBox;

    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public AdminSignupForm() {
        setTitle("Admin Signup");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 2));

        // Left panel (Image)
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());

        // Load the image
        ImageIcon imageIcon = new ImageIcon(getClass().getResource("/images/signup.jpg"));
        Image image = imageIcon.getImage().getScaledInstance(640, 720, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(image));
        leftPanel.add(imageLabel, BorderLayout.CENTER);
        add(leftPanel);

        // Right panel (Signup Form)
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(new Color(245, 245, 245));
        add(rightPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Title Label
        JLabel titleLabel = new JLabel("Admin Sign Up");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 153, 255));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        rightPanel.add(titleLabel, gbc);

        // Subtitle Label
        JLabel subtitleLabel = new JLabel("Create admin account");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(102, 102, 102));
        gbc.gridy = 1;
        rightPanel.add(subtitleLabel, gbc);

        // First Name Field
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        rightPanel.add(createFormLabel("First Name:"), gbc);
        gbc.gridx = 1;
        firstNameField = createFormTextField();
        rightPanel.add(firstNameField, gbc);

        // Last Name Field
        gbc.gridx = 0;
        gbc.gridy = 3;
        rightPanel.add(createFormLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        lastNameField = createFormTextField();
        rightPanel.add(lastNameField, gbc);

        // Username Field
        gbc.gridx = 0;
        gbc.gridy = 4;
        rightPanel.add(createFormLabel("Username:"), gbc);
        gbc.gridx = 1;
        usernameField = createFormTextField();
        rightPanel.add(usernameField, gbc);

        // Email Field
        gbc.gridx = 0;
        gbc.gridy = 5;
        rightPanel.add(createFormLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = createFormTextField();
        rightPanel.add(emailField, gbc);

        // Password Field
        gbc.gridx = 0;
        gbc.gridy = 6;
        rightPanel.add(createFormLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(250, 30));
        rightPanel.add(passwordField, gbc);

        // Phone Number Field
        gbc.gridx = 0;
        gbc.gridy = 7;
        rightPanel.add(createFormLabel("Phone Number:"), gbc);
        gbc.gridx = 1;
        phoneField = createFormTextField();
        rightPanel.add(phoneField, gbc);

        // Address Field
        gbc.gridx = 0;
        gbc.gridy = 8;
        rightPanel.add(createFormLabel("Address:"), gbc);
        gbc.gridx = 1;
        addressField = createFormTextField();
        rightPanel.add(addressField, gbc);

        // Remember Me Checkbox
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        rememberMeCheckBox = new JCheckBox("Remember me");
        rememberMeCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rememberMeCheckBox.setBackground(new Color(245, 245, 245));
        rightPanel.add(rememberMeCheckBox, gbc);

        // Signup Button
        gbc.gridy = 10;
        signupButton = new JButton("Create Admin Account");
        signupButton.setBackground(new Color(255, 0, 51)); // Red color for admin
        signupButton.setForeground(Color.WHITE);
        signupButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        signupButton.setPreferredSize(new Dimension(250, 40));
        signupButton.setFocusPainted(false);
        signupButton.addActionListener(this::performAdminSignup);
        rightPanel.add(signupButton, gbc);

        // Back to Login Link
        gbc.gridy = 11;
        JLabel backToLoginLabel = new JLabel("Already have an account? Login here");
        backToLoginLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backToLoginLabel.setForeground(new Color(0, 153, 255));
        backToLoginLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backToLoginLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });
        rightPanel.add(backToLoginLabel, gbc);

        setVisible(true);
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return label;
    }

    private JTextField createFormTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(250, 30));
        return field;
    }

    private void performAdminSignup(ActionEvent e) {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        String phone = phoneField.getText();
        String address = addressField.getText();

        // Check for empty fields
        if (anyFieldEmpty(firstName, lastName, username, email, password, phone, address)) {
            showError("All fields are required");
            return;
        }

        // Validate phone number (exactly 10 digits)
        if (!phone.matches("\\d{10}")) {
            showError("Phone number must be exactly 10 digits");
            return;
        }

        // Validate email format
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            showError("Please enter a valid email address");
            return;
        }

        // Validate password length
        if (password.length() <= 6) {
            showError("Password must be greater than 6 characters");
            return;
        }

        User admin = createAdminUser(firstName, lastName, username, email, password, phone, address);
        
        if (new UserController().registerUser(admin)) {
            showSuccess();
            redirectToLogin();
        } else {
            showError("Registration failed. Username or Email might be taken");
        }
    }

    private boolean anyFieldEmpty(String... fields) {
        for (String field : fields) {
            if (field.isEmpty()) return true;
        }
        return false;
    }

    private User createAdminUser(String firstName, String lastName, String username, 
                               String email, String password, String phone, String address) {
        User admin = new User();
        admin.setUsername(username);
        admin.setPassword(password);
        admin.setRole("admin");
        admin.setFirstName(firstName);
        admin.setLastName(lastName);
        admin.setEmail(email);
        admin.setPhoneNumber(phone);
        admin.setAddress(address);
        return admin;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess() {
        JOptionPane.showMessageDialog(this, "Admin account created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void redirectToLogin() {
        new LoginFrame().setVisible(true);
        dispose();
    }
}