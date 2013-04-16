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
	private final int NORTH = 90;
	private final int EAST = 0;
	private final int WEST = 180;
	private final int SOUTH = 270;
	
	public Navigator(RXTXRobot r)
	{
		this.r = r;
		this.sensor = new RFIDSensor();
		this.sensor.setPort(RFID_PORT);
		this.bumpSensorEngaged = false;
	}
	public void setUp()
	{
		this.orient(this.NORTH);
		this.findRFID();
	}
	public void orient(int direction)
	{
//		Takes priming read of the bump sensors. The Robot will move accordingly to
//		which bump sensor is engaged. Note that if for some reason the boolean
//		bumpSensorEngaged is true. The Robot will not turn
		if(!this.bumpSensorEngaged)
			this.r.runMotor(RXTXRobot.MOTOR1, 255, RXTXRobot.MOTOR2, -255, 0);
		else if(readBumpSensor(1))
			this.r.runMotor(RXTXRobot.MOTOR1, -255, RXTXRobot.MOTOR2, -255, 1000);
		else if(readBumpSensor(2))
			this.r.runMotor(RXTXRobot.MOTOR1, 255, RXTXRobot.MOTOR2, 255, 1000);
		else
			this.bumpSensorEngaged = false;
		this.r.refreshAnalogPins();
		while(this.r.readCompass() != direction)
		{
			if(this.readBumpSensor(1) || this.readBumpSensor(2))
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
			if(this.readBumpSensor(1) || this.readBumpSensor(2))
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
		while(!this.readBumpSensor(1) && !this.readBumpSensor(2))
		{
			this.r.refreshAnalogPins();
		}
		this.r.runMotor(RXTXRobot.MOTOR1, -255, RXTXRobot.MOTOR2, -255, 200);
	}
	private boolean readBumpSensor(int x)
	{
		boolean engaged = false;
		if(x == 1)
		{
			if(this.r.getAnalogPin(3).getValue() == 0)
				engaged = true;
		}
		else if(x == 2)
		{
			if(this.r.getAnalogPin(2).getValue() == 0)
				engaged = true;
		}
		return engaged;
	}
}
