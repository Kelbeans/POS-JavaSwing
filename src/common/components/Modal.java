package common.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class Modal {

    private JDialog dialog;
    private String userChoice;
    private JTextField itemName;
    private JTextField itemQuantity;

    /*
     * Simple Modal with Customizable Title, Message and Buttons
     */
    public String displayModal(String modalTitle, String message, String button1Text, String button2Text) {
        userChoice = null;

        dialog = new JDialog();
        dialog.setTitle(modalTitle);
        dialog.setBounds(350, 350, 350, 150);
        dialog.setModal(true); // Make it modal
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(null);

        // Message label
        JLabel messageLabel = new JLabel(message, JLabel.CENTER);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        dialog.add(messageLabel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());

        // First button (Yes or custom)
        JButton button1 = new JButton(button1Text);
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userChoice = button1Text;
                dialog.dispose();
            }
        });

        // Second button (No or custom)
        JButton button2 = new JButton(button2Text);
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userChoice = button2Text;
                dialog.dispose();
            }
        });

        buttonPanel.add(button1);
        buttonPanel.add(button2);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);

        return userChoice;
    }

    /*
     * Modal With Input Field
     */
    public String displayInputModal(String modalTitle, String message, String placeholderText, String button1Text, String button2Text) {
        userChoice = null;
        final String[] inputValue = {null};

        dialog = new JDialog();
        dialog.setTitle(modalTitle);
        dialog.setBounds(350, 350, 400, 200);
        dialog.setModal(true); // Make it modal
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(null);

        // Create main panel for message and input
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        // Message label
        JLabel messageLabel = new JLabel(message, JLabel.CENTER);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        mainPanel.add(messageLabel, BorderLayout.NORTH);

        // Input field
        JTextField inputField = new JTextField(placeholderText);
        inputField.setPreferredSize(new Dimension(300, 30));
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // Set initial placeholder appearance
        inputField.setForeground(Color.GRAY);

        // Add focus listener to clear placeholder text
        inputField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (inputField.getText().equals(placeholderText)) {
                    inputField.setText("");
                    inputField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (inputField.getText().isEmpty()) {
                    inputField.setText(placeholderText);
                    inputField.setForeground(Color.GRAY);
                }
            }
        });

        if (modalTitle.equalsIgnoreCase("Discount")) {
            // Create discount-specific UI
            String[] discountType = {"PWD", "SENIOR", "COUPON"};
            JComboBox<String> discountTypeComboBox = new JComboBox<>(discountType);
            discountTypeComboBox.setSelectedIndex(2); // Default to COUPON
            discountTypeComboBox.setPreferredSize(new Dimension(150, 30));

            // Create a panel to hold both combo box and input field
            JPanel discountPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

            // Add combo box label
            JLabel comboLabel = new JLabel("Discount Type:");
            discountPanel.add(comboLabel);
            discountPanel.add(discountTypeComboBox);

            // Initially hide input field for non-COUPON selections
            inputField.setPreferredSize(new Dimension(200, 30));
            inputField.setVisible(true); // Show by default since COUPON is selected

            // Add input field label and field
            JLabel inputLabel = new JLabel("Coupon Code:");
            inputLabel.setVisible(true); // Show by default since COUPON is selected
            discountPanel.add(inputLabel);
            discountPanel.add(inputField);

            // Add action listener to combo box
            discountTypeComboBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String selectedType = (String) discountTypeComboBox.getSelectedItem();
                    boolean showInputField = "COUPON".equals(selectedType);

                    inputField.setVisible(showInputField);
                    inputLabel.setVisible(showInputField);

                    if (showInputField) {
                        inputField.setForeground(Color.GRAY);
                    } else {
                        inputValue[0] = selectedType; // Store the discount type
                    }

                    // Refresh the panel
                    discountPanel.revalidate();
                    discountPanel.repaint();
                }
            });

            mainPanel.add(discountPanel, BorderLayout.CENTER);

        } else {
            // Regular input modal - just add the input field
            mainPanel.add(inputField, BorderLayout.CENTER);
        }

        dialog.add(mainPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());

        // First button (OK or custom)
        JButton button1 = new JButton(button1Text);
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (modalTitle.equalsIgnoreCase("Discount")) {
                    // Handle discount modal
                    JComboBox<String> comboBox = null;

                    // Find the combo box in the dialog
                    for (Component comp : dialog.getContentPane().getComponents()) {
                        if (comp instanceof JPanel) {
                            for (Component subComp : ((JPanel) comp).getComponents()) {
                                if (subComp instanceof JPanel) {
                                    for (Component innerComp : ((JPanel) subComp).getComponents()) {
                                        if (innerComp instanceof JComboBox) {
                                            comboBox = (JComboBox<String>) innerComp;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (comboBox != null) {
                        String selectedType = (String) comboBox.getSelectedItem();

                        if ("COUPON".equals(selectedType)) {
                            // For COUPON, get the input field value
                            String couponCode = inputField.getText();
                            if (!couponCode.equals("Enter coupon code") && !couponCode.trim().isEmpty()) {
                                inputValue[0] = "COUPON:" + couponCode;
                            } else {
                                inputValue[0] = "COUPON:";
                            }
                        } else {
                            // For PWD/SENIOR, just return the type
                            inputValue[0] = selectedType;
                        }
                    }
                    userChoice = button1Text;
                } else {
                    // Handle regular modal
                    String text = inputField.getText();
                    if (!text.equals(placeholderText) && !text.trim().isEmpty()) {
                        inputValue[0] = text;
                        userChoice = button1Text;
                    } else {
                        userChoice = button1Text;
                    }
                }
                dialog.dispose();
            }
        });

        // Second button (Cancel or custom)
        JButton button2 = new JButton(button2Text);
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userChoice = button2Text;
                dialog.dispose();
            }
        });

        // Add Enter key functionality to submit
        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button1.doClick(); // Simulate clicking the first button
            }
        });

        buttonPanel.add(button1);
        buttonPanel.add(button2);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);

        // Return the input value if OK was clicked and input was provided, otherwise return the button choice
        return inputValue[0] != null ? inputValue[0] : userChoice;
    }


    /*
        * Item Quantity Modal
        * This modal allows the user to enter the item name and quantity
    */
    public String displayQuantityModal(String modalTitle, String message, String button1Text, String button2Text){
        dialog = new JDialog();
        dialog.setTitle(modalTitle);
        dialog.setBounds(350, 350, 350, 150);
        dialog.setModal(true); // Make it modal
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(null);

        // Message label
        JLabel messageLabel = new JLabel(message, JLabel.CENTER);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        dialog.add(messageLabel, BorderLayout.CENTER);

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
                dialog.dispose();
            }
        });



        // Second button (No or custom)
        JButton button2 = new JButton(button2Text);
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userChoice = button2Text;
                dialog.dispose();
            }
        });

        buttonPanel.add(button1);
        buttonPanel.add(button2);
        dialog.add(messageLabel, BorderLayout.NORTH);
        dialog.add(textFieldPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);

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


