package loansystem.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SignupFrame extends JFrame {
    private JPanel leftPanel, rightPanel;
    private JButton userSignupButton, adminSignupButton, backButton;

    public SignupFrame() {
        setTitle("Loan Management System");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 2));

        // Left panel (Image)
        leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());

        // Load the image
        ImageIcon imageIcon = new ImageIcon(getClass().getResource("/images/signup.jpg")); // Path to your image
        Image image = imageIcon.getImage().getScaledInstance(640, 720, Image.SCALE_SMOOTH); // Resize the image
        JLabel imageLabel = new JLabel(new ImageIcon(image));
        leftPanel.add(imageLabel, BorderLayout.CENTER);

        add(leftPanel);

        // Right panel (Options)
        rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(new Color(245, 245, 245)); // Light gray background
        add(rightPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15); // Increased padding
        gbc.gridwidth = 2; // Span across two columns
        gbc.anchor = GridBagConstraints.CENTER; // Center alignment

        // Title Label
        JLabel titleLabel = new JLabel("SIGNUP");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28)); // Larger font size
        titleLabel.setForeground(new Color(0, 153, 255)); // Blue color
        gbc.gridx = 0;
        gbc.gridy = 0;
        rightPanel.add(titleLabel, gbc);

        // Sign Up as User Button
        userSignupButton = new JButton("Sign Up as User");
        userSignupButton.setBackground(new Color(0, 153, 255)); // Blue color
        userSignupButton.setForeground(Color.WHITE);
        userSignupButton.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Modern font
        userSignupButton.setPreferredSize(new Dimension(250, 50)); // Larger button
        userSignupButton.setFocusPainted(false); // Remove focus border
        userSignupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new UserSignupForm().setVisible(true); // Open User Signup Form
                dispose(); // Close the current window
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 1;
        rightPanel.add(userSignupButton, gbc);

        // Sign Up as Admin Button
        adminSignupButton = new JButton("Sign Up as Admin");
        adminSignupButton.setBackground(new Color(255, 0, 51)); // Red color
        adminSignupButton.setForeground(Color.WHITE);
        adminSignupButton.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Modern font
        adminSignupButton.setPreferredSize(new Dimension(250, 50)); // Larger button
        adminSignupButton.setFocusPainted(false); // Remove focus border
        adminSignupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AdminSignupForm().setVisible(true); // Open Admin Signup Form
                dispose(); // Close the current window
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 2;
        rightPanel.add(adminSignupButton, gbc);

        // Back to Login Button
        backButton = new JButton("Back to Login");
        backButton.setBackground(new Color(102, 102, 102)); // Gray color
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Modern font
        backButton.setPreferredSize(new Dimension(250, 50)); // Larger button
        backButton.setFocusPainted(false); // Remove focus border
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LoginFrame().setVisible(true); // Redirect to login page
                dispose(); // Close the signup window
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 3;
        rightPanel.add(backButton, gbc);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SignupFrame::new);
    }
}