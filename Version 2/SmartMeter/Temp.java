package SmartMeter;

import Server.*;
import java.util.*;
import Client.*;
import java.io.*;

public class Temp {



    public static void main (String [] args)
    {
        ServerSocket s = new ServerSocket();
        s.validCommands();
        
        Engine e = new Engine (s, "Provider Key", true);
        e.command();
        System.out.println(e.netUsage());
        System.out.println(e.smDebug());
    }
}
