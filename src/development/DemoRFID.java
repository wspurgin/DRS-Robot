package development;
import rxtxrobot.*;

public class DemoRFID {

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		RXTXRobot r = new RXTXRobot();
		r.setPort("/dev/tty.usbmodem1411");
		r.setVerbose(true);
		r.connect();
		runRobot(r);
	}
	static void runRobot(RXTXRobot r)
	{
		int ticks = calculateTicks();
//		the ticks are chosen arbitrarily.  
		r.runEncodedMotor(RXTXRobot.MOTOR1, 255, ticks, RXTXRobot.MOTOR2, 255, ticks);
	}
	static int calculateTicks()
	{
		int distance = 36;
//		the motors speed range [-255 - 255] just represent binary 
		
		return 0;
	}

}
