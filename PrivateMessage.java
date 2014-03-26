package chat;


import java.util.*;


public class PrivateMessage extends Message
{
	//to who
	private String userdestiny;

	public PrivateMessage(String from, String to, String text)
	{
		super(from,text);
		userdestiny = to;
	}

	public String getDeliveryName()
	{
		return userdestiny;
	}
}