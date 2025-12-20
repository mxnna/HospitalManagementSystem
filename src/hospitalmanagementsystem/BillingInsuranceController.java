package hospitalmanagementsystem;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class BillingInsuranceController implements Initializable {

    // Tab Buttons
    @FXML private Button insuranceVerificationTab;
    @FXML private Button processPaymentTab;
    @FXML private Button pendingBalancesTab;
    @FXML private Button transactionHistoryTab;

    // Content Area - StackPane
    @FXML private StackPane contentArea;
    
    // Views
    @FXML private VBox insuranceVerificationView;
    @FXML private VBox processPaymentView;
    @FXML private VBox pendingBalancesView;
    @FXML private VBox transactionHistoryView;

    // Insurance Verification Fields
    @FXML private TextField patientIdField;
    @FXML private ComboBox<String> insuranceProviderCombo;
    @FXML private TextField policyNumberField;
    @FXML private TextField groupNumberField;
    @FXML private Button verifyCoverageBtn;
    @FXML private VBox verificationResult;
    @FXML private Label statusBadge;
    @FXML private Label statusLabel;
    @FXML private Label coverageTypeLabel;
    @FXML private Label copayAmountLabel;
    @FXML private Label deductibleLabel;

    // Process Payment Fields
    @FXML private TextField paymentPatientField;
    @FXML private ComboBox<String> paymentTypeCombo;
    @FXML private TextField amountField;
    @FXML private ComboBox<String> paymentMethodCombo;
    @FXML private TextArea notesArea;

    // Pending Balances Table
    @FXML private TableView<PendingBalance> pendingBalancesTable;
    @FXML private TableColumn<PendingBalance, String> invoiceIdCol;
    @FXML private TableColumn<PendingBalance, String> patientCol;
    @FXML private TableColumn<PendingBalance, String> insuranceCol;
    @FXML private TableColumn<PendingBalance, String> totalAmountCol;
    @FXML private TableColumn<PendingBalance, String> copayDueCol;
    @FXML private TableColumn<PendingBalance, String> statusCol;
    @FXML private TableColumn<PendingBalance, Void> actionsCol;

    // Transaction History Table
    @FXML private TableView<Transaction> transactionHistoryTable;
    @FXML private TableColumn<Transaction, String> transactionIdCol;
    @FXML private TableColumn<Transaction, String> patientNameCol;
    @FXML private TableColumn<Transaction, String> typeCol;
    @FXML private TableColumn<Transaction, String> amountColHist;
    @FXML private TableColumn<Transaction, String> methodCol;
    @FXML private TableColumn<Transaction, String> dateTimeCol;
    @FXML private TableColumn<Transaction, Void> actionsColHist;

    private ObservableList<PendingBalance> pendingBalancesList;
    private ObservableList<Transaction> transactionsList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeComboBoxes();
        initializePendingBalancesTable();
        initializeTransactionHistoryTable();
        
        // إخفاء كل الـ Views عدا الأولى
        hideAllViews();
        insuranceVerificationView.setVisible(true);
        insuranceVerificationView.setManaged(true);
        
        // تحديث التبويب النشط
        updateActiveTab(insuranceVerificationTab);
    }

    private void initializeComboBoxes() {
        try {
            // Insurance Providers
            insuranceProviderCombo.setItems(FXCollections.observableArrayList(
                "Blue Cross Blue Shield",
                "Medicare",
                "Aetna",
                "United Healthcare",
                "Cigna"
            ));
            
            // Set default selection if needed
            if (!insuranceProviderCombo.getItems().isEmpty()) {
                insuranceProviderCombo.getSelectionModel().selectFirst();
            }

            // Payment Types
            paymentTypeCombo.setItems(FXCollections.observableArrayList(
                "Co-pay",
                "Deductible", 
                "Full Payment",
                "Partial Payment"
            ));
            if (!paymentTypeCombo.getItems().isEmpty()) {
                paymentTypeCombo.getSelectionModel().selectFirst();
            }

            // Payment Methods
            paymentMethodCombo.setItems(FXCollections.observableArrayList(
                "Credit Card",
                "Debit Card", 
                "Cash",
                "Check",
                "Insurance"
            ));
            if (!paymentMethodCombo.getItems().isEmpty()) {
                paymentMethodCombo.getSelectionModel().selectFirst();
            }
            
        } catch (NullPointerException e) {
            System.err.println("ComboBox initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializePendingBalancesTable() {
        invoiceIdCol.setCellValueFactory(new PropertyValueFactory<>("invoiceId"));
        patientCol.setCellValueFactory(new PropertyValueFactory<>("patient"));
        insuranceCol.setCellValueFactory(new PropertyValueFactory<>("insurance"));
        totalAmountCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        copayDueCol.setCellValueFactory(new PropertyValueFactory<>("copayDue"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Add Action Buttons
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button collectBtn = new Button("Collect");
            private final Button printBtn = new Button("🖨");

            {
                collectBtn.getStyleClass().add("primary-button");
                collectBtn.setStyle("-fx-font-size: 12px; -fx-padding: 6 12;");
                printBtn.setStyle("-fx-font-size: 14px; -fx-padding: 6 10; -fx-background-color: transparent; -fx-border-color: #d0d0d0; -fx-border-width: 1; -fx-border-radius: 3;");
                
                collectBtn.setOnAction(event -> {
                    PendingBalance balance = getTableView().getItems().get(getIndex());
                    collectPayment(balance);
                });
                
                printBtn.setOnAction(event -> {
                    PendingBalance balance = getTableView().getItems().get(getIndex());
                    printInvoice(balance);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, collectBtn, printBtn);
                    buttons.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(buttons);
                }
            }
        });

        // Sample Data - Updated to match new insurance provider names
        pendingBalancesList = FXCollections.observableArrayList(
            new PendingBalance("INV001", "Sarah Johnson\nP12345", "Blue Cross Blue Shield", "$250.00", "$25.00", "pending"),
            new PendingBalance("INV002", "Mike Brown\nP12346", "Medicare", "$450.00", "$0.00", "pending"),
            new PendingBalance("INV003", "Emily Davis\nP12347", "Aetna", "$180.00", "$20.00", "partial")
        );
        pendingBalancesTable.setItems(pendingBalancesList);
    }

    private void initializeTransactionHistoryTable() {
        transactionIdCol.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        patientNameCol.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        amountColHist.setCellValueFactory(new PropertyValueFactory<>("amount"));
        methodCol.setCellValueFactory(new PropertyValueFactory<>("method"));
        dateTimeCol.setCellValueFactory(new PropertyValueFactory<>("dateTime"));

        // Add Print Button
        actionsColHist.setCellFactory(param -> new TableCell<>() {
            private final Button printBtn = new Button("🖨");

            {
                printBtn.setStyle("-fx-font-size: 14px; -fx-padding: 6 10; -fx-background-color: transparent; -fx-border-color: #d0d0d0; -fx-border-width: 1; -fx-border-radius: 3; -fx-cursor: hand;");
                printBtn.setOnAction(event -> {
                    Transaction transaction = getTableView().getItems().get(getIndex());
                    printReceipt(transaction);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(printBtn);
                }
            }
        });

        // Sample Data
        transactionsList = FXCollections.observableArrayList(
            new Transaction("TXN001", "James Wilson", "Co-pay", "$50.00", "Credit Card", "10/20/2025\n08:15 AM"),
            new Transaction("TXN002", "Lisa Anderson", "Co-pay", "$35.00", "Cash", "10/20/2025\n10:30 AM"),
            new Transaction("TXN003", "David Martinez", "Deductible", "$125.00", "Debit Card", "10/19/2025\n02:45 PM")
        );
        transactionHistoryTable.setItems(transactionsList);
    }

    @FXML
    private void showInsuranceVerification() {
        System.out.println("Show Insurance Verification clicked");
        hideAllViews();
        insuranceVerificationView.setVisible(true);
        insuranceVerificationView.setManaged(true);
        updateActiveTab(insuranceVerificationTab);
    }

    @FXML
    private void showProcessPayment() {
        System.out.println("Show Process Payment clicked");
        hideAllViews();
        processPaymentView.setVisible(true);
        processPaymentView.setManaged(true);
        updateActiveTab(processPaymentTab);
    }

    @FXML
    private void showPendingBalances() {
        System.out.println("Show Pending Balances clicked");
        hideAllViews();
        pendingBalancesView.setVisible(true);
        pendingBalancesView.setManaged(true);
        updateActiveTab(pendingBalancesTab);
    }

    @FXML
    private void showTransactionHistory() {
        System.out.println("Show Transaction History clicked");
        hideAllViews();
        transactionHistoryView.setVisible(true);
        transactionHistoryView.setManaged(true);
        updateActiveTab(transactionHistoryTab);
    }

    private void hideAllViews() {
        insuranceVerificationView.setVisible(false);
        insuranceVerificationView.setManaged(false);
        
        processPaymentView.setVisible(false);
        processPaymentView.setManaged(false);
        
        pendingBalancesView.setVisible(false);
        pendingBalancesView.setManaged(false);
        
        transactionHistoryView.setVisible(false);
        transactionHistoryView.setManaged(false);
    }

    private void updateActiveTab(Button activeButton) {
        // إزالة الـ active-tab من كل الأزرار
        insuranceVerificationTab.getStyleClass().remove("active-tab");
        processPaymentTab.getStyleClass().remove("active-tab");
        pendingBalancesTab.getStyleClass().remove("active-tab");
        transactionHistoryTab.getStyleClass().remove("active-tab");
        
        // إضافة الـ active-tab للزر المحدد
        activeButton.getStyleClass().add("active-tab");
    }

    @FXML
    private void verifyCoverage() {
        String patientId = patientIdField.getText();
        String provider = insuranceProviderCombo.getValue();
        String policyNumber = policyNumberField.getText();
        String groupNumber = groupNumberField.getText();

        if (patientId.isEmpty() || provider == null || policyNumber.isEmpty()) {
            showAlert("Validation Error", "Please fill in all required fields.");
            return;
        }

        // Simulate verification
        statusLabel.setText("Active");
        coverageTypeLabel.setText("PPO");
        copayAmountLabel.setText("$25.00");
        deductibleLabel.setText("$500.00");
        
        verificationResult.setVisible(true);
        verificationResult.setManaged(true);
        
        showAlert("Success", "Insurance coverage verified successfully!");
    }

    @FXML
    private void processPayment() {
        String patient = paymentPatientField.getText();
        String type = paymentTypeCombo.getValue();
        String amount = amountField.getText();
        String method = paymentMethodCombo.getValue();

        if (patient.isEmpty() || type == null || amount.isEmpty() || method == null) {
            showAlert("Validation Error", "Please fill in all required fields.");
            return;
        }

        // Add transaction to history
        String transactionId = "TXN" + String.format("%03d", transactionsList.size() + 1);
        LocalDateTime now = LocalDateTime.now();
        String dateTime = now.format(DateTimeFormatter.ofPattern("MM/dd/yyyy\nhh:mm a"));
        
        Transaction newTransaction = new Transaction(transactionId, patient, type, "$" + amount, method, dateTime);
        transactionsList.add(0, newTransaction);
        
        // Clear fields
        paymentPatientField.clear();
        paymentTypeCombo.setValue(null);
        amountField.setText("0.00");
        paymentMethodCombo.setValue(null);
        notesArea.clear();
        
        showAlert("Success", "Payment processed successfully!\nTransaction ID: " + transactionId);
    }

    private void collectPayment(PendingBalance balance) {
        showAlert("Collect Payment", "Collecting payment for Invoice: " + balance.getInvoiceId());
    }

    private void printInvoice(PendingBalance balance) {
        showAlert("Print Invoice", "Printing invoice: " + balance.getInvoiceId());
    }

    private void printReceipt(Transaction transaction) {
        showAlert("Print Receipt", "Printing receipt for Transaction: " + transaction.getTransactionId());
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Data Models
    public static class PendingBalance {
        private final SimpleStringProperty invoiceId;
        private final SimpleStringProperty patient;
        private final SimpleStringProperty insurance;
        private final SimpleStringProperty totalAmount;
        private final SimpleStringProperty copayDue;
        private final SimpleStringProperty status;

        public PendingBalance(String invoiceId, String patient, String insurance, 
                             String totalAmount, String copayDue, String status) {
            this.invoiceId = new SimpleStringProperty(invoiceId);
            this.patient = new SimpleStringProperty(patient);
            this.insurance = new SimpleStringProperty(insurance);
            this.totalAmount = new SimpleStringProperty(totalAmount);
            this.copayDue = new SimpleStringProperty(copayDue);
            this.status = new SimpleStringProperty(status);
        }

        public String getInvoiceId() { return invoiceId.get(); }
        public String getPatient() { return patient.get(); }
        public String getInsurance() { return insurance.get(); }
        public String getTotalAmount() { return totalAmount.get(); }
        public String getCopayDue() { return copayDue.get(); }
        public String getStatus() { return status.get(); }
    }

    public static class Transaction {
        private final SimpleStringProperty transactionId;
        private final SimpleStringProperty patientName;
        private final SimpleStringProperty type;
        private final SimpleStringProperty amount;
        private final SimpleStringProperty method;
        private final SimpleStringProperty dateTime;

        public Transaction(String transactionId, String patientName, String type, 
                          String amount, String method, String dateTime) {
            this.transactionId = new SimpleStringProperty(transactionId);
            this.patientName = new SimpleStringProperty(patientName);
            this.type = new SimpleStringProperty(type);
            this.amount = new SimpleStringProperty(amount);
            this.method = new SimpleStringProperty(method);
            this.dateTime = new SimpleStringProperty(dateTime);
        }

        public String getTransactionId() { return transactionId.get(); }
        public String getPatientName() { return patientName.get(); }
        public String getType() { return type.get(); }
        public String getAmount() { return amount.get(); }
        public String getMethod() { return method.get(); }
        public String getDateTime() { return dateTime.get(); }
    }
}

@FXML
    private Button aboutpage;


    @FXML
    private Button appointmentspage;

    @FXML
    private Button billingpage;

    @FXML
    private Button doctorpage;

    @FXML
    private Button homepage;

    @FXML
    private Button patientpage;

    @FXML
    private Button servicepage;