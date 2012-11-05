package Server;
import java.util.*;
import SmartMeter.*;
/**
 * Command
 * A class represenging a signed command from either the provider 
 * or the consumer.  Self-verifies the action provided. 
 */
public class Command 
{
    private CommandAction commandAction; //the action to be executed
    private String hash; //the hash of the commandAction to verify authenticity
    private boolean validityTester; //A boolean signifying whether the signature of the object is valid or not
    
    /**
     * Constructor
     * @param ca The command action assigned to the command, the bit that says what is 
     * going to be executed. 
     * @param h the hash of the command action. 
     */
    public Command(CommandAction ca, String h)
    {
        commandAction = ca;
        hash = h;
        validityTester = true;
    }
    
    /**
     * Constructor - set sig
     * The typical constructor with the additional feature of the ability of setting the signature 
     * to either return true or false. 
     */
    public Command(CommandAction ca, String h, boolean validSig)
    {
        this(ca, h);
        validityTester = validSig; //set the 'signature'
    }
    
    /**
     * getAction
     * Verifies the action and provides it. 
     */
    public String getAction() throws InvalidSignature
    {
        if(sigCheck())
            return commandAction.getAction();
        else throw new InvalidSignature();
    }
    
    /**
     * getActionTime
     * Returns the date/time of the execution to be carried out. 
     */
    public Date getActionTime() throws InvalidSignature
    {
        if(sigCheck())
            return commandAction.getActionTime();
        else throw new InvalidSignature();
    }
    
    /**
     * getVendor
     * Returns the signed vendor who issued the command
     */
    public String getVendor() throws InvalidSignature
    {
        if(sigCheck())
            return commandAction.getVendor();
        else throw new InvalidSignature();
    }
    
    /**
     * sigCheck
     * A dummy method signifying the check of the command action as being 
     * Hashed by the corresponding cryptographic key. 
     */
    public boolean sigCheck()
    {
        return validityTester; //skips the signature checking for testing purposes
    }
    

}

