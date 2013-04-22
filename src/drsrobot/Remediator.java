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
	    moveSensor();
	    // Measure turbidity, temperature, and pH
	    //this.turbidity = measureTurbidity();
	    temperature = measureTemp();
	    pH = measurePH();
	    
        // Prints out turbidity, temperature, and pH to the screen
	    System.out.println("The turbidity is " + turbidity + ".");
	    System.out.printf("The temperature is %.1f degrees Celsius.\n", temperature);
	    System.out.printf("The pH is %.1f.\n", pH);
	    
	    remediate();
	}
	
	// Adds neutralizing solution to water until pH becomes neutral
	public void remediate()
	{
		pH = measurePH();
		int mixSpeed = 100; // Max
		double volumeOriginal = 750;
		double volume = ((Math.pow(10, -1*pH) - Math.pow(10, -7)) * volumeOriginal) / (Math.pow(10, -3) - Math.pow(10, -7));
		volume *= .80;
		volume = 30000 + (volume - 2.4) * (25 / 3);
		int time = (int)(volume / 1000);
		
		// Run loop for time needed to add 80% of calculated value
		this.r.runMotor(RXTXRobot.MOTOR3, 255, 0);
		for(int i = 0; i < time; i++)
		{
			r.sleep(1000);
		}
		r.runMotor(RXTXRobot.MOTOR3, 0, 0);
		
		// Loops while the pH is not neutral
		while(pH < 7.0 || pH > 7.5)
		{
			r.runMotor(RXTXRobot.MOTOR3, 255, 0);
            r.sleep(5000);
            r.runMotor(RXTXRobot.MOTOR3, 0, 0);
            
			r.setMixerSpeed(mixSpeed);
			r.runMixer(RXTXRobot.MOTOR4, 500);
            r.stopMixer(RXTXRobot.MOTOR4);
		    
		    pH = measurePH();
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
			r.refreshDigitalPins();
			notStable++;
			
			// If temperature has not stabilized after 100 iterations, break
			if(notStable > 100)
				break;
			
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
			r.sleep(50);
		}
		
		// If temperature did not stabilize after 100 iterations, use weighted average
		if(notStable > 100)
			perm = (int)findAverageValue(2);
		
		return perm;
	}
	
	// Returns turbidity in NTU
	public double measureTurbidity()
	{	
		double a = .1474;
		double b = -118.3897;
		double c = 23748.1467;
		double y = findAverageValue(0);
//		c = c - y;
//		int result = (int)((b + Math.sqrt(b * b - 4 * a * c)) / (2 * a));
		
		int result = (int)(a * y * y + b * y + c);
		
//		double a = -.0975;
//		double b = 386.9245;
//		double y = this.findAverageValue(0);
//		int result = (int)(a * y + b);
		
		return result;
	}

	// Returns the pH of a liquid
	public double measurePH()
	{
		double a = 0.0006524;
		double b = -.74155;
		double R = 8.3145;
		double F = 96485.339924;
		double T = temperature + 273.15;
		double y = findAverageValue(1);
		
		return -(((a * y + b) * F) / (R * T * 2.3));
	}
	
	// Calculate the weighted average of a value read for sensor based off pin number
	public double findAverageValue(int pinNum)
	{
		double y = 0;
		// If pinNum is 2, run code for temperature
		if(pinNum == 2)
		{
			ArrayList<Double> list = new ArrayList<Double>(); 
			ArrayList<Integer> frequency = new ArrayList<Integer>();
			
			// Loop 50 times
			for(int i = 0; i < 200; i++)
			{
				r.refreshDigitalPins();
				y = r.getTemperature();
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
				r.sleep(50);
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
				r.refreshAnalogPins();
				y = r.getAnalogPin(pinNum).getValue();
				
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
				r.sleep(50);
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
//		System.out.println(y);
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
		if(courseNumber == 1)
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
		if(courseNumber == 2)
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
		if(courseNumber == 3)
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
		if(this.courseNumber == 1)
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
		if(courseNumber == 2)
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
		if(courseNumber == 3)
		{
			int j;
			j= 10;
			// Returns package to 145, 145
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
}
