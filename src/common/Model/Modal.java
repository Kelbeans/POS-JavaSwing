package common.Model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class Modal {

    private JDialog dialog;
    private String userChoice;

    // Method with customizable buttons
    public String displayModal(String modalTitle, String message, String button1Text, String button2Text) {
        userChoice = null;

        dialog = new JDialog();
        dialog.setTitle(modalTitle);
        dialog.setBounds(350, 350, 350, 150);
        dialog.setModal(true); // Make it modal
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLayout(new BorderLayout());

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

    // Method with input field and customizable buttons
    public String displayInputModal(String modalTitle, String message, String placeholderText, String button1Text, String button2Text) {
        userChoice = null;
        final String[] inputValue = {null};

        dialog = new JDialog();
        dialog.setTitle(modalTitle);
        dialog.setBounds(350, 350, 400, 200);
        dialog.setModal(true); // Make it modal
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLayout(new BorderLayout());

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

        // Set initial placeholder appearance
        inputField.setForeground(Color.GRAY);

        mainPanel.add(inputField, BorderLayout.CENTER);
        dialog.add(mainPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());

        // First button (OK or custom)
        JButton button1 = new JButton(button1Text);
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = inputField.getText();
                // Don't return placeholder text as input
                if (!text.equals(placeholderText) && !text.trim().isEmpty()) {
                    inputValue[0] = text;
                    userChoice = button1Text;
                } else {
                    userChoice = button1Text;
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

}