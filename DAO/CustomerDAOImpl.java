package Project.DAO;

import Project.Model.Customer;
import Project.Model.StoreDataManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Implementation of the CustomerDAO interface.
 * Handles all database and file operations for Customer entities.
 * Uses the Singleton pattern through StoreDataManager for database connections.
 */
public class CustomerDAOImpl implements CustomerDAO {
    private static final String CUSTOMERS_FILE = "customers.txt";
    private final StoreDataManager storeDataManager;

    /**
     * Constructs a new CustomerDAOImpl instance.
     * Initializes the StoreDataManager singleton.
     */
    public CustomerDAOImpl() {
        this.storeDataManager = StoreDataManager.getInstance();
    }

    /**
     * Retrieves all customers from the data store.
     * In database mode: Fetches customers from the database.
     * In file mode: Reads customers from the text file.
     * 
     * @return List of all customers
     */
    @Override
    public List<Customer> getAllCustomers() {
        if (storeDataManager.isDatabaseMode()) {
            return getAllCustomersFromDatabase();
        } else {
            return getAllCustomersFromFile();
        }
    }

    /**
     * Retrieves all customers from the database.
     * Executes a SELECT query and maps the results to Customer objects.
     * 
     * @return List of customers from the database
     */
    private List<Customer> getAllCustomersFromDatabase() {
        List<Customer> customers = new ArrayList<>();
        try (Connection conn = storeDataManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM customers")) {
            
            while (rs.next()) {
                Customer customer = extractCustomerFromResultSet(rs);
                if (customer != null) {
                    customers.add(customer);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving customers from database: " + e.getMessage());
            e.printStackTrace();
        }
        return customers;
    }

    /**
     * Retrieves all customers from the text file.
     * Reads each line and converts it to a Customer object.
     * 
     * @return List of customers from the file
     */
    private List<Customer> getAllCustomersFromFile() {
        List<Customer> customers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(CUSTOMERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Customer customer = Customer.stringFromLine(line);
                if (customer != null) {
                    customers.add(customer);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading customers from file: " + e.getMessage());
            e.printStackTrace();
        }
        return customers;
    }

    /**
     * Retrieves a customer by their ID.
     * In database mode: Queries the database by ID.
     * In file mode: Searches through the text file.
     * 
     * @param id The customer ID to search for
     * @return The customer if found, null otherwise
     */
    @Override
    public Customer getCustomerById(int id) {
        if (storeDataManager.isDatabaseMode()) {
            return getCustomerByIdFromDatabase(id);
        } else {
            return getCustomerByIdFromFile(id);
        }
    }

    /**
     * Retrieves a customer by ID from the database.
     * Uses a prepared statement to prevent SQL injection.
     * 
     * @param id The customer ID to search for
     * @return The customer if found, null otherwise
     */
    private Customer getCustomerByIdFromDatabase(int id) {
        try (Connection conn = storeDataManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM customers WHERE id = ?")) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractCustomerFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving customer from database: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves a customer by ID from the text file.
     * Reads each line until finding a matching ID.
     * 
     * @param id The customer ID to search for
     * @return The customer if found, null otherwise
     */
    private Customer getCustomerByIdFromFile(int id) {
        try (BufferedReader reader = new BufferedReader(new FileReader(CUSTOMERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Customer customer = Customer.stringFromLine(line);
                if (customer != null && customer.getId() == id) {
                    return customer;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading customer from file: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves a customer by their email.
     * In database mode: Queries the database by email.
     * In file mode: Searches through the text file.
     * 
     * @param email The customer email to search for
     * @return The customer if found, null otherwise
     */
    @Override
    public Customer getCustomerByEmail(String email) {
        if (storeDataManager.isDatabaseMode()) {
            return getCustomerByEmailFromDatabase(email);
        } else {
            return getCustomerByEmailFromFile(email);
        }
    }

    /**
     * Retrieves a customer by email from the database.
     * Uses a prepared statement to prevent SQL injection.
     * 
     * @param email The customer email to search for
     * @return The customer if found, null otherwise
     */
    private Customer getCustomerByEmailFromDatabase(String email) {
        try (Connection conn = storeDataManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM customers WHERE email = ?")) {
            
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractCustomerFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving customer from database: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves a customer by email from the text file.
     * Reads each line until finding a matching email.
     * 
     * @param email The customer email to search for
     * @return The customer if found, null otherwise
     */
    private Customer getCustomerByEmailFromFile(String email) {
        try (BufferedReader reader = new BufferedReader(new FileReader(CUSTOMERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Customer customer = Customer.stringFromLine(line);
                if (customer != null && customer.getEmail().equals(email)) {
                    return customer;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading customer from file: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Adds a new customer to the data store.
     * In database mode: Inserts into the database.
     * In file mode: Appends to the text file.
     * 
     * @param customer The customer to add
     * @return true if successful, false otherwise
     */
    @Override
    public boolean addCustomer(Customer customer) {
        if (storeDataManager.isDatabaseMode()) {
            return addCustomerToDatabase(customer);
        } else {
            return addCustomerToFile(customer);
        }
    }

    /**
     * Adds a customer to the database.
     * Uses a prepared statement to prevent SQL injection.
     * 
     * @param customer The customer to add
     * @return true if successful, false otherwise
     */
    private boolean addCustomerToDatabase(Customer customer) {
        try (Connection conn = storeDataManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO customers (name, email, password, pps, address) VALUES (?, ?, ?, ?, ?)")) {
            
            ps.setString(1, customer.getName());
            ps.setString(2, customer.getEmail());
            ps.setString(3, customer.getPassword());
            ps.setString(4, customer.getPps());
            ps.setString(5, customer.getAddress());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error adding customer to database: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Adds a customer to the text file.
     * Appends the customer's string representation to the file.
     * 
     * @param customer The customer to add
     * @return true if successful, false otherwise
     */
    private boolean addCustomerToFile(Customer customer) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CUSTOMERS_FILE, true))) {
            writer.write(customer.toStringForFile());
            writer.newLine();
            return true;
        } catch (IOException e) {
            System.out.println("Error writing customer to file: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates an existing customer in the data store.
     * In database mode: Updates the database record.
     * In file mode: Rewrites the entire file with updated customer.
     * 
     * @param customer The customer to update
     * @return true if successful, false otherwise
     */
    @Override
    public boolean updateCustomer(Customer customer) {
        if (storeDataManager.isDatabaseMode()) {
            return updateCustomerInDatabase(customer);
        } else {
            return updateCustomerInFile(customer);
        }
    }

    /**
     * Updates a customer in the database.
     * Uses a prepared statement to prevent SQL injection.
     * 
     * @param customer The customer to update
     * @return true if successful, false otherwise
     */
    private boolean updateCustomerInDatabase(Customer customer) {
        try (Connection conn = storeDataManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "UPDATE customers SET name = ?, email = ?, password = ?, pps = ?, address = ? WHERE id = ?")) {
            
            ps.setString(1, customer.getName());
            ps.setString(2, customer.getEmail());
            ps.setString(3, customer.getPassword());
            ps.setString(4, customer.getPps());
            ps.setString(5, customer.getAddress());
            ps.setInt(6, customer.getId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating customer in database: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates a customer in the text file.
     * Reads all customers, updates the matching one, and writes all back to file.
     * 
     * @param customer The customer to update
     * @return true if successful, false otherwise
     */
    private boolean updateCustomerInFile(Customer customer) {
        List<Customer> customers = getAllCustomersFromFile();
        for (int i = 0; i < customers.size(); i++) {
            if (customers.get(i).getId() == customer.getId()) {
                customers.set(i, customer);
                break;
            }
        }
        return saveCustomersToFile(customers);
    }

    /**
     * Deletes a customer from the data store.
     * In database mode: Deletes from the database.
     * In file mode: Removes from the text file.
     * 
     * @param id The ID of the customer to delete
     * @return true if successful, false otherwise
     */
    @Override
    public boolean deleteCustomer(int id) {
        if (storeDataManager.isDatabaseMode()) {
            return deleteCustomerFromDatabase(id);
        } else {
            return deleteCustomerFromFile(id);
        }
    }

    /**
     * Deletes a customer from the database.
     * Uses a prepared statement to prevent SQL injection.
     * 
     * @param id The ID of the customer to delete
     * @return true if successful, false otherwise
     */
    private boolean deleteCustomerFromDatabase(int id) {
        try (Connection conn = storeDataManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM customers WHERE id = ?")) {
            
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting customer from database: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a customer from the text file.
     * Reads all customers, removes the matching one, and writes remaining back to file.
     * 
     * @param id The ID of the customer to delete
     * @return true if successful, false otherwise
     */
    private boolean deleteCustomerFromFile(int id) {
        List<Customer> customers = getAllCustomersFromFile();
        customers.removeIf(c -> c.getId() == id);
        return saveCustomersToFile(customers);
    }

    /**
     * Saves all customers to the data store.
     * In database mode: No action needed as changes are immediate.
     * In file mode: Writes all customers to the text file.
     * 
     * @return true if successful, false otherwise
     */
    @Override
    public boolean saveCustomers() {
        if (!storeDataManager.isDatabaseMode()) {
            List<Customer> customers = getAllCustomersFromFile();
            return saveCustomersToFile(customers);
        }
        return true; // Database changes are immediate
    }

    /**
     * Saves a list of customers to the text file.
     * Overwrites the existing file with the new customer list.
     * 
     * @param customers The list of customers to save
     * @return true if successful, false otherwise
     */
    private boolean saveCustomersToFile(List<Customer> customers) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CUSTOMERS_FILE))) {
            for (Customer customer : customers) {
                writer.write(customer.toStringForFile());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error saving customers to file: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Loads all customers from the data store.
     * In database mode: No action needed as data is always current.
     * In file mode: Reads customers from the text file.
     * 
     * @return true if successful, false otherwise
     */
    @Override
    public boolean loadCustomers() {
        if (!storeDataManager.isDatabaseMode()) {
            List<Customer> customers = getAllCustomersFromFile();
            return !customers.isEmpty();
        }
        return true; // Database data is always current
    }

    /**
     * Extracts a Customer object from a database ResultSet.
     * Maps database columns to Customer object properties.
     * 
     * @param rs The ResultSet containing customer data
     * @return A Customer object, or null if extraction fails
     * @throws SQLException if a database access error occurs
     */
    private Customer extractCustomerFromResultSet(ResultSet rs) throws SQLException {
        return new Customer.Builder(
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("password"),
            rs.getString("pps"))
            .address(rs.getString("address"))
            .build();
    }
} 