import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CastVote extends JFrame {
    private final int voterId;
    private JComboBox<String> candidateComboBox;
    
    public CastVote(int voterId) {
        this.voterId = voterId;
        setTitle("Cast Your Vote");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
        loadCandidates();
    }
    
    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(StyleConstants.SECONDARY_COLOR);
        
        JLabel titleLabel = new JLabel("Cast Your Vote", SwingConstants.CENTER);
        titleLabel.setFont(StyleConstants.SUBTITLE_FONT);
        titleLabel.setForeground(StyleConstants.PRIMARY_COLOR);
        
        JPanel formPanel = new JPanel(new GridLayout(3, 1, 10, 20));
        formPanel.setBackground(StyleConstants.SECONDARY_COLOR);
        
        // Candidate Selection
        JPanel candidatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        candidatePanel.setBackground(StyleConstants.SECONDARY_COLOR);
        JLabel candidateLabel = new JLabel("Select Candidate:");
        candidateComboBox = new JComboBox<>();
        candidateComboBox.setPreferredSize(new Dimension(300, 30));
        candidatePanel.add(candidateLabel);
        candidatePanel.add(candidateComboBox);
        
        // Submit Button
        JButton submitBtn = new JButton("Submit Vote");
        submitBtn.setFont(StyleConstants.BUTTON_FONT);
        submitBtn.setBackground(StyleConstants.PRIMARY_COLOR);
        submitBtn.setForeground(Color.WHITE);
        submitBtn.addActionListener(this::submitVote);
        
        // Back Button
        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.addActionListener(e -> {
            new VoterDashboard(voterId, "").setVisible(true);
            dispose();
        });
        
        formPanel.add(candidatePanel);
        formPanel.add(submitBtn);
        formPanel.add(backBtn);
        
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);
    }
    
    private void loadCandidates() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT candidate_id, name, party FROM candidates WHERE is_approved = true";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                int id = rs.getInt("candidate_id");
                String name = rs.getString("name");
                String party = rs.getString("party");
                candidateComboBox.addItem(name + " (" + party + ") - ID: " + id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading candidates", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void submitVote(ActionEvent e) {
        String selected = (String) candidateComboBox.getSelectedItem();
        if (selected == null || selected.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a candidate", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Extract candidate ID from the selection string
        int candidateId = Integer.parseInt(selected.split("ID: ")[1]);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check if already voted
            String checkSql = "SELECT has_voted FROM voters WHERE voter_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, voterId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next() && rs.getBoolean("has_voted")) {
                JOptionPane.showMessageDialog(this, "You have already voted", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Record the vote
            String voteSql = "INSERT INTO votes (voter_id, candidate_id) VALUES (?, ?)";
            PreparedStatement voteStmt = conn.prepareStatement(voteSql);
            voteStmt.setInt(1, voterId);
            voteStmt.setInt(2, candidateId);
            voteStmt.executeUpdate();
            
            // Mark voter as voted
            String updateSql = "UPDATE voters SET has_voted = true WHERE voter_id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateSql);
            updateStmt.setInt(1, voterId);
            updateStmt.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "Vote submitted successfully!", "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
            new VoterDashboard(voterId, "").setVisible(true);
            dispose();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error submitting vote", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}