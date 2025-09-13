package Project.DAO;

import Project.Model.Customer;
import java.util.List;

/**
 * Data Access Object interface for Customer entities.
 * Defines standard operations to be performed on Customer objects.
 * 
 * This interface follows the DAO pattern, providing a consistent API for
 * accessing and manipulating customer data regardless of the underlying
 * storage mechanism (database or file system).
 */
public interface CustomerDAO {
    /**
     * Retrieves all customers from the data store.
     * The implementation should handle both database and file storage modes.
     * 
     * @return List of all customers, empty list if none found
     */
    List<Customer> getAllCustomers();

    /**
     * Retrieves a customer by their unique identifier.
     * 
     * @param id The customer ID to search for
     * @return The customer if found, null otherwise
     */
    Customer getCustomerById(int id);

    /**
     * Retrieves a customer by their email address.
     * Email addresses are unique identifiers for customers.
     * 
     * @param email The customer email to search for
     * @return The customer if found, null otherwise
     */
    Customer getCustomerByEmail(String email);

    /**
     * Adds a new customer to the data store.
     * The implementation should validate the customer data before saving.
     * 
     * @param customer The customer to add
     * @return true if successful, false otherwise
     */
    boolean addCustomer(Customer customer);

    /**
     * Updates an existing customer in the data store.
     * The implementation should validate the updated customer data.
     * 
     * @param customer The customer to update
     * @return true if successful, false otherwise
     */
    boolean updateCustomer(Customer customer);

    /**
     * Deletes a customer from the data store.
     * The implementation should handle any related data (e.g., orders).
     * 
     * @param id The ID of the customer to delete
     * @return true if successful, false otherwise
     */
    boolean deleteCustomer(int id);

    /**
     * Saves all customers to the data store.
     * In database mode: No action needed as changes are immediate.
     * In file mode: Writes all customers to the text file.
     * 
     * @return true if successful, false otherwise
     */
    boolean saveCustomers();

    /**
     * Loads all customers from the data store.
     * In database mode: No action needed as data is always current.
     * In file mode: Reads customers from the text file.
     * 
     * @return true if successful, false otherwise
     */
    boolean loadCustomers();
} 