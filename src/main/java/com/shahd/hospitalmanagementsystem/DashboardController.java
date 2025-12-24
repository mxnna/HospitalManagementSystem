package com.shahd.hospitalmanagementsystem;

import com.shahd.models.Appointment;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class DashboardController implements Initializable {

    @FXML private Label appointmentsCount;
    @FXML private Label patientsCount;
    @FXML private Label doctorsCount;
    @FXML private Label emergencyCount;

    @FXML private TableView<Appointment> appointmentsTable;
    @FXML private TableColumn<Appointment, String> colApptId;
    @FXML private TableColumn<Appointment, String> colPatient;
    @FXML private TableColumn<Appointment, String> colDoctor;
    @FXML private TableColumn<Appointment, String> colTime;
    @FXML private TableColumn<Appointment, String> colStatus;

    @FXML private TableView<Appointment> emergencyTable;
    @FXML private TableColumn<Appointment, String> colCaseId;
    @FXML private TableColumn<Appointment, String> colCasePatient;
    @FXML private TableColumn<Appointment, String> colReason;
    @FXML private TableColumn<Appointment, String> colCaseTime;
    @FXML private TableColumn<Appointment, String> colPriority;

    private ObservableList<Appointment> apptItems = FXCollections.observableArrayList();
    private ObservableList<Appointment> emergencyItems = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTableColumns();
        loadCounts();
        loadAppointments();
        loadEmergencies();
    }

    private void setupTableColumns() {
        colApptId.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        colPatient.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        colDoctor.setCellValueFactory(new PropertyValueFactory<>("doctorName"));
        colTime.setCellValueFactory(cell -> cell.getValue().appointmentDateTimeProperty());
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        appointmentsTable.setItems(apptItems);

        colCaseId.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        colCasePatient.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        colReason.setCellValueFactory(new PropertyValueFactory<>("reason"));
        colCaseTime.setCellValueFactory(cell -> cell.getValue().appointmentDateTimeProperty());
        colPriority.setCellValueFactory(new PropertyValueFactory<>("status"));

        emergencyTable.setItems(emergencyItems);
    }

    private int tryQueryCount(String sql) {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            // ignore and fallback
        }
        return -1;
    }

    private void loadCounts() {
        int appts = tryQueryCount("SELECT COUNT(*) FROM appointments WHERE DATE(appointment_date) = CURDATE()");
        if (appts < 0) appts = tryQueryCount("SELECT COUNT(*) FROM appointments WHERE DATE(appointment_datetime) = CURDATE()");
        if (appts < 0) appts = tryQueryCount("SELECT COUNT(*) FROM appointments WHERE DATE(date) = CURDATE()");

        int patients = tryQueryCount("SELECT COUNT(*) FROM patients WHERE checked_in = 1");
        int doctors = tryQueryCount("SELECT COUNT(*) FROM doctors WHERE on_duty = 1");
        int emergencies = tryQueryCount("SELECT COUNT(*) FROM appointments WHERE status IN ('emergency','critical') OR priority='critical'");

        // Fallbacks if DB not available
        if (appts < 0) appts = 6; // sample
        if (patients < 0) patients = 18;
        if (doctors < 0) doctors = 12;
        if (emergencies < 0) emergencies = 3;

        appointmentsCount.setText(String.valueOf(appts));
        patientsCount.setText(String.valueOf(patients));
        doctorsCount.setText(String.valueOf(doctors));
        emergencyCount.setText(String.valueOf(emergencies));
    }

    private void loadAppointments() {
        apptItems.clear();
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn != null) {
                Statement st = conn.createStatement();
                String sql = "SELECT a.appointment_id, CONCAT(COALESCE(p.first_name,''), ' ', COALESCE(p.last_name,'')) AS patient_name, "
                        + "CONCAT(COALESCE(d.first_name,''), ' ', COALESCE(d.last_name,'')) AS doctor_name, a.appointment_date, a.appointment_time, a.status, a.reason "
                        + "FROM appointments a "
                        + "LEFT JOIN patients p ON a.patient_id = p.patient_id "
                        + "LEFT JOIN doctors d ON a.doctor_id = d.doctor_id "
                        + "WHERE DATE(a.appointment_date)=CURDATE() "
                        + "ORDER BY a.appointment_time LIMIT 50";
                ResultSet rs = st.executeQuery(sql);
                while (rs.next()) {
                    String id = rs.getString(1);
                    String patient = rs.getString(2);
                    String doctor = rs.getString(3);
                    String date = rs.getString(4);
                    String time = rs.getString(5);
                    String status = rs.getString(6);
                    String reason = rs.getString(7);
                    apptItems.add(new Appointment(id, "", patient == null ? "" : patient, "", doctor == null ? "" : doctor, "", date == null ? "" : date, time == null ? "" : time, status == null ? "" : status, reason == null ? "" : reason));
                }
                rs.close();
                st.close();
                conn.close();
            }
        } catch (Exception ex) {
            // fallback to sample provider
            for (com.shahd.hospitalmanagementsystem.Appointmentdataprovider.Appointment a : com.shahd.hospitalmanagementsystem.Appointmentdataprovider.getSampleAppointments()) {
                String id = a.getAppointmentId();
                String patient = a.getPatientName();
                String doctor = a.getDoctorName();
                String dateTime = a.getDateTime();
                String timeOnly = dateTime != null && dateTime.contains("\n") ? dateTime.split("\\n")[1] : "";
                String status = a.getStatus();
                apptItems.add(new Appointment(id, "", patient, "", doctor, a.getSpecialty(), "", timeOnly, status, ""));
            }
        }
    }

    private void loadEmergencies() {
        emergencyItems.clear();
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn != null) {
                Statement st = conn.createStatement();
                String sql = "SELECT a.appointment_id, CONCAT(COALESCE(p.first_name,''), ' ', COALESCE(p.last_name,'')) AS patient_name, a.reason, a.appointment_date, a.appointment_time, a.status "
                        + "FROM appointments a LEFT JOIN patients p ON a.patient_id = p.patient_id "
                        + "WHERE a.status IN ('emergency','critical') OR a.status IN ('confirmed','checked_in') "
                        + "ORDER BY a.appointment_date DESC LIMIT 50";
                ResultSet rs = st.executeQuery(sql);
                while (rs.next()) {
                    String id = rs.getString(1);
                    String patient = rs.getString(2);
                    String reason = rs.getString(3);
                    String date = rs.getString(4);
                    String time = rs.getString(5);
                    String status = rs.getString(6);
                    emergencyItems.add(new Appointment(id, "", patient == null ? "" : patient, "", "", "", date == null ? "" : date, time == null ? "" : time, status == null ? "" : status, reason == null ? "" : reason));
                }
                rs.close();
                st.close();
                conn.close();
            }
        } catch (Exception ex) {
            // fallback: use sample appointments that have status 'checked-in' or 'confirmed' as mock emergencies
            for (com.shahd.hospitalmanagementsystem.Appointmentdataprovider.Appointment a : com.shahd.hospitalmanagementsystem.Appointmentdataprovider.getSampleAppointments()) {
                if ("checked-in".equalsIgnoreCase(a.getStatus()) || "confirmed".equalsIgnoreCase(a.getStatus())) {
                    String id = a.getAppointmentId();
                    String patient = a.getPatientName();
                    String dateTime = a.getDateTime();
                    String timeOnly = dateTime != null && dateTime.contains("\n") ? dateTime.split("\\n")[1] : "";
                    emergencyItems.add(new Appointment(id, "", patient, "", "", a.getSpecialty(), "", timeOnly, a.getStatus(), "Emergency"));
                }
            }
        }
    }

}
