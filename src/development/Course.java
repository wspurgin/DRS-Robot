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
	
	public void setCourseName(String s)
	{
		courseName = s;
	}
	
	public void setObstacleName(String s)
	{
		obstacleName = s;
	}
	
	public void setChallenge(String s)
	{
		challenge = s;
	}
	
	public String getCourseName()
	{
		return courseName;
	}
	
	public String getObstacleName()
	{
		return obstacleName;
	}
	
	public String getChallenge()
	{
		return challenge;
	}
	
  	public abstract String toString();
}