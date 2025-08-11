package common.Model;

import java.util.HashMap;


public class MockItems {
    private HashMap<String, String> description;
    private HashMap<String, Double> price;

    public MockItems(){
      description = new HashMap<>();
      description.put("052000135176", "GATORADE COOL BLUE 28OZ");
      description.put("860006114916", "REDDY ICE 7LB BAG");
      description.put("041594899038", "TB POLAR POP 30OZ FOAM");

      price = new HashMap<>();
      price.put("052000135176", 3.42);
      price.put("860006114916", 14.95);
      price.put("041594899038", 0.99);
    }
    public void printMockItems() {
        System.out.println("Mock Items List:");
        System.out.println("----------------------------------------");
        for (String barcode : description.keySet()) {
            String item = description.get(barcode);
            Double itemPrice = price.get(barcode);
            System.out.printf("Barcode: %s | Item: %-20s | Price: $%.2f%n",
                    barcode, item, itemPrice);
        }
        System.out.println("----------------------------------------");
    }

    public boolean validateBarcode(String barcode){
        if (description.containsKey(barcode)){
            System.out.println("Barcode is valid: " + barcode);
            return true;
        }
        return false;
    }

    public Double scannedBarcodePrice(String barcode){
        Double value = 0.00;
        String productName = "";
        if (price.containsKey(barcode)){
            value = price.get(barcode);
            productName = description.get(barcode);
            System.out.println(productName + ": $" + value);
        }
        return value;
    }
    public String scannedBarcodeDescription(String barcode){
        String productName = "";
        if (description.containsKey(barcode)){
            productName = description.get(barcode);
        }
        return productName;
    }

}
