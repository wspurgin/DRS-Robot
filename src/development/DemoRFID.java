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
	private String Liberia = "6A003E4EA6BC";
	private String Kenya = "6A003E834B9C";
	private String Djibouti = "6A003E6E477D";
	
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

		int ticks = 0;

		r.runMotor(RXTXRobot.MOTOR1, 255, RXTXRobot.MOTOR2, 255, ticks);
		while(!(sensor.hasTag()))
		{
			r.sleep(200);
		}
		r.runMotor(RXTXRobot.MOTOR1, 0, RXTXRobot.MOTOR2, 0, ticks);
		String tag = sensor.getTag();
		sensor.close();
		tag = sensor.getTag();
		sensor.close();
		
		System.out.println(tag);
		
		int tagNumber = 0;
		
		if (tag.equals("67007BB62B81")) 
		{
			tagNumber = 1;
		}
		Course course = null;
		
		if(tag.equals(Liberia))
			course = new LiberiaCourse();
		else if(tag.equals(Kenya))
			course = new KenyaCourse();
		else if(tag.equals(Djibouti))
			course = new DjiboutiCourse();

		else 
			System.out.println("Error: RFID tag not recognized");
		
		/*
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
		*/
		
		System.out.println(course.toString());
		
		r.close();
	}
	
	public void close()
	{
		r.moveBothServos(145, 0);
		r.close();
	}
}
