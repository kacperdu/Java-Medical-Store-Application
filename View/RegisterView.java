package Project.View;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * View class responsible for displaying the user registration interface.
 * Handles the layout and styling of registration form components including
 * input fields, registration button, and message display.
 */
public class RegisterView {
    private VBox view;
    private TextField nameField;
    private TextField emailField;
    private PasswordField passwordField;
    private TextField ppsField;
    private Button registerButton;
    private Label messageLabel;

    /**
     * Constructs a new registration view with styled UI components.
     * Initializes the layout, input fields, registration button, and message label.
     */
    public RegisterView() {
        view = new VBox(20);
        view.setPadding(new Insets(20));
        view.setAlignment(Pos.TOP_CENTER);
        view.setStyle("-fx-background-color: #f5f5f5;");

        // Create header
        Label header = new Label("Register");
        header.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        header.setStyle("-fx-text-fill: #2c3e50;");

        // Create form fields
        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(15);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

        // Initialize and style text fields
        nameField = new TextField();
        emailField = new TextField();
        passwordField = new PasswordField();
        ppsField = new TextField();

        // Style text fields
        styleTextField(nameField, "Enter name");
        styleTextField(emailField, "Enter email");
        styleTextField(passwordField, "Enter password");
        styleTextField(ppsField, "Enter PPS number");

        // Add form fields with styled labels
        addFormField(form, "Name:", nameField, 0);
        addFormField(form, "Email:", emailField, 1);
        addFormField(form, "Password:", passwordField, 2);
        addFormField(form, "PPS Number:", ppsField, 3);

        // Initialize and style register button
        registerButton = new Button("Register");
        styleButton(registerButton, "#2ecc71");

        // Create button container
        VBox buttonContainer = new VBox(10);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.getChildren().add(registerButton);

        // Initialize and style message label
        messageLabel = new Label("");
        messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        messageLabel.setAlignment(Pos.CENTER);
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(300);

        // Add all components to main view
        view.getChildren().addAll(header, form, buttonContainer, messageLabel);
    }

    /**
     * Styles a TextField with consistent appearance.
     * @param field The TextField to style
     * @param prompt The prompt text to display
     */
    private void styleTextField(TextField field, String prompt) {
        field.setPromptText(prompt);
        field.setPrefWidth(250);
        field.setStyle("-fx-background-radius: 5; -fx-border-radius: 5;");
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
                       "-fx-background-radius: 5;");
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
     * @return The TextField for user name
     */
    public TextField getNameField() { return nameField; }

    /**
     * Returns the email input field.
     * @return The TextField for user email
     */
    public TextField getEmailField() { return emailField; }

    /**
     * Returns the password input field.
     * @return The PasswordField for user password
     */
    public PasswordField getPasswordField() { return passwordField; }

    /**
     * Returns the PPS number input field.
     * @return The TextField for user PPS number
     */
    public TextField getPpsField() { return ppsField; }

    /**
     * Returns the register button.
     * @return The Button for registration action
     */
    public Button getRegisterButton() { return registerButton; }

    /**
     * Returns the message label.
     * @return The Label for displaying messages
     */
    public Label getMessageLabel() { return messageLabel; }

    /**
     * Displays a message in the message label.
     * The message is automatically cleared after 3 seconds.
     * @param message The message to display
     */
    public void showMessage(String message) {
        messageLabel.setText(message);
        // Clear the message after 3 seconds
        new java.util.Timer().schedule(
            new java.util.TimerTask() {
                @Override
                public void run() {
                    javafx.application.Platform.runLater(() -> messageLabel.setText(""));
                }
            },
            3000
        );
    }

    /**
     * Returns the main view component.
     * @return The VBox containing the registration view
     */
    public VBox getView() { return view; }
} 