import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class SearchItemPage extends JFrame {
    private JTextField categoryField;
    private JTable resultTable;
    private DefaultTableModel tableModel;

    private String currentUser;

    public SearchItemPage(String currentUser,String firstName) {
        this.currentUser = currentUser;

        setTitle("Search Items by Category");
        setSize(600, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Search form
        JPanel searchPanel = new JPanel(new FlowLayout());
        categoryField = new JTextField(15);
        JButton searchButton = new JButton("Search");

        searchPanel.add(new JLabel("Enter Category:"));
        searchPanel.add(categoryField);
        searchPanel.add(searchButton);
        add(searchPanel, BorderLayout.NORTH);

        // Table for results
        tableModel = new DefaultTableModel(new String[]{"ID", "Title", "Category", "Price", "Username"}, 0);
        resultTable = new JTable(tableModel);

        resultTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {  // Double-click
                    int selectedRow = resultTable.getSelectedRow();
                    if (selectedRow != -1) {
                        int itemId = (int) tableModel.getValueAt(selectedRow, 0);
                        String seller = (String) tableModel.getValueAt(selectedRow, 4);  // poster username
                        new ReviewForm(itemId, currentUser, seller);
                    }
                }
            }
        });

        add(new JScrollPane(resultTable), BorderLayout.CENTER);

        // Action listener
        searchButton.addActionListener(e -> searchByCategory());


        JButton dashboardButton = new JButton("Return Home");
        dashboardButton.addActionListener(e -> {
            dispose();
            new DashboardPage(currentUser, firstName);
        });

        JButton reviewButton = new JButton("Write Review");
        //reviewButton.setEnabled(false); // initially disabled

        reviewButton.addActionListener(e -> {
            int selectedRow = resultTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Please select an item to review.");
                return;
            }

            int itemId = (int) tableModel.getValueAt(selectedRow, 0);
            String seller = (String) tableModel.getValueAt(selectedRow, 4);

            new ReviewForm(itemId, currentUser, seller);
        });

        resultTable.getSelectionModel().addListSelectionListener(e -> {
            reviewButton.setEnabled(resultTable.getSelectedRow() != -1);
        });


        JPanel buttonPanel = new JPanel();
        buttonPanel.add(reviewButton);
        buttonPanel.add(dashboardButton);

        add(buttonPanel, BorderLayout.SOUTH);


        setVisible(true);
    }

    private void searchByCategory() {
        String category = categoryField.getText().trim();
        if (category.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a category.");
            return;
        }

        tableModel.setRowCount(0); // Clear previous results

        try (Connection conn = JDBC_connect.getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT id, title, category, price, username FROM items WHERE category LIKE ?")) {

            stmt.setString(1, "%" + category + "%");  // Match any substring

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String itemCategory = rs.getString("category");
                double price = rs.getDouble("price");
                String username = rs.getString("username");

                tableModel.addRow(new Object[]{id, title, itemCategory, price, username});
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }


}
