package SmartMeter;
import java.io.*;
import java.util.*;
/**
 * FileHandler
 */
public class FileHandler 
{
    private String id; //the ID of the meter, used to generate unique file names. 
    private File publicKey; //The public key used by the Smart MEter. 
    private File privateKey; //The private key used by the smart Meter
    private File providerKey; //the provider's public key, used to check the veracity of incoming messages
    
    private File usageData; //The file containin the usage data block
    
    
    /* Constants: Used in file naming convention */
    private final String PUBLICKEY = "public"; //the filename for the public key of the SM
    private final String PRIVATEKEY = "private"; //the filename of the private key of the SM
    private final String PROVIDERKEY= "provider"; //the filename for the provider public key
    
    
    /**
     * Constructor
     * @param id The id of the smart meter, used to avoid file collisions and identify the meter
     * @param providerKey The 'public' key of the provider, used to send to the provider. 
     */
    public FileHandler(String id, String providerKey) throws IOException
    {
        
        
        
    }

    /**
     * initialise
     * Sets up the files
     * 
     * @throws IOException
     */
    private void initialise(String ProviderKey) throws IOException
    {
        publicKey = new File(PUBLICKEY + id);
        privateKey = new File(PRIVATEKEY + id);
        providerKey = new File(PROVIDERKEY + id);
                 
        if(!publicKey.exists())
            writeOut(publicKey, Crypto.genPublicKey());
            
        if(!privateKey.exists())
            writeOut(privateKey, Crypto.genPrivateKey());
           
        if(!providerKey.exists())
            writeOut(providerKey, ProviderKey);
    }
    
    /**
     * getProviderKey
     * Reads the provider key, the public key used to send to the provider and returns 
     * it. 
     */
    protected String getProviderKey() throws IOException
    {
        return readIn(providerKey);
    }
    
    /**
     * getPublicKey
     * Reads the public key of the smart meter, used to send to the provider and returns 
     * it. 
     */
    protected String getPublicKey() throws IOException
    {
        return readIn(publicKey);
    }
    
    /**
     * getPrivateKey
     * Reads the private key of the smart meter and returns it.
     */
    protected String getPrivateKey() throws IOException
    {
        return readIn(privateKey);
    }
    
    /**
     * readIn
     * Reads a file in and returns the contents as an unbroken String
     */
    private String readIn(File in) throws IOException
    {
        Scanner scan = new Scanner(in);
        
        String output = "";
        while(scan.hasNext())
            output = output + scan.next();
            
        return output;
    }
    
    /**
     * writeOut
     * writes out the specified stream to a given file. Simulated here as a String. 
     */
    private void writeOut(File f, String stream) throws IOException
    {
        FileOutputStream out = new FileOutputStream (f);
        out.write(stream.getBytes());
        out.close();
    }
    

}
