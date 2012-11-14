package Server;
import java.util.*;
/**
 * Command
 * A class representing a single action. May be issued by Vendor or 
 * consumer. May be signed or unsigned. 
 */
public class CommandAction
{
    private String action; //the action required
    private String vendor; //the vendor. Optional
    private Date actionTime; //the time at which the action is to be executed
    
    /**
     * Constructor
     * @param a the action assigned
     * @param v the vendor issuing the action
     * @param at the action time, or the time at which the action is to be executed. 
     */
    public CommandAction(String a, String v, Date at)
    {
        action = a;
        vendor = v;
        actionTime = at;
    }
    
    public String getAction()
    {
        return action;
    }
    
    public String getVendor()
    {
        return vendor;
    }
    
    public Date getActionTime()
    {
        return actionTime;
    }
}
