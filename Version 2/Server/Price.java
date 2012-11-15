package Server;
import SmartMeter.*;
import java.util.Date;
import java.io.*;
/**
 * Price
 * A signed container containing all relevant price information.
 * 
 * @author David Porter
 * @version 1
 */
public class Price implements Serializable
{
    private PriceSignal information;//The price information
    private String key;//The attached signature, verifying authenticity of 
    //the price information

    /**
     * Constructor for objects of class Price
     */
    public Price(PriceSignal setup, String h)
    {
       setPriceSignal(setup);
       setkey(h);
    }
    
    /**
     * getPrice
     * Returns a validated double, referring to the price per KWH. 
     */
    public double getPrice() 
    {
        return information.getPrice();
    }
    
    /**
     * getDate
     * Returns a validated Date referring to the date the price information
     * was sent. 
     */
    public Date getDate() 
    {
         return information.getDate();
    }
    
    /**
     * getVendor
     * Returns a validated String referring to the vendor. 
     */
    public String getVendor()
    {
        return information.getVendor();
    }
       
    /**
     * getFeedInPrice
     * returns the validated present feed-in price. 
     */
    public double getFeedInPrice() 
    {
        return information.getFeedPrice();
    }
    
    /**
     * getKey
     * returns the signature 'key'.
     */
    public String getKey()
    {
        return key;
    }
    
    /**
     * setkey
     * Private method that sets the price signal key and signature. 
     */
    private void setkey (String in)
    {
        key = in;
    }
    
    /**
     * A private method used to set the price signal at construction. 
     * Sets all relevant signed information. 
     */
    private void setPriceSignal(PriceSignal in)
    {
        information = in;
    }
    
    /**
     * toString
     * Returns a string representation of the price.
     */
    public String toString()
    {
        return "Price: " + information.getPrice() + " Feed in Price: " + information.getFeedPrice();
    }
}
