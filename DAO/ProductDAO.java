package Project.DAO;

import Project.Model.Product;
import java.util.List;

/**
 * Data Access Object interface for Product entities.
 * Defines standard operations to be performed on Product objects.
 * 
 * This interface follows the DAO pattern, providing a consistent API for
 * accessing and manipulating product data regardless of the underlying
 * storage mechanism (database or file system).
 */
public interface ProductDAO {
    /**
     * Retrieves all products from the data store.
     * The implementation should handle both database and file storage modes.
     * 
     * @return List of all products, empty list if none found
     */
    List<Product> getAllProducts();

    /**
     * Retrieves a product by its unique identifier.
     * 
     * @param id The product ID to search for
     * @return The product if found, null otherwise
     */
    Product getProductById(int id);

    /**
     * Adds a new product to the data store.
     * The implementation should validate the product data before saving.
     * 
     * @param product The product to add
     * @return true if successful, false otherwise
     */
    boolean addProduct(Product product);

    /**
     * Updates an existing product in the data store.
     * The implementation should validate the updated product data.
     * 
     * @param product The product to update
     * @return true if successful, false otherwise
     */
    boolean updateProduct(Product product);

    /**
     * Deletes a product from the data store.
     * The implementation should handle any related data (e.g., orders).
     * 
     * @param id The ID of the product to delete
     * @return true if successful, false otherwise
     */
    boolean deleteProduct(int id);

    /**
     * Saves all products to the data store.
     * In database mode: No action needed as changes are immediate.
     * In file mode: Writes all products to the text file.
     * 
     * @return true if successful, false otherwise
     */
    boolean saveProducts();

    /**
     * Loads all products from the data store.
     * In database mode: No action needed as data is always current.
     * In file mode: Reads products from the text file.
     * 
     * @return true if successful, false otherwise
     */
    boolean loadProducts();
} 