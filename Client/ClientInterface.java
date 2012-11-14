package Client;
import SmartMeter.*;
import Server.*;
import java.util.*;
/**
 * Client interface
 * The simulation interface with which the customer may interact with the Smart
 * Meter. 
 * 
 * @author David Porter
 * @version 1
 */
public class ClientInterface  implements SmartMeterClient
{
    Engine engine;//The connection to the main smartmeter engine.

    public ClientInterface(Engine e)
    {
        engine = e;
    }


    /**
     * getPrice
     * Returns a validated current price of electricity. 
     * Returns negative 1 if invalid signature. 
     */
    public double getPrice()
    {
        return engine.getPrice();
    }
    
    /**
     * forecast
     * Creates an array of UsageBlocks as a simple means to forecast future useage
     * and returns them. Intended to represent the forecasted electricity usage over 
     * periods by applications as they register themselves with the meter. 
     * 
     * For testing purposes here it will always use a quantity of 100. 
     */
    public  UsageBlock[] forecast()
    {
        UsageBlock [] u = new UsageBlock [3];
        
        for (int i=0; i < u.length; i++)
        {
            u[i] = new UsageBlock(100, new Date(), new Date());
        }
        
        return u;
    }
    
    /**
     * getNetUsage
     * A method which allows for the summation of billing information for the client
     * per given billing period. 
     */
    public double getNetUsage()
    {
        return 0;
    }
    
    /**
     * getFeedInPrice
     * Returns the 'feed in' price, or the price at which electricity is sold on to the 
     * Grid. 
     */
    public double getFeedInPrice()
    {
        return engine.getFeedInPrice();
    }
    
    
}
