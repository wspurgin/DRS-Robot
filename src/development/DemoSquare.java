package development;
import java.util.Scanner;
import rxtxrobot.*;

public class DemoSquare 
{
	private RXTXRobot r;
	private String port;
	
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
		
		s.close();
	}
	
	// Moves the robot in a square
	public void run()
	{
		r.setPort(this.port);
		r.setHasEncodedMotors(true);
		r.connect();
		
		int ticks = 306313;	// Number of ticks necessary to move 2 feet
		int turns = 2055; // Number of ticks necessary to turn at a 90 degree angle
		
		// Square
		r.runEncodedMotor(RXTXRobot.MOTOR1, 255, ticks, RXTXRobot.MOTOR2, 255, ticks); // Side
		r.runMotor(RXTXRobot.MOTOR1, -255, RXTXRobot.MOTOR2, 255, turns); // Turn
		r.runEncodedMotor(RXTXRobot.MOTOR1, 255, ticks, RXTXRobot.MOTOR2, 255, ticks); // Side
		r.runMotor(RXTXRobot.MOTOR1, -255, RXTXRobot.MOTOR2, 255, turns); // Turn
		r.runEncodedMotor(RXTXRobot.MOTOR1, 255, ticks, RXTXRobot.MOTOR2, 255, ticks); // Side
		r.runMotor(RXTXRobot.MOTOR1, -255, RXTXRobot.MOTOR2, 255, turns); // Turn
		r.runEncodedMotor(RXTXRobot.MOTOR1, 255, ticks, RXTXRobot.MOTOR2, 255, ticks); // Side
		//r.close();
	}
	
	// Closes robot and sensor
	public void close()
	{
		r.close();
	}
}
