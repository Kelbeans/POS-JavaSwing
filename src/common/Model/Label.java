package common.Model;

import common.Controller.DollarConversion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Label {

    private double TAX_RATE = 0.07;
    private double customValue;
    private DollarConversion dollarConversion;

    public JLabel getLabel(String labelName, double value) {
        JLabel label = new JLabel();

        label.setFont(new Font("Monospaced", Font.PLAIN, 15));
        // Make background transparent
        label.setBorder(new EmptyBorder(10, 0, 10, 0));
        label.setOpaque(false); // This is the key line

        switch (labelName) {
            case "totalBeforeTax":
                label.setText("Total Before Tax: \t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + String.format(" $%.2f", customValue));
                label.setBounds(10,530,380,20);
                break;
                case "computedTax":
                    label.setBackground(new Color(0x9ECAD6));
                    label.setOpaque(true);
                    label.setText("Computed Tax: \t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + String.format(" $%.2f", customValue));
                    label.setBounds(10,550,380,20);
                    break;
            case "discount":
                label.setBackground(new Color(0x9ECAD6));
                label.setOpaque(true);
                label.setText("Discount: \t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + String.format(" $%.2f", customValue));
                label.setBounds(10,590,380,20);
                break;
                    case "totalWithTax":
                        label.setText("Total: \t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + String.format(" $%.2f", customValue));
                        label.setBounds(10,570,380,20);
                        break;
                        case "nextTotal":
                            label.setBackground(new Color(0x727D73));
                            label.setOpaque(true);
                            label.setBounds(10,610,380,20);
                            break;
                        case "customerChange":
                            label.setBackground(new Color(0xDA6C6C));

                            label.setBounds(10,630,380,20);
                            label.setOpaque(true);
                            break;
            default:
                getDouble("Default", value);
                label.setText("Default: \t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + String.format(" $%.2f", customValue));
                label.setBounds(10, 590,380,20);

        }
        label.setVisible(true);
        return label;
    }


    public double getDouble(String labelName, double value){
        switch (labelName){
            case "totalBeforeTax":
                customValue = value;
                System.out.println("Total Before Tax: " + String.format("%.2f", customValue));
                return customValue;
            case "computedTax":
                customValue = value * TAX_RATE;
                System.out.println("Computed Tax: " + String.format("%.2f", customValue));
                return customValue;
                case "discount":
                customValue = value;
                System.out.println("Discount 20 Percent: " + String.format("%.2f", customValue));
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
                    return customValue;
                } catch (Exception e) {
                    System.err.println("Error in customerChange case: " + e.getMessage());
                    return 0.00;
                }
        }
        return 0.00;
    }

}
