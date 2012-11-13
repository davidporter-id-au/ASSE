import SmartMeter.*;
import Server.*;
import java.util.*;
/**
 * An attempt to create external and damanging modificaitons to the SmartMeter class
 * 
 * @author David Porter
 */
public class ExternalTests
{

    
    public ExternalTests()
    {
              
    }
    
    
    
    
    /**
     * signatureTest
     * The purpose of the the test is to determine if the Smart meter will correctly
     * handle being given a number of commands with invalid signatures. 
     * 
     * Expected output: Failure to execute all commands. 
     * 
     * All data should remain as initially declared, at default values. 
     */
    public void signatureTest()
    {
        String screen = "\nsignatureTest \nThe purpose of the the test is to determine if the Smart meter will correctly";
        screen = screen + "\nhandle being given a number of commands with invalid signatures.";
        screen = screen + "\n \nExpected output: Failure to execute all hostile commands. \n\n";
        
        System.out.println(screen);
        System.out.println("Start normal server\n");
        
        ServerSocket normalServer = new ServerSocket();
        ServerSocket hostileServer = new ServerSocket();
        
        normalServer.validCommands(); //Engage a sequence of valid commands
        hostileServer.testSignature(); //Engage a series of hostile and invalid commands
        
        Engine engine = new Engine(normalServer); //Start up a new instance of the smartmeter for testing 
        //and with default values
        
        engine.command();//execute commands
        
        
        examineSM(engine); //examine and display information
        
        System.out.println("\nStart hostile server\n");
        engine.setServer(hostileServer);
        engine.command(); //attempt to execute
        examineSM(engine);//examine
        
    }
    
    /**
     * AccessTest
     * Testing access to private methods and variables
     * 
     * Oracle: No methods or variables should be allowed at compile-time. 
     */
    public void Test3_PrivateMethodsAndVariables()
    {
        
       //none of the following should be allowed:
        
       
//             engine.currentPrice = null; //the present price being used. 
//             engine.currentUse = null;// the current usage block. 
//             engine.currentProduction = null; //the current production block
//             engine.usage = null; //the history of usage as a list of usageBlocks
//             engine.production = null; //the amount of produced power
//             engine.socket = null; //the simulated server socket
//             engine.VALIDTIMEFRAME = 5 = null; //the amount of time a command is active for, in minutes
//             engine.VENDOR = "vendor" = null; //The specific vendor for which this device
//             engine.ensor = null; //the input from the electrical physical reader, indicating usage
//             engine.forecast = null; //the forecast amount of usage as is provided by appliances
//             engine.cryptoKey = null; 

//         engine.addUsage(15);
//         engine.addProduction(15);
//         engine.forecast();
//         engine.usage();
//         engine.clear();
//         engine.setUsage(new Vector());
//         engine.setProduction(new Vector());
//         engine.getCurrentPrice();
//         engine.packup();
//         engine.setCurrentPrice(new Price (new PriceSignal("Vend", 23,23, new Date())));
//         
//         
        


    }
    
    /**
     * replayAttackTest
     * 
     * Provides a set of commands to the smartmeter which are signed correctly,
     * but have invalid dates. This is a typical scenario when 
     * a hostile actor has captured valid commands and resends them to the 
     * application later. 
     * 
     * Oracle: Should be no change in system variables as the (simulated) date stamp
     * should fail to authenticate on the second set of commands. 
     */
    public void test_replayAttacks()
    {
        String screen = "replayAttackTest\n";
            screen = screen +  "Provides a set of commands to the smartmeter which are signed correctly,\n";
            screen = screen +  "but have invalid dates. This is a typical scenario when \n";
            screen = screen +  "a hostile actor has captured valid commands and resends them to the\n ";
            screen = screen +  "application later. \n";
              
            screen = screen +  "\nOracle: Should be no change in system variables as the (simulated) date stamp";
            screen = screen +  "should fail to authenticate on the second set of commands. \n";
        
        System.out.println(screen);
            
            
        ServerSocket normalServer = new ServerSocket();
        ServerSocket hostileServer = new ServerSocket();
        
        //Set up a bad time signature
        Date badDate = new Date();// get the current time
        Calendar c = Calendar.getInstance();
        c.setTime(badDate);
        c.roll(Calendar.MONTH, false); //roll back to previous month. 
        badDate = c.getTime(); //reassign now that a bad-date has been created. 
        
        //Load up their comands
        normalServer.validCommands();
        hostileServer.testDateValidity(badDate);
        
        System.out.println("Normal Server");
        Engine e = new Engine(normalServer); //get baseline

        e.command();
        
        examineSM(e);
        
        System.out.println("Hostile Server");
        
        //Try adding hostile variables and test effect
        e.setServer(hostileServer);
        
        e.command();
        
        examineSM(e); //output variables after change
        
    }
    
    /**
     * test_dataRetention
     * A test determined to see if the smart meter will handle loss of power
     * or hard-reboot without dataloss
     * 
     * Oracle: Smart Meter shows valid usage data after being recreated.
     */
    public void test_dataRetention()
    {   
        System.out.println("Data Retention Test");
        System.out.println("A test determined to see if the smart meter will handle loss of power");
        
        System.out.println("Oracle: Smart Meter shows valid usage data after being recreated.");
        
        
        ServerSocket s = new ServerSocket();
        s.validCommands();
        
        Engine e = new Engine(s);
        
        e.command();
        
        e = null; //remove engine
        
        e = new Engine(s); //and reload
        
        examineSM(e, true);
        
    }
    

    /**
     * test_priceSignature
     * A test to determine the correctness of the price signature/key handling
     * mechanism.
     * 
     * Oracle: No commands should be processed from the hostile server. No 
     * Price changes should be effected to the SmartMeter. Price should remain at 10, 100.
     * 
     */
    public void test_priceSignature()
    {
        System.out.println("Price Signature test");
        System.out.println("A test to determine the correctness of the price signature/key handling mechanism.");
        System.out.println("\nOracle: No price changes should occur, price should remain at 10, 100.");
        System.out.println("\nAnd not change to 5, 50");
        
        PriceSignal priceSignal = new PriceSignal("vendor", 10, 100, new Date());
        PriceSignal invalidPriceSignal = new PriceSignal("vendor", 5, 50, new Date());
        
        Price validPrice = new Price(priceSignal, "###Secret KEY###"); //Create a valid price with correct key
        Price invalidPrice = new Price (invalidPriceSignal, "Wrong key");
        
        CommandAction cUpdate = new CommandAction("update", "vendor", new Date());
        
        Command updatePrice = new Command(cUpdate, "###Secret KEY###"); //prepare to send a valid price update command
        
        
        //run initial correct price update
        
        ServerSocket s = new ServerSocket();
        
        s.addCommand(updatePrice);
        s.setPrice(validPrice);
        
        Engine e = new Engine(s);
       
        e.command(); //execute initial, correct command
        
        examineSM(e); //examine results
        
        System.out.println("\nHostile Price changes: ");
        
        //Now test invalid signature in price
        s.setPrice(invalidPrice);
        s.addCommand(updatePrice);
        
        e.command(); //execute update on invalid price
        examineSM(e); //examine results
        
    }
    
    /**
     * test_priceDateCheck
     * Tests the date checking feature of the price update process. Designed
     * to simulate a replay attack, where the date packet has been captured
     * and replayed at a later date. 
     * 
     * Oracle: Date should not be affected from the original. Price should
     * remain at 10, 100. 
     */
    public void test_priceDateCheck()
    {
        System.out.println("Price Date Check Test:");
        System.out.println("Tests the date checking feature of the price update process. Designed");
        System.out.println("to simulate a replay attack, where the date packet has been captured");
        System.out.println("and replayed at a later date. ");
        System.out.println("\nOracle: Date should not be affected from the original. Price should");
        System.out.println("remain at 10, 100.");
        
        //Create a bad date
        Date badDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(badDate);
        c.roll(Calendar.MONTH, false); //roll back to previous month. 
        badDate = c.getTime(); //reassign now that a bad-date has been created. 
              
        
        PriceSignal priceSignal = new PriceSignal("vendor", 10, 100, new Date());
        PriceSignal invalidPriceSignal = new PriceSignal("vendor", 5, 50, badDate);
        
        Price validPrice = new Price(priceSignal, "###Secret KEY###"); //Create a valid price with correct key
        Price invalidPrice = new Price (invalidPriceSignal, "###Secret KEY###");
        
        CommandAction cUpdate = new CommandAction("update", "vendor", new Date());
        Command updatePrice = new Command(cUpdate, "###Secret KEY###"); //prepare to send a valid price update command
        
        
        //run initial correct price update
        
        ServerSocket s = new ServerSocket();
        
        s.addCommand(updatePrice);
        s.setPrice(validPrice);
        
        Engine e = new Engine(s);
       
        e.command(); //execute initial, correct command
 
        examineSM(e); //examine results
        
        
        //Now test invalid date in price
        s.setPrice(invalidPrice);
        s.addCommand(updatePrice);
        
        e.command(); //execute update on invalid price
        examineSM(e); //examine results
        
    } 
    
    /**
     * testSendEnc
     * 
     * A method which observes all data received by the server to demonstrate if it has been 
     * through the appropriate encryption simulation methods in the 
     * Smart meter. 
     * 
     * Oracle: All data should indicate that encryption 'has' (in a simulated sense) occured. 
     */
    public void testSendEnc()
    {
        System.out.println("Send Encryption Test");
        System.out.println("A method which observes all data received by the server to demonstrate if it has been ");
        System.out.println("through the appropriate encryption simulation methods in the SmartMeter");        
        System.out.println("\nOracle: All data should indicate that encryption 'has' (in a simulated sense) occured.");

        
        ServerSocket s = new ServerSocket();
        
        s.validCommands();
        
        Engine e = new Engine(s);
        
        e.command();//execute commands
        
        System.out.println(s.examineReceived());
        
    }
    
    
    /**
     * examineSM
     * Displays internal information of the smartmeter through the debug interface
     * @param verbose determines whether to show additional information
     */
    private void examineSM(Engine e, boolean verbose)
    {
        System.out.println(e.smDebug(verbose));
    }
    
    private void examineSM(Engine e)
    {
        examineSM(e, false);
    }
    
    /**
     * examineServer
     * Takes a 'Server' set of output and displays it on screen. 

     */
    private void examineServer(ServerSocket server)
    {        
        Stack s = server.receivedData; //get stack directly from the server
        
        while(!s.isEmpty())
        {
            System.out.println((String)s.pop());
        }
    }
    
    
    
    
}
