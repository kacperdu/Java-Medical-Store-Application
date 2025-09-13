package Project.Controller;

import Project.Model.LoginModel;
import Project.View.LoginView;
import javafx.stage.Stage;

/**
 * Controller class responsible for managing user authentication.
 * Handles user login operations and manages the communication between the login view and model.
 */
public class LoginController {
    private LoginModel model;
    private LoginView view;
    private Stage stage;

    /**
     * Constructs a new login controller with the specified model and view.
     * @param model The login model to use
     * @param view The login view to use
     */
    public LoginController(LoginModel model, LoginView view) {
        this.model = model;
        this.view = view;
        initializeActions();
    }

    /**
     * Initializes the login button action.
     * Sets up the event handler for the login process.
     */
    private void initializeActions() {
        view.getLoginButton().setOnAction(e -> {
            String email = view.getUsernameField().getText().trim();
            String password = view.getPasswordField().getText().trim();
            
            if (email.isEmpty() || password.isEmpty()) {
                view.showMessage("Please enter both email and password.");
                return;
            }
            
            if (model.login(email, password)) {
                if (stage != null) {
                    stage.close();
                }
            } else {
                view.showMessage("Invalid email or password. Please try again.");
            }
        });
    }
} 