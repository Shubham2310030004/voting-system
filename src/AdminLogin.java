import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Random;
import javax.swing.*;

public class AdminLogin extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField otpField;
    private JTextField captchaField;
    private JLabel captchaLabel;
    private int generatedOtp;
    private String generatedCaptcha;
    private Random random = new Random();

    public AdminLogin() {
        setTitle("Admin Login");
        setSize(400, 400);
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
        
        JLabel titleLabel = new JLabel("Admin Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 102, 204));
        
        JPanel formPanel = new JPanel(new GridLayout(5, 1, 10, 20)); 
        formPanel.setBackground(new Color(240, 240, 240));
        
        JPanel usernamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        usernamePanel.setBackground(new Color(240, 240, 240));
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(20);
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameField);
        
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
        loginBtn.addActionListener(this::performLogin);
        
        formPanel.add(usernamePanel);
        formPanel.add(passwordPanel);
        formPanel.add(otpPanel);
        formPanel.add(captchaPanel);
        formPanel.add(loginBtn);
        
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);
    }
    
    private void performLogin(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String otpInput = otpField.getText().trim();
        String captchaInput = captchaField.getText().trim();
        
        if (username.isEmpty() || password.isEmpty() || otpInput.isEmpty() || captchaInput.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
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
            String sql = "SELECT admin_id FROM admins WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, 
                    "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                new AdminDashboard().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Database error", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}