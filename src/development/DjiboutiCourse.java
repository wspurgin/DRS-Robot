package development;
public class DjiboutiCourse extends Course
{
/*
this class is an extension of the base class Course. It is designed to 
basically add quantitative values that can be associated to this RFID tag.
That way our eventual Navigator class will have values that it can pull
(if it should need them) in order to assist it in running the Kenya course. 
Measurements are in inches.
*/
	private int wallGap;
	
	public DjiboutiCourse()
	{
		super();
		this.wallGap = 24;
		setCourseName("Dadaab, Kenya");
		setObstacleName("Wall that runs along the center line, and has a " + wallGap + "\" gap in the middle of the wall.");
		setChallenge("Below Ground");
		setCourseNumber(3);
	}
	public String toString()
	{
		String toString = getCourseName() + ":\nwith a " + getObstacleName() + "\nThe challenge is a Well placed at: " + getChallenge() + ".";
		return toString;
	}
}