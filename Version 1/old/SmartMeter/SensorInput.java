    package SmartMeter;
/**
 * SensorInput
 * A class representing the data being added from the devices' hardware sensors
 * as they monitor electricity usage over a given period of time. 
 * Due to concurrency support and the like not being implemented, 
 * it is given in descrete blocks rather than continuously. 
 * 
 * It is assumed that Sensor coding will be implemented separately, and is therefore
 * is not being tested here. All this is intended to perform is a simulation. 
 */

public class SensorInput{

/**
 * Constructor
 */
public SensorInput()
{
}


/**
 * issue
 * Provides a set of data as a simulation of the sensor input coming from the devices hardware
 * meters. The intention is that this amount represents a unit measure, rather than a 
 * flow measure as one might expect. IE, KWH, rather than KW. This is for the purpose
 * of simplification. 
 */
public double issue()
{
    return Math.random()*100;
}

}
