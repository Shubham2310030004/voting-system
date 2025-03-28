import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ForgotPassword extends JFrame {
    private JTextField emailField;
    
    public ForgotPassword() {
        setTitle("Forgot Password");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }
    
    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(StyleConstants.SECONDARY_COLOR);
        
        JLabel titleLabel = new JLabel("Forgot Password", SwingConstants.CENTER);
        titleLabel.setFont(StyleConstants.SUBTITLE_FONT);
        titleLabel.setForeground(StyleConstants.PRIMARY_COLOR);
        
        JPanel formPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        formPanel.setBackground(StyleConstants.SECONDARY_COLOR);
        
        // Email
        JPanel emailPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        emailPanel.setBackground(StyleConstants.SECONDARY_COLOR);
        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField(20);
        emailPanel.add(emailLabel);
        emailPanel.add(emailField);
        
        // Submit Button
        JButton submitBtn = new JButton("Reset Password");
        submitBtn.setFont(StyleConstants.BUTTON_FONT);
        submitBtn.setBackground(StyleConstants.PRIMARY_COLOR);
        submitBtn.setForeground(Color.WHITE);
        submitBtn.addActionListener(this::resetPassword);
        
        // Back Button
        JButton backBtn = new JButton("Back to Login");
        backBtn.addActionListener(e -> {
            new VoterLogin().setVisible(true);
            dispose();
        });
        
        formPanel.add(emailPanel);
        formPanel.add(submitBtn);
        formPanel.add(backBtn);
        
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);
    }
    
    private void resetPassword(ActionEvent e) {
        String email = emailField.getText().trim();
        
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your email", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check if email exists
            String sql = "SELECT email FROM voters WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                // In a real application, you would send a password reset email
                // Here we'll just show a success message
                JOptionPane.showMessageDialog(this, 
                    "Password reset instructions sent to your email", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                new VoterLogin().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Email not found", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}