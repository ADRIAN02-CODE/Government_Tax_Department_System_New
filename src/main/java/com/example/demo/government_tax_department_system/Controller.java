package com.example.demo.government_tax_department_system;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    @FXML private TableView<Transaction> basketTable;
    @FXML private TableColumn<Transaction, String> codeColumn;
    @FXML private TableColumn<Transaction, Double> priceColumn;
    @FXML private TableColumn<Transaction, Double> qtyColumn;
    @FXML private TableColumn<Transaction, Double> qtyColumn1;
    @FXML private TableColumn<Transaction, Integer> qtyColumn2;
    @FXML private TableColumn<Transaction, String> qtyColumn3;
    @FXML private TableColumn<Transaction, Boolean> codeColumn1;

    @FXML private Button importButton;
    @FXML private Button calcTaxButton;
    @FXML private Button deleteButton;
    @FXML private Button generateBillButton;
    @FXML private Button viewReportsButton;

    private ObservableList<Transaction> transactions = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Initialize the table columns
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("internalPrice"));
        qtyColumn.setCellValueFactory(new PropertyValueFactory<>("discount"));
        qtyColumn1.setCellValueFactory(new PropertyValueFactory<>("salePrice"));
        qtyColumn2.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        qtyColumn3.setCellValueFactory(new PropertyValueFactory<>("checksum"));
        codeColumn1.setCellValueFactory(new PropertyValueFactory<>("isValid"));

        basketTable.setItems(transactions);
    }

    @FXML
    private void handleImportButton() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Transaction File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            if (!selectedFile.getName().toLowerCase().endsWith(".csv")) {
                showAlert("Error", "Please select a valid CSV file.");
                return;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                transactions.clear();
                String line;
                boolean isFirstLine = true;
                while ((line = reader.readLine()) != null) {
                    // Skip the header row
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue;
                    }
                    String[] parts = line.split(",", -1); // Handle empty fields
                    if (parts.length == 7) {
                        try {
                            Transaction transaction = new Transaction(
                                    parts[0], // itemCode
                                    Double.parseDouble(parts[1]), // internalPrice
                                    Double.parseDouble(parts[2]), // discount
                                    Double.parseDouble(parts[3]), // salePrice
                                    Integer.parseInt(parts[4]), // quantity
                                    parts[5] // checksum
                            );
                            // Optionally set valid field if Transaction class supports it
                            transactions.add(transaction);
                        } catch (NumberFormatException e) {
                            showAlert("Warning", "Skipping invalid row: " + line);
                        }
                    } else {
                        showAlert("Warning", "Skipping row with incorrect columns: " + line);
                    }
                }
                showImportSummary();
            } catch (IOException e) {
                showAlert("Error", "Failed to read file: " + e.getMessage());
            }
        }
    }


    @FXML
    private void handleCalcTaxButton() {
        if (transactions.isEmpty()) {
            showAlert("Error", "No transactions to calculate tax for.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog("15");
        dialog.setTitle("Tax Calculation");
        dialog.setHeaderText("Enter Tax Rate");
        dialog.setContentText("Tax Rate (%):");

        dialog.showAndWait().ifPresent(taxRateStr -> {
            try {
                double taxRate = Double.parseDouble(taxRateStr) / 100;
                double totalProfit = 0.0;

                for (Transaction t : transactions) {
                    t.calculateProfit();
                    totalProfit += t.getProfit();
                }

                double finalTax = totalProfit * taxRate;

                showAlert("Tax Calculation Result",
                        String.format("Total Profit: %.2f\nTax Rate: %.2f%%\nFinal Tax: %.2f",
                                totalProfit, taxRate * 100, finalTax));

                basketTable.refresh();
            } catch (NumberFormatException e) {
                showAlert("Error", "Please enter a valid number for tax rate.");
            }
        });
    }

    @FXML
    private void handleDeleteButton() {
        Transaction selected = basketTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            transactions.remove(selected);
        } else {
            showAlert("Error", "Please select a transaction to delete.");
        }
    }

    @FXML
    private void handleGenerateBillButton() {
        // Remove transactions with zero profit
        List<Transaction> toRemove = new ArrayList<>();
        for (Transaction t : transactions) {
            t.calculateProfit();
            if (t.getProfit() == 0) {
                toRemove.add(t);
            }
        }
        transactions.removeAll(toRemove);
        basketTable.refresh();

        showAlert("Bill Generated", String.format("%d items with zero profit removed.", toRemove.size()));
    }

    @FXML
    private void handleViewReportsButton() {
        int totalRecords = transactions.size();
        int validRecords = (int) transactions.stream().filter(Transaction::getIsValid).count();
        int invalidRecords = totalRecords - validRecords;

        StringBuilder report = new StringBuilder();
        report.append(String.format("Total Records: %d\n", totalRecords));
        report.append(String.format("Valid Records: %d\n", validRecords));
        report.append(String.format("Invalid Records: %d\n\n", invalidRecords));

        report.append("Invalid Transactions:\n");
        transactions.stream()
                .filter(t -> !t.getIsValid())
                .forEach(t -> report.append(t.toString()).append("\n"));

        TextArea textArea = new TextArea(report.toString());
        textArea.setEditable(false);

        ScrollPane scrollPane = new ScrollPane(textArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        Stage stage = new Stage();
        stage.setTitle("Transaction Report");
        stage.setScene(new javafx.scene.Scene(scrollPane, 500, 400));
        stage.show();
    }

    private void showImportSummary() {
        int totalRecords = transactions.size();
        int validRecords = (int) transactions.stream().filter(Transaction::getIsValid).count();
        int invalidRecords = totalRecords - validRecords;

        showAlert("Import Summary",
                String.format("Total records imported: %d\nValid records: %d\nInvalid records: %d",
                        totalRecords, validRecords, invalidRecords));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}