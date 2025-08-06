import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ReviewForm extends JFrame {
    private int itemId;
    private String reviewer;
    private String poster;

    public ReviewForm(int itemId, String reviewer, String poster) {
        this.itemId = itemId;
        this.reviewer = reviewer;
        this.poster = poster;

        setTitle("Write a Review");
        setSize(400, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel ratingLabel = new JLabel("Rating:");
        String[] ratings = {"Excellent", "Good", "Fair", "Poor"};
        JComboBox<String> ratingDropdown = new JComboBox<>(ratings);

        JLabel commentLabel = new JLabel("Comment:");
        JTextField commentField = new JTextField();

        formPanel.add(ratingLabel);
        formPanel.add(ratingDropdown);
        formPanel.add(commentLabel);
        formPanel.add(commentField);

        JButton submitButton = new JButton("Submit Review");

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(submitButton);

        add(formPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        submitButton.addActionListener(e -> {
            String rating = (String) ratingDropdown.getSelectedItem();
            String comment = commentField.getText().trim();

            if (comment.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Comment cannot be empty.");
                return;
            }

            if (reviewer.equals(poster)) {
                JOptionPane.showMessageDialog(this, "You cannot review your own item.");
                return;
            }

            try (Connection conn = JDBC_connect.getConnection()) {
                // 1. Check if already reviewed
                PreparedStatement checkStmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM reviews WHERE item_id = ? AND reviewer_username = ?"
                );
                checkStmt.setInt(1, itemId);
                checkStmt.setString(2, reviewer);
                ResultSet checkRs = checkStmt.executeQuery();
                checkRs.next();
                if (checkRs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this, "You have already reviewed this item.");
                    return;
                }

                // 2. Check if already wrote 2 reviews today
                PreparedStatement countStmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM reviews WHERE reviewer_username = ? AND review_date = CURDATE()"
                );
                countStmt.setString(1, reviewer);
                ResultSet countRs = countStmt.executeQuery();
                countRs.next();
                if (countRs.getInt(1) >= 2) {
                    JOptionPane.showMessageDialog(this, "You can only write 2 reviews per day.");
                    return;
                }

                // 3. Insert review
                PreparedStatement insertStmt = conn.prepareStatement(
                    "INSERT INTO reviews (item_id, reviewer_username, rating, comment) VALUES (?, ?, ?, ?)"
                );
                insertStmt.setInt(1, itemId);
                insertStmt.setString(2, reviewer);
                insertStmt.setString(3, rating);
                insertStmt.setString(4, comment);
                insertStmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Review submitted!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();

            } catch (SQLException ex1) {
                ex1.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database error.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        setVisible(true);
    }
}
