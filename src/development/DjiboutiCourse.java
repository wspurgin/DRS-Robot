
public class DjiboutCourse extends Course
{
/*
this class is an extension of the base class Course. It is designed to 
basically add quanitative values that can be associated to this RFID tag.
That way our eventual Navigator class will have values that it can pull
(if it should need them) in order to assit it in running the Kenya course. 
Measurements are in inches.
*/
	private int wallGap;
	
	public DjiboutiCourse()
	{
		super();
		this.wallGap = 24;
		this.courseName = "Dadaab, Kenya";
		this.obstacleName = "Wall that runs along the center line, and has a " + wallGap + "\" gap in the middle of the wall.";
		this.challenge = "Below Ground";
	}
	public String toString()
	{
		String toString = this.courseName + ":\n with a " + obstacleName + "\nThe challenge is a Well placed:" + this.challenge + ".";
		return toString;
	}
}