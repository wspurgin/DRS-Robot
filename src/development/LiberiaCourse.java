
public class LiberiaCourse extends Course
{
/*
this class is an extension of the base class Course. It is designed to 
basically add quanitative values that can be associated to this RFID tag.
That way our eventual Navigator class will have values that it can pull
(if it should need them) in order to assit it in running the Liberia course. 
Measurements are in inches.
*/
	private int mazeGap;
	private int mazeWallLength;
	
	public LiberiaCourse()
	{
		super();
		this.mazeGap = 20;
		this.mazeWallLength = 60;
		this.courseName = "Fish Town, Liberia";
		this.obstacleName = "Simple Maze of two walls which are " + mazeWallLength + "\" long, parallel to and " + mazeGap / 2 + "\" away\nfrom the center line on opposite sides of the field.";
		this.challenge = "Ground Level"
	}
	public String toString()
	{
		String toString = this.courseName + ":\n with a " + obstacleName + "\nThe challenge is a Well placed at:" + this.challenge + ".";
		return toString;
	}
}