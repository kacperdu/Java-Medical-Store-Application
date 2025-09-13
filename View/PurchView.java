package Project.View;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class PurchView {
    private VBox mainView;
    private ComboBox<String> productComboBox;
    private TextField quantityField;
    private Button purchaseButton;
    private Button listButton;
    private TextArea eventTextArea;

    public PurchView() {
        // Create main container
        mainView = new VBox(20);
        mainView.setPadding(new Insets(20));
        mainView.setStyle("-fx-background-color: #f5f5f5;");

        // Create header
        Label headerLabel = new Label("Purchase Products");
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        headerLabel.setTextFill(Color.web("#2c3e50"));
        headerLabel.setAlignment(Pos.CENTER);

        // Create form container
        VBox formContainer = new VBox(15);
        formContainer.setPadding(new Insets(20));
        formContainer.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        // Create product selection
        HBox productBox = new HBox(10);
        productBox.setAlignment(Pos.CENTER_LEFT);
        Label productLabel = new Label("Product:");
        productLabel.setFont(Font.font("Arial", 14));
        productComboBox = new ComboBox<>();
        productComboBox.setPrefWidth(200);
        productBox.getChildren().addAll(productLabel, productComboBox);

        // Create quantity input
        HBox quantityBox = new HBox(10);
        quantityBox.setAlignment(Pos.CENTER_LEFT);
        Label quantityLabel = new Label("Quantity:");
        quantityLabel.setFont(Font.font("Arial", 14));
        quantityField = new TextField();
        quantityField.setPrefWidth(100);
        quantityBox.getChildren().addAll(quantityLabel, quantityField);

        // Create buttons container
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        // Create purchase button
        purchaseButton = new Button("Purchase");
        styleButton(purchaseButton, "#2ecc71");

        // Create list button
        listButton = new Button("List Purchases");
        styleButton(listButton, "#3498db");

        buttonBox.getChildren().addAll(purchaseButton, listButton);

        // Create event text area
        eventTextArea = new TextArea();
        eventTextArea.setEditable(false);
        eventTextArea.setPrefHeight(200);
        eventTextArea.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 14;");

        // Add all components to form container
        formContainer.getChildren().addAll(productBox, quantityBox, buttonBox, eventTextArea);

        // Add all components to main view
        mainView.getChildren().addAll(headerLabel, formContainer);
    }

    private void styleButton(Button button, String color) {
        button.setStyle("-fx-background-color: " + color + "; " +
                       "-fx-text-fill: white; " +
                       "-fx-font-weight: bold; " +
                       "-fx-padding: 10 20; " +
                       "-fx-background-radius: 5;");
        button.setPrefWidth(150);
    }

    // Getters

    public ComboBox<String> getProductComboBox() {
        return productComboBox;
    }

    public TextField getQuantityField() {
        return quantityField;
    }

    public Button getPurchaseButton() {
        return purchaseButton;
    }

    public Button getListButton() {
        return listButton;
    }

    public void eventMessage(String message) {
        eventTextArea.appendText(message + "\n");
    }

    public void clearFields() {
        productComboBox.getSelectionModel().clearSelection();
        quantityField.clear();
    }

    public VBox getView() {
        return mainView;
    }
} 