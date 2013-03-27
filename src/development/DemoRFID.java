package development;
import rxtxrobot.*;
import java.util.*;

public class DemoRFID 
{
	// Robot and RFID sensor
	private RXTXRobot r;
	private RFIDSensor sensor;
	private String mainPort;
	private final String RFID_PORT = "/dev/tty.usbserial-A901JX0L";
	
	// Constructor
	public DemoRFID()
	{
		r = new RXTXRobot();
        System.out.println("Enter main connection port: ");
        Scanner s = new Scanner(System.in);
        mainPort = s.nextLine();
        s.close();
		r.setPort(mainPort);
		r.setHasEncodedMotors(true);
		
		sensor = new RFIDSensor();
		sensor.setPort(RFID_PORT);
	}
	
	public void run()
	{
		r.connect();
		sensor.connect();

		r.runMotor(RXTXRobot.MOTOR1, 255, RXTXRobot.MOTOR2, 255, ticks);
		while(!(sensor.hasTag()))
		{
			r.sleep(200);
		}
		r.runMotor(RXTXRobot.MOTOR1, 0, RXTXRobot.MOTOR2, 0, ticks);
		String tag = s.getTag();
		s.close();
		String tag = sensor.getTag();
		sensor.close();
		
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
