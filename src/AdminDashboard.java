import java.awt.*;
import javax.swing.*;

public class AdminDashboard extends JFrame {
    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }
    
    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 240, 240));
        
        JLabel titleLabel = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 102, 204));
        
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 20, 20));
        buttonPanel.setBackground(new Color(240, 240, 240));
        
        JButton manageVotersBtn = createStyledButton("Manage Voters");
        manageVotersBtn.addActionListener(e -> {
            new AdminVoterManagement().setVisible(true);
            dispose();
        });
        
        JButton manageCandidatesBtn = createStyledButton("Manage Candidates");
        manageCandidatesBtn.addActionListener(e -> {
            new AdminCandidateManagement().setVisible(true);
            dispose();
        });
        
        JButton viewResultsBtn = createStyledButton("View Election Results");
        viewResultsBtn.addActionListener(e -> {
            new ResultDisplay().setVisible(true);
        });
        
        JButton logoutBtn = createStyledButton("Logout");
        logoutBtn.addActionListener(e -> {
            new Dashboard().setVisible(true);
            dispose();
        });
        
        buttonPanel.add(manageVotersBtn);
        buttonPanel.add(manageCandidatesBtn);
        buttonPanel.add(viewResultsBtn);
        buttonPanel.add(logoutBtn);
        
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        add(mainPanel);
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(0, 102, 204));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(300, 35));
        return button;
    }
}