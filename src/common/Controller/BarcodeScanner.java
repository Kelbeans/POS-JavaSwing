package common.Controller;

import common.Model.MockItems;
import common.Model.Pane;

import java.io.InputStream;

public class BarcodeScanner {
    private java.util.Scanner scanner;
    private String barcode;
    private MockItems mockItems;
    private Pane pane;

    public BarcodeScanner(InputStream in, Pane pane) {
        this.scanner = new java.util.Scanner(in);
        this.pane = pane;
        this.mockItems = new MockItems();
    }

    public void scanBarcode() {
        while (scanner.hasNextLine()) {
            barcode = scanner.nextLine();
            System.out.println("Scanned Barcode: " + barcode);
            mockItems.validateBarcode(barcode);
            Double amount = mockItems.scannedBarcodePrice(barcode);
            String description = mockItems.scannedBarcodeDescription(barcode);
            pane.addTextToScreen(description, amount);
        }
        scanner.close();
    }
}