import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginPage {

    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private Connection connection;
    private static final String DB_URL = "ADD HERE";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "ADD HERE";

    public LoginPage() {
        initializeDatabase();
        initializeLogin();
    }

    private void initializeDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            System.out.println("Database connected successfully.");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        JOptionPane.showMessageDialog(null, "DB Connection Failed: " + e.getLocalizedMessage(), "Error", 0);
        }
    }

    private void initializeLogin() {
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
        } catch (Exception ignored) {}

        frame = new JFrame("Pharmacy Management System - Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 420);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("Login to Pharmacy System", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 123, 255));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel formPanel = new JPanel(new GridLayout(2,2,10,10));
        formPanel.setBackground(Color.WHITE);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 18));

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 18));

        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);

        mainPanel.add(formPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);

        JButton loginButton = createStyledButton("Login", e -> {
            String username = usernameField.getText().trim();
            String password = String.valueOf(passwordField.getPassword());

            String role = authenticateUser(username, password);
            if (role != null) {
                JOptionPane.showMessageDialog(frame, "Login Successful");
                frame.dispose();
                if (role.equals("Admin")) {
                    new AdminDashboardPage(connection, username);
                } else if (role.equals("Cashier")) {
                    new PharmacistDashboardPage(connection, username);
                } else {
                    JOptionPane.showMessageDialog(null, "Unknown role: " + role, "Role Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid Username or Password", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton signUpButton = createStyledButton("Sign Up", e -> openSignUpForm());

        buttonPanel.add(loginButton);
        buttonPanel.add(signUpButton);

        mainPanel.add(buttonPanel);
        frame.getContentPane().add(mainPanel);
        frame.setVisible(true);
    }

    private JButton createStyledButton(String title, ActionListener action) {
        JButton button = new JButton(title);
        button.setFont(new Font("Arial", Font.PLAIN, 18));
        button.setBackground(new Color(0, 123, 255));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.addActionListener(action);
        return button;
    }

    private String authenticateUser(String username, String password) {
        if (connection == null) return null;
        String query = "SELECT `role` FROM `users` WHERE `username` = ? AND `password` = ? LIMIT 1";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, username);
            pst.setString(2, password); // Plain text for simplicity
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) return rs.getString("role");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void openSignUpForm() {
        JFrame signUpFrame = new JFrame("Sign Up");
        signUpFrame.setSize(420, 320);
        signUpFrame.setLocationRelativeTo(null);
        signUpFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel signUpPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        signUpPanel.setBackground(Color.WHITE);

        JLabel usernameLabel = new JLabel("Username:");
        JTextField newUsernameField = new JTextField(15);

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField newPasswordField = new JPasswordField(15);

        JLabel roleLabel = new JLabel("Role:");
        JComboBox roleComboBox = new JComboBox(new String[]{"Admin", "Cashier"});

        JButton registerButton = createStyledButton("Register", e -> {
            String newUsername = newUsernameField.getText().trim();
            String newPassword = String.valueOf(newPasswordField.getPassword());
            String role = (String) roleComboBox.getSelectedItem();

        if ("".equals(newUsername) || "".equals(newPassword) || role == null)
 {
                JOptionPane.showMessageDialog(signUpFrame, "Please enter valid data", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                if (registerUser(newUsername, newPassword, role)) {
                    JOptionPane.showMessageDialog(signUpFrame, "Registration Successful");
                    signUpFrame.dispose();
                } else {
                    JOptionPane.showMessageDialog(signUpFrame, "Username already exists or DB error", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });    

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = 17;
        signUpPanel.add(usernameLabel, gbc);
        gbc.gridx = 1; signUpPanel.add(newUsernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; signUpPanel.add(passwordLabel, gbc);
        gbc.gridx = 1; signUpPanel.add(newPasswordField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; signUpPanel.add(roleLabel, gbc);
        gbc.gridx = 1; signUpPanel.add(roleComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.anchor = 10;
        signUpPanel.add(registerButton, gbc);

        signUpFrame.add(signUpPanel);
        signUpFrame.setVisible(true);
    }

    private boolean registerUser(String username, String password, String role) {
        if (connection == null) return false;
        String checkQuery = "SELECT 1 FROM users u WHERE u.username = ? LIMIT 1";
        String insertQuery = "INSERT INTO users (username, password, role, full_name) VALUES (?, ?, ?, ?)";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
            checkStmt.setString(1, username);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        try (PreparedStatement pst = connection.prepareStatement(insertQuery)) {
            pst.setString(1, username);
            pst.setString(2, password);
            pst.setString(3, role);
            pst.setString(4, username);
            pst.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Launch login
    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginPage::new);
    }
}
