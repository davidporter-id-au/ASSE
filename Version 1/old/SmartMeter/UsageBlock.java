package SmartMeter;
import Server.*;    
import java.util.*;
/**
 * UsageBlock
 * a class specificing the use of electricity over a period of time at a particular 
 * Price
 */
public class UsageBlock
{
    private double qty; //the quantity of usage over period
    private Date start; //the start of the period at which this was applicable
    private Date end; //the end of the period for which this was applicable. 
    private Price price; //the price at which the usage block is charged
    
    /**
     * Constructor - full
     */
    protected UsageBlock(Price p, double q, Date sd, Date ed)
    {
        setStartUse(sd);
        setEnd(ed);
        setPrice(p);
        setQty(q);
    }
    
    /**
     * Constructor - forecast
     * Used to bundle usage forecasts, ignores price. 
     */
    public UsageBlock(double q, Date sd, Date ed)
    {
        setQty(q);
        setStartUse(sd);
        setEnd(ed);
    }
    
    /**
     * Constructor - price only
     * Sets up a new usage block with a price only
     */
    protected UsageBlock(Price p)
    {
        setup(); //set the start time.
        setQty(0);
        setPrice(p);
    }
    
    
    /**
     * Setup
     * Sets up the billing usage block, as used in the constructor.
     * Creates a start date. 
     */
    private void setup()
    {
        setStartUse(new Date()); //sets to time of invokation. 
    }
    
    /**
     * add
     * Adds the specified amount to the usage.
     */
    protected void add(double in)
    {
        setQty(qty + in);
    }
    
    /**
     * setStart
     * Sets the start date.
     */
    private void setStartUse(Date s)
    {
        start = s;
    }
    
    /**
     * setPrice
     * Sets the price object
     */
    private void setPrice(Price p)
    {
        price = p;
    }
    
    /**
     * setEnd
     * Sets the end date at which the usage blocked is to have occured
     */
    private void setEnd(Date d)
    {
        end = d;
    }
    
    /**
     * SetQty
     * sets the qty used
     */
    private void setQty(double q)
    {
        qty = q;   
    }
    
    /**
     * getPrice
     * returns the price
     */
    public Price getPrice()
    {
        return price;
    }
    
    /**
     * getEnd
     * returns the end date of usageblock
     */
    public Date getEnd()
    {
        return end;
    }
    
    /**
     * getStart
     * returns the start date
     */
    public Date getStart()
    {
        return start;
    }
    
    /**
     * getQty
     * returns the quantity
     */
    public double getQty()
    {
        return qty;
        
    }
    
    /**
     * getValue
     * returns the value (price times quantity) of the block. 
     * Returns -1 if error. 
     */
    public double getValue()
    {
        try{
            return price.getPrice() * qty;
        }
        catch (InvalidSignature e)
        {
            return -1; 
        }
    }
    
    public String toString()
    {
        return "Qty: " + qty + "\nStart: " + start + "\nend: " + end + "\nprice: " +price; 
    }
    
}
