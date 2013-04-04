package development;
import java.util.Scanner;
import rxtxrobot.*;

public class DemoSquare 
{
	private RXTXRobot r;
	private String port;
	private RFIDSensor sensor;
	private final String RFID_PORT = "/dev/tty.usbserial-A901JX0L";
	private int bumpSensorPinNumber;
	
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
		
		System.out.println("Enter bumb sensor Pin number: ");
		this.bumpSensorPinNumber = s.nextInt();
		
		sensor = new RFIDSensor();
		sensor.setPort(RFID_PORT);
		
		s.close();
	}
	
	// Counts number of ticks traveled in a distance (before stopped by RFID tag)
	public int ticksIn()
	{
	    //r.setPort(this.port);
	    r.setHasEncodedMotors(true);
	    r.connect();
	    sensor.connect();
	    
	    r.resetEncodedMotorPosition(RXTXRobot.MOTOR1);
	    
	    r.refreshAnalogPins();
	    AnalogPin bumpSensor = r.getAnalogPin(bumpSensorPinNumber);
	    
	    // Moves forward while no tag has been read
	    r.runMotor(RXTXRobot.MOTOR1, 255, RXTXRobot.MOTOR2, 255, 0);
	    
	    while(!(sensor.hasTag()))
	    {
	    	if(bumpSensor.getValue() != 0)
	    	{
	    		r.runEncodedMotor(RXTXRobot.MOTOR1, -255, 10000, RXTXRobot.MOTOR2, -255, 10000);
	    		r.runMotor(RXTXRobot.MOTOR1, 0, RXTXRobot.MOTOR2, 0, 0);
	    		break;
	    	}
	    	
	    	r.refreshAnalogPins();
	    	r.sleep(200);
	    }
	    
	    r.runMotor(RXTXRobot.MOTOR1, 0, RXTXRobot.MOTOR2, 0, 0);
	    System.out.println(r.getEncodedMotorPosition(RXTXRobot.MOTOR1) + " ticks in 2 feet.");
	    
	    sensor.close();
	    
	    return r.getEncodedMotorPosition(RXTXRobot.MOTOR1);
	 }
	
	// Moves the robot in a square
	public void run()
	{
		r.setPort(this.port);
		r.setHasEncodedMotors(true);
		r.connect();
		
		int ticks = 306313;	// Number of ticks necessary to move 2 feet
		int turns = 2195; // Number of ticks necessary to turn at a 90 degree angle
		
		r.refreshAnalogPins();
		AnalogPin bumpSensor = r.getAnalogPin(bumpSensorPinNumber);
		
		// Square
		for(int i = 0; i < 3; i++)
		{
			r.refreshAnalogPins();
			
			if(bumpSensor.getValue() != 0)
	    	{
				r.runEncodedMotor(RXTXRobot.MOTOR1, -255, 10000, RXTXRobot.MOTOR2, -255, 10000);
	    		r.runMotor(RXTXRobot.MOTOR1, 0, RXTXRobot.MOTOR2, 0, 0);
	    		break;
	    	}
			r.runEncodedMotor(RXTXRobot.MOTOR1, 255, ticks, RXTXRobot.MOTOR2, 255, ticks); // Side
			r.runMotor(RXTXRobot.MOTOR1, -255, RXTXRobot.MOTOR2, 255, turns); // Turn
		}
		
		if(bumpSensor.getValue() == 0)
			r.runEncodedMotor(RXTXRobot.MOTOR1, 255, ticks, RXTXRobot.MOTOR2, 255, ticks); // Side
	}
	
	// Closes robot and sensor
	public void close()
	{
		r.close();
	}
}
