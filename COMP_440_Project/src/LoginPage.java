import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginPage extends JFrame {
    JTextField usernameField;
    JPasswordField passwordField;
    JButton loginButton;
    JButton showPasswordBtn;
    JLabel registerLink;
    boolean isPasswordVisible = false;

    public LoginPage() {
        setTitle("Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        card.setBackground(Color.WHITE);

        JLabel title = new JLabel("Welcome Back!");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        loginButton = new JButton("Login");
        showPasswordBtn = new JButton("Show");

        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        passwordPanel.add(showPasswordBtn, BorderLayout.EAST);

        styleField(usernameField);
        styleField(passwordField);
        styleButton(loginButton);

        card.add(title);
        card.add(labeled("Username", usernameField));
        card.add(Box.createVerticalStrut(10));
        card.add(labeled("Password", passwordPanel));
        card.add(Box.createVerticalStrut(10));
        card.add(loginButton);

        registerLink = new JLabel("<html><u>Don't have an account? Register</u></html>");
        registerLink.setForeground(Color.BLUE.darker());
        registerLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerLink.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        card.add(registerLink);

        setLayout(new BorderLayout());
        add(card, BorderLayout.CENTER);
        getContentPane().setBackground(new Color(240, 240, 240));

        showPasswordBtn.addActionListener(e -> {
            isPasswordVisible = !isPasswordVisible;
            passwordField.setEchoChar(isPasswordVisible ? (char) 0 : '.');
            showPasswordBtn.setText(isPasswordVisible ? "Hide" : "Show");
        });

        loginButton.addActionListener(e -> login());

        registerLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dispose();
                new RegisterPage();
            }
        });

        setVisible(true);
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Information required. Please fill out all fields.");
            return;
        }

        try (Connection conn = JDBC_connect.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String firstName = rs.getString("firstName");

                Session.currentUsername = username;
                
                dispose();

                new DashboardPage(username, firstName); //added username with firstName
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void styleField(JTextField field) {
        field.setMaximumSize(new Dimension(300, 30));
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
    }

    private void styleButton(JButton btn) {
        btn.setBackground(new Color(59, 89, 182));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Tahoma", Font.BOLD, 14));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private JPanel labeled(String labelText, JComponent field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(5, 5));
        panel.setBackground(Color.white);
        JLabel label = new JLabel(labelText);
        label.setForeground(Color.BLACK);
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    public static void main(String[] args) {
        new LoginPage();
    }
}