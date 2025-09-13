package Project.View;

import Project.Model.Customer;
import Project.Model.Order;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.ListCell;

/**
 * View class responsible for displaying the order management interface.
 * Handles the layout and styling of order management UI components including
 * customer and order selection, management buttons, and event display.
 */
public class OrderManagementView {
    private VBox view;
    private ComboBox<Customer> customerDropdown = new ComboBox<>();
    private ComboBox<String> orderDropdown = new ComboBox<>();
    private Button checkOrdersButton = new Button("Check Orders");
    private Button removeButton = new Button("Remove Order");
    private Button listButton = new Button("List Orders");
    private Button saveButton = new Button("Save Orders");
    private Button loadButton = new Button("Load Orders");
    private Button clearButton = new Button("Clear Text");
    private TextArea eventTextArea = new TextArea();

    /**
     * Constructs a new order management view with styled UI components.
     * Initializes the layout, dropdowns, buttons, and text area.
     */
    public OrderManagementView() {
        view = new VBox(20);
        view.setPadding(new Insets(20));
        view.setAlignment(Pos.CENTER);
        view.setStyle("-fx-background-color: #f5f5f5;");

        // Create header
        Label header = new Label("Order Management");
        header.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        header.setStyle("-fx-text-fill: #2c3e50;");

        // Style dropdowns
        styleComboBox(customerDropdown, "Select Customer");
        styleComboBox(orderDropdown, "Select Order");
        orderDropdown.setDisable(true);

        // Style buttons
        styleButton(checkOrdersButton, "#3498db");
        styleButton(removeButton, "#e74c3c");
        styleButton(listButton, "#3498db");
        styleButton(saveButton, "#2ecc71");
        styleButton(loadButton, "#3498db");
        styleButton(clearButton, "#e74c3c");

        // Style text area
        eventTextArea.setPrefHeight(250);
        eventTextArea.setWrapText(true);
        eventTextArea.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-border-radius: 5; -fx-padding: 10;");

        // Create containers
        GridPane formContainer = new GridPane();
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setHgap(10);
        formContainer.setVgap(10);
        formContainer.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        formContainer.setPadding(new Insets(20));

        // Add form fields
        formContainer.add(new Label("Customer:"), 0, 0);
        formContainer.add(customerDropdown, 1, 0);
        formContainer.add(new Label("Order:"), 0, 1);
        formContainer.add(orderDropdown, 1, 1);

        // Create button containers
        HBox actionButtons = new HBox(10, checkOrdersButton, removeButton, listButton);
        actionButtons.setAlignment(Pos.CENTER);
        HBox utilityButtons = new HBox(10, saveButton, loadButton, clearButton);
        utilityButtons.setAlignment(Pos.CENTER);

        // Add all components to the main view
        view.getChildren().addAll(header, formContainer, actionButtons, utilityButtons, eventTextArea);
    }

    /**
     * Styles a ComboBox with consistent appearance.
     * @param comboBox The ComboBox to style
     * @param promptText The prompt text to display
     */
    private void styleComboBox(ComboBox<?> comboBox, String promptText) {
        comboBox.setPromptText(promptText);
        comboBox.setPrefWidth(250);
        comboBox.setStyle("-fx-background-color: white; " +
                         "-fx-background-radius: 5; " +
                         "-fx-border-radius: 5; " +
                         "-fx-border-color: #bdc3c7; " +
                         "-fx-border-width: 1; " +
                         "-fx-font-size: 14px; " +
                         "-fx-font-family: Arial; " +
                         "-fx-padding: 5 10;");
    }

    /**
     * Styles a Button with consistent appearance.
     * @param button The Button to style
     * @param color The background color to apply
     */
    private void styleButton(Button button, String color) {
        button.setStyle("-fx-background-color: " + color + "; " +
                       "-fx-text-fill: white; " +
                       "-fx-font-weight: bold; " +
                       "-fx-background-radius: 5; " +
                       "-fx-font-size: 14px; " +
                       "-fx-font-family: Arial;");
        button.setPrefWidth(150);
        button.setPrefHeight(35);
    }

    /**
     * Populates the customer dropdown with customer information.
     * @param customers The list of customers to display
     */
    public void populateCustomerDropdown(List<Customer> customers) {
        // Clear any existing items from the dropdown
        customerDropdown.getItems().clear();
        
        // Add all customers from the provided list to the dropdown
        customerDropdown.getItems().addAll(customers);
        
        // Set up a custom cell factory to control how each customer is displayed in the dropdown
        // This allows us to customize the appearance of each item in the dropdown list
        customerDropdown.setCellFactory(lv -> new ListCell<Customer>() {
            @Override
            protected void updateItem(Customer customer, boolean empty) {
                // Call the parent class's updateItem method to handle basic cell behavior
                super.updateItem(customer, empty);
                
                // If the cell is empty or the customer is null, display nothing
                if (empty || customer == null) {
                    setText(null);
                } else {
                    // Format the customer display text as "Name (PPS)"
                    // This creates a more user-friendly display format
                    setText(customer.getName() + " (" + customer.getPps() + ")");
                }
            }
        });
    }

    /**
     * Populates the order dropdown with order information.
     * @param orders The list of orders to display
     */
    public void populateOrderDropdown(List<Order> orders) {
        orderDropdown.getItems().clear();
        for (Order order : orders) {
            orderDropdown.getItems().add("Order #" + order.getOrderId() + " from " + order.getDate() + " - Total: $" + String.format("%.2f", order.calculateTotal()));
        }
        orderDropdown.setDisable(false);
    }

    /**
     * Returns the customer selection dropdown.
     * @return The ComboBox for customer selection
     */
    public ComboBox<Customer> getCustomerDropdown() {
        return customerDropdown;
    }

    /**
     * Returns the order selection dropdown.
     * @return The ComboBox for order selection
     */
    public ComboBox<String> getOrderDropdown() {
        return orderDropdown;
    }

    /**
     * Returns the check orders button.
     * @return The Button for checking orders
     */
    public Button getCheckOrdersButton() {
        return checkOrdersButton;
    }

    /**
     * Returns the remove order button.
     * @return The Button for removing orders
     */
    public Button getRemoveButton() {
        return removeButton;
    }

    /**
     * Returns the list orders button.
     * @return The Button for listing orders
     */
    public Button getListButton() {
        return listButton;
    }

    /**
     * Returns the save orders button.
     * @return The Button for saving orders
     */
    public Button getSaveButton() {
        return saveButton;
    }

    /**
     * Returns the load orders button.
     * @return The Button for loading orders
     */
    public Button getLoadButton() {
        return loadButton;
    }

    /**
     * Returns the clear text button.
     * @return The Button for clearing text
     */
    public Button getClearButton() {
        return clearButton;
    }

    /**
     * Appends a message to the event text area.
     * @param message The message to display
     */
    public void eventMessage(String message) {
        eventTextArea.appendText(message + "\n");
    }

    /**
     * Clears the contents of the event text area.
     */
    public void clearTextArea() {
        eventTextArea.clear();
    }

    /**
     * Returns the main view component.
     * @return The VBox containing the order management view
     */
    public VBox getView() {
        return view;
    }
} 