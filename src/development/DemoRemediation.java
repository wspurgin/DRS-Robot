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
	private boolean phIsNeutral;
	
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
        
        	this.phIsNeutral = false;
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
            this.phIsNeutral = true;
	    
        // Prints out turbidity, temperature, and pH to the screen
	    System.out.println("The turbidity is " + turbidity + ".");
	    System.out.println("The temperature is " + temperature + " degrees Celsius.");
	    System.out.println("The pH is " + pH + ".");
	    
	    remediate();
	}
	
	// Adds neutralizing solution to water until pH becomes neutral
	public void remediate()
	{
		int mixSpeed = 255; // Max	
		double v = ((.75*Math.pow(10, -1*this.pH))-(7.5*Math.pow(10, -8)))/(9.999*Math.pow(10,-8));
		v *= .80;
		
		// Loops while the pH is not neutral
		while(!this.phIsNeutral)
		{
            		r.sleep(500);
			// Dose water
			r.runMotor(RXTXRobot.MOTOR3, 255, 30000);

			r.setMixerSpeed(mixSpeed);
			r.runMixer(RXTXRobot.MOTOR4, 500);
            		r.stopMixer(RXTXRobot.MOTOR4);

		    this.pH = measurePH();
		    
		    if(this.pH >= 7.0 && this.pH <= 7.5)
		    	this.phIsNeutral = true;
		}
	}

	// Returns the temperature of a liquid in Celsius
	public int measureTemp()
	{
		int perm = 0;
		int count = 0;
		while(true)
		{
			r.refreshDigitalPins();
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

		int b = 0;
		int m = 1;
			
		int y = 0;
		int count = 0;
		while(true)
		{
			if(r.getAnalogPin(5).getValue() == y)
			{
				count++;
				if(count == 10)
					break;
			}
			else
			{
				y = r.getAnalogPin(5).getValue();
				count = 0;
			}
			r.sleep(500);
		}	
		
		return (y - b) / m;
	}

	// Returns the pH of a liquid
	public double measurePH()
	{
		double b = 1258.66666666667;
		double m = -94;
		
		double y = 0;
		int count = 0;
		while(true)
		{
			if(r.getAnalogPin(5).getValue() == y)
			{
				count++;
				if(count == 10)
					break;
			}
			else
			{
				y = r.getAnalogPin(5).getValue();
				count = 0;
			}
			r.sleep(500);
		}
		
		return (y - b) / m;
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