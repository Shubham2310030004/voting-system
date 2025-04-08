import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Random;
import javax.swing.*;

public class CandidateLogin extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JTextField otpField;
    private JTextField captchaField;
    private JLabel captchaLabel;
    private int generatedOtp;
    private String generatedCaptcha;
    private Random random = new Random();

    public CandidateLogin() {
        setTitle("Candidate Login");
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
        mainPanel.setBackground(new Color(240, 240, 240));
        
        JLabel titleLabel = new JLabel("Candidate Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 102, 204));
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(240, 240, 240));
        
        JPanel emailPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        emailPanel.setBackground(new Color(240, 240, 240));
        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField(20);
        emailPanel.add(emailLabel);
        emailPanel.add(emailField);
        
        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        passwordPanel.setBackground(new Color(240, 240, 240));
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);

        JPanel otpPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        otpPanel.setBackground(new Color(240, 240, 240));
        JLabel otpLabel = new JLabel("OTP:");
        otpField = new JTextField(6);
        JButton generateOtpBtn = new JButton("Generate OTP");
        generateOtpBtn.addActionListener(e -> {
            generatedOtp = 100000 + random.nextInt(900000);
            JOptionPane.showMessageDialog(this, "Your OTP is: " + generatedOtp, 
                "OTP Generated", JOptionPane.INFORMATION_MESSAGE);
        });
        otpPanel.add(otpLabel);
        otpPanel.add(otpField);
        otpPanel.add(generateOtpBtn);

        JPanel captchaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        captchaPanel.setBackground(new Color(240, 240, 240));
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
        loginBtn.setFont(new Font("Arial", Font.BOLD, 14));
        loginBtn.setBackground(new Color(0, 102, 204));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.addActionListener(this::performLogin);
        
        JButton registerBtn = new JButton("Not registered? Sign up here");
        registerBtn.setBorderPainted(false);
        registerBtn.setContentAreaFilled(false);
        registerBtn.setForeground(new Color(0, 102, 204));
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerBtn.addActionListener(e -> {
            new CandidateRegistration().setVisible(true);
            dispose();
        });
        
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(emailPanel);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(passwordPanel);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(otpPanel);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(captchaPanel);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(loginBtn);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(registerBtn);
        
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
            showError("Please fill all fields");
            return;
        }

        if (!captchaInput.equals(generatedCaptcha)) {
            showError("Invalid CAPTCHA");
            generateCaptcha();
            captchaLabel.setText("CAPTCHA: " + generatedCaptcha);
            return;
        }

        if (Integer.parseInt(otpInput) != generatedOtp) {
            showError("Invalid OTP");
            return;
        }
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT candidate_id, name, is_approved FROM candidates WHERE email = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, password);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                if (!rs.getBoolean("is_approved")) {
                    showInfo("Your account is pending admin approval");
                    return;
                }
                
                int candidateId = rs.getInt("candidate_id");
                String name = rs.getString("name");
                
                new CandidateDashboard(candidateId, name).setVisible(true);
                dispose();
            } else {
                showError("Invalid email or password");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showError("Database error: " + ex.getMessage());
        }
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
}