package Project.DAO;

import Project.Model.Product;
import Project.Model.StoreDataManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

/**
 * Implementation of the ProductDAO interface.
 * Handles all database and file operations for Product entities.
 * Uses the Singleton pattern through StoreDataManager for database connections.
 */
public class ProductDAOImpl implements ProductDAO {
    private static final String PRODUCTS_FILE = "products.txt";
    private final StoreDataManager storeDataManager;

    /**
     * Constructs a new ProductDAOImpl instance.
     * Initializes the StoreDataManager singleton.
     */
    public ProductDAOImpl() {
        this.storeDataManager = StoreDataManager.getInstance();
    }

    /**
     * Retrieves all products from the data store.
     * In database mode: Fetches products from the database.
     * In file mode: Reads products from the text file.
     * 
     * @return List of all products
     */
    @Override
    public List<Product> getAllProducts() {
        if (storeDataManager.isDatabaseMode()) {
            return getAllProductsFromDatabase();
        } else {
            return getAllProductsFromFile();
        }
    }

    /**
     * Retrieves all products from the database.
     * Executes a SELECT query and maps the results to Product objects.
     * 
     * @return List of products from the database
     */
    private List<Product> getAllProductsFromDatabase() {
        List<Product> products = new ArrayList<>();
        try (Connection conn = storeDataManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM products")) {
            
            while (rs.next()) {
                Product product = extractProductFromResultSet(rs);
                if (product != null) {
                    products.add(product);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving products from database: " + e.getMessage());
            e.printStackTrace();
        }
        return products;
    }

    /**
     * Retrieves all products from the text file.
     * Reads each line and converts it to a Product object.
     * 
     * @return List of products from the file
     */
    private List<Product> getAllProductsFromFile() {
        List<Product> products = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(PRODUCTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Product product = Product.stringFromLine(line);
                if (product != null) {
                    products.add(product);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading products from file: " + e.getMessage());
            e.printStackTrace();
        }
        return products;
    }

    /**
     * Retrieves a product by its ID.
     * In database mode: Queries the database by ID.
     * In file mode: Searches through the text file.
     * 
     * @param id The product ID to search for
     * @return The product if found, null otherwise
     */
    @Override
    public Product getProductById(int id) {
        if (storeDataManager.isDatabaseMode()) {
            return getProductByIdFromDatabase(id);
        } else {
            return getProductByIdFromFile(id);
        }
    }

    /**
     * Retrieves a product by ID from the database.
     * Uses a prepared statement to prevent SQL injection.
     * 
     * @param id The product ID to search for
     * @return The product if found, null otherwise
     */
    private Product getProductByIdFromDatabase(int id) {
        try (Connection conn = storeDataManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM products WHERE id = ?")) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractProductFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving product from database: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves a product by ID from the text file.
     * Reads each line until finding a matching ID.
     * 
     * @param id The product ID to search for
     * @return The product if found, null otherwise
     */
    private Product getProductByIdFromFile(int id) {
        try (BufferedReader reader = new BufferedReader(new FileReader(PRODUCTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Product product = Product.stringFromLine(line);
                if (product != null && product.getId() == id) {
                    return product;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading product from file: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Adds a new product to the data store.
     * In database mode: Inserts into the database.
     * In file mode: Appends to the text file.
     * 
     * @param product The product to add
     * @return true if successful, false otherwise
     */
    @Override
    public boolean addProduct(Product product) {
        if (storeDataManager.isDatabaseMode()) {
            return addProductToDatabase(product);
        } else {
            return addProductToFile(product);
        }
    }

    /**
     * Adds a product to the database.
     * Uses a prepared statement to prevent SQL injection.
     * 
     * @param product The product to add
     * @return true if successful, false otherwise
     */
    private boolean addProductToDatabase(Product product) {
        try (Connection conn = storeDataManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO products (name, price, stock) VALUES (?, ?, ?)")) {
            
            ps.setString(1, product.getName());
            ps.setDouble(2, product.getPrice());
            ps.setInt(3, product.getStock());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error adding product to database: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Adds a product to the text file.
     * Appends the product's string representation to the file.
     * 
     * @param product The product to add
     * @return true if successful, false otherwise
     */
    private boolean addProductToFile(Product product) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PRODUCTS_FILE, true))) {
            writer.write(product.toStringForFile());
            writer.newLine();
            return true;
        } catch (IOException e) {
            System.out.println("Error writing product to file: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates an existing product in the data store.
     * In database mode: Updates the database record.
     * In file mode: Rewrites the entire file with updated product.
     * 
     * @param product The product to update
     * @return true if successful, false otherwise
     */
    @Override
    public boolean updateProduct(Product product) {
        if (storeDataManager.isDatabaseMode()) {
            return updateProductInDatabase(product);
        } else {
            return updateProductInFile(product);
        }
    }

    /**
     * Updates a product in the database.
     * Uses a prepared statement to prevent SQL injection.
     * 
     * @param product The product to update
     * @return true if successful, false otherwise
     */
    private boolean updateProductInDatabase(Product product) {
        try (Connection conn = storeDataManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "UPDATE products SET name = ?, price = ?, stock = ? WHERE id = ?")) {
            
            ps.setString(1, product.getName());
            ps.setDouble(2, product.getPrice());
            ps.setInt(3, product.getStock());
            ps.setInt(4, product.getId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating product in database: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates a product in the text file.
     * Reads all products, updates the matching one, and writes all back to file.
     * 
     * @param product The product to update
     * @return true if successful, false otherwise
     */
    private boolean updateProductInFile(Product product) {
        List<Product> products = getAllProductsFromFile();
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == product.getId()) {
                products.set(i, product);
                break;
            }
        }
        return saveProductsToFile(products);
    }

    /**
     * Deletes a product from the data store.
     * In database mode: Deletes from the database.
     * In file mode: Removes from the text file.
     * 
     * @param id The ID of the product to delete
     * @return true if successful, false otherwise
     */
    @Override
    public boolean deleteProduct(int id) {
        if (storeDataManager.isDatabaseMode()) {
            return deleteProductFromDatabase(id);
        } else {
            return deleteProductFromFile(id);
        }
    }

    /**
     * Deletes a product from the database.
     * Uses a prepared statement to prevent SQL injection.
     * 
     * @param id The ID of the product to delete
     * @return true if successful, false otherwise
     */
    private boolean deleteProductFromDatabase(int id) {
        try (Connection conn = storeDataManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM products WHERE id = ?")) {
            
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting product from database: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a product from the text file.
     * Reads all products, removes the matching one, and writes remaining back to file.
     * 
     * @param id The ID of the product to delete
     * @return true if successful, false otherwise
     */
    private boolean deleteProductFromFile(int id) {
        List<Product> products = getAllProductsFromFile();
        products.removeIf(p -> p.getId() == id);
        return saveProductsToFile(products);
    }

    /**
     * Saves all products to the data store.
     * In database mode: No action needed as changes are immediate.
     * In file mode: Writes all products to the text file.
     * 
     * @return true if successful, false otherwise
     */
    @Override
    public boolean saveProducts() {
        if (!storeDataManager.isDatabaseMode()) {
            List<Product> products = getAllProductsFromFile();
            return saveProductsToFile(products);
        }
        return true; // Database changes are immediate
    }

    /**
     * Saves a list of products to the text file.
     * Overwrites the existing file with the new product list.
     * 
     * @param products The list of products to save
     * @return true if successful, false otherwise
     */
    private boolean saveProductsToFile(List<Product> products) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PRODUCTS_FILE))) {
            for (Product product : products) {
                writer.write(product.toStringForFile());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error saving products to file: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Loads all products from the data store.
     * In database mode: No action needed as data is always current.
     * In file mode: Reads products from the text file.
     * 
     * @return true if successful, false otherwise
     */
    @Override
    public boolean loadProducts() {
        if (!storeDataManager.isDatabaseMode()) {
            List<Product> products = getAllProductsFromFile();
            return !products.isEmpty();
        }
        return true; // Database data is always current
    }

    /**
     * Extracts a Product object from a database ResultSet.
     * Maps database columns to Product object properties.
     * 
     * @param rs The ResultSet containing product data
     * @return A Product object, or null if extraction fails
     * @throws SQLException if a database access error occurs
     */
    private Product extractProductFromResultSet(ResultSet rs) throws SQLException {
        return new Product.Builder(
            rs.getString("name"),
            rs.getDouble("price"))
            .stock(rs.getInt("stock"))
            .build();
    }
} 