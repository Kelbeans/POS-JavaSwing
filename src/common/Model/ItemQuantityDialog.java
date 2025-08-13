package common.Model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ItemQuantityDialog {

    private JDialog quantityDialog;
    private JTextField itemName;
    private JTextField itemQuantity;
    private String userChoice;

    public String displayQuantityModal(String modalTitle, String message, String button1Text, String button2Text){
        quantityDialog = new JDialog();
        quantityDialog.setTitle(modalTitle);
        quantityDialog.setBounds(350, 350, 350, 150);
        quantityDialog.setModal(true); // Make it modal
        quantityDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        quantityDialog.setLayout(new BorderLayout());

        // Message label
        JLabel messageLabel = new JLabel("Enter an Item Quantity to Edit", JLabel.CENTER);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        quantityDialog.add(messageLabel, BorderLayout.CENTER);

        JPanel textFieldPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        itemName = new JTextField(10);
        itemName.setPreferredSize(new Dimension(100, 30));
        itemName.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        itemQuantity = new JTextField(2);
        itemQuantity.setPreferredSize(new Dimension(100, 30));
        itemQuantity.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        textFieldPanel.add(itemName);
        textFieldPanel.add(itemQuantity);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());

        // First button (Yes or custom)
        JButton button1 = new JButton(button1Text);
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userChoice = button1Text;
                // Retrieve and log the values
                String name = itemName.getText();
                String quantity = itemQuantity.getText();
                System.out.println("Item Name: " + name);
                System.out.println("Item Quantity: " + quantity);
                quantityDialog.dispose();
            }
        });



        // Second button (No or custom)
        JButton button2 = new JButton(button2Text);
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userChoice = button2Text;
                quantityDialog.dispose();
            }
        });

        buttonPanel.add(button1);
        buttonPanel.add(button2);
        quantityDialog.add(messageLabel, BorderLayout.NORTH);
        quantityDialog.add(textFieldPanel, BorderLayout.CENTER);
        quantityDialog.add(buttonPanel, BorderLayout.SOUTH);
        quantityDialog.setVisible(true);

        return userChoice;
    }

    public String returnItemValues(){
        return itemName.getText() + " " + itemQuantity.getText();
    }

    // Example function to use the values
    public void processItemValues(String itemValues) {
        String[] values = itemValues.split(" ", 2); // Split on first space
        String name = values[0];
        String quantity = values.length > 1 ? values[1] : "";
        System.out.println("Processed Item Name: " + name);
        System.out.println("Processed Item Quantity: " + quantity);
    }
}
