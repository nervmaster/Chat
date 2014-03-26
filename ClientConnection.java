package chat;


import java.util.*;
import java.io.*;
import java.net.*;



//deals with the clients
class ClientConnection extends Thread
{
	SynchList outputs;
	Socket s;
	ObjectInputStream objin;
	ObjectOutputStream objout;
	String clientname;
	DBCommunication db;


	public ClientConnection(Socket s, SynchList v, DBCommunication db) throws Exception
	{
		outputs = v;
		this.s = s;
		objin = new ObjectInputStream(s.getInputStream());
		objout = new ObjectOutputStream(s.getOutputStream());
		this.db = db;
	}


	//to approve a new client username
	//it must be unique
	private void approvalLoop() throws Exception
	{
		Object c;
		JoinMessage jm;
		boolean login = false;
		while(!login )
		{
			c = objin.readObject();
			if(c instanceof JoinMessage)
			{
				jm = (JoinMessage) c;
				clientname = jm.getUsername();

				if(!outputs.verifyDuplicate(jm.getUsername()) && !jm.getUsername().equals("Server")) 
				{
					outputs.add(clientname, objout);
					login = true;
					objout.writeObject(new Message("Server", "Login Successful"));
				}
				else
				{
					objout.writeObject(new Message("Server","Username already taken"));
				}
			}
			else if(c instanceof AdminRights)
			{
				AdminRights adm = (AdminRights) c;
				if(adm.getUsername().equals("Admin") && adm.getPassword().equals("123456"))
				{
					outputs.addAdmin(objout);
					clientname = "Admin";
					login = true;
					objout.writeObject(new Message("Server","Login Successful"));

				}
				else
				{
					objout.writeObject(new Message("Server","Wrong Password"));
				}
			}
			broadcast(new Message("Server",clientname + " joined "));
		}
	}


	//broadcast message
	private void broadcast(Message m) throws Exception
	{
		for (ObjectOutputStream client : outputs.getValues())
		{
			client.writeObject(m);
		}
		sendToAdmin(m);
	}

	//send private message
	private void sendPM(PrivateMessage pm) throws Exception
	{
		//user1 : To: User2 : messagecontenthere	
		sendToAdmin(new Message(clientname, " To: " + pm.getDeliveryName() + " : " + pm.getPlainText() ));

		pm.setPlainText( " PM : " + pm.getPlainText());
		objout.writeObject(new Message(clientname, pm.getPlainText() ));
		if(outputs.verifyDuplicate(pm.getDeliveryName())) //verify if destinatary exists
		{
			//User1 : PM : messagecontent
			( outputs.getByName(pm.getDeliveryName()) ).writeObject( new Message(clientname, pm.getPlainText()) );
		}
		else
		{
			objout.writeObject(new Message("Server","Failed to delivery"));
		}
	}

	private void sendWHO(WhoMessage m) throws Exception
	{
		for(String s : outputs.getClients() )
		{
			objout.writeObject(new Message("Server", s));
		}
		sendToAdmin(new Message("Server", clientname + " used a WHO command"));
	}

	private void sendToAdmin(Message m) throws Exception
	{
		for(int i=0;i<outputs.adminSize();i++)
		{
				(outputs.getAdmin(i)).writeObject(m);
		}
		db.writeMessage(m);
	}

	private void terminateConnection() throws Exception
	{
		s.close();
		System.out.println(clientname + " left server "); // client ended
		broadcast(new Message("Server", clientname + " left chat ")); //broadcast that client ended
		outputs.remove(clientname);
	}




	public void run()
	{
		Object c;
		boolean clientquit = false;
		try
		{
			approvalLoop();
			while(!clientquit)
			{
				c = objin.readObject();
				if(c instanceof JoinMessage || c instanceof AdminRights)
				{
					//do nothing;
				}
				else if(c instanceof PrivateMessage)
				{
					sendPM((PrivateMessage) c);
				}
				else if(c instanceof WhoMessage)
				{
					sendWHO((WhoMessage) c);
				}
				else if(c instanceof TerminateMessage) //terminates connection;
				{
					terminateConnection();
					clientquit = true;
				}
				else //simple message
				{
					broadcast((Message) c);
				}
			}
		}
		catch(Exception e)
		{
			//System.out.print(e);
		}
	}
}