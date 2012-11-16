package SmartMeter;
/**
 * Write a description of interface SmartMeterClient here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public interface SmartMeterClient 
{ 
    
    /**
     * getPrice
     * Returns the market price of electricity at the given instant. Given as price per kwh. 
     */
    public double getPrice();
   
    /**
     * getFeedInPrice
     * Returns the present feed-in price. Given as price per kwh. 
     */
    public double getFeedInPrice();
    
    /**
     * getNetUsage
     * Ought to return a net dollar amount representing the value of electricity 
     * that has been used in the current billing cycle. 
     */
    public double getNetUsage();
}
