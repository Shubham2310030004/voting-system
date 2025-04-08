import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import java.util.Random;

public class VoterLogin extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JTextField otpField;
    private JTextField captchaField;
    private JLabel captchaLabel;
    private int generatedOtp;
    private String generatedCaptcha;
    private Random random = new Random();

    public VoterLogin() {
        setTitle("Voter Login");
        setSize(400, 450); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        generateCaptcha();
        initUI();
    }

    private void generateCaptcha() {
        generatedCaptcha = String.format("%04d", random.nextInt(10000));
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(StyleConstants.SECONDARY_COLOR);
        
        JLabel titleLabel = new JLabel("Voter Login", SwingConstants.CENTER);
        titleLabel.setFont(StyleConstants.SUBTITLE_FONT);
        titleLabel.setForeground(StyleConstants.PRIMARY_COLOR);
        
        JPanel formPanel = new JPanel(new GridLayout(7, 1, 10, 10)); // Increased rows
        formPanel.setBackground(StyleConstants.SECONDARY_COLOR);
        
        JPanel emailPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        emailPanel.setBackground(StyleConstants.SECONDARY_COLOR);
        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField(20);
        emailPanel.add(emailLabel);
        emailPanel.add(emailField);
        
        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        passwordPanel.setBackground(StyleConstants.SECONDARY_COLOR);
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);

        JPanel otpPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        otpPanel.setBackground(StyleConstants.SECONDARY_COLOR);
        JLabel otpLabel = new JLabel("OTP:");
        otpField = new JTextField(6);
        JButton generateOtpBtn = new JButton("Generate OTP");
        generateOtpBtn.addActionListener(e -> {
            generatedOtp = 100000 + random.nextInt(900000); // 6-digit OTP
            JOptionPane.showMessageDialog(this, "Your OTP is: " + generatedOtp, 
                "OTP Generated", JOptionPane.INFORMATION_MESSAGE);
        });
        otpPanel.add(otpLabel);
        otpPanel.add(otpField);
        otpPanel.add(generateOtpBtn);

        JPanel captchaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        captchaPanel.setBackground(StyleConstants.SECONDARY_COLOR);
        captchaLabel = new JLabel("CAPTCHA: " + generatedCaptcha);
        captchaField = new JTextField(6);
        JButton refreshCaptchaBtn = new JButton("Refresh");
        refreshCaptchaBtn.addActionListener(e -> {
            generateCaptcha();
            captchaLabel.setText("CAPTCHA: " + generatedCaptcha);
        });
        captchaPanel.add(captchaLabel);
        captchaPanel.add(captchaField);
        captchaPanel.add(refreshCaptchaBtn);
        
        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(StyleConstants.BUTTON_FONT);
        loginBtn.setBackground(StyleConstants.PRIMARY_COLOR);
        loginBtn.setForeground(Color.WHITE);
        loginBtn.addActionListener(this::performLogin);
        
        JButton forgotPasswordBtn = new JButton("Forgot Password?");
        forgotPasswordBtn.setBorderPainted(false);
        forgotPasswordBtn.setContentAreaFilled(false);
        forgotPasswordBtn.setForeground(StyleConstants.PRIMARY_COLOR);
        forgotPasswordBtn.addActionListener(e -> new ForgotPassword().setVisible(true));
        
        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        registerPanel.setBackground(StyleConstants.SECONDARY_COLOR);
        JLabel registerLabel = new JLabel("Don't have an account?");
        JButton registerBtn = new JButton("Register");
        registerBtn.setBorderPainted(false);
        registerBtn.setContentAreaFilled(false);
        registerBtn.setForeground(StyleConstants.PRIMARY_COLOR);
        registerBtn.addActionListener(e -> {
            new VoterRegistration().setVisible(true);
            dispose();
        });
        registerPanel.add(registerLabel);
        registerPanel.add(registerBtn);
        
        formPanel.add(emailPanel);
        formPanel.add(passwordPanel);
        formPanel.add(otpPanel);
        formPanel.add(captchaPanel);
        formPanel.add(loginBtn);
        formPanel.add(forgotPasswordBtn);
        formPanel.add(registerPanel);
        
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);
    }
    
    private void performLogin(ActionEvent e) {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String otpInput = otpField.getText().trim();
        String captchaInput = captchaField.getText().trim();
        
        if (email.isEmpty() || password.isEmpty() || otpInput.isEmpty() || captchaInput.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!captchaInput.equals(generatedCaptcha)) {
            JOptionPane.showMessageDialog(this, "Invalid CAPTCHA", "Error", JOptionPane.ERROR_MESSAGE);
            generateCaptcha();
            captchaLabel.setText("CAPTCHA: " + generatedCaptcha);
            return;
        }

        if (Integer.parseInt(otpInput) != generatedOtp) {
            JOptionPane.showMessageDialog(this, "Invalid OTP", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT voter_id, name, password FROM voters WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                if (PasswordUtils.verifyPassword(password, storedPassword)) {
                    int voterId = rs.getInt("voter_id");
                    String name = rs.getString("name");
                    
                    JOptionPane.showMessageDialog(this, "Login successful! Welcome " + name, 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                    new VoterDashboard(voterId, name).setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid password", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Email not found", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}