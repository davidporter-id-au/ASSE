import SmartMeter.*;
import Server.*;
import java.util.*;
/**
 * An attempt to create external and damanging modificaitons to the SmartMeter class
 * 
 * @author David Porter
 */
public class ExternalTests
{


    /**
     * AccessTest
     */
    public static void Test3_PrivateMethodsAndVariables()
    {
        
       //none of the following should be allowed:
        
       
//             Engine.currentPrice = null; //the present price being used. 
//             Engine.currentUse = null;// the current usage block. 
//             Engine.currentProduction = null; //the current production block
//             Engine.usage = null; //the history of usage as a list of usageBlocks
//             Engine.production = null; //the amount of produced power
//             Engine.socket = null; //the simulated server socket
//             Engine.VALIDTIMEFRAME = 5 = null; //the amount of time a command is active for, in minutes
//             Engine.VENDOR = "vendor" = null; //The specific vendor for which this device
//             Engine.ensor = null; //the input from the electrical physical reader, indicating usage
//             Engine.forecast = null; //the forecast amount of usage as is provided by appliances
//             Engine.cryptoKey = null; 

//         Engine.addUsage(15);
//         Engine.addProduction(15);
//         Engine.forecast();
//         Engine.usage();
//         Engine.clear();
//         Engine.setUsage(new Vector());
//         Engine.setProduction(new Vector());
//         Engine.getCurrentPrice();
//         Engine.packup();
//         Engine.setCurrentPrice(new Price (new PriceSignal("Vend", 23,23, new Date())));
//         
//         
        


    }
    
    public static void test3_publicMethodsAndVariables()
    {
        Engine.getPrice();
        Engine.updateForecast();
        Engine.update();
        
        
    }
    
    public static void test4_MissingEnc()
    {
        ServerSocket server = Engine.getServer();
        
        Stack s = server.receivedData; //get stack directly from the server
        
        while(!s.isEmpty())
        {
            System.out.println((String)s.pop());
            
        }
        
    }
    
    
}
