import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class VoterList extends JFrame {
    private JTable voterTable;
    
    public VoterList() {
        setTitle("Voter List");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
        loadVoters();
    }
    
    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(StyleConstants.SECONDARY_COLOR);
        
        JLabel titleLabel = new JLabel("Voter List", SwingConstants.CENTER);
        titleLabel.setFont(StyleConstants.SUBTITLE_FONT);
        titleLabel.setForeground(StyleConstants.PRIMARY_COLOR);

        voterTable = new JTable();
        voterTable.setModel(new DefaultTableModel(
            new Object[]{"ID", "Name", "Email", "Voter Card Number", "Has Voted"}, 0
        ));
        JScrollPane scrollPane = new JScrollPane(voterTable);

        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.addActionListener(e -> {
            new AdminDashboard().setVisible(true);
            dispose();
        });
        
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(backBtn, BorderLayout.SOUTH);
        add(mainPanel);
    }
    
    private void loadVoters() {
        DefaultTableModel model = (DefaultTableModel) voterTable.getModel();
        model.setRowCount(0);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT voter_id, name, email, voter_card_number, has_voted FROM voters";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                model.addRow(new Object[]{
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
}