package Project.Model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class responsible for managing product data and operations.
 * Handles product list management and business logic.
 * Supports both text file and serialization-based persistence.
 */
public class ProdModel {
    // List to store all products
    private ArrayList<Product> prodList;
    
    // Flag to determine storage mode (serialization vs text file)
    private boolean serializationMode;
    
    // File names for different storage modes
    private static final String PRODUCTS_FILE = "products.txt";
    private static final String PRODUCTS_SER_FILE = "products.ser";

    /**
     * Constructor to initialize the model with existing products.
     * @param prodList Initial list of products, can be null
     */
    public ProdModel(ArrayList<Product> prodList) {
        // Initialize with provided list or empty list if null
        this.prodList = prodList != null ? prodList : new ArrayList<>();
        // Default to text file mode
        this.serializationMode = false;
    }

    /**
     * Returns a copy of the product list to prevent external modifications.
     * @return A new ArrayList containing all products
     */
    public ArrayList<Product> getProdList() {
        // Return a defensive copy to prevent external modifications
        return new ArrayList<>(prodList);
    }

    /**
     * Replaces the entire product list with a new one.
     * @param products The new list of products
     */
    public void setProdList(List<Product> products) {
        // Clear existing list and add all new products
        this.prodList.clear();
        this.prodList.addAll(products);
    }

    /**
     * Adds a new product to the list.
     * @param product The product to add
     * @return true if the product was added successfully, false if the product already exists
     */
    public boolean addProd(Product product) {
        // Check for null product or duplicate name
        if (product == null || findInList(product.getName()) != null) {
            return false;
        }
        // Add product to list
        prodList.add(product);
        return true;
    }

    /**
     * Removes a product from the list.
     * @param name The name of the product to remove
     * @return true if the product was removed successfully, false if the product was not found
     */
    public boolean removeProd(String name) {
        // Validate input
        if (name == null || name.isEmpty()) {
            return false;
        }

        // Find and remove product
        Product product = findInList(name);
        if (product != null) {
            prodList.remove(product);
            return true;
        }
        return false;
    }

    /**
     * Finds a product by its name.
     * @param name The name to search for
     * @return The product if found, null otherwise
     */
    public Product findInList(String name) {
        // Use stream to find first matching product by name
        return prodList.stream()
            .filter(p -> p.getName().equals(name))
            .findFirst()
            .orElse(null);
    }

    /**
     * Updates a product in the list.
     * @param product The product to update
     * @return true if the product was updated successfully, false otherwise
     */
    public boolean updateProduct(Product product) {
        // Validate input
        if (product == null) {
            return false;
        }

        // Find and update product
        int index = prodList.indexOf(findInList(product.getName()));
        if (index != -1) {
            prodList.set(index, product);
            return true;
        }
        return false;
    }

    /**
     * Loads all products from the appropriate storage.
     * @return true if loading was successful, false otherwise
     */
    public boolean loadProducts() {
        // Choose loading method based on storage mode
        if (serializationMode) {
            return loadFromSerializedFile();
        } else {
            return loadFromTextFile();
        }
    }

    /**
     * Saves all products to the appropriate storage.
     * @return true if saving was successful, false otherwise
     */
    public boolean saveProducts() {
        // Choose saving method based on storage mode
        if (serializationMode) {
            return saveToSerializedFile();
        } else {
            return saveToTextFile();
        }
    }

    /**
     * Loads products from a text file.
     * @return true if loading was successful, false otherwise
     */
    private boolean loadFromTextFile() {
        // Create file if it doesn't exist
        File file = new File(PRODUCTS_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("Error creating products file: " + e.getMessage());
                return false;
            }
        }

        // Read products from file
        try (BufferedReader reader = new BufferedReader(new FileReader(PRODUCTS_FILE))) {
            // Clear existing list
            prodList.clear();
            String line;
            // Read each line and convert to Product object
            while ((line = reader.readLine()) != null) {
                Product product = Product.stringFromLine(line);
                if (product != null) {
                    prodList.add(product);
                }
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error loading products from text file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Saves products to a text file.
     * @return true if saving was successful, false otherwise
     */
    private boolean saveToTextFile() {
        // Write products to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PRODUCTS_FILE))) {
            // Convert each product to string and write to file
            for (Product product : prodList) {
                writer.write(product.toStringForFile());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error saving products to text file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Loads products from a serialized file.
     * @return true if loading was successful, false otherwise
     */
    private boolean loadFromSerializedFile() {
        // Read serialized products from file
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(PRODUCTS_SER_FILE))) {
            @SuppressWarnings("unchecked")
            ArrayList<Product> loadedProducts = (ArrayList<Product>) in.readObject();
            // Replace current list with loaded products
            prodList = loadedProducts;
            return true;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading products from serialized file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Saves products to a serialized file.
     * @return true if saving was successful, false otherwise
     */
    private boolean saveToSerializedFile() {
        // Write products to serialized file
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(PRODUCTS_SER_FILE))) {
            out.writeObject(prodList);
            return true;
        } catch (IOException e) {
            System.err.println("Error saving products to serialized file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Sets the serialization mode for file operations.
     * @param mode true to use serialization, false to use text files
     */
    public void setSerializationMode(boolean mode) {
        this.serializationMode = mode;
    }

    /**
     * Returns whether the model is in serialization mode.
     * @return true if using serialization, false if using text files
     */
    public boolean isSerializationMode() {
        return serializationMode;
    }
}
