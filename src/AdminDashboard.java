import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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
        mainPanel.setBackground(StyleConstants.SECONDARY_COLOR);
        
        JLabel titleLabel = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(StyleConstants.SUBTITLE_FONT);
        titleLabel.setForeground(StyleConstants.PRIMARY_COLOR);
        
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 20, 20));
        buttonPanel.setBackground(StyleConstants.SECONDARY_COLOR);
        
        JButton viewResultsBtn = createStyledButton("View Election Results");
        viewResultsBtn.addActionListener(e -> {
            new ResultDisplay().setVisible(true);
        });
        
        JButton viewCandidatesBtn = createStyledButton("Manage Candidates");
        viewCandidatesBtn.addActionListener(e -> {
            new CandidateList().setVisible(true);
        });
        
        JButton viewVotersBtn = createStyledButton("View Voters");
        viewVotersBtn.addActionListener(e -> {
            new VoterList().setVisible(true);
        });
        
        JButton logoutBtn = createStyledButton("Logout");
        logoutBtn.addActionListener(e -> {
            new Dashboard().setVisible(true);
            dispose();
        });
        
        buttonPanel.add(viewResultsBtn);
        buttonPanel.add(viewCandidatesBtn);
        buttonPanel.add(viewVotersBtn);
        buttonPanel.add(logoutBtn);
        
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        add(mainPanel);
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(StyleConstants.BUTTON_FONT);
        button.setBackground(StyleConstants.PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(StyleConstants.COMPONENT_WIDTH, StyleConstants.COMPONENT_HEIGHT));
        return button;
    }
}