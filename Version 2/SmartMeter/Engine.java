package SmartMeter;
import Server.*;
import java.util.*;
import Client.*;
import java.io.*;
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
    private  PriceInterface currentPrice; //the present price being used. 
    private  UsageBlock currentUse;// the current usage block. 
    private  UsageBlock currentProduction; //the current production block
    private  AbstractList <UsageBlock> usage; //the history of usage as a list of usageBlocks
    private  AbstractList <UsageBlock> production; //the amount of produced power
    
    //Communication objects:
    private ServerSocket socket; //the simulated server socket
    private ClientInterface client; //The simulated communication with a client interface. 
    
    private  final int VALIDTIMEFRAME = 5; //the amount of time a command is active for, in minutes
    private  final String VENDOR = "vendor"; //The specific vendor for which this device
    private  SensorInput sensor; //the input from the electrical physical reader, indicating usage
    private  UsageBlock[] forecast; //the forecast amount of usage as is provided by appliances
    
    private String cryptoKey; //The Provider's public key
    private String meterPublicKey; //The smart meter's public key
    private String meterPrivateKey; //The smart meter's private key
    
    private FileHandler fileHandler; //The file handler
    
    private final String id = "12345"; //A hard-coded unique identifier constant, equivalent of something simar to MAC address
    
    /*=============Debug information ==================*/
    private int commandExec_Debug; //A debug variable used to demonstrate the number of command executions that have occured. 
    
    private boolean verbose = false;
    
    private final String StartMsg = "\nSmart Meter starting up\n Use of this device is governed by applicable laws... \n"; //Display message on startup
    
    
    /**
     * debugConstructor
     * creates an engine which operates with the option to flag verbose mode on.
     */
    public Engine(ServerSocket s, String providerKey, boolean v)
    {
        this(s, providerKey);
        verbose = v;
    }
    
    /**
     * Constructor
     * Please note that the ability to provide a specified socket here is for testing only.
     * @param s The server which the engine will be interacting with.
     * @param c The clinet interface with which the Smart Meter Engine will be interacting with. 
     */
    public Engine(ServerSocket s, String providerKey)
    {
        System.out.println(StartMsg);
       // System.out.println("\nSystem Loading");
        
        try
        {
            fileHandler = new FileHandler(id, providerKey);
        }
        catch(IOException e)
        {
            System.out.println("IO Error");
        }
        
        setupKeys(providerKey);//get the keys
        
        
        setServer(s);
        setClient(this);
        
        clear(); //Use a vector to add all the elements in the usage log. 
        existingUsage(); //load up the existing data from disk if available. 
        
        
        getCurrentPrice();
        currentUse = new UsageBlock(currentPrice);//start new block based on new price
        updateForecast();
        setProduction(new Vector()); //set the initial queue for production as it occurs. 
        sensor = new SensorInput();
        
        sensorInput();//simulate some use

        
        //Debug
        commandExec_Debug = 0;
        
        //End debug
        
    }
    
    /**
     * setupKeys
     * Sets the keys. 
     */
    private void setupKeys(String providerKey)
    {
        try
        {
            meterPrivateKey = fileHandler.getPrivateKey();
            meterPublicKey = fileHandler.getPublicKey();
            cryptoKey = fileHandler.getProviderKey();
        }
        catch(IOException e)
        {
            System.out.println("File error");
        }
    }
    
    /**
     * writeUsageData
     * Writes the present usage data to a file. In a concurrently run system, this would be periodically
     * updated as required. Here the periodic write is only simulated. 
     */
    private void writeUsageData()
    {
        try{
            fileHandler.writeUsage(usage);
        }
        catch(IOException e)
        {
            System.out.println("IOException, unable to write out usage");
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * writeProductionData
     * Writes the present level of production of electricity to a file. Similarly with the above method,
     * this ought to be run periodically in a concurrent system. 
     */
    private void writeProductionData()
    {
        try{
            fileHandler.writeProduction(production);
        }
        catch(IOException e)
        {
            System.out.println("IOException, unable to write out production");
            System.out.println(e.getMessage());
        }
    }
    
    
    /**
     * existingUsage
     * Loads up the existing usage with the fileHandler
     */
    private void existingUsage()
    {
        try
        {
            
            AbstractList <UsageBlock> b = fileHandler.getUsage();
            
            if(b != null)
                usage = b;
                
            AbstractList <UsageBlock> p = fileHandler.getProduction();
            
            if(p != null)
                production = p;
        }
        catch(IOException e)
        {
            System.out.println("IOException");
        }
        catch(ClassNotFoundException e)
        {
            System.out.println("ClassNotFoundException, data not loaded");
        }
        
    }
    
    
    /**
     * command
     * The command loop, wait for commands and then execute them.
     */
    public void command()
    {
        Command c = listener();//normally this would be called by some kind of event reception. 
        
        while (c != null) //while there are commands, keep on working through them. 
        {
       
            try
            {
                verifyCommandSignature(c); //Check signature of the command.    
                if(timeCheck(c.getActionTime())) //ensure that the command was issued within a valid time frame. 
                
                
                { //ie, that it is not an old command being replayed by an an attacker. 
                    if(c.getAction().equals("update"))
                    {
                        //update prices method call
                        if(verbose)
                            System.out.println("update");
                        update(); //pass command specifics to method
                    }
                    else if (c.getAction().equals("forecast"))
                    {
                        //System.out.println("forecast");
                        forecast();

                    }
                    else if (c.getAction().equals("usage"))
                    {
                        //System.out.println("usage");
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
                    if(verbose)
                        System.out.println("Invalid timeframe for command");
                }
            }
            catch (InvalidSignature e)
            {
                //
            }
            
           
            c = listener();  //get the next command
            
            writeUsageData(); //Write out the present usage data at each command iteration, to simulate the progression of data.
            writeProductionData();
        }
    }
    
    /**
     * verifyCommandSignature
     * Takes a command and verifies its 'key' against the one stored locally to determine the authenticity of the command
     */
    protected void verifyCommandSignature(Command c) throws InvalidSignature
    {
        if (!c.getSig().equals(cryptoKey))
        {
            if(verbose)
                throw new InvalidSignature("\nInvalid Signature, command not executed\n");
            else throw new InvalidSignature("");
        }
    }
    
    /**
     * sensorInput
     * Since the class isn't using concurrency, this is being simulated by a loop into a 
     * queue here, representing usage over time. This is not a 'real' method class, 
     * rather, it is a demonstration of the sensor being used over time. The 5 here is an 
     * arbirtrary demonstrative number. 
     * 
     * This is only for a single price point, it does not affect the creation of usage blocks
     * since they will only change when there is a change in price. 
     * 
     * Note that this is being called from the update method for test purposes. It could be 
     * theoretically called at any point in time because it represents the quantity of usage 
     * over a passage of time. However, if done this way it will simulate data usage
     * over price changes and create usage blocks. 
     * 
     */
    private void sensorInput()
    {
        for(int i =0; i<5; i++)
        {
            sensorData();
        }
    }
    
    /**
     * sensorData
     * The representation of data being added to usage records as it is detected.
     * However, this method lacks a time dimension and would typically be called
     * concurrently. 
     */
    private void sensorData()
    {
        addUsage(sensor.issue());
    }
    
    /**
     * addUsage
     * Adds the specified amount of power to the present block of usage. 
     */
    protected void addUsage(double in)
    {
        currentUse.add(in);
    }
    
    protected void addProduction(double in)
    {
        currentProduction.add(in);
    }
    
    /**
     * forecast
     * Sends the forecast data for applications for the given period. 
     */
    private  void forecast()
    {
       updateForecast();
       sender(forecast);
       
       commandExec_Debug++;
    }
    
    /**
     * usage
     * Sends the usage data. 
     */
    private  void usage()
    {
        Object [] send = new Object [3]; //create a bundle of the items to send
        
        
        sender(usage); //Send the usage and production data
        sender(production);
        
        commandExec_Debug++; //increment execution count
    }
    
    /**
     * clear 
     * Clears the log files, implicitly it is understood that coomand will be invoked 
     * once usage data has been sent and confirmed. 
     * Note this specifies a vector as the usage type. However, this is not necessary. 
     */
    private  void clear()
    {
        setUsage(new Vector());
        setProduction(new Vector());
        
        commandExec_Debug++;
    }
    
    /**
     * setUsage
     * Sets the usage data. 
     */
    private  void setUsage(AbstractList l)
    {
        usage = l;
    }
    
    /**
     * setProduction
     * Sets the production list, ie, the list of production blocks of power. 
     */
    private  void setProduction(AbstractList l)
    {
        production = l;
    }
    
    /**
     * getPrice
     * Provides the present use price in decimal form. 
     * Will throw -1 in the event of a problem. 
     */
    public double getPrice()
    {   
        if(currentPrice.getKey().equals(cryptoKey))
            return currentPrice.getPrice();
        else
        {
            if(verbose)
                System.out.println("Signature error - Price update not performed");
            return -1 ;
        }
    }
    
    /**
     * getFeedInPrice
     */
    public double getFeedInPrice()
    {
        return currentPrice.getFeedInPrice();
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
    private  void getCurrentPrice()
    {   
       Price p = socket.sendPrice(); //Get the present price from the server
       
       if (p.getKey().equals(cryptoKey) && timeCheck(p.getDate()))
       {
           setCurrentPrice(p);
           if(verbose)
                System.out.println("Price updated to " + currentPrice.getPrice()); 
        }
       else 
       {
           if(verbose)
                System.out.println("Price signature invalid, price not updated");
       }
    }
    
    /**
     * updateForecast
     * updates the forecast data from appliances.
     */
    public  void updateForecast()
    {
        if(verbose)
            System.out.println("Updating forcast...");
        forecast = client.forecast();
        
        for(int i=0; i<forecast.length; i++)
        {
            if(verbose)
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
    public  void update()
    {
        sensorInput(); //Add usage quatity to simulate passage of time
        if(verbose)
            System.out.println("refreshing price information...");
        getCurrentPrice(); //refresh price information. 
        packup(); //packup the old usage block and place it in the log. 
        currentUse = new UsageBlock(currentPrice);//start new block based on new price
        currentProduction = new UsageBlock(currentPrice);
        
        commandExec_Debug++;
    }
    
    /**
     * packup
     * Takes the current usage block and packs it up and places it in the log
     */
    private  void packup()
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
    private  void setCurrentPrice(Price p)
    {
        currentPrice = p;
    }
    
    /**
     * listener
     * A simulation of something that listens at a socket, and when a command is 
     * received, passes it on to the command method
     */
    private  Command listener()
    {
        return socket.issueCommands();
    }
    
    /**
     * sender
     * A simulated socket. Commands are sent through this to be theoretically dispatched
     * securely to the server. Signed on the fly and dispatched with signature. 
     */
    private void sender(Object o)
    {   
        String sendString = encryptSign(o);
        
        //send to server through socket
        socket.receiveData(sendString);
        if(verbose)
            System.out.println("Sending to socket: " + sendString);
    }
    
    /**
     * encryptSign
     * encrypts and signs the given data. In this case, the signature and encryption
     * is given simulated only. 
     */
    private String encryptSign(Object o)
    {
        return "<Start symmetric encryption with for Smart Meter ID" + id + " with key: " + cryptoKey + "> " + "\nObject being sent:" + o + "<End encryption with " + cryptoKey + ">";
    }
    
    /**
     * timeCheck
     * a method that checks the timestamp on a signature and will return
     * true if the time is within a VALIDTIMEFRAME. 
     */
    private  boolean timeCheck(Date d)
    {   
        Date now = new Date();//the time now
        
        
        Calendar cal = Calendar.getInstance(); //Create a calendar object for the window in the past
        
        cal.setTime(now);
        
        for(int i = 0; i < VALIDTIMEFRAME; i++)
            cal.roll(Calendar.MINUTE, false); //roll back the calendar VALIDTIMEFRAME minutes
        
        Date validWindow = cal.getTime();   
        
        
        if(d.after(validWindow)) //If the action is within VALIDTIMEFRAME minutes 
            return true; //... it is a valid time signature
        else 
            return false;
        
    }
    
    /**
     * getServer
     * Returns the 'server socket'. Used for testing purposes. 
     */
    public  ServerSocket getServer() 
    {
        return socket;
    }
    
    /**
     * setServer
     * allows the server 'socket' to be added. This is necessary only for testing and would be removed
     * in a production system. A production system would probably hardcode a http address. 
     * 
     * However, even with this being open, the security consequences are intended to be little because
     * all server input is considered to be untrusted anyway and will always be verified. 
     */
    public void setServer(ServerSocket s)
    {
        socket = s;
    }
    
    /**
     * setClient
     * Allows the client interface and interaction to be simulated in a similar manner to that of
     * the server. The passing of a referece to itself is only a means to establish a demonstrative 
     * connection. 
     */
    public void setClient(Engine e)
    {
        client = new ClientInterface(e);
    }
    
    /**
     * netUsage
     * A calculation for the client of their total electricity consumption given in dollar figures.
     * Essentially performs a simple calculation of each usage block that has been created 
     * multiplied by the price at that block. 
     */
    public double netUsage()
    {
        double sum = 0; //the running total
        
        for(int i=0; i<usage.size(); i++)
        {
            sum = sum + (usage.get(i).getQty() * usage.get(i).getPrice().getPrice());
        }
        
        return sum;
    }
    
    /* =================================DEBUG METHODS===================================*/
    
        
    
    
    /**
     * numberCommandsDebug
     * A simple method which return the number of commands that have executed since the 
     * creating of the Smart Meter Engine Object. 
     */
    public int numberCommandsDebug()
    {
        return commandExec_Debug;
    }
    
    /**
     * smDebug
     * returns a list of system state variables and returns this as a string.
     * This would not exist on a production system because it exposes
     * confidential information for the purpose of validity testing. 
     * 
     * @param verbose whether or not to include large amounts of information pertaining
     * to production
     */
    public String smDebug(boolean verbose)
    {
        String output = "\n\n=========== Begin Debug Data =================\n";
        
        output = output + currentPrice + "\n";
        output = output + "Current consumption block: " + currentUse  + "\n";
        output = output + "Current Production block: "+ currentProduction  + "\n";
        output = output + "Key: " + cryptoKey  + "\n";
        
        output = output + "\nnet usage: " + netUsage() + "\n";
        
        if(verbose)
        {
            output = output + "\nUsage: \n";
            for(UsageBlock b: usage)
            {
                output = output + b + "\n";
            }
            
            output = output + "\nProduction: \n";
            for(UsageBlock b: production)
            {
                output = output + b + "\n";
            }
            
            output = output + "\nforecast: \n";
            for(UsageBlock b: forecast)
            {
                output = output + b + "\n";
            }
        }
        return output + "===========================================";
        
    }
    
    /**
     * smDebug - Quieter mode
     * starts the debugger in non-verbose mode by default
     */
    public String smDebug()
    {
        return smDebug(false);
    }
    
    /**
     * smDebug_getPrice
     * Returns the current price value, used for debugging purposes only. 
     */
    public double smDebug_getPrice()
    {
        return currentPrice.getPrice();
    }
    
    
}
