package drsrobot;
import rxtxrobot.*;

import java.util.ArrayList;

public class Remediator 
{
	private RXTXRobot r;
	private int turbidity;
	private double temperature;
	private double pH;
	private int courseNumber;
	
	// Constructor
	public Remediator(RXTXRobot r, int courseNumber)
	{
		// Initialize robot
		this.r = r;
		this.r.moveBothServos(145, 145);
		
		this.courseNumber = courseNumber;
		
		this.turbidity = 0;
		this.temperature = 0.0;
		this.pH = 0.0;
	}
	
	// Test the water for values of turbidity, temperature, and pH
	public void test()
	{   
	    this.moveSensor();
	    // Measure turbidity, temperature, and pH
	    //this.turbidity = measureTurbidity();
	    this.temperature = measureTemp();
	    this.pH = measurePH();
	    
        // Prints out turbidity, temperature, and pH to the screen
	    System.out.println("The turbidity is " + this.turbidity + ".");
	    System.out.println("The temperature is " + this.temperature + " degrees Celsius.");
	    System.out.println("The pH is " + this.pH + ".");
	    
	    // A test will normally call the remediate method, but for the sake of this
	    // demo we will not call the remediate method since each one much be done statically
	    // ^^ What test?
	    this.remediate();
	}
	
	// Adds neutralizing solution to water until pH becomes neutral
	public void remediate()
	{
		this.pH = this.measurePH();
		int mixSpeed = 100; // Max
		double volumeOriginal = 750;
		double volume = ((Math.pow(10, -1*this.pH) - Math.pow(10, -7)) * volumeOriginal) / (Math.pow(10, -3) - Math.pow(10, -7));
		volume *= .80;
		volume = 30000 + (volume - 2.4) * (25 / 3);
		int time = (int)(volume / 1000);
		
//		// Run loop for time needed to add 80% of calculated value
//		this.r.runMotor(RXTXRobot.MOTOR3, 255, 0);
//		for(int i = 0; i < time; i++)
//		{
//			this.r.sleep(1000);
//		}
//		this.r.runMotor(RXTXRobot.MOTOR3, 0, 0);
		
		// Loops while the pH is not neutral
		while(this.pH < 7.0 || this.pH > 7.5)
		{
			this.r.runMotor(RXTXRobot.MOTOR3, 255, 0);
            this.r.sleep(5000);
            this.r.runMotor(RXTXRobot.MOTOR3, 0, 0);
            
			this.r.setMixerSpeed(mixSpeed);
			this.r.runMixer(RXTXRobot.MOTOR4, 500);
            this.r.stopMixer(RXTXRobot.MOTOR4);
		    
		    this.pH = measurePH();
		}
	}

	// Returns the temperature of a liquid in Celsius
	public int measureTemp()
	{
		int perm = 0;
		int notStable = 0;
		int count = 0;
		
		// Loop until temperature stabilizes
		while(true)
		{
			this.r.refreshDigitalPins();
			notStable++;
			
			// If temperature has not stabilized after 100 iterations, break
			if(notStable > 100)
				break;
			
			if(this.r.getTemperature() == perm)
			{
				count++;
				if(count == 10)
					break;
			}
			else
			{
				perm = this.r.getTemperature();
				count = 0;
			}
			this.r.sleep(200);
		}
		
		// If temperature did not stabilize after 100 iterations, use weighted average
		if(notStable > 100)
			perm = (int)findAverageValue(2);
		
		return perm;
	}
	
	// Returns turbidity in NTU
	public double measureTurbidity()
	{	
		double a = .0005922;
		double b = .3487;
		double c = 450.605;
		double y = this.findAverageValue(1);
		c = c - y;
		
		int result = (int)((b - Math.sqrt(b * b - 4 * a * c)) / (2 * a));
		
		if(y < 400 || result == 0)
		{
			a = -.10081;
			b = 445.82;
			result = (int)((y - b) / a);
		}
		
		return result;
	}

	// Returns the pH of a liquid
	public double measurePH()
	{
		double b = 1258.66666666667;
		double m = -94;
		double y = this.findAverageValue(5);
		
		return (y - b) / m;
	}
	
	// Calculate the weighted average of a value read for sensor based off pin number
	private double findAverageValue(int pinNum)
	{
		double y = 0;
		// If pinNum is 2, run code for temperature
		if(pinNum == 2)
		{
			ArrayList<Double> list = new ArrayList<Double>(); 
			ArrayList<Integer> frequency = new ArrayList<Integer>();
			
			// Loop 50 times
			for(int i = 0; i < 50; i++)
			{
				this.r.refreshDigitalPins();
				y = this.r.getTemperature();
				// If the list is does not contain value, add, else increment frequency
				if(!list.contains(y))
				{
					list.add(y);
					frequency.add(1);
				}
				else
				{
					int index =	list.indexOf(y);
					frequency.set(index, frequency.get(index)+1);
				}
				this.r.sleep(100);
			}
			
			int count = 0;
			y = 0;
			// Loop through frequencies and add values to y if their frequencies are greater than 4
			for(int i = 0; i < frequency.size(); i++)
			{
				int freq = frequency.get(i);
				double val = list.get(i);
				
				if(freq >= 4)
				{
					y = y + (val * freq);
					count += freq;
				}
			}
			y /= (double)count;
		}
		else
		{
			ArrayList<Double> list = new ArrayList<Double>(); 
			ArrayList<Integer> frequency = new ArrayList<Integer>();
			
			// Loop 200 times
			for(int i = 0; i < 200; i++)
			{
				this.r.refreshAnalogPins();
				y = this.r.getAnalogPin(pinNum).getValue();
				
				// If the list is does not contain value, add, else increment frequency
				if(!list.contains(y))
				{
					list.add(y);
					frequency.add(1);
				}
				else
				{
					int index =	list.indexOf(y);
					frequency.set(index, frequency.get(index)+1);
				}
				this.r.sleep(100);
			}
			
			int count = 0;
			y = 0;
			// Loop through frequencies and add values to y if their frequencies are greater than 4
			for(int i = 0; i < frequency.size(); i++)
			{
				int freq = frequency.get(i);
				double val = list.get(i);
				
				if(freq >= 4)
				{
					y = y + (val*freq);
					count += freq;
				}
			}
			y /= (double)count;
		}
		
//		System.out.println("\n" + y);
		return y;
	}
	
	// Returns String with sensor move location
	// BECAUSE we cannot measure the height of the water with the robot during its independent
	// navigation, we must hard code the different angles for each case. therefore, any numbers 
	// changed here, change it in the removeSensors() method, too.
	public void moveSensor()
	{
		// Below ground
		// Robot needs to be about 29.2 cm away from the center of the water.
		if(this.courseNumber == 1)
		{
			// Get the arm in the correct general location, the closer
			//it gets to the water, the more slowly the arm will move
			for(int i = 145; i > 20; i--)
			{
				this.r.moveServo(RXTXRobot.SERVO1, i);
				this.r.sleep(50);
				if(i > 90)
					this.r.moveServo(RXTXRobot.SERVO2, i);
					this.r.sleep(50);
			}		
		}
		// Ground
		// Since the water is at ground level, there shouldn't be a need to take 
		// multiple steps to angle the sensor perpendicularly over. However, the robot 
		// needs to be 29.28 cm from the water in order for this to work.
		if(this.courseNumber == 2)
		{
			// Get the arm in the correct general location, the closer
			// it gets, the more slowly the arm will move
			for(int i = 145; i > 40; i--)
			{
				this.r.moveServo(RXTXRobot.SERVO1, i);
				this.r.sleep(50);
				this.r.moveServo(RXTXRobot.SERVO2, i);
				this.r.sleep(50);
			}
		}
		
		// Above ground
		if(this.courseNumber == 3)
		{
			// Get the arm in the correct general location, the closer
			// it gets to the water, the more slowly the arm will move
			for(int i = 145; i > 90; i--) 
			{
				// Moves main arm directly over the water, 66 degrees
				this.r.moveBothServos(i, i); 
				this.r.sleep(50);
			}
			for(int i = 90; i > 10; i--)
			{
				this.r.moveServo(RXTXRobot.SERVO2, i);
				this.r.sleep(70);
			}
			for(int i = 90; i > 75; i--)
			{
				this.r.moveServo(RXTXRobot.SERVO2, i);
				this.r.sleep(70);
			}
		}
	}
	
	// Removes the sensors from the water (reverse of moveSensor() method)
	public void removeSensor()
	{
		if(this.courseNumber == 1)
		{
			int j;
			j= 90;
			//Returns arm to 145, 145
			for(int i = 20; i <= 145; i++)
			{
				this.r.moveServo(RXTXRobot.SERVO1, i);
				this.r.sleep(50);
				if(i > 90) 
				{
					this.r.moveServo(RXTXRobot.SERVO2, j);
					this.r.sleep(50);
					j++;
				}
			}	
		}	
		// Ground
		if(this.courseNumber == 2)
		{
			int j;
			j= 40;
			//Returns package to 145, 145
			for(int i = 20; i <= 145; i++)
			{
				this.r.moveServo(RXTXRobot.SERVO1, i);
				this.r.sleep(50);
				if(i > 40) 
				{
					this.r.moveServo(RXTXRobot.SERVO2, j);
					this.r.sleep(50);
					j++;
				}
			}
		}
		// Above ground
		if(this.courseNumber == 3)
		{
			int j;
			j= 10;
			//Returns package to 145, 145
			for(int i = 75; i <= 145; i++)
			{
				this.r.moveServo(RXTXRobot.SERVO1, i);
				this.r.sleep(50);
				if(i > 10) 
				{
					this.r.moveServo(RXTXRobot.SERVO2, j);
					this.r.sleep(50);
					j++;
				}
			}
		}
	}
}