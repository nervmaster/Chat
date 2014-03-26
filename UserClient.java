package chat;


import java.io.*;
import java.util.*;
import java.net.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.*;



public class UserClient implements ActionListener
{
	Socket s;
	int portno = 5001;
	String host = "localhost";

	ObjectInputStream objin;
	ObjectOutputStream objout;

	boolean login = false;
	String name;



	/*Graphical Interface*/
	private JTextField user = new JTextField("user",20);
	private JTextArea server = new JTextArea("server",5,20);
	private JScrollPane sp = new JScrollPane(server);
	private JFrame window = new JFrame("client");
	private DefaultCaret caret = (DefaultCaret) server.getCaret();

	//listening server
	class ServerReader extends Thread
	{

		public void run()
		{
			String s="";
			Message msg;
			try
			{
				while(true)
				{
					msg = (Message) objin.readObject();
					if(!login)
					{
						if(msg.getUsername().equals("Server") && msg.getPlainText().equals("Login Successful"))
						{
							login = true;
						}
					}
					s= s + msg.getUsername() + ":" + msg.getPlainText() + "\n";
					server.setText(s);
				}
			}

			catch(Exception e)
			{
				System.out.println(e);
			}
		}
	}

	//constructor and stablishes connection
	public UserClient(String[] args) throws Exception
	{
		try
		{
			if(args.length == 2)
			{
				host = args[0];
				portno = Integer.parseInt(args[1]);
			}
			s = new Socket(host, portno);
			objout = new ObjectOutputStream(s.getOutputStream());
			objin = new ObjectInputStream(s.getInputStream());
			new ServerReader().start();
		}
		catch(Exception e)
		{
			System.out.println(e);
		}

		window.setSize(300,300);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLayout(new FlowLayout());
		window.add(sp);
		window.add(user);

		user.addActionListener(this);
		window.setVisible(true);

		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	}

	//when user types and press return
	public void actionPerformed(ActionEvent a)
	{
		String s = user.getText();

		String[] w = s.split(" ");

		try
		{
			if(!login)
			{
				if(w[0].equals("!join"))
				{
					if(w.length == 3)
					{
						objout.writeObject(new AdminRights(w[1],w[2])); //try to login a new admin (with password)
						name = "Admin";
					}
					else
					{
						objout.writeObject(new JoinMessage(w[1]));
						name = w[1];
					}
				}
			}
			else //login is made
			{
				if(w[0].charAt(0) == '@') //PM cmd
				{
					String to;
					String message;

					to = w[0].substring(1, w[0].indexOf(":")); //get destinatary
					message = w[0].substring(w[0].indexOf(":")+1); //get message from first word

					for(int i=1;i<w.length;i++)
					{
						message = message + " " + w[i]; //concatenates the content
					}
					objout.writeObject(new PrivateMessage(name, to, message));

				}
				else if (w[0].equals("!who")) //who cmd
				{
					objout.writeObject(new WhoMessage(name));
				}
				else if (w[0].equals("!quit"))
				{
					objout.writeObject(new TerminateMessage(name));
					objout.close();
					objin.close();
					this.s.close();
					System.exit(0);
				}
				else
				{
					objout.writeObject(new Message(name, s));
				}
			}
			user.setText("");
		}
		
		catch(Exception e)
		{
			System.out.println(e);
		}
	}

	public static void main(String[] args) throws Exception
	{
		new UserClient(args);
	}
}