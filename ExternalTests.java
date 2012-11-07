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

    Engine engine;//The main Smart-meter class
    
    public ExternalTests()
    {
    }
    
    /**
     * signatureTest
     * The purpose of the the test is to determine if the Smart meter will correctly
     * handle being given a number of commands with invalid signatures. 
     * 
     * Expected output: Failure to execute all commands. 
     * 
     * All data should remain as initially declared, at default values. 
     */
    public void signatureTest()
    {
        ServerSocket normalServer = new ServerSocket();
        ServerSocket hostileServer = new ServerSocket();
        
        normalServer.validCommands(); //Engage a sequence of valid commands
        hostileServer.testSignature(); //Engage a series of hostile and invalid commands
        
        Engine engine = new Engine(normalServer); //Start up a new instance of the smartmeter for testing 
        //and with default values
        
        engine.command();//execute commands
        
        
        examineSM(engine); //examine and display information
        
        
        engine.setServer(hostileServer);
        engine.command(); //attempt to execute
        examineSM(engine);//examine
        
    }
    
    /**
     * AccessTest
     */
    public  void Test3_PrivateMethodsAndVariables()
    {
        
       //none of the following should be allowed:
        
       
//             engine.currentPrice = null; //the present price being used. 
//             engine.currentUse = null;// the current usage block. 
//             engine.currentProduction = null; //the current production block
//             engine.usage = null; //the history of usage as a list of usageBlocks
//             engine.production = null; //the amount of produced power
//             engine.socket = null; //the simulated server socket
//             engine.VALIDTIMEFRAME = 5 = null; //the amount of time a command is active for, in minutes
//             engine.VENDOR = "vendor" = null; //The specific vendor for which this device
//             engine.ensor = null; //the input from the electrical physical reader, indicating usage
//             engine.forecast = null; //the forecast amount of usage as is provided by appliances
//             engine.cryptoKey = null; 

//         engine.addUsage(15);
//         engine.addProduction(15);
//         engine.forecast();
//         engine.usage();
//         engine.clear();
//         engine.setUsage(new Vector());
//         engine.setProduction(new Vector());
//         engine.getCurrentPrice();
//         engine.packup();
//         engine.setCurrentPrice(new Price (new PriceSignal("Vend", 23,23, new Date())));
//         
//         
        


    }
    
    public  void test3_publicMethodsAndVariables()
    {
        engine.getPrice();
        engine.updateForecast();
        engine.update();
    }
    
    /**
     * examineSM
     * Displays internal information of the smartmeter through the debug interface
     */
    public void examineSM(Engine e)
    {
        System.out.println(e.smDebug());
    }
    
    /**
     * examineServer
     * Takes a 'Server' set of output and displays it on screen. 
     */
    public void examineServer(ServerSocket server)
    {        
        Stack s = server.receivedData; //get stack directly from the server
        
        while(!s.isEmpty())
        {
            System.out.println((String)s.pop());
        }
    }
    
    
    
    
}
