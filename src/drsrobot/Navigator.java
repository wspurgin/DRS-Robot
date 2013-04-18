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
	private final int NORTH = 171;
	private final int EAST = 238;
	private final int WEST = 351;
	private final int SOUTH = 82;
	
	public Navigator(RXTXRobot r)
	{
		this.r = r;
		this.sensor = new RFIDSensor();
		this.sensor.setPort(RFID_PORT);
		this.bumpSensorEngaged = false;
	}
	public void setUp()
	{
		this.orient(this.EAST);
		this.findRFID();
	}
	public void orient(int direction)
	{
        //		Takes priming read of the bump sensors. The Robot will move accordingly to
        //		which bump sensor is engaged. Note that if for some reason the boolean
        //		bumpSensorEngaged is true. The Robot will not turn
		if(!this.bumpSensorEngaged)
			this.r.runMotor(RXTXRobot.MOTOR1, 255, RXTXRobot.MOTOR2, -255, 0);
		else if(this.readBumpSensor())
		{
			this.r.runMotor(RXTXRobot.MOTOR1, -255, RXTXRobot.MOTOR2, -255, 100);
			if(this.readBumpSensor())
				this.r.runMotor(RXTXRobot.MOTOR1, 255, RXTXRobot.MOTOR2, 255, 200);
		}
		else
			this.bumpSensorEngaged = false;
		this.r.refreshAnalogPins();
		while(this.r.readCompass() > direction + 2 || this.r.readCompass() < direction - 2)
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
	public void findRFID()
	{
		this.sensor.connect();
		this.r.runMotor(RXTXRobot.MOTOR1, 255, RXTXRobot.MOTOR2, 255, 0);
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
			int bearing = this.r.readCompass();
			if(bearing == EAST)
				this.orient(SOUTH);
			else
				this.orient(bearing - 90);
			this.findRFID();
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
			this.r.runMotor(RXTXRobot.MOTOR1, 255, RXTXRobot.MOTOR2, 255, 0);
			while(this.r.getPing() < 20 && !this.readBumpSensor())
			{
				this.r.refreshAnalogPins();
			}
			/*
			 *The robot will "travel" a bit as it turns allowing it to move a little forward.
			 *This way (since the Ping sensor is in the middle on the right of the
			 *robot) we will move completely through the gap.
			 */
			this.r.runMotor(RXTXRobot.MOTOR1, 255, RXTXRobot.MOTOR2, 0, 10000);
			orient(this.NORTH);
		}
		else if(this.courseNumber == 2)
		{
			orient(this.NORTH);
		}
		findWell(this.EAST);
	}
	//DO NOT TOUCH> NICKS> DO OBSTCLES SHIT
	public void goHome()
	{
		if(this.courseNumber == 1 || this.courseNumber == 3)
		{
			orient(this.WEST);
			moveForwardWithBumpSensors();
			orient(this.SOUTH);
			moveForwardWithBumpSensors();
			orient(this.EAST);
			this.r.runMotor(RXTXRobot.MOTOR1, 255, RXTXRobot.MOTOR2, 255, 0);
			while(this.r.getPing() < 20 && !this.readBumpSensor())
			{
				this.r.refreshAnalogPins();
			}
			/*
			 *The robot will "travel" a bit as it turns allowing it to move a little forward.
			 *This way (since the Ping sensor is in the middle on the right of the
			 *robot) we will move completely through the gap.
			 */
			this.r.runMotor(RXTXRobot.MOTOR1, 255, RXTXRobot.MOTOR2, 0, 10000);
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
	private void moveForwardWithBumpSensors()
	{
		this.r.runMotor(RXTXRobot.MOTOR1, 255, RXTXRobot.MOTOR2, 255, 0);
		while(!this.readBumpSensor())
		{
			this.r.refreshAnalogPins();
		}
		this.r.runMotor(RXTXRobot.MOTOR1, -255, RXTXRobot.MOTOR2, -255, 200);
	}
	private boolean readBumpSensor()
	{
		boolean engaged = false;
		if(this.r.getAnalogPin(2).getValue() == 0)
			engaged = true;
		
		return engaged;
	}
	private boolean lineSensor()
	{
		int count = 0;
		for(int i = 0; i < 10; i++)
		{
			if(this.r.getAnalogPin(3).getValue() < 800)
				count++;
			
			if(count >= 3)
				return true;
		}
		
		return false;
	}
	private void findWell(int direction)
	{
		boolean lineSensor = false;
		this.orient(direction);
		this.r.runMotor(RXTXRobot.MOTOR1, 255, RXTXRobot.MOTOR2, 255, 10000);
		this.r.runMotor(RXTXRobot.MOTOR1, 255, RXTXRobot.MOTOR2, 255, 0);
		while(this.r.getPing() < 20)
		{
			lineSensor = this.lineSensor();
			if(lineSensor)
				break;
			
			if(this.readBumpSensor())
			{
				this.r.runMotor(RXTXRobot.MOTOR1, -255, RXTXRobot.MOTOR2, -255, 200);
				this.bumpSensorEngaged = true;
				break;
			}
			this.r.refreshAnalogPins();
		}
		this.r.runMotor(RXTXRobot.MOTOR1, 0, RXTXRobot.MOTOR2, 0, 0);
		
		if(this.bumpSensorEngaged)
		{
			this.bumpSensorEngaged = false;
			
			int bearing = this.r.readCompass();
			if(bearing >= SOUTH - 2 && bearing <= SOUTH + 2)
				this.findWell(WEST);
			else if(bearing >= EAST - 2 && bearing <= EAST + 2)
				this.findWell(SOUTH);
			else if(bearing >= NORTH - 2 && bearing <= NORTH + 2)
				this.findWell(EAST);
			else if(bearing >= WEST -2 && bearing <= WEST + 2)
				this.findWell(NORTH);
		}
		else if(!lineSensor)
		{
			int bearing = this.r.readCompass();
			if(bearing >= SOUTH - 2 && bearing <= SOUTH + 2)
				this.findWell(EAST);
			else if(bearing >= EAST - 2 && bearing <= EAST + 2)
				this.findWell(NORTH);
			else if(bearing >= NORTH - 2 && bearing <= NORTH + 2)
				this.findWell(WEST);
			else if(bearing >= WEST -2 && bearing <= WEST + 2)
				this.findWell(SOUTH);
		}
		else
		{
			moveIntoPosition();
		}
	}
	void moveIntoPosition()
	{
		
	}
}