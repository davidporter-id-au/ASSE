package SmartMeter;
import Server.*;
import java.util.*;
import Client.*;
/**
 * Engine
 * The main controller class, the class which is responsible for instansiation and control
 * of all smart-meter objects and their appropriate functions. While typically
 * This would exist in a concurrent environment, here, it will act to demonstrate such 
 * activity by reading through a simulated Server socket the various activities and 
 * commands as would be performed by a smart meter. 
 * 
 * 
 * A note on Crypto, it assumed that in this version that Symmetric Encryption with a 
 * Hardcoded key is being used. 
 * 
 * @author David Porter
 * @version 1
 */
public class Engine
{
    private static Price currentPrice; //the present price being used. 
    private static UsageBlock currentUse;// the current usage block. 
    private static UsageBlock currentProduction; //the current production block
    private static AbstractList usage; //the history of usage as a list of usageBlocks
    private static AbstractList production; //the amount of produced power
    private static ServerSocket socket; //the simulated server socket
    private static final int VALIDTIMEFRAME = 5; //the amount of time a command is active for, in minutes
    private static final String VENDOR = "vendor"; //The specific vendor for which this device
    private static SensorInput sensor; //the input from the electrical physical reader, indicating usage
    private static UsageBlock[] forecast; //the forecast amount of usage as is provided by appliances
    private static final String cryptoKey = "###Secret KEY###"; //The symmetric crypto key. 
    
    /**
     * startupEngine
     * The equivalent of a contructor, but for the static elements of the engine. 
     * Must be called first at runtime. 
     */
    public static void startupEngine()
    {
        clear(); //Use a vector to add all the elements in the usage log. 
        socket = new ServerSocket();
        getCurrentPrice();
        currentUse = new UsageBlock(currentPrice);//start new block based on new price
        updateForecast();
        setProduction(new Vector()); //set the initial queue for production as it occurs. 
    }
    
    /**
     * Command
     * The command loop, wait for commands and then execute them
     */
    public static void command()
    {
        Command c = listener();//normally this would be called by some kind of event reception. 
        
        while (c != null) //while there are commands, keep on working through them. 
        {
       
            try
            {
                if(timeCheck(c)) //ensure that the command was issued within a valid time frame. 
                { //ie, that it is not an old command being replayed by an an attacker. 
                    if(c.getAction().equals("update"))
                    {
                        //update prices method call
                        System.out.println("update");
                        update(); //pass command specifics to method
                    }
                    else if (c.getAction().equals("forecast"))
                    {
                        System.out.println("forecast");
                        forecast();
                    }
                    else if (c.getAction().equals("usage"))
                    {
                        System.out.println("usage");
                        usage();
                    }
                    else if (c.getAction().equals("clear"))
                    {
                        //Clear logs method call
                        clear();
                    }
                }
                 else //invalid time frame specified. 
                {
                    System.out.println("Invalid timeframe for command");
                }
            }
            catch (InvalidSignature e)
            {
                //
            }
            
           
            c = listener();  //get the next command
        }
    }
    
    /**
     * addUsage
     * Adds the specified amount of power to the present block of usage. 
     */
    protected static void addUsage(double in)
    {
        currentUse.add(in);
    }
    
    protected static void addProduction(double in)
    {
        production.add(in);
    }
    
    /**
     * forecast
     * Sends the forecast data for applications for the given period. 
     */
    private static void forecast()
    {
       updateForecast();
       sender(forecast);
    }
    
    /**
     * usage
     * Sends the usage data. 
     */
    private static void usage()
    {
        Object [] send = new Object [3]; //create a bundle of the items to send
        
        send[0] = "Production and consumption of electricity ";
        
        //Add usage
        send[1] = usage;
        
        //add production
        send[2] = production; 
        
        sender(usage); //send the list of usage data   
        sender(production);//send the production data
    }
    
    /**
     * clear 
     * Clears the log files, implicitly it is understood that coomand will be invoked 
     * once usage data has been sent and confirmed. 
     * Note this specifies a vector as the usage type. However, this is not necessary. 
     */
    private static void clear()
    {
        setUsage(new Vector());
        setProduction(new Vector());
    }
    
    /**
     * setUsage
     * Sets the usage data. 
     */
    private static void setUsage(AbstractList l)
    {
        usage = l;
    }
    
    /**
     * setProduction
     * Sets the production list, ie, the list of production blocks of power. 
     */
    private static void setProduction(AbstractList l)
    {
        production = l;
    }
    
    /**
     * getPrice
     * Provides the present price in decimal form. 
     * Will throw -1 in the event of a problem. 
     */
    public static double getPrice()
    {   
        try{
            return currentPrice.getPrice();
        }
        catch(InvalidSignature e)
        {
            System.out.println("Signature error");
            return -1 ;
        }
    }
    
    /**
     * getCurrentPrice
     * Updates the current operational price and set it. 
     * 
     * Given the demonstrative nature of the class, this is only 
     * creating the information our of thin air. In reality, 
     * it ought to get it through a socket or some kind of 
     * transport medium from the provider. 
     */
    private static void getCurrentPrice()
    {
        
            
            
       Price p = socket.sendPrice(); //Get the present price from the server
       
       if (p.validSig())
       {
           setCurrentPrice(p);
           try
           {
                System.out.println("Price updated to " + currentPrice.getPrice()); 
           }
           catch(InvalidSignature e)
           {
               System.out.println("Price signature invalid, price not updated"); //redundant
           }
        }
        else 
        {
            System.out.println("Price signature invalid, price not updated");
        }
        
    }
    
    /**
     * updateForecast
     * updates the forecast data from appliances.
     */
    public static void updateForecast()
    {
        System.out.println("Updating forcast...");
        forecast = ClientInterface.forecast();
        
        for(int i=0; i<forecast.length; i++)
        {
            System.out.println("Block " + i +": " +forecast[i].getQty());
            
        }
    }
    
    /**
     * update
     * Downloads the latest price information and sets it accordingly
     * Note that this command may be invoked by any party, including the 
     * customer. It does require a valid signature, but does 
     * not require vendor appropval
     */
    public static void update()
    {
        System.out.println("refreshing price information...");
        getCurrentPrice(); //refresh price information. 
        packup(); //packup the old usage block and place it in the log. 
        currentUse = new UsageBlock(currentPrice);//start new block based on new price
        currentProduction = new UsageBlock(currentPrice);
        
    }
    
    /**
     * packup
     * Takes the current usage block and packs it up and places it in the log
     */
    private static void packup()
    {
        usage.add(currentUse); //add to the use log
        currentUse = null; //and erase.
        
        production.add(currentProduction);
        currentProduction = null;
        
    }
    
    /**
     * setCurrentPrice
     * Sets the current operation price. 
     */
    private static void setCurrentPrice(Price p)
    {
        currentPrice = p;
    }
    
    /**
     * listener
     * A simulation of something that listens at a socket, and when a command is 
     * received, passes it on to the command method
     */
    private static Command listener()
    {
        return socket.issueCommands();
    }
    
    /**
     * sender
     * A simulated socket. Commands are sent through this to be theoretically dispatched
     * securely to the server. Signed on the fly and dispatched with signature. 
     */
    private static void sender(Object o)
    {   
        String sendString = encryptSign(o);
        //send to server through socket
        
        System.out.println("Sending to socket: " + sendString);
    }
    
    /**
     * encryptSign
     * encrypts and signs the given data. In this case, the signature and encryption
     * is given simulated only. 
     */
    private static String encryptSign(Object o)
    {
        return "Encrypted and sent object." + cryptoKey + "\nObject being sent:" + o;
    }
    
    /**
     * timeCheck
     * a method that checks the timestamp on a signature and will return
     * true if the time is within a VALIDTIMEFRAME. 
     * 
     * As of version 1, this is not implemented but merely serves as an example of 
     * this kind of behaviour. At the present, this will return false as it 
     * receives an invalid signature. 
     */
    private static boolean timeCheck(Command c)
    {
        try{
            Date actionTime = c.getActionTime();

//             Calendar from = new GregorianCalendar();
//             
//             Calendar until = new GregorianCalendar();
//             
//             for(int i=0 ;i<VALIDTIMEFRAME; i++) 
// roll forward valid time VALIDTIME number of minutes
//             {
//                 until.roll(Calendar.MINUTE, true);
//             }
//             
//             for(int i=0; i<VALIDTIMEFRAME; i++)
//             {
//                 from.roll(Calendar.MINUTE, false); //roll back 
//             }
//             
//             if(from.before(actionTime))
//             {
//                 if(until.after(actionTime))              
//                 {
//                     return true; //time must be valid. 
//                 }
//                 else return false;
//             }
//             else return false;
//             
            return true; //need to figure out some kind of time checking mechanism
        }
        catch (InvalidSignature e)
        {
           // System.out.println("Invalid Date signature");
            return false;
            
        }
        
    }
    
    public static ServerSocket getServer() 
    {
        return socket;
    }
    
}
