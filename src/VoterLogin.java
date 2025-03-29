import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class VoterLogin extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    
    public VoterLogin() {
        setTitle("Voter Login");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }
    
    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(StyleConstants.SECONDARY_COLOR);
        
        JLabel titleLabel = new JLabel("Voter Login", SwingConstants.CENTER);
        titleLabel.setFont(StyleConstants.SUBTITLE_FONT);
        titleLabel.setForeground(StyleConstants.PRIMARY_COLOR);
        
        JPanel formPanel = new JPanel(new GridLayout(5, 1, 10, 10));
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
        
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
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