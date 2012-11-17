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
    
    public static void main (String [] args)
    {
        
        
        System.out.println("Smart Meter - test suite\n");
        
        boolean testSuite1, testSuite2, testSuite3, testSuite4, testSuite5, testSuite6;
        
        boolean v = false; //verbose mode? 
        
        testSuite1 = functionalityTest(v);
        testSuite2 = commandSignatureTest(v);
        testSuite3 = test_replayAttacks(v);
        testSuite4 = test_dataRetention(v);
        testSuite5 = test_priceSignature(v);
        testSuite6 = test_priceDateCheck(v);

        System.out.println("\nResults:\n");
        
        System.out.println("Test Suite 1: - functionality \t\t" + testSuite1);
        System.out.println("Test Suite 2: - command signatures\t" + testSuite2);
        System.out.println("Test Suite 3: - command date checks\t" + testSuite3);
        System.out.println("Test Suite 4: - data retention \t\t" + testSuite4);
        System.out.println("Test Suite 5: - price signatures \t" + testSuite5);
        System.out.println("Test Suite 6: - price date check \t" + testSuite6);

        
        System.out.println("\n\nManual Tests: \n\n");
        
        testSendEnc();
        
    }
    
    
    
    /**
     * functionaility test
     * Should create a server undergo a set of normal tests
     */
    public static boolean functionalityTest(boolean verbose)
    {
        boolean pass = true;
        
        CommandAction cUpdate = new CommandAction("update", "vendor", new Date());
        CommandAction cGetForecast = new CommandAction("forecast", "vendor", new Date());
        CommandAction cGetUsage = new CommandAction("usage", "vendor", new Date());
        CommandAction cClear = new CommandAction("clear", "vendor", new Date());

        Command update = new Command(cUpdate, "###SecretKEY###");
        Command forecast = new Command(cGetForecast, "###SecretKEY###");
        Command usage = new Command(cGetUsage, "###SecretKEY###");
        Command clear = new Command(cClear, "###SecretKEY###");
        
        Stack commands = new Stack();
        
        commands.add(update);
        commands.add(forecast);
        commands.add(usage);
        commands.add(clear);
        
        // all commands have been created, are valid and should be executed. 
        
        
        ServerSocket s = new ServerSocket();
        s.setStack(commands); //give the server the commands
        
        
        Engine e = new Engine(s, "###SecretKEY###");

        e.command();//execute
        
        SmartMeterClient sm = e.getClientInterface();
        
        if(verbose)
        {
            /*      TEST 1.1 - Test of basic command execution
            */
            System.out.println("Test 1.1: Pass? " + (e.numberCommandsDebug() == 4));
            
            /*      Test 1.2 - Test of correct handling of price information
            */
            System.out.println("Test 1.2: Pass? " + (sm.getPrice() == 10));
            
            /*      Test 1.3 - Test of correct handling of quantity and usage information
            */
            System.out.println("Test 1.3: Pass? " + (sm.netUsage() == 15000));
        }
        
        
        if(verbose)
        {
            
            examineSM(e);
        
        }
        pass = pass && e.numberCommandsDebug() == 4; //Test suite result
        
        
        //further extension in functionality testing if required
        
        if(verbose)
        System.out.println("All tests pass? " + pass);
        return pass;
        
    }
    
    /**
     * commandSignatureTest
     * The purpose of the the test is to determine if the Smart meter will correctly
     * handle being given a number of commands with invalid signatures. 
     * 
     * Expected output: Failure to execute all commands. 
     * 
     * All data should remain as initially declared, at default values. 
     */
    public static boolean commandSignatureTest(boolean verbose)
    {
        CommandAction cUpdate = new CommandAction("update", "vendor", new Date());
        CommandAction cGetForecast = new CommandAction("forecast", "vendor", new Date());
        CommandAction cGetUsage = new CommandAction("usage", "vendor", new Date());
        CommandAction cClear = new CommandAction("clear", "vendor", new Date());

        Command update = new Command(cUpdate, "invalidKey");
        Command forecast = new Command(cGetForecast, "invalidKey");
        Command usage = new Command(cGetUsage, "invalidKey");
        Command clear = new Command(cClear, "invalidKey");
        
        Stack commands = new Stack();
        
        commands.add(update);
        commands.add(forecast);
        commands.add(usage);
        commands.add(clear);
        
        // all commands have been created, are valid and should be executed. 
        
        
        ServerSocket s = new ServerSocket();
        s.setStack(commands); //give the server the commands


        if(verbose)
            System.out.println("\nStart hostile server\n");
        
        Engine engine = new Engine(s, "###SecretKEY###"); //Start up a new instance of the smartmeter for testing 
     
        
        engine.command();//execute commands
      
        

        engine.setServer(s);
        engine.command(); //attempt to execute
        
        if(verbose)
            System.out.println("\nResults for tests 2.1, 2.2, 2.3, 2.4:");
        
        if(engine.numberCommandsDebug() == 0)
        {
            if(verbose)
                System.out.println("\nPassed Tests");
            return true;
        }
        else 
        {
            if(verbose)
            {
                System.out.println("\nTests Failed");
                
                examineSM(engine); //examine results
            }
            
            return false;
        }
        

        
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
    public static boolean test_replayAttacks(boolean verbose)
    {
        if(verbose)
        {
            String screen = "replayAttackTest\n";
            screen = screen +  "Provides a set of commands to the smartmeter which are signed correctly,\n";
            screen = screen +  "but have invalid dates. This is a typical scenario when \n";
            screen = screen +  "a hostile actor has captured valid commands and resends them to the\n ";
            screen = screen +  "application later. \n";
              
            screen = screen +  "\nOracle: Should be no change in system variables as the (simulated) date stamp";
            screen = screen +  "should fail to authenticate on the second set of commands. \n";
            
            System.out.println(screen);
        }
            
        //ServerSocket normalServer = new ServerSocket();
        ServerSocket hostileServer = new ServerSocket();
        
        //Set up a bad time signature
        Date badDate = new Date();// get the current time
        Calendar c = Calendar.getInstance();
        c.setTime(badDate);
        c.roll(Calendar.MONTH, false); //roll back to previous month. 
        badDate = c.getTime(); //reassign now that a bad-date has been created. 
        
        //Load up their comands
        hostileServer.testDateValidity(badDate);
        
        //Try adding hostile variables and test effect
        Engine e = new Engine(hostileServer, "###SecretKEY###");
        
        e.command();
        
        //examineSM(e); //output variables after change
        
        if(e.numberCommandsDebug() == 0)
        {
            if(verbose)
                System.out.println("\nPassed Tests");
            return true;
        }
        else 
        {
            if(verbose)
            {
                System.out.println("\nTests Failed");
                
                examineSM(e); //examine results
            }
            
            return false;
        }
        
    }
    
    /**
     * test_dataRetention
     * A test determined to see if the smart meter will handle loss of power
     * or hard-reboot without dataloss
     * 
     * Oracle: Smart Meter shows valid usage data after being recreated.
     */
    public static boolean test_dataRetention(boolean verbose)
    {   
        if(verbose)
        {
            System.out.println("Data Retention Test");
            System.out.println("A test determined to see if the smart meter will handle loss of power");
            
            System.out.println("Oracle: Smart Meter shows valid usage data after being recreated.");        
        }
        
        
        
        CommandAction cUpdate = new CommandAction("update", "vendor", new Date());
        CommandAction cGetForecast = new CommandAction("forecast", "vendor", new Date());
        CommandAction cGetUsage = new CommandAction("usage", "vendor", new Date());
        CommandAction cClear = new CommandAction("clear", "vendor", new Date());

        Command update = new Command(cUpdate, "###SecretKEY###");
        Command forecast = new Command(cGetForecast, "###SecretKEY###");
        Command usage = new Command(cGetUsage, "###SecretKEY###");

        
        Stack commands = new Stack();
        
        commands.add(update);
        commands.add(forecast);
        commands.add(usage);

        
        // all commands have been created, are valid and should be executed. 
        
        
        ServerSocket s = new ServerSocket();
        s.setStack(commands); //give the server the commands
        
        Engine e = new Engine(s, "###SecretKEY###");
        
        e.command();
        
        e = null; //remove engine
        
        e = new Engine(s, "###SecretKEY###"); //and reload
        
        SmartMeterClient sm = e.getClientInterface();
        
        //The meter should show that the is a quantity of use after being restarted. 
        if(sm.netUsage() > 0)
        {
            if(verbose)
                System.out.println("\nPassed Test");
            return true;
        }
        else 
        {
            if(verbose)
            {
                System.out.println("\nTest Failed");
                
                examineSM(e); //examine results
            }
            return false;
        }
        
    }
    

    /**
     * test_priceSignature
     * A test to determine the correctness of the price signature/key handling
     * mechanism.
     * 
     * Oracle: No commands should be processed from the hostile server. No 
     * Price changes should be effected to the SmartMeter. Price should remain at 10, 100
     */
    public static boolean test_priceSignature(boolean verbose)
    {
        if(verbose)
        {
            System.out.println("Price Signature test");
            System.out.println("A test to determine the correctness of the price signature/key handling mechanism.");
            System.out.println("\nOracle: No price changes should occur, price should remain at 10, 100.");
            System.out.println("\nAnd not change to 5, 50");
        }
        
        PriceSignal priceSignal = new PriceSignal("vendor", 10, 100, new Date());
        PriceSignal invalidPriceSignal = new PriceSignal("vendor", 5, 50, new Date());
        
        Price validPrice = new Price(priceSignal, "###SecretKEY###"); //Create a valid price with correct key
        Price invalidPrice = new Price (invalidPriceSignal, "Wrong key");
        
        CommandAction cUpdate = new CommandAction("update", "vendor", new Date());
        
        Command updatePrice = new Command(cUpdate, "###SecretKEY###"); //prepare to send a valid price update command
        
        
        //run initial correct price update
        
        ServerSocket s = new ServerSocket();
        
        s.addCommand(updatePrice);
        s.setPrice(validPrice);
        
        Engine e = new Engine(s, "###SecretKEY###");
       
        e.command(); //execute initial, correct command
        
        //examineSM(e); //examine results
        
        if(verbose)
            System.out.println("\nHostile Price changes: ");
        
        //Now test invalid signature in price
        s.setPrice(invalidPrice);
        s.addCommand(updatePrice);
        
        e.command(); //execute update on invalid price
        //examineSM(e); //examine results
        
        SmartMeterClient sm = e.getClientInterface();
        
        if(sm.getPrice()==10)
        {
            if(verbose)
                System.out.println("\nPassed Test");
            return true;
        }
        else 
        {
            if(verbose)
            {
                System.out.println("\nTest Failed");
                
                examineSM(e); //examine results
            
            }
            return false;
        }
        
        
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
    public static boolean test_priceDateCheck(boolean verbose)
    {
        if(verbose)
        {
            System.out.println("Price Date Check Test:");
            System.out.println("Tests the date checking feature of the price update process. Designed");
            System.out.println("to simulate a replay attack, where the date packet has been captured");
            System.out.println("and replayed at a later date. ");
            System.out.println("\nOracle: Date should not be affected from the original. Price should");
            System.out.println("remain at 10, 100.");
        }
        
        //Create a bad date
        Date badDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(badDate);
        c.roll(Calendar.MONTH, false); //roll back to previous month. 
        badDate = c.getTime(); //reassign now that a bad-date has been created. 
              
        
        PriceSignal priceSignal = new PriceSignal("vendor", 10, 100, new Date());
        PriceSignal invalidPriceSignal = new PriceSignal("vendor", 5, 50, badDate);
        
        Price validPrice = new Price(priceSignal, "###SecretKEY###"); //Create a valid price with correct key
        Price invalidPrice = new Price (invalidPriceSignal, "###SecretKEY###");
        
        CommandAction cUpdate = new CommandAction("update", "vendor", new Date());
        Command updatePrice = new Command(cUpdate, "###SecretKEY###"); //prepare to send a valid price update command
        
        
        //run initial correct price update
        
        ServerSocket s = new ServerSocket();
        
        s.addCommand(updatePrice);
        s.setPrice(validPrice);
        
        Engine e = new Engine(s, "###SecretKEY###");
       
        e.command(); //execute initial, correct command
 
        //examineSM(e); //examine results
        
        
        //Now test invalid date in price
        s.setPrice(invalidPrice);
        s.addCommand(updatePrice);
        
        e.command(); //execute update on invalid price
        SmartMeterClient sm = e.getClientInterface();
        
        if(sm.getPrice()==10)
        {
            if(verbose)
                System.out.println("\nPassed Test");
            return true;
        }
        else 
        {
            if(verbose)
            {
                System.out.println("\nTest Failed");
                
                examineSM(e); //examine results
            }
            
            return false;
        }
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
    public static void testSendEnc()
    {
        System.out.println("Send Encryption Test");
        System.out.println("A method which observes all data received by the server to demonstrate if it has been ");
        System.out.println("through the appropriate encryption simulation methods in the SmartMeter");        
        System.out.println("\nOracle: All data should indicate that encryption 'has' (in a simulated sense) occured.");

        
        ServerSocket s = new ServerSocket();
        
        s.validCommands();
        
        Engine e = new Engine(s, "###SecretKEY###");
        
        e.command();//execute commands
        
        System.out.println(s.examineReceived());
        
    }
    
    
    /**
     * AccessTest
     * Testing access to private methods and variables
     * 
     * Oracle: No methods or variables should be allowed at compile-time. 
     */
    public static void Test3_PrivateMethodsAndVariables()
    {
        
       //none of the following should be allowed:
        
       ServerSocket s = new ServerSocket();
       
       Engine engine = new Engine(s,"###SecretKEY###");
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
     * examineSM
     * Displays internal information of the smartmeter through the debug interface
     * @param verbose determines whether to show additional information
     */
    private static void examineSM(Engine e, boolean verbose)
    {
        System.out.println(e.smDebug(verbose));
    }
    
    private static void examineSM(Engine e)
    {
        examineSM(e, false);
    }
    
    /**
     * examineServer
     * Takes a 'Server' set of output and displays it on screen. 

     */
    private static void examineServer(ServerSocket server)
    {        
        Stack s = server.receivedData; //get stack directly from the server
        
        while(!s.isEmpty())
        {
            System.out.println((String)s.pop());
        }
    }
    
    
    
    
}
