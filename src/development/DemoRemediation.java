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
	private boolean phIsNeatural;
	
	// Constructor
	public DemoRemediation()
	{
		// Initialize robot
		r = new RXTXRobot();
		r.setResetOnClose(false);
		
		// Establish port
        System.out.println("Enter connection port: ");
        Scanner s = new Scanner(System.in);
        this.port = s.nextLine();
		r.setPort(this.port);
		r.connect();
		r.moveBothServos(145, 145);
		
		// Set course
		System.out.println("Enter Course number: ");
		int number = s.nextInt();
		switch(number)
		{
			case 1:
				course = new LiberiaCourse();
				break;
			case 2:
				course = new KenyaCourse();
				break;
			case 3:
				course = new DjiboutiCourse();
				break;
			default:
				course = null;
				break;
		}
		this.turbidity = 0;
		this.temperature = 0.0;
		this.pH = 0.0;
        
        	this.phIsNeatural = false;
	}
	
	// Test the water for values of turbidity, temperature, and pH
	public void test()
	{   
	    moveSensor();
	    // Measure turbidity, temperature, and pH
	    this.turbidity = measureTurbidity();
	    this.temperature = measureTemp();
	    this.pH = measurePH();
	    
	    // Determine if pH is neutral
        if (this.pH >= 7.0 && this.pH <= 7.5)
            this.phIsNeatural = true;
	    
        // Prints out turbidity, temperature, and pH to the screen
	    System.out.println("The turbidity is " + turbidity + ".");
	    System.out.println("The temperature is " + temperature + " degrees Celsius.");
	    System.out.println("The pH is " + pH + ".");
	    
	    // A test will normally call the remediate method, but for the sake of this
	    // demo we will not call the remediate method since each one much be done statically
	    // ^^ What test?
	    remediate(this.pH);
	}
	
	// Adds neutralizing solution to water until pH becomes neutral
	public void remediate(double pH)
	{
        this.pH = pH;
		int mixSpeed = 255; // Max
		
		// Loops while the pH is not neutral
		while(!phIsNeatural)
		{
            r.sleep(500);
			// Dose water
			r.setMixerSpeed(mixSpeed);
			r.runMixer(RXTXRobot.MOTOR3, 500);
            r.stopMixer(RXTXRobot.MOTOR3);
		    
		    pH = measurePH();
		}
	}

	// Returns the temperature of a liquid in Celsius
	public int measureTemp()
	{
		int perm = 0;
		int count = 0;
		while(true)
		{
			if(r.getTemperature() == perm)
			{
				count++;
				if(count == 10)
					break;
			}
			else
			{
				perm = r.getTemperature();
				count = 0;
			}
			r.sleep(500);
		}
		return perm;
	}
	
	// Returns turbidity in NTU
	public int measureTurbidity()
	{
		return 0;
	}

	// Returns the pH of a liquid
	public double measurePH()
	{
		double E = 0; // E(0) - E -- this is the value we read in
		double K = (1.985/96485.339924)*2.30;
		
		return E / (K * this.temperature);
	}
	
	// Returns String with sensor move location
	// BECAUSE we cannot measure the height of the water with the robot during its independent
	// navigation, we must hard code the different angles for each case. therefore, any numbers 
	// changed here, change it in the removeSensors() method, too.
	public void moveSensor()
	{
			
		// Below ground
		// Robot needs to be about 29.2 cm away from the center of the water.
		if(this.course.getCourseNumber() == 1)
		{
			// Get the arm in the correct general location, the closer
			//it gets to the water, the more slowly the arm will move
			for(int i = 145; i > 20; i--)
			{
				r.moveServo(RXTXRobot.SERVO1, i);
				r.sleep(50);
				if(i > 90)
					r.moveServo(RXTXRobot.SERVO2, i);
					r.sleep(50);
			}
				
		}
		// Ground
		// Since the water is at ground level, there shouldn't be a need to take 
		// multiple steps to angle the sensor perpendicularly over. However, the robot 
		// needs to be 29.28 cm from the water in order for this to work.
		if(this.course.getCourseNumber() == 2)
		{
			// Get the arm in the correct general location, the closer
			// it gets, the more slowly the arm will move
			for(int i = 145; i > 40; i--)
			{
				
				r.moveServo(RXTXRobot.SERVO1, i);
				r.sleep(50);
				r.moveServo(RXTXRobot.SERVO2, i);
				r.sleep(50);
			}
		}
		
		// Above ground
		if(this.course.getCourseNumber() == 3)
		{
			// Get the arm in the correct general location, the closer
			// it gets to the water, the more slowly the arm will move
			
			
			for(int i = 145; i > 90; i--) 
			{
				// Moves main arm directly over the water, 66 degrees
				r.moveBothServos(i, i); 
				r.sleep(50);
			}
			for(int i = 90; i > 10; i--)
			{
				r.moveServo(RXTXRobot.SERVO2, i);
				r.sleep(70);
			}
			for(int i = 90; i > 75; i--)
			{
				r.moveServo(RXTXRobot.SERVO2, i);
				r.sleep(70);
			}
		}
	}
	
	// Removes the sensors from the water (reverse of moveSensor() method)
	public void removeSensor()
	{
		if(this.course.getCourseNumber() == 1)
		{
			int j;
			j= 90;
			//Returns arm to 145, 145
			for(int i = 20; i <= 145; i++)
			{
				r.moveServo(RXTXRobot.SERVO1, i);
				r.sleep(50);
				if(i > 90) 
				{
					r.moveServo(RXTXRobot.SERVO2, j);
					r.sleep(50);
					j++;
				}
			}	
		}	
		// Ground
		if(this.course.getCourseNumber() == 2)
		{
			int j;
			j= 40;
			//Returns package to 145, 145
			for(int i = 20; i <= 145; i++)
			{
				r.moveServo(RXTXRobot.SERVO1, i);
				r.sleep(50);
				if(i > 40) 
				{
					r.moveServo(RXTXRobot.SERVO2, j);
					r.sleep(50);
					j++;
				}
			}
		}
		// Above ground
		if(this.course.getCourseNumber() == 3)
		{
			int j;
			j= 10;
			//Returns package to 145, 145
			for(int i = 75; i <= 145; i++)
			{
				r.moveServo(RXTXRobot.SERVO1, i);
				r.sleep(50);
				if(i > 10) 
				{
					r.moveServo(RXTXRobot.SERVO2, j);
					r.sleep(50);
					j++;
				}
			}
		}
	}
	//This method will be used to close all the devices that are connected
	//in the constructor of this class.
	public void close()
	{
		r.moveBothServos(145, 0);
		r.close();
	}
}