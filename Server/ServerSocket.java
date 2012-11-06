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
    
    /**
     * Constructor
     * sets up the socket with a series of simulated commands
     */
    public ServerSocket()
    {
        commands = new Stack();
        receivedData = new Stack();
        
        //valid Server Commands
        CommandAction cUpdate = new CommandAction("update", "vendor", new Date());
        CommandAction cGetForecast = new CommandAction("forecast", "vendor", new Date());
        CommandAction cGetUsage = new CommandAction("usage", "vendor", new Date());
        CommandAction cGetProduction = new CommandAction("production", "vendor", new Date());
        
        
//Test a bunch of actions with valid signatures and add them to the stack to dispense for testing purposes later. 
        Command update = new Command(cUpdate, "hash");
       Command forecast = new Command(cGetForecast, "hash");
      //  Command usage = new Command(cGetUsage, "hash");
      //  Command production = new Command(cGetProduction, "hash");
    
        commands.add(update);
      commands.add(forecast);
      //  commands.add(usage);
      //  commands.add(production);
        
        
        //Test a bunch of actions with invalid sigatures. 
//         Command updateF = new Command(cUpdate, "hash", false);
//         Command forecastF = new Command(cGetForecast, "hash", false);
//         Command usageF = new Command(cGetUsage, "hash", false);
//         Command productionF = new Command(cGetProduction, "hash", false);
//         
//         commands.add(updateF);
//         commands.add(forecastF);
//         commands.add(usageF);
//         commands.add(productionF);
       
        
        
        
        
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
     */
    public double issuePrice()
    {
        return Math.random()*10; 
    }
    
    /**
     * issueFeedinPrice
     * A method which simulates the feed-in price
     */
    public double issueFeedinPrice()
    {
        return (int)(Math.random()*100);
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
     * receiveCommands
     * A simulation of commands being received by the smart-meter for the purpose of testing 
     * the sending of sensitive data.
     */
    public void receiveCommands(Object o)
    {
        receivedData.add(o);
    }
    
    
    
}
