package SmartMeter;
/**
 * InvalidSignature
 * An exception class that is used to signify that the signature on the file
 * does not match the data given. 
 */
public class InvalidSignature extends Exception
{
    public InvalidSignature(String s)
    {
         System.out.println(s);
    }
}
