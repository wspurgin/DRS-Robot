package development;
import rxtxrobot.*;
import java.util.*;

public class DemoRFID 
{


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
		r.close();
	}
	static void runRobot(RXTXRobot r)
	{
		RFIDSensor s = new RFIDSensor;
		s.connect();
		while(!(s.hasTag))
		{
			int ticks = 200;
//			the ticks are chosen arbitrarily(for now).  
			r.runEncodedMotor(RXTXRobot.MOTOR1, 255, ticks, RXTXRobot.MOTOR2, 255, ticks);
		}
		String tag = s.getTag;
		s.close;
		int tagNumber = Integer.parseInt(tag)
;		Course course;
		switch(tagNumber) 
		{
			case 1:
				course = new LiberiaCourse();
				break;
			case 2:
				course = new KenyaCourse();
				break;
			case 3:
				course = new DjiboutiCourse();
				break;
			default:
				course = null;
				break;
		}
		System.out.println(course.toString());
	}
}
