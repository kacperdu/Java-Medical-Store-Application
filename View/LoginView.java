package Project.View;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * View class responsible for displaying the user login interface.
 * Handles the layout and styling of login form components including
 * input fields, login button, and message display.
 */
public class LoginView {
    private VBox view;
    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginButton;
    private Label messageLabel;

    /**
     * Constructs a new login view with styled UI components.
     * Initializes the layout, input fields, login button, and message label.
     */
    public LoginView() {
        view = new VBox(20);
        view.setPadding(new Insets(20));
        view.setAlignment(Pos.TOP_CENTER);
        view.setStyle("-fx-background-color: #f5f5f5;");

        // Create header
        Label header = new Label("Login");
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
        usernameField = new TextField();
        passwordField = new PasswordField();

        // Style text fields
        styleTextField(usernameField, "Enter email");
        styleTextField(passwordField, "Enter password");

        // Add form fields with styled labels
        addFormField(form, "Email:", usernameField, 0);
        addFormField(form, "Password:", passwordField, 1);

        // Initialize and style login button
        loginButton = new Button("Login");
        styleButton(loginButton, "#2ecc71");

        // Create button container
        VBox buttonContainer = new VBox(10);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.getChildren().add(loginButton);

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
     * Displays a message in the message label.
     * @param message The message to display
     */
    public void showMessage(String message) {messageLabel.setText(message);}

    /**
     * Returns the username input field.
     * @return The TextField for username
     */
    public TextField getUsernameField() { return usernameField; }

    /**
     * Returns the password input field.
     * @return The PasswordField for password
     */
    public PasswordField getPasswordField() { return passwordField; }

    /**
     * Returns the login button.
     * @return The Button for login action
     */
    public Button getLoginButton() { return loginButton; }

    /**
     * Returns the message label.
     * @return The Label for displaying messages
     */
    public Label getMessageLabel() { return messageLabel; }

    /**
     * Returns the main view component.
     * @return The VBox containing the login view
     */
    public VBox getView() { return view; }

} 