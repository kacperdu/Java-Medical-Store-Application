package Project.Model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class responsible for managing welcome screen data and menu options.
 * Handles welcome message and menu options persistence.
 */
public class WelcomeModel {
    // Fields
    private String welcomeMessage;
    private String[] menuOptions;
    private final String file_Name;
    private final String serial_FileName;
    private boolean isSerializationMode;

    /**
     * Constructor to initialize the welcome model with file names.
     * @param fileName Name of the text file for storing welcome data
     * @param serialFileName Name of the serialized file for storing welcome data
     */
    public WelcomeModel(String fileName, String serialFileName) {
        this.welcomeMessage = "Welcome to the Store Management System";
        this.menuOptions = new String[] {
            "1. Customer Management",
            "2. Product Management",
            "3. Order Management",
            "4. Exit"
        };
        this.file_Name = fileName;
        this.serial_FileName = serialFileName;
        this.isSerializationMode = false;
    }

    // Mode Methods
    /**
     * Sets the serialization mode for file operations.
     * @param mode true to use serialization, false to use text files
     */
    public void setSerializationMode(boolean mode) {
        this.isSerializationMode = mode;
    }
    /**
     * Returns whether the model is in serialization mode.
     * @return true if using serialization, false if using text files
     */
    public boolean isSerializationMode() {
        return this.isSerializationMode;
    }

    // File I/O Methods

    /**
     * Saves welcome data to a text file.
     * @throws IOException if there is an error writing to the file
     */
    private void saveToTextFile() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file_Name))) {
            writer.write(welcomeMessage);
            writer.newLine();
            for (String option : menuOptions) {
                writer.write(option);
                writer.newLine();
            }
        }
    }

    /**
     * Loads welcome data from a text file.
     * @throws IOException if there is an error reading from the file
     */
    private void loadFromTextFile() throws IOException {
        File file = new File(file_Name);
        if (!file.exists()) {
            file.createNewFile();
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file_Name))) {
            welcomeMessage = reader.readLine();
            List<String> options = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                options.add(line);
            }
            menuOptions = options.toArray(new String[0]);
        }
    }

    /**
     * Serializes the welcome data to a binary file.
     * @throws IOException if there is an error writing to the file
     */
    public void serializeMessage() throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(serial_FileName))) {
            out.writeObject(welcomeMessage);
            out.writeObject(menuOptions);
        }
    }

    /**
     * Deserializes the welcome data from a binary file.
     * @throws IOException if there is an error reading from the file
     * @throws ClassNotFoundException if the class of a serialized object cannot be found
     */
    public void deserializeMessage() throws IOException, ClassNotFoundException {
        File file = new File(serial_FileName);
        if (!file.exists()) {
            System.out.println("No serialized file found");
            return;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(serial_FileName))) {
            welcomeMessage = (String) in.readObject();
            menuOptions = (String[]) in.readObject();
        }
    }
} 