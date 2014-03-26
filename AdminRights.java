package chat;


import java.util.*;


public class AdminRights extends Message
{
	private String password;

	public AdminRights(String name, String pass)
	{
		super(name);
		password = pass;
	}

	public String getPassword()
	{
		return password;
	}

}