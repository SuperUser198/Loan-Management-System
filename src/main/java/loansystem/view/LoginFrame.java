package loansystem.view;

import loansystem.controller.UserController;
import loansystem.model.User;
import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.RoundRectangle2D;

public class LoginFrame extends JFrame {
    private JPanel leftPanel, rightPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel backToSignupLabel;

    public LoginFrame() {
        setTitle("LoanManagementSystem - Login");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 2));

        // Left panel (Image)
        leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());

        // Load the image
        ImageIcon imageIcon = new ImageIcon(getClass().getResource("/images/login.jpg"));
        Image image = imageIcon.getImage().getScaledInstance(640, 720, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(image));
        leftPanel.add(imageLabel, BorderLayout.CENTER);
        add(leftPanel);

        // Right panel (Login Form)
        rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(new Color(245, 245, 245));
        add(rightPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Title Label
        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 153, 255));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        rightPanel.add(titleLabel, gbc);

        // Subtitle Label
        JLabel subtitleLabel = new JLabel("Welcome back! Please login to your account");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(102, 102, 102));
        gbc.gridy = 1;
        rightPanel.add(subtitleLabel, gbc);

        // Username Field
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        rightPanel.add(createFormLabel("Username:"), gbc);
        gbc.gridx = 1;
        usernameField = createFormTextField();
        rightPanel.add(usernameField, gbc);

        // Password Field
        gbc.gridx = 0;
        gbc.gridy = 3;
        rightPanel.add(createFormLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(250, 30));
        rightPanel.add(passwordField, gbc);

        // Login Button
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        loginButton = createRoundedButton("Login", new Color(0, 153, 255), Color.WHITE);
        loginButton.addActionListener(this::performLogin);
        rightPanel.add(loginButton, gbc);

        // Back to Signup Link
        gbc.gridy = 5;
        backToSignupLabel = new JLabel("Don't have an account? Sign up here");
        backToSignupLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backToSignupLabel.setForeground(new Color(0, 153, 255));
        backToSignupLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backToSignupLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new SignupFrame().setVisible(true);
                dispose();
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                backToSignupLabel.setForeground(new Color(0, 102, 204));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                backToSignupLabel.setForeground(new Color(0, 153, 255));
            }
        });
        rightPanel.add(backToSignupLabel, gbc);

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

    private JButton createRoundedButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 15, 15));
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(250, 40));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }

    private void performLogin(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showStyledError("Login Error", "Username and password are required");
            return;
        }

        UserController userController = new UserController();
        User user = userController.authenticateUser(username, password);

        if (user != null) {
            // Check account status
            if ("suspended".equalsIgnoreCase(user.getAccountStatus())) {
                showStyledError("Account Suspended", 
                    "Your account has been suspended. Please contact administrator.");
                return;
            } else if ("blocked".equalsIgnoreCase(user.getAccountStatus())) {
                showStyledError("Account Blocked", 
                    "Your account has been blocked. Please contact support.");
                return;
            }

            showStyledSuccess("Login Successful", "Welcome back, " + user.getUsername() + "!");
            
            if (user.getRole().equals("admin")) {
                new AdminDashboard(user.getUserId()).setVisible(true);
            } else {
                new UserDashboard(user.getUserId()).setVisible(true);
            }
            dispose();
        } else {
            showStyledError("Login Failed", "Invalid username or password.");
        }
    }

    private void showStyledError(String title, String message) {
        JOptionPane.showMessageDialog(this, 
            createStyledMessagePanel(message, new Color(255, 235, 238)), 
            title, 
            JOptionPane.ERROR_MESSAGE);
    }

    private void showStyledSuccess(String title, String message) {
        JOptionPane.showMessageDialog(this, 
            createStyledMessagePanel(message, new Color(230, 245, 230)), 
            title, 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private JPanel createStyledMessagePanel(String message, Color bgColor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel label = new JLabel(message);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(label, BorderLayout.CENTER);
        
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame();
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Start maximized
        });
    }
}