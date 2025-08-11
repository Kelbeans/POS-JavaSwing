package common.Model;

import common.Controller.DollarConversion;

import javax.swing.*;
import java.awt.*;

public class Label {

    private double TAX_RATE = 0.07;
    private double customValue;
    private DollarConversion dollarConversion;

    public JLabel getLabel(String labelName, double value) {
        JLabel label = new JLabel();

        label.setFont(new Font("Monospaced", Font.PLAIN, 15));

        switch (labelName) {
            case "totalBeforeTax":
                label.setText("Total Before Tax: \t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + String.format(" $%.2f", customValue));
                label.setBounds(10,530,380,20);
                break;
                case "computedTax":
                    label.setText("Computed Tax: \t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + String.format(" $%.2f", customValue));
                    label.setBounds(10,550,380,20);
                    break;
                    case "totalWithTax":
                        label.setText("Total After Tax: \t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + String.format(" $%.2f", customValue));
                        label.setBounds(10,570,380,20);
                        break;
                        case "nextTotal":
                            label.setBounds(10,590,380,20);
                            break;
                        case "customerChange":
                            label.setBounds(10,610,380,20);
                            break;
            default:
                getDouble("Default", value);
                label.setText("Default: \t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + String.format(" $%.2f", customValue));
                label.setBounds(10, 590,380,20);

        }
        label.setOpaque(true);
        label.setVisible(true);
        return label;
    }


    public double getDouble(String labelName, double value){
        System.out.println("[TRACE]getDouble function " + value);
        switch (labelName){
            case "totalBeforeTax":
            case "nextTotal":
                customValue = value;
                System.out.println("Total Before Tax: " + String.format("%.2f", customValue));
                return customValue;
            case "computedTax":
                customValue = value * TAX_RATE;
                System.out.println("Computed Tax: " + String.format("%.2f", customValue));
                return customValue;
            case "totalWithTax":
                customValue = value + (value * TAX_RATE);
                System.out.println("Total After Tax: " + String.format("%.2f", customValue));
                return customValue;
            case "customerChange":
                try {
                    dollarConversion = new DollarConversion();
                    double nextDollarValue = dollarConversion.getNextDollarValue(value);
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
