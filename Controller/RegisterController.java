package Project.Controller;

import Project.Model.Customer;
import Project.Model.CustModel;
import Project.Model.LoginModel;
import Project.Model.StoreDataManager;
import Project.View.RegisterView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import java.util.ArrayList;

/**
 * Controller class responsible for handling user registration.
 * Manages the communication between the registration view and models.
 */
public class RegisterController {
    private ArrayList<Customer> customers;
    private RegisterView view;
    private CustModel custModel;
    private LoginModel loginModel;
    private Stage stage;

    /**
     * Constructs a new RegisterController with the specified parameters.
     * @param customers List of existing customers
     * @param view The registration view
     * @param custModel The customer model
     * @param loginModel The login model
     */
    public RegisterController(ArrayList<Customer> customers, RegisterView view, CustModel custModel, LoginModel loginModel) {
        this.customers = customers;
        this.view = view;
        this.custModel = custModel;
        this.loginModel = loginModel;
        setupEventHandlers();
    }

    /**
     * Sets the stage for this controller.
     * @param stage The stage to set
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void setupEventHandlers() {
        view.getRegisterButton().setOnAction(e -> register());
    }

    private void register() {
        try {
            String name = view.getNameField().getText().trim();
            String email = view.getEmailField().getText().trim();
            String password = view.getPasswordField().getText().trim();
            String pps = view.getPpsField().getText().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || pps.isEmpty()) {
                view.showMessage("All fields must be filled.");
                return;
            }

            // Check if email already exists
            for (Customer customer : customers) {
                if (customer.getEmail().equals(email)) {
                    view.showMessage("Email already registered.");
                    return;
                }
            }

            // Create new customer
            Customer newCustomer = new Customer.Builder(name, email, password, pps)
                .build();

            // Add to models
            if (custModel.addCust(newCustomer)) {
                loginModel.getCustomers().add(newCustomer);
                
                // Save to database using StoreDataManager
                if (StoreDataManager.getInstance().saveStoreData(
                    new ArrayList<>(custModel.getCustList()),
                    new ArrayList<>(),
                    new ArrayList<>()
                )) {
                    view.showMessage("Registration successful!");
                    stage.close();
                } else {
                    view.showMessage("Registration successful but failed to save to database.");
                }
            } else {
                view.showMessage("Failed to register customer.");
            }
        } catch (IllegalArgumentException e) {
            view.showMessage("Error: " + e.getMessage());
        } catch (Exception e) {
            view.showMessage("An error occurred during registration.");
        }
    }
} 