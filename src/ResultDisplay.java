import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ResultDisplay extends JFrame {
    private JTable resultTable;
    
    public ResultDisplay() {
        setTitle("Election Results");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
        loadResults();
    }
    
    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(StyleConstants.SECONDARY_COLOR);
        
        JLabel titleLabel = new JLabel("Election Results", SwingConstants.CENTER);
        titleLabel.setFont(StyleConstants.SUBTITLE_FONT);
        titleLabel.setForeground(StyleConstants.PRIMARY_COLOR);

        resultTable = new JTable();
        resultTable.setModel(new DefaultTableModel(
            new Object[]{"Candidate", "Party", "Votes Received"}, 0
        ));
        JScrollPane scrollPane = new JScrollPane(resultTable);

        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> dispose());
        
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(backBtn, BorderLayout.SOUTH);
        add(mainPanel);
    }
    
    private void loadResults() {
        DefaultTableModel model = (DefaultTableModel) resultTable.getModel();
        model.setRowCount(0); // Clear existing data
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT c.name, c.party, COUNT(v.vote_id) as votes " +
                         "FROM candidates c LEFT JOIN votes v ON c.candidate_id = v.candidate_id " +
                         "WHERE c.is_approved = true " +
                         "GROUP BY c.candidate_id " +
                         "ORDER BY votes DESC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("name"),
                    rs.getString("party"),
                    rs.getInt("votes")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading results", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}