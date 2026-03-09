import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.shahd.hospitalmanagementsystem.DatabaseConnection;

public class FindPatient {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                System.out.println("Failed to connect to database");
                return;
            }

            // Find patient named menna
            String query = "SELECT * FROM patients WHERE first_name LIKE ? OR last_name LIKE ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, "%menna%");
                pstmt.setString(2, "%menna%");

                try (ResultSet rs = pstmt.executeQuery()) {
                    System.out.println("Searching for patient 'menna':");
                    boolean found = false;
                    while (rs.next()) {
                        found = true;
                        String currentId = rs.getString("patient_id");
                        System.out.println("Found patient:");
                        System.out.println("  ID: " + currentId);
                        System.out.println("  First Name: " + rs.getString("first_name"));
                        System.out.println("  Last Name: " + rs.getString("last_name"));
                        System.out.println("  Date of Birth: " + rs.getString("date_of_birth"));
                        System.out.println("  Phone: " + rs.getString("phone"));
                        System.out.println("  Email: " + rs.getString("email"));
                        System.out.println("  Gender: " + rs.getString("gender"));
                        System.out.println("  Insurance: " + rs.getString("insurance_provider"));
                        System.out.println("  Emergency Contact: " + rs.getString("emergency_contact_name"));
                        System.out.println("  Active: " + rs.getBoolean("is_active"));
                        System.out.println("---");

                        // If no ID, generate one and update
                        if (currentId == null || currentId.trim().isEmpty()) {
                            // Generate next patient ID
                            String newId = generateNextPatientId(conn);
                            System.out.println("Assigning new ID: " + newId);

                            // Update the patient record
                            String updateQuery = "UPDATE patients SET patient_id = ? WHERE first_name = ? AND last_name = ?";
                            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                                updateStmt.setString(1, newId);
                                updateStmt.setString(2, rs.getString("first_name"));
                                updateStmt.setString(3, rs.getString("last_name"));
                                int rowsAffected = updateStmt.executeUpdate();
                                if (rowsAffected > 0) {
                                    System.out.println("Successfully updated patient ID to: " + newId);
                                } else {
                                    System.out.println("Failed to update patient ID");
                                }
                            }
                        } else {
                            System.out.println("Patient already has ID: " + currentId);
                        }
                    }
                    if (!found) {
                        System.out.println("No patient named 'menna' found.");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String generateNextPatientId(Connection conn) throws SQLException {
        String query = "SELECT patient_id FROM patients WHERE patient_id IS NOT NULL AND patient_id != '' ORDER BY CAST(SUBSTRING(patient_id, 2) AS UNSIGNED) DESC LIMIT 1";
        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                String lastId = rs.getString("patient_id");
                if (lastId.startsWith("P")) {
                    try {
                        int num = Integer.parseInt(lastId.substring(1));
                        return "P" + (num + 1);
                    } catch (NumberFormatException e) {
                        return "P1";
                    }
                }
            }
        }
        return "P1";
    }
}