package drsrobot;
import rxtxrobot.*;
/*
 * This class is responsible for the states from after activation to remediation
 */
public class Navigator
{
	private RXTXRobot r;
	private boolean bumpSensorEngaged;
	private final int NORTH = 90;
	private final int EAST = 0;
	private final int WEST = 180;
	private final int SOUTH = 270;
	
	public Navigator(RXTXRobot r)
	{
		this.r = r;
		this.bumpSensorEngaged = false;
	}
	public void setUp()
	{
		orient(this.NORTH);
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
		r.refreshAnalogPins();
		while(r.readCompass() != direction)
		{
			if(readBumpSensor(1) || readBumpSensor(2))
			{
				bumpSensorEngaged = true;
				break;
			}
			this.r.refreshAnalogPins();
		}
		this.r.runMotor(RXTXRobot.MOTOR1, 0, RXTXRobot.MOTOR2, 0, 0);
		if(this.bumpSensorEngaged)
		{
			this.bumpSensorEngaged = false;
			r.refreshAnalogPins();
			orient(direction);
		}
	}
	public void findRFID()
	{
		
	}
	public void goToWell()
	{
		
	}
	public RXTXRobot getR() {
		return r;
	}
	public void setR(RXTXRobot r) {
		this.r = r;
	}
	public int getNORTH() {
		return NORTH;
	}
	public int getEAST() {
		return EAST;
	}
	public int getWEST() {
		return WEST;
	}
	public int getSOUTH() {
		return SOUTH;
	}
	private void moveForwardWithBumpSensors()
	{
		this.r.runMotor(RXTXRobot.MOTOR1, 255, RXTXRobot.MOTOR2, 255, 0);
		while(!readBumpSensor(1) && !readBumpSensor(2))
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
