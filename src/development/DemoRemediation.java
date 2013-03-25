package development;
import rxtxrobot.*;
import java.util.*;

public class DemoRemediation
{
	private RXTXRobot r;
    private Course course;
    private String port;
	private int turbidity;
	private double temperature;
	private double pH;
    private boolean phIsNeatural
	
	public DemoRemediation(Course c)
	{
		r = new RXTXRobot();
        System.out.println("Enter connection port: ");
        Scanner s = new Scanner(System in);
        this.port = s.nextLine();
		r.setPort(this.port);
		
		this.course = c;

		this.turbidity = 0;
		this.temperature = 0.0;
		this.pH = 0.0;
        
        this.phIsNeatural = false;
	}
	
	// Test the water for values of turbidity, temperature, and pH
	public void test()
	{
        r.connect();
	    String challenge = course.getChallenge();
	   
	    moveArm(challenge);
	    
	    this.turbidity = measureTurbidity();
	    this.temperature = measureTemp();
	    this.pH = measurePH();
        if (this.pH >= 7.0 && this.pH <= 7.5)
            this.phIsNeatural = true;
	    
	    System.out.println("The turbidity is " + turbidity + ".");
	    System.out.println("The temperature is " + temperature + " degrees Celsius.");
	    System.out.println("The pH is " + pH + ".");
//      A test will normally call the remediate method, but for the sake of this
//      demo we will not call the remediate method since each one much be done
//      statically
        
//	    remediate(this.pH);
        r.close();
	
	}
	
	// Adds neutralizing solution to water until pH becomes neutral
	public void remediate(double pH)
	{
        this.pH = pH;
        r.connect()
		int mixSpeed = 255; // Max
		
		while(!phIsNeatural)
		{
            r.sleep(500);
			// Dose water
			r.setMixerSpeed(mixSpeed);
			r.runMixer(r.MOTOR3, 500);
            r.stopMixer(r.MOTOR3);
		    
		    pH = measurePH();
		}
        r.close();
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
		double E = 0; // E(0) - E -- this is the value we read in
		double K = (1.985/96485.339924)*2.30;
		
		return E/(K*this.temperature);
	}
	
	// Returns String with sensor move location
	private void moveArmDown(String challenge)
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