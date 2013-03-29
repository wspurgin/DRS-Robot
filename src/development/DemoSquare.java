package development;
import java.util.Scanner;
import rxtxrobot.*;

public class DemoSquare 
{
	private RXTXRobot r;
	private String port;
	private RFIDSensor sensor;
	private final String RFID_PORT = "/dev/tty.usbserial-A901JX0L";
	
	// Constructor
	public DemoSquare()
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
		
		sensor = new RFIDSensor();
		sensor.setPort(RFID_PORT);
		
		s.close();
	}
	
	// Counts number of ticks traveled in a distance (before stopped by RFID tag)
	public int ticksIn()
	{
		r.setPort(this.port);
		r.setHasEncodedMotors(true);
		r.connect();
		sensor.connect();
		
		int count = 0;
		
		r.resetEncodedMotorPosition(RXTXRobot.MOTOR1);
		
		// Moves forward while no tag has been read
		r.runMotor(RXTXRobot.MOTOR1, -255, RXTXRobot.MOTOR2, -255, 0);
		
		while(!(sensor.hasTag()))
		{
			r.sleep(200);
			count++;
		}
		
		r.runMotor(RXTXRobot.MOTOR1, 0, RXTXRobot.MOTOR2, 0, 0);
		
		System.out.println(r.getEncodedMotorPosition(RXTXRobot.MOTOR1) + " ticks in 2 feet.");
		return r.getEncodedMotorPosition(RXTXRobot.MOTOR1);
	}
	
	// Moves the robot in a square
	public void run()
	{
		r.setPort(this.port);
		r.setHasEncodedMotors(true);
		r.connect();
		
		int ticks = 5000;	// Number of ticks necessary to move 2 feet
		int turns = 10000; // Number of ticks necessary to turn at a 90 degree angle
		
		// Square
		r.runEncodedMotor(RXTXRobot.MOTOR1, -255, ticks, RXTXRobot.MOTOR2, -255, ticks); // Side
		r.runEncodedMotor(RXTXRobot.MOTOR1, -255, turns, RXTXRobot.MOTOR2, 255, turns); // Turn
		r.runEncodedMotor(RXTXRobot.MOTOR1, -255, ticks, RXTXRobot.MOTOR2, -255, ticks); // Side
		r.runEncodedMotor(RXTXRobot.MOTOR1, -255, turns, RXTXRobot.MOTOR2, 255, turns); // Turn
		r.runEncodedMotor(RXTXRobot.MOTOR1, -255, ticks, RXTXRobot.MOTOR2, -255, ticks); // Side
		r.runEncodedMotor(RXTXRobot.MOTOR1, -255, turns, RXTXRobot.MOTOR2, 255, turns); // Turn
		r.runEncodedMotor(RXTXRobot.MOTOR1, -255, ticks, RXTXRobot.MOTOR2, -255, ticks); // Side
	}
	
	// Closes robot and sensor
	public void close()
	{
		sensor.close();
		r.close();
	}
}
