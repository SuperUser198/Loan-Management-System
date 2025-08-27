package loansystem.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LandingPage extends JFrame {
    private Timer typingTimer;
    private int typingIndex = 0;
    private String fullText = "<font color='#0066FF'>BEST</font> <font color='#FFA500'>LOAN SOLUTIONS</font>";
    private JLabel titleLabel;

    public LandingPage() {
        setTitle("Loan Management System - Home");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // SET FULL BACKGROUND IMAGE
        JLabel backgroundLabel = new JLabel(new ImageIcon(getClass().getResource("/images/landing.jpg")));
        setContentPane(backgroundLabel);
        backgroundLabel.setLayout(new BorderLayout());

        // HEADER PANEL (Navigation Bar)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Left-aligned logo
        JLabel logoLabel = new JLabel("<html><font color='orange'><b>LOAN<b></font> <font color='orange'><b>SYSTEM<b></font></html>");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        headerPanel.add(logoLabel, BorderLayout.WEST);

        // Center-aligned menu items
        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        menuPanel.setOpaque(false);

        JButton homeBtn = createBoldNavButton("Home");
       
        JButton calculatorBtn = createBoldNavButton("EMI Calculator");
        JButton supportBtn = createBoldNavButton("Support");

        // Add action listeners
        homeBtn.addActionListener(e -> {
            // Just bring to front if already open
            this.toFront();
            this.requestFocus();
        });

       

        calculatorBtn.addActionListener(e -> {
            new EMICalculatorFrame().setVisible(true);
        });

        supportBtn.addActionListener(e -> showModernSupportDialog());

        menuPanel.add(homeBtn);
       
        menuPanel.add(calculatorBtn);
        menuPanel.add(supportBtn);

        headerPanel.add(menuPanel, BorderLayout.CENTER);

        // Right-aligned auth buttons
        JPanel authPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        authPanel.setOpaque(false);

        JButton signInBtn = new RoundedButton("Sign In", new Color(70, 130, 180));
        JButton signUpBtn = new RoundedButton("Sign Up", new Color(60, 179, 113));

        signInBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            this.dispose(); // Close current window
        });

        signUpBtn.addActionListener(e -> {
            new SignupFrame().setVisible(true);
            this.dispose(); // Close current window
        });

        authPanel.add(signInBtn);
        authPanel.add(signUpBtn);
        headerPanel.add(authPanel, BorderLayout.EAST);

        // HERO SECTION (Main Content)
        JPanel heroPanel = new JPanel(new GridBagLayout());
        heroPanel.setOpaque(false);
        heroPanel.setBorder(BorderFactory.createEmptyBorder(0, 100, 100, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel subtitleLabel = new JLabel("WE PROVIDE");
        subtitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        subtitleLabel.setForeground(Color.WHITE);

        titleLabel = new JLabel();
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));

        JLabel description = new JLabel("<html><div style='width: 500px; color: white;'>Track your loans, calculate EMIs, and manage payments effortlessly.<br>"
                + "Our system makes financial management simple and efficient.</div></html>");
        description.setFont(new Font("Segoe UI", Font.PLAIN, 18));

        JButton getStartedButton = new RoundedButton("GET STARTED", new Color(255, 165, 0));
        getStartedButton.setPreferredSize(new Dimension(200, 50));
        getStartedButton.setFont(new Font("Segoe UI", Font.BOLD, 16));

        getStartedButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            this.dispose();
        });

        textPanel.add(Box.createVerticalStrut(50));
        textPanel.add(subtitleLabel);
        textPanel.add(Box.createVerticalStrut(10));
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(20));
        textPanel.add(description);
        textPanel.add(Box.createVerticalStrut(30));
        textPanel.add(getStartedButton);

        gbc.gridx = 0;
        gbc.gridy = 0;
        heroPanel.add(textPanel, gbc);

        // ADDING COMPONENTS
        backgroundLabel.add(headerPanel, BorderLayout.NORTH);
        backgroundLabel.add(heroPanel, BorderLayout.CENTER);

        // Start typing animation
        startTypingAnimation();
    }

    private void startTypingAnimation() {
        // Create a hidden measurement label to stabilize the layout
        JLabel measurementLabel = new JLabel("<html>" + fullText + "</html>");
        measurementLabel.setFont(titleLabel.getFont());
        measurementLabel.setSize(measurementLabel.getPreferredSize());
        
        typingTimer = new Timer(50, new ActionListener() {
            private boolean showCursor = true;
            private int cursorBlinkDelay = 0;
            private final int BLINK_INTERVAL = 10;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                // Calculate visible text portion
                String visibleText = typingIndex <= fullText.length() 
                    ? fullText.substring(0, typingIndex)
                    : "";
                    
                // Add cursor
                String cursor = showCursor ? "<span style='border-right: 2px solid #0066FF;'>&nbsp;</span>" : "&nbsp;";
                
                // Create stable HTML structure
                String htmlText = "<html><div style='display:inline-block; height:" + 
                                measurementLabel.getHeight() + "px;'>" +
                                visibleText + cursor + "</div></html>";
                
                titleLabel.setText(htmlText);
                
                // Update typing position
                if (typingIndex <= fullText.length() * 2) {
                    typingIndex++;
                } else {
                    typingIndex = 0;
                }
                
                // Handle cursor blinking
                if (cursorBlinkDelay++ >= BLINK_INTERVAL) {
                    showCursor = !showCursor;
                    cursorBlinkDelay = 0;
                }
            }
        });
        
        typingTimer.setInitialDelay(50);
        typingTimer.start();
    }

    private void showModernSupportDialog() {
        JDialog dialog = new JDialog(this, "Support Center", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(500, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(new Color(245, 245, 245));

        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        JLabel headerLabel = new JLabel("How can we help you?");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);
        dialog.add(headerPanel, BorderLayout.NORTH);

        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        contentPanel.setBackground(new Color(245, 245, 245));

        JLabel supportLabel = new JLabel("<html><div style='text-align:center;'>"
                + "<p>Our support team is available 24/7 to assist you with any questions or issues.</p>"
                + "<p>Please choose from the options below:</p></div></html>");
        supportLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        supportLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton emailBtn = new JButton("Email Support");
        styleSupportButton(emailBtn);
        emailBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(dialog, "Please email us at: support@loansystem.com", "Email Support", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton callBtn = new JButton("Call Support");
        styleSupportButton(callBtn);
        callBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(dialog, "Call us at: 9119870177", "Call Support", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton chatBtn = new JButton("Live Chat");
        styleSupportButton(chatBtn);
        chatBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(dialog, "Our live chat is currently offline. Please try email or phone support.", "Live Chat", JOptionPane.INFORMATION_MESSAGE);
        });

        contentPanel.add(supportLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(emailBtn);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(callBtn);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(chatBtn);

        dialog.add(contentPanel, BorderLayout.CENTER);

        // Footer panel
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(new Color(245, 245, 245));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 15));

        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        closeBtn.addActionListener(e -> dialog.dispose());
        footerPanel.add(closeBtn);

        dialog.add(footerPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void styleSupportButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private JButton createBoldNavButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(new Color(255, 215, 0));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(Color.WHITE);
            }
        });
        
        return button;
    }
}

class RoundedButton extends JButton {
    private Color baseColor;
    private Color hoverColor;
    private Color clickColor;
    private boolean isHovered = false;
    private boolean isPressed = false;

    public RoundedButton(String text, Color color) {
        super(text);
        this.baseColor = color;
        this.hoverColor = color.darker();
        this.clickColor = color.brighter();
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setForeground(Color.WHITE);
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int arc = height;

        Color buttonColor = isPressed ? clickColor : (isHovered ? hoverColor : baseColor);

        g2.setColor(new Color(0, 0, 0, 30));
        g2.fillRoundRect(2, 2, width - 4, height - 4, arc, arc);

        g2.setColor(buttonColor);
        g2.fillRoundRect(0, 0, width, height, arc, arc);

        super.paintComponent(g);
    }
}