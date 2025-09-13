package Project.Controller;

import Project.Model.CustModel;
import Project.Model.Customer;
import Project.View.CustView;
import Project.Model.StoreDataManager;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Controller class responsible for managing customer-related operations.
 * Handles user interactions for adding, removing, finding, and listing customers.
 * Manages the communication between the customer view and model.
 */
public class CustController {

    // Field

    private CustModel model;
    private CustView view;

    // Constructor

    /**
     * Constructs a new customer controller with the specified model and view.
     * @param model The customer model to use
     * @param view The customer view to use
     */
    public CustController(CustModel model, CustView view) {
        this.model = model;
        this.view = view;
        // Initialize button actions
        setupEventHandlers();
    }

    // Methods

    /**
     * Sets up event handlers for all view buttons.
     * Each button is assigned its corresponding action method.
     */
    private void setupEventHandlers() {
        // Each button is set an action which calls a method when clicked
        view.getAddButton().setOnAction(e -> addCustomer());
        view.getRemoveButton().setOnAction(e -> removeCustomer());
        view.getListButton().setOnAction(e -> listCustomers());
        view.getFindButton().setOnAction(e -> findCustomer());
        view.getSaveButton().setOnAction(e -> saveCustomers());
        view.getLoadButton().setOnAction(e -> loadCustomers());
    }

    /**
     * Adds a new customer using the information entered in the view.
     * Validates input fields and creates a new customer if all fields are valid.
     * Displays appropriate success or error messages.
     */
    private void addCustomer() {
        try {
            String name = view.getNameField().getText().trim();
            String email = view.getEmailField().getText().trim();
            String pps = view.getPpsField().getText().trim();
            String address = view.getAddressField().getText().trim();
            if (name.isEmpty() || email.isEmpty() || pps.isEmpty() || address.isEmpty()) {
                view.eventMessage("\nError: All fields must be filled.\n");
                return;
            }
            // Create a temporary password for customers added through management
            String tempPassword = "password123";
            Customer customer = new Customer.Builder(name, email, tempPassword, pps)
                .address(address)
                .build();
            if (model.addCust(customer)) {
                // Save to database using StoreDataManager
                if (StoreDataManager.getInstance().saveStoreData(
                    new ArrayList<>(model.getCustList()),
                    new ArrayList<>(),
                    new ArrayList<>()
                )) {
                    view.eventMessage("\nCustomer added and saved to database successfully.\n");
                } else {
                    view.eventMessage("\nCustomer added but failed to save to database.\n");
                }
                view.clearFields();
            } else {
                view.eventMessage("\nError: Customer with this PPS already exists.\n");
            }
        } catch (IllegalArgumentException e) {
            view.eventMessage("\nError: " + e.getMessage() + "\n");
        } catch (Exception e) {
            view.eventMessage("\nError: Failed to add customer.\n");
        }
    }
    /**
     * Removes a customer based on the PPS number entered in the view.
     * Shows a confirmation dialog before removal.
     * Displays appropriate success or error messages.
     */
    private void removeCustomer() {
        try {
            String pps = view.getPpsField().getText().trim();
            if (pps.isEmpty()) {
                view.eventMessage("\nError: PPS field must be filled.\n");
                return;
            }
            Alert confirm = new Alert(AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Removal");
            confirm.setHeaderText("Remove Customer");
            confirm.setContentText("Are you sure you want to remove this customer?");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (model.removeCust(pps)) {
                    view.clearFields();
                    view.eventMessage("\nCustomer removed successfully.\n");
                } else {
                    view.eventMessage("\nError: Customer not found.\n");
                }
            }
        } catch (Exception e) {
            view.eventMessage("\nError: Failed to remove customer.\n");
        }
    }
    /**
     * Finds a customer based on the PPS number entered in the view.
     * If found, displays the customer's details in the view.
     * Displays appropriate error messages if the customer is not found.
     */
    private void findCustomer() {
        try {
            String pps = view.getPpsField().getText().trim();
            if (pps.isEmpty()) {
                view.eventMessage("\nError: PPS field must be filled.\n");
                return;
            }
            Customer customer = model.findInList(pps);
            if (customer != null) {
                view.getNameField().setText(customer.getName());
                view.getEmailField().setText(customer.getEmail());
                view.getPpsField().setText(customer.getPps());
                view.getAddressField().setText(customer.getAddress());
                view.eventMessage("\nCustomer Details:\n" +
                                "-----------------\n" +
                                "Name:    " + customer.getName() + "\n" +
                                "Email:   " + customer.getEmail() + "\n" +
                                "PPS:     " + customer.getPps() + "\n" +
                                "Address: " + customer.getAddress() + "\n");
            } else {
                view.eventMessage("\nError: Customer not found.\n");
            }
        } catch (Exception e) {
            view.eventMessage("\nError: Failed to find customer.\n");
        }
    }
    /**
     * Saves the current list of customers to a file.
     * Uses the model's save functionality and displays appropriate messages.
     */
    private void saveCustomers() {
        try {
            if (model.saveCustomers()) {
                view.eventMessage("\nCustomers saved successfully.\n");
            } else {
                view.eventMessage("\nError: Failed to save customers.\n");
            }
        } catch (Exception e) {
            view.eventMessage("\nError: Failed to save customers.\n");
        }
    }
    /**
     * Loads customers from the database into the model.
     * Uses StoreDataManager to load data and displays appropriate messages.
     * Shows a popup with loaded customer details for validation.
     */
    private void loadCustomers() {
        try {
            if (model.loadCustomers()) {
                view.eventMessage("\nSuccessfully loaded customers from database.\n");
                updateView();
            } else {
                view.eventMessage("\nError: Failed to load customers from database.\n");
            }
        } catch (Exception e) {
            System.out.println("Error loading customers from database: " + e.getMessage());
            e.printStackTrace();
            view.eventMessage("\nError: Failed to load customers from database.\n");
        }
    }

    /**
     * Lists all customers in the system.
     * Displays a formatted list of customers with their details.
     */
    private void listCustomers() {
        StringBuilder customersList = new StringBuilder();
        customersList.append("\n=== CUSTOMER LIST ===\n");
        customersList.append("=====================\n\n");
        for (Customer customer : model.getCustList()) {
            customersList.append("Customer: ").append(customer.getName()).append("\n");
            customersList.append("  PPS:     ").append(customer.getPps()).append("\n");
            customersList.append("  Email:   ").append(customer.getEmail()).append("\n");
            customersList.append("  Address: ").append(customer.getAddress()).append("\n");
            customersList.append("  --------------------\n");
        }
        if (model.getCustList().isEmpty()) {
            customersList.append("No customers available in the system.\n");
        }
        customersList.append("\n=====================\n");
        view.eventMessage(customersList.toString());
    }

    /**
     * Updates the view with current customer data.
     */
    private void updateView() {
        ArrayList<Customer> customers = model.getCustList();
        StringBuilder sb = new StringBuilder();
        sb.append("\n=== CURRENT CUSTOMERS ===\n");
        sb.append("========================\n\n");
        for (Customer customer : customers) {
            sb.append("Customer: ").append(customer.getName()).append("\n");
            sb.append("  PPS:     ").append(customer.getPps()).append("\n");
            sb.append("  Email:   ").append(customer.getEmail()).append("\n");
            sb.append("  Address: ").append(customer.getAddress()).append("\n");
            sb.append("  --------------------\n");
        }
        if (customers.isEmpty()) {
            sb.append("No customers available in the system.\n");
        }
        sb.append("\n========================\n");
        view.eventMessage(sb.toString());
    }

    /**
     * Returns the main view component of the customer interface.
     * @return The VBox containing the customer view
     */
    public VBox getVbox(){
        return view.getView();
    }
}
