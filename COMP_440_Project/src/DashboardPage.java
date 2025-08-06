import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class DashboardPage extends JFrame {
    public String currentUser;

    public DashboardPage(String username, String firstName) {
        this.currentUser = username;

        setTitle("Dashboard");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel welcomeLabel = new JLabel("Welcome to your dashboard, " + firstName + "!");
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        add(welcomeLabel, BorderLayout.CENTER);
        getContentPane().setBackground(new Color(240, 240, 240));


        JButton postItemButton = new JButton("Post New Item");
        postItemButton.addActionListener(e -> {
            dispose(); 
            new PostItemPage(currentUser, firstName);
        });


        JButton searchItemsButton = new JButton("Search Items");
        searchItemsButton.addActionListener(e -> {
            dispose(); 
            new SearchItemPage(currentUser,firstName);
        });


        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginPage();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(postItemButton);
        buttonPanel.add(searchItemsButton);
        buttonPanel.add(logoutButton);

        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
} 
