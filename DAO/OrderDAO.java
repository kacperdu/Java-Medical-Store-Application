package Project.DAO;

import Project.Model.Order;
import java.util.List;

/**
 * Data Access Object interface for Order entities.
 * Defines standard operations to be performed on Order objects.
 * 
 * This interface follows the DAO pattern, providing a consistent API for
 * accessing and manipulating order data regardless of the underlying
 * storage mechanism (database or file system).
 */
public interface OrderDAO {
    /**
     * Retrieves all orders from the data store.
     * The implementation should handle both database and file storage modes.
     * 
     * @return List of all orders, empty list if none found
     */
    List<Order> getAllOrders();

    /**
     * Retrieves an order by its unique identifier.
     * 
     * @param id The order ID to search for
     * @return The order if found, null otherwise
     */
    Order getOrderById(int id);

    /**
     * Retrieves all orders for a specific customer.
     * 
     * @param customerId The ID of the customer
     * @return List of orders for the customer, empty list if none found
     */
    List<Order> getOrdersByCustomerId(int customerId);

    /**
     * Retrieves all orders containing a specific product.
     * 
     * @param productId The ID of the product
     * @return List of orders containing the product, empty list if none found
     */
    List<Order> getOrdersByProduct(int productId);

    /**
     * Adds a new order to the data store.
     * The implementation should validate the order data before saving.
     * 
     * @param order The order to add
     * @return true if successful, false otherwise
     */
    boolean addOrder(Order order);

    /**
     * Updates an existing order in the data store.
     * The implementation should validate the updated order data.
     * 
     * @param order The order to update
     * @return true if successful, false otherwise
     */
    boolean updateOrder(Order order);

    /**
     * Deletes an order from the data store.
     * 
     * @param id The ID of the order to delete
     * @return true if successful, false otherwise
     */
    boolean deleteOrder(int id);

    /**
     * Saves all orders to the data store.
     * In database mode: No action needed as changes are immediate.
     * In file mode: Writes all orders to the text file.
     * 
     * @return true if successful, false otherwise
     */
    boolean saveOrders();

    /**
     * Loads all orders from the data store.
     * In database mode: No action needed as data is always current.
     * In file mode: Reads orders from the text file.
     * 
     * @return true if successful, false otherwise
     */
    boolean loadOrders();
} 