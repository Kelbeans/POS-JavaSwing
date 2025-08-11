package common.Model;

import javax.swing.*;

public class Modal{

    private Pane pane;

    private JDialog dialog;

   public void displayModal(String modalName){

       dialog = new JDialog();
       dialog.setTitle(modalName);
       dialog.setBounds(350, 350, 300,300);
       dialog.setVisible(true);

       JLabel l = new JLabel("this is a dialog box");
       dialog.add(l);

       JButton b = new JButton("OK");
       dialog.add(b);

   }

}
