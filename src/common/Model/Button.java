package common.Model;

import common.Controller.DollarConversion;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Button {

    private final Pane pane;

    private DollarConversion dollarConversion;

    public Button(Pane pane) {
        this.pane = pane;

    }

    public AbstractButton testButtonPanel(String buttonName, ArrayList<Integer> buttonStyle){
        JButton button = new JButton(buttonName);
        button.setBounds(buttonStyle.get(0),buttonStyle.get(1),200,200);
        button.setBackground(new Color(0xFAF9EE));
        button.setForeground(Color.BLACK);
        button.setOpaque(true);
        button.setBorder(new LineBorder(Color.DARK_GRAY, 1));

        if (buttonName.equalsIgnoreCase("Void Transaction")){
            button.setBackground(new Color(0xAF3E3E));
            button.setForeground(Color.WHITE);
            button.setOpaque(true);

        }
        if(buttonName.equalsIgnoreCase("Next Dollar")){
            button.setBackground(new Color(0x748DAE));
            button.setForeground(Color.WHITE);
            button.setOpaque(true);

        }
        if(buttonName.equalsIgnoreCase("Exact Dollar")){
            button.setBackground(new Color(0xA2AF9B));
            button.setForeground(Color.WHITE);
            button.setOpaque(true);

        }

        if(buttonName.equalsIgnoreCase("Void Item")){
            button.setBackground(new Color(0x7C444F));
            button.setForeground(Color.WHITE);
            button.setOpaque(true);

        }

        if(buttonName.equalsIgnoreCase("Quantity Change")){
            button.setBackground(new Color(0xDE8F5F));
            button.setForeground(Color.WHITE);
            button.setOpaque(true);
        }

//      LAMBDA STYLE ACTION --- button.addActionListener(e -> System.out.println(buttonName + " is Clicked"));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(buttonName+" is clicked");
                if(
                        !buttonName.equalsIgnoreCase("Next Dollar")
                        && !buttonName.equalsIgnoreCase("Exact Dollar")
                        && !buttonName.equalsIgnoreCase("Void Transaction")
                                && !buttonName.equalsIgnoreCase("Void Item")
                                && !buttonName.equalsIgnoreCase("Quantity Change"))
                {
                    pane.addTextToScreen(buttonName, null);
                }
                else if (buttonName.equalsIgnoreCase("Next Dollar")){
                    if (dollarConversion == null) {
                        dollarConversion = new DollarConversion();

//                        pane.handleNextDollar();

                    }
                    pane.addTextToScreen(buttonName, null);

                }
                else if (buttonName.equalsIgnoreCase("Void Transaction")){
                    pane.addTextToScreen(buttonName, null);
                }
                else if (buttonName.equalsIgnoreCase("Void Item")){
                    pane.addTextToScreen(buttonName, null);
                }
                else if (buttonName.equalsIgnoreCase("Exact Dollar")){
                    pane.addTextToScreen(buttonName, null);
                }
                else if (buttonName.equalsIgnoreCase("Quantity Change")){
                    pane.addTextToScreen(buttonName, null);
                }

            };
        });
        return button;

    }

}
