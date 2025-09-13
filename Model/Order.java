package Project.Model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing an order in the store management system.
 * Implements Serializable for data persistence and uses the Builder pattern for object creation.
 * Manages order information including customer, products, date, and total amount.
 */
public class Order implements Serializable {
    private static final long serialVersionUID = -1361882999488444383L;
    private static int nextOrderId = 1;  // Static counter for order IDs
    
    private Customer customer;
    private List<Product> products;
    private LocalDate date;
    private int quantity;
    private double total;
    private int orderId;

    /**
     * Private constructor used by the Builder pattern.
     * @param builder The builder containing the order data
     */
    private Order(Builder builder) {
        this.customer = builder.customer;
        this.products = new ArrayList<>(builder.products);
        this.date = builder.date;
        this.quantity = builder.quantity;
        this.total = calculateTotal();
        this.orderId = builder.orderId;
    }

    /**
     * Builder class for creating Order objects.
     * Implements the Builder pattern to provide a fluent interface for object creation.
     */
    public static class Builder {
        private Customer customer;
        private List<Product> products = new ArrayList<>();
        private LocalDate date = LocalDate.now();
        private int quantity = 1;
        private int orderId = nextOrderId++;

        /**
         * Creates a new Builder with required order information.
         * @param customer The customer placing the order
         * @param products The list of products in the order
         */
        public Builder(Customer customer, List<Product> products) {
            this.customer = customer;
            this.products.addAll(products);
        }

        /**
         * Sets the order date.
         * @param date The date to set
         * @return This builder instance for method chaining
         */
        public Builder date(LocalDate date) {
            this.date = date;
            return this;
        }

        /**
         * Sets the quantity of products in the order.
         * @param quantity The quantity to set
         * @return This builder instance for method chaining
         */
        public Builder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        /**
         * Sets a specific order ID.
         * @param orderId The order ID to set
         * @return This builder instance for method chaining
         */
        public Builder orderId(int orderId) {
            this.orderId = orderId;
            if (orderId >= nextOrderId) {
                nextOrderId = orderId + 1;
            }
            return this;
        }

        /**
         * Builds and validates the Order object.
         * @return A new Order instance
         * @throws IllegalArgumentException if validation fails
         */
        public Order build() {
            validate();
            return new Order(this);
        }

        /**
         * Validates the builder's data before creating the Order.
         * @throws IllegalArgumentException if any validation fails
         */
        private void validate() {
            if (customer == null) {
                throw new IllegalArgumentException("Customer cannot be null");
            }
            if (products == null || products.isEmpty()) {
                throw new IllegalArgumentException("Products list cannot be null or empty");
            }
            if (date == null) {
                throw new IllegalArgumentException("Date cannot be null");
            }
            if (quantity < 1) {
                throw new IllegalArgumentException("Quantity must be at least 1");
            }
        }
    }

    // Getters
    /**
     * Returns the order ID.
     * @return The order ID
     */
    public int getOrderId() {
        return orderId;
    }

    /**
     * Returns the customer who placed the order.
     * @return The customer object
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Returns a copy of the list of products in the order.
     * @return A new ArrayList containing all products
     */
    public List<Product> getProducts() {
        return new ArrayList<>(products);
    }

    /**
     * Returns the date of the order.
     * @return The order date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns the quantity of products in the order.
     * @return The order quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Returns the total amount of the order.
     * @return The order total
     */
    public double getTotal() {
        return total;
    }

    /**
     * Sets the customer for this order.
     * @param customer The new customer to set
     */
    public void setCustomer(Customer customer) {
        if (customer != null) {
            this.customer = customer;
        }
    }

    /**
     * Sets the list of products for this order.
     * @param products The new list of products to set
     */
    public void setProducts(List<Product> products) {
        if (products != null && !products.isEmpty()) {
            this.products = new ArrayList<>(products);
            this.total = calculateTotal();
        }
    }

    /**
     * Sets the date for this order.
     * @param date The new date to set
     */
    public void setDate(LocalDate date) {
        if (date != null) {
            this.date = date;
        }
    }

    /**
     * Sets the total amount for this order.
     * @param total The new total amount to set
     */
    public void setTotal(double total) {
        if (total >= 0) {
            this.total = total;
        }
    }

    /**
     * Sets the quantity of products in the order.
     * @param quantity The new quantity to set
     */
    public void setQuantity(int quantity) {
        if (quantity > 0) {
            this.quantity = quantity;
            this.total = calculateTotal();
        }
    }

    /**
     * Sets the order ID for this order.
     * @param orderId The new order ID to set
     */
    public void setOrderId(int orderId) {
        this.orderId = orderId;
        if (orderId >= nextOrderId) {
            nextOrderId = orderId + 1;
        }
    }

    /**
     * Calculates the total amount of the order.
     * @return The calculated total
     */
    public double calculateTotal() {
        double total = 0;
        for (Product product : products) {
            total += product.getPrice() * quantity;
        }
        return total;
    }

    /**
     * Returns a string representation of the order for file storage.
     * Format: "orderId,customerPps,date,quantity,productName"
     * @return A string representation suitable for file storage
     */
    public String toStringForFile() {
        StringBuilder sb = new StringBuilder();
        sb.append(orderId).append(",");
        sb.append(customer.getPps()).append(",");
        sb.append(date.toString()).append(",");
        sb.append(quantity).append(",");
        sb.append(products.get(0).getName()); // Only one product per order
        return sb.toString();
    }

    /**
     * Creates a new Order instance from a string representation.
     * @param line The string representation of the order
     * @param customers The list of available customers
     * @param products The list of available products
     * @return A new Order instance, or null if the line format is invalid
     */
    public static Order stringFromLine(String line, List<Customer> customers, List<Product> products) {
        if (line == null || line.trim().isEmpty()) {
            System.out.println("Empty line provided");
            return null;
        }

        try {
            String[] parts = line.split(",");
            if (parts.length != 5) {
                System.out.println("Invalid line format: " + line);
                return null;
            }

            int orderId = Integer.parseInt(parts[0].trim());
            String customerPps = parts[1].trim();
            LocalDate date = LocalDate.parse(parts[2].trim());
            int quantity = Integer.parseInt(parts[3].trim());
            String productName = parts[4].trim();

            Customer customer = findCustomer(customers, customerPps);
            if (customer == null) {
                System.out.println("Customer not found with PPS: " + customerPps);
                return null;
            }

            Product product = findProduct(products, productName);
            if (product == null) {
                System.out.println("Product not found: " + productName);
                return null;
            }

            return new Builder(customer, List.of(product))
                    .date(date)
                    .quantity(quantity)
                    .orderId(orderId)
                    .build();
        } catch (NumberFormatException e) {
            System.out.println("Error parsing numbers from line: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.out.println("Error parsing order from line: " + line);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Finds a customer in the list by their PPS number.
     * @param customers The list of customers to search
     * @param pps The PPS number to search for
     * @return The found customer, or null if not found
     */
    private static Customer findCustomer(List<Customer> customers, String pps) {
        for (Customer customer : customers) {
            if (customer.getPps().equals(pps)) {
                return customer;
            }
        }
        return null;
    }

    /**
     * Finds a product in the list by its name.
     * @param products The list of products to search
     * @param name The product name to search for
     * @return The found product, or null if not found
     */
    private static Product findProduct(List<Product> products, String name) {
        for (Product product : products) {
            if (product.getName().equals(name)) {
                return product;
            }
        }
        return null;
    }

    /**
     * Returns a string representation of the order.
     * Format includes order ID, customer name, date, products, and total.
     * @return A formatted string representation of the order
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Order #").append(orderId).append(" for: ").append(customer.getName()).append("\n");
        sb.append("Date: ").append(date).append("\n");
        sb.append("Products:\n");
        for (Product product : products) {
            sb.append("- ").append(product.getName()).append(": $").append(product.getPrice()).append(" x").append(quantity).append("\n");
        }
        sb.append("Total: $").append(calculateTotal());
        return sb.toString();
    }
}

