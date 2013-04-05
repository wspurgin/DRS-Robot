package development;
import java.util.*;

public class DemoHub {

	/**
	 * This class acts as the hub for Demo Day. It will allow for the specific
	 * demo to be chosen and run.
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("This the DemoHub.\nSelect the Number corresponding to the Demo you wish to run\n1. DemoRFID\n2. DemoLine3. DemoSquare\n4. DemoRemediation");
		Scanner s = new Scanner(System.in);
		int choice = s.nextInt();
		
		switch(choice)
		{
			case 1:
				DemoRFID demoRFID = new DemoRFID();
				demoRFID.run();
				demoRFID.close();
				break;
			case 2:
				DemoLine demoLine = new DemoLine();
				demoLine.run();
				demoLine.close();
				break;
			case 3:
				DemoSquare demoSquare = new DemoSquare();
				demoSquare.run();
				demoSquare.close();
				break;
			case 4:
				DemoRemediation  demoRemediation = new DemoRemediation();
				System.out.println("Please enter which part of the Demo do you want.\n1. Move Arm\n2. Test Solution\n3. Remediate Solution");
				int demo = s.nextInt();
				switch(demo)
				{
					case 1:
						demoRemediation.moveSensor();
						System.out.println("Do you want to Remove the Sensor Package?(y,n)");
						String go = s.nextLine();
						if(go.equals("y") || go.equals("Y") || go.equals("\n"))
						{
							demoRemediation.removeSensor();
						}
						else if(go.equals("n") || go.equals("N"))
						{
							System.out.println("Warning sensor Package will not be removed from there current position till demo closes. It will be a violent movement.");
						}
						else
						{
							System.out.println("Command not recognized, Sensors will be removed by default.");
							demoRemediation.removeSensor();
						}
						break;
					case 2:
						System.out.println("Preparing to test.");
						demoRemediation.test();
						break;
					case 3:
						System.out.println("Preparin to remeidate");
						demoRemediation.remediate();
						break;
					default :
						System.out.println("An invalid choice was entered. To avoid errors, the program will close. Please restart this application.");
						break;
				}
				demoRemediation.close();
				break;
			default :
				System.out.println("An invalid choice was entered. To avoid errors, the program will close. Please restart this application.");
		}
		
	}

}
