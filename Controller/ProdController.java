package Project.Controller;

import Project.Model.ProdModel;
import Project.Model.Product;
import Project.View.ProdView;
import javafx.scene.layout.VBox;

/**
 * Controller class responsible for managing product-related operations.
 * Handles user interactions for adding, removing, finding, and listing products.
 * Manages the communication between the product view and model.
 */
public class ProdController {

    // Field

    private ProdModel model;
    private ProdView view;

    // Constructor

    /**
     * Constructs a new product controller with the specified model and view.
     * @param model The product model to use
     * @param view The product view to use
     */
    public ProdController(ProdModel model, ProdView view) {
        this.model = model;
        this.view = view;
        setupEventHandlers();
    }

    // Methods

    /**
     * Sets up event handlers for all view buttons.
     * Each button is assigned its corresponding action method.
     */
    private void setupEventHandlers() {
        // Each button is set an action which calls a method when clicked
        view.getAddButton().setOnAction(e -> addProduct());
        view.getRemoveButton().setOnAction(e -> removeProduct());
        view.getListButton().setOnAction(e -> listProducts());
        view.getFindButton().setOnAction(e -> findProduct());
        view.getSaveButton().setOnAction(e -> saveProducts());
        view.getLoadButton().setOnAction(e -> loadProducts());
    }

    /**
     * Adds a new product using the information entered in the view.
     * Validates input fields and creates a new product if all fields are valid.
     * Displays appropriate success or error messages.
     */
    private void addProduct() {
        String name = view.getNameField().getText();
        String priceText = view.getPriceField().getText();
        String stockText = view.getStockField().getText();
        
        if (name.isEmpty() || priceText.isEmpty() || stockText.isEmpty()) {
            view.eventMessage("\nError: Please fill in all fields.\n");
            return;
        }
        
        try {
            double price = Double.parseDouble(priceText);
            int stock = Integer.parseInt(stockText);

            if (price <= 0 || stock < 0) {
                view.eventMessage("\nError: Price must be positive and stock cannot be negative.\n");
                return;
            }
            
            Product product = new Product.Builder(name, price)
                .stock(stock)
                .build();
            if (model.addProd(product)) {
                view.eventMessage("\nProduct added successfully.\n");
                view.clearFields();
            } else {
                view.eventMessage("\nError: Product with this name already exists.\n");
            }
        } catch (NumberFormatException e) {
            view.eventMessage("\nError: Please enter valid numbers for price and stock.\n");
        }
    }

    /**
     * Removes a product based on the name entered in the view.
     * Displays appropriate success or error messages.
     */
    private void removeProduct() {
        String name = view.getRemoveProdField().getText();
        if (name.isEmpty()) {
            view.eventMessage("\nError: Please enter a product name to remove.\n");
            return;
        }
        
        if (model.removeProd(name)) {
            view.eventMessage("\nProduct removed successfully.\n");
            view.clearFields();
        } else {
            view.eventMessage("\nError: Product not found.\n");
        }
    }

    /**
     * Finds a product based on the name entered in the view.
     * If found, displays the product's details in the view.
     * Displays appropriate error messages if the product is not found.
     */
    private void findProduct() {
        String name = view.getNameField().getText();
        if (name.isEmpty()) {
            view.eventMessage("\nError: Please enter a product name to find.\n");
            return;
        }
        
        Product product = model.findInList(name);
        if (product != null) {
            view.eventMessage("\nProduct Details:\n" +
                            "----------------\n" +
                            "Name:  " + product.getName() + "\n" +
                            "Price: $" + String.format("%.2f", product.getPrice()) + "\n" +
                            "Stock: " + product.getStock() + "\n");
        } else {
            view.eventMessage("\nError: Product not found.\n");
        }
    }

    /**
     * Saves the current list of products to a file.
     * Uses the model's save functionality and displays appropriate messages.
     */
    private void saveProducts() {
        try {
            model.saveProducts();
            view.eventMessage("\nProducts saved successfully.\n");
        } catch (Exception e) {
            view.eventMessage("\nError: Failed to save products.\n");
        }
    }

    /**
     * Loads products from a file into the model.
     * Uses the model's load functionality and displays appropriate messages.
     */
    private void loadProducts() {
        try {
            model.loadProducts();
            view.eventMessage("\nProducts loaded successfully.\n");
        } catch (Exception e) {
            view.eventMessage("\nError: Failed to load products.\n");
        }
    }

    /**
     * Lists all products in the system.
     * Displays a formatted list of products with their details.
     */
    private void listProducts() {
        StringBuilder productsList = new StringBuilder();
        productsList.append("\n=== PRODUCT LIST ===\n");
        productsList.append("====================\n\n");
        
        for (Product product : model.getProdList()) {
            productsList.append("Product: ").append(product.getName()).append("\n");
            productsList.append("  Price: $").append(String.format("%.2f", product.getPrice())).append("\n");
            productsList.append("  Stock: ").append(product.getStock()).append("\n");
            productsList.append("  --------------------\n");
        }
        
        if (model.getProdList().isEmpty()) {
            productsList.append("No products available in the system.\n");
        }
        
        productsList.append("\n====================\n");
        view.eventMessage(productsList.toString());
    }

    /**
     * Returns the main view component of the product interface.
     * @return The VBox containing the product view
     */
    public VBox getVbox() {
        return view.getView();
    }

}