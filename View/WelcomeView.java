package Project.View;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * View class responsible for displaying the welcome screen interface.
 * Handles the layout and styling of the welcome screen including
 * welcome message and authentication buttons.
 */
public class WelcomeView {
    private VBox view;
    private Label welcomeLabel;
    private Button loginButton;
    private Button registerButton;

    /**
     * Constructs a new welcome view with styled UI components.
     * Initializes the layout, welcome message, and authentication buttons.
     */
    public WelcomeView() {
        view = new VBox(30);
        view.setPadding(new Insets(40));
        view.setAlignment(Pos.CENTER);
        view.setStyle("-fx-background-color: #f5f5f5;");

        // Create welcome label
        welcomeLabel = new Label("Welcome to the Store Management System");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        welcomeLabel.setStyle("-fx-text-fill: #2c3e50; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

        // Create auth buttons
        loginButton = new Button("Login");
        registerButton = new Button("Register New Account");

        // Style buttons
        styleButton(loginButton, "#2ecc71");
        styleButton(registerButton, "#3498db");

        // Create button container
        VBox buttonBox = new VBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setStyle("-fx-background-color: white; " +
                         "-fx-background-radius: 15; " +
                         "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 15, 0, 0, 0); " +
                         "-fx-border-color: #e0e0e0; " +
                         "-fx-border-width: 1; " +
                         "-fx-border-radius: 15;");
        buttonBox.setPadding(new Insets(30));
        buttonBox.getChildren().addAll(loginButton, registerButton);

        // Add all components to main view
        view.getChildren().addAll(welcomeLabel, buttonBox);
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
                "-fx-background-radius: 8; " +
                "-fx-font-size: 16px; " +
                "-fx-font-family: Arial; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0); " +
                "-fx-cursor: hand;");
        button.setPrefWidth(300);
        button.setPrefHeight(50);
    }

    /**
     * Returns the login button.
     * @return The Button for login action
     */
    public Button getLoginButton() {return loginButton;}

    /**
     * Returns the register button.
     * @return The Button for registration action
     */
    public Button getRegisterButton() {return registerButton;}

    /**
     * Returns the main view component.
     * @return The VBox containing the welcome view
     */
    public VBox getView() {
        return view;
    }
} 