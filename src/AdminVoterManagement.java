import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AdminVoterManagement extends JFrame {
    private JTable voterTable;
    private DefaultTableModel tableModel;
    
    public AdminVoterManagement() {
        setTitle("Voter Management");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
        loadVoters();
    }
    
    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
   
        JLabel titleLabel = new JLabel("Voter Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        

        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Email", "Voter Card #", "Has Voted"}, 0);
        voterTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(voterTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
   
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton deleteBtn = new JButton("Delete Selected Voter");
        deleteBtn.addActionListener(this::deleteSelectedVoter);
        
        JButton refreshBtn = new JButton("Refresh List");
        refreshBtn.addActionListener(e -> loadVoters());
        
        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.addActionListener(e -> {
            new AdminDashboard().setVisible(true);
            dispose();
        });
        
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(backBtn);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }
    
    private void loadVoters() {
        tableModel.setRowCount(0); // Clear existing data
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT voter_id, name, email, voter_card_number, has_voted FROM voters";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("voter_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("voter_card_number"),
                    rs.getBoolean("has_voted") ? "Yes" : "No"
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading voters", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteSelectedVoter(ActionEvent e) {
        int selectedRow = voterTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a voter to delete", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int voterId = (int) voterTable.getValueAt(selectedRow, 0);
        String voterName = (String) voterTable.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete voter: " + voterName + "?", 
            "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "DELETE FROM voters WHERE voter_id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, voterId);
                
                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    JOptionPane.showMessageDialog(this, "Voter deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadVoters();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting voter", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}