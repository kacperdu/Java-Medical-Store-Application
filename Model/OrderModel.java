package Project.Model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import Project.DAO.OrderDAO;
import Project.DAO.OrderDAOImpl;

/**
 * Model class responsible for managing order data and operations.
 * Handles order list management and business logic.
 * Integrates with Customer and Product models for order processing.
 */
public class OrderModel {

    // Field

    private ArrayList<Order> orderList;
    private CustModel custModel;
    private ProdModel prodModel;
    private Customer currentCustomer;
    private OrderDAO orderDAO;
    private boolean serializationMode;

    // Constructor

    /**
     * Constructor to initialize the model with customer and product models.
     * @param custModel The customer model for order-customer relationships
     * @param prodModel The product model for order-product relationships
     */
    public OrderModel(CustModel custModel, ProdModel prodModel) {
        this.custModel = custModel;
        this.prodModel = prodModel;
        this.orderList = new ArrayList<>();
        this.orderDAO = new OrderDAOImpl();
        this.serializationMode = false; // Default to text file mode
    }

    /**
     * Sets the current customer and loads their orders.
     * @param customer The customer to set as current
     */
    public void setCurrentCustomer(Customer customer) {
        this.currentCustomer = customer;
        loadOrders();
    }

    /**
     * Updates the list of all customers in the model.
     * @param customers The new list of customers
     */
    public void setAllCustomers(List<Customer> customers) {
        this.custModel.setCustList(customers);
    }

    /**
     * Returns the current customer.
     * @return The current customer, or null if none is set
     */
    public Customer getCurrentCustomer() {
        return currentCustomer;
    }

    /**
     * Returns the customer model.
     * @return The customer model instance
     */
    public CustModel getCustModel() {
        return custModel;
    }

    /**
     * Returns the product model.
     * @return The product model instance
     */
    public ProdModel getProdModel() {
        return prodModel;
    }

    /**
     * Returns a list of all customers.
     * @return A list of all customers
     */
    public List<Customer> getCustomers() {
        return custModel.getCustList();
    }

    /**
     * Returns a list of all products.
     * @return A list of all products
     */
    public List<Product> getProducts() {
        return prodModel.getProdList();
    }

    /**
     * Finds a product by its name.
     * @param name The name to search for
     * @return The product if found, null otherwise
     */
    public Product findProduct(String name) {
        return prodModel.findInList(name);
    }

    /**
     * Finds a customer by their PPS number.
     * @param pps The PPS number to search for
     * @return The customer if found, null otherwise
     */
    public Customer findCustomer(String pps) {
        return custModel.findInList(pps);
    }

    /**
     * Returns a list of orders for a specific customer in the last month.
     * @param pps The PPS number of the customer
     * @return A list of orders for the specified customer
     */
    public List<Order> getCustomerPurchasesLastMonthOrders(String pps) {
        Customer customer = findCustomer(pps);
        if (customer != null) {
            // First try to get orders from the customer's list
            List<Order> customerOrders = customer.getOrders();
            if (customerOrders != null && !customerOrders.isEmpty()) {
                return customerOrders;
            }
            
            // If no orders in customer's list, try to get from DAO
            return orderDAO.getOrdersByCustomerId(customer.getId());
        }
        return new ArrayList<>();
    }

    /**
     * Returns a copy of the order list.
     * @return A new list containing all orders
     */
    public List<Order> getOrders() {
        return new ArrayList<>(orderList);
    }

    /**
     * Sets the list of orders.
     * @param orders The new list of orders
     */
    public void setOrders(List<Order> orders) {
        this.orderList.clear();
        this.orderList.addAll(orders);
    }

    /**
     * Loads all orders from the database.
     * @return true if loading was successful, false otherwise
     */
    public boolean loadOrders() {
        if (serializationMode) {
            try {
                File file = new File("orders.ser");
                if (!file.exists()) {
                    // If file doesn't exist, create an empty order list
                    orderList = new ArrayList<>();
                    return true;
                }
                FileInputStream fileIn = new FileInputStream(file);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                @SuppressWarnings("unchecked")
                ArrayList<Order> loadedOrders = (ArrayList<Order>) in.readObject();
                orderList = loadedOrders;
                in.close();
                fileIn.close();
                return true;
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading orders: " + e.getMessage());
                // If there's an error, create an empty order list
                orderList = new ArrayList<>();
                return true;
            }
        } else {
            List<Order> loadedOrders = orderDAO.getAllOrders();
            if (loadedOrders != null) {
                orderList.clear();
                orderList.addAll(loadedOrders);
                return true;
            }
            return false;
        }
    }

    /**
     * Saves all orders to the database.
     * @return true if saving was successful, false otherwise
     */
    public boolean saveOrders() {
        if (serializationMode) {
            try {
                FileOutputStream fileOut = new FileOutputStream("orders.ser");
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(orderList);
                out.close();
                fileOut.close();
                return true;
            } catch (IOException e) {
                System.err.println("Error saving orders: " + e.getMessage());
                return false;
            }
        } else {
            return orderDAO.saveOrders();
        }
    }

    /**
     * Removes an order from a customer and the database.
     * @param customer The customer to remove the order from
     * @param order The order to remove
     * @return true if the order was removed successfully, false otherwise
     */
    public boolean removeOrderFromCustomer(Customer customer, Order order) {
        if (customer != null && order != null) {
            boolean removed = customer.removeOrder(order);
            if (removed && orderDAO.deleteOrder(order.getOrderId())) {
                orderList.remove(order);
                return true;
            }
        }
        return false;
    }

    public String sortOrders(String criteria) {
        if (criteria == null) {
            return "Invalid sorting criteria.";
        }

        // Get all orders from all customers
        List<Order> allOrders = new ArrayList<>();
        for (Customer customer : getCustomers()) {
            allOrders.addAll(customer.getOrders());
        }

        if (allOrders.isEmpty()) {
            return "No orders to sort.";
        }

        // Convert to array for sorting
        Order[] orderArray = allOrders.toArray(new Order[0]);
        
        // Sort based on criteria using bubble sort
        if (criteria.equals("Sort by Date")) {
            for (int i = 0; i < orderArray.length - 1; i++) {
                for (int j = 0; j < orderArray.length - i - 1; j++) {
                    if (orderArray[j].getDate().compareTo(orderArray[j + 1].getDate()) > 0) {
                        Order temp = orderArray[j];
                        orderArray[j] = orderArray[j + 1];
                        orderArray[j + 1] = temp;
                    }
                }
            }
        } else if (criteria.equals("Sort by Product Name")) {
            for (int i = 0; i < orderArray.length - 1; i++) {
                for (int j = 0; j < orderArray.length - i - 1; j++) {
                    String name1 = orderArray[j].getProducts().get(0).getName();
                    String name2 = orderArray[j + 1].getProducts().get(0).getName();
                    if (name1.compareToIgnoreCase(name2) > 0) {
                        Order temp = orderArray[j];
                        orderArray[j] = orderArray[j + 1];
                        orderArray[j + 1] = temp;
                    }
                }
            }
        } else {
            return "Invalid sorting criteria.";
        }

        // Clear all orders from customers
        for (Customer customer : getCustomers()) {
            customer.getOrders().clear();
        }

        // Add sorted orders back to their respective customers
        for (Order order : orderArray) {
            order.getCustomer().addOrder(order);
        }

        // Build result string
        String result = "Orders sorted by " + criteria.toLowerCase() + ":\n";
        result += "========================================\n";
        
        // Only show each order once
        for (Order order : orderArray) {
            result += "Customer: " + order.getCustomer().getName() + " (" + order.getCustomer().getPps() + ")\n";
            result += "----------------------------------------\n";
            result += "Date: " + order.getDate() + "\n";
            result += "Products:\n";
            for (Product product : order.getProducts()) {
                result += "  - " + product.getName() + ": $" + String.format("%.2f", product.getPrice()) + "\n";
            }
            result += "Order Total: $" + String.format("%.2f", order.calculateTotal()) + "\n";
            result += "----------------------------------------\n";
        }
        
        result += "========================================\n";
        return result;
    }

    public String stockInfo(String productName) {
        if (productName == null || productName.isEmpty()) {
            return "Invalid product name.";
        }

        Product product = findProduct(productName);
        if (product != null) {
            return "Current stock for " + product.getName() + ": " + product.getStock();
        }
        return "Product not found.";
    }

    public String salesReportLastMonth() {
        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        int soldCount = 0;

        for (Customer customer : getCustomers()) {
            for (Order order : customer.getOrders()) {
                if (order.getDate().isAfter(lastMonth)) {
                    soldCount += order.getProducts().size();
                }
            }
        }
        return "Products sold last month: " + soldCount;
    }

    public String getCustomerPurchasesLastMonth(String customerPps) {
        if (customerPps == null || customerPps.isEmpty()) {
            return "Invalid customer PPS.";
        }
        Customer customer = findCustomer(customerPps);
        if (customer == null) {
            return "Customer not found.";
        }
        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        StringBuilder report = new StringBuilder();
        report.append("Purchases for ").append(customer.getName()).append(" last month:\n");
        boolean hasPurchases = false;
        for (Order order : customer.getOrders()) {
            if (order.getDate().isAfter(lastMonth)) {
                hasPurchases = true;
                report.append("Date: ").append(order.getDate()).append("\n");
                report.append("Products:\n");
                for (Product product : order.getProducts()) {
                    report.append("- ").append(product.getName())
                          .append(": $").append(product.getPrice()).append("\n");
                }
                report.append("Order Total: $").append(order.calculateTotal()).append("\n\n");
            }
        }
        if (!hasPurchases) {
            return "No purchases found for " + customer.getName() + " last month.";
        }
        return report.toString();
    }

    public String handlePurchase(String customerPps, String productName) {
        if (customerPps == null || customerPps.isEmpty() || productName == null || productName.isEmpty()) {
            return "Invalid customer PPS or product name.";
        }
        Customer customer = findCustomer(customerPps);
        if (customer == null) {
            return "Customer not found.";
        }
        Product product = findProduct(productName);
        if (product == null) {
            return "Product not found.";
        }
        if (product.getStock() <= 0) {
            return "Product is out of stock.";
        }
        try {
            List<Product> prodList = new ArrayList<>();
            prodList.add(product);
            Order newOrder = new Order.Builder(customer, prodList)
                .date(LocalDate.now())
                .build();
            customer.addOrder(newOrder);
            
            // Update product stock in the product model
            product.setStock(product.getStock() - 1);
            prodModel.updateProduct(product);
            
            // Save the orders after adding a new one
            if (orderDAO.saveOrders()) {
                System.out.println("Order saved successfully");
            } else {
                System.out.println("Failed to save order");
            }
            
            return "Added " + product.getName() + " to " + customer.getName() + "'s order.";
        } catch (IllegalArgumentException e) {
            return "Error processing purchase: " + e.getMessage();
        }
    }
    // Calculating total from list
    public double calculateTotal(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }

        double total = 0;
        for (Order order : customer.getOrders()) {
            total += order.calculateTotal();
        }
        return total;
    }
    // Generating receipt
    public String generateReceipt(Customer customer) {
        if (customer == null) {
            return "Invalid customer.";
        }

        if (customer.getOrders().isEmpty()) {
            return "No orders found for " + customer.getName() + ".";
        }
        StringBuilder receipt = new StringBuilder();
        receipt.append("Receipt for: ").append(customer.getName()).append("\n");
        receipt.append("Date: ").append(LocalDate.now()).append("\n\n");
        double total = 0;
        for (Order order : customer.getOrders()) {
            receipt.append("Order Date: ").append(order.getDate()).append("\n");
            receipt.append("Items:\n");
            for (Product product : order.getProducts()) {
                receipt.append("- ").append(product.getName()).append(": $").append(product.getPrice()).append("\n");
            }
            receipt.append("Order Total: $").append(order.calculateTotal()).append("\n");
            total += order.calculateTotal();
        }
        receipt.append("Grand Total: $").append(total).append("\n");
        return receipt.toString();
    }
    public boolean makePurchase(String productName, int quantity) {
        if (currentCustomer == null) {
            System.out.println("No current customer selected");
            return false;
        }
        Product product = prodModel.findInList(productName);
        if (product == null) {
            System.out.println("Product not found: " + productName);
            return false;
        }
        if (product.getStock() < quantity) {
            System.out.println("Insufficient stock for product: " + productName);
            return false;
        }
        try {
            // Create a list with the single product
            List<Product> products = new ArrayList<>();
            products.add(product);
            
            // Create new order with the list of products
            Order order = new Order.Builder(currentCustomer, products)
                .date(LocalDate.now())
                .quantity(quantity)
                .build();
            
            System.out.println("Creating new order #" + order.getOrderId() + " for customer " + currentCustomer.getName());
            
            // Add order to both the customer and the model's list
            currentCustomer.addOrder(order);
            orderList.add(order);
            
            // Update product stock in the product model
            product.setStock(product.getStock() - quantity);
            prodModel.updateProduct(product);

            // Save the orders after adding a new one
            if (orderDAO.saveOrders()) {
                System.out.println("Order saved successfully");
            } else {
                System.out.println("Failed to save order");
            }
            
            System.out.println("Order added successfully. Total orders: " + orderList.size());
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println("Error creating order: " + e.getMessage());
            return false;
        }
    }

    public void setSerializationMode(boolean mode) {
        this.serializationMode = mode;
    }

    public boolean isSerializationMode() {
        return serializationMode;
    }
}
