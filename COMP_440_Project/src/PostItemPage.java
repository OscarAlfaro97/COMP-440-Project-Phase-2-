import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class PostItemPage extends JFrame {
    private JTextField titleField, categoryField, priceField;
    private JTextArea descriptionArea;
    private JButton postButton;
    private JLabel statusLabel;
    private String currentUser, firstName;

    public PostItemPage(String username, String firstName) {
        super("Post New Item");
        this.currentUser = username;
        this.firstName = firstName;

        // Form Panel with Labels and Inputs
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        formPanel.add(new JLabel("Title:"));
        titleField = new JTextField();
        formPanel.add(titleField);

        formPanel.add(new JLabel("Description:"));
        descriptionArea = new JTextArea(2, 20);
        formPanel.add(new JScrollPane(descriptionArea));

        formPanel.add(new JLabel("Category:"));
        categoryField = new JTextField(20);
        formPanel.add(categoryField);

        formPanel.add(new JLabel("Price:"));
        priceField = new JTextField(20);
        formPanel.add(priceField);

        /*formPanel.add(new JLabel("Status:"));
        statusLabel = new JLabel(" ");
        formPanel.add(statusLabel);*/

        // Buttons
        postButton = new JButton("Post Item");
        postButton.addActionListener(e -> postItem());

        JButton dashboardButton = new JButton("Return Home");
        dashboardButton.addActionListener(e -> {
            dispose();
            new DashboardPage(currentUser, firstName);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(postButton);
        buttonPanel.add(dashboardButton);

        // Add to frame
        setLayout(new BorderLayout());
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void postItem() {
        String title = titleField.getText().trim();
        String desc = descriptionArea.getText().trim();
        String category = categoryField.getText().trim();
        String priceText = priceField.getText().trim();

        if (title.isEmpty() || desc.isEmpty() || category.isEmpty() || priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
            //statusLabel.setText("All fields are required.");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);

            boolean success = JDBC_connect.insertItem(currentUser, title, desc, category, price);

            if (success) {
                JOptionPane.showMessageDialog(this, "Item posted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
                new DashboardPage(currentUser, firstName);  // ← Return to dashboard
            } else {
                JOptionPane.showMessageDialog(this, "You’ve already posted 2 items today. Try again tomorrow.", "Error", JOptionPane.ERROR_MESSAGE);
                this.dispose();
                new DashboardPage(currentUser, firstName);  // ← Return to dashboard
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid price format.", "Error", JOptionPane.ERROR_MESSAGE);
            //statusLabel.setText("Invalid price format.");
        }
    }
}
