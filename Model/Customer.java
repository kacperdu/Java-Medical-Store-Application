package Project.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Class representing a customer in the store management system.
 * Implements Serializable for data persistence and uses the Builder pattern for object creation.
 * Manages customer information including personal details and order history.
 */
public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Pattern PPS_PATTERN = Pattern.compile("^[0-9]{7}[A-Z]{1,2}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    private int id;
    private String name;
    private String email;
    private String password;
    private String pps;
    private String address;
    private ArrayList<Order> orders;

    /**
     * Validates an email address format.
     * @param email The email address to validate
     * @return true if the email is valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            return false;
        }
        return true;
    }

    /**
     * Validates a PPS number format.
     * @param pps The PPS number to validate
     * @return true if the PPS number is valid, false otherwise
     */
    public static boolean isValidPPS(String pps) {
        if (pps == null) {
            return false;
        }
        return PPS_PATTERN.matcher(pps).matches();
    }

    /**
     * Private constructor used by the Builder pattern.
     * @param builder The builder containing the customer data
     */
    private Customer(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.email = builder.email;
        this.password = builder.password;
        this.pps = builder.pps;
        this.address = builder.address;
        this.orders = new ArrayList<>();
    }

    /**
     * Builder class for creating Customer objects.
     * Implements the Builder pattern to provide a fluent interface for object creation.
     */
    public static class Builder {
        private int id;
        private String name;
        private String email;
        private String password;
        private String pps;
        private String address = "";

        /**
         * Creates a new Builder with required customer information.
         * @param name The customer's name
         * @param email The customer's email address
         * @param password The customer's password
         * @param pps The customer's PPS number
         */
        public Builder(String name, String email, String password, String pps) {
            this.name = name;
            this.email = email;
            this.password = password;
            this.pps = pps;
        }

        /**
         * Sets the customer's ID.
         * @param id The ID to set
         * @return This builder instance for method chaining
         */
        public Builder id(int id) {
            this.id = id;
            return this;
        }

        /**
         * Sets the customer's address.
         * @param address The address to set
         * @return This builder instance for method chaining
         */
        public Builder address(String address) {
            this.address = address;
            return this;
        }

        /**
         * Builds and validates the Customer object.
         * @return A new Customer instance
         * @throws IllegalArgumentException if validation fails
         */
        public Customer build() {
            validate();
            return new Customer(this);
        }

        /**
         * Validates the builder's data before creating the Customer.
         * @throws IllegalArgumentException if any validation fails
         */
        private void validate() {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Name cannot be empty");
            }
            if (!Customer.isValidEmail(email)) {
                throw new IllegalArgumentException("Please enter a valid email address");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new IllegalArgumentException("Password cannot be empty");
            }
            if (!Customer.isValidPPS(pps)) {
                throw new IllegalArgumentException("PPS must be 7 numbers followed by 1-2 letters");
            }
        }
    }

    // Getters
    /**
     * Returns the customer's ID.
     * @return The customer ID
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the customer's name.
     * @return The customer name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the customer's email address.
     * @return The customer email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the customer's password.
     * @return The customer password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Returns the customer's PPS number.
     * @return The customer PPS number
     */
    public String getPps() {
        return pps;
    }

    /**
     * Returns the customer's address.
     * @return The customer address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Returns a copy of the customer's order list.
     * @return A new ArrayList containing all orders
     */
    public ArrayList<Order> getOrders() {
        return new ArrayList<>(orders);
    }

    // Setters
    /**
     * Sets the customer's ID.
     * @param id The new ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets the customer's name.
     * @param name The new name to set
     */
    public void setName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }
    }

    /**
     * Sets the customer's email address.
     * @param email The new email to set
     */
    public void setEmail(String email) {
        if (isValidEmail(email)) {
            this.email = email;
        }
    }

    /**
     * Sets the customer's password.
     * @param password The new password to set
     */
    public void setPassword(String password) {
        if (password != null && !password.trim().isEmpty()) {
            this.password = password;
        }
    }

    /**
     * Sets the customer's PPS number.
     * @param pps The new PPS number to set
     */
    public void setPps(String pps) {
        if (isValidPPS(pps)) {
            this.pps = pps;
        }
    }

    /**
     * Sets the customer's address.
     * @param address The new address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    // Order Management Methods
    /**
     * Adds an order to the customer's order list.
     * @param order The order to add
     */
    public void addOrder(Order order) {
        if (order != null) {
            orders.add(order);
        }
    }

    /**
     * Removes an order from the customer's order list.
     * @param order The order to remove
     * @return true if the order was removed, false otherwise
     */
    public boolean removeOrder(Order order) {
        if (order != null) {
            return orders.remove(order);
        }
        return false;
    }

    /**
     * Returns a string representation of the customer for file storage.
     * Format: "name,email,password,pps,address"
     * @return A string representation suitable for file storage
     */
    public String toStringForFile() {
        return String.join(",",
                name, email, password, pps, address
        );
    }

    /**
     * Creates a new Customer instance from a string representation.
     * @param line The string representation of the customer
     * @return A new Customer instance, or null if the line format is invalid
     */
    public static Customer stringFromLine(String line) {
        String[] bits = line.split(",", -1);
        if (bits.length >= 5) {
            try {
                String name = bits[0].trim();
                String email = bits[1].trim();
                String password = bits[2].trim();
                String pps = bits[3].trim();
                String address = bits[4].trim();

                return new Builder(name, email, password, pps)
                        .address(address)
                        .build();
            } catch (Exception e) {
                System.out.println("Error parsing customer data: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        }
        System.out.println("Invalid customer data format: " + line);
        return null;
    }

    /**
     * Returns a string representation of the customer.
     * Format: "name (pps)"
     * @return A formatted string representation of the customer
     */
    public String toString() {
        return name + " (" + pps + ")";
    }
}
