package common.Model;

import common.Controller.BarcodeScanner;
import common.Utils.TransactionIdGenerator;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class Pane {
    private final JLayeredPane layeredPane;
    private double price = 0;
    private int labelY = 25;
    private ArrayList<Double> prices;
    private final JLabel totalLabel;
    private final JLabel totalWithTaxLabel;
    private final JLabel computedTaxLabel;
    private final JLabel customerChangeLabel;
    private final JLabel nextTotalLabel;
    private double total;
    private double totalWithTax;
    private double nextDollarValue;
    private Set<String> itemSet;
    private Label customLabel;
    private Modal popUpModal;
    private ItemQuantityDialog itemQuantityDialog;
    private HashMap<String, Integer> itemQuantityHashMap = new HashMap<>();
    private HashMap<String, JLabel> itemLabels = new HashMap<>();
    private TransactionIdGenerator transactionIdGenerator;


    public Pane(){
        layeredPane = new JLayeredPane();
        layeredPane.setBounds(600,1,400,600);
        layeredPane.setBackground(new Color(0xECFAE5));
        layeredPane.setOpaque(true);
        layeredPane.setBorder(BorderFactory.createTitledBorder(
                "Items Scanned--------------Price-----------Quantity"));

        prices = new ArrayList<>();

        customLabel = new Label();

        totalLabel = customLabel.getLabel("totalBeforeTax", total);
        computedTaxLabel = customLabel.getLabel("computedTax", total);
        totalWithTaxLabel = customLabel.getLabel("totalWithTax", total);
        nextTotalLabel = customLabel.getLabel("nextTotal", total);
        customerChangeLabel = customLabel.getLabel("customerChange", total);

        layeredPane.add(totalLabel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(computedTaxLabel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(totalWithTaxLabel, JLayeredPane.DEFAULT_LAYER);
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

        prices.add(finalPrice);

        JLabel itemLabelInPanel = new JLabel();
        itemLabelInPanel.setFont(new Font("Monospaced", Font.PLAIN, 15));

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
            double totalItemPrice = finalPrice * quantity;
            String displayText = String.format("%-20s $%.2f           (x%d)", itemName, totalItemPrice, quantity);

            if (!itemSet.contains(itemName)) {
                // New item - create new label AND increment labelY
                itemSet.add(itemName);
                itemLabelInPanel.setText(displayText);
                itemLabels.put(itemName, itemLabelInPanel);
                itemLabelInPanel.setBounds(10, labelY, 380, 20);
                labelY += 25; // Only increment for new labels
            } else {
                // Existing item - just update the existing label, don't increment labelY
                JLabel existingLabel = itemLabels.get(itemName);
                if (existingLabel != null) {
                    existingLabel.setText(displayText);
                }
                System.out.println("Updated: " + displayText);
            }
        }


        total = calculateTotal();


        //Calling Action Button Function
        actionButtons(itemName);

        totalLabel.setText(
                String.format("Total Before Tax: \t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t$%.2f", customLabel.getDouble("totalBeforeTax", total))
        );
        computedTaxLabel.setText(
                String.format("Computed Tax:\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t$%.2f", customLabel.getDouble("computedTax", total))
        );

        totalWithTaxLabel.setText(
                String.format("Total After Tax: \t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t$%.2f", customLabel.getDouble("totalWithTax", total))
        );

        totalWithTax = customLabel.getDouble("totalWithTax", total);


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
                price = 0.00;
                break;
            default:
                break;
        }
        return price;
    }

    public void nextDollarValue(double value){
        nextDollarValue = value;
        System.out.println("Next Dollar Value: " + nextDollarValue);
    }



    public double getTotalAfterTax() {
        System.out.println("[TRACE] getTotalAfterTax " + totalWithTax);
        return totalWithTax;
    }


    private double calculateTotal() {
        return prices.stream().mapToDouble(Double::doubleValue).sum();
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
                nextDollarButtonIsClicked(true, itemName, totalWithTax);
                transactionIdGenerator = new TransactionIdGenerator();
                transactionIdGenerator.generateTransactionId();
            }
        } else {
            popUpModal = new Modal();
            popUpModal.displayModal("Empty Cart", "No items in the cart. Please add items to proceed.", "OK", "Cancel");
            System.out.println("Item Quantity HashMap is Empty");
            return;
        }

        if(itemName.equalsIgnoreCase("Void Transaction")){
            voidTransact(true);
        }

        if (itemName.equalsIgnoreCase("Void Item")) {
            voidItemTransaction(true);
        }

        if (itemName.equalsIgnoreCase("Quantity Change")) {
            changeItemQuantity(true, "Juice", 1);
        }

    }

    /*
        * Next Dollar Case Fix to Illegal Component Issue. Need to refresh the label to display the new value.
        * This handles the Next Dollar Button and the Exact Dollar Button.
    */
    public void nextDollarButtonIsClicked(boolean showModal, String buttonName, Double value){
        popUpModal = new Modal();



        if (showModal) {
            String userChoice = popUpModal.displayModal("Payment Transaction Confirmation",
                    "Select a Payment Method",
                    "Cash Payment",
                    "Credit/Debit");

            // Only proceed with void if user clicked "Yes"
            if ("Cash Payment".equals(userChoice)) {
                System.out.println("Cash Payment is Selected");
            }
            else {
                System.out.println("Credit/Debit Payment is Selected");
            }
            System.out.println("Please pay a total of: " + String.format("$%.2f", value));
        }

        layeredPane.setBounds(600,1,400,643);
        totalWithTaxLabel.setOpaque(true);
        totalWithTaxLabel.setBackground(new Color(0x727D73));
        // Remove components if they already exist
        layeredPane.remove(nextTotalLabel);
        layeredPane.remove(customerChangeLabel);

        // Now add them
        layeredPane.add(nextTotalLabel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(customerChangeLabel, JLayeredPane.DEFAULT_LAYER);

        if(buttonName.equalsIgnoreCase("Exact Dollar")){
            nextTotalLabel.setText(String.format("Exact Dollar Value:  \t\t\t\t\t\t\t\t\t\t\t\t\t\t$%.2f", totalWithTax));
            customerChangeLabel.setText(String.format("Customer Change:  \t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t$%.2f", totalWithTax - totalWithTax));
        } else {
            nextTotalLabel.setText(String.format("Next Dollar Value:  \t\t\t\t\t\t\t\t\t\t\t\t\t\t\t$%.2f", nextDollarValue));
            customerChangeLabel.setText(String.format("Customer Change:  \t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t$%.2f", nextDollarValue - totalWithTax));
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

            // Only proceed with void if user clicked "Yes"
            if (!"Yes".equals(userChoice)) {
                System.out.println("Void Transaction Cancelled");
                return; // Exit method without voiding if user clicked "No" or closed dialog
            }
            System.out.println("Void Transaction Confirmed");
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
        totalWithTax = 0.00;
        nextDollarValue = 0.00;
        labelY = 25;
        totalLabel.setText("Total Before Tax: $0.00");
        totalWithTaxLabel.setOpaque(false);
        computedTaxLabel.setText("Computed Tax: $0.00");
        totalWithTaxLabel.setText("Total After Tax: $0.00");
        layeredPane.setBounds(600,1,400,600);
        layeredPane.add(totalLabel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(computedTaxLabel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(totalWithTaxLabel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.revalidate();
        layeredPane.repaint();
    }

    // Fix the hashMap issue with quantity. By in case of using a class level currentQuantity variable we change it to Global Scope.
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

            // Check if userChoice is "Cancel" or null (dialog closed)
            if ("Cancel".equals(userChoice) || userChoice == null) {
                System.out.println("Voiding Item: is cancelled");
                return; // Exit method without voiding
            }

            // If userChoice is not "Proceed", it must be the item name
            String itemName = "Proceed".equals(userChoice) ? null : userChoice;

            // If no valid item name was provided, print message and return
            if (itemName == null || itemName.trim().isEmpty()) {
                System.out.println("No valid item name provided for voiding");
                return;
            }

            System.out.println("Item to void: " + itemName);
            voidItemConfirmation(showModal, itemName);
        }
    }


    public void voidItemConfirmation(boolean showModal, String itemName) {
        popUpModal = new Modal();
        if (showModal) {
            String userChoice = popUpModal.displayModal("Void Item Confirmation",
                    "Are you sure you want to remove: " + (itemName != null ? itemName : "Unknown Item"),
                    "Yes", "No");

            // Only proceed with void if user clicked "Yes"
            if (!"Yes".equals(userChoice)) {
                System.out.println("Voiding Item: " + (itemName != null ? itemName : "Unknown Item") + " is cancelled");
                return; // Exit method without voiding
            }
            System.out.println("Item: " + (itemName != null ? itemName : "Unknown Item") + " is voided");
        }

        if (itemName != null && itemLabels.containsKey(itemName)) {
            itemLabels.remove(itemName);
            layeredPane.remove(itemLabels.get(itemName));
            layeredPane.revalidate();
            layeredPane.repaint();
        }
    }

    public void changeItemQuantity(boolean showModal, String itemName, int quantity){
        itemQuantityDialog = new ItemQuantityDialog();



        if (showModal) {
            String userChoice = itemQuantityDialog.displayQuantityModal("Quantity Change Confirmation",
                    "Enter an Item to Change Quantity",
                    "Proceed", "Cancel");
        }

        String fetchInputtedValue =  itemQuantityDialog.returnItemValues();
        itemQuantityDialog.processItemValues(fetchInputtedValue);
        System.out.println(fetchInputtedValue);


        if (itemLabels.containsKey(fetchInputtedValue)) {
            itemQuantityHashMap.put(itemName, quantity);
        }
    }

}
