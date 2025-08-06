import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RegisterPage extends JFrame {
    JTextField usernameField, firstNameField, lastNameField, emailField;
    JPasswordField passwordField, confirmPasswordField;
    JButton registerButton, showPassBtn, showConfirmBtn;
    boolean isPassVisible = false;
    boolean isConfirmVisible = false;

    public RegisterPage() {
        setTitle("Register");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        card.setBackground(Color.WHITE);

        JLabel title = new JLabel("Create Account");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        confirmPasswordField = new JPasswordField(15);
        firstNameField = new JTextField(15);
        lastNameField = new JTextField(15);
        emailField = new JTextField(15);
        registerButton = new JButton("Register");

        showPassBtn = new JButton("Show");
        showConfirmBtn = new JButton("Show");

        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        passwordPanel.add(showPassBtn, BorderLayout.EAST);

        JPanel confirmPanel = new JPanel(new BorderLayout());
        confirmPanel.add(confirmPasswordField, BorderLayout.CENTER);
        confirmPanel.add(showConfirmBtn, BorderLayout.EAST);

        styleField(usernameField);
        styleField(passwordField);
        styleField(confirmPasswordField);
        styleField(firstNameField);
        styleField(lastNameField);
        styleField(emailField);
        styleButton(registerButton);

        card.add(title);
        card.add(labeled("Username", usernameField));
        card.add(Box.createVerticalStrut(10));
        card.add(labeled("Password", passwordPanel));
        card.add(Box.createVerticalStrut(10));
        card.add(labeled("Confirm Password", confirmPanel));
        card.add(Box.createVerticalStrut(10));
        card.add(labeled("First Name", firstNameField));
        card.add(Box.createVerticalStrut(10));
        card.add(labeled("Last Name", lastNameField));
        card.add(Box.createVerticalStrut(10));
        card.add(labeled("Email", emailField));
        card.add(Box.createVerticalStrut(15));
        card.add(registerButton);

        JLabel loginLink = new JLabel("<html><u>Already have an account? Login</u></html>");
        loginLink.setForeground(Color.BLUE.darker());
        loginLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginLink.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        card.add(loginLink);

        setLayout(new BorderLayout());
        add(card, BorderLayout.CENTER);
        getContentPane().setBackground(new Color(240, 240, 240));

        showPassBtn.addActionListener(e -> {
            isPassVisible = !isPassVisible;
            passwordField.setEchoChar(isPassVisible ? (char) 0 : '*');
            showPassBtn.setText(isPassVisible ? "Hide" : "Show");
        });

        showConfirmBtn.addActionListener(e -> {
            isConfirmVisible = !isConfirmVisible;
            confirmPasswordField.setEchoChar(isConfirmVisible ? (char) 0 : '*');
            showConfirmBtn.setText(isConfirmVisible ? "Hide" : "Show");
        });

        registerButton.addActionListener(e -> register());

        loginLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dispose();
                new LoginPage();
            }
        });

        setVisible(true);
    }

    private void register() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirm = new String(confirmPasswordField.getPassword()).trim();
        String first = firstNameField.getText().trim();
        String last = lastNameField.getText().trim();
        String email = emailField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty() ||
                first.isEmpty() || last.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠Information required. Please fill out all fields.");
            return;
        }

        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!");
            return;
        }

        try (Connection conn = JDBC_connect.getConnection()) {
            PreparedStatement check = conn.prepareStatement("SELECT * FROM users WHERE username = ? OR email = ?");
            check.setString(1, username);
            check.setString(2, email);
            ResultSet rs = check.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Username or Email already exists!");
                return;
            }

            PreparedStatement insert = conn.prepareStatement(
                    "INSERT INTO users (username, password, firstName, lastName, email) VALUES (?, ?, ?, ?, ?)"
            );
            insert.setString(1, username);
            insert.setString(2, password);
            insert.setString(3, first);
            insert.setString(4, last);
            insert.setString(5, email);
            insert.executeUpdate();

            JOptionPane.showMessageDialog(this, "Registration successful!");
            dispose();
            new LoginPage();
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
        panel.setBackground(Color.WHITE);
        JLabel label = new JLabel(labelText);
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }
}