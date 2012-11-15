package Server;
import java.util.*;
import SmartMeter.*;
/**
 * ServerSocket
 * A simulated set of commands from a server
 */
public class ServerSocket
{
    public Stack commands; //The list of commands that will be sent to the Smart Meter
    public Stack<String> receivedData; //The list of commands that have been sent to the smart meter.
    public Price price;// the actual price block
    
    /**
     * Constructor
     * sets up the socket with a series of simulated commands.
     * 
     * Note that, by default, it sets the price to have a sell amount of 10, and production
     * amount of 100. 
     */
    public ServerSocket(double sellp, double feedp)
    {
        commands = new Stack();
        receivedData = new Stack();
        
       // sellPrice = sellp;
       // feedinPrice = feedp;
        
        //Wrap up information in a price block. Redundant, but good for testing
        PriceSignal p = new PriceSignal("Vendor", 10, 100, new Date());
        
        price = new Price (p, "###Secret KEY###"); //wrap up with signature.
        
    }
    
    /**
     * Constructor - Default values
     */
    public ServerSocket()
    {
        this(10, 100);
    }
    
    /**
     * testCommands
     * Loads a sequence of test commands into the command issue stack
     */
    public void testCommand(String s, Date d)
    {
        //valid Server Commands
        CommandAction cUpdate = new CommandAction("update", "vendor", d);
        CommandAction cGetForecast = new CommandAction("forecast", "vendor", d);
        CommandAction cGetUsage = new CommandAction("usage", "vendor", d);
        CommandAction cGetProduction = new CommandAction("production", "vendor", d);
        
        //Test a bunch of actions with valid signatures and add them to the stack to dispense for testing purposes later. 
        Command update = new Command(cUpdate, s);
        Command forecast = new Command(cGetForecast, s);
        Command usage = new Command(cGetUsage, s);
        Command production = new Command(cGetProduction, s);
        
        //Add them to the command stack
        commands.add(update);
        commands.add(update);
        commands.add(update); //Three to demonstrate the effect of usage blocks
        
        
        commands.add(forecast);
        commands.add(usage);
        commands.add(production);
    }
    
    /**
     * testSignature
     * Loads a sequence of invalid test commands into the command issue stack
     * The commands only are invalid insofar as the signature is incorrect
     */
    public void testSignature()
    {
        testCommand("InvalidSig", new Date()); //try invalid string
        testCommand("", new Date()); //try empty string
    }
    
    /**
     * testDateValidity
     * Loads a sequence of commands into the command stack with valid signatures, but with 
     * invalid dates. Typical of a replay attack.
     */
    public void testDateValidity(Date d)
    { 
        testCommand("###Secret KEY###", d); //load valid commands with invalid dates
        testCommand("invalidKey", d); //load invalid commands with invalid dates
    }
    
    /**
     * validCommands
     * Loads a sequence of valid commands into the command stack.
     */
    public void validCommands()
    {
        testCommand("###Secret KEY###", new Date()); //load valid commands with valid dates
    }
    
    /**
     * issueCommands
     * A method which simulates a set of server commands being issued. For simplicity's sake
     * we'll assume they are issued in date order. 
     */
    public Command issueCommands()
    {   
        if(!commands.isEmpty())
        {
            
            return (Command)commands.pop();
            
        }
        else return null;
    }
    

    /**
     * sendPrice
     * A method which packs up all price information nicely and returns it. 
     */
    public Price sendPrice()
    {
        return price;
    }
    
    /**
     * receiveData
     * A simulation of commands being received by the smart-meter for the purpose of testing 
     * the sending of sensitive data.
     * 
     * For ease of use, only strings will be used here. 
     */
    public void receiveData(String data)
    {
        receivedData.add(data);
    }
    
    /**
     * examineReceived
     * 
     * A method which will output the received data
     */
    public String examineReceived()
    {
        String output = "";
        for(int i = 0; i < receivedData.size(); i++)
        {
            output = output + receivedData.get(i);
        }
        return output;
    }
    
    /**
     * setPrice
     * Allows the setting of a specific price for the purposes of testing
     */
    public void setPrice(Price p)
    {
        price = p;
    }
    
    /**
     * addCommand
     * Adds a command to the command stack. Added for the purposes of testing. 
     */
    public void addCommand(Command c)
    {
        commands.add(c);
        
    }
}
