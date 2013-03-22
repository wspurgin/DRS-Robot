import rxtxrobot.*;

public class Robot 
{
	RXTXRobot r;
	Navigator navigator;
	
	public Robot ()
	{
		r = new RXTXRobot();
		navigator = new Navigator ();
	}
	
	public static void run ()
	{
		
	}
	
	public static void main (String[] args)
	{
		run ();
	}
}
