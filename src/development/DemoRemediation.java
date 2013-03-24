import rxtxrobot.*;

public class DemoRemediation
{
	RXTXRobot r;
	Course course;
	
	public DemoRemediation(Course c)
	{
		r = new RXTXRobot();
		r.setPort("/dev/tty.usbmodem1a1221");
		
		course = c;
	}
	
	// Test the water for values of turbidity, temperature, and pH
	public boolean test()
	{
	    String challenge = course.getChallenge();
	   
	    moveArm(challenge);
	    
	    int turbidity = measureTurbidity();
	    double temperature = measureTemp();
	    double pH = measurepH();
	    
	    System.out.println("Turbidity: " + turbidity);
	    System.out.println("Temperature: " + temperature + " degrees Celsius");
	    System.out.println("pH: " + pH);
	    
		remediate(pH);
	    
	    return true;
	}
	
	// Adds neutralizing solution to water until pH becomes neutral
	public boolean remediate(double pH)
	{
		int mixSpeed = 255; // Max
		double doseNeeded = 0.0;
		
		while(pH < 6.8 || pH > 7.2)
		{
			doseNeeded = calculateDose(pH);
			
			// Dose water
			
			r.setMixerSpeed(mixSpeed);
			r.runMixer(r.MOTOR3, 500);
		    
		    pH = measurepH();
		}
		
	    return true;
	}

	// Returns the temperature of a liquid in Celsius
	private double measureTemp()
	{
		return 0.0;
	}
	
	// Returns the pH of a liquid
	private double measurepH()
	{
		return 0.0;
	}
	
	// Returns turbidity in NTU
	private int measureTurbidity()
	{
		return 0;
	}
	
	// Calculates the amount of remediating liquid necessary
	private double calculateDose(double d)
	{
		return 0.0;
	}
	
	// Returns String with sensor move location
	private void moveArm(String challenge)
	{
		if (challenge.equals("Below Ground"))
		{
			
		}
	    else if (challenge.equals("Above Ground"))
	    {
	    	
	    }
	    else if (challenge.equals("Ground Level"))
	    {
	    	
	    }
	}
}