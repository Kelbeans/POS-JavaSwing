//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import common.Model.Button;
import common.Model.Pane;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
public class Main {

    public static void main(String[] args) throws Exception {

//        try (Connection conn = DatabaseConfig.getConnection()) {
//
//            System.out.println("Connected to H2!");
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }


        System.out.println("******** Items in the Basket **********");
        /*
            *Test Components
        */
        // Main Frame
        JFrame mainWindow = new JFrame();

        // Main window styling
        mainWindow.setTitle("Basic Point of Sale System");
        mainWindow.setSize(1000,1000);
        mainWindow.setLayout(null);

        Pane pane = new Pane();

        JLabel popularItemsLabel = new JLabel("Popular Items");
        popularItemsLabel.setBounds(250,0,380,20);
        popularItemsLabel.setFont(new Font("Monospaced", Font.PLAIN, 15));

        JLabel actionButtonLabel = new JLabel("Action Buttons");
        actionButtonLabel.setBounds(250, 420, 380, 20);
        actionButtonLabel.setFont(new Font("Monospaced", Font.PLAIN, 15));


        //Button Components
        Button coffee = new Button(pane);
        ArrayList<Integer> coffeeButtonStyle = new ArrayList<>();
        coffeeButtonStyle.add(0);
        coffeeButtonStyle.add(20);

        Button hotdog = new Button(pane);
        ArrayList<Integer> hotdogButton = new ArrayList<>();
        hotdogButton.add(0);
        hotdogButton.add(220);

        Button donut = new Button(pane);
        ArrayList<Integer> donutButton = new ArrayList<>();
        donutButton.add(200);
        donutButton.add(20);
        Button iceCream = new Button(pane);
        ArrayList<Integer> iceCreamButton = new ArrayList<>();
        iceCreamButton.add(400);
        iceCreamButton.add(20);

        Button juice = new Button(pane);
        ArrayList<Integer> juiceButton = new ArrayList<>();
        juiceButton.add(200);
        juiceButton.add(220);

        Button milkShake = new Button(pane);
        ArrayList<Integer> milkShakeButton = new ArrayList<>();
        milkShakeButton.add(400);
        milkShakeButton.add(220);

        Button exactDollar = new Button(pane);
        ArrayList<Integer> exactDollarButton = new ArrayList<>();
        exactDollarButton.add(0);
        exactDollarButton.add(440);

        Button nextDollar = new Button(pane);
        ArrayList<Integer> nextDollarButton = new ArrayList<>();
        nextDollarButton.add(200);
        nextDollarButton.add(440);

        Button voidTransaction = new Button(pane);
        ArrayList<Integer> voidTransactionButton = new ArrayList<>();
        voidTransactionButton.add(400);
        voidTransactionButton.add(440);

        Button voidItem = new Button(pane);
        ArrayList<Integer> voidItemButton = new ArrayList<>();
        voidItemButton.add(0);
        voidItemButton.add(640);

        Button quantityChange = new Button(pane);
        ArrayList<Integer> quantityChangeButton = new ArrayList<>();
        quantityChangeButton.add(200);
        quantityChangeButton.add(640);


        /* Adding Components to the Main Window */
        mainWindow.add(popularItemsLabel);
        mainWindow.add(actionButtonLabel);
        mainWindow.add(coffee.testButtonPanel("Coffee", coffeeButtonStyle));
        mainWindow.add(hotdog.testButtonPanel("Hotdog", hotdogButton));
        mainWindow.add(donut.testButtonPanel("Donut", donutButton));
        mainWindow.add(iceCream.testButtonPanel("Ice Cream", iceCreamButton));
        mainWindow.add(juice.testButtonPanel("Juice", juiceButton));
        mainWindow.add(milkShake.testButtonPanel("Milk Shake", milkShakeButton));
        mainWindow.add(exactDollar.testButtonPanel("Exact Dollar", exactDollarButton));
        mainWindow.add(nextDollar.testButtonPanel("Next Dollar", nextDollarButton));
        mainWindow.add(voidTransaction.testButtonPanel("Void Transaction", voidTransactionButton));
        mainWindow.add(voidItem.testButtonPanel("Void Item", voidItemButton));
        mainWindow.add(quantityChange.testButtonPanel("Quantity Change", quantityChangeButton));
        mainWindow.add(pane.displayPane());

        // Make the Main Window Visible to Screen.
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setVisible(true);

        pane.scanBarcode();

    }


}