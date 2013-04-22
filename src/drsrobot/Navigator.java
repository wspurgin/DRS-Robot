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
	private final int width = 1011159;
	private final int length = 2219578;
	private final int EAST = 260;
	private final int turns = 2550;
	private final int m1 = 240;
	private final int m2 = 255;
	private final int platformDistance = 351090;
	private final int raisedPlatformDistance = 924824;

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

	// Receiving positive one means left turn, negative one right turn
	public void orient(int direction)
	{
		System.out.println("Orienting...");
        // Takes priming read of the bump sensors. The Robot will move accordingly to
        // which bump sensor is engaged. Note that if for some reason the boolean
        // bumpSensorEngaged is true. The Robot will not turn
		if(!bumpSensorEngaged)
			r.runMotor(RXTXRobot.MOTOR1, 195, RXTXRobot.MOTOR2, -215, 0);
		else if(readBumpSensor())
		{
			r.runMotor(RXTXRobot.MOTOR1, -m1, RXTXRobot.MOTOR2, -m2, 100);
			r.refreshAnalogPins();
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

	// Left if positive, right is negative
	public void turn(int direction)
	{
		System.out.println("Turning...");
		if(direction > 0)
		{
			r.runMotor(RXTXRobot.MOTOR1, -m1, RXTXRobot.MOTOR2, m2, turns);

			// Change bearing
			if(bearing == 1)
				bearing = 4;
			else
				bearing--;
		}
		else
		{
			r.runMotor(RXTXRobot.MOTOR1, m1, RXTXRobot.MOTOR2, -m2, turns);

			// Change bearing
			if(bearing == 4)
				bearing = 1;
			else
				bearing++;
		}
	}

	// Sets robot up and calls findRFID()
	public void setUp()
	{
		orient(EAST);
		moveForwardWithBumpSensor();
		turn(-1);
		r.runMotor(RXTXRobot.MOTOR1, -m1, RXTXRobot.MOTOR2, -m2, 1000);
		findRFID();
	}

	// Circles play field until RFID tag is found
	public void findRFID()
	{
		System.out.println("Moving to RFID....");
		r.runMotor(RXTXRobot.MOTOR1, m1, RXTXRobot.MOTOR2, m2, 0);
		r.refreshAnalogPins();
		while(!sensor.hasTag())
		{
			if(this.readBumpSensor())
			{
				straighten();
				r.runMotor(RXTXRobot.MOTOR1, -m1, RXTXRobot.MOTOR2, -m2, 150);
				bumpSensorEngaged = true;
				break;
			}
			r.refreshAnalogPins();
		}
		r.runMotor(RXTXRobot.MOTOR1, 0, RXTXRobot.MOTOR2, 0, 0);

		// You've hit a wall, turn and continue searching
		if(bumpSensorEngaged)
		{
			System.out.println("Bump sensor hit");
			bumpSensorEngaged = false;
			r.refreshAnalogPins();
			turn(-1);
			printBearing();
			findRFID();
		}

		// You've found the tag! Set course number appropriately
		String tag = sensor.getTag();
		//Fish Town, maze, ground well
		if(tag.equals("67007BBDB819"))
			courseNumber = 1;
		//Dadaab, high bar, above ground well
		else if(tag.equals("6A003E834B9C"))
			courseNumber = 2;
		//Ali Ade, wall, below ground well
		else if(tag.equals("6A003E6E477D"))
			courseNumber = 3;
		else
			System.out.println("ERROR: Invalid RFID tag " + tag);
	}

	// Recursively calls to follow the wall and avoid obstacles until line sensor senses white line
	public void goToWell()
	{
		System.out.println("Going to well...");
		if(courseNumber != 2)
		{
			while(bearing != 1)
			{
				turn(-1);
			}
			System.out.println("Get to East wall");
			moveForwardWithBumpSensor();
			turn(1);
			moveForwardWithBumpSensor();
			straighten();
			turn(1);
			System.out.println("Reached middle, move forward with ping sensor");
			moveForwardWithPingSensor();
			turn(-1);
			if(courseNumber == 1)
			{
				System.out.println("On course one, move forward to far West wall");
				r.runMotor(RXTXRobot.MOTOR1, m1, RXTXRobot.MOTOR2, m2, 1000);
				turn(1);
				moveForwardWithBumpSensor();
				turn(-1);
				findWell();
			}
			else if(courseNumber == 3)
			{
				System.out.println("On course three, move forward to far East wall");
				//Move robot to the East wall
				r.runMotor(RXTXRobot.MOTOR1, m1, RXTXRobot.MOTOR2, m2, 4000);
				turn(-1);
				moveForwardWithBumpSensor();
				turn(1);
				findLowWell();
			}
		}
		if(courseNumber == 2)
		{
			System.out.println("On course two, move forward to far West wall");
			while(bearing != 3)
			{ 
				turn(-1);
			}
			moveForwardWithBumpSensor();
			turn(-1);
			findWell();
		}
	}

	public void findWell()
	{
		System.out.println("Finding well...");
		r.refreshAnalogPins();
		boolean lineSensor = false;
        
		System.out.println("Prepare to traverse until bump hit or line found");
		r.runMotor(RXTXRobot.MOTOR1, m1, RXTXRobot.MOTOR2, m2, 0);
		while(!readBumpSensor())
		{
			r.refreshAnalogPins();
            
			lineSensor = lineSensor();
			if(lineSensor)
				break;
		}

		if(lineSensor)
		{
			r.runMotor(RXTXRobot.MOTOR1, 0, RXTXRobot.MOTOR2, 0, 0);
			System.out.println("Line Sensor Triggered");
			alignToWell();
			return;
		}

		straighten();
		r.runMotor(RXTXRobot.MOTOR1, -m1, RXTXRobot.MOTOR2, -m2, 1000);
       
		if(bearing == 1)
		{
			System.out.println("Going E, turn W");
			turn(-1);
			r.runMotor(RXTXRobot.MOTOR1, m1, RXTXRobot.MOTOR2, m2, 3000);
			turn(-1);
			findWell();
		}
		else if(bearing == 3)
		{
			System.out.println("Going W, turn E");
			turn(1);
			r.runMotor(RXTXRobot.MOTOR1, m1, RXTXRobot.MOTOR2, m2, 3000);
			turn(1);
			findWell();
		}
		else if(bearing == 4)
		{
			System.out.println("Going N, turn E");
			turn(-1);
			findWell();
		}
	}

	public void findLowWell()
	{
		System.out.println("Finding low well...");

	    // Run motors indefinitely
		r.runMotor(RXTXRobot.MOTOR1, m1, RXTXRobot.MOTOR2, m2, 0);
		while(!readBumpSensor())
		{
			r.refreshAnalogPins();
            
			if(lineSensor())
				break;
		}
		// Turn off motors
		r.runMotor(RXTXRobot.MOTOR1, 0, RXTXRobot.MOTOR2, 0, 0);
            
		// If the bump sensor was triggered, turn to avoid either an obstacle or a wall
		if(readBumpSensor())
		{
			System.out.println("Bump sensor hit");
			bumpSensorEngaged = false;
			straighten();
			r.runMotor(RXTXRobot.MOTOR1, -m1, RXTXRobot.MOTOR2, -m2, 1000);
			turn(1);
			findLowWell();
		}
		// THE LINE SENSOR WAS TRIGGERED! You're at the well.
		else
		{
			System.out.println("Line sensor triggered");
			approachWell();
		}
	}

	//	Move the robot into appropriate position for remediation to begin.
    //	Because the line sensor was activated when this method is called, we have to
    //	assume that the well is in front of or behind us on our current bearing.
	public void alignToWell()
	{
		System.out.println("Aligning to well...");
        // This method needs to move to it's perpendicular position to put the Ping
        // sensor in position.
		turn(1);
        // Reads for the  base distance from the Ping sensor.
		int base = r.getPing();
		int count = 0;

		r.runMotor(RXTXRobot.MOTOR1, m1, RXTXRobot.MOTOR2, m2, 0);

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
			System.out.println("Found well, approach");
			turn(-1);
			approachWell();
		}
        // In the case that it went too long without reading a different value
        // it calls it self recursively to locate the well behind it's original bearing
		else if(count == 25)
		{
			System.out.println("Make 180 degree turn");
			// Make a 180 degree turn
			turn(1);
			alignToWell();
		}
	}
    
    public void approachWell()
    {
    	System.out.println("Approaching well...");
    	if(courseNumber == 1)
    	{
    		System.out.println("Moving forward for course 1");
	    	this.r.runMotor(RXTXRobot.MOTOR1, -m1, RXTXRobot.MOTOR2, -m2, 2500);
	    	moveForwardWithLineSensor();
	    	this.r.resetEncodedMotorPosition(RXTXRobot.MOTOR1);
	    	this.r.runMotor(RXTXRobot.MOTOR1, m1, RXTXRobot.MOTOR2, m2, 0);
	    	while(this.r.getEncodedMotorPosition(RXTXRobot.MOTOR1) < this.platformDistance - 59942)
	    	{
	    		r.sleep(50);
	    	}
	    	this.r.runMotor(RXTXRobot.MOTOR1, 0, RXTXRobot.MOTOR2, 0, 0);
    	}
    	else if(courseNumber == 2)
    	{
    		System.out.println("Moving forward for course 2");
    		moveForwardWithBumpSensor();
    		straighten();
    	}
    	else if(courseNumber == 3)
    	{
    		System.out.println("Moving forward for course 3");
    		this.r.resetEncodedMotorPosition(RXTXRobot.MOTOR1);
	    	this.r.runMotor(RXTXRobot.MOTOR1, m1, RXTXRobot.MOTOR2, m2, 0);
	    	while(this.r.getEncodedMotorPosition(RXTXRobot.MOTOR1) < this.raisedPlatformDistance - 70000)
	    	{
	    		r.sleep(50);
	    	}
	    	this.r.runMotor(RXTXRobot.MOTOR1, 0, RXTXRobot.MOTOR2, 0, 0);
	    	r.refreshAnalogPins();
	    	if(readBumpSensor())
	    	{
	    		this.r.runMotor(RXTXRobot.MOTOR1, -m1, RXTXRobot.MOTOR2, -m2, 1000);
	    	}

	    	this.r.runMotor(RXTXRobot.MOTOR1, 235, RXTXRobot.MOTOR2, 0, 250);
    	}
    	
    	System.out.println("Arrived at well-- Time to remediate!");
    }

    //Takes robot back to other side of field
	public void goHome()
	{
		System.out.println("Going home...");
		while(bearing != 3)
		{
			turn(1);
		}
		moveForwardWithBumpSensor();
		turn(1);
		moveForwardWithBumpSensor();
		straighten();
		if(courseNumber == 1 || courseNumber == 3)
		{
			turn(1);
			moveForwardWithPingSensor();
			turn(-1);
			r.runMotor(RXTXRobot.MOTOR1, m1, RXTXRobot.MOTOR2, m2, 10000);
		}
		System.out.println("We made it! DRS! DRS! DRS! WOOOOO!");
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

	// Tests if bump sensor has been triggered
	private boolean readBumpSensor()
	{
		if(r.getAnalogPin(2).getValue() == 0)
			return true;

		return false;
	}

	// Runs the robot against whatever surface is pressing against it to straighten it out
	private void straighten()
	{
		r.runMotor(RXTXRobot.MOTOR1, 120, RXTXRobot.MOTOR2, 130, 1500);
	}
    
	// Returns true if after looping tens times, at least three of the values were less than 800 (on white)
	private boolean lineSensor()
	{
		int count = 0;
		// Loop ten times
		for(int i = 0; i < 10; i++)
		{
			// Increment count, line sensor returning low values
			if(r.getAnalogPin(3).getValue() < 750)
				count++;
			// On the white, return true
			if(count >= 3)
				return true;
		}
        
		return false;
	}

	public void moveForwardWithPingSensor()
	{
		r.refreshAnalogPins();
		System.out.println("Moving forward with ping sensor...");
		r.runMotor(RXTXRobot.MOTOR1, m1, RXTXRobot.MOTOR2, m2, 0);
		while(r.getPing() < 40 && !readBumpSensor())
		{
			r.refreshAnalogPins();
		}
		r.runMotor(RXTXRobot.MOTOR1, m1, RXTXRobot.MOTOR2, m2, 2000);
	}

	// Moves forward until bump sensor is triggered
	private void moveForwardWithBumpSensor()
	{
		System.out.println("Moving forward with bump sensor...");
		r.refreshAnalogPins();
		r.runMotor(RXTXRobot.MOTOR1, m1, RXTXRobot.MOTOR2, m2, 0);
		while(!readBumpSensor())
		{
			r.refreshAnalogPins();
		}
		straighten();
		r.runMotor(RXTXRobot.MOTOR1, -m1, RXTXRobot.MOTOR2, -m2, 700);
	}
	private void moveForwardWithLineSensor()
	{
		System.out.println("Moving forward with line sensor...");
		r.runMotor(RXTXRobot.MOTOR1, m1, RXTXRobot.MOTOR2, m2, 0);
		while(!lineSensor())
		{
			r.sleep(50);
		}
		r.runMotor(RXTXRobot.MOTOR1, 01, RXTXRobot.MOTOR2, 0, 0);
	}

}