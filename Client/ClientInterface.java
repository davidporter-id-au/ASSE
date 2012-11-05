package Client;
import SmartMeter.*;
import Server.*;
import java.util.*;
/**
 * Write a description of class ClientEngine here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class ClientInterface 
{
    /**
     * getPrice
     * Returns a validated current price of electricity. 
     * Returns negative 1 if invalid signature. 
     */
    public double getPrice()
    {
        return Engine.getPrice();
    }
    
    /**
     * forecast
     * Creates an array of UsageBlocks as a simple means to forecast future useage
     * and returns them. Intended to represent the forecasted electricity usage over 
     * periods by applications as they register themselves with the meter. 
     */
    public static UsageBlock[] forecast()
    {
        UsageBlock [] u = new UsageBlock [10];
        
        for (int i=0; i < u.length; i++)
        {
            u[i] = new UsageBlock(Math.random()*100, new Date(), new Date());
        }
        
        return u;
    }
}
