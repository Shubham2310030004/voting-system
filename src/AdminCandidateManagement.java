import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AdminCandidateManagement extends JFrame {
    private JTable candidateTable;
    private DefaultTableModel tableModel;
    
    public AdminCandidateManagement() {
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
        
        
        JLabel titleLabel = new JLabel("Candidate Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        
        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Email", "Party", "Approved"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Integer.class : String.class;
            }
        };
        
        candidateTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(candidateTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton deleteBtn = new JButton("Delete Selected Candidate");
        deleteBtn.addActionListener(this::deleteSelectedCandidate);
        
        JButton approveBtn = new JButton("Approve/Disapprove");
        approveBtn.addActionListener(this::toggleApproval);
        
        JButton refreshBtn = new JButton("Refresh List");
        refreshBtn.addActionListener(e -> loadCandidates());
        
        
        buttonPanel.add(deleteBtn);
        buttonPanel.add(approveBtn);
        buttonPanel.add(refreshBtn);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }
    
    private void loadCandidates() {
        tableModel.setRowCount(0);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT candidate_id, name, email, party, is_approved FROM candidates";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                tableModel.addRow(new Object[]{
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
    
    private void deleteSelectedCandidate(ActionEvent e) {
        int selectedRow = candidateTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a candidate to delete", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            Object idValue = tableModel.getValueAt(selectedRow, 0);
            int candidateId = (idValue instanceof Number) ? ((Number)idValue).intValue() : Integer.parseInt(idValue.toString());
            
            String candidateName = tableModel.getValueAt(selectedRow, 1).toString();
            
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete candidate: " + candidateName + "?", 
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DatabaseConnection.getConnection()) {
                    String sql = "DELETE FROM candidates WHERE candidate_id = ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, candidateId);
                    
                    int affectedRows = stmt.executeUpdate();
                    if (affectedRows > 0) {
                        JOptionPane.showMessageDialog(this, "Candidate deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadCandidates();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error deleting candidate", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid candidate selection", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void toggleApproval(ActionEvent e) {
        int selectedRow = candidateTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a candidate", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {

            Object idValue = tableModel.getValueAt(selectedRow, 0);
            int candidateId = (idValue instanceof Number) ? ((Number)idValue).intValue() : Integer.parseInt(idValue.toString());
            
            String currentStatus = tableModel.getValueAt(selectedRow, 4).toString();
            boolean newStatus = !currentStatus.equalsIgnoreCase("Yes");
            
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "UPDATE candidates SET is_approved = ? WHERE candidate_id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setBoolean(1, newStatus);
                stmt.setInt(2, candidateId);
                
                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Candidate approval status updated", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadCandidates();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Error updating candidate status", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid candidate selection", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}