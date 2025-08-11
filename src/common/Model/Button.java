package common.Model;

import common.Controller.DollarConversion;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Button {

    private final Pane pane;

    private DollarConversion dollarConversion;

    private Modal popUpModal;

    public Button(Pane pane) {
        this.pane = pane;

    }

    public AbstractButton testButtonPanel(String buttonName, ArrayList<Integer> buttonStyle){
        JButton button = new JButton(buttonName);
        button.setBounds(buttonStyle.get(0),buttonStyle.get(1),200,200);
//      LAMBDA STYLE ACTION --- button.addActionListener(e -> System.out.println(buttonName + " is Clicked"));

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(buttonName+" is clicked");
                if(
                        !buttonName.equalsIgnoreCase("Next Dollar")
                        && !buttonName.equalsIgnoreCase("Exact Dollar")
                        && !buttonName.equalsIgnoreCase("Void Transaction"))
                {
                    pane.addTextToScreen(buttonName, null);
                }
                else if (buttonName.equalsIgnoreCase("Next Dollar")){
                    if (dollarConversion == null) {
                        dollarConversion = new DollarConversion();
                    }
                    double value = pane.getTotalAfterTax();
                    double convertedValue = dollarConversion.getNextDollarValue(value);

                    pane.nextDollarValue(convertedValue);
                    pane.addTextToScreen(buttonName, null);

                }
                else if (buttonName.equalsIgnoreCase("Void Transaction")){
                    pane.addTextToScreen(buttonName, null);
                }

            };
        });
        return button;

    }

}
