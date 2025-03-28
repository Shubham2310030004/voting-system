import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame {
    public Dashboard() {
        setTitle("Voting System - Main Dashboard");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }
    
    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(StyleConstants.SECONDARY_COLOR);
        
        JLabel titleLabel = new JLabel("Voting System", SwingConstants.CENTER);
        titleLabel.setFont(StyleConstants.TITLE_FONT);
        titleLabel.setForeground(StyleConstants.PRIMARY_COLOR);
        
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 20, 20));
        buttonPanel.setBackground(StyleConstants.SECONDARY_COLOR);
        
        JButton voterBtn = createStyledButton("Voter Dashboard");
        voterBtn.addActionListener(e -> {
            new VoterLogin().setVisible(true);
            dispose();
        });
        
        JButton candidateBtn = createStyledButton("Candidate Dashboard");
        candidateBtn.addActionListener(e -> {
            new CandidateLogin().setVisible(true);
            dispose();
        });
        
        JButton adminBtn = createStyledButton("Admin Dashboard");
        adminBtn.addActionListener(e -> {
            new AdminLogin().setVisible(true);
            dispose();
        });
        
        buttonPanel.add(voterBtn);
        buttonPanel.add(candidateBtn);
        buttonPanel.add(adminBtn);
        
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