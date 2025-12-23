package com.shahd.hospitalmanagementsystem;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.StringJoiner;

import org.junit.jupiter.api.Test;

public class ImportSqlTest {

  @Test
  public void importSqlFileIntoDb() throws Exception {
    File f = new File("C:\\Users\\Lenovo\\Downloads\\Hospital.sql");
    assertTrue(f.exists(), "SQL file must exist at the expected path: " + f.getAbsolutePath());

    try (Connection conn = DatabaseConnection.getConnection()) {
      assertNotNull(conn, "Database connection should not be null");
      try (BufferedReader r = new BufferedReader(new FileReader(f))) {
        String line;
        StringJoiner sb = new StringJoiner("\n");
        while ((line = r.readLine()) != null) {
          line = line.trim();
          // Skip comment lines
          if (line.startsWith("--") || line.startsWith("#") || line.isEmpty()) continue;
          sb.add(line);
        }

        // Split on semicolons - naive but works for typical schema dumps
        String[] statements = sb.toString().split(";\s*(?=([^']*'[^']*')*[^']*$)");
        try (Statement stmt = conn.createStatement()) {
          for (String raw : statements) {
            String sql = raw.trim();
            if (sql.isEmpty()) continue;
            try {
              stmt.execute(sql);
            } catch (Exception ex) {
              // Log and continue - many dumps include 'DROP' or 'USE' that may fail safely
              System.err.println("Failed to execute statement (continuing): " + ex.getMessage());
            }
          }
        }
      }
    }
  }
}