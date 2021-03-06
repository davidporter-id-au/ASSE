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
    private File providerKeyFile; //the provider's public key, used to check the veracity of incoming messages
    
    private File usageData; //The file containin the usage data block
    
    
    /* Constants: Used in file naming convention */
    private final String PUBLICKEY = "publicKey"; //the filename for the public key of the SM
    private final String PRIVATEKEY = "privateKey"; //the filename of the private key of the SM
    private final String PROVIDERKEY = "providerKey"; //the filename for the provider public key
    private final String USAGEDATA = "usage";
    private final String PRODUCTIONDATA = "production";
    
    /**
     * Constructor
     * @param id The id of the smart meter, used to avoid file collisions and identify the meter
     * @param providerKey The 'public' key of the provider, used to send to the provider. 
     */
    public FileHandler(String identifier, String providerKey) throws IOException
    {
        id = identifier;
        initialise(providerKey);
    }

    /**
     * initialise
     * Sets up the files
     * 
     * @throws IOException
     */
    private void initialise(String providerKey) throws IOException
    {
        publicKey = new File(PUBLICKEY + id);
        privateKey = new File(PRIVATEKEY + id);
        providerKeyFile = new File(PROVIDERKEY + id);
                 
        if(!publicKey.exists())
            writeOut(publicKey, Crypto.genPublicKey());
            
        if(!privateKey.exists())
            writeOut(privateKey, Crypto.genPrivateKey());
           
        if(!providerKeyFile.exists())
            writeOut(providerKeyFile, providerKey);
    }
    
    /**
     * getProviderKey
     * Reads the provider key, the public key used to send to the provider and returns 
     * it. 
     */
    protected String getProviderKey() throws IOException
    {
        return readIn(providerKeyFile);
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
    
    /**
     * writeUsage
     * A specific method for writing usage data to a usage file
     */
    protected void writeUsage(AbstractList <UsageBlock> b) throws IOException
    {
        FileOutputStream fos =  new FileOutputStream(USAGEDATA + id);
        ObjectOutputStream out = new ObjectOutputStream(fos);
        
        out.writeObject(b);
        out.close();
    }
    
    /**
     * writeProduction
     * A specific method for writing energy production data to a usage file
     */
    protected void writeProduction(AbstractList <UsageBlock> p) throws IOException
    {
        FileOutputStream fos = new FileOutputStream(PRODUCTIONDATA + id);
        ObjectOutputStream out = new ObjectOutputStream(fos);
            
        out.writeObject(p);
        out.close();
    }
    
    /**
     * getUsage
     * gets, if it exists, the existing usage.
     */
    protected AbstractList <UsageBlock> getUsage() throws IOException, ClassNotFoundException
    {
        File usage = new File(USAGEDATA + id);
        
        if(usage.exists())
        {
            FileInputStream fis = new FileInputStream(USAGEDATA + id);
            ObjectInputStream in = new ObjectInputStream (fis);
            
            AbstractList <UsageBlock> output = (AbstractList <UsageBlock>) in.readObject();
            
            in.close();
            return output;
        }
        else return null;
    }
    
    /**
     * getProduction
     * gets, if it exists, the existing production.
     */
    protected AbstractList <UsageBlock> getProduction() throws IOException, ClassNotFoundException
    {
        File production = new File(PRODUCTIONDATA + id);
        
        if(production.exists())
        {
            FileInputStream fis = new FileInputStream(PRODUCTIONDATA + id);
            ObjectInputStream in = new ObjectInputStream (fis);
            
            AbstractList <UsageBlock> output = (AbstractList <UsageBlock>) in.readObject();
            
            in.close();
            return output;
        }
        else return null;
    }

}
