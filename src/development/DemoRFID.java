package development;
import rxtxrobot.*;
import java.util.*;

public class DemoRFID 
{
	// Robot and RFID sensor
	private RXTXRobot r;
	private RFIDSensor s;
    private String mainPort;
    private final String RFID-PORT = "/dev/tty.usbserial-A901JX0L";
	
	// Constructor
	public DemoRFID()
	{
		r = new RXTXRobot();
        System.out.println("Enter main connection port: ");
        Scanner s = new Scanner(System.int);
        mainPort = s.nextLine();
        s.close();
		r.setPort(port);
		r.setHasEncodedMotors(true);
		
		RFIDSensor s = new RFIDSensor();
		s.setPort(DemoRFID.RFID-PORT);
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
