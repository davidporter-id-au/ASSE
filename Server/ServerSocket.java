package Server;
import java.util.*;
/**
 * ServerSocket
 * A simulated set of commands from a server
 */
public class ServerSocket
{
    public Stack commands; //The list of commands that will be sent to the Smart Meter
    public Stack receivedData; //The list of commands that have been sent to the smart meter.
    public double sellPrice; //the price at which electricity is sold
    public double feedinPrice; //the price at which electricity is bought off consumers
    
    /**
     * Constructor
     * sets up the socket with a series of simulated commands
     */
    public ServerSocket(double sellp, double feedp)
    {
        commands = new Stack();
        receivedData = new Stack();
        
        sellPrice = sellp;
        feedinPrice = feedp;
        
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
    public void testCommand(String s, boolean d)
    {
        //valid Server Commands
        CommandAction cUpdate = new CommandAction("update", "vendor", new Date());
        CommandAction cGetForecast = new CommandAction("forecast", "vendor", new Date());
        CommandAction cGetUsage = new CommandAction("usage", "vendor", new Date());
        CommandAction cGetProduction = new CommandAction("production", "vendor", new Date());
        
        //Test a bunch of actions with valid signatures and add them to the stack to dispense for testing purposes later. 
        Command update = new Command(cUpdate, s, d);
        Command forecast = new Command(cGetForecast, s, d);
        Command usage = new Command(cGetUsage, s, d);
        Command production = new Command(cGetProduction, s, d);
        
        //Add them to the command stack
        commands.add(update);
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
        testCommand("InvalidSig", true); //try invalid string
        testCommand("", true); //try empty string
    }
    
    /**
     * testDateValidity
     * Loads a sequence of commands into the command stack with valid signatures, but with 
     * invalid dates. Typical of a replay attack.
     */
    public void testDateValidity()
    {
        testCommand("###Secret KEY###", false); //load valid commands with invalid dates
        testCommand("invalidKey", false); //load invalid commands with invalid dates
    }
    
    /**
     * validCommands
     * Loads a sequence of valid commands into the command stack.
     */
    public void validCommands()
    {
        testCommand("###Secret KEY###", true); //load valid commands with valid dates
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
     * issuePrice
     * A method which simulates the prices at a given time. 
     * For the purposes of testing, this will always be 10.
     */
    public double issuePrice()
    {
        return sellPrice; 
    }
    
    /**
     * issueFeedinPrice
     * A method which simulates the feed-in price. For the purposes
     * of testing this will always be 100.
     */
    public double issueFeedinPrice()
    {
        return feedinPrice;
    }
    
    /**
     * sendPrice
     * A method which packs up all price information nicely and returns it. 
     */
    public Price sendPrice()
    {
        PriceSignal p = new PriceSignal("Vendor", issuePrice(), issueFeedinPrice(), new Date());
        
        Price pr = new Price (p, "sig", true); //wrap up with signature.
        
        return pr;
    }
    
    /**
     * receiveData
     * A simulation of commands being received by the smart-meter for the purpose of testing 
     * the sending of sensitive data.
     */
    public void receiveData(Object o)
    {
        receivedData.add(o);
    }
    
    
    
}
