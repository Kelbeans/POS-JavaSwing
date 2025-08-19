package common.components;

import common.helperMethods.BarcodeScanner;
import common.helperMethods.DollarConversion;
import common.dto.ApiResponse;
import common.helperMethods.TransactionIdGenerator;
import config.ApiConfig;
import config.SocketConfig;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Pane {

    private JLayeredPane layeredPane;
    private JTable table;
    private DefaultTableModel model;
    private JScrollPane scrollPane;
    private TransactionIdGenerator transactionIdGenerator;
    private Modal popUpModal;
    private Label label;

    // Existing variables
    private List<Double> prices;
    private SocketConfig clientSideVj;
    private Label customLabel;
    private double totalAfterTax;
    private double discountedTotalAmount;
    private double total;
    private double discounGet;
    private double price;
    private double totalWithTax;
    private String transactionId;
    private String discountType;
    private String discountPercent;
    private int labelY = 25;

    // Labels for totals
    private JLabel totalLabel;
    private JLabel computedTaxLabel;
    private JLabel discountLabel;
    private JLabel totalWithTaxLabel;
    private JLabel nextTotalLabel;
    private JLabel customerChangeLabel;

    // Item tracking
    private Set<String> itemSet;
    private HashMap<String, Integer> itemQuantityHashMap = new HashMap<>();
    private HashMap<String, JLabel> itemLabels = new HashMap<>();

    public Pane() {
        layeredPane = new JLayeredPane();
        layeredPane.setBounds(600, 1, 600, 400); // Reduced height to show only Total Before Tax
        layeredPane.setBackground(Color.white);
        layeredPane.setOpaque(true);

        // Create a more professional border with title
        Border innerBorder = BorderFactory.createLineBorder(new Color(50, 50, 50), 1);
        Border outerBorder = BorderFactory.createEmptyBorder(17, 5, 5, 5);
        Border compoundBorder = BorderFactory.createCompoundBorder(outerBorder, innerBorder);

        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                compoundBorder,
                "ORDER DETAILS",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                new Color(50, 50, 50)
        );

        titledBorder.setTitleJustification(TitledBorder.CENTER);
        layeredPane.setBorder(titledBorder);

        // Initialize existing variables
        prices = new ArrayList<>();
        clientSideVj = new SocketConfig("localhost", 8080);
        clientSideVj.sendLogAsync("Mock Basic Point of Sales System is Online!");
        customLabel = new Label();
        totalAfterTax = discountedTotalAmount;
        itemSet = new HashSet<>();

        // Create the table
        createTable();

        // Create total labels
        createTotalLabels();
    }

    private void createTable() {
        // Column Names
        String[] columnNames = {"Description", "Price", "Quantity", "Total"};

        // Table Model
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // make cells read-only
            }
        };

        table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.setFillsViewportHeight(true);

        // Set preferred width for Description column
        TableColumn descriptionColumn = table.getColumnModel().getColumn(0);
        descriptionColumn.setPreferredWidth(250); // Increased width for Description column

        // Improve table appearance
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(new Color(184, 207, 229));
        table.setSelectionForeground(Color.BLACK);
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        // Create scroll pane for the table
        scrollPane = new JScrollPane(table);
        scrollPane.setBounds(10, 30, 580, 300); // Height for table only
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().setBackground(Color.WHITE);

        layeredPane.add(scrollPane, JLayeredPane.DEFAULT_LAYER);
    }

    private void createTotalLabels() {

        totalLabel = customLabel.getLabel("totalBeforeTax", total);
        computedTaxLabel = customLabel.getLabel("computedTax", total);
        discountLabel = customLabel.getLabel("discount", discounGet);
        totalWithTaxLabel = customLabel.getLabel("totalWithTax", total);
        nextTotalLabel = customLabel.getLabel("nextTotal", total);
        customerChangeLabel = customLabel.getLabel("customerChange", total);

        // Add only totalLabel to the layered pane by default
        layeredPane.add(totalLabel, JLayeredPane.DEFAULT_LAYER);
    }

    public JLayeredPane displayPane() {
        return layeredPane;
    }

    // Add or update item in the table
    public void addOrUpdateItem(String itemName, double price, int quantity) {
        SwingUtilities.invokeLater(() -> {
            double totalPrice = price * quantity;

            // Check if item already exists in the table
            for (int i = 0; i < model.getRowCount(); i++) {
                if (model.getValueAt(i, 0).equals(itemName)) {
                    // Update existing row
                    model.setValueAt(String.format("$%.2f", price), i, 1);
                    model.setValueAt(quantity, i, 2);
                    model.setValueAt(String.format("$%.2f", totalPrice), i, 3);
                    System.out.println("Updated item: " + itemName + " with price: $" + price + " and quantity: " + quantity);
                    updateTotalDisplay();
                    return;
                }
            }

            // Add new row
            model.addRow(new Object[]{
                    itemName,
                    String.format("$%.2f", price),
                    quantity,
                    String.format("$%.2f", totalPrice)
            });
            System.out.println("Added new item: " + itemName + " with price: $" + price + " and quantity: " + quantity);
            updateTotalDisplay();
        });
    }

    // Remove an item from the table
    public void removeItem(String itemName) {
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < model.getRowCount(); i++) {
                if (model.getValueAt(i, 0).equals(itemName)) {
                    model.removeRow(i);
                    System.out.println("Removed item: " + itemName);
                    updateTotalDisplay();
                    return;
                }
            }
        });
    }

    // Clear all items from the table
    public void clearAllItems() {
        SwingUtilities.invokeLater(() -> {
            model.setRowCount(0);
            System.out.println("Cleared all items from table");
            updateTotalDisplay();
        });
    }

    public void addTextToScreen(String itemName, Double scannedPrice) {
        double getPrice = itemPrice(itemName);
        double finalPrice;

        customLabel = new Label();

        if (scannedPrice != null && scannedPrice > 0) {
            finalPrice = scannedPrice;
        } else if (getPrice > 0) {
            finalPrice = getPrice;
        } else {
            finalPrice = 0.00;
        }

        // Quantity Function Calling
        quantityPerItem(itemName);

        if (itemSet == null) {
            itemSet = new HashSet<>();
        }

        if (!"Next Dollar".equalsIgnoreCase(itemName) &&
                !"Exact Dollar".equalsIgnoreCase(itemName) &&
                !"Void Transaction".equalsIgnoreCase(itemName) &&
                !"Void Item".equalsIgnoreCase(itemName) &&
                !"Quantity Change".equalsIgnoreCase(itemName)) {

            int quantity = itemQuantityHashMap.getOrDefault(itemName, 1);

            clientSideVj.sendLogAsync("Item Added/Updated: " + itemName
                    + " Price: " + finalPrice
                    + " Quantity: " + quantity);

            // Add/update item in the table
            addOrUpdateItem(itemName, finalPrice, quantity);

            if (!itemSet.contains(itemName)) {
                itemSet.add(itemName);
                clientSideVj.sendLogAsync("Item Added: " + itemName + " \nPrice: " + finalPrice + " \nQuantity: " + quantity);
            } else {
                clientSideVj.sendLogAsync("Item Updated: " + itemName + " \nPrice: " + finalPrice + " \nUpdated Quantity: " + quantity);
            }

            resetUi();
        }

        prices.add(finalPrice);
        total = calculateTotal();

        // Calling Action Button Function
        actionButtons(itemName);

        // Update total labels
        totalLabel.setText(
                String.format("%-93s$%.2f", "Total Before Tax: ", customLabel.getDouble("totalBeforeTax", total))
        );
        computedTaxLabel.setText(
                String.format("%-90s$%.2f", "Computed Tax 7%: ", customLabel.getDouble("computedTax", discountedTotalAmount))
        );

        if (discountType != null && !discountType.isEmpty()) {
            if (discountType.equalsIgnoreCase("SENIOR")) {
                discountLabel.setText(
                        String.format("Discount (%s - %s): %-63s $%.2f",
                                discountType,
                                discountPercent,
                                "",
                                customLabel.getDouble("discount", discounGet))
                );
            } else if (discountType.equalsIgnoreCase("COUPON")) {
                discountLabel.setText(
                        String.format("Discount (%s - %s): %-58s  $%.2f",
                                discountType,
                                discountPercent,
                                "",
                                customLabel.getDouble("discount", discounGet))
                );
            } else {
                discountLabel.setText(
                        String.format("Discount (%s - %s): %-65s  $%.2f",
                                discountType,
                                discountPercent,
                                "",
                                customLabel.getDouble("discount", discounGet))
                );
            }
        }

        totalWithTaxLabel.setText(String.format("%-92s$%.2f", "Discounted Total:", customLabel.getDouble("totalWithTax", discountedTotalAmount)));
        totalAfterTax = customLabel.getDouble("totalWithTax", discountedTotalAmount);
    }

    public double itemPrice(String itemName) {
        switch (itemName) {
            case "Donut":
                price = 5.00;
                break;
            case "Coffee":
                price = 6.00;
                break;
            case "Hotdog":
                price = 8.00;
                break;
            case "Ice Cream":
                price = 2.00;
                break;
            case "Juice":
                price = 1.50;
                break;
            case "Milk Shake":
                price = 3.50;
                break;
            case "Next Dollar":
            case "Exact Dollar":
            case "Void Transaction":
            case "Void Item":
            case "Quantity Change":
                price = 0.00;
                break;
            default:
                break;
        }
        return price;
    }

    public Double convertToNextDollar(Double totalAfterTax) {
        DollarConversion dollarConversion = new DollarConversion();
        clientSideVj.sendLogAsync("Converting to Next Dollar: " + String.format("$%.2f", totalAfterTax));
        return dollarConversion.getNextDollarValue(totalAfterTax);
    }

    private double calculateTotal() {
        Double calculatedTotal = prices.stream().mapToDouble(Double::doubleValue).sum();
        clientSideVj.sendLogAsync("Calculated Total " + calculatedTotal);
        return calculatedTotal;
    }

    public void scanBarcode() {
        BarcodeScanner barcodeScanner = new BarcodeScanner(System.in, this);
        barcodeScanner.scanBarcode();
    }

    public void actionButtons(String itemName) {
        popUpModal = new Modal();
        if (!itemQuantityHashMap.isEmpty()) {
            if (itemName.equalsIgnoreCase("Next Dollar") || itemName.equalsIgnoreCase("Exact Dollar")) {
                transactionIdGenerator = new TransactionIdGenerator();
                transactionId = transactionIdGenerator.generateTransactionId();
                nextDollarButtonIsClicked(true, itemName, totalAfterTax);
            } else if (itemName.equalsIgnoreCase("Void Transaction")) {
                String userChoice = popUpModal.displayModal("Void Transaction Confirmation",
                        "Are you sure you want to void the transaction?",
                        "Yes",
                        "No");

                if ("Yes".equals(userChoice)) {
                    voidTransact();
                } else {
                    System.out.println("Void Transaction Cancelled");
                    clientSideVj.sendLogAsync("Void Transaction Cancelled");
                }
            } else {
                // Reset UI for other actions (Void Item, Quantity Change)
                resetUi();
                if (itemName.equalsIgnoreCase("Void Item")) {
                    voidItemTransaction(true);
                } else if (itemName.equalsIgnoreCase("Quantity Change")) {
                    changeItemQuantity(true, "Juice", 1);
                }
            }
            System.out.println("Item Quantity HashMap: " + itemQuantityHashMap);
        } else {
            popUpModal.displayModal("Empty Cart", "No items in the cart. Please add items to proceed.", "OK", "Cancel");
            clientSideVj.sendLogAsync("Item Quantity HashMap is Empty");
            System.out.println("Item Quantity HashMap is Empty");
        }
    }

    public void nextDollarButtonIsClicked(boolean showModal, String buttonName, Double value) {
        popUpModal = new Modal();

        DollarConversion dollarConversion = new DollarConversion();
        totalWithTax = dollarConversion.getNextDollarValue(discountedTotalAmount);
        clientSideVj.sendLogAsync("Total With Tax: " + totalWithTax);

        if (showModal) {
            String discountChoice = popUpModal.displayInputModal("Discount",
                    "Select a Type of Discount",
                    "Enter Discount Coupon Value",
                    "Proceed",
                    "Cancel");

            if ("Cancel".equals(discountChoice) || discountChoice == null) {
                System.out.println("Discount Cancelled");
                clientSideVj.sendLogAsync("Discount Cancelled");
                resetUi(); // Reset UI on cancel
                return;
            }

            if (discountChoice != null && !discountChoice.equals("Proceed")) {
                discountType = discountChoice;
                String couponCode = null;

                if (discountChoice.startsWith("COUPON:")) {
                    discountType = "COUPON";
                    couponCode = discountChoice.substring(7);
                    System.out.println("Coupon discount selected with code: " + couponCode);
                    clientSideVj.sendLogAsync("Coupon discount selected with code: " + couponCode);
                } else {
                    discountType = discountChoice;
                    couponCode = discountChoice;
                    System.out.println(discountType + " discount selected");
                    clientSideVj.sendLogAsync(discountType + " discount selected");
                }

                discountApi(discountType, couponCode);

                Double totalAfterTax = customLabel.getDouble("totalWithTax", discountedTotalAmount);


                String paymentChoice = popUpModal.displayModal("Payment Transaction Confirmation",
                        "Select a Payment Method",
                        "Cash Payment",
                        "Credit/Debit");

                if ("Cash Payment".equals(paymentChoice)) {
                    clientSideVj.sendLogAsync("Cash Payment is Selected");
                    System.out.println("Cash Payment is Selected");
                    layeredPane.setBounds(600, 1, 600, 480);
                    customerChangeLabel.setBounds(10,450, 580,20);
                    nextTotalLabel.setVisible(true);
                    // Set customer change for cash payment
                    if (buttonName.equalsIgnoreCase("Exact Dollar")) {
                        nextTotalLabel.setText(String.format("%-93s$%.2f", "Exact Dollar Value: ", totalAfterTax));
                        customerChangeLabel.setText(String.format("%-90s $%.2f", "Customer Change:" , customLabel.getDouble("customerChangeExactDollar", totalAfterTax)));
                        clientSideVj.sendLogAsync("Exact Dollar Value: " + String.format("$%.2f", totalAfterTax));
                    } else {
                        nextTotalLabel.setText(String.format("%-93s$%.2f", "Next Dollar Value: ", convertToNextDollar(totalAfterTax)));
                        customerChangeLabel.setText(String.format("%-90s $%.2f", "Customer Change:", customLabel.getDouble("customerChangeNextDollar", totalAfterTax)));
                    }

                } else if ("Credit/Debit".equals(paymentChoice)) {
                    System.out.println("Credit/Debit Payment is Selected");
                    clientSideVj.sendLogAsync("Credit/Debit Payment is Selected");
                    //Hide the next dollar label
                    nextTotalLabel.setVisible(false);
                    layeredPane.setBounds(600, 1, 600, 460);
                    customerChangeLabel.setBounds(10,430, 580,20);
                    customerChangeLabel.setText(String.format("%-80sNO CHANGE", "Credit Card Payment -"));
                } else {
                    System.out.println("Payment Cancelled");
                    clientSideVj.sendLogAsync("Payment Cancelled");
                    resetUi(); // Reset UI on cancel
                    return;
                }

                clientSideVj.sendLogAsync("Please pay a total of: " + String.format("$%.2f", value));
                System.out.println("Please pay a total of: " + String.format("$%.2f", value));
            } else {
                System.out.println("No discount type selected");
                clientSideVj.sendLogAsync("No discount type selected");
                resetUi(); // Reset UI if no discount selected
                return;
            }
        }

        // Expand pane and show all labels
        totalWithTaxLabel.setOpaque(true);
        totalWithTaxLabel.setBackground(new Color(0x727D73));
        // Add all labels to layeredPane and make them visible
        layeredPane.removeAll();
        layeredPane.add(scrollPane, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(totalLabel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(computedTaxLabel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(discountLabel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(totalWithTaxLabel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(nextTotalLabel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(customerChangeLabel, JLayeredPane.DEFAULT_LAYER);

        // Make all labels visible
        computedTaxLabel.setVisible(true);
        discountLabel.setVisible(true);
        totalWithTaxLabel.setVisible(true);
        customerChangeLabel.setVisible(true);

        // Refresh the display
        layeredPane.revalidate();
        layeredPane.repaint();
    }

    private void voidTransact() {
            // Reset logic only executes if user clicks "Yes"
            SwingUtilities.invokeLater(() -> {
                model.setRowCount(0); // Clear all item rows

                // Clear collections
                itemQuantityHashMap.clear();
                itemLabels.clear();
                itemSet.clear();
                prices.clear();

                // Reset label values
                totalLabel.setText(
                        String.format("%-93ss$%.2f", "Total Before Tax: ", customLabel.getDouble("totalBeforeTax", 0.00))
                );
                resetUi();
            });

            System.out.println("Void Transaction Confirmed");
            clientSideVj.sendLogAsync("Void Transaction Confirmed");

    }

    public HashMap<String, Integer> quantityPerItem(String itemName) {
        if (!itemName.equalsIgnoreCase("Next Dollar")
                && !itemName.equalsIgnoreCase("Exact Dollar")
                && !itemName.equalsIgnoreCase("Void Transaction")
                && !itemName.equalsIgnoreCase("Void Item")
                && !itemName.equalsIgnoreCase("Quantity Change")) {
            if (itemQuantityHashMap.containsKey(itemName)) {
                int currentQuantity = itemQuantityHashMap.get(itemName);
                itemQuantityHashMap.put(itemName, currentQuantity + 1);
                System.out.println("Updated Item Name: " + itemName + "\nUpdated Quantity: " + (currentQuantity + 1));
            } else {
                itemQuantityHashMap.put(itemName, 1);
                System.out.println("Item Name: " + itemName + " \nQuantity: 1");
            }
        }
        System.out.println("Item Quantity HashMap: " + itemQuantityHashMap);
        return itemQuantityHashMap;
    }

    public void voidItemTransaction(boolean showModal) {
        popUpModal = new Modal();
        if (showModal) {
            String userChoice = popUpModal.displayInputModal("Void Item Transaction",
                    "Enter an Item to Void",
                    "Item to void",
                    "Proceed",
                    "Cancel");

            if ("Cancel".equals(userChoice) || userChoice == null) {
                System.out.println("Voiding Item: is cancelled");
                clientSideVj.sendLogAsync("Voiding Item: is cancelled");
                return;
            }

            String itemName = "Proceed".equals(userChoice) ? null : userChoice;

            if (itemName == null || itemName.trim().isEmpty()) {
                System.out.println("No valid item name provided for voiding");
                clientSideVj.sendLogAsync("No valid item name provided for voiding");
                return;
            }

            System.out.println("Item to void: " + itemName);
            clientSideVj.sendLogAsync("Item to void: " + itemName);
            voidItemConfirmation(showModal, itemName);
        }
    }

    public void voidItemConfirmation(boolean showModal, String itemName) {
        popUpModal = new Modal();
        if (showModal) {
            String userChoice = popUpModal.displayModal("Void Item Confirmation",
                    "Are you sure you want to remove: " + (itemName != null ? itemName : "Unknown Item"),
                    "Yes", "No");

            if (!"Yes".equals(userChoice)) {
                System.out.println("Voiding Item: " + (itemName != null ? itemName : "Unknown Item") + " is cancelled");
                clientSideVj.sendLogAsync("Voiding Item: " + (itemName != null ? itemName : "Unknown Item") + " is cancelled");
                return;
            }
            System.out.println("Item: " + (itemName != null ? itemName : "Unknown Item") + " is voided");
            clientSideVj.sendLogAsync("Item: " + (itemName != null ? itemName : "Unknown Item") + " is voided");
        }

        // Remove item from collections and table
        if (itemName != null) {

            removeItem(itemName);
            itemQuantityHashMap.remove(itemName);
            itemSet.remove(itemName);
            itemLabels.remove(itemName);
            double itemPrice = itemPrice(itemName);
            int quantity = itemQuantityHashMap.getOrDefault(itemName, 1);
            for (int i = 0; i < quantity; i++) {
                prices.remove(Double.valueOf(itemPrice));
            }
            total = calculateTotal();
            updateTotalDisplay();
            resetUi();
        }
    }

    private void updateTotalDisplay() {
        totalLabel.setText(String.format("%-93s$%.2f", "Total Before Tax:", total));
        totalWithTaxLabel.setText(String.format("%-50s$%.2f", "Discounted Total:", customLabel.getDouble("totalWithTax", discountedTotalAmount)));
    }

    public void changeItemQuantity(boolean showModal, String itemName, int newQuantity) {
        if (popUpModal == null) {
            System.err.println("popUpModal is not initialized!");
            return;
        }
        if (clientSideVj == null) {
            System.err.println("clientSideVj is not initialized!");
            return;
        }

        String actualItemName = itemName;
        int actualNewQuantity = newQuantity;

        if (showModal) {
            String modalTitle = "Quantity Change Confirmation";
            String modalMessage = "Enter an Item name and its new Quantity";
            String userChoice = popUpModal.displayQuantityModal(modalTitle, modalMessage, "Proceed", "Cancel");

            if ("Cancel".equals(userChoice)) {
                System.out.println("Quantity change cancelled");
                clientSideVj.sendLogAsync("Quantity change cancelled");
                return;
            }

            String fetchInputtedValue = popUpModal.returnItemValues();
            popUpModal.processItemValues(fetchInputtedValue);

            String trimmedInput = fetchInputtedValue.trim();
            int lastSpaceIndex = trimmedInput.lastIndexOf(' ');

            if (lastSpaceIndex != -1) {
                String potentialItemName = trimmedInput.substring(0, lastSpaceIndex);
                String potentialQuantity = trimmedInput.substring(lastSpaceIndex + 1);

                try {
                    int dialogQuantity = Integer.parseInt(potentialQuantity);
                    actualItemName = potentialItemName;
                    actualNewQuantity = dialogQuantity;
                    System.out.println("Setting new quantity from dialog: " + dialogQuantity);
                    clientSideVj.sendLogAsync("Setting new quantity from dialog: " + dialogQuantity);
                } catch (NumberFormatException e) {
                    actualItemName = trimmedInput;
                    System.out.println("No valid quantity found, using whole string as item name and parameter quantity: " + actualNewQuantity);
                    clientSideVj.sendLogAsync("No valid quantity found, using whole string as item name and parameter quantity: " + actualNewQuantity);
                }
            } else {
                actualItemName = trimmedInput;
                System.out.println("No spaces found, using whole string as item name: " + actualItemName);
                clientSideVj.sendLogAsync("No spaces found, using whole string as item name: " + actualItemName);
            }

            System.out.println("Item to change quantity: " + fetchInputtedValue);
            System.out.println("Extracted item name: " + actualItemName);
            System.out.println("Setting quantity to: " + actualNewQuantity);
            clientSideVj.sendLogAsync("Item to change quantity: " + fetchInputtedValue);
            clientSideVj.sendLogAsync("Extracted item name: " + actualItemName);
            clientSideVj.sendLogAsync("Setting quantity to: " + actualNewQuantity);
        }

        if (actualItemName != null && itemQuantityHashMap.containsKey(actualItemName)) {
            int currentQuantity = itemQuantityHashMap.getOrDefault(actualItemName, 1);

            System.out.println("Current quantity for " + actualItemName + ": " + currentQuantity);
            System.out.println("Setting new quantity to: " + actualNewQuantity);
            clientSideVj.sendLogAsync("Current quantity for " + actualItemName + ": " + currentQuantity);
            clientSideVj.sendLogAsync("Setting new quantity to: " + actualNewQuantity);

            if (actualNewQuantity <= 0) {
                removeItemCompletely(actualItemName);
            } else {
                updateItemQuantity(actualItemName, actualNewQuantity);
            }
        } else {
            System.out.println("Item '" + actualItemName + "' not found in the current transaction");
            clientSideVj.sendLogAsync("Item '" + actualItemName + "' not found in the current transaction");
        }
    }

    private void removeItemCompletely(String itemName) {
        System.out.println("Removing item completely: " + itemName);
        int currentQuantity = itemQuantityHashMap.getOrDefault(itemName, 1);
        double itemPriceValue = itemPrice(itemName);
        for (int i = 0; i < currentQuantity; i++) {
            prices.remove(Double.valueOf(itemPriceValue));
        }
        itemLabels.remove(itemName);
        itemQuantityHashMap.remove(itemName);
        itemSet.remove(itemName);
        removeItem(itemName);
        total = calculateTotal();
        customLabel.getDouble("totalBeforeTax", total);
        updateTotalDisplay();
        resetUi();
        System.out.println("Item '" + itemName + "' completely removed. New total: $" + total);
        clientSideVj.sendLogAsync("Item '" + itemName + "' completely removed. New total: $" + total);
    }

    private void updateItemQuantity(String itemName, int newQuantity) {
        int currentQuantity = itemQuantityHashMap.getOrDefault(itemName, 1);
        double itemPriceValue = itemPrice(itemName);
        int quantityDifference = newQuantity - currentQuantity;
        if (quantityDifference > 0) {
            for (int i = 0; i < quantityDifference; i++) {
                prices.add(itemPriceValue);
            }
            System.out.println("Added " + quantityDifference + " more of " + itemName);
            clientSideVj.sendLogAsync("Added " + quantityDifference + " more of " + itemName);
        } else if (quantityDifference < 0) {
            int pricesToRemove = Math.abs(quantityDifference);
            for (int i = 0; i < pricesToRemove; i++) {
                prices.remove(Double.valueOf(itemPriceValue));
            }
            System.out.println("Removed " + pricesToRemove + " of " + itemName);
            clientSideVj.sendLogAsync("Removed " + pricesToRemove + " of " + itemName);
        }
        itemQuantityHashMap.put(itemName, newQuantity);
        addOrUpdateItem(itemName, itemPriceValue, newQuantity);
        total = calculateTotal();
        customLabel.getDouble("totalBeforeTax", total);
        updateTotalDisplay();
        resetUi();
        System.out.println("Updated " + itemName + " quantity to " + newQuantity + ". New total: $" + total);
        clientSideVj.sendLogAsync("Updated " + itemName + " quantity to " + newQuantity + ". New total: $" + total);
    }

    public ApiResponse discountApi(String discountType, String discount) {
        ApiConfig apiConfig = new ApiConfig();
        clientSideVj.sendLogAsync("Discount API is being called for type: " + discountType);

        String jsonRequest = String.format(
                "{\"transactionId\":\"%s\", \"discount\":\"%s\", \"totalAmountBeforeTax\":%.2f}",
                transactionId, discount, total
        );

        clientSideVj.sendLogAsync("Discount Type (URL param): " + discountType);
        clientSideVj.sendLogAsync("API Parameters: " + jsonRequest);

        String couponCodeParam = "COUPON".equals(discountType) ? discount : null;

        ApiResponse discountResponse = apiConfig.callDiscountApi(jsonRequest, discountType, couponCodeParam);

        try {
            String responseTransactionId = discountResponse.getTransactionId();
            discountPercent = discountResponse.getDiscount();
            Double discountAmount = discountResponse.getAmountDiscounted();
            Double finalTotal = discountResponse.getTotalAmountBeforeTax();

            discounGet = discountAmount;
            discountedTotalAmount = finalTotal;

            clientSideVj.sendLogAsync("API Response: " + discountResponse);
            System.out.println("Discount Response: " + responseTransactionId + " " + discountPercent + " " + discountAmount + " " + finalTotal);
        } catch (Exception e) {
            clientSideVj.sendLogAsync("Failed to get discount: " + e.getMessage());
            System.err.println("Failed to get discount: " + e.getMessage());
        }

        return discountResponse;
    }

    public void resetUi() {
        // Reset pane size and show only Total Before Tax label
        layeredPane.setBounds(600, 1, 600, 400);
        layeredPane.removeAll();
        layeredPane.add(scrollPane, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(totalLabel, JLayeredPane.DEFAULT_LAYER);
        totalLabel.setBounds(10, 350, 580, 25);
        computedTaxLabel.setVisible(false);
        discountLabel.setVisible(false);
        totalWithTaxLabel.setVisible(false);
        nextTotalLabel.setVisible(false);
        customerChangeLabel.setVisible(false);
        totalWithTaxLabel.setOpaque(false);
        layeredPane.revalidate();
        layeredPane.repaint();
        table.revalidate();
        table.repaint();
    }
}