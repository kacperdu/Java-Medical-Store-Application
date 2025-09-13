package Project.Model;

import java.io.Serializable;

/**
 * Class representing a product in the store management system.
 * Implements Serializable for data persistence and uses the Builder pattern for object creation.
 * Manages product information including name, price, and stock level.
 */
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private double price;
    private int stock;
    private int id;

    /**
     * Private constructor used by the Builder pattern.
     * @param builder The builder containing the product data
     */
    private Product(Builder builder) {
        this.name = builder.name;
        this.price = builder.price;
        this.stock = builder.stock;
        this.id = builder.id;
    }

    /**
     * Builder class for creating Product objects.
     * Implements the Builder pattern to provide a fluent interface for object creation.
     */
    public static class Builder {
        private String name;
        private double price;
        private int stock;
        private int id;

        /**
         * Creates a new Builder with required product information.
         * @param name The product name
         * @param price The product price
         */
        public Builder(String name, double price) {
            this.name = name;
            this.price = price;
            this.stock = 0;
        }

        /**
         * Sets the stock level for the product.
         * @param stock The stock level to set
         * @return This builder instance for method chaining
         */
        public Builder stock(int stock) {
            this.stock = stock;
            return this;
        }

        /**
         * Sets the product ID.
         * @param id The ID to set
         * @return This builder instance for method chaining
         */
        public Builder id(int id) {
            this.id = id;
            return this;
        }

        /**
         * Builds and validates the Product object.
         * @return A new Product instance
         * @throws IllegalArgumentException if validation fails
         */
        public Product build() {
            validate();
            return new Product(this);
        }

        /**
         * Validates the builder's data before creating the Product.
         * @throws IllegalArgumentException if any validation fails
         */
        private void validate() {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Product name cannot be null or empty");
            }
            if (price < 0) {
                throw new IllegalArgumentException("Product price cannot be negative");
            }
            if (stock < 0) {
                throw new IllegalArgumentException("Product stock cannot be negative");
            }
        }
    }

    // Getters
    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public int getId() {
        return id;
    }

    // Setters
    public void setName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }
    }

    public void setPrice(double price) {
        if (price >= 0) {
            this.price = price;
        }
    }

    public void setStock(int stock) {
        if (stock >= 0) {
            this.stock = stock;
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * Creates a new Product with decreased stock.
     * @param amount The amount to decrease stock by
     * @return A new Product instance with updated stock
     */
    public Product withDecreasedStock(int amount) {
        if (stock < amount) {
            throw new IllegalArgumentException("Cannot decrease stock below 0");
        }
        return new Builder(name, price)
            .stock(stock - amount)
            .id(id)
            .build();
    }

    @Override
    public String toString() {
        return String.format("Product{name='%s', price=%.2f, stock=%d}", name, price, stock);
    }

    /**
     * Creates a Product object from a string line.
     * Format: "id,name,price,stock"
     * @param line The string line to parse
     * @return A new Product object, or null if parsing fails
     */
    public static Product stringFromLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }

        try {
            String[] parts = line.split(",");
            if (parts.length != 4) {
                return null;
            }

            int id = Integer.parseInt(parts[0].trim());
            String name = parts[1].trim();
            double price = Double.parseDouble(parts[2].trim());
            int stock = Integer.parseInt(parts[3].trim());

            return new Builder(name, price)
                .id(id)
                .stock(stock)
                .build();
        } catch (NumberFormatException e) {
            System.err.println("Error parsing numbers from line: " + e.getMessage());
            return null;
        } catch (IllegalArgumentException e) {
            System.err.println("Error validating product data: " + e.getMessage());
            return null;
        }
    }

    /**
     * Returns a string representation of the product for file storage.
     * Format: "id,name,price,stock"
     * @return A string representation suitable for file storage
     */
    public String toStringForFile() {
        return String.format("%d,%s,%.2f,%d", id, name, price, stock);
    }
}
