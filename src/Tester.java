import rxtxrobot.*;
public class Tester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RXTXRobot r = new RXTXRobot(); // Create RXTXRobot object 
		r.setPort("/dev/tty.usbmodem1421"); // Set the port to COM3 
		r.setVerbose(true); // Turn on debugging messages 
		r.connect(); 
		r.moveServo(RXTXRobot.SERVO1, 30); // Move Servo 1 to location 30  
		r.close(); 
	}

}
