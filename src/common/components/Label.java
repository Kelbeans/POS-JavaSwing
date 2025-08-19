package common.components;

import common.helperMethods.DollarConversion;
import config.SocketConfig;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Label {

    private double TAX_RATE = 0.07;
    private double customValue;
    private DollarConversion dollarConversion;
    private SocketConfig clientSideVj;

    private int width = 580;

    public JLabel getLabel(String labelName, double value) {
        JLabel label = new JLabel();
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        // Make background transparent
        label.setBorder(new EmptyBorder(10, 10, 10, 0));
        label.setOpaque(false); // This is the key line
        switch (labelName) {
            case "totalBeforeTax":
                label.setText(String.format("%-93s$%.2f", "Total Before Tax: ",  customValue));
                label.setBounds(10,350, width,20);
                break;
                case "computedTax":
                    label.setBackground(new Color(0x9ECAD6));
                    label.setOpaque(true);
                    label.setText(String.format("%-45s$%.2f", "Computed Tax: ", customValue));
                    label.setBounds(10,370, width,20);
                    break;
            case "discount":
                label.setBackground(new Color(0x9ECAD6));
                label.setOpaque(true);
                label.setText(String.format("%-45s$%.2f", "Discount: ", customValue));
                label.setBounds(10,390, width,20);
                break;
                    case "totalWithTax":
                        label.setText(String.format("%-51s$%.2f", "Total Before Tax: ", customValue));
                        label.setBounds(10,410, width,20);
                        break;
                        case "nextTotal":
                            label.setBackground(new Color(0x727D73));
                            label.setOpaque(true);
                            label.setBounds(10,430, width,20);
                            break;
                        case "customerChange":
                            label.setBackground(new Color(0xDA6C6C));

                            label.setBounds(10,450, width,20);
                            label.setOpaque(true);
                            break;
            default:
                getDouble("Default", value);
                label.setText("Default: \t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + String.format(" $%.2f", customValue));
                label.setBounds(10, 590, width,20);

        }
        label.setVisible(true);
        return label;
    }


    public double getDouble(String labelName, double value){

        clientSideVj = new SocketConfig("localhost", 8080);

        switch (labelName){
            case "totalBeforeTax":
                customValue = value;
                System.out.println("Total Before Tax: " + String.format("%.2f", customValue));
                return customValue;
            case "computedTax":
                customValue = value * TAX_RATE;
                System.out.println("Computed Tax 7%%: " + String.format("%.2f", customValue));
                return customValue;
                case "discount":
                customValue = value;
                System.out.println("Discount 20%%: " + String.format("%.2f", customValue));
                return customValue;
            case "totalWithTax":
                customValue = value + (value * TAX_RATE);
                System.out.println("Discounted Total With Tax: " + String.format("%.2f", customValue));
                return customValue;
                case "nextTotal":
                    dollarConversion = new DollarConversion();
                    double nextDollarValue = dollarConversion.getNextDollarValue(value);
                    customValue = nextDollarValue;
                    System.out.println("Next Dollar Value: " + String.format("%.2f", customValue));
                    return customValue;
            case "customerChange":
                try {
                    dollarConversion = new DollarConversion();
                    nextDollarValue = dollarConversion.getNextDollarValue(value);
                    System.out.println("Next Dollar Value: " + String.format("%.2f", nextDollarValue));
                    customValue = nextDollarValue - value;
                    System.out.println("Customer Change: " + String.format("%.2f", customValue));
                    clientSideVj.sendLogAsync("Please Pay: " + String.format("$%.2f", value));
                    clientSideVj.sendLogAsync("Customer Change: " + String.format("$%.2f", customValue));
                    return customValue;
                } catch (Exception e) {
                    System.err.println("Error in customerChange case: " + e.getMessage());
                    return 0.00;
                }
            default:
                switch (labelName){
                    case "customerChangeNextDollar":
                        dollarConversion = new DollarConversion();
                        nextDollarValue = dollarConversion.getNextDollarValue(value);
                        System.out.println("Next Dollar Value: " + String.format("%.2f", nextDollarValue));
                        customValue = nextDollarValue - value;
                        System.out.println("Customer Change: " + String.format("%.2f", customValue));
                        clientSideVj.sendLogAsync("Please Pay: " + String.format("$%.2f", value));
                        clientSideVj.sendLogAsync("Customer Change: " + String.format("$%.2f", customValue));
                        return customValue;
                    case "customerChangeExactDollar":
                        System.out.println("Exact Dollar Value: " + String.format("%.2f", value));
                        customValue = value - value;
                        System.out.println("Customer Change: " + String.format("%.2f", customValue));
                        clientSideVj.sendLogAsync("Please Pay: " + String.format("$%.2f", value));
                        clientSideVj.sendLogAsync("Customer Change: " + String.format("$%.2f", customValue));
                        return customValue;
                }
                break;
        }
        return 0.00;
    }

}
