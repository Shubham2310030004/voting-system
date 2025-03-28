import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class VoterRegistration extends JFrame {
    private JTextField nameField, emailField, dobField, cardNumberField, addressField;
    private JPasswordField passwordField, confirmPasswordField;
    
    public VoterRegistration() {
        setTitle("Voter Registration");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }
    
    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(StyleConstants.SECONDARY_COLOR);
        
        JLabel titleLabel = new JLabel("Voter Registration", SwingConstants.CENTER);
        titleLabel.setFont(StyleConstants.SUBTITLE_FONT);
        titleLabel.setForeground(StyleConstants.PRIMARY_COLOR);
        
        JPanel formPanel = new JPanel(new GridLayout(9, 1, 5, 5));
        formPanel.setBackground(StyleConstants.SECONDARY_COLOR);
        
        // Name
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        namePanel.setBackground(StyleConstants.SECONDARY_COLOR);
        JLabel nameLabel = new JLabel("Full Name:");
        nameField = new JTextField(20);
        namePanel.add(nameLabel);
        namePanel.add(nameField);
        
        // Email
        JPanel emailPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        emailPanel.setBackground(StyleConstants.SECONDARY_COLOR);
        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField(20);
        emailPanel.add(emailLabel);
        emailPanel.add(emailField);
        
        // Password
        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        passwordPanel.setBackground(StyleConstants.SECONDARY_COLOR);
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);
        
        // Confirm Password
        JPanel confirmPasswordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        confirmPasswordPanel.setBackground(StyleConstants.SECONDARY_COLOR);
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordField = new JPasswordField(20);
        confirmPasswordPanel.add(confirmPasswordLabel);
        confirmPasswordPanel.add(confirmPasswordField);
        
        // Date of Birth
        JPanel dobPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dobPanel.setBackground(StyleConstants.SECONDARY_COLOR);
        JLabel dobLabel = new JLabel("Date of Birth (YYYY-MM-DD):");
        dobField = new JTextField(20);
        dobPanel.add(dobLabel);
        dobPanel.add(dobField);
        
        // Voter Card Number
        JPanel cardNumberPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cardNumberPanel.setBackground(StyleConstants.SECONDARY_COLOR);
        JLabel cardNumberLabel = new JLabel("Voter Card Number:");
        cardNumberField = new JTextField(20);
        cardNumberPanel.add(cardNumberLabel);
        cardNumberPanel.add(cardNumberField);
        
        // Address
        JPanel addressPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addressPanel.setBackground(StyleConstants.SECONDARY_COLOR);
        JLabel addressLabel = new JLabel("Address:");
        addressField = new JTextField(20);
        addressPanel.add(addressLabel);
        addressPanel.add(addressField);
        
        // Register Button
        JButton registerBtn = new JButton("Register");
        registerBtn.setFont(StyleConstants.BUTTON_FONT);
        registerBtn.setBackground(StyleConstants.PRIMARY_COLOR);
        registerBtn.setForeground(Color.WHITE);
        registerBtn.addActionListener(this::registerVoter);
        
        // Back Button
        JButton backBtn = new JButton("Back to Login");
        backBtn.addActionListener(e -> {
            new VoterLogin().setVisible(true);
            dispose();
        });
        
        formPanel.add(namePanel);
        formPanel.add(emailPanel);
        formPanel.add(passwordPanel);
        formPanel.add(confirmPasswordPanel);
        formPanel.add(dobPanel);
        formPanel.add(cardNumberPanel);
        formPanel.add(addressPanel);
        formPanel.add(registerBtn);
        formPanel.add(backBtn);
        
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);
    }
    
    private void registerVoter(ActionEvent e) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String dobStr = dobField.getText().trim();
        String cardNumber = cardNumberField.getText().trim();
        String address = addressField.getText().trim();
        
        // Validation
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || 
            confirmPassword.isEmpty() || dobStr.isEmpty() || cardNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all required fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (password.length() < 8) {
            JOptionPane.showMessageDialog(this, "Password must be at least 8 characters", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            LocalDate dob = LocalDate.parse(dobStr, DateTimeFormatter.ISO_DATE);
            LocalDate eighteenYearsAgo = LocalDate.now().minusYears(18);
            if (dob.isAfter(eighteenYearsAgo)) {
                JOptionPane.showMessageDialog(this, "You must be at least 18 years old", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Hash password
        String hashedPassword = PasswordUtils.createPasswordHash(password);
        
        // Save to database
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check if email already exists
            String checkSql = "SELECT email FROM voters WHERE email = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Email already registered", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check if voter card number exists
            checkSql = "SELECT voter_card_number FROM voters WHERE voter_card_number = ?";
            checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, cardNumber);
            rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Voter card number already registered", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Insert new voter
            String insertSql = "INSERT INTO voters (name, email, password, dob, address, voter_card_number) " +
                              "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setString(1, name);
            insertStmt.setString(2, email);
            insertStmt.setString(3, hashedPassword);
            insertStmt.setString(4, dobStr);
            insertStmt.setString(5, address);
            insertStmt.setString(6, cardNumber);
            
            int rowsAffected = insertStmt.executeUpdate();
            
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Registration successful! Please login.", "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                new VoterLogin().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error during registration", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}