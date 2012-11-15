package Client;
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
     * getVendor
     * Returns the vendor.
     */
    public String getVendor();
    
    /**
     * getFeedInPrice
     * Returns the present feed-in price. Given as price per kwh. 
     */
    public double getFeedInPrice();
    
    /**
     * getTotalUseCost
     * Returns the total cost, in dollars of the present usage in a specific 
     * periodic increment. 
     */
    public double getTotalUseCost();
}
