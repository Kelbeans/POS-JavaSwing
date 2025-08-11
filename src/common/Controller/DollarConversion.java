package common.Controller;

public class DollarConversion {

    double finalValue = 0.00;

    public double getNextDollarValue(double value){
            finalValue = Math.ceil(value);
        return finalValue;
    }
}
