package Project.Model;

import java.util.ArrayList;

/**
 * Model class responsible for managing user authentication and login operations.
 * Handles user login, logout, and admin privileges.
 */
public class LoginModel {
    // Fields
    private ArrayList<Customer> customers;
    private String errorMessage;
    private boolean isLoggedIn;
    private Customer currentCustomer;

    /**
     * Constructor to initialize the login model with existing customers.
     * @param customers Initial list of customers
     */
    public LoginModel(ArrayList<Customer> customers) {
        this.customers = customers;
        this.errorMessage = "";
        this.isLoggedIn = false;
        this.currentCustomer = null;
    }

    /**
     * Attempts to log in a user with the provided credentials.
     * @param email The user's email address
     * @param password The user's password
     * @return true if login was successful, false otherwise
     */
    public boolean login(String email, String password) {
        System.out.println("Attempting login with: " + email + ", " + password); // Debug
        
        // Validate input
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            System.out.println("Login failed - empty credentials");
            return false;
        }

        // Check for admin login
        if (email.equals("admin@gmail.com") && password.equals("admin123")) {
            // Create a temporary admin customer
            currentCustomer = new Customer.Builder("Admin", "admin@gmail.com", "admin123", "1234567A")
                .address("Admin Address")
                .build();
            isLoggedIn = true;
            System.out.println("Admin login successful!"); // Debug
            return true;
        }

        // Check regular customer login
        for (Customer customer : customers) {
            System.out.println("Checking customer: " + customer.getEmail() + ", " + customer.getPassword()); // Debug
            if (customer.getEmail().equals(email) && customer.getPassword().equals(password)) {
                currentCustomer = customer;
                isLoggedIn = true;
                System.out.println("Login successful!"); // Debug
                return true;
            }
        }
        System.out.println("Login failed - no matching credentials found"); // Debug
        return false;
    }

    /**
     * Returns the list of customers.
     * @return The list of customers
     */
    public ArrayList<Customer> getCustomers() {
        return customers;
    }

    /**
     * Sets the list of customers.
     * @param customers The new list of customers
     */
    public void setCustomers(ArrayList<Customer> customers) {
        this.customers.clear();
        this.customers.addAll(customers);
    }

    /**
     * Returns the currently logged-in customer.
     * @return The current customer, or null if no one is logged in
     */
    public Customer getCurrentCustomer() {
        return currentCustomer;
    }

    /**
     * Checks if the current user has admin privileges.
     * @return true if the current user is an admin, false otherwise
     */
    public boolean isAdmin() {
        return currentCustomer != null && 
               currentCustomer.getEmail().equals("admin@gmail.com") && 
               currentCustomer.getPassword().equals("admin123");
    }
} 