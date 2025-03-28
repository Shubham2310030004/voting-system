import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class CandidateRegistration extends JFrame {
    private JTextField nameField, emailField, partyField;
    private JPasswordField passwordField;
    
    public CandidateRegistration() {
        setTitle("Candidate Registration");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }
    
    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 240, 240));
        
        JLabel titleLabel = new JLabel("Candidate Registration", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 102, 204));
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(240, 240, 240));
        
        // Add form fields (name, email, password, party) similar to login example
        // ...
        
        JButton registerBtn = new JButton("Register");
        registerBtn.addActionListener(this::registerCandidate);
        
        // Add all components to formPanel
        // ...
        
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);
    }
    
    private void registerCandidate(ActionEvent e) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String party = partyField.getText().trim();
        
        // Validation
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || party.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please fill all required fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check if email exists
            String checkSql = "SELECT email FROM candidates WHERE email = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, email);
            
            if (checkStmt.executeQuery().next()) {
                JOptionPane.showMessageDialog(this,
                    "Email already registered", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Insert new candidate with plain text password
            String insertSql = "INSERT INTO candidates (name, email, password, party) " +
                             "VALUES (?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setString(1, name);
            insertStmt.setString(2, email);
            insertStmt.setString(3, password); // Storing plain text password
            insertStmt.setString(4, party);
            
            if (insertStmt.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this,
                    "Registration successful! Waiting for admin approval.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                new CandidateLogin().setVisible(true);
                dispose();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Registration failed", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}