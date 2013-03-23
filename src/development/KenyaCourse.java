
public class KenyaCourse extends Course
{
/*
this class is an extension of the base class Course. It is designed to 
basically add quanitative values that can be associated to this RFID tag.
That way our eventual Navigator class will have values that it can pull
(if it should need them) in order to assit it in running the Kenya course. 
Measurements are in inches.
*/
	
	public KenyaCourse()
	{
		super();
		this.courseName = "Dadaab, Kenya";
		this.obstacleName = "Low Bar between 12\" to 20\" high";
		this.challenge = "Above Ground";
	}
	public String toString()
	{
		String toString = this.courseName + ":\n with a " + obstacleName + "\n that runs along the center line,\nThe challenge is a Well placed:" + this.challenge + ".";
		return toString;
	}
}