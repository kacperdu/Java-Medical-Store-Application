package Project.Controller;

import Project.Model.Customer;
import Project.Model.Order;
import Project.Model.OrderModel;
import Project.Model.Product;
import Project.View.OrderManagementView;

/**
 * Controller class responsible for managing order management operations.
 * Handles user interactions for checking, removing, listing, and saving orders.
 * Manages the communication between the order management view and model.
 */
public class OrderManagementController {
    private OrderModel model;
    private OrderManagementView view;

    /**
     * Constructs a new order management controller with the specified model and view.
     * @param model The order model to use
     * @param view The order management view to use
     */
    public OrderManagementController(OrderModel model, OrderManagementView view) {
        this.model = model;
        this.view = view;
        initializeView();
        setupEventHandlers();
    }

    /**
     * Initializes the view with customer dropdown.
     */
    private void initializeView() {
        view.populateCustomerDropdown(model.getCustomers());
    }

    /**
     * Sets up event handlers for all view buttons.
     * Each button is assigned its corresponding action method.
     */
    private void setupEventHandlers() {
        view.getCheckOrdersButton().setOnAction(e -> checkOrders());
        view.getRemoveButton().setOnAction(e -> removeOrder());
        view.getListButton().setOnAction(e -> listOrders());
        view.getSaveButton().setOnAction(e -> saveOrders());
        view.getLoadButton().setOnAction(e -> loadOrders());
        view.getClearButton().setOnAction(e -> view.clearTextArea());
    }

    /**
     * Checks and displays orders for the selected customer.
     * Updates the order dropdown with the customer's orders.
     */
    private void checkOrders() {
        Customer customer = view.getCustomerDropdown().getValue();
        if (customer == null) {
            view.eventMessage("Please select a customer first.");
            return;
        }

        // Get all orders for the selected customer
        view.populateOrderDropdown(customer.getOrders());
        view.eventMessage("Orders loaded for " + customer.getName());
    }

    /**
     * Removes the selected order from the customer's order list.
     * Updates the view and saves changes to file.
     */
    private void removeOrder() {
        Customer customer = view.getCustomerDropdown().getValue();
        String orderSelection = view.getOrderDropdown().getValue();
        
        if (customer == null) {
            view.eventMessage("Please select a customer first.");
            return;
        }
        
        if (orderSelection == null) {
            view.eventMessage("Please select an order to remove.");
            return;
        }

        // Find the selected order in the customer's orders
        Order selectedOrder = null;
        for (Order order : customer.getOrders()) {
            if (orderSelection.contains("Order #" + order.getOrderId())) {
                selectedOrder = order;
                break;
            }
        }

        if (selectedOrder == null) {
            view.eventMessage("Order not found.");
            return;
        }
        // Remove the order
        if (model.removeOrderFromCustomer(customer, selectedOrder)) {
            view.eventMessage("Order #" + selectedOrder.getOrderId() + " removed successfully.");
            // Refresh the order dropdown
            checkOrders();
            // Save the changes
            if (model.saveOrders()) {
                view.eventMessage("Changes saved successfully.");
            } else {
                view.eventMessage("Failed to save changes.");
            }
        } else {
            view.eventMessage("Failed to remove order.");
        }
    }

    /**
     * Lists all orders for the selected customer.
     * Displays a formatted list of orders with their details.
     */
    private void listOrders() {
        Customer customer = view.getCustomerDropdown().getValue();
        if (customer == null) {
            view.eventMessage("Please select a customer first.");
            return;
        }

        StringBuilder ordersList = new StringBuilder();
        ordersList.append("\n=== ORDERS FOR ").append(customer.getName()).append(" ===\n");
        ordersList.append("================================\n\n");
        
        for (Order order : customer.getOrders()) {
            ordersList.append("Date: ").append(order.getDate()).append("\n");
            ordersList.append("Products:\n");
            for (Product product : order.getProducts()) {
                ordersList.append("- ").append(product.getName())
                         .append(": $").append(String.format("%.2f", product.getPrice()))
                         .append(" x").append(order.getQuantity()).append("\n");
            }
            ordersList.append("Total: $").append(String.format("%.2f", order.calculateTotal())).append("\n");
            ordersList.append("--------------------------------\n");
        }
        
        if (customer.getOrders().isEmpty()) {
            ordersList.append("No orders found.\n");
        }
        
        ordersList.append("\n================================\n");
        view.eventMessage(ordersList.toString());
    }

    /**
     * Saves the current list of orders to the database.
     * Uses the model's save functionality and displays appropriate messages.
     */
    private void saveOrders() {
        try {
            if (model.saveOrders()) {
                view.eventMessage("Orders saved successfully.");
            } else {
                view.eventMessage("Failed to save orders.");
            }
        } catch (Exception e) {
            view.eventMessage("Error saving orders: " + e.getMessage());
        }
    }

    /**
     * Loads orders from the database into the model.
     * Uses the model's load functionality and displays appropriate messages.
     * Refreshes the customer dropdown after loading.
     */
    private void loadOrders() {
        try {
            if (model.loadOrders()) {
                view.eventMessage("Orders loaded successfully.");
                // Refresh the customer dropdown
                view.populateCustomerDropdown(model.getCustomers());
            } else {
                view.eventMessage("Failed to load orders.");
            }
        } catch (Exception e) {
            view.eventMessage("Error loading orders: " + e.getMessage());
        }
    }
} 