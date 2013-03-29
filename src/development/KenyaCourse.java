package development;
public class KenyaCourse extends Course
{
/*
this class is an extension of the base class Course. It is designed to 
basically add quantitative values that can be associated to this RFID tag.
That way our eventual Navigator class will have values that it can pull
(if it should need them) in order to assist it in running the Kenya course. 
Measurements are in inches.
*/
	
	public KenyaCourse()
	{
		super();
		setCourseName("Dadaab, Kenya");
		setObstacleName("Low Bar between 12\" to 20\" high");
		setChallenge("Above Ground");
		setCourseNumber(2);
	}
	public String toString()
	{
		String toString = getCourseName() + ":\nwith a " + getObstacleName() + "\n that runs along the center line,\nThe challenge is a Well placed: " + getChallenge() + ".";
		return toString;
	}
}