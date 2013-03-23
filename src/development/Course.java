import java.util.*;

public abstract class Course
{
	private String courseName;
	private String obstacleName;
	private String challenge;
	
	public Course()
	{
		courseName = "";
		obstacleName = "";
		challenge = "";
	}
  	public abstract String toString();
}