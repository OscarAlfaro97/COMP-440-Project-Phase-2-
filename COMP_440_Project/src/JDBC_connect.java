import java.sql.*;

public class JDBC_connect {

    // This method must be added for your GUI to work
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/user_auth", "root", "A9411106a!***");  // your actual password
    }

    public static void main(String[] args) {
        try {
            Connection connection = getConnection();  // uses the method above
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM users");

            while (resultSet.next()) {
                System.out.println(resultSet.getString("firstname"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    
    public static boolean insertItem(String username, String title, String desc, String category, double price) {
        try (Connection conn = getConnection()) {
            // Check how many items the user has posted today
            String checkSQL = "SELECT COUNT(*) FROM items WHERE username = ? AND DATE(post_date) = CURDATE()";
            PreparedStatement checkStmt = conn.prepareStatement(checkSQL);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);

            System.out.println("DEBUG: Posts today by " + username + " = " + count);

            if (count >= 2) return false;

            // Insert the item
            String insertSQL = "INSERT INTO items (username, title, description, category, price, post_date) VALUES (?, ?, ?, ?, ?, NOW())";
            PreparedStatement insertStmt = conn.prepareStatement(insertSQL);
            insertStmt.setString(1, username);
            insertStmt.setString(2, title);
            insertStmt.setString(3, desc);
            insertStmt.setString(4, category);
            insertStmt.setDouble(5, price);
            insertStmt.executeUpdate();

            return true;
        } 

        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}