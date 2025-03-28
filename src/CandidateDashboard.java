import java.awt.*;
import javax.swing.*;

public class CandidateDashboard extends JFrame {
    private final int candidateId;
    private final String candidateName;
    
    public CandidateDashboard(int candidateId, String candidateName) {
        this.candidateId = candidateId;
        this.candidateName = candidateName;
        setTitle("Candidate Dashboard - Welcome " + candidateName);
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }
    
    private void initUI() {
        // Create style constants if StyleConstants class is missing
        Color SECONDARY_COLOR = new Color(240, 240, 240);
        Color PRIMARY_COLOR = new Color(0, 102, 204);
        Font SUBTITLE_FONT = new Font("Arial", Font.BOLD, 18);
        Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);
        int COMPONENT_WIDTH = 300;
        int COMPONENT_HEIGHT = 35;
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(SECONDARY_COLOR);
        
        JLabel titleLabel = new JLabel("Welcome, " + candidateName, SwingConstants.CENTER);
        titleLabel.setFont(SUBTITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 20, 20));
        buttonPanel.setBackground(SECONDARY_COLOR);
        
        JButton viewResultsBtn = createStyledButton("View Election Results", 
            BUTTON_FONT, PRIMARY_COLOR, COMPONENT_WIDTH, COMPONENT_HEIGHT);
        viewResultsBtn.addActionListener(e -> {
            new ResultDisplay().setVisible(true);
        });
        
        JButton logoutBtn = createStyledButton("Logout", 
            BUTTON_FONT, PRIMARY_COLOR, COMPONENT_WIDTH, COMPONENT_HEIGHT);
        logoutBtn.addActionListener(e -> {
            new Dashboard().setVisible(true);
            dispose();
        });
        
        buttonPanel.add(viewResultsBtn);
        buttonPanel.add(logoutBtn);
        
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        add(mainPanel);
    }
    
    private JButton createStyledButton(String text, Font font, Color bgColor, int width, int height) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(width, height));
        return button;
    }
}