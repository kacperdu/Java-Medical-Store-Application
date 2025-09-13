package Project;

import Project.Controller.*;
import Project.Model.*;
import Project.View.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.util.ArrayList;
import java.util.Optional;
import javafx.scene.layout.VBox;

/**
 * Main application class for the Management System.
 * Handles initialization of MVC components and main UI setup.
 */
public class Main extends Application {
    private CustModel custModel;
    private ProdModel prodModel;
    private OrderModel orderModel;
    private LoginModel loginModel;
    private RegisterModel registerModel;
    private TabPane tabPane;
    private Tab customerTab;
    private Tab productTab;
    private Tab orderTab;
    private Tab purchaseTab;
    private Tab orderManagementTab;
    private MenuBar menuBar;
    private StoreDataManager storeDataManager;
    private boolean useSerialization = false; // Flag to track storage mode

    /**
     * JavaFX start method. Initializes models, views, controllers, and sets up the main UI.
     * @param primaryStage The primary stage for this application.
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize StoreDataManager singleton first
            storeDataManager = StoreDataManager.getInstance();
            
            // Show dialog to choose storage mode
            Alert modeAlert = new Alert(Alert.AlertType.NONE);
            modeAlert.setTitle("Choose Storage Mode");
            modeAlert.setHeaderText("How would you like to store your data?");
            
            // Create custom buttons
            ButtonType databaseButton = new ButtonType("Database");
            ButtonType textFileButton = new ButtonType("Text Files");
            ButtonType serializationButton = new ButtonType("Serialization");
            modeAlert.getButtonTypes().setAll(databaseButton, textFileButton, serializationButton);
            
            Optional<ButtonType> modeResult = modeAlert.showAndWait();
            if (modeResult.isPresent()) {
                ButtonType selectedButton = modeResult.get();
                if (selectedButton == databaseButton) {
                    storeDataManager.setDatabaseMode(true);
                    useSerialization = false;
                } else if (selectedButton == textFileButton) {
                    storeDataManager.setDatabaseMode(false);
                    useSerialization = false;
                } else if (selectedButton == serializationButton) {
                    storeDataManager.setDatabaseMode(false);
                    useSerialization = true;
                }
            } else {
                // Default to text files if dialog is closed
                storeDataManager.setDatabaseMode(false);
                useSerialization = false;
            }
            
            // Initialize models with empty lists
            custModel = new CustModel(new ArrayList<>());
            prodModel = new ProdModel(new ArrayList<>());
            orderModel = new OrderModel(custModel, prodModel);
            
            // Set serialization mode for all models
            custModel.setSerializationMode(useSerialization);
            prodModel.setSerializationMode(useSerialization);
            orderModel.setSerializationMode(useSerialization);
            
            // Initialize authentication models
            ArrayList<Customer> customers = new ArrayList<>();
            loginModel = new LoginModel(customers);
            registerModel = new RegisterModel(customers, "register.txt", "register.ser");

            // Show dialog to choose loading method
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Choose Loading Method");
            alert.setHeaderText("How would you like to load the data?");
            alert.setContentText("Choose 'OK' to load from " + (storeDataManager.isDatabaseMode() ? "database" : (useSerialization ? "serialized files" : "text files")) + 
                               ", 'Cancel' to start with empty data");
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                loadStoreData();
            } else {
                System.out.println("Starting with empty data");
                Alert feedback = new Alert(Alert.AlertType.INFORMATION);
                feedback.setTitle("Load Result");
                feedback.setHeaderText("Empty Data");
                feedback.setContentText("Starting with empty data");
                feedback.showAndWait();
            }

            // Initialize views for all main sections
            WelcomeView welcomeView = new WelcomeView();
            CustView custView = new CustView();
            ProdView prodView = new ProdView();
            OrderView orderView = new OrderView();
            PurchView purchView = new PurchView();
            OrderManagementView orderManagementView = new OrderManagementView();
            
            // Initialize controllers for each section
            CustController custController = new CustController(custModel, custView);
            ProdController prodController = new ProdController(prodModel, prodView);
            OrderController orderController = new OrderController(orderModel, orderView);
            PurchController purchController = new PurchController(orderModel, purchView);
            OrderManagementController orderManagementController = new OrderManagementController(orderModel, orderManagementView);
            
            // Create TabPane for navigation
            tabPane = new TabPane();
            
            // Create Tabs for each section
            customerTab = new Tab("Customer Management", custView.getView());
            productTab = new Tab("Product Management", prodView.getView());
            orderTab = new Tab("Order History", orderView.getView());
            purchaseTab = new Tab("Purchase", purchView.getView());
            orderManagementTab = new Tab("Order Management", orderManagementView.getView());
            
            // Initially disable management tabs for security
            customerTab.setDisable(true);
            productTab.setDisable(true);
            orderTab.setDisable(true);
            orderManagementTab.setDisable(true);
            
            // Add tabs to TabPane
            tabPane.getTabs().addAll(customerTab, productTab, orderManagementTab, orderTab, purchaseTab);
            
            // Create menu bar and file menu
            menuBar = new MenuBar();
            Menu fileMenu = new Menu("File");
            
            // Menu item to save store data
            MenuItem saveMenuItem = new MenuItem("Save Store Data");
            saveMenuItem.setOnAction(e -> saveStoreData());
            
            // Menu item to test database connection
            MenuItem testConnectionMenuItem = new MenuItem("Test Database Connection");
            testConnectionMenuItem.setOnAction(e -> {
                boolean connected = storeDataManager.testConnection();
                Alert connectionAlert = new Alert(AlertType.INFORMATION);
                connectionAlert.setTitle("Database Connection Test");
                connectionAlert.setHeaderText(connected ? "Connection Successful" : "Connection Failed");
                connectionAlert.setContentText(connected ? 
                    "Successfully connected to the database!" : 
                    "Failed to connect to the database. Please check your database settings and ensure the database is running.");
                connectionAlert.showAndWait();
            });
            
            // Menu item to load store data
            MenuItem loadMenuItem = new MenuItem("Load Store Data");
            loadMenuItem.setOnAction(e -> loadStoreData());
            
            fileMenu.getItems().addAll(saveMenuItem, testConnectionMenuItem, loadMenuItem);
            menuBar.getMenus().add(fileMenu);
            
            // Create main layout with menu and tabs
            VBox mainLayout = new VBox(menuBar, tabPane);
            
            // Set up welcome view actions for login
            welcomeView.getLoginButton().setOnAction(e -> {
                Stage loginStage = new Stage();
                LoginView loginView = new LoginView();
                loginStage.setTitle("Login");
                loginStage.setScene(new Scene(loginView.getView(), 400, 300));
                loginStage.show();
                
                loginView.getLoginButton().setOnAction(loginEvent -> {
                    String email = loginView.getUsernameField().getText();
                    String password = loginView.getPasswordField().getText();
                    
                    if (loginModel.login(email, password)) {
                        loginStage.close();
                        if (loginModel.isAdmin()) {
                            // Enable management tabs for admin
                            customerTab.setDisable(false);
                            productTab.setDisable(false);
                            orderTab.setDisable(false);
                            orderManagementTab.setDisable(false);
                            purchaseTab.setDisable(true);
                            tabPane.getSelectionModel().select(customerTab);
                            // Only set customers, don't reload orders
                            orderModel.setAllCustomers(custModel.getCustList());
                        } else {
                            // Only enable purchase tab for regular users
                            customerTab.setDisable(true);
                            productTab.setDisable(true);
                            orderTab.setDisable(true);
                            orderManagementTab.setDisable(true);
                            purchaseTab.setDisable(false);
                            tabPane.getSelectionModel().select(purchaseTab);
                            // Set current customer and load their orders
                            orderModel.setCurrentCustomer(loginModel.getCurrentCustomer());
                        }
                        primaryStage.setScene(new Scene(mainLayout, 800, 600));
                    } else {
                        loginView.showMessage("Invalid credentials. Please try again.");
                    }
                });
            });
            
            // Set up welcome view actions for registration
            welcomeView.getRegisterButton().setOnAction(e -> {
                Stage registerStage = new Stage();
                RegisterView registerView = new RegisterView();
                RegisterController registerController = new RegisterController(loginModel.getCustomers(), registerView, custModel, loginModel);
                registerController.setStage(registerStage);
                registerStage.setTitle("Register");
                registerStage.setScene(new Scene(registerView.getView(), 400, 300));
                registerStage.show();
            });
            
            // Set up the main stage and show the welcome view first
            primaryStage.setTitle("Store Management System");
            primaryStage.setScene(new Scene(welcomeView.getView(), 800, 600));
            primaryStage.show();
            
            // Handle window close event to save data
            primaryStage.setOnCloseRequest(this::handleCloseRequest);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the window close event. Saves all data to the database.
     * @param event The window event.
     */
    private void handleCloseRequest(WindowEvent event) {
        try {
            // Ask user if they want to save before closing
            Alert saveAlert = new Alert(Alert.AlertType.CONFIRMATION);
            saveAlert.setTitle("Save Before Closing");
            saveAlert.setHeaderText("Would you like to save the store data before closing?");
            saveAlert.setContentText("Choose 'OK' to save to " + 
                                   (storeDataManager.isDatabaseMode() ? "database" : (useSerialization ? "serialized files" : "text files")) + 
                                   ", 'Cancel' to close without saving");
            
            Optional<ButtonType> saveResult = saveAlert.showAndWait();
            if (saveResult.isPresent() && saveResult.get() == ButtonType.OK) {
                // Save data using the appropriate mode
                custModel.saveCustomers();
                prodModel.saveProducts();
                orderModel.saveOrders();
                
                System.out.println("Successfully saved all data to " + 
                                 (storeDataManager.isDatabaseMode() ? "database" : (useSerialization ? "serialized files" : "text files")));
                Alert feedback = new Alert(Alert.AlertType.INFORMATION);
                feedback.setTitle("Save Result");
                feedback.setHeaderText("Success");
                feedback.setContentText("Data has been successfully saved to " + 
                                      (storeDataManager.isDatabaseMode() ? "database" : (useSerialization ? "serialized files" : "text files")));
                feedback.showAndWait();
            }
        } catch (Exception e) {
            System.out.println("Error during save: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Saves the current store data to the database and shows a popup with the result.
     */
    private void saveStoreData() {
        // Save data using the appropriate mode
        custModel.saveCustomers();
        prodModel.saveProducts();
        orderModel.saveOrders();
        
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Save Store Data");
        alert.setHeaderText("Success");
        alert.setContentText("Store data has been saved successfully to " + 
                           (storeDataManager.isDatabaseMode() ? "database" : (useSerialization ? "serialized files" : "text files")));
        alert.showAndWait();
    }
    
    /**
     * Loads store data from the database and updates the models. Shows a popup with the result.
     */
    private void loadStoreData() {
        // Load data using the appropriate mode
        custModel.loadCustomers();
        prodModel.loadProducts();
        orderModel.loadOrders();
        
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Load Store Data");
        alert.setHeaderText("Success");
        alert.setContentText("Store data has been loaded successfully from " + 
                           (storeDataManager.isDatabaseMode() ? "database" : (useSerialization ? "serialized files" : "text files")));
        alert.showAndWait();
    }

    /**
     * Main entry point of the application.
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
