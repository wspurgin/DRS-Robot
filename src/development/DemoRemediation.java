package development;
import rxtxrobot.*;

public class DemoRemediation
{
	RXTXRobot r;
	Course course;
	int turbidity;
	double temperature;
	double pH;
	
	public DemoRemediation(Course c)
	{
		r = new RXTXRobot();
		r.setPort("/dev/tty.usbmodem1a1221");
		
		course = c;

		turbidity = 0;
		temperature = 0.0;
		pH = 0.0;
	}
	
	// Test the water for values of turbidity, temperature, and pH
	public boolean test()
	{
	    String challenge = course.getChallenge();
	   
	    moveArmDown(challenge);
	    
	    turbidity = measureTurbidity();
	    temperature = measureTemp();
	    pH = measurePH();
	    
	    System.out.println("The turbidity is " + turbidity + ".");
	    System.out.println("The temperature is " + temperature + " degrees Celsius.");
	    System.out.println("The pH is " + pH + ".");
	    
		remediate();
		
		moveArmUp(challenge);
	    
	    return true;	}
	
	// Adds neutralizing solution to water until pH becomes neutral
	public boolean remediate(double pH)
	{
		int mixSpeed = 255; // Max
		
		while(pH < 6.8 || pH > 7.2)
		{
			
			// Dose water
			
			r.setMixerSpeed(mixSpeed);
			r.runMixer(r.MOTOR3, 500);
		    
		    pH = measurePH();
		}
		
	    return true;
	}

	// Returns the temperature of a liquid in Celsius
	private double measureTemp()
	{
		r.refreshDigitalPins();
		return r.getTemperature();
	}
	
	// Returns turbidity in NTU
	private int measureTurbidity()
	{
		return 0;
	}

	// Returns the pH of a liquid
	private double measurepH()
	{
		return 0.0;
	}
	
	// Returns String with sensor move location
	private boolean moveArmDown(String challenge)
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
	    else
	    	return false;
		
		return true;
	}
	
	private boolean moveArmUp(String challenge)
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
	    else
	    	return false;
		
		return true;
	}
}