package Server;
import java.util.*;
import SmartMeter.*;
/**
 * Command
 * A class represenging a signed command from either the provider 
 * or the consumer. Wraps the command in a signiture to enable verification. 
 */
public class Command 
{
    private CommandAction commandAction; //the action to be executed
    private String signature; //The signature of the server verifying the command is valid. 
    private boolean validDate; //A boolean representing the validity of the date signing. used to prevent replay attacks
    
    /**
     * Constructor
     * @param ca The command action assigned to the command, the bit that says what is 
     * going to be executed. 
     * @param sig the signature or hash of the command action. 
     * @param d The 'validity' of the date being added. 
     */
    public Command(CommandAction ca, String sig, boolean d)
    {
        commandAction = ca;
        signature = sig;
        validDate = d;
    }
    
    /**
     * getAction
     * provides the action given in the command
     */
    public String getAction()
    {
        return commandAction.getAction();
    }
    
    /**
     * getActionTime
     * Returns the date/time of the execution to be carried out. 
     */
    public Date getActionTime() 
    {
        return commandAction.getActionTime();   
    }
    
    /**
     * getVendor
     * Returns the signed vendor who issued the command
     */
    public String getVendor() throws InvalidSignature
    {
        return commandAction.getVendor();
    }
    
    /**
     * sigCheck
     * A dummy method signifying the check of the command action as being 
     * Hashed by the corresponding cryptographic key. 
     */
    public String getSig()
    {
        return signature; //skips the signature checking for testing purposes
    }
    

}

