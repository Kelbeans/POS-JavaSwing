//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import common.components.Button;
import common.components.Pane;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

public class Main {
    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 1000;
    private static final String WINDOW_TITLE = "Basic Point of Sale System";

    // Button positions
    private static final int[][] POPULAR_ITEM_POSITIONS = {
            {0, 20}, {0, 220}, {200, 20}, {400, 20}, {200, 220}, {400, 220}
    };

    private static final int[][] ACTION_BUTTON_POSITIONS = {
            {0, 440}, {200, 440}, {400, 440}, {0, 640}, {200, 640}
    };

    // Item names
    private static final String[] POPULAR_ITEMS = {
            "Coffee", "Hotdog", "Donut", "Ice Cream", "Juice", "Milk Shake"
    };

    private static final String[] ACTION_ITEMS = {
            "Exact Dollar", "Next Dollar", "Void Transaction", "Void Item", "Quantity Change"
    };

    public static void main(String[] args) throws Exception {
        System.out.println("******** Items in the Basket **********");

        // Initialize main components
        JFrame mainWindow = createMainWindow();
        Pane pane = new Pane();

        // Add labels
        addLabels(mainWindow);

        // Add popular item buttons
        addPopularItemButtons(mainWindow, pane);

        // Add action buttons
        addActionButtons(mainWindow, pane);

        // Add main pane
        mainWindow.add(pane.displayPane());

        // Show window and start barcode scanning
        showWindow(mainWindow);
        pane.scanBarcode();
    }

    private static JFrame createMainWindow() {
        JFrame mainWindow = new JFrame();
        mainWindow.setTitle(WINDOW_TITLE);
        mainWindow.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        mainWindow.setLayout(null);
        mainWindow.setLocationRelativeTo(null);
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return mainWindow;
    }

    private static void addLabels(JFrame mainWindow) {
        Font labelFont = new Font("Segoe UI", Font.BOLD, 15);
        JLabel popularItemsLabel = createLabel("Popular Items", 250, 0, labelFont);
        JLabel actionButtonLabel = createLabel("Action Buttons", 250, 420, labelFont);

        mainWindow.add(popularItemsLabel);
        mainWindow.add(actionButtonLabel);
    }


    private static JLabel createLabel(String text, int x, int y, Font font) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, 380, 20);
        label.setFont(font);
        return label;
    }

    private static void addPopularItemButtons(JFrame mainWindow, Pane pane) {
        for (int i = 0; i < POPULAR_ITEMS.length; i++) {
            Button button = new Button(pane);
            ArrayList<Integer> position = createPosition(POPULAR_ITEM_POSITIONS[i]);
            mainWindow.add(button.testButtonPanel(POPULAR_ITEMS[i], position));
        }
    }

    private static void addActionButtons(JFrame mainWindow, Pane pane) {
        for (int i = 0; i < ACTION_ITEMS.length; i++) {
            Button button = new Button(pane);
            ArrayList<Integer> position = createPosition(ACTION_BUTTON_POSITIONS[i]);
            mainWindow.add(button.testButtonPanel(ACTION_ITEMS[i], position));
        }
    }

    private static ArrayList<Integer> createPosition(int[] coords) {
        ArrayList<Integer> position = new ArrayList<>();
        position.add(coords[0]);
        position.add(coords[1]);
        return position;
    }

    private static void showWindow(JFrame mainWindow) {
        mainWindow.setVisible(true);
    }
}