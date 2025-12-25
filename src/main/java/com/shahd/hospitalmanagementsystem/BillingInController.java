/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.shahd.hospitalmanagementsystem;

import com.shahd.models.Invoice;
import com.shahd.models.Transaction;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Billing & Insurance controller
 */
public class BillingInController implements Initializable {

    @FXML private Button insuranceVerificationTab;
    @FXML private Button processPaymentTab;
    @FXML private Button pendingBalancesTab;
    @FXML private Button transactionHistoryTab;

    @FXML private StackPane contentArea;

    @FXML private VBox insuranceVerificationView;
    @FXML private VBox processPaymentView;
    @FXML private VBox pendingBalancesView;
    @FXML private VBox transactionHistoryView;

    @FXML private TextField patientIdField;
    @FXML private ComboBox<String> insuranceProviderCombo;
    @FXML private TextField policyNumberField;
    @FXML private TextField groupNumberField;

    @FXML private TextField paymentPatientField;
    @FXML private ComboBox<String> paymentTypeCombo;
    @FXML private TextField amountField;
    @FXML private ComboBox<String> paymentMethodCombo;
    @FXML private TextArea notesArea;

    @FXML private VBox verificationResult;
    @FXML private Label statusBadge;
    @FXML private Label statusLabel;
    @FXML private Label coverageTypeLabel;
    @FXML private Label copayAmountLabel;
    @FXML private Label deductibleLabel;

    @FXML private TableView<Invoice> pendingBalancesTable;
    @FXML private TableColumn<Invoice, String> invoiceIdCol;
    @FXML private TableColumn<Invoice, String> patientCol;
    @FXML private TableColumn<Invoice, String> insuranceCol;
    @FXML private TableColumn<Invoice, Double> totalAmountCol;
    @FXML private TableColumn<Invoice, Double> copayDueCol;
    @FXML private TableColumn<Invoice, String> statusCol;
    @FXML private TableColumn<Invoice, String> actionsCol;

    @FXML private TableView<Transaction> transactionHistoryTable;
    @FXML private TableColumn<Transaction, String> transactionIdCol;
    @FXML private TableColumn<Transaction, String> patientNameCol;
    @FXML private TableColumn<Transaction, String> typeCol;
    @FXML private TableColumn<Transaction, Double> amountColHist;
    @FXML private TableColumn<Transaction, String> methodCol;
    @FXML private TableColumn<Transaction, String> dateTimeCol;
    @FXML private TableColumn<Transaction, String> actionsColHist;

    private ObservableList<Invoice> invoices = FXCollections.observableArrayList();
    private ObservableList<Transaction> transactions = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Setup dropdown menus
        setupDropdowns();
        
        // Setup table columns
        setupPendingBalancesTable();
        setupTransactionHistoryTable();
        
        // Load data
        loadInvoices();
        loadTransactions();
        
        // Default to Insurance Verification tab
        showInsuranceVerification(null);
    }

    private void setupDropdowns() {
        // Insurance Provider options
        ObservableList<String> insuranceProviders = FXCollections.observableArrayList(
            "Blue Cross PPO",
            "Medicare",
            "Medicaid",
            "Aetna HMO",
            "UnitedHealth",
            "Cigna",
            "Humana",
            "Kaiser Permanente"
        );
        insuranceProviderCombo.setItems(insuranceProviders);

        // Payment Type options
        ObservableList<String> paymentTypes = FXCollections.observableArrayList(
            "Co-pay",
            "Deductible",
            "Co-insurance",
            "Full Balance"
        );
        paymentTypeCombo.setItems(paymentTypes);

        // Payment Method options
        ObservableList<String> paymentMethods = FXCollections.observableArrayList(
            "Credit Card",
            "Debit Card",
            "Cash",
            "Check",
            "Electronic Transfer",
            "Insurance"
        );
        paymentMethodCombo.setItems(paymentMethods);
    }

    private void setupPendingBalancesTable() {
        invoiceIdCol.setCellValueFactory(new PropertyValueFactory<>("invoiceId"));
        patientCol.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        insuranceCol.setCellValueFactory(new PropertyValueFactory<>("insurance"));
        totalAmountCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        copayDueCol.setCellValueFactory(new PropertyValueFactory<>("copayDue"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // Format currency columns
        totalAmountCol.setCellFactory(col -> new TableCell<Invoice, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : String.format("$%.2f", item));
            }
        });
        
        copayDueCol.setCellFactory(col -> new TableCell<Invoice, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : String.format("$%.2f", item));
            }
        });
        
        // Status badge styling
        statusCol.setCellFactory(col -> new TableCell<Invoice, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle(null);
                } else {
                    setText(item);
                    if ("pending".equalsIgnoreCase(item)) {
                        setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold;");
                    } else if ("partial".equalsIgnoreCase(item)) {
                        setStyle("-fx-text-fill: #3b82f6; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
                    }
                }
            }
        });

        // Actions column
        actionsCol.setCellFactory(col -> new TableCell<Invoice, String>() {
            private final Button collectBtn = new Button("Collect");
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    collectBtn.setStyle("-fx-padding: 8 16 8 16; -fx-font-size: 12; -fx-background-color: #00b4d8; -fx-text-fill: white; -fx-border-radius: 4; -fx-cursor: hand; -fx-font-weight: bold;");
                    collectBtn.setOnAction(e -> {
                        Invoice inv = getTableView().getItems().get(getIndex());
                        System.out.println("Collecting payment for " + inv.getPatientName());
                    });
                    setGraphic(collectBtn);
                }
            }
        });

        pendingBalancesTable.setItems(invoices);
        
        // Add row selection styling - only selected row highlighted in blue
        pendingBalancesTable.setRowFactory(tv -> new TableRow<Invoice>() {
            @Override
            protected void updateItem(Invoice item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || !isSelected()) {
                    setStyle("");
                } else {
                    setStyle("-fx-background-color: #dbeafe;");
                }
            }
        });
    }

    private void setupTransactionHistoryTable() {
        transactionIdCol.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        patientNameCol.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        amountColHist.setCellValueFactory(new PropertyValueFactory<>("amount"));
        methodCol.setCellValueFactory(new PropertyValueFactory<>("method"));
        dateTimeCol.setCellValueFactory(new PropertyValueFactory<>("dateTime"));

        // Format currency column
        amountColHist.setCellFactory(col -> new TableCell<Transaction, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : String.format("$%.2f", item));
            }
        });

        // Actions column
        actionsColHist.setCellFactory(col -> new TableCell<Transaction, String>() {
            private final Button printBtn = new Button("🖨");
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    printBtn.setStyle("-fx-padding: 8 12 8 12; -fx-font-size: 12; -fx-background-color: #0096c7; -fx-border-radius: 4; -fx-cursor: hand; -fx-text-fill: white; -fx-font-weight: bold;");
                    printBtn.setOnAction(e -> {
                        Transaction trans = getTableView().getItems().get(getIndex());
                        System.out.println("Printing receipt for transaction " + trans.getTransactionId());
                    });
                    setGraphic(printBtn);
                }
            }
        });

        transactionHistoryTable.setItems(transactions);
        
        // Add row selection styling - only selected row highlighted in blue
        transactionHistoryTable.setRowFactory(tv -> new TableRow<Transaction>() {
            @Override
            protected void updateItem(Transaction item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || !isSelected()) {
                    setStyle("");
                } else {
                    setStyle("-fx-background-color: #dbeafe;");
                }
            }
        });
    }

    private void loadInvoices() {
        invoices.clear();
        invoices.add(new Invoice("INV001", "P12345", "Sarah Johnson", "Blue Cross PPO", 250.00, 25.00, "pending"));
        invoices.add(new Invoice("INV002", "P12346", "Mike Brown", "Medicare", 450.00, 0.00, "pending"));
        invoices.add(new Invoice("INV003", "P12347", "Emily Davis", "Aetna HMO", 180.00, 20.00, "partial"));
    }

    private void loadTransactions() {
        transactions.clear();
        transactions.add(new Transaction("TXN001", "James Wilson", "Co-pay", 50.00, "Credit Card", "10/20/2025 09:15 AM"));
        transactions.add(new Transaction("TXN002", "Lisa Anderson", "Co-pay", 35.00, "Cash", "10/20/2025 10:30 AM"));
        transactions.add(new Transaction("TXN003", "David Martinez", "Deductible", 125.00, "Debit Card", "10/19/2025 02:45 PM"));
    }

    @FXML
    private void showInsuranceVerification(ActionEvent event) {
        setActiveView(insuranceVerificationView);
    }

    @FXML
    private void showProcessPayment(ActionEvent event) {
        setActiveView(processPaymentView);
    }

    @FXML
    private void showPendingBalances(ActionEvent event) {
        setActiveView(pendingBalancesView);
    }

    @FXML
    private void showTransactionHistory(ActionEvent event) {
        setActiveView(transactionHistoryView);
    }

    @FXML
    private void verifyCoverage(ActionEvent event) {
        // Minimal verification simulation
        verificationResult.setVisible(true);
        verificationResult.setManaged(true);
        statusLabel.setText("Verified");
        coverageTypeLabel.setText("Full");
        copayAmountLabel.setText("$20");
        deductibleLabel.setText("$100");
        statusBadge.setText("Active");
    }

    @FXML
    private void processPayment(ActionEvent event) {
        // Minimal stub: mark payment processed
        // In a real implementation, add DB calls / receipt printing
        System.out.println("Payment processed for amount: " + amountField.getText());
    }

    private void setActiveView(VBox view) {
        // Hide all views first
        if (insuranceVerificationView != null) {
            insuranceVerificationView.setVisible(false);
            insuranceVerificationView.setManaged(false);
        }
        if (processPaymentView != null) {
            processPaymentView.setVisible(false);
            processPaymentView.setManaged(false);
        }
        if (pendingBalancesView != null) {
            pendingBalancesView.setVisible(false);
            pendingBalancesView.setManaged(false);
        }
        if (transactionHistoryView != null) {
            transactionHistoryView.setVisible(false);
            transactionHistoryView.setManaged(false);
        }

        if (view != null) {
            view.setVisible(true);
            view.setManaged(true);
        }

        // Update tab button styles so only the corresponding tab shows as active
        if (insuranceVerificationTab != null) {
            insuranceVerificationTab.getStyleClass().remove("active-tab");
        }
        if (processPaymentTab != null) {
            processPaymentTab.getStyleClass().remove("active-tab");
        }
        if (pendingBalancesTab != null) {
            pendingBalancesTab.getStyleClass().remove("active-tab");
        }
        if (transactionHistoryTab != null) {
            transactionHistoryTab.getStyleClass().remove("active-tab");
        }

        if (view == insuranceVerificationView && insuranceVerificationTab != null) {
            if (!insuranceVerificationTab.getStyleClass().contains("active-tab")) {
                insuranceVerificationTab.getStyleClass().add("active-tab");
            }
        } else if (view == processPaymentView && processPaymentTab != null) {
            if (!processPaymentTab.getStyleClass().contains("active-tab")) {
                processPaymentTab.getStyleClass().add("active-tab");
            }
        } else if (view == pendingBalancesView && pendingBalancesTab != null) {
            if (!pendingBalancesTab.getStyleClass().contains("active-tab")) {
                pendingBalancesTab.getStyleClass().add("active-tab");
            }
        } else if (view == transactionHistoryView && transactionHistoryTab != null) {
            if (!transactionHistoryTab.getStyleClass().contains("active-tab")) {
                transactionHistoryTab.getStyleClass().add("active-tab");
            }
        }
    }
}
