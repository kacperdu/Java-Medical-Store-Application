package Project.View;

import Project.Model.Customer;
import Project.Model.Product;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * View class responsible for displaying the order interface.
 * Handles the layout and styling of order-related UI components including
 * customer and product selection, order management buttons, and event display.
 */
public class OrderView {
    private VBox view;
    private ComboBox<String> customerDropdown = new ComboBox<>();
    private ComboBox<String> productDropdown = new ComboBox<>();
    private ComboBox<String> sortDropdown = new ComboBox<>();
    private Button sortButton = new Button("Sort Orders");
    private Button stockCheckButton = new Button("Check Stock");
    private Button salesCheckButton = new Button("Monthly Sales");
    private Button addButton = new Button("Add Purchase");
    private Button receiptButton = new Button("Generate Receipt");
    private Button clearButton = new Button("Clear Text");
    private TextArea eventTextArea = new TextArea();

    /**
     * Constructs a new order view with styled UI components.
     * Initializes the layout, dropdowns, buttons, and text area.
     */
    public OrderView() {
        view = new VBox(20);
        view.setPadding(new Insets(20));
        view.setAlignment(Pos.CENTER);
        view.setStyle("-fx-background-color: #f5f5f5;");

        // Create header
        Label header = new Label("Order History");
        header.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        header.setStyle("-fx-text-fill: #2c3e50;");

        // Style dropdowns
        styleComboBox(customerDropdown, "Select Customer");
        styleComboBox(productDropdown, "Select Product");
        styleComboBox(sortDropdown, "Sort Orders");
        sortDropdown.getItems().addAll("Sort by Date", "Sort by Product Name");
        sortDropdown.setValue("Sort by Date");

        // Style buttons
        styleButton(addButton, "#2ecc71");
        styleButton(receiptButton, "#3498db");
        styleButton(sortButton, "#3498db");
        styleButton(stockCheckButton, "#3498db");
        styleButton(salesCheckButton, "#3498db");
        styleButton(clearButton, "#e74c3c");

        // Style text area
        eventTextArea.setPrefHeight(250);
        eventTextArea.setWrapText(true);
        eventTextArea.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-border-radius: 5; -fx-padding: 10;");

        // Create containers
        VBox formContainer = new VBox(15);
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        formContainer.setPadding(new Insets(20));

        HBox customerBox = new HBox(10, new Label("Customer:"), customerDropdown);
        customerBox.setAlignment(Pos.CENTER);

        HBox productBox = new HBox(10, new Label("Product:"), productDropdown);
        productBox.setAlignment(Pos.CENTER);

        HBox buttonsBox = new HBox(10, addButton, receiptButton);
        buttonsBox.setAlignment(Pos.CENTER);

        HBox sortBox = new HBox(10, new Label("Sort Orders:"), sortDropdown, sortButton);
        sortBox.setAlignment(Pos.CENTER);

        HBox stockSalesBox = new HBox(10, stockCheckButton, salesCheckButton);
        stockSalesBox.setAlignment(Pos.CENTER);

        HBox clearBox = new HBox(10, clearButton);
        clearBox.setAlignment(Pos.CENTER);

        formContainer.getChildren().addAll(customerBox, productBox, buttonsBox, sortBox, stockSalesBox, clearBox);

        view.getChildren().addAll(header, formContainer, eventTextArea);
    }

    /**
     * Styles a ComboBox with consistent appearance.
     * @param comboBox The ComboBox to style
     * @param prompt The prompt text to display
     */
    private void styleComboBox(ComboBox<String> comboBox, String prompt) {
        comboBox.setPromptText(prompt);
        comboBox.setPrefWidth(250);
        comboBox.setStyle("-fx-background-color: white; " +
                         "-fx-background-radius: 5; " +
                         "-fx-border-radius: 5; " +
                         "-fx-border-color: #bdc3c7; " +
                         "-fx-border-width: 1; " +
                         "-fx-font-size: 14px; " +
                         "-fx-font-family: Arial; " +
                         "-fx-padding: 5 10;");
        
        // Style the dropdown list
        comboBox.setStyle("-fx-background-color: white; " +
                         "-fx-background-radius: 5; " +
                         "-fx-border-radius: 5; " +
                         "-fx-border-color: #bdc3c7; " +
                         "-fx-border-width: 1; " +
                         "-fx-font-size: 14px; " +
                         "-fx-font-family: Arial; " +
                         "-fx-padding: 5 10;");
        
        // Style the dropdown list items
        comboBox.setCellFactory(lv -> {
            return new javafx.scene.control.ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item);
                        setStyle("-fx-font-size: 14px; " +
                                "-fx-font-family: Arial; " +
                                "-fx-padding: 5 10;");
                    }
                }
            };
        });
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
     * Returns the main view component.
     * @return The VBox containing the order view
     */
    public VBox getView() {
        return view;
    }

    /**
     * Populates the customer dropdown with customer information.
     * @param customers The list of customers to display
     */
    public void populateCustomerDropdown(List<Customer> customers) {
        customerDropdown.getItems().clear();
        for (Customer customer : customers) {
            customerDropdown.getItems().add(customer.getName() + " (" + customer.getPps() + ")");
        }
    }
    // Populating the dropdown with products
    public void populateProductDropdown(List<Product> products) {
        productDropdown.getItems().clear();
        for (Product product : products) {
            productDropdown.getItems().add(product.getName() + " ($" + product.getPrice() + ")");
        }
    }
    /**
     * Returns the customer selection dropdown.
     * @return The ComboBox for customer selection
     */
    public ComboBox<String> getCustomerDropdown() {
        return customerDropdown;
    }
    /**
     * Returns the product selection dropdown.
     * @return The ComboBox for product selection
     */
    public ComboBox<String> getProductDropdown() {
        return productDropdown;
    }
    /**
     * Returns the sort options dropdown.
     * @return The ComboBox for sort options
     */
    public ComboBox<String> getSortDropdown() {
        return sortDropdown;
    }
    /**
     * Returns the sort orders button.
     * @return The Button for sorting orders
     */
    public Button getSortButton() {
        return sortButton;
    }
    /**
     * Returns the stock check button.
     * @return The Button for checking stock
     */
    public Button getStockCheckButton() {
        return stockCheckButton;
    }
    /**
     * Returns the sales check button.
     * @return The Button for checking sales
     */
    public Button getSalesCheckButton() {
        return salesCheckButton;
    }
    /**
     * Returns the add purchase button.
     * @return The Button for adding purchases
     */
    public Button getAddButton() {
        return addButton;
    }
    /**
     * Returns the receipt generation button.
     * @return The Button for generating receipts
     */
    public Button getReceiptButton(){
        return receiptButton;
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
}
