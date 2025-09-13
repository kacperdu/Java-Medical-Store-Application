package Project.DAO;

import Project.Model.Order;
import Project.Model.Customer;
import Project.Model.Product;
import Project.Model.StoreDataManager;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

/**
 * Implementation of the OrderDAO interface.
 * Handles all database and file operations for Order entities.
 * Uses the Singleton pattern through StoreDataManager for database connections.
 */
public class OrderDAOImpl implements OrderDAO {
    private static final String ORDERS_FILE = "orders.txt";
    private final StoreDataManager storeDataManager;
    private final CustomerDAO customerDAO;
    private final ProductDAO productDAO;

    /**
     * Constructs a new OrderDAOImpl instance.
     * Initializes the StoreDataManager singleton.
     */
    public OrderDAOImpl() {
        this.storeDataManager = StoreDataManager.getInstance();
        this.customerDAO = new CustomerDAOImpl();
        this.productDAO = new ProductDAOImpl();
    }

    /**
     * Retrieves all orders from the data store.
     * In database mode: Fetches orders from the database.
     * In file mode: Reads orders from the text file.
     * 
     * @return List of all orders
     */
    @Override
    public List<Order> getAllOrders() {
        if (storeDataManager.isDatabaseMode()) {
            return getAllOrdersFromDatabase();
        } else {
            return getAllOrdersFromFile();
        }
    }

    /**
     * Retrieves all orders from the database.
     * Executes a SELECT query and maps the results to Order objects.
     * 
     * @return List of orders from the database
     */
    private List<Order> getAllOrdersFromDatabase() {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM orders";
        
        try (Connection conn = storeDataManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Order order = extractOrderFromResultSet(rs);
                if (order != null) {
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving all orders: " + e.getMessage());
            e.printStackTrace();
        }
        return orders;
    }

    /**
     * Retrieves all orders from the text file.
     * Reads each line and converts it to an Order object.
     * 
     * @return List of orders from the file
     */
    private List<Order> getAllOrdersFromFile() {
        List<Order> orders = new ArrayList<>();
        File file = new File(ORDERS_FILE);
        
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Error creating orders file: " + e.getMessage());
                return orders;
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(ORDERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Order order = Order.stringFromLine(line, customerDAO.getAllCustomers(), productDAO.getAllProducts());
                if (order != null) {
                    orders.add(order);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading orders file: " + e.getMessage());
            e.printStackTrace();
        }
        return orders;
    }

    /**
     * Retrieves an order by its ID.
     * In database mode: Queries the database by ID.
     * In file mode: Searches through the text file.
     * 
     * @param id The order ID to search for
     * @return The order if found, null otherwise
     */
    @Override
    public Order getOrderById(int id) {
        if (storeDataManager.isDatabaseMode()) {
            return getOrderByIdFromDatabase(id);
        } else {
            return getOrderByIdFromFile(id);
        }
    }

    /**
     * Retrieves an order by ID from the database.
     * Uses a prepared statement to prevent SQL injection.
     * 
     * @param id The order ID to search for
     * @return The order if found, null otherwise
     */
    private Order getOrderByIdFromDatabase(int id) {
        String query = "SELECT * FROM orders WHERE id = ?";
        
        try (Connection conn = storeDataManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractOrderFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving order by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves an order by ID from the text file.
     * Reads each line until finding a matching ID.
     * 
     * @param id The order ID to search for
     * @return The order if found, null otherwise
     */
    private Order getOrderByIdFromFile(int id) {
        List<Order> orders = getAllOrdersFromFile();
        return orders.stream()
            .filter(o -> o.getOrderId() == id)
            .findFirst()
            .orElse(null);
    }

    /**
     * Retrieves orders by customer ID.
     * In database mode: Queries the database by customer ID.
     * In file mode: Filters orders from the text file.
     * 
     * @param customerId The ID of the customer to search for
     * @return List of orders by customer
     */
    @Override
    public List<Order> getOrdersByCustomerId(int customerId) {
        if (storeDataManager.isDatabaseMode()) {
            return getOrdersByCustomerIdFromDatabase(customerId);
        } else {
            return getOrdersByCustomerIdFromFile(customerId);
        }
    }

    /**
     * Retrieves orders by customer ID from the database.
     * Uses a prepared statement to prevent SQL injection.
     * 
     * @param customerId The ID of the customer to search for
     * @return List of orders by customer
     */
    private List<Order> getOrdersByCustomerIdFromDatabase(int customerId) {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM orders WHERE customer_id = ?";
        
        try (Connection conn = storeDataManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Order order = extractOrderFromResultSet(rs);
                    if (order != null) {
                        orders.add(order);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving orders by customer ID: " + e.getMessage());
            e.printStackTrace();
        }
        return orders;
    }

    /**
     * Retrieves orders by customer ID from the text file.
     * Filters orders from the text file.
     * 
     * @param customerId The ID of the customer to search for
     * @return List of orders by customer
     */
    private List<Order> getOrdersByCustomerIdFromFile(int customerId) {
        List<Order> orders = getAllOrdersFromFile();
        orders.removeIf(o -> o.getCustomer().getId() != customerId);
        return orders;
    }

    /**
     * Retrieves orders by product ID.
     * In database mode: Queries the database by product ID.
     * In file mode: Filters orders from the text file.
     * 
     * @param productId The ID of the product to search for
     * @return List of orders by product
     */
    @Override
    public List<Order> getOrdersByProduct(int productId) {
        if (storeDataManager.isDatabaseMode()) {
            return getOrdersByProductFromDatabase(productId);
        } else {
            return getOrdersByProductFromFile(productId);
        }
    }

    /**
     * Retrieves orders by product ID from the database.
     * Uses a prepared statement to prevent SQL injection.
     * 
     * @param productId The ID of the product to search for
     * @return List of orders by product
     */
    private List<Order> getOrdersByProductFromDatabase(int productId) {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT o.* FROM orders o " +
                      "JOIN order_products op ON o.id = op.order_id " +
                      "WHERE op.product_id = ?";
        
        try (Connection conn = storeDataManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, productId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Order order = extractOrderFromResultSet(rs);
                    if (order != null) {
                        orders.add(order);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving orders by product: " + e.getMessage());
            e.printStackTrace();
        }
        return orders;
    }

    /**
     * Retrieves orders by product ID from the text file.
     * Filters orders from the text file.
     * 
     * @param productId The ID of the product to search for
     * @return List of orders by product
     */
    private List<Order> getOrdersByProductFromFile(int productId) {
        List<Order> orders = getAllOrdersFromFile();
        orders.removeIf(o -> o.getProducts().stream().noneMatch(p -> p.getId() == productId));
        return orders;
    }

    /**
     * Adds a new order to the data store.
     * In database mode: Inserts into the database.
     * In file mode: Appends to the text file.
     * 
     * @param order The order to add
     * @return true if successful, false otherwise
     */
    @Override
    public boolean addOrder(Order order) {
        if (storeDataManager.isDatabaseMode()) {
            return addOrderToDatabase(order);
        } else {
            return addOrderToFile(order);
        }
    }

    /**
     * Adds an order to the database.
     * Uses a prepared statement to prevent SQL injection.
     * 
     * @param order The order to add
     * @return true if successful, false otherwise
     */
    private boolean addOrderToDatabase(Order order) {
        String query = "INSERT INTO orders (customer_id, date, quantity) VALUES (?, ?, ?)";
        
        try (Connection conn = storeDataManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, order.getCustomer().getId());
            pstmt.setDate(2, Date.valueOf(order.getDate()));
            pstmt.setInt(3, order.getQuantity());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        order.setOrderId(generatedKeys.getInt(1));
                    }
                }
                return saveOrderProducts(order);
            }
        } catch (SQLException e) {
            System.out.println("Error adding order: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Adds an order to the text file.
     * Appends the order's string representation to the file.
     * 
     * @param order The order to add
     * @return true if successful, false otherwise
     */
    private boolean addOrderToFile(Order order) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ORDERS_FILE, true))) {
            writer.write(order.toStringForFile());
            writer.newLine();
            return true;
        } catch (IOException e) {
            System.out.println("Error adding order to file: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates an existing order in the data store.
     * In database mode: Updates the database record.
     * In file mode: Rewrites the entire file with updated order.
     * 
     * @param order The order to update
     * @return true if successful, false otherwise
     */
    @Override
    public boolean updateOrder(Order order) {
        if (storeDataManager.isDatabaseMode()) {
            return updateOrderInDatabase(order);
        } else {
            return updateOrderInFile(order);
        }
    }

    /**
     * Updates an order in the database.
     * Uses a prepared statement to prevent SQL injection.
     * 
     * @param order The order to update
     * @return true if successful, false otherwise
     */
    private boolean updateOrderInDatabase(Order order) {
        String query = "UPDATE orders SET customer_id = ?, date = ?, quantity = ? WHERE id = ?";
        
        try (Connection conn = storeDataManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, order.getCustomer().getId());
            pstmt.setDate(2, Date.valueOf(order.getDate()));
            pstmt.setInt(3, order.getQuantity());
            pstmt.setInt(4, order.getOrderId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating order: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates an order in the text file.
     * Reads all orders, updates the matching one, and writes all back to file.
     * 
     * @param order The order to update
     * @return true if successful, false otherwise
     */
    private boolean updateOrderInFile(Order order) {
        List<Order> orders = getAllOrdersFromFile();
        boolean found = false;
        
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getOrderId() == order.getOrderId()) {
                orders.set(i, order);
                found = true;
                break;
            }
        }
        
        if (found) {
            return saveOrdersToFile(orders);
        }
        return false;
    }

    /**
     * Deletes an order from the data store.
     * In database mode: Deletes from the database.
     * In file mode: Removes from the text file.
     * 
     * @param id The ID of the order to delete
     * @return true if successful, false otherwise
     */
    @Override
    public boolean deleteOrder(int id) {
        if (storeDataManager.isDatabaseMode()) {
            return deleteOrderFromDatabase(id);
        } else {
            return deleteOrderFromFile(id);
        }
    }

    /**
     * Deletes an order from the database.
     * Uses a prepared statement to prevent SQL injection.
     * 
     * @param id The ID of the order to delete
     * @return true if successful, false otherwise
     */
    private boolean deleteOrderFromDatabase(int id) {
        String query = "DELETE FROM orders WHERE id = ?";
        
        try (Connection conn = storeDataManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting order: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Deletes an order from the text file.
     * Reads all orders, removes the matching one, and writes remaining back to file.
     * 
     * @param id The ID of the order to delete
     * @return true if successful, false otherwise
     */
    private boolean deleteOrderFromFile(int id) {
        List<Order> orders = getAllOrdersFromFile();
        boolean removed = orders.removeIf(o -> o.getOrderId() == id);
        
        if (removed) {
            return saveOrdersToFile(orders);
        }
        return false;
    }

    /**
     * Saves all orders to the data store.
     * In database mode: No action needed as changes are immediate.
     * In file mode: Writes all orders to the text file.
     * 
     * @return true if successful, false otherwise
     */
    @Override
    public boolean saveOrders() {
        if (storeDataManager.isDatabaseMode()) {
            return true; // Database saves are handled automatically
        } else {
            return saveOrdersToFile(getAllOrdersFromFile());
        }
    }

    /**
     * Saves a list of orders to the text file.
     * Overwrites the existing file with the new order list.
     * 
     * @param orders The list of orders to save
     * @return true if successful, false otherwise
     */
    private boolean saveOrdersToFile(List<Order> orders) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ORDERS_FILE))) {
            for (Order order : orders) {
                writer.write(order.toStringForFile());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error saving orders to file: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Loads all orders from the data store.
     * In database mode: No action needed as data is always current.
     * In file mode: Reads orders from the text file.
     * 
     * @return true if successful, false otherwise
     */
    @Override
    public boolean loadOrders() {
        return true; // Loading is handled in getAllOrders()
    }

    /**
     * Extracts an Order object from a database ResultSet.
     * Maps database columns to Order object properties.
     * 
     * @param rs The ResultSet containing order data
     * @return An Order object, or null if extraction fails
     * @throws SQLException if a database access error occurs
     */
    private Order extractOrderFromResultSet(ResultSet rs) throws SQLException {
        int customerId = rs.getInt("customer_id");
        Customer customer = customerDAO.getCustomerById(customerId);
        if (customer == null) {
            return null;
        }

        List<Product> products = getOrderProducts(rs.getInt("id"));
        if (products.isEmpty()) {
            return null;
        }

        return new Order.Builder(customer, products)
            .date(rs.getDate("date").toLocalDate())
            .quantity(rs.getInt("quantity"))
            .orderId(rs.getInt("id"))
            .build();
    }

    private List<Product> getOrderProducts(int orderId) throws SQLException {
        List<Product> products = new ArrayList<>();
        String query = "SELECT p.* FROM products p " +
                      "JOIN order_products op ON p.id = op.product_id " +
                      "WHERE op.order_id = ?";
        
        try (Connection conn = storeDataManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, orderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    products.add(new Product.Builder(
                        rs.getString("name"),
                        rs.getDouble("price"))
                        .stock(rs.getInt("stock"))
                        .build());
                }
            }
        }
        return products;
    }

    private boolean saveOrderProducts(Order order) {
        String query = "INSERT INTO order_products (order_id, product_id) VALUES (?, ?)";
        
        try (Connection conn = storeDataManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            for (Product product : order.getProducts()) {
                pstmt.setInt(1, order.getOrderId());
                pstmt.setInt(2, product.getId());
                pstmt.addBatch();
            }
            
            pstmt.executeBatch();
            return true;
        } catch (SQLException e) {
            System.out.println("Error saving order products: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
} 