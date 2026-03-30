import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ReportPanel {

    private final JFrame mainStage;
    private final Connection connection;

    public ReportPanel(JFrame mainStage, Connection connection) {
        this.mainStage = mainStage;
        this.connection = connection;
    }

    public JPanel createReportsPanel() {
        JPanel container = new JPanel(new BorderLayout(12, 12));
        container.setBackground(Color.WHITE);

        JLabel titleView = new JLabel("HealthFirst Pharmacy - Business Insights", SwingConstants.CENTER);
        titleView.setFont(new Font("Verdana", Font.BOLD, 22));
        titleView.setForeground(new Color(45, 95, 140));
        container.add(titleView, BorderLayout.NORTH);

        JPanel gridBody = new JPanel(new GridLayout(4, 1, 12, 12));
        gridBody.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        gridBody.add(initRevenueModule());
        gridBody.add(initStockWarningModule());
        gridBody.add(initSalesHistoryModule());
        gridBody.add(initExpirationModule());

        container.add(gridBody, BorderLayout.CENTER);
        return container;
    }

    private JPanel initRevenueModule() {
        JPanel card = new JPanel(new FlowLayout(FlowLayout.LEADING));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        card.setBackground(new Color(235, 245, 255));

        // FIXED: Aliases renamed to match Java getters below
        String sql = "SELECT SUM(total_amount) AS revenue_sum, COUNT(1) AS transaction_count FROM sales";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet results = stmt.executeQuery()) {
            if (results.next()) {
                int rowCount = results.getInt("transaction_count");
                double revenueSum = results.getDouble("revenue_sum");
                String infoText = String.format("<html><b>Earnings Total:</b> $%.2f<br><b>Volume:</b> %d orders</html>", revenueSum, rowCount);
                
                JLabel display = new JLabel(infoText);
                display.setFont(new Font("SansSerif", Font.PLAIN, 17));
                display.setForeground(new Color(40, 80, 120));
                card.add(display);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            card.add(new JLabel("Revenue data unavailable: " + ex.getMessage()));
        }
        return card;
    }

    private JPanel initStockWarningModule() {
        JPanel card = new JPanel(new FlowLayout(FlowLayout.LEFT));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(240, 80, 80)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        card.setBackground(new Color(255, 245, 245));

        JLabel head = new JLabel("⚡ CRITICAL STOCK LEVELS", SwingConstants.LEFT);
        head.setFont(new Font("Tahoma", Font.BOLD, 16));
        head.setForeground(new Color(180, 0, 0));

        JTextArea logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setPreferredSize(new Dimension(580, 90));
        logArea.setLineWrap(true);

        String sql = "SELECT name, quantity_in_stock FROM medicines WHERE quantity_in_stock < 10 ORDER BY quantity_in_stock ASC";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet results = stmt.executeQuery()) {
            boolean noData = true;
            while (results.next()) {
                noData = false;
                logArea.append(results.getString("name") + " (Qty: " + results.getInt("quantity_in_stock") + ")\n");
            }
            if (noData) {
                logArea.setText("Inventory levels are currently optimal.");
                logArea.setForeground(new Color(0, 120, 0));
            } else {
                logArea.setForeground(Color.RED.darker());
            }
        } catch (SQLException ex) {
            logArea.setText("Database error: " + ex.getMessage());
        }

        card.add(head);
        card.add(new JScrollPane(logArea));
        return card;
    }

    private JPanel initSalesHistoryModule() {
        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190, 190, 190)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        card.setBackground(new Color(245, 255, 245));

        JLabel title = new JLabel("📝 Latest Activity", SwingConstants.CENTER);
        title.setFont(new Font("Tahoma", Font.BOLD, 17));
        title.setForeground(new Color(30, 90, 30));

        String[] headers = {"Timestamp", "Staff", "Amount Paid"};
        DefaultTableModel tableData = new DefaultTableModel(headers, 0);

        JTable activityTable = new JTable(tableData);
        JScrollPane scrollBox = new JScrollPane(activityTable);
        scrollBox.setPreferredSize(new Dimension(580, 180));

        // FIXED: Query structure and column mapping
        String sql = "SELECT DATE_FORMAT(s.sale_date, '%d/%m/%Y %H:%i') AS timestamp, " +
                     "COALESCE(u.username, 'N/A') AS staff_name, " +
                     "s.total_amount AS subtotal " +
                     "FROM sales s " +
                     "LEFT JOIN users u ON u.user_id = s.user_id " +
                     "ORDER BY s.sale_date DESC LIMIT 10";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet results = stmt.executeQuery()) {
            while (results.next()) {
                tableData.addRow(new Object[]{
                        results.getString("timestamp"),
                        results.getString("staff_name"),
                        String.format("$%.2f", results.getDouble("subtotal"))
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        card.add(title, BorderLayout.NORTH);
        card.add(scrollBox, BorderLayout.CENTER);
        return card;
    }

    private JPanel initExpirationModule() {
        JPanel card = new JPanel(new FlowLayout(FlowLayout.LEFT));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 140, 0)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        card.setBackground(new Color(255, 253, 230));

        JLabel head = new JLabel("📅 UPCOMING EXPIRATIONS", SwingConstants.LEFT);
        head.setFont(new Font("Tahoma", Font.BOLD, 16));
        head.setForeground(new Color(210, 105, 30));

        JTextArea expArea = new JTextArea();
        expArea.setEditable(false);
        expArea.setPreferredSize(new Dimension(570, 85));
        expArea.setWrapStyleWord(true);

        // FIXED: Variable was 'query', changed to 'sql' to match prepareStatement call
        String sql = "SELECT m.name AS med_title, m.expiry_date AS exp_day " + 
                     "FROM medicines m " + 
                     "WHERE m.expiry_date >= CURDATE() " +
                     "AND m.expiry_date <= (CURDATE() + INTERVAL 1 MONTH) " + 
                     "ORDER BY exp_day ASC";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet results = stmt.executeQuery()) {
            boolean isClear = true;
            while (results.next()) {
                isClear = false;
                // FIXED: results.getString aliases to match the SQL above
                expArea.append(results.getString("med_title") + " — Exp: " + results.getString("exp_day") + "\n");
            }
            if (isClear) {
                expArea.setText("No stock expiring within the next 30 days.");
                expArea.setForeground(new Color(0, 100, 0));
            } else {
                expArea.setForeground(new Color(200, 70, 0));
            }
        } catch (SQLException ex) {
            expArea.setText("Query failed: " + ex.getMessage());
        }

        card.add(head);
        card.add(new JScrollPane(expArea));
        return card;
    }
}
