import rxtxrobot.RXTXRobot;

/*
 * need to implement the following:
 * String moveSensor(int caseNum); returns String with sensor move location
 * int measureTurbidity(); //returns turbidity in NTU
 * double measurepH(int pH);
 * double measureTempInC();
 */

public class DemoRemediation extends Course
{
	RXTXRobot r;
	
	public DemoRemediation()
	{
		r = new RXTXRobot();
		r.setPort("/dev/tty.usbmodem1a1221");
	}
	
	boolean testNRemediate()
	{
	    boolean fail = false;
	    int mixerSpeed = 255;
	    int numReRun = 3;
	    //max speed is 255
	    
	    String challenge = getChallenge();
	    
	    int case1 = 1;
	    int case2 = 2;
	    int case3 = 3;
	    
	    if (challenge.equals("Below Ground"))
	        moveSensor(case1);
	    else if (challenge.equals("Above Ground"))
	        moveSensor(case3);
	    else if (challenge.equals("Ground Level"))
	        moveSensor(case2);
	    else
	        return fail;
	    
	    //create Scanner object in case
	    //need to type in given starting pH
	    double temperature = measureTempInC();
	    double pH = measurepH();
	    double doseNeeded = calculateDose(pH);
	    
	    r.setMixerSpeed(mixerSpeed);//from RXTXRobot class
	    //DETERMINE WHEN TO STOP MIXER
	    stopMixer();
	    
	    double pHFinal = measurepH(); //should be in 7-7.5 range
	    double[] pHs = new double[numReRun];
	    
	    for (int rerun = 0; rerun < numReRun; rerun++)
	        pHs[rerun] = measurepH();
	    
	    return true;
	}

	// Returns the temperature of a liquid in Celsius
	double measureTempInC()
	{
		return 0.0;
	}
	
	// Returns the pH of a liquid
	double measurepH()
	{
		return 0.0;
	}
	
	// Returns turbidity in NTU
	int measureTurbidity()
	{
		return 0;
	}
	
	// Calculates the amount of remediating liquid necessary
	double calculateDose(double d)
	{
		return 0.0;
	}
	
	// Determines when to stop the mixer
	void stopMixer()
	{
		
	}
	
	// Returns String with sensor move location
	String moveSensor(int caseNum)
	{
		return "";
	}
	
	public String toString()
	{
		return "";
	}
}