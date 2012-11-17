package SmartMeter;

import Server.*;
import java.util.*;
import java.io.*;

public class Temp {



    public static void main (String [] args)
    {
        ServerSocket s = new ServerSocket();
        s.validCommands();
        
        Engine e = new Engine (s, "###SecretKEY###", true);
        e.command();
        System.out.println(e.netUsage());
        System.out.println(e.smDebug());
    }
}
