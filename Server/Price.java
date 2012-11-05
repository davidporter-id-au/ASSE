package Server;
import SmartMeter.*;
import java.util.Date;
/**
 * Price
 * A signed container containing all relevant price information.
 * 
 * @author David Porter
 * @version 1
 */
public class Price
{
    private PriceSignal information;//The price information
    private boolean validSig; //an abstraction of the signature verification process
    private String hash;//The attached signature, verifying authenticity of 
    //the price information

    /**
     * Constructor for objects of class Price
     */
    public Price(PriceSignal setup, String h)
    {
       setPriceSignal(setup);
       setHash(h);
    }
    
    /**
     * Constructor with signature
     * allows for the specification of a valid or invalid signature
     */
    public Price (PriceSignal setup, String h, boolean sig)
    {
        this(setup,h);
        validSig = sig; // setup the boolean representation of the signature;
    }
    
    /**
     * getPrice
     * Returns a validated double, referring to the price per KWH. 
     */
    public double getPrice() throws InvalidSignature
    {
        if(validSig()) 
        {
            return information.getPrice();
        }
        else throw new InvalidSignature();
    }
    
    /**
     * getDate
     * Returns a validated Date referring to the date the price information
     * was sent. 
     */
    public Date getDate() throws InvalidSignature
    {
        if(validSig())
        {
            return information.getDate();
        }
        else throw new InvalidSignature();
    }
    
    /**
     * getVendor
     * Returns a validated String referring to the vendor. 
     */
    public String getVendor() throws InvalidSignature
    {
        if(validSig())
        {
            return information.getVendor();
        }
        else throw new InvalidSignature();
    }
    
    /**
     * validSig
     * Performs a check of the associated signature upon the information
     * given and returns true if found to be valid. 
     */
    public boolean validSig()
    {
        return validSig;//some check of validity
    }
    
    /**
     * getFeedInPrice
     * returns the validated present feed-in price. 
     */
    public double getFeedInPrice() throws InvalidSignature
    {
        if(validSig())
        {
            return information.getFeedPrice();
        }
        else throw new InvalidSignature();
    }
    
    /**
     * setHash
     * Private method that sets the price signal hash and signature. 
     */
    private void setHash (String in)
    {
        hash = in;
    }
    
    /**
     * A private method used to set the price signal at construction. 
     * Sets all relevant signed information. 
     */
    private void setPriceSignal(PriceSignal in)
    {
        information = in;
    }
}
