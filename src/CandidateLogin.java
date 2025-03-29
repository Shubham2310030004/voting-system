import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class CandidateLogin extends JFrame {
    private JTextField usernameField;  // Changed from emailField to usernameField
    private JPasswordField passwordField;
    private JComboBox<String> emailDomainComboBox;
    
    public CandidateLogin() {
        setTitle("Candidate Login");
        setSize(500, 350);  // Increased width to accommodate the new components
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }
    
    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 240, 240));
        
        JLabel titleLabel = new JLabel("Candidate Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 102, 204));
        
        JPanel formPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        formPanel.setBackground(new Color(240, 240, 240));
        
        // Email - Modified to include username, @, and domain dropdown
        JPanel emailPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        emailPanel.setBackground(new Color(240, 240, 240));
        JLabel emailLabel = new JLabel("Email:");
        
        // Username part before @
        usernameField = new JTextField(10);
        
        // The @ symbol - constant and not editable
        JLabel atLabel = new JLabel("@");
        atLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Domain dropdown with popular email providers
        String[] popularDomains = {"gmail.com", "yahoo.com", "hotmail.com", "outlook.com", "icloud.com"};
        emailDomainComboBox = new JComboBox<>(popularDomains);
        emailDomainComboBox.setEditable(true); // Allow custom domains
        
        emailPanel.add(emailLabel);
        emailPanel.add(usernameField);
        emailPanel.add(atLabel);
        emailPanel.add(emailDomainComboBox);
        
        // Password
        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        passwordPanel.setBackground(new Color(240, 240, 240));
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);
        
        // Login Button
        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(new Font("Arial", Font.BOLD, 14));
        loginBtn.setBackground(new Color(0, 102, 204));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.addActionListener(this::performLogin);
        
        // Registration Link
        JButton registerBtn = new JButton("Not registered? Sign up here");
        registerBtn.setBorderPainted(false);
        registerBtn.setContentAreaFilled(false);
        registerBtn.setForeground(new Color(0, 102, 204));
        registerBtn.addActionListener(e -> {
            new CandidateRegistration().setVisible(true);
            dispose();
        });
        
        formPanel.add(emailPanel);
        formPanel.add(passwordPanel);
        formPanel.add(loginBtn);
        formPanel.add(registerBtn);
        
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);
    }
    
    private void performLogin(ActionEvent e) {
        String username = usernameField.getText().trim();
        String domain = (String) emailDomainComboBox.getSelectedItem();
        String email = username + "@" + domain;  // Combine to form full email
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || domain.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT candidate_id, name, is_approved FROM candidates " +
                         "WHERE email = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, password);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                if (!rs.getBoolean("is_approved")) {
                    JOptionPane.showMessageDialog(this,
                        "Your account is pending admin approval",
                        "Information", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                
                int candidateId = rs.getInt("candidate_id");
                String name = rs.getString("name");
                
                new CandidateDashboard(candidateId, name).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Invalid email or password", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Database error", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
