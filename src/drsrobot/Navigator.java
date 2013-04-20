package drsrobot;
import rxtxrobot.*;
/*
 * This class is responsible for the states from after activation to remediation
 */
public class Navigator
{
	private RXTXRobot r;
	private RFIDSensor sensor;
	private final String RFID_PORT = "/dev/tty.usbserial-A901JX0L";
	private boolean bumpSensorEngaged;
	private int courseNumber;
	private final int NORTH = 199;
	private final int EAST = 101;
	private final int WEST = 281;
	private final int SOUTH = 17;
	
	public Navigator(RXTXRobot r)
	{
		this.r = r;
		this.sensor = new RFIDSensor();
		this.sensor.setPort(RFID_PORT);
		this.sensor.connect();
		this.bumpSensorEngaged = false;
		this.courseNumber = 1;
	}
	public void setUp()
	{
		this.orient(this.EAST);
		this.findRFID(EAST);
	}
	public void orient(int direction)
	{
        //		Takes priming read of the bump sensors. The Robot will move accordingly to
        //		which bump sensor is engaged. Note that if for some reason the boolean
        //		bumpSensorEngaged is true. The Robot will not turn
		if(!this.bumpSensorEngaged)
			this.r.runMotor(RXTXRobot.MOTOR1, 195, RXTXRobot.MOTOR2, -215, 0);
		else if(this.readBumpSensor())
		{
			this.r.runMotor(RXTXRobot.MOTOR1, -235, RXTXRobot.MOTOR2, -255, 100);
			if(this.readBumpSensor())
				this.r.runMotor(RXTXRobot.MOTOR1, 235, RXTXRobot.MOTOR2, 255, 200);
            orient(direction);
		}
		else
			this.bumpSensorEngaged = false;
		this.r.refreshAnalogPins();
		while(this.r.readCompass() > direction + 4 || this.r.readCompass() < direction - 4)
		{
			if(this.readBumpSensor())
			{
				this.bumpSensorEngaged = true;
				break;
			}
			this.r.refreshAnalogPins();
		}
		this.r.runMotor(RXTXRobot.MOTOR1, 0, RXTXRobot.MOTOR2, 0, 0);
		if(this.bumpSensorEngaged)
		{
			this.bumpSensorEngaged = false;
			this.r.refreshAnalogPins();
			this.orient(direction);
		}
	}
    //	This method circles the perimeter of the playing field until the robot locates
    //	the RFID tag and assigns the courseNumber appropriately.
	public void findRFID(int direction)
	{
		System.out.println("Moving to RFID....");
		this.r.runMotor(RXTXRobot.MOTOR1, 235, RXTXRobot.MOTOR2, 255, 0);
		while(!this.sensor.hasTag())
		{
			if(this.readBumpSensor())
			{
				this.bumpSensorEngaged = true;
				break;
			}
			this.r.refreshAnalogPins();
		}
		this.r.runMotor(RXTXRobot.MOTOR1, 0, RXTXRobot.MOTOR2, 0, 0);
		if(this.bumpSensorEngaged)
		{
			this.bumpSensorEngaged = false;
			this.r.refreshAnalogPins();
			if(direction >= SOUTH - 4 && direction <= SOUTH + 4)
			{
				System.out.println("Change bearing: WEST");
				direction = WEST;
				orient(WEST);
			}
			else if(direction >= EAST - 4 && direction <= EAST + 4)
			{
				System.out.println("Change bearing: SOUTH");
				direction = SOUTH;
				orient(SOUTH);
			}
			else if(direction >= NORTH - 4 && direction <= NORTH + 4)
			{
				System.out.println("Change bearing: EAST");
				direction = SOUTH;
				orient(EAST);
			}
			else if(direction >= WEST - 4 && direction <= WEST + 4)
			{
				System.out.println("Change bearing: NORTH");
				direction = NORTH;
				orient(NORTH);
			}
			
			this.findRFID(direction);
		}
		String tag = this.sensor.getTag();
		if(tag.equals("6A003E4EA6BC"))
			this.courseNumber = 1;
		else if(tag.equals("6A003E834B9C"))
			this.courseNumber = 2;
		else if(tag.equals("6A003E6E477D"))
			this.courseNumber = 3;
		else
			System.out.println("ERROR: Invalid RFID tag");
		this.sensor.close();
	}
	public void goToWell()
	{
		if(this.courseNumber == 1 || this.courseNumber == 3)
		{
			orient(this.EAST);
			moveForwardWithBumpSensors();
			orient(this.NORTH);
			moveForwardWithBumpSensors();
			orient(this.WEST);
			this.r.runMotor(RXTXRobot.MOTOR1, 235, RXTXRobot.MOTOR2, 255, 0);
			while(this.r.getPing() < 20 && !this.readBumpSensor())
			{
				this.r.refreshAnalogPins();
			}
			/*
			 *The robot will "travel" a bit as it turns allowing it to move a little forward.
			 *This way (since the Ping sensor is in the middle on the right of the
			 *robot) we will move completely through the gap.
			 */
			this.r.runMotor(RXTXRobot.MOTOR1, 235, RXTXRobot.MOTOR2, 0, 10000);
		}
        //		Course number 2 only needs the following code, since it has no obstacle to navigate around. i.e it has no particular
        //		condition that would require its own steps.
		
		orient(this.EAST);
		moveForwardWithBumpSensors();
		findWell(this.NORTH);
	}
	// Takes robot back to other side of field
	public void goHome()
	{
		if(this.courseNumber == 1 || this.courseNumber == 3)
		{
			orient(this.WEST);
			moveForwardWithBumpSensors();
			orient(this.SOUTH);
			moveForwardWithBumpSensors();
			orient(this.EAST);
			this.r.runMotor(RXTXRobot.MOTOR1, 235, RXTXRobot.MOTOR2, 255, 0);
			while(this.r.getPing() < 20 && !this.readBumpSensor())
			{
				this.r.refreshAnalogPins();
			}
			/*
			 *The robot will "travel" a bit as it turns allowing it to move a little forward.
			 *This way (since the Ping sensor is in the middle on the right of the
			 *robot) we will move completely through the gap.
			 */
			this.r.runMotor(RXTXRobot.MOTOR1, 235, RXTXRobot.MOTOR2, 0, 10000);
			orient(this.SOUTH);
		}
		else if(this.courseNumber == 2)
		{
			orient(this.SOUTH);
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
	public int getNORTH()
	{
		return NORTH;
	}
	public int getEAST()
	{
		return EAST;
	}
	public int getWEST()
	{
		return WEST;
	}
	public int getSOUTH()
	{
		return SOUTH;
	}
	public int getCourseNumber()
	{
		return this.courseNumber;
	}
	public void moveForwardWithBumpSensors()
	{
		this.r.refreshAnalogPins();
		this.r.runMotor(RXTXRobot.MOTOR1, 235, RXTXRobot.MOTOR2, 255, 0);
		while(!this.readBumpSensor())
		{
			this.r.refreshAnalogPins();
		}
		this.r.runMotor(RXTXRobot.MOTOR1, -235, RXTXRobot.MOTOR2, -255, 1000);
	}
	public boolean readBumpSensor()
	{
		boolean engaged = false;
		if(this.r.getAnalogPin(2).getValue() == 0)
			engaged = true;
		
		return engaged;
	}
    //	Returns true if after looping tens times, at least three of the values were less than 800 (on white)
	private boolean lineSensor()
	{
		int count = 0;
        //		Loop ten times
		for(int i = 0; i < 10; i++)
		{
            //			Increment count, line sensor returning low values
			if(this.r.getAnalogPin(3).getValue() < 800)
				count++;
            //			On the white, return true
			if(count >= 3)
				return true;
		}
		
		return false;
	}
    //	Recursively calls to follow the wall and avoid obstacles until line sensor senses white line
	private void findWell(int direction)
	{
		boolean lineSensor = false;
		this.orient(direction);
        //		Run forward unconditionally
		this.r.runMotor(RXTXRobot.MOTOR1, 235, RXTXRobot.MOTOR2, 255, 10000);
        //		Run motors indefinitely
		this.r.runMotor(RXTXRobot.MOTOR1, 235, RXTXRobot.MOTOR2, 255, 0);
        //		Loop while close to the wall, break if either line sensor or bump sensors are triggered
		while(this.r.getPing() < 20)
		{
			lineSensor = this.lineSensor();
            //			Break if line sensor trigged
			if(lineSensor)
				break;
			
            //			Back up and break if bump sensor triggered
			if(this.readBumpSensor())
			{
				this.r.runMotor(RXTXRobot.MOTOR1, -235, RXTXRobot.MOTOR2, -255, 2000);
				this.bumpSensorEngaged = true;
				break;
			}
			this.r.refreshAnalogPins();
		}
        //		Turn off motors
		this.r.runMotor(RXTXRobot.MOTOR1, 0, RXTXRobot.MOTOR2, 0, 0);
		
        //		If the bump sensor was triggered, turn to avoid either an obstacle or a wall
		if(this.bumpSensorEngaged)
		{
			this.bumpSensorEngaged = false;
			
			int bearing = this.r.readCompass();
			if(bearing >= SOUTH - 4 && bearing <= SOUTH + 4)
				this.findWell(WEST);
			else if(bearing >= EAST - 4 && bearing <= EAST + 4)
				this.findWell(SOUTH);
			else if(bearing >= NORTH - 4 && bearing <= NORTH + 4)
				this.findWell(EAST);
			else if(bearing >= WEST - 4 && bearing <= WEST + 4)
				this.findWell(NORTH);
		}
        //		If the bump sensor wasn't triggered and line sensor wasn't triggered, the ping over-distanced
		else if(!lineSensor)
		{
            //			Turn back from whence you came
			int bearing = this.r.readCompass();
			if(bearing >= SOUTH - 4 && bearing <= SOUTH + 4)
				this.findWell(EAST);
			else if(bearing >= EAST - 4 && bearing <= EAST + 4)
				this.findWell(NORTH);
			else if(bearing >= NORTH - 4 && bearing <= NORTH + 4)
				this.findWell(EAST);
			else if(bearing >= WEST - 4 && bearing <= WEST + 4)
				this.findWell(SOUTH);
		}
        //		THE LINE SENSOR WAS TRIGGERED! You're at the well.
		else
		{
			moveIntoPosition(this.r.readCompass());
		}
	}
    //	Move the robot into appropriate position for remediation to begin.
    //	Because the line sensor was activated when this method is called, we have to
    //	assume that the well is in front of or behind us on our current bearing.
	private void moveIntoPosition(int bearing)
	{
        //		this method needs to move to it's perpendicular position to put the Ping
        //		sensor in position.
		if(bearing >= SOUTH - 4 && bearing <= SOUTH + 4)
			orient(EAST);
		else if(bearing >= EAST - 4 && bearing <= EAST + 4)
			orient(NORTH);
		else if(bearing >= NORTH - 4 && bearing <= NORTH + 4)
			orient(WEST);
		else if(bearing >= WEST - 4 && bearing <= WEST + 4)
			orient(SOUTH);
        //		reads for the  base distance from the Ping sensor.
		int base = this.r.getPing();
		int count = 0;
		this.r.runMotor(RXTXRobot.MOTOR1, 235, RXTXRobot.MOTOR2, 255, 0);
        //		if the Ping sensor reads a significant spike in distances, we know that it returned
        //		the distance to the well. Otherwise, it has gone too far (should run for approx. 3 sec)
		while((this.r.getPing() >= base - 2 && this.r.getPing() <= base + 2) && count != 25)
		{
			r.sleep(100);
			count++;
		}
		this.r.runMotor(RXTXRobot.MOTOR1, 0, RXTXRobot.MOTOR2, 0, 0);
        //		in the case that the Ping sensor had been engaged we move towards the object which
        //		should be the well.
		if(!(this.r.getPing() >= base -2 && this.r.getPing() <= base + 2))
		{
			if(bearing >= SOUTH - 4 && bearing <= SOUTH + 4)
				orient(SOUTH);
			else if(bearing >= EAST - 4 && bearing <= EAST + 4)
				orient(EAST);
			else if(bearing >= NORTH - 4 && bearing <= NORTH + 4)
				orient(NORTH);
			else if(bearing >= WEST - 4 && bearing <= WEST + 4)
				orient(WEST);
			moveForwardWithBumpSensors();
		}
        //		in the case that it went too long without reading a different value
        //		it calls it self recursively to locate the well behind it's original bearing
		else if(count == 25)
		{
			if(bearing >= SOUTH - 4 && bearing <= SOUTH + 4)
				moveIntoPosition(NORTH);
			else if(bearing >= EAST - 4 && bearing <= EAST + 4)
				moveIntoPosition(WEST);
			else if(bearing >= NORTH - 4 && bearing <= NORTH + 4)
				moveIntoPosition(SOUTH);
			else if(bearing >= WEST - 4 && bearing <= WEST + 4)
				moveIntoPosition(EAST);
		}
	}
}