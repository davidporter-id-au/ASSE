package SmartMeter;


/**
 * Crypto
 * A class created for the purposes of handling cryptography. 
 * 
 * The library itself is only demonstrative, it does not implement actual 
 * cryptographic functions. However, the intention would be to use appropriate 
 * cryptographic providers from the javax.crypto library. 
 * 
 * @author David Porter
 * @version 2
 */
public class Crypto
{
    public Crypto()
    {
        
    }
    
    /**
     * genPublicKey
     * A demonstrative public key. 
     */
    public static String genPublicKey()
    {
        //Use cryptographic generation algorithm here:
        
        return "Public key: " + Math.random();
    }
    
    
    /**
     * genPrivateKey
     * A demonstrative public key. 
     */
    public static String genPrivateKey()
    {
        //Use cryptographic generation algorithm here:
        
        return "Private key: " + Math.random();
    }
    
    
    /**
     * genSymmetricKey
     * A demonstration method for the symmetric key. 
     */
    public static String genSymmetricKey()
    {
        //Use cryptographic generation algorithm here:
        
        return "Symmetric key: " + Math.random();
    }
    
}
