package Project.Model;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class responsible for managing both file and database operations.
 * Handles serialization/deserialization and database connectivity.
 */
public class StoreDataManager {
    private static final String STORE_DATA_FILE = "store_data.ser";
    private static StoreDataManager instance;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/customer_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Kdu8200?";
    private Connection connection;
    private boolean isDatabaseMode = false;

    private StoreDataManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found");
            e.printStackTrace();
        }
    }

    public static StoreDataManager getInstance() {
        if (instance == null) {
            instance = new StoreDataManager();
        }
        return instance;
    }

    /**
     * Gets a database connection. Creates a new one if it doesn't exist, is closed, or is invalid.
     * @return The database connection
     * @throws SQLException if a database access error occurs
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed() || !isConnectionValid()) {
            closeConnection(); // Ensure old connection is properly closed
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        }
        return connection;
    }

    /**
     * Checks if the current connection is valid by executing a simple query.
     * @return true if the connection is valid, false otherwise
     */
    private boolean isConnectionValid() {
        if (connection == null) {
            return false;
        }
        
        try {
            // Try to execute a simple query to validate the connection
            try (Statement stmt = connection.createStatement()) {
                stmt.executeQuery("SELECT 1");
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Closes the database connection.
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Error closing database connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Sets the mode to use database operations.
     * @param useDatabase true to use database, false to use file operations
     */
    public void setDatabaseMode(boolean useDatabase) {
        this.isDatabaseMode = useDatabase;
    }

    /**
     * Returns whether the manager is in database mode.
     * @return true if using database, false if using file operations
     */
    public boolean isDatabaseMode() {
        return isDatabaseMode;
    }

    /**
     * Tests the database connection and returns the result.
     * @return true if connection is successful, false otherwise
     */
    public boolean testConnection() {
        if (!isDatabaseMode) {
            System.out.println("Database mode is not enabled");
            return false;
        }
        
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("Successfully connected to the database!");
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Saves store data either to a database or a file based on the current mode.
     * @param customers List of customers to save
     * @param products List of products to save
     * @param orders List of orders to save
     * @return true if successful, false otherwise
     */
    public boolean saveStoreData(ArrayList<Customer> customers, ArrayList<Product> products, ArrayList<Order> orders) {
        if (isDatabaseMode) {
            return saveToDatabase(customers, products, orders);
        } else {
            return saveToFile(customers, products, orders);
        }
    }

    /**
     * Loads store data either from a database or a file based on the current mode.
     * @return Array containing customers, products, and orders, or null if loading fails
     */
    public Object[] loadStoreData() {
        if (isDatabaseMode) {
            return loadFromDatabase();
        } else {
            return loadFromFile();
        }
    }

    /**
     * Saves data to the database.
     * @param customers List of customers to save
     * @param products List of products to save
     * @param orders List of orders to save
     * @return true if successful, false otherwise
     */
    private boolean saveToDatabase(ArrayList<Customer> customers, ArrayList<Product> products, ArrayList<Order> orders) {
        try {
            startTransaction();
            
            // Save customers
            for (Customer customer : customers) {
                try {
                    PreparedStatement ps = prepareStatement(
                        "INSERT INTO customers (name, email, password, pps, address) VALUES (?, ?, ?, ?, ?)");
                    if (ps != null) {
                        ps.setString(1, customer.getName());
                        ps.setString(2, customer.getEmail());
                        ps.setString(3, customer.getPassword());
                        ps.setString(4, customer.getPps());
                        ps.setString(5, customer.getAddress());
                        ps.executeUpdate();
                    }
                } catch (SQLException e) {
                    System.out.println("Error saving customer: " + e.getMessage());
                    rollbackTransaction();
                    return false;
                }
            }

            // Save products
            for (Product product : products) {
                try {
                    PreparedStatement ps = prepareStatement(
                        "INSERT INTO products (name, price, stock) VALUES (?, ?, ?)");
                    if (ps != null) {
                        ps.setString(1, product.getName());
                        ps.setDouble(2, product.getPrice());
                        ps.setInt(3, product.getStock());
                        ps.executeUpdate();
                    }
                } catch (SQLException e) {
                    System.out.println("Error saving product: " + e.getMessage());
                    rollbackTransaction();
                    return false;
                }
            }

            // Save orders
            for (Order order : orders) {
                try {
                    PreparedStatement ps = prepareStatement(
                        "INSERT INTO orders (customer_id, date, quantity) VALUES (?, ?, ?)");
                    if (ps != null) {
                        ps.setInt(1, order.getCustomer().getId());
                        ps.setDate(2, Date.valueOf(order.getDate()));
                        ps.setInt(3, order.getQuantity());
                        ps.executeUpdate();
                    }
                } catch (SQLException e) {
                    System.out.println("Error saving order: " + e.getMessage());
                    rollbackTransaction();
                    return false;
                }
            }

            commitTransaction();
            return true;
        } catch (Exception e) {
            System.out.println("Error saving data to database: " + e.getMessage());
            rollbackTransaction();
            return false;
        }
    }

    /**
     * Loads data from the database.
     * @return Array containing customers, products, and orders, or null if loading fails
     */
    private Object[] loadFromDatabase() {
        try {
            ArrayList<Customer> customers = new ArrayList<>();
            ArrayList<Product> products = new ArrayList<>();
            ArrayList<Order> orders = new ArrayList<>();

            // Load customers
            ResultSet customerRs = executeQuery("SELECT * FROM customers");
            if (customerRs != null) {
                while (customerRs.next()) {
                    try {
                        Customer customer = new Customer.Builder(
                            customerRs.getString("name"),
                            customerRs.getString("email"),
                            customerRs.getString("password"),
                            customerRs.getString("pps"))
                            .address(customerRs.getString("address"))
                            .build();
                        customers.add(customer);
                    } catch (SQLException e) {
                        System.out.println("Error loading customer: " + e.getMessage());
                    }
                }
            }

            // Load products
            ResultSet productRs = executeQuery("SELECT * FROM products");
            if (productRs != null) {
                while (productRs.next()) {
                    try {
                        Product product = new Product.Builder(
                            productRs.getString("name"),
                            productRs.getDouble("price"))
                            .stock(productRs.getInt("stock"))
                            .build();
                        products.add(product);
                    } catch (SQLException e) {
                        System.out.println("Error loading product: " + e.getMessage());
                    }
                }
            }

            // Load orders
            ResultSet orderRs = executeQuery("SELECT * FROM orders");
            if (orderRs != null) {
                while (orderRs.next()) {
                    try {
                        int customerId = orderRs.getInt("customer_id");
                        Customer customer = customers.stream()
                            .filter(c -> c.getId() == customerId)
                            .findFirst()
                            .orElse(null);

                        if (customer != null) {
                            List<Product> orderProducts = new ArrayList<>();
                            Order order = new Order.Builder(customer, orderProducts)
                                .date(orderRs.getDate("date").toLocalDate())
                                .quantity(orderRs.getInt("quantity"))
                                .build();
                            orders.add(order);
                        }
                    } catch (SQLException e) {
                        System.out.println("Error loading order: " + e.getMessage());
                    }
                }
            }

            return new Object[]{customers, products, orders};
        } catch (Exception e) {
            System.out.println("Error loading data from database: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Prepares a statement for execution.
     * @param query The SQL query to prepare
     * @return The prepared statement
     * @throws SQLException if a database access error occurs
     */
    private PreparedStatement prepareStatement(String query) throws SQLException {
        return getConnection().prepareStatement(query);
    }

    /**
     * Executes a query and returns the result set.
     * @param query The SQL query to execute
     * @return The result set
     * @throws SQLException if a database access error occurs
     */
    private ResultSet executeQuery(String query) throws SQLException {
        return getConnection().createStatement().executeQuery(query);
    }

    /**
     * Starts a database transaction.
     * @throws SQLException if a database access error occurs
     */
    private void startTransaction() throws SQLException {
        getConnection().setAutoCommit(false);
    }

    /**
     * Commits the current transaction.
     * @throws SQLException if a database access error occurs
     */
    private void commitTransaction() throws SQLException {
        getConnection().commit();
        getConnection().setAutoCommit(true);
    }

    /**
     * Rolls back the current transaction.
     */
    private void rollbackTransaction() {
        try {
            getConnection().rollback();
            getConnection().setAutoCommit(true);
        } catch (SQLException e) {
            System.out.println("Error rolling back transaction: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean saveToFile(ArrayList<Customer> customers, ArrayList<Product> products, ArrayList<Order> orders) {
        // Create parent directory if it doesn't exist
        File file = new File(STORE_DATA_FILE);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(STORE_DATA_FILE))) {
            // Write each list separately to handle potential serialization issues
            out.writeObject(customers);
            out.writeObject(products);
            out.writeObject(orders);
            out.flush();
            return true;
        } catch (IOException e) {
            System.out.println("Error saving store data: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private Object[] loadFromFile() {
        // Attempt to read the serialized store data file
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(STORE_DATA_FILE))) {
            // Read and deserialize the three main data collections in order:
            // 1. Customers list
            ArrayList<Customer> customers = (ArrayList<Customer>) in.readObject();
            // 2. Products list
            ArrayList<Product> products = (ArrayList<Product>) in.readObject();
            // 3. Orders list
            ArrayList<Order> orders = (ArrayList<Order>) in.readObject();
            
            // Return the three collections as an array of Objects
            return new Object[]{customers, products, orders};
            
        } catch (FileNotFoundException e) {
            // If the file doesn't exist, start with empty collections
            System.out.println("Store data file not found. Starting with empty data.");
            return new Object[]{
                new ArrayList<Customer>(),  // Empty customers list
                new ArrayList<Product>(),   // Empty products list
                new ArrayList<Order>()      // Empty orders list
            };
            
        } catch (IOException | ClassNotFoundException e) {
            // Handle any other errors during file reading or deserialization
            System.out.println("Error loading store data: " + e.getMessage());
            e.printStackTrace();
            return null;  // Return null to indicate failure to the calling code
        }
    }
} 