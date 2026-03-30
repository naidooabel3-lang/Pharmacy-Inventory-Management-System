import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class PharmacistDashboardPage {

    private JFrame pharmacistFrame;
    private final Connection connection;
    private final String currentUser;

    private static class CartEntry {
        final int medId;
        final String title;
        final double unitPrice;
        final int amount;

        CartEntry(int id, String name, double price, int qty) {
            this.medId = id;
            this.title = name;
            this.unitPrice = price;
            this.amount = qty;
        }
    }

    private final ArrayList<CartEntry> basket = new ArrayList<>();
    private final DefaultTableModel cartModel = new DefaultTableModel(new String[]{"Item", "Price", "Qty", "Total"}, 0);
    private final JLabel totalLabel = new JLabel("Total: 0.00");

    public PharmacistDashboardPage(Connection connection, String username) {
        this.connection = connection;
        this.currentUser = username;
        displayDashboard();
    }

    private void displayDashboard() {
        try { UIManager.setLookAndFeel(new FlatLightLaf()); } catch (Exception ignored) {}

        pharmacistFrame = new JFrame("Pharmacy Management - Pharmacist Console");
        pharmacistFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pharmacistFrame.setSize(1000, 650);
        pharmacistFrame.setLocationRelativeTo(null);

        JPanel mainLayout = new JPanel(new BorderLayout());
        mainLayout.setBackground(new Color(38, 38, 38));
        
        JLabel title = new JLabel("Pharmacist Panel - Inventory Control", 0);
        title.setFont(new Font("Segoe UI", 1, 28));
        title.setForeground(new Color(20, 110, 160));
        title.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        mainLayout.add(title, "North");

        JSplitPane splitView = new JSplitPane(1, createPosPanel(), createManageMedicinesMainPanel());
        splitView.setDividerLocation(520);
        splitView.setBorder(null);
        mainLayout.add(splitView, "Center");

        JButton exitBtn = createButton("Log Out", e -> {
            pharmacistFrame.dispose();
            new LoginPage(); 
        });
        
        JPanel footer = new JPanel();
        footer.setOpaque(false);
        footer.add(exitBtn);
        mainLayout.add(footer, "South");

        pharmacistFrame.setContentPane(mainLayout);
        pharmacistFrame.setVisible(true);
    }

    private JPanel createPosPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        p.setBackground(Color.WHITE);

        JTextField search = new JTextField(25), qtyInput = new JTextField("1", 4);
        JPanel inputRow = new JPanel(new FlowLayout(0));
        inputRow.setOpaque(false);
        
        inputRow.add(new JLabel("Product:")); inputRow.add(search);
        inputRow.add(new JLabel("Qty:")); inputRow.add(qtyInput);
        inputRow.add(createButton("Add", e -> processCartAddition(search.getText().trim(), qtyInput.getText().trim())));

        p.add(inputRow, "North");
        p.add(new JScrollPane(new JTable(cartModel)), "Center");

        JPanel sideActions = new JPanel(new GridLayout(4, 1, 8, 8));
        sideActions.setBackground(Color.WHITE);
        totalLabel.setFont(new Font("Segoe UI", 1, 19));
        totalLabel.setForeground(new Color(40, 90, 140));

        sideActions.add(totalLabel);
        sideActions.add(createButton("Remove Item", e -> removeSelectedItem()));
        sideActions.add(createButton("Empty Cart", e -> clearAllItems()));
        sideActions.add(createButton("Checkout", e -> checkoutProcess()));

        p.add(sideActions, "East");
        return p;
    }

    private void processCartAddition(String name, String qtyStr) {
        try {
            int qty = Integer.parseInt(qtyStr);
            if (qty <= 0 || name.isEmpty()) throw new Exception();

            String sql = "SELECT medicine_id, price, quantity_in_stock FROM medicines WHERE name = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, name);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    int id = rs.getInt(1), stock = rs.getInt(3);
                    double price = rs.getDouble(2);
                    if (qty > stock) {
                        JOptionPane.showMessageDialog(pharmacistFrame, "Stock insufficient: " + stock);
                        return;
                    }
                    basket.add(new CartEntry(id, name, price, qty));
                    cartModel.addRow(new Object[]{name, price, qty, String.format("%.2f", price * qty)});
                    syncTotal();
                } else JOptionPane.showMessageDialog(pharmacistFrame, "Not found.");
            }
        } catch (Exception ex) { JOptionPane.showMessageDialog(pharmacistFrame, "Invalid Input"); }
    }

    private void syncTotal() {
        double sum = 0;
        for (CartEntry item : basket) {
            sum += item.unitPrice * item.amount;
        }
        totalLabel.setText(String.format("Total: %.2f", sum));
    }

    private void removeSelectedItem() {
    }

    private void clearAllItems() {
        basket.clear();
        cartModel.setRowCount(0);
        syncTotal();
    }

    private JButton createButton(String text, java.awt.event.ActionListener action) {
        JButton b = new JButton(text);
        b.addActionListener(action);
        return b;
    }

    private JPanel createManageMedicinesMainPanel() { return new JPanel(); }
    private void checkoutProcess() { /* Your checkout logic */ }
}
