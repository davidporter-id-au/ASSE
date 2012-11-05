package SmartMeter;
import java.util.Date;
/**
 * PriceInterface
 * 
 * A standardised means to work with the Price Class. A snapshot of the market
 * conditions at a given instant. 
 * 
 * All implementations ought to have underlying verification of the supplier's 
 * signature implmented so that invalid information cannot be passed on.  
 */
public interface PriceInterface
{
    /**
     * getPrice
     * Returns the price as a double. 
     */
    public double getPrice();
    
    /**
     * getVendor
     * Returns the vendor as a String
     */
    public String getVendor();
    
    /**
     * getDate
     * Returns the Date the price data is valid for. 
     */
    public Date getDate();
   
}
