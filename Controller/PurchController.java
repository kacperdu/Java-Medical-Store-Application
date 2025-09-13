package Project.Controller;

import Project.Model.OrderModel;
import Project.View.PurchView;
import Project.Model.Order;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;

/**
 * Controller class responsible for managing purchase-related operations.
 * Handles user interactions for making purchases and viewing purchase history.
 * Manages the communication between the purchase view and order model.
 */
public class PurchController {
    // Fields
    private OrderModel model;  // Reference to the order model for business logic
    private PurchView view;    // Reference to the purchase view for UI interactions

    /**
     * Constructs a new purchase controller with the specified model and view.
     * Initializes the view components and sets up event handlers.
     * 
     * @param model The order model to use for business logic
     * @param view The purchase view to use for UI interactions
     */
    public PurchController(OrderModel model, PurchView view) {
        this.model = model;
        this.view = view;
        initializeActions();
        updateProductList();
    }

    /**
     * Initializes event handlers for the view components.
     * Sets up actions for the purchase and list buttons.
     */
    private void initializeActions() {
        // Set up purchase button action
        view.getPurchaseButton().setOnAction(e -> makePurchase());
        
        // Set up list button action
        view.getListButton().setOnAction(e -> listPurchases());
    }

    /**
     * Updates the product list in the view's combo box.
     * Retrieves all products from the model and formats them for display.
     */
    private void updateProductList() {
        // Create an observable list to hold the formatted product names
        ObservableList<String> productNames = FXCollections.observableArrayList();
        
        // Format each product as "name - $price" and add to the list
        model.getProdModel().getProdList().forEach(product -> 
            productNames.add(product.getName() + " - $" + String.format("%.2f", product.getPrice()))
        );
        
        // Update the combo box with the formatted product list
        view.getProductComboBox().setItems(productNames);
    }

    /**
     * Handles the purchase process when the purchase button is clicked.
     * Validates input, processes the purchase, and updates the UI accordingly.
     */
    private void makePurchase() {
        // Get the selected product and quantity from the view
        String selectedProduct = view.getProductComboBox().getValue();
        String quantityText = view.getQuantityField().getText();

        // Validate product selection
        if (selectedProduct == null || selectedProduct.isEmpty()) {
            view.eventMessage("Please select a product.");
            return;
        }

        // Validate quantity input
        if (quantityText.isEmpty()) {
            view.eventMessage("Please enter a quantity.");
            return;
        }

        try {
            // Parse and validate quantity
            int quantity = Integer.parseInt(quantityText);
            if (quantity <= 0) {
                view.eventMessage("Quantity must be greater than 0.");
                return;
            }

            // Extract product name from the combo box value (removes price information)
            String productName = selectedProduct.split(" - ")[0];
            
            // Process the purchase through the model
            if (model.makePurchase(productName, quantity)) {
                view.eventMessage("Purchase successful!");
                view.clearFields();
                updateProductList();
            } else {
                view.eventMessage("Purchase failed. Please check product availability.");
            }
        } catch (NumberFormatException e) {
            view.eventMessage("Please enter a valid number for quantity.");
        }
    }

    /**
     * Lists all purchases made by the current customer.
     * Displays a formatted list of purchases with details.
     */
    private void listPurchases() {
        // Check if a customer is logged in
        if (model.getCurrentCustomer() == null) {
            view.eventMessage("No customer logged in.");
            return;
        }

        // Create a formatted string to display purchases
        StringBuilder purchases = new StringBuilder();
        purchases.append("\n=== YOUR PURCHASES ===\n");
        purchases.append("=====================\n\n");
        
        // Get the customer's orders from the model
        List<Order> customerOrders = model.getCustomerPurchasesLastMonthOrders(model.getCurrentCustomer().getPps());
        
        // Check if there are any orders
        if (customerOrders == null || customerOrders.isEmpty()) {
            purchases.append("No purchases found.\n");
        } else {
            // Format each order's details
            for (Order order : customerOrders) {
                purchases.append("Date: ").append(order.getDate()).append("\n");
                purchases.append("Product: ").append(order.getProducts().get(0).getName()).append("\n");
                purchases.append("Quantity: ").append(order.getQuantity()).append("\n");
                purchases.append("Total: $").append(String.format("%.2f", order.calculateTotal())).append("\n");
                purchases.append("---------------------\n");
            }
        }
        
        // Add closing separator
        purchases.append("\n=====================\n");
        
        // Display the formatted purchases in the view
        view.eventMessage(purchases.toString());
    }
} 