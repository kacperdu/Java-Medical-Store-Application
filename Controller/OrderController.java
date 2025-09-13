package Project.Controller;

import Project.Model.Customer;
import Project.Model.OrderModel;
import Project.View.OrderView;
import javafx.scene.layout.VBox;

/**
 * Controller class responsible for managing order-related operations.
 * Handles user interactions for making purchases, generating receipts, and managing orders.
 * Manages the communication between the order view and model.
 */
public class OrderController {
    // Field
    private OrderModel model;
    private OrderView view;

    // Constructor

    /**
     * Constructs a new order controller with the specified model and view.
     * @param model The order model to use
     * @param view The order view to use
     */
    public OrderController(OrderModel model, OrderView view) {
        this.model = model;
        this.view = view;
        initializeView();
        setupEventHandlers();
    }

    // Methods

    /**
     * Initializes the view with customer and product dropdowns.
     */
    private void initializeView() {
        view.populateCustomerDropdown(model.getCustomers());
        view.populateProductDropdown(model.getProducts());
    }

    /**
     * Sets up event handlers for all view buttons and dropdowns.
     * Each control is assigned its corresponding action method.
     */
    private void setupEventHandlers() {
        // Each button is set an action which calls a method when clicked
        view.getAddButton().setOnAction(e -> handlePurchase());
        view.getReceiptButton().setOnAction(e -> generateReceipt());
        view.getSortButton().setOnAction(e -> sortOrders());
        view.getStockCheckButton().setOnAction(e -> checkStock());
        view.getSalesCheckButton().setOnAction(e -> monthlySalesReport());
        view.getCustomerDropdown().setOnAction(e -> showCustomerPurchases());
        view.getClearButton().setOnAction(e -> view.clearTextArea());
    }

    /**
     * Handles the purchase process for a selected customer and product.
     * Validates selections and processes the purchase through the model.
     * Displays appropriate success or error messages.
     */
    private void handlePurchase() {
        String customerSelection = view.getCustomerDropdown().getValue();
        String productSelection = view.getProductDropdown().getValue();
        
        if (customerSelection == null || productSelection == null) {
            view.eventMessage("Please select both customer and product.");
            return;
        }
        
        String pps = customerSelection.substring(customerSelection.indexOf("(") + 1, customerSelection.indexOf(")"));
        String productName = productSelection.substring(0, productSelection.indexOf(" ($"));
        
        String message = model.handlePurchase(pps, productName);
        view.eventMessage(message);
    }

    /**
     * Generates a receipt for the selected customer.
     * Displays a formatted receipt with order details.
     */
    private void generateReceipt() {
        String customerSelection = view.getCustomerDropdown().getValue();
        if (customerSelection == null) {
            view.eventMessage("Please select a customer to generate a receipt.");
            return;
        }
        
        String pps = customerSelection.substring(customerSelection.indexOf("(") + 1, customerSelection.indexOf(")"));
        Customer customer = model.findCustomer(pps);
        if (customer == null) {
            view.eventMessage("Customer not found.");
            return;
        }
        
        String receipt = model.generateReceipt(customer);
        view.eventMessage(receipt);
    }

    /**
     * Sorts orders based on the selected criteria.
     * Displays the sorted list of orders.
     */
    private void sortOrders() {
        String sortType = view.getSortDropdown().getValue();
        if (sortType == null) {
            view.eventMessage("Please select a sort type.");
            return;
        }
        String result = model.sortOrders(sortType);
        view.eventMessage(result);
    }

    /**
     * Checks the stock level for the selected product.
     * Displays the current stock information.
     */
    private void checkStock() {
        String productSelection = view.getProductDropdown().getValue();
        if (productSelection != null) {
            String productName = productSelection.substring(0, productSelection.indexOf(" ($"));
            String message = model.stockInfo(productName);
            view.eventMessage(message);
        }
    }

    /**
     * Generates a monthly sales report.
     * Displays the total number of products sold in the last month.
     */
    private void monthlySalesReport() {
        String report = model.salesReportLastMonth();
        view.eventMessage(report);
    }

    /**
     * Shows the purchases made by the selected customer in the last month.
     * Displays a detailed list of purchases.
     */
    private void showCustomerPurchases() {
        String customerSelection = view.getCustomerDropdown().getValue();
        if (customerSelection != null) {
            String pps = customerSelection.substring(customerSelection.indexOf("(") + 1, customerSelection.indexOf(")"));
            String report = model.getCustomerPurchasesLastMonth(pps);
            view.eventMessage(report);
        }
    }

    /**
     * Returns the main view component of the order interface.
     * @return The VBox containing the order view
     */
    public VBox getVbox() {
        return view.getView();
    }
}
