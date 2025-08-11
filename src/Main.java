//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import common.Model.Button;
import common.Model.Pane;
import java.util.ArrayList;
import javax.swing.*;
public class Main {

    public static void main(String[] args) {

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
        mainWindow.setSize(1000,1000);
        mainWindow.setLayout(null);

        Pane pane = new Pane();


        //Button Components
        Button coffee = new Button(pane);
        ArrayList<Integer> coffeeButtonStyle = new ArrayList<>();
        coffeeButtonStyle.add(0);
        coffeeButtonStyle.add(0);

        Button hotdog = new Button(pane);
        ArrayList<Integer> hotdogButton = new ArrayList<>();
        hotdogButton.add(0);
        hotdogButton.add(200);

        Button donut = new Button(pane);
        ArrayList<Integer> donutButton = new ArrayList<>();
        donutButton.add(200);
        donutButton.add(0);
        Button iceCream = new Button(pane);
        ArrayList<Integer> iceCreamButton = new ArrayList<>();
        iceCreamButton.add(400);
        iceCreamButton.add(0);

        Button juice = new Button(pane);
        ArrayList<Integer> juiceButton = new ArrayList<>();
        juiceButton.add(200);
        juiceButton.add(200);

        Button milkShake = new Button(pane);
        ArrayList<Integer> milkShakeButton = new ArrayList<>();
        milkShakeButton.add(400);
        milkShakeButton.add(200);

        Button exactDollar = new Button(pane);
        ArrayList<Integer> exactDollarButton = new ArrayList<>();
        exactDollarButton.add(0);
        exactDollarButton.add(400);

        Button nextDollar = new Button(pane);
        ArrayList<Integer> nextDollarButton = new ArrayList<>();
        nextDollarButton.add(200);
        nextDollarButton.add(400);

        Button voidTransaction = new Button(pane);
        ArrayList<Integer> voidTransactionButton = new ArrayList<>();
        voidTransactionButton.add(400);
        voidTransactionButton.add(400);

        /* Adding Components to the Main Window */
        mainWindow.add(coffee.testButtonPanel("Coffee", coffeeButtonStyle));
        mainWindow.add(hotdog.testButtonPanel("Hotdog", hotdogButton));
        mainWindow.add(donut.testButtonPanel("Donut", donutButton));
        mainWindow.add(iceCream.testButtonPanel("Ice Cream", iceCreamButton));
        mainWindow.add(juice.testButtonPanel("Juice", juiceButton));
        mainWindow.add(milkShake.testButtonPanel("Milk Shake", milkShakeButton));
        mainWindow.add(exactDollar.testButtonPanel("Exact Dollar", exactDollarButton));
        mainWindow.add(nextDollar.testButtonPanel("Next Dollar", nextDollarButton));
        mainWindow.add(voidTransaction.testButtonPanel("Void Transaction", voidTransactionButton));
        mainWindow.add(pane.displayPane());

        // Make the Main Window Visible to Screen.
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setVisible(true);

        pane.scanBarcode();

    }


}