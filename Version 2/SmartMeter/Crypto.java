package SmartMeter;


/**
 * Crypto
 * A class created for the purposes of handling cryptography. 
 * 
 * @author David Porter
 * @version 2
 */
public class Crypto
{
    public Crypto()
    {
        
    }
    
    public static String genPublicKey()
    {
        //Use cryptographic generation algorithm here:
        
        return "key: " + Math.random();
    }
}
