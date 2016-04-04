package Network;
import GUI.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.List;

public class CanvasClient extends Thread{

	private ApplicationMainScreenPanel mainGUI;
	private ApplicationMainScreen mainScreenFrame;
	private LoginScreenFrame loginGUI;
	private CreateAccountFrame createAccGUI;
	private CreatingCanvas createCanvasGUI;
	private DrawingCanvas canvasGUI;
	private SelectCanvasServer selectGUI;
	private String username;

	private int port = 9000;
	private Socket clientSocket;
	private String currentMachineIP = "";

	private static final String CREATE_ACCOUNT = "0x00";
	private static final String LOGIN_REQUEST = "0x02";
	private static final String CREATE_CANVAS_REQUEST = "0x04";
	private static final String EDIT_CANVAS = "0x11";
	private static final String BAN_REQUEST = "0x13";
	private static final String FRIEND_REQUEST = "0x16";
	private static final String LIST_REQUEST = "0x18";
	private static final String DISCONNECT = "0x20";
	private static final String JOIN_REQUEST = "0x21";
	private static final String UPLOAD_REQUEST = "0x22";
	private static final String CLEAR_REQUEST = "0x23";
	
	private String[] code;
	private LoginScreenFrame f;
	

	public CanvasClient (String IP, String port)
	{
		InetAddress addr = null;
		try {
			addr = InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		currentMachineIP = addr.getHostAddress();
		

		
		try {
			clientSocket = new Socket(IP, Integer.parseInt(port));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

		/*loginGUI = new LoginScreenFrame(this);
		createAccGUI = new CreateAccountFrame(this);
		createCanvasGUI = new CreatingCanvas(this);
		canvasGUI = new DrawingCanvas(this);*/

		mainScreenFrame = new ApplicationMainScreen();
		mainGUI = new ApplicationMainScreenPanel(this,mainScreenFrame);
		Container content = mainScreenFrame.getContentPane();
		content.setLayout(new BorderLayout());

		content.add(mainGUI, BorderLayout.CENTER);
		content.setVisible(true);
		mainScreenFrame.setVisible(true);
	}
	@Override
	public void run()
	{
		while(true)
		{
			try
			{
				BufferedReader inBuffer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				String line = inBuffer.readLine();
				System.out.println("THE LINE:" +line);
				code = line.split("\t");
				if(line.equals(BAN_REQUEST))
				{
					canvasGUI.closeApplication();
					this.close();
				}
				else if(code[0].equals(LIST_REQUEST))
				{
					System.out.println("blah" + code[1]);
					canvasGUI.listFriends(code[1]);
				}
				else if(line.equals(CLEAR_REQUEST))
				{
					System.out.println("Ok WE CLEAR!");
					canvasGUI.clearOther();
				}
				
				canvasGUI.UpdatedLine(Integer.parseInt(code[0]), Integer.parseInt(code[1]), Integer.parseInt(code[2]), Integer.parseInt(code[3]), code[4].toString(),Integer.parseInt(code[5]));
			
				//canvasGUI.listFriends(("hello"));
				//canvasGUI.UpdatedLine(Integer.parseInt(code[0]), Integer.parseInt(code[1]), Integer.parseInt(code[2]), Integer.parseInt(code[3]), code[4].toString(),Integer.parseInt(code[5]));

				//System.out.println("LOLOLOLOL: " + line);
			}
			catch(Exception ex)
			{
				//ignore
			}
		}
	}

	public void close() throws IOException
	{
		clientSocket.close();
	}

	public void createLoginFrame(ApplicationMainScreen frame)
	{
		loginGUI = new LoginScreenFrame(this, frame);
		loginGUI.setVisible(true);
		frame.setVisible(false);
	}
	
	//Send the username and password to the server for validation
	public void loginRequest(String username, String password, LoginScreenFrame frame) throws IOException
	{
		f = frame;
		DataOutputStream outBuffer = new DataOutputStream(clientSocket.getOutputStream()); 
		BufferedReader inBuffer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 
		outBuffer.writeBytes(LOGIN_REQUEST + '\t' + username + '\t' + password);
		
		// Getting response from the server
        String line = inBuffer.readLine();
        System.out.println("Server: " + line);

		//Check the response from the server
        if(line.equals("0"))
		{
			this.username = username;
			loginGUI.successMessage();
			loginGUI.setVisible(false);
			CreatingCanvas createDrawing = new CreatingCanvas(this,f);
			createDrawing.setVisible(true);
		}
		else if(line.equals("1"))
		{
			loginGUI.failMessage();
			System.out.println("invalid credentials");
			//SHOULD OUTPUT AN ERROR MESSAGE
		}
	}

	public void createAccountFrame(ApplicationMainScreen frame)
	{
		createAccGUI = new CreateAccountFrame(this, frame);
		createAccGUI.setVisible(true);
		frame.setVisible(false);
	}

	//send message to server for account creation
	public void createAccount(String username, String password) throws IOException
	{
		DataOutputStream outBuffer = new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader inBuffer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		outBuffer.writeBytes(CREATE_ACCOUNT + '\t' + username + '\t' + password);

		// Getting response from the server
		String line = inBuffer.readLine();
		System.out.println("Server: " + line);

		//Check the response from the server
		if(line.equals("0"))
		{
			mainScreenFrame.setVisible(true);
			createAccGUI.successMessage();
			createAccGUI.dispose();

			//CreatingCanvas createDrawing = new CreatingCanvas();
			//createDrawing.setVisible(true);
		}
		else if(line.equals("1"))
		{
			System.out.println("invalid credentials");
			createAccGUI.createError();
			//createAccGUI.createError();
			//SHOULD OUTPUT AN ERROR MESSAGE
		}
	}

	public void createCanvasRequest() throws Exception
	{
		//Send request to the server
		DataOutputStream outBuffer = new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader inBuffer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		outBuffer.writeBytes(CREATE_CANVAS_REQUEST + '\t' + username + '\t');

		//Getting response from the server
		String line = inBuffer.readLine();

		if(line.equals("0"))
		{
			System.out.println("canvas createdddd");
			DrawingScreenFrame newFrame = new DrawingScreenFrame();

			Container content = newFrame.getContentPane();
			content.setLayout(new BorderLayout());


			try {
				canvasGUI = new DrawingCanvas(this);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			content.add(canvasGUI, BorderLayout.CENTER);
			content.setVisible(true);
			newFrame.setVisible(true);
		}
		//TODO: Add an error message saying that all servers are currently taken
		else if(line.equals("1"))
		{
			System.out.println("ALL SERVERS ARE TAKEN");
			//SHOULD OUTPUT AN ERROR MESSAGE
		}
	}

	public void createJoinFrame(CreatingCanvas frame)
	{
		selectGUI = new SelectCanvasServer(this, frame);
		selectGUI.setVisible(true);
		frame.setVisible(false);
	}

	public void joinRequest(String serverNumber) throws Exception
	{
		//Send request to the server
		DataOutputStream outBuffer = new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader inBuffer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		outBuffer.writeBytes(JOIN_REQUEST + '\t' + serverNumber + '\t' + this.username);

		//Getting response from the server
		String line = inBuffer.readLine();
		System.out.println("Server: " + line);
		if(line.equals("0"))
		{
			selectGUI.dispose();
			System.out.println("joining canvas");
			DrawingScreenFrame newFrame = new DrawingScreenFrame();

			Container content = newFrame.getContentPane();
			content.setLayout(new BorderLayout());


			try {
				canvasGUI = new DrawingCanvas(this);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			content.add(canvasGUI, BorderLayout.CENTER);
			content.setVisible(true);
			newFrame.setVisible(true);
			//TODO: UPDATE REQUEST IMMEDIATELY JOINING TO GET THE CANVAS
		}
		else if(line.equals("1"))
		{
			selectGUI.emptyMessage();
			//TODO: ADD MESSAGE SAYING THAT SERVER IS FULL
			System.out.println("Server is full");
		}
	}

	public void updateCanvas(int oldX, int oldY, int newX, int newY, String color, int penSize) throws Exception
	{
		DataOutputStream outBuffer = new DataOutputStream(clientSocket.getOutputStream());
		outBuffer.write((EDIT_CANVAS + '\t' + this.username + '\t' +oldX + '\t' + oldY + '\t' + newX + '\t' + newY + '\t' + color + '\t' + penSize + '\t').getBytes(Charset.forName("us-ascii")));
		canvasGUI.UpdatedLine(Integer.parseInt(code[0]), Integer.parseInt(code[1]), Integer.parseInt(code[2]), Integer.parseInt(code[3]), code[4].toString(), Integer.parseInt(code[5]));
	}

	//Retrieve a list of friends for the specific client from the server -- will display on the DrawingCanvas
	public void listFriends() throws  Exception
	{
		//Send request to the server
		DataOutputStream outBuffer = new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader inBuffer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		outBuffer.writeBytes(LIST_REQUEST + '\t' + this.username);
	}

	public void listFriendMain() throws  Exception
	{
		//Send request to the server
		DataOutputStream outBuffer = new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader inBuffer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		outBuffer.writeBytes(LIST_REQUEST + '\t' + this.username);

		//Getting response from the server
		String line = inBuffer.readLine();
		System.out.println("Server: " + line);
		createCanvasGUI.listFriends(line);
	}

	//Get the username from textField and pass it to the server to see if
	//user exists, if exists it will send message to the user
	public void banUser(String username) throws Exception
	{
		//Send request to the server
		DataOutputStream outBuffer = new DataOutputStream(clientSocket.getOutputStream());
		outBuffer.writeBytes(BAN_REQUEST + '\t' + this.username + '\t' + username);
	}

	public void addFriend(String username) throws Exception
	{
		//Send request to the server
		DataOutputStream outBuffer = new DataOutputStream(clientSocket.getOutputStream());
		outBuffer.writeBytes(FRIEND_REQUEST + '\t' + this.username + '\t' + username);
		BufferedReader inBuffer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	}

	public void logout() throws Exception
	{
		//Send request to the server
		DataOutputStream outBuffer = new DataOutputStream(clientSocket.getOutputStream());
		outBuffer.writeBytes(DISCONNECT + '\t' + this.username);
	}
	public void uploadImage(Image upImage) throws IOException 
	{
		System.out.println(upImage);
		DataOutputStream outBuffer = new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader inBuffer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		outBuffer.writeBytes(UPLOAD_REQUEST + '\t' + upImage);
	}
	
	public void clearCanvas() throws IOException 
	{
		DataOutputStream outBuffer = new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader inBuffer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		outBuffer.writeBytes(CLEAR_REQUEST);

		
	}


}
