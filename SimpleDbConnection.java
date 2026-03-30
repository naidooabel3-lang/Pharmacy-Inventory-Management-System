import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SimpleDbConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/healthfirst";
    private static final String USER = "root";
    private static final String PASS = "Admin123";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASS)) {
            System.out.println("Connected to the database successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}