package Project.View;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * View class responsible for displaying the product management interface.
 * Handles the layout and styling of product-related UI components including
 * input fields, action buttons, and event logging.
 */
public class ProdView {
    private VBox view;
    private TextField nameField = new TextField();
    private TextField priceField = new TextField();
    private TextField stockField = new TextField();
    private TextField removeProdField = new TextField();
    private Button addButton = new Button("Add Product");
    private Button removeButton = new Button("Remove Product");
    private Button listButton = new Button("List Products");
    private Button findButton = new Button("Find");
    private Button saveButton = new Button("Save");
    private Button loadButton = new Button("Load");
    private TextArea eventLog = new TextArea();

    /**
     * Constructs a new product view with styled UI components.
     * Initializes the layout, input fields, buttons, and event log.
     */
    public ProdView() {
        view = new VBox(20);
        view.setPadding(new Insets(20));
        view.setAlignment(Pos.TOP_CENTER);
        view.setStyle("-fx-background-color: #f5f5f5;");

        // Create header
        Label header = new Label("Product Management");
        header.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        header.setStyle("-fx-text-fill: #2c3e50;");

        // Create form fields
        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(15);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

        // Style text fields
        styleTextField(nameField, "Product Name");
        styleTextField(priceField, "Price");
        styleTextField(stockField, "Stock");
        styleTextField(removeProdField, "Product Name to Remove");

        // Add form fields with styled labels
        addFormField(form, "Name:", nameField, 0);
        addFormField(form, "Price:", priceField, 1);
        addFormField(form, "Stock:", stockField, 2);
        addFormField(form, "Remove Product:", removeProdField, 3);

        // Create button section
        HBox actionButtons = new HBox(20);
        actionButtons.setAlignment(Pos.CENTER);
        styleButton(addButton, "#2ecc71");
        styleButton(removeButton, "#e74c3c");
        styleButton(listButton, "#3498db");
        actionButtons.getChildren().addAll(addButton, removeButton, listButton);

        HBox utilityButtons = new HBox(20);
        utilityButtons.setAlignment(Pos.CENTER);
        styleButton(saveButton, "#3498db");
        styleButton(loadButton, "#3498db");
        styleButton(findButton, "#3498db");
        utilityButtons.getChildren().addAll(saveButton, loadButton, findButton);

        // Style and add event log
        eventLog.setEditable(false);
        eventLog.setPrefRowCount(5);
        eventLog.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        eventLog.setPadding(new Insets(10));

        // Add all sections to the main view
        view.getChildren().addAll(header, form, actionButtons, utilityButtons, eventLog);
    }

    /**
     * Styles a TextField with consistent appearance.
     * @param field The TextField to style
     * @param prompt The prompt text to display
     */
    private void styleTextField(TextField field, String prompt) {
        field.setPromptText(prompt);
        field.setPrefWidth(250);
        field.setStyle("-fx-background-color: white; " +
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
     * Adds a form field to the grid layout.
     * @param form The GridPane to add the field to
     * @param labelText The label text for the field
     * @param field The TextField to add
     * @param row The row index in the grid
     */
    private void addFormField(GridPane form, String labelText, TextField field, int row) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        form.add(label, 0, row);
        form.add(field, 1, row);
    }

    /**
     * Returns the name input field.
     * @return The TextField for product name
     */
    public TextField getNameField() {
        return nameField;
    }

    /**
     * Returns the price input field.
     * @return The TextField for product price
     */
    public TextField getPriceField() {
        return priceField;
    }

    /**
     * Returns the stock input field.
     * @return The TextField for product stock
     */
    public TextField getStockField() { return stockField; }

    /**
     * Returns the product name field for product removal.
     * @return The TextField for entering product name to remove
     */
    public TextField getRemoveProdField() { return removeProdField; }

    /**
     * Returns the add product button.
     * @return The Button for adding products
     */
    public Button getAddButton() { return addButton; }

    /**
     * Returns the remove product button.
     * @return The Button for removing products
     */
    public Button getRemoveButton() { return removeButton; }

    /**
     * Returns the list products button.
     * @return The Button for listing products
     */
    public Button getListButton() { return listButton; }

    /**
     * Returns the find product button.
     * @return The Button for finding products
     */
    public Button getFindButton() { return findButton; }

    /**
     * Returns the save products button.
     * @return The Button for saving products
     */
    public Button getSaveButton() { return saveButton; }

    /**
     * Returns the load products button.
     * @return The Button for loading products
     */
    public Button getLoadButton() { return loadButton; }

    /**
     * Clears all input fields.
     */
    public void clearFields() {
        nameField.clear();
        priceField.clear();
        stockField.clear();
        removeProdField.clear();
    }

    /**
     * Appends a message to the event log.
     * @param message The message to display
     */
    public void eventMessage(String message) {
        eventLog.appendText(message + "\n");
    }

    /**
     * Returns the main view component.
     * @return The VBox containing the product view
     */
    public VBox getView() { return view; }
}
