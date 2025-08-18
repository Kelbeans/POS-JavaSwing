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
import java.awt.*;
import java.util.*;

public class Pane {

    private final JLayeredPane layeredPane;
    private final JLabel totalLabel;
    private final JLabel totalWithTaxLabel;
    private final JLabel computedTaxLabel;
    private final JLabel discountLabel;
    private final JLabel customerChangeLabel;
    private final JLabel nextTotalLabel;

    private ArrayList<Double> prices;
    private Set<String> itemSet;
    private HashMap<String, Integer> itemQuantityHashMap = new HashMap<>();
    private HashMap<String, JLabel> itemLabels = new HashMap<>();


    private String transactionId;
    public String discountType;
    public String discountPercent;
    private int labelY = 25;
    private double total;
    private double totalAfterTax;
    private double discounGet;
    private double discountedTotalAmount;
    private double totalWithTax;
    private double price;

    private Label customLabel;
    private Modal popUpModal;
    private TransactionIdGenerator transactionIdGenerator;
    private SocketConfig clientSideVj;


    public Pane(){
        layeredPane = new JLayeredPane();
        layeredPane.setBounds(600, 1, 400, 560);
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
                new Color(50, 50, 50) // Dark gray text
        );

        // Add padding to the title
        titledBorder.setTitleJustification(TitledBorder.CENTER);
        layeredPane.setBorder(titledBorder);

        prices = new ArrayList<>();
        //clientSideVj = new SocketConfig("192.168.8.125", 1234); SEAN SOCKET
        clientSideVj = new SocketConfig("localhost", 8080);
        clientSideVj.sendLogAsync("Mock Basic Point of Sales System is Online!");

        customLabel = new Label();

        totalAfterTax = discountedTotalAmount;

        totalLabel = customLabel.getLabel("totalBeforeTax", total);
        computedTaxLabel = customLabel.getLabel("computedTax", total);
        discountLabel = customLabel.getLabel("discount", discounGet);
        totalWithTaxLabel = customLabel.getLabel("totalWithTax", total);
        nextTotalLabel = customLabel.getLabel("nextTotal", total);
        customerChangeLabel = customLabel.getLabel("customerChange", total);

        layeredPane.add(totalLabel, JLayeredPane.DEFAULT_LAYER);

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

        JLabel itemLabelInPanel = new JLabel();
        itemLabelInPanel.setFont(new Font("Arial ", Font.BOLD, 15));

        if (itemSet == null) {
            itemSet = new HashSet<>();
        }

        /*
            * Fix to the Quantity issue that duplicates the same label even its present.
            * We created a new class level HashMap to store the quantity of each item.
            * private HashMap<String, JLabel> itemLabels = new HashMap<>();
            * We also fix the invisible label gap.
        */
        if (
                !"Next Dollar".equalsIgnoreCase(itemName) &&
                        !"Exact Dollar".equalsIgnoreCase(itemName) &&
                        !"Void Transaction".equalsIgnoreCase(itemName) &&
                        !"Void Item".equalsIgnoreCase(itemName)  &&
                        !"Quantity Change".equalsIgnoreCase(itemName)) {

            int quantity = itemQuantityHashMap.getOrDefault(itemName, 1);

            // If you want to display in the panel the totalAmount of the specific item, you can uncomment the below code.
            // double totalItemPrice = finalPrice * quantity;
            double totalItemPrice = finalPrice * quantity;
            String displayText = String.format("%-30s$%.2f                (x%d)", itemName, price, quantity);

            if (!itemSet.contains(itemName)) {
                // New item - create new label AND increment labelY
                itemSet.add(itemName);
                itemLabelInPanel.setText(displayText);

                // Send logs to the Virtual Journal
                clientSideVj.sendLogAsync("Item Added: " + itemName + " \nPrice: " + finalPrice + " \nQuantity: " + quantity);
                itemLabels.put(itemName, itemLabelInPanel);
                itemLabelInPanel.setBounds(10, labelY, 380, 20);
                labelY += 25; // Only increment for new labels
            } else {
                // Existing item - just update the existing label, don't increment labelY
                JLabel existingLabel = itemLabels.get(itemName);
                if (existingLabel != null) {
                    existingLabel.setText(displayText);
                }
                clientSideVj.sendLogAsync("Item Updated: " + itemName + " \nPrice: " + finalPrice + " \nUpdated Quantity: " + quantity);
                System.out.println("Updated: " + displayText);
            }

            resetUi();

        }

        prices.add(finalPrice);
        total = calculateTotal();


        DollarConversion dollarConversion1 = new DollarConversion();


        //Calling Action Button Function
        actionButtons(itemName);

                totalLabel.setText(
                String.format("%-50s$%.2f", "Total Before Tax: ", customLabel.getDouble("totalBeforeTax", total))
        );
        computedTaxLabel.setText(
                String.format("%-47s$%.2f", "Computed Tax 7%: ",customLabel.getDouble("computedTax",discountedTotalAmount))
        );

        if (discountType != null && !discountType.isEmpty()) {
            if (discountType.equalsIgnoreCase("SENIOR")) {
                discountLabel.setText(
                        String.format("Discount (%s - %s): %-21s $%.2f",
                                discountType,
                                discountPercent,
                                "", // filler spaces
                                customLabel.getDouble("discount", discounGet))
                );
            } else if (discountType.equalsIgnoreCase("COUPON")) {
                discountLabel.setText(
                        String.format("Discount (%s - %s): %-17s  $%.2f",
                                discountType,
                                discountPercent,
                                "",
                                customLabel.getDouble("discount", discounGet))
                );
            } else {
                discountLabel.setText(
                        String.format("Discount (%s - %s): %-23s  $%.2f",
                                discountType,
                                discountPercent,
                                "",
                                customLabel.getDouble("discount", discounGet))
                );
            }
        }





        totalWithTaxLabel.setText(String.format("%-50s$%.2f", "Discounted Total: ",customLabel.getDouble("totalWithTax",discountedTotalAmount)));

        totalAfterTax = customLabel.getDouble("totalWithTax", discountedTotalAmount);
        /*
            * We move this code inside the Next Dollar Function to avoid the duplicate label issue and invisible gap
              itemLabelInPanel.setBounds(10, labelY, 380, 20);
              labelY += 25;
        */
        itemLabelInPanel.setOpaque(false);
        itemLabelInPanel.setVisible(true);
        layeredPane.add(itemLabelInPanel, JLayeredPane.DEFAULT_LAYER);

    }

    public double itemPrice(String itemName){
        switch (itemName){
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

    public Double convertToNextDollar(Double totalAfterTax){
        DollarConversion dollarConversion = new DollarConversion();
        clientSideVj.sendLogAsync("Converting to Next Dollar: " + String.format("$%.2f", totalAfterTax));
        return dollarConversion.getNextDollarValue(totalAfterTax);
    }


    private double calculateTotal() {

        Double calculatedTotal = prices.stream().mapToDouble(Double::doubleValue).sum();
        clientSideVj.sendLogAsync("Calculated Total " + calculatedTotal);
        return calculatedTotal;
    }


    public JLayeredPane displayPane(){
        return layeredPane;
    }

    public void scanBarcode() {
        BarcodeScanner barcodeScanner = new BarcodeScanner(System.in, this);
        barcodeScanner.scanBarcode();
    }

    /*
     * Action Buttons *
     * Next Dollar Button
     * Void Transaction Button
     * Void Item Button
     * Quantity Change Button
     */

    public void actionButtons(String itemName){
            if (!itemQuantityHashMap.isEmpty()) {
                if (itemName.equalsIgnoreCase("Next Dollar") || itemName.equalsIgnoreCase("Exact Dollar")) {
                    transactionIdGenerator = new TransactionIdGenerator();
                    transactionId = transactionIdGenerator.generateTransactionId();
                    nextDollarButtonIsClicked(true, itemName, totalAfterTax);
                }
                System.out.println("Item Quantity HashMap: " + itemQuantityHashMap);

                if (itemName.equalsIgnoreCase("Void Transaction")) {
                    voidTransact(true);
                }

                if (itemName.equalsIgnoreCase("Void Item")) {
                    voidItemTransaction(true);
                }

                if (itemName.equalsIgnoreCase("Quantity Change")) {
                    changeItemQuantity(true, "Juice", 1);
                }
            } else {
                popUpModal = new Modal();
                popUpModal.displayModal("Empty Cart", "No items in the cart. Please add items to proceed.", "OK", "Cancel");
                clientSideVj.sendLogAsync("Item Quantity HashMap is Empty");
                System.out.println("Item Quantity HashMap is Empty");
            }
    }

    /*
        * Next Dollar Case Fix to Illegal Component Issue. Need to refresh the label to display the new value.
        * This handles the Next Dollar Button and the Exact Dollar Button.
    */
    public void nextDollarButtonIsClicked(boolean showModal, String buttonName, Double value){

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

            // Check if user cancelled
            if ("Cancel".equals(discountChoice) || discountChoice == null) {
                System.out.println("Discount Cancelled");
                clientSideVj.sendLogAsync("Discount Cancelled");
                return;
            }

            // Process discount selection
            if (discountChoice != null && !discountChoice.equals("Proceed")) {
                discountType = discountChoice;
                // Parse the discount choice
                String couponCode = null;

                if (discountChoice.startsWith("COUPON:")) {
                    discountType = "COUPON";
                    couponCode = discountChoice.substring(7); // Extract coupon code after "COUPON:"
                    System.out.println("Coupon discount selected with code: " + couponCode);
                    clientSideVj.sendLogAsync("Coupon discount selected with code: " + couponCode);
                } else {
                    discountType = discountChoice; // PWD or SENIOR
                    couponCode = discountChoice; // For PWD/SENIOR, the discount value is the type itself
                    System.out.println(discountType + " discount selected");
                    clientSideVj.sendLogAsync(discountType + " discount selected");
                }

                // Call discount API with the selected type
                discountApi(discountType, couponCode);

                // Proceed to payment confirmation
                String paymentChoice = popUpModal.displayModal("Payment Transaction Confirmation",
                        "Select a Payment Method",
                        "Cash Payment",
                        "Credit/Debit");

                if ("Cash Payment".equals(paymentChoice)) {
                    clientSideVj.sendLogAsync("Cash Payment is Selected");
                    System.out.println("Cash Payment is Selected");
                } else if ("Credit/Debit".equals(paymentChoice)) {
                    System.out.println("Credit/Debit Payment is Selected");
                    clientSideVj.sendLogAsync("Credit/Debit Payment is Selected");
                } else {
                    System.out.println("Payment Cancelled");
                    clientSideVj.sendLogAsync("Payment Cancelled");
                    return;
                }

                clientSideVj.sendLogAsync("Please pay a total of: " + String.format("$%.2f", value));
                System.out.println("Please pay a total of: " + String.format("$%.2f", value));

            } else {
                System.out.println("No discount type selected");
                clientSideVj.sendLogAsync("No discount type selected");
                return;
            }
        }


        layeredPane.setBounds(600,1,400
                ,660);
        totalWithTaxLabel.setOpaque(true);
        totalWithTaxLabel.setBackground(new Color(0x727D73));

        // Remove components if they already exist
        layeredPane.remove(nextTotalLabel);
        layeredPane.remove(totalWithTaxLabel);
        layeredPane.remove(computedTaxLabel);
        layeredPane.remove(discountLabel);
        layeredPane.remove(customerChangeLabel);

        // Now add them
        discountLabel.setBounds(10,550,380,20);
        computedTaxLabel.setBounds(10,570,380,20);
        totalWithTaxLabel.setBounds(10,590,380,20);

        layeredPane.add(computedTaxLabel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(discountLabel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(totalWithTaxLabel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(nextTotalLabel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(customerChangeLabel, JLayeredPane.DEFAULT_LAYER);

        Double totalAfterTax = customLabel.getDouble("totalWithTax", discountedTotalAmount);


        if(buttonName.equalsIgnoreCase("Exact Dollar")){
            nextTotalLabel.setText(String.format("%-51s$%.2f", "Exact Dollar Value:",  totalAfterTax));
            customerChangeLabel.setText(String.format("%-48s $%.2f", "Customer Change:", customLabel.getDouble("customerChangeExactDollar", totalAfterTax)));
            clientSideVj.sendLogAsync("Exact Dollar Value: " + String.format("$%.2f", totalAfterTax));
        } else {
            nextTotalLabel.setText(String.format("%-51s$%.2f", "Next Dollar Value:", convertToNextDollar(totalAfterTax)));
            customerChangeLabel.setText(String.format("%-48s $%.2f", "Customer Change:", customLabel.getDouble("customerChangeNextDollar", totalAfterTax)));

        }

        // Refresh the display
        layeredPane.revalidate();
        layeredPane.repaint();
    }


    /*
        * Void Transaction Case Fix to Illegal Component Issue. Need to refresh the label to display the new value.
        * Need to remove all components and re-add them.
    */
    private void voidTransact(boolean showModal) {
        popUpModal = new Modal();

        if (showModal) {
            String userChoice = popUpModal.displayModal("Void Transaction Confirmation",
                    "Are you sure you want to void the transaction?",
                    "Yes",
                    "No");


            if (!"Yes".equals(userChoice)) {
                System.out.println("Void Transaction Cancelled");
                clientSideVj.sendLogAsync("Void Transaction Cancelled");
                return;
            }
            System.out.println("Void Transaction Confirmed");
            clientSideVj.sendLogAsync("Void Transaction Confirmed");
        }

        //Clear HashMaps
        itemQuantityHashMap.clear();
        itemLabels.clear();
        itemSet.clear();

        // Reset panel - only executes if showModal is false OR user clicked "Yes"
        layeredPane.removeAll();
        prices.clear();
        itemSet.clear();
        total = 0.00;
        totalAfterTax = 0.00;
        labelY = 25;
        totalLabel.setText("Total Before Tax: $0.00");
        totalWithTaxLabel.setOpaque(false);
        computedTaxLabel.setText("Computed Tax: $0.00");
        totalWithTaxLabel.setText("Total After Tax: $0.00");
        layeredPane.setBounds(600,1,400
                ,600);
        layeredPane.add(totalLabel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.revalidate();
        layeredPane.repaint();
    }

    // Fix the hashMap issue with quantity. By in case of using a FUNCTION_SCOPED currentQuantity variable we move it to CLASS_LEVEL.
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
                return; // Exit method without voiding
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

        // Remove item from collections and UI
        if (itemName != null) {

            if (itemName != null && itemLabels.containsKey(itemName)) {
                // Get references before removing
                Component itemComponent = itemLabels.get(itemName);
                double itemPrice = itemPrice(itemName); // Use your existing method

                itemLabels.remove(itemName);

                System.out.println("DEBUG: Prices before removal: " + prices);
                System.out.println("DEBUG: Total before removal: $" + calculateTotal());
                clientSideVj.sendLogAsync("DEBUG: Prices before removal: " + prices);
                clientSideVj.sendLogAsync("DEBUG: Total before removal: $" + calculateTotal());

                Double priceToRemove = Double.valueOf(itemPrice);
                prices.removeIf(price -> price.equals(priceToRemove));

                System.out.println("DEBUG: Prices after removal: " + prices);
                clientSideVj.sendLogAsync("DEBUG: Prices after removal: " + prices);

                layeredPane.remove(itemComponent);
                layeredPane.revalidate();
                layeredPane.repaint();

                repositionItemLabels();
                total = calculateTotal();
                customLabel.getDouble("totalBeforeTax", total);
                updateTotalDisplay();

                System.out.println("Item '" + itemName + "' voided. New total: $" + total);
                clientSideVj.sendLogAsync("Item '" + itemName + "' voided. New total: $" + total);

            } else {
                System.out.println("Item '" + itemName + "' not found in the current transaction");
                clientSideVj.sendLogAsync("Item '" + itemName + "' not found in the current transaction");
            }
        }
    }


    private void updateTotalDisplay() {
        totalLabel.setText(
                String.format("Total Before Tax: \t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t$%.2f",
                        customLabel.getDouble("totalBeforeTax", total))
        );
    }


    private void repositionItemLabels() {
        int currentY = 25; // Reset to starting position

        // Go through all remaining items and reposition them
        for (Map.Entry<String, JLabel> entry : itemLabels.entrySet()) {
            JLabel label = entry.getValue();
            label.setBounds(10, currentY, 380, 20);
            currentY += 25; // Move to next position
        }

        resetUi();

        // Update labelY to the next available position
        labelY = currentY;
    }

    public void changeItemQuantity(boolean showModal, String itemName, int newQuantity) {

        String actualItemName = itemName; // Use the parameter by default
        int actualNewQuantity = newQuantity; // Use the parameter by default

        if (showModal) {
            String modalTitle = "Quantity Change Confirmation";
            String modalMessage = "Enter an Item name and its new Quantity";
            String userChoice = popUpModal.displayQuantityModal(modalTitle, modalMessage, "Proceed", "Cancel");

            // Check if user cancelled
            if ("Cancel".equals(userChoice)) {
                System.out.println("Quantity change cancelled");
                clientSideVj.sendLogAsync("Quantity change cancelled");
                return;
            }

            // Get the item name from dialog input when modal is shown
            String fetchInputtedValue = popUpModal.returnItemValues();
            popUpModal.processItemValues(fetchInputtedValue);

            // Extract item name and quantity (assuming format is "ItemName Quantity")
            // Find the last space to separate item name from quantity
            String trimmedInput = fetchInputtedValue.trim();
            int lastSpaceIndex = trimmedInput.lastIndexOf(' ');

            if (lastSpaceIndex != -1) {
                String potentialItemName = trimmedInput.substring(0, lastSpaceIndex);
                String potentialQuantity = trimmedInput.substring(lastSpaceIndex + 1);

                try {
                    int dialogQuantity = Integer.parseInt(potentialQuantity);
                    actualItemName = potentialItemName; // Use everything before the last space as item name
                    actualNewQuantity = dialogQuantity; // Use the quantity from dialog as new quantity
                    System.out.println("Setting new quantity from dialog: " + dialogQuantity);
                    clientSideVj.sendLogAsync("Setting new quantity from dialog: " + dialogQuantity);
                } catch (NumberFormatException e) {
                    // If last part isn't a number, use the whole string as item name
                    actualItemName = trimmedInput;
                    System.out.println("No valid quantity found, using whole string as item name and parameter quantity: " + actualNewQuantity);
                    clientSideVj.sendLogAsync("No valid quantity found, using whole string as item name and parameter quantity: " + actualNewQuantity);
                }
            } else {
                // No spaces found, use the whole string as item name
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

        // Use actualItemName and actualNewQuantity for the rest of the logic
        if (actualItemName != null && itemLabels.containsKey(actualItemName)) {
            int currentQuantity = itemQuantityHashMap.getOrDefault(actualItemName, 1);

            System.out.println("Current quantity for " + actualItemName + ": " + currentQuantity);
            System.out.println("Setting new quantity to: " + actualNewQuantity);
            clientSideVj.sendLogAsync("Current quantity for " + actualItemName + ": " + currentQuantity);
            clientSideVj.sendLogAsync("Setting new quantity to: " + actualNewQuantity);

            if (actualNewQuantity <= 0) {
                // Remove item completely when quantity is 0 or less
                removeItemCompletely(actualItemName);
            } else {
                // Update quantity to the new value
                updateItemQuantity(actualItemName, actualNewQuantity);
            }
        } else {
            System.out.println("Item '" + actualItemName + "' not found in the current transaction");
            clientSideVj.sendLogAsync("Item '" + actualItemName + "' not found in the current transaction");
        }
    }

    // Keep your existing helper methods unchanged
    private void removeItemCompletely(String itemName) {
        System.out.println("Removing item completely: " + itemName);
        // Get current quantity to know how many prices to remove
        int currentQuantity = itemQuantityHashMap.getOrDefault(itemName, 1);
        double itemPriceValue = itemPrice(itemName);
        // Remove all instances of this item's price from prices list
        for (int i = 0; i < currentQuantity; i++) {
            Double priceToRemove = Double.valueOf(itemPriceValue);
            prices.remove(priceToRemove);
        }
        // Remove from all collections
        Component itemComponent = itemLabels.get(itemName);
        itemLabels.remove(itemName);
        itemQuantityHashMap.remove(itemName);
        itemSet.remove(itemName);
        // Remove from UI
        layeredPane.remove(itemComponent);
        // Reposition remaining items
        repositionItemLabels();
        // Update total and display
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
        // Calculate price difference
        int quantityDifference = newQuantity - currentQuantity;
        if (quantityDifference > 0) {
            // Adding more items - add prices to list
            for (int i = 0; i < quantityDifference; i++) {
                prices.add(itemPriceValue);
            }
            System.out.println("Added " + quantityDifference + " more of " + itemName);
            clientSideVj.sendLogAsync("Added " + quantityDifference + " more of " + itemName);
        } else if (quantityDifference < 0) {
            // Removing items - remove prices from list
            int pricesToRemove = Math.abs(quantityDifference);
            for (int i = 0; i < pricesToRemove; i++) {
                Double priceToRemove = Double.valueOf(itemPriceValue);
                prices.remove(priceToRemove);
            }
            System.out.println("Removed " + pricesToRemove + " of " + itemName);
            clientSideVj.sendLogAsync("Removed " + pricesToRemove + " of " + itemName);
        }
        // Update quantity in HashMap
        itemQuantityHashMap.put(itemName, newQuantity);
        // Update the display text for this item
        double totalItemPrice = itemPriceValue * newQuantity;
        String displayText = String.format("%-30s$%.2f                (x%d)", itemName, totalItemPrice, newQuantity);
        JLabel existingLabel = itemLabels.get(itemName);
        if (existingLabel != null) {
            existingLabel.setText(displayText);
        }
        // Update total and display
        total = calculateTotal();
        customLabel.getDouble("totalBeforeTax", total);
        updateTotalDisplay();
        resetUi();
        System.out.println("Updated " + itemName + " quantity to " + newQuantity + ". New total: $" + total);
        clientSideVj.sendLogAsync("Updated " + itemName + " quantity to " + newQuantity + ". New total: $" + total);
    }

    // Enhanced discountApi method to handle different discount types
    public ApiResponse discountApi(String discountType, String discount) {
        ApiConfig apiConfig = new ApiConfig();
        clientSideVj.sendLogAsync("Discount API is being called for type: " + discountType);

        // Build JSON request body - discount value comes from user input
        String jsonRequest = String.format(
                "{\"transactionId\":\"%s\", \"discount\":\"%s\", \"totalAmountBeforeTax\":%.2f}",
                transactionId, discount, total
        );

        clientSideVj.sendLogAsync("Discount Type (URL param): " + discountType);
        clientSideVj.sendLogAsync("API Parameters: " + jsonRequest);

        // For COUPON type, pass the coupon code as URL parameter
        String couponCodeParam = "COUPON".equals(discountType) ? discount : null;

        // Call API with query parameters
        ApiResponse discountResponse = apiConfig.callDiscountApi(jsonRequest, discountType, couponCodeParam);

        try {
            // Process the response
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

    public void resetUi(){
        layeredPane.remove(nextTotalLabel);
        layeredPane.remove(totalWithTaxLabel);
        layeredPane.remove(computedTaxLabel);
        layeredPane.remove(discountLabel);
        layeredPane.remove(customerChangeLabel);
        layeredPane.setBounds(600,1,400
                ,560);
        layeredPane.revalidate();
        layeredPane.repaint();
    }

}
