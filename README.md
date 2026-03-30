Welcome to the Pharmacy Information Management System (PIMS). 
This desktop application is designed to streamline pharmacy operations, including inventory management and business reporting.

📂 Project Structure
AbelNaidoo_pims.exe: A executable bundled with the required database drivers for immediate testing.
/src: Contains all Java source files (.java)
/screenshots: Visual documentation of the system interfaces.
database.sql: The complete schema script. Includes CREATE statements and INSERT commands for:
- Login (Admin / Cashier)
- Admin dashboard (manage users)
- Cashier (pharmacist) dashboard (manage medicines, generate bill)
- Basic reporting panel (low stock, expiry, recent sales)

Default database & credentials
- Database name: healthfirst
- Default users in database.sql:
- Admin: username = admin, password = password
- Cashier: username = cashier, password = password

Prerequisites
- Java JDK 8 or later installed
- MySQL server running 
- MySQL JDBC driver (mysql-connector-java X.X.jar)
- FlatLaf look-and-feel JAR (flatlaf-X.X.jar) on the classpath
- Command-line access or MySQL Workbench to run SQL script

Install the database
1. Start your MySQL server.
2. Open a terminal / command prompt or MySQL Workbench.
3. Run the SQL script included: database.sql
4. Confirm the database and tables exist:
- Database: healthfirst
- Tables: users, suppliers, medicines, sales, sale_items

Configure DB credentials in the Java app
- Open src/LoginPage.java
- Update these constants near the top with your MySQL credentials:
    private static final String DB_URL  = "jdbc:mysql://localhost:3306/healthfirst";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Add_your_mysql_password_here";
- If you keep MySQL defaults, DB_USER may be "root" and DB_PASS your root password.

Compile and run the application
1. Make sure the MySQL JDBC JAR and FlatLaf JAR are available before running the program.

2. Running the Application
Simply double-click AbelNaidoo_pims.exe.
Note: Ensure your MySQL service is active on the default port (3306) for the application to establish a connection.



