package development;
import rxtxrobot.*;
import java.util.*;

public class DemoRFID 
{
	// Robot and RFID sensor
	RXTXRobot r;
	RFIDSensor s;
	
	// Constructor
	public DemoRFID()
	{
		r = new RXTXRobot();
		r.setPort("/dev/tty.usbmodem1a1221");
		r.setHasEncodedMotors(true);
		
		RFIDSensor s = new RFIDSensor();
		s.setPort("/dev/tty.usbserial-A901JX0L");
	}
	
	public void run()
	{
		r.connect();
		s.connect();
		
		while(!(s.hasTag()))
		{
			int ticks = 200;
//			the ticks are chosen arbitrarily(for now).  
			r.runEncodedMotor(RXTXRobot.MOTOR1, 255, ticks, RXTXRobot.MOTOR2, 255, ticks);
		}
		
		String tag = s.getTag();
		s.close();
		System.out.println(tag);
		
		int tagNumber = 0;
		
		if (tag.equals("67007BB62B81")) 
		{
			tagNumber = 1;
		}
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
				course = new LiberiaCourse();
				break;
		}
		System.out.println(course.toString());
		
		r.close();
	}
}
