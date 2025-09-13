package Project.Controller;

import Project.Model.WelcomeModel;
import Project.View.WelcomeView;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;

import java.util.Optional;

/**
 * Controller class responsible for managing the welcome screen.
 * Handles the initialization of the welcome view and its integration with the main application.
 */
public class WelcomeController {
    private WelcomeModel model;
    private WelcomeView view;
    private javafx.scene.control.TabPane tabPane;

    /**
     * Constructs a new welcome controller with the specified parameters.
     * @param model The welcome model to use
     * @param view The welcome view to use
     * @param tabPane The main tab pane of the application
     */
    public WelcomeController(WelcomeModel model, WelcomeView view, javafx.scene.control.TabPane tabPane) {
        this.model = model;
        this.view = view;
        this.tabPane = tabPane;
        initializeActions();
    }

    private void initializeActions() {}

    /**
     * Returns the main view component of the welcome interface.
     * @return The VBox containing the welcome view
     */
    public VBox getVbox() {
        return view.getView();
    }
} 