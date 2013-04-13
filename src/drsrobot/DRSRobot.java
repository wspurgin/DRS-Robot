package drsrobot;
import rxtxrobot.*;
import java.util.*;

public class DRSRobot 
{

	private int courseNumber;
	private RXTXRobot r;
	
//	  DRSRobot from which the robot mechanics are operated.
	public DRSRobot() 
	{
		System.out.println("This is the DRS Robot, please enter your connection port");
		Scanner s = new Scanner(System.in);
		r.setPort(s.nextLine());
		r.setHasEncodedMotors(true);
		r.connect();
	}
	public void run()
	{
		Navigator n = new Navigator(this.r);
		n.setUp();
		n.goToWell();
//		Remediator d = new Remediator(this.r);
//		remediator code to follow
	}
	public void close()
	{
		this.r.close();
	}
	public int getCourseNumber() {
		return courseNumber;
	}
	public void setCourseNumber(int courseNumber) {
		this.courseNumber = courseNumber;
	}
	public RXTXRobot getR() {
		return r;
	}
	public void setR(RXTXRobot r) {
		this.r = r;
	}
}
