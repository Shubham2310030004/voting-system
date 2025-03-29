import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class CandidateList extends JFrame {
    private JTable candidateTable;
    
    public CandidateList() {
        setTitle("Candidate Management");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
        loadCandidates();
    }
    
    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(StyleConstants.SECONDARY_COLOR);
        
        JLabel titleLabel = new JLabel("Candidate Management", SwingConstants.CENTER);
        titleLabel.setFont(StyleConstants.SUBTITLE_FONT);
        titleLabel.setForeground(StyleConstants.PRIMARY_COLOR);
        
        candidateTable = new JTable();
        candidateTable.setModel(new DefaultTableModel(
            new Object[]{"ID", "Name", "Email", "Party", "Approved"}, 0
        ));
        JScrollPane scrollPane = new JScrollPane(candidateTable);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(StyleConstants.SECONDARY_COLOR);
        
        JButton approveBtn = new JButton("Approve Candidate");
        approveBtn.addActionListener(e -> approveCandidate());
        
        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.addActionListener(e -> {
            new AdminDashboard().setVisible(true);
            dispose();
        });
        
        buttonPanel.add(approveBtn);
        buttonPanel.add(backBtn);
        
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }
    
    private void loadCandidates() {
        DefaultTableModel model = (DefaultTableModel) candidateTable.getModel();
        model.setRowCount(0); // Clear existing data
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT candidate_id, name, email, party, is_approved FROM candidates";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("candidate_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("party"),
                    rs.getBoolean("is_approved") ? "Yes" : "No"
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading candidates", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void approveCandidate() {
        int selectedRow = candidateTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a candidate", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int candidateId = (int) candidateTable.getValueAt(selectedRow, 0);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE candidates SET is_approved = true WHERE candidate_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, candidateId);
            stmt.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "Candidate approved successfully", "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            loadCandidates(); 
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error approving candidate", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}