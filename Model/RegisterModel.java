package Project.Model;

import java.util.ArrayList;
import java.util.regex.Pattern;
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
import java.util.List;

/**
 * Model class responsible for managing user registration.
 * Handles new user registration, validation, and data persistence.
 */
public class RegisterModel {
    // Fields
    private ArrayList<Customer> customers;
    private String errorMessage;
    private final String file_Name;
    private final String serial_FileName;
    private boolean isSerializationMode;
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");

    /**
     * Constructor to initialize the registration model with existing customers.
     * @param customers Initial list of customers
     * @param fileName Name of the text file for storing registration data
     * @param serialFileName Name of the serialized file for storing registration data
     */
    public RegisterModel(List<Customer> customers, String fileName, String serialFileName) {
        this.customers = new ArrayList<>(customers);
        this.errorMessage = "";
        this.file_Name = System.getProperty("user.home") + "/Desktop/Object Oriented Programming/Project/" + fileName;
        this.serial_FileName = System.getProperty("user.home") + "/Desktop/Object Oriented Programming/Project/" + serialFileName;
        this.isSerializationMode = false;
    }

    // Mode Methods
    /**
     * Sets the serialization mode for file operations.
     * @param mode true to use serialization, false to use text files
     */
    public void setSerializationMode(boolean mode) {
        this.isSerializationMode = mode;
    }

    /**
     * Returns whether the model is in serialization mode.
     * @return true if using serialization, false if using text files
     */
    public boolean isSerializationMode() {
        return this.isSerializationMode;
    }

    // List Management Methods
    /**
     * Returns a copy of the customer list.
     * @return A new ArrayList containing all customers
     */
    public ArrayList<Customer> getCustomers() {
        return new ArrayList<>(customers);
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
     * Registers a new customer with the provided information.
     * @param name The customer's name
     * @param email The customer's email address
     * @param password The customer's password
     * @param pps The customer's PPS number
     * @return true if registration was successful, false otherwise
     */
    public boolean register(String name, String email, String password, String pps) {
        errorMessage = "";
        
        try {
            isValidPassword(password);
            
            // Check if customer already exists
            for (Customer customer : customers) {
                if (customer.getEmail().equals(email)) {
                    errorMessage = "Email already registered";
                    return false;
                }
                if (customer.getPps().equals(pps)) {
                    errorMessage = "PPS already registered";
                    return false;
                }
            }
            
            // Create new customer (this will validate the email and PPS)
            Customer newCustomer = new Customer.Builder(name, email, password, pps).build();
            customers.add(newCustomer);
            return true;
        } catch (IllegalArgumentException e) {
            errorMessage = e.getMessage();
            return false;
        }
    }

    /**
     * Returns the current error message.
     * @return The error message, or empty string if none
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    // File I/O Methods
    /**
     * Saves customers to file based on the current mode.
     * @throws IOException if there is an error writing to the file
     */
    public void saveCustomers() throws IOException {
        if (isSerializationMode) {
            serializeCustomers();
        } else {
            saveToTextFile();
        }
    }

    /**
     * Loads customers from file based on the current mode.
     * @throws IOException if there is an error reading from the file
     */
    public void loadCustomers() throws IOException {
        if (isSerializationMode) {
            try {
                deserializeCustomers();
            } catch (ClassNotFoundException e) {
                throw new IOException("Failed to deserialize customers: " + e.getMessage());
            }
        } else {
            loadFromTextFile();
        }
    }

    /**
     * Saves customers to a text file.
     * @throws IOException if there is an error writing to the file
     */
    private void saveToTextFile() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file_Name))) {
            for (Customer customer : customers) {
                writer.write(customer.toStringForFile());
                writer.newLine();
            }
        }
    }

    /**
     * Loads customers from a text file.
     * @throws IOException if there is an error reading from the file
     */
    private void loadFromTextFile() throws IOException {
        File file = new File(file_Name);
        if (!file.exists()) {
            file.createNewFile();
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file_Name))) {
            customers.clear();
            String line;
            while ((line = reader.readLine()) != null) {
                Customer customer = Customer.stringFromLine(line);
                if (customer != null) {
                    customers.add(customer);
                }
            }
        }
    }

    /**
     * Serializes the customer list to a binary file.
     * @throws IOException if there is an error writing to the file
     */
    public void serializeCustomers() throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(serial_FileName))) {
            out.writeObject(customers);
        }
    }

    /**
     * Deserializes the customer list from a binary file.
     * @throws IOException if there is an error reading from the file
     * @throws ClassNotFoundException if the class of a serialized object cannot be found
     */
    @SuppressWarnings("unchecked")
    public void deserializeCustomers() throws IOException, ClassNotFoundException {
        File file = new File(serial_FileName);
        if (!file.exists()) {
            System.out.println("No serialized file found");
            return;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(serial_FileName))) {
            ArrayList<Customer> loadedCustomers = (ArrayList<Customer>) in.readObject();
            System.out.println("Loaded " + loadedCustomers.size() + " customers from serialized file");
            
            customers.clear();
            customers.addAll(loadedCustomers);
        }
    }

    /**
     * Validates a password against the required pattern.
     * @param password The password to validate
     * @return true if the password is valid
     * @throws IllegalArgumentException if the password is invalid
     */
    private boolean isValidPassword(String password) {
        if (password == null || !PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException("Password must be at least 8 characters long and contain at least one number, one uppercase letter, one lowercase letter, and one special character");
        }
        return true;
    }
} 