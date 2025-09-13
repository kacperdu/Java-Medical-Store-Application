package Project.Model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class responsible for managing customer data and operations.
 * Handles customer list management and business logic.
 */
public class CustModel {
    private ArrayList<Customer> custList;
    private boolean serializationMode;
    private static final String CUSTOMERS_FILE = "customers.txt";
    private static final String CUSTOMERS_SER_FILE = "customers.ser";

    /**
     * Constructor to initialize the model with existing customers.
     * @param custList Initial list of customers, can be null
     */
    public CustModel(ArrayList<Customer> custList) {
        this.custList = custList != null ? custList : new ArrayList<>();
        this.serializationMode = false; // Default to text file mode
    }

    /**
     * Returns a copy of the customer list to prevent external modifications.
     * @return A new ArrayList containing all customers
     */
    public ArrayList<Customer> getCustList() {
        return new ArrayList<>(custList);
    }

    /**
     * Replaces the entire customer list with a new one.
     * @param customers The new list of customers
     */
    public void setCustList(List<Customer> customers) {
        this.custList.clear();
        this.custList.addAll(customers);
    }

    /**
     * Adds a new customer to the list.
     * @param customer The customer to add
     * @return true if the customer was added successfully, false if the customer already exists
     */
    public boolean addCust(Customer customer) {
        if (customer == null || findInList(customer.getPps()) != null) {
            return false;
        }
        custList.add(customer);
        return true;
    }

    /**
     * Removes a customer from the list.
     * @param pps The PPS number of the customer to remove
     * @return true if the customer was removed successfully, false if the customer was not found
     */
    public boolean removeCust(String pps) {
        if (pps == null || pps.isEmpty()) {
            return false;
        }

        Customer customer = findInList(pps);
        if (customer != null) {
            custList.remove(customer);
            return true;
        }
        return false;
    }

    /**
     * Finds a customer by their PPS number.
     * @param pps The PPS number to search for
     * @return The customer if found, null otherwise
     */
    public Customer findInList(String pps) {
        return custList.stream()
            .filter(c -> c.getPps().equals(pps))
            .findFirst()
            .orElse(null);
    }

    /**
     * Updates a customer in the list.
     * @param customer The customer to update
     * @return true if the customer was updated successfully, false otherwise
     */
    public boolean updateCustomer(Customer customer) {
        if (customer == null) {
            return false;
        }

        int index = custList.indexOf(findInList(customer.getPps()));
        if (index != -1) {
            custList.set(index, customer);
            return true;
        }
        return false;
    }

    /**
     * Loads all customers from the appropriate storage.
     * @return true if loading was successful, false otherwise
     */
    public boolean loadCustomers() {
        if (serializationMode) {
            return loadFromSerializedFile();
        } else {
            return loadFromTextFile();
        }
    }

    /**
     * Saves all customers to the appropriate storage.
     * @return true if saving was successful, false otherwise
     */
    public boolean saveCustomers() {
        if (serializationMode) {
            return saveToSerializedFile();
        } else {
            return saveToTextFile();
        }
    }

    private boolean loadFromTextFile() {
        File file = new File(CUSTOMERS_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("Error creating customers file: " + e.getMessage());
                return false;
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(CUSTOMERS_FILE))) {
            custList.clear();
            String line;
            while ((line = reader.readLine()) != null) {
                Customer customer = Customer.stringFromLine(line);
                if (customer != null) {
                    custList.add(customer);
                }
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error loading customers from text file: " + e.getMessage());
            return false;
        }
    }

    private boolean saveToTextFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CUSTOMERS_FILE))) {
            for (Customer customer : custList) {
                writer.write(customer.toStringForFile());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error saving customers to text file: " + e.getMessage());
            return false;
        }
    }

    private boolean loadFromSerializedFile() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(CUSTOMERS_SER_FILE))) {
            @SuppressWarnings("unchecked")
            ArrayList<Customer> loadedCustomers = (ArrayList<Customer>) in.readObject();
            custList = loadedCustomers;
            return true;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading customers from serialized file: " + e.getMessage());
            return false;
        }
    }

    private boolean saveToSerializedFile() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(CUSTOMERS_SER_FILE))) {
            out.writeObject(custList);
            return true;
        } catch (IOException e) {
            System.err.println("Error saving customers to serialized file: " + e.getMessage());
            return false;
        }
    }

    public void setSerializationMode(boolean mode) {
        this.serializationMode = mode;
    }

    public boolean isSerializationMode() {
        return serializationMode;
    }
}