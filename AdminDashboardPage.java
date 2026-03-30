import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AdminDashboardPage {

    private JFrame adminFrame;
    private Connection connection;
    private String currentUser;

    public AdminDashboardPage(Connection connection, String username) {
        this.connection = connection;
        this.currentUser = username;
        initializeAdminDashboard();
    }

    private void initializeAdminDashboard() {
        applyTheme();

        adminFrame = new JFrame("Pharmacy Management System - Admin Dashboard");
        adminFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        adminFrame.setSize(850, 550);
        adminFrame.setLocationRelativeTo(null);
        adminFrame.setLayout(new BorderLayout());

        setupHeaderRegion();
        setupMainNavigation();
        setupFooterRegion();

        adminFrame.setVisible(true);
    }

    private void applyTheme() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ignored) {}
    }

    private void setupHeaderRegion() {
        JTextField headerTitle = new JTextField("Admin Dashboard - Manage Users / Suppliers / Reports");
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerTitle.setForeground(new Color(51, 102, 153));
        headerTitle.setEditable(false);
        headerTitle.setBorder(null);
        headerTitle.setOpaque(false);
        headerTitle.setHorizontalAlignment(JTextField.CENTER);
        
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        headerPanel.add(headerTitle);
        adminFrame.add(headerPanel, BorderLayout.NORTH);
    }

    private void setupMainNavigation() {
        JTabbedPane navigationTabs = new JTabbedPane();
        navigationTabs.addTab("Users", createUserManagementPanel());
        navigationTabs.addTab("Suppliers", createSupplierManagementPanel());
        // Note: Ensure ReportPanel.java is in your project folder
        navigationTabs.addTab("Reports", new ReportPanel(adminFrame, connection).createReportsPanel());

        adminFrame.add(navigationTabs, BorderLayout.CENTER);
    }

    private void setupFooterRegion() {
        JButton logoutBtn = createStyledButton("Secure Log Out", e -> {
            adminFrame.dispose();
            new LoginPage();
        });

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        footerPanel.add(logoutBtn);
        adminFrame.add(footerPanel, BorderLayout.SOUTH);
    }
    // All Users table (Add / Remove / View / Update)

    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));

        toolbar.add(createStyledButton("Add User", e -> showAddUserDialog()));
        toolbar.add(createStyledButton("View Users", e -> viewUsers()));
        toolbar.add(createStyledButton("Update User", e -> showUpdateUserDialog()));
        toolbar.add(createStyledButton("Remove User", e -> showRemoveUserDialog()));

        panel.add(toolbar, BorderLayout.NORTH);
        return panel;
    }

    // Supplier Management Panel 
    private JPanel createSupplierManagementPanel() {
        return new JPanel(); 
    }

    private JButton createStyledButton(String text, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.addActionListener(action);
        return btn;
    }

    private void showAddUserDialog() {
        JPanel p = new JPanel(new GridLayout(4,2,8,8));
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"Admin","Cashier"});
        JTextField fullNameField = new JTextField();

        p.add(new JLabel("Username:")); p.add(usernameField);
        p.add(new JLabel("Password:")); p.add(passwordField);
        p.add(new JLabel("Role:")); p.add(roleBox);
        p.add(new JLabel("Full name:")); p.add(fullNameField);

        int res = JOptionPane.showConfirmDialog(adminFrame, p, "Add User", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            addUser(usernameField.getText().trim(), new String(passwordField.getPassword()), (String) roleBox.getSelectedItem(), fullNameField.getText().trim());
        }
    }

    private void showRemoveUserDialog() {
        String username = JOptionPane.showInputDialog(adminFrame, "Enter username to remove:");
        if (username != null && !username.trim().isEmpty()) {
            removeUser(username.trim());
        }
    }

    private void showUpdateUserDialog() {
        // Placeholder for your update logic
        JOptionPane.showMessageDialog(adminFrame, "Update Dialog Logic Goes Here");
    }

    // Users Date Base Admin operations

    private void addUser(String u, String p, String r, String f) {
        String sql = "INSERT INTO users (username, password, role, full_name) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, u); pst.setString(2, p); pst.setString(3, r); pst.setString(4, f);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(adminFrame, "User Added!");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void removeUser(String username) {
        String sql = "DELETE FROM users WHERE username = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, username);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(adminFrame, "User Removed.");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void viewUsers() {
        JOptionPane.showMessageDialog(adminFrame, "Viewing users logic goes here.");
    }
}
