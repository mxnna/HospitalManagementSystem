import java.sql.*;

public class test_db {
    public static void main(String[] args) {
        String url = "jdbc:mysql://127.0.0.1:3307/hospital_management";
        String user = "root";
        String password = "#Hkhmhm2005";
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("✓ Connected to database successfully!");
            
            // Check patients table
            String patientQuery = "SELECT COUNT(*) as count FROM patients";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(patientQuery)) {
                if (rs.next()) {
                    System.out.println("✓ Patients table: " + rs.getInt("count") + " records");
                }
            }
            
            // Check appointments table
            String appointmentQuery = "SELECT COUNT(*) as count FROM appointments";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(appointmentQuery)) {
                if (rs.next()) {
                    System.out.println("✓ Appointments table: " + rs.getInt("count") + " records");
                }
            }
            
            // List patient columns
            String columnQuery = "SELECT * FROM patients LIMIT 1";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(columnQuery)) {
                ResultSetMetaData meta = rs.getMetaData();
                System.out.println("\n✓ Patient table columns:");
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    System.out.println("  - " + meta.getColumnName(i));
                }
            }
            
            // List appointment columns
            String apptColumnQuery = "SELECT * FROM appointments LIMIT 1";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(apptColumnQuery)) {
                ResultSetMetaData meta = rs.getMetaData();
                System.out.println("\n✓ Appointment table columns:");
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    System.out.println("  - " + meta.getColumnName(i));
                }
            }
            
            conn.close();
        } catch (ClassNotFoundException e) {
            System.err.println("✗ MySQL Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("✗ Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
