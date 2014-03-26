package chat;


import java.awt.*;
import java.util.*;
import java.io.*;



public class Message implements Serializable
{
	//user identifier
	protected String username;

	//actual content of message
	protected String plaintext;

	//message type; 0 for simple message
	protected int messagetype;


	public Message(String name)
	{
		username = name;
		plaintext = "";
	}

	public Message(String name, String text)
	{
		username = name;
		plaintext = text;
	}

	public void setPlainText(String s)
	{
		plaintext = s;
	}

	public String getUsername()
	{
		return username;
	}	

	public String getPlainText()
	{
		return plaintext;
	}

}


