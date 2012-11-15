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
        
        Engine e = new Engine (s, "Provider Key");
    }
}
