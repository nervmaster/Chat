package chat;

import java.util.*;
import java.io.*;

/*Class that tracks all the clients*/
public class SynchList
{
	ArrayList <ObjectOutputStream> admins;
	Hashtable <String, ObjectOutputStream> users;
	SynchList() 
	{
		admins = new ArrayList <ObjectOutputStream> ();
		users = new Hashtable <String, ObjectOutputStream> ();
	}

	synchronized void add(String s, ObjectOutputStream o)
	{
		users.put(s,o);
	}

	synchronized void addAdmin(ObjectOutputStream o)
	{
		admins.add(o);
	}

	synchronized int size()
	{
		return users.size();
	}

	synchronized ObjectOutputStream getByName(String s)
	{
		return users.get(s);
	}

	synchronized boolean verifyDuplicate(String s)
	{
		return users.containsKey(s);		
	}

	synchronized int adminSize()
	{
		return admins.size();
	}

	synchronized ObjectOutputStream getAdmin( int i)
	{
		return admins.get(i);
	}

	synchronized Collection<ObjectOutputStream> getValues()
	{
		return users.values();
	}

	synchronized Set<String> getClients()
	{
		return users.keySet();
	}

	synchronized void remove(String s)
	{
		users.remove(s);
	}
}
