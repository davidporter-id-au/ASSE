package Server;
import java.util.Date;
/**
 * PriceSignal
 * a representation of the price of electricity at a given time.
 */
public class PriceSignal
{

    private double price; //the price, in kwh. 
    private Date date; //the Date this signal was valid for. 
    private String vendor; //The creator of this price signal
    private double feedInPrice; //the price of generated electricty

    /**
     * Constructor for objects of class PriceSignal
     */
    public PriceSignal(String v, double p, double f, Date d)
    {
     setVendor(v);
     setPrice(p);
     setDate(d);
     setFeedInPrice(f);
    }
    
    /**
     * getPrice
     * Returns the price at a given point in time. Represented as price per KWH.
     */
    public double getPrice()
    {
        return price;
    }
    
    /**
     * getDate
     * Returns the date at which this price signal was valid for. 
     */
    public Date getDate()
    {
        return date; 
    }
    
    /**
     * getVendor
     * Returns the vendor of origin. 
     */
    public String getVendor()
    {
        return vendor;
    }
    
    /**
     * setVendor
     * A private method used to set the Vendor variable. 
     * Should not be accessible outside the class once constructed. 
     * once 
     */
    private void setVendor(String s)
    {
        vendor = s;
    }
    
    /**
     * setPrice
     * A private method used to set the price at construction. 
     * @param p Price, per KWH, given at time the signal is issued. 
     */
    private void setPrice(double p)
    {
        price = p;
    }
    
    /**
     * setDate
     * A private method used to set the Date valid for the price signal at 
     * construction. 
     * @param d the Date being specified for that price signal point. 
     */
    private void setDate(Date d)
    {
        date = d;
    }
    
    /**
     * getFeedPrice
     * Retrieves the feedin price. The price at which electricity is resold to the market. 
     */
    public double getFeedPrice()
    {
        return feedInPrice;
    }
    
    /**
     * setFeedPrice
     * A private method used to set the feed-in price. 
     */
    private void setFeedInPrice(double in)
    {
        feedInPrice = in;
    }
}
