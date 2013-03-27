package development;
import rxtxrobot.*;
import java.util.*;

public class DemoLine {
	
	private RXTXRobot r;
	private String port;
	private int bumpSensorPinNumber;
	
	public DemoLine()
	{
		r = new RXTXRobot();
		System.out.println("Enter connection port: ");
		Scanner s = new Scanner(System.in);
		port = s.nextLine();
		System.out.println("Enter bumb sensor Pin number: ");
		this.bumpSensorPinNumber = s.nextInt();
		s.close();
	}
	public void run()
	{
		r.setPort(this.port);
		r.setHasEncodedMotors(true);
		r.connect();
	 
		r.refreshAnalogPins(); // Cache the Analog pin information 
		AnalogPin bumpSensor = r.getAnalogPin(bumpSensorPinNumber);
//       Will move (some what more slowly than normal because of the loop) until bump sensor is triggered.
        
		while(bumpSensor.getValue() != 0)
		{
			r.runEncodedMotor(RXTXRobot.MOTOR1, 255, 2000, RXTXRobot.MOTOR2, 255, 2000);
			r.refreshAnalogPins();
		}
//       After bump sensor is triggered, Robot will wait two seconds, then back up slightly and make
//        a 90 degree left turn. NOTE: ticks chosen arbitrarily for now.
		r.sleep(2000);
		r.runEncodedMotor(RXTXRobot.MOTOR1, -255, 5000, RXTXRobot.MOTOR2, -255, 5000);
		r.runEncodedMotor(RXTXRobot.MOTOR1, -255, 10000, RXTXRobot.MOTOR2, 255, 10000);
		r.close(); 
	}
}
