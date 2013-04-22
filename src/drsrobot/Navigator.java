package drsrobot;
import rxtxrobot.*;

 //This class is responsible for the states from after activation to remediation

public class Navigator
{
	private RXTXRobot r;
	private RFIDSensor sensor;
	private final String RFID_PORT = "/dev/tty.usbserial-A901JX0L";
	private boolean bumpSensorEngaged;
	private int courseNumber;
	private int bearing;
	private final int width = 10;
	private final int length = 10;
	private final int EAST = 254;
	
	/*private final int NORTH = 180;
	private final int WEST = 81;
	private final int SOUTH = 334;*/
	
	public Navigator(RXTXRobot r)
	{
		this.r = r;
		this.sensor = new RFIDSensor();
		this.sensor.setPort(RFID_PORT);
		this.sensor.connect();
		this.bumpSensorEngaged = false;
		this.courseNumber = 1;
		
		// East is 1, 
		bearing = 1;
	}
	
	// Print out bearing
	private void printBearing()
	{
		if(bearing == 1)
			System.out.println("EAST");
		else if(bearing == 2)
			System.out.println("SOUTH");
		else if(bearing == 3)
			System.out.println("WEST");
		else
			System.out.println("NORTH");
	}
	
	// Receiving positive one means left turn, negative one right turn
	public void orient(int direction)
	{
        // Takes priming read of the bump sensors. The Robot will move accordingly to
        // which bump sensor is engaged. Note that if for some reason the boolean
        // bumpSensorEngaged is true. The Robot will not turn
		if(!bumpSensorEngaged)
			r.runMotor(RXTXRobot.MOTOR1, 195, RXTXRobot.MOTOR2, -215, 0);
		else if(readBumpSensor())
		{
			r.runMotor(RXTXRobot.MOTOR1, -235, RXTXRobot.MOTOR2, -255, 100);
			if(readBumpSensor())
				r.runMotor(RXTXRobot.MOTOR1, 235, RXTXRobot.MOTOR2, 255, 200);
            orient(direction);
		}
		else
			bumpSensorEngaged = false;
		r.refreshAnalogPins();
		while(r.readCompass() > direction + 2 || r.readCompass() < direction - 2)
		{
			if(readBumpSensor())
			{
				bumpSensorEngaged = true;
				break;
			}
			r.refreshAnalogPins();
		}
		r.runMotor(RXTXRobot.MOTOR1, 0, RXTXRobot.MOTOR2, 0, 0);
		if(bumpSensorEngaged)
		{
			bumpSensorEngaged = false;
			r.refreshAnalogPins();
			orient(direction);
		}
	}
	
	// Tests if bump sensor has been triggered
	private boolean readBumpSensor()
	{
		if(r.getAnalogPin(2).getValue() == 0)
			return true;
		
		return false;
	}
	
	// Returns true if after looping tens times, at least three of the values were less than 800 (on white)
	private boolean lineSensor()
	{
		int count = 0;
		// Loop ten times
		for(int i = 0; i < 10; i++)
		{
			// Increment count, line sensor returning low values
			if(r.getAnalogPin(3).getValue() < 800)
				count++;
			// On the white, return true
			if(count >= 3)
				return true;
		}

		return false;
	}
	
	// Left if positive, right is negative
	public void turn(int direction)
	{
		if(direction > 0)
		{
			r.runMotor(RXTXRobot.MOTOR1, -235, RXTXRobot.MOTOR2, 255, 2472);
			
			// Change bearing
			if(bearing == 1)
				bearing = 4;
			else
				bearing--;
		}
		else
		{
			r.runMotor(RXTXRobot.MOTOR1, 235, RXTXRobot.MOTOR2, -255, 2472);
			
			// Change bearing
			if(bearing == 4)
				bearing = 1;
			else
				bearing++;
		}
		
	}
	
	// Runs the robot against whatever surface is pressing against it to straighten it out
	private void straighten()
	{
		r.runMotor(RXTXRobot.MOTOR1, 120, RXTXRobot.MOTOR2, 130, 700);
	}
	
	// Moves forward until bump sensor is triggered
	private void moveForwardWithBumpSensor()
	{
		r.refreshAnalogPins();
		r.runMotor(RXTXRobot.MOTOR1, 235, RXTXRobot.MOTOR2, 255, 0);
		while(!readBumpSensor())
		{
			r.refreshAnalogPins();
		}
		straighten();
		r.runMotor(RXTXRobot.MOTOR1, -235, RXTXRobot.MOTOR2, -255, 1000);
	}
	
	private void moveForwardWithPingSensor()
	{
		r.runMotor(RXTXRobot.MOTOR1, 235, RXTXRobot.MOTOR2, 255, 0);
		while(r.getPing() < 20 && !readBumpSensor())
		{
			r.refreshAnalogPins();
		}
		r.runMotor(RXTXRobot.MOTOR1, 235, RXTXRobot.MOTOR2, 255, 1000);
	}
	
	// Sets robot up and calls findRFID()
	public void setUp()
	{
		orient(EAST);
		findRFID();
	}
	
	// Circles play field until RFID tag is found
	private void findRFID()
	{
		System.out.println("Moving to RFID....");
		r.runMotor(RXTXRobot.MOTOR1, 235, RXTXRobot.MOTOR2, 255, 0);
		while(!sensor.hasTag())
		{
			if(this.readBumpSensor())
			{
				straighten();
				r.runMotor(RXTXRobot.MOTOR1, -235, RXTXRobot.MOTOR2, -255, 1000);
				bumpSensorEngaged = true;
				break;
			}
			r.refreshAnalogPins();
		}
		r.runMotor(RXTXRobot.MOTOR1, 0, RXTXRobot.MOTOR2, 0, 0);
		
		// You've hit a wall, turn and continue searching
		if(bumpSensorEngaged)
		{
			bumpSensorEngaged = false;
			r.refreshAnalogPins();
			turn(-1);
			printBearing();
			findRFID();
		}
		
		// You've found the tag! Set course number appropriately
		String tag = sensor.getTag();
		if(tag.equals("6A003E4EA6BC"))
			courseNumber = 1;
		else if(tag.equals("6A003E834B9C"))
			courseNumber = 2;
		else if(tag.equals("6A003E6E477D"))
			courseNumber = 3;
		else
			System.out.println("ERROR: Invalid RFID tag " + tag);
		sensor.close();
	}
	
	// Recursively calls to follow the wall and avoid obstacles until line sensor senses white line
	public void goToWell()
	{
		if(courseNumber == 1)
		{
			int count = 0;
			
			if(bearing == 2)
				count = 3;
			else if(bearing == 3)
				count = 2;
			else if(bearing == 4)
				count = 1;
			
			for(int i = 0; i < count; i++)
			{
				moveForwardWithBumpSensor();
				turn(-1);
			}
			
			moveForwardWithPingSensor();
			turn(1);
			r.runMotor(RXTXRobot.MOTOR1, 235, RXTXRobot.MOTOR2, 255, 1000);
			turn(1);
			moveForwardWithBumpSensor();
			turn(-1);
			findWell();
		}
		else if(courseNumber == 2)
		{
			int count = 0;
			
			if(bearing == 2)
				count = 2;
			else if(bearing == 3)
				count = 1;
			else if(bearing == 4)
				count = 0;
			
			for(int i = 0; i < count; i++)
			{
				moveForwardWithBumpSensor();
				turn(-1);
			}
			findWell();
		}
		else if(courseNumber == 3)
		{
			if(bearing == 2)
			{
				turn(1);
				turn(1);
				moveForwardWithBumpSensor();
				turn(1);
				moveForwardWithPingSensor();
				turn(-1);
				r.runMotor(RXTXRobot.MOTOR1, 235, RXTXRobot.MOTOR2, 255, 1000);
				turn(-1);
			}
			findLowWell();
		}
	}
	
	private void findWell()
	{
		boolean object = false;
		boolean lineSensor = false;
			
		r.resetEncodedMotorPosition(RXTXRobot.MOTOR1);
		r.runMotor(RXTXRobot.MOTOR1, 235, RXTXRobot.MOTOR2, 255, 0);
		while(!readBumpSensor())
		{
			r.refreshAnalogPins();
				
			lineSensor = lineSensor();
			if(lineSensor)
				break;
				
			bumpSensorEngaged = true;
				
			if(r.getEncodedMotorPosition(RXTXRobot.MOTOR1) < width - 10 && courseNumber == 2)
				object = true;
			if(r.getEncodedMotorPosition(RXTXRobot.MOTOR1) < width / 2 && courseNumber == 1)
				object = true;
		}
			
		if(object)
		{
			turn(-1);
			r.runMotor(RXTXRobot.MOTOR1, 235, RXTXRobot.MOTOR2, 255, 1000);
			turn(1);
			r.runMotor(RXTXRobot.MOTOR1, 235, RXTXRobot.MOTOR2, 255, 1000);
			turn(1);
			moveForwardWithBumpSensor();
			turn(-1);
			findWell();
		}
		else if(bearing == 1)
		{
			turn(-1);
			r.runMotor(RXTXRobot.MOTOR1, 235, RXTXRobot.MOTOR2, 255, 1000);
			turn(-1);
			findWell();
		}
		else if(bearing == 3)
		{
			turn(1);
			r.runMotor(RXTXRobot.MOTOR1, 235, RXTXRobot.MOTOR2, 255, 1000);
			turn(1);
			findWell();
		}
	}
	
	private void findLowWell()
	{
		boolean lineSensor = false;
	
		// Run forward unconditionally
		r.runMotor(RXTXRobot.MOTOR1, 235, RXTXRobot.MOTOR2, 255, 1000);
		
	    // Run motors indefinitely
		r.runMotor(RXTXRobot.MOTOR1, 235, RXTXRobot.MOTOR2, 255, 0);
			
		// Loop while close to the wall, break if either line sensor or bump sensors are triggered
		while(r.getPing() < 20)
		{
			r.refreshAnalogPins();
				
			lineSensor = lineSensor();
			if(lineSensor)
				break;
					
			// Back up and break if bump sensor triggered
			if(readBumpSensor())
			{
				straighten();
				r.runMotor(RXTXRobot.MOTOR1, -235, RXTXRobot.MOTOR2, -255, 2000);
				bumpSensorEngaged = true;
				break;
			}
				
			// Turn off motors
			r.runMotor(RXTXRobot.MOTOR1, 0, RXTXRobot.MOTOR2, 0, 0);
				
			// If the bump sensor was triggered, turn to avoid either an obstacle or a wall
			if(bumpSensorEngaged)
			{
				bumpSensorEngaged = false;
				turn(-1);
				goToWell();
			}
		    // Turn back from whence you came (the ping sensor was over-distanced)
			else if(!lineSensor)
				turn(1);
			// THE LINE SENSOR WAS TRIGGERED! You're at the well.
			else
				moveIntoPosition();
		}
	}
	
	//	Move the robot into appropriate position for remediation to begin.
    //	Because the line sensor was activated when this method is called, we have to
    //	assume that the well is in front of or behind us on our current bearing.
	private void moveIntoPosition()
	{
        // This method needs to move to it's perpendicular position to put the Ping
        // sensor in position.
		turn(1);
        // Reads for the  base distance from the Ping sensor.
		int base = r.getPing();
		int count = 0;
		
		r.runMotor(RXTXRobot.MOTOR1, 235, RXTXRobot.MOTOR2, 255, 0);
		
        // If the Ping sensor reads a significant spike in distances, we know that it returned
        // the distance to the well. Otherwise, it has gone too far (should run for approx. 3 sec)
		while((r.getPing() >= base - 2 && r.getPing() <= base + 2) && count != 25)
		{
			r.sleep(100);
			count++;
		}
		this.r.runMotor(RXTXRobot.MOTOR1, 0, RXTXRobot.MOTOR2, 0, 0);
		
        // In the case that the Ping sensor had been engaged we move towards the object which
        // should be the well.
		if(!(r.getPing() >= base - 2 && r.getPing() <= base + 2))
		{
			turn(-1);
			moveForwardWithBumpSensor();
		}
        // In the case that it went too long without reading a different value
        // it calls it self recursively to locate the well behind it's original bearing
		else if(count == 25)
		{
			// Make a 180 degree turn
			turn(1);
			turn(1);
		}
	}

	//Takes robot back to other side of field
	public void goHome()
	{
		while(bearing != 2)
		{
			turn(1);
			moveForwardWithBumpSensor();
		}
		
		if(courseNumber == 1 || courseNumber == 3)
		{
			turn(1);
			
			r.runMotor(RXTXRobot.MOTOR1, 235, RXTXRobot.MOTOR2, 255, 0);
			moveForwardWithPingSensor();

			// The robot will "travel" a bit as it turns allowing it to move a little forward.
			// This way (since the Ping sensor is in the middle on the right of the
			// robot) we will move completely through the gap.
			r.runMotor(RXTXRobot.MOTOR1, 235, RXTXRobot.MOTOR2, 0, 10000);
			r.runMotor(RXTXRobot.MOTOR1, 235, RXTXRobot.MOTOR2, 255, 10000);
		}
		else if(courseNumber == 2)
		{
			moveForwardWithBumpSensor();
		}
	}

	public RXTXRobot getR() 
	{
		return r;
	}

	public void setR(RXTXRobot r) 
	{
		this.r = r;
	}

	public RFIDSensor getSensor() 
	{
		return sensor;
	}

	public void setSensor(RFIDSensor sensor) 
	{
		this.sensor = sensor;
	}

	public boolean isBumpSensorEngaged() 
	{
		return bumpSensorEngaged;
	}

	public void setBumpSensorEngaged(boolean bumpSensorEngaged) 
	{
		this.bumpSensorEngaged = bumpSensorEngaged;
	}

	public int getCourseNumber()
	{
		return courseNumber;
	}

	public void setCourseNumber(int courseNumber) 
	{
		this.courseNumber = courseNumber;
	}

	public int getBearing() 
	{
		return bearing;
	}

	public void setBearing(int bearing) 
	{
		this.bearing = bearing;
	}

	public String getRFID_PORT() 
	{
		return RFID_PORT;
	}

	public int getWidth() 
	{
		return width;
	}

	public int getLength() 
	{
		return length;
	}

	public int getEAST() 
	{
		return EAST;
	}
	
}