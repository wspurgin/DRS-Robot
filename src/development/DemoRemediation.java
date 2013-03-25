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
		//the current values are rough calculations as the exact measurements of the robot are not known.
		
		//BECAUSE we cannot measure the height of the water with the robot during its independent
		//naviagtion, we must hard code the different angles for each case. therefore, any numbers changed here, change it
		//in the method below too (the method to removeSensors). just remember to change both values. note that the 
		//removeSensors method is the reverse order of these current algorithm steps. 
		String moveSensor(int caseNum)
		{// second method to remove the sensors
			
			//bellow ground, robot needs to be about 29.2 cm away from the center of the water.
			if(caseNum == 1)
			{
				//these forloops are to get the arm in the correct general location, the closer
				//it gets to the water, the more slowly the arm will move, to help avoid accidents
				for(int i = 11; i>0; i--)
				{
				r.moveBothServos(0, i);//move second arm into correct location, 66 degrees
				r.sleep(200);
				}
				for(int i = 19; i>0; i--) 
				{
				r.moveBothServos(i, 0); // moves main arm directly over the water, 155 degrees
				r.sleep(200);
				}
				
			}
			
			//ground.  since the water is at ground level. there shouldnt be a need to take 
			//multiple steps to angle the sensor perpendicularly over. However, the robot 
			//needs to be 29.28 cm from the water in order for this to work.
			if(caseNum == 2)
			{
				//these forloops are to get the arm in the correct general location, the closer
				//it gets to the water, the more slowly the arm will move, to help avoid accidents
				for(int i = 14; i>0; i--)
				{
				r.moveBothServos(0, i);//104
				r.sleep(200);
				}
				for(int i = 13; i>0; i--) 
				{
				r.moveBothServos(i, 0); // moves main arm directly over the water, 91 degrees
				r.sleep(200);
				}
			}
			
			
			//above ground
			if(caseNum == 3)
			{
				//these forloops are to get the arm in the correct general location, the closer
				//it gets to the water, the more slowly the arm will move, to help avoid accidents
				for(int i = 11; i>0; i--)
				{
				r.moveBothServos(0, i);//move second arm into extended position, 66 degrees (plus the 25 the arm is already at)
				r.sleep(200);
				}
				for(int i = 11; i>0; i--) 
				{
				r.moveBothServos(i, 0); // moves main arm directly over the water, 66 degrees
				r.sleep(200);
				}
				for(int i = 10; i>0; i--)
				{
				r.moveBothServos(0, -i);// -55 degrees, will angle the sensor over the water.
				r.sleep(200);
				}
				for(int i = 3; i>0; i--) 
				{
				r.moveBothServos(i, 0); // very slight movement to put the sensor into the water
				r.sleep(200);
				}
				
			}
				
			return "Sensors deployed";
		}
		
		String removeSensor(int caseNum)
		{// reverse of the moveSensor method
			
		
			if(caseNum == 1)
			{
				//reverse steps to remove the sensors. 
				for(int i = 19; i>0; i--) 
				{
				r.moveBothServos(-i, 0); 
				r.sleep(200);
				}
				for(int i = 11; i>0; i--)
				{
				r.moveBothServos(0, -i);
				r.sleep(200);
				}
				
				
			}
			
			
			if(caseNum == 2)
			{
				
				for(int i = 13; i>0; i--) 
				{
				r.moveBothServos(-i, 0); 
				r.sleep(200);
				}
				for(int i = 14; i>0; i--)
				{
				r.moveBothServos(0, -i);
				r.sleep(200);
				}
				
			}
			
			
			//above ground
			if(caseNum == 3)
			{
				
				for(int i = 3; i>0; i--) 
				{
				r.moveBothServos(-i, 0); 
				r.sleep(200);
				}
				for(int i = 10; i>0; i--)
				{
				r.moveBothServos(0, i);
				r.sleep(200);
				}
				for(int i = 11; i>0; i--) 
				{
				r.moveBothServos(-i, 0); 
				r.sleep(200);
				}
				for(int i = 11; i>0; i--)
				{
				r.moveBothServos(0, -i);
				r.sleep(200);
				}
				
				
				
				
			}
				
			return "Sensors retracted";
		}
}