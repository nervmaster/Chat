package chat;

import java.net.*;
import java.io.*;
import java.util.*;

//class listens for new clients and throws a new ClientConnection for each new user
public class MainServer
{
	static SynchList outputs= new SynchList();

	public static void main(String[] args) throws Exception
	{
		int portno = 5000;
		String dbname = "CHAT";
		String user = "nervmaster";
		String pass = "123456";

		for(int i = 1; i < args.length ; i++)
		{
			switch(i)
			{
				case 1:
				{
					portno = Integer.parseInt(args[0]);
					break;
				}
				case 2:
				{
					dbname = args[1];
					break;
				}
				case 3:
				{
					user = args[2];
					break;
				}
				case 4:
				{
					pass = args[3];
					break;
				}
			}
		}
				

		ServerSocket s = new ServerSocket(portno);
		DBCommunication db = new DBCommunication(dbname,user,pass);
	 	ClientConnection k;

		while(true)
		{
			k = new ClientConnection(s.accept(),outputs,db);
			k.start();
			System.out.println("client joined");
		}
	}
}
