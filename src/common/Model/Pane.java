package common.Model;

import common.Controller.BarcodeScanner;
import common.Controller.DollarConversion;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

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


    public Pane(){
        layeredPane = new JLayeredPane();
        layeredPane.setBounds(600,1,400,600);
        layeredPane.setOpaque(true);
        layeredPane.setBorder(BorderFactory.createTitledBorder(
                "Items Scanned-----------------------------Quantity"));

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

        prices.add(finalPrice);

        JLabel label = new JLabel();

//        System.out.println("Array of Prices: " + prices);

        label.setFont(new Font("Monospaced", Font.PLAIN, 15));

        if (itemSet == null) {
            itemSet = new HashSet<>();
        }
        if (
                !"Next Dollar".equalsIgnoreCase(itemName) &&
                !"Exact Dollar".equalsIgnoreCase(itemName) && !"Void Transaction".equalsIgnoreCase(itemName)) {

            itemSet.add(itemName);
            String displayText = String.format("%s: $%.2f", itemName, finalPrice);
            label.setText(displayText);
            System.out.println(displayText);
        }

        if(itemName.equalsIgnoreCase("Void Transaction")){
            voidTransact(true);
        }


        System.out.println("Item List: " + itemSet);

        total = calculateTotal();

        totalLabel.setText(
                String.format("Total Before Tax: \t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t$%.2f", customLabel.getDouble("totalBeforeTax", total))
        );
        computedTaxLabel.setText(
                String.format("Computed Tax: \t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t$%.2f", customLabel.getDouble("computedTax", total))
        );

        totalWithTaxLabel.setText(
                String.format("Total After Tax: \t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t$%.2f", customLabel.getDouble("totalWithTax", total))
        );

        if (itemName.equalsIgnoreCase("Next Dollar")) {
            layeredPane.add(nextTotalLabel, JLayeredPane.DEFAULT_LAYER);
            layeredPane.add(customerChangeLabel, JLayeredPane.DEFAULT_LAYER);
            nextTotalLabel.setText(String.format("Next Dollar Value:  \t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t$%.2f", nextDollarValue));
            customerChangeLabel.setText(String.format("Customer Change:  \t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t$%.2f", nextDollarValue - totalWithTax));
            layeredPane.setBounds(600,1,400,640);
        }

        totalWithTax = customLabel.getDouble("totalWithTax", total);
        label.setBounds(10, labelY, 380, 20);
        labelY += 25;

        label.setOpaque(true);
        label.setVisible(true);
        layeredPane.add(label, JLayeredPane.DEFAULT_LAYER);

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

//    public int quantityOfItem(String itemName) {
//
//        itemList = new ArrayList<>();
//        System.out.println("Item List in the Quantity Function: " + itemList);
//        itemList.add(itemName);
//        int quantity = 0;
//        for (String item : itemList) {
//            if (item.equalsIgnoreCase(itemName)) {
//                quantity++;
//            }
//        }
//
//        System.out.println("Quantity of " + itemName + ": " + quantity);
//        return quantity;
//    }

    private void voidTransact(boolean showModal) {

        popUpModal = new Modal();

        if (showModal) {
            popUpModal.displayModal("Void Transaction Confirmation");
        }
        layeredPane.removeAll();
        prices.clear();
        itemSet.clear();
        total = 0.00;
        totalWithTax = 0.00;
        nextDollarValue = 0.00;
        labelY = 25;
        totalLabel.setText("Total Before Tax: $0.00");
        computedTaxLabel.setText("Computed Tax: $0.00");
        totalWithTaxLabel.setText("Total After Tax: $0.00");
        layeredPane.setBounds(600,1,400,600);
        layeredPane.add(totalLabel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(computedTaxLabel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(totalWithTaxLabel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.revalidate();
        layeredPane.repaint();
    }

    public HashMap<String, Integer> quantityPerItem(String itemName){
        int quantity = 1;
        HashMap<String, Integer> itemQuantity = new HashMap<>();
            itemQuantity.put(itemName, 1);
            if (itemQuantity.containsKey(itemName))
                itemQuantity.put(itemName, quantity + 1);
        return itemQuantity;
    }

}
