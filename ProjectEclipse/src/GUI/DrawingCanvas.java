package GUI;



import javax.swing.*;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.GroupLayout.Alignment;

import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.BorderLayout;

import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import Network.*;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

public class DrawingCanvas extends JPanel implements ActionListener, ChangeListener {
	
	 // Image that we will draw using the paint.
    private Image image;
    
    private Image imageToUpload;
    // Graphics2D object this is what we will use to draw on
    private Graphics2D drawing;

    //Coordinates of the mouse X/Y old and new coordinates used
    // for drawing the image of the user
    private int oldXCoord;
    private int oldYCoord;
    private int currentXCoord;
    private int currentYCoord;
    private int intSizeOfPen = 5;
    private double doubleSizeOfPen;
    
    private UploadImageFrame uploadFrame;
    String currentDir = "";
    
    private String imageToUploadPath;

	private CanvasClient client;
	private JPanel panel;
	
	//Declaring button variables
	private JButton btnRed;
	private JButton btnBlack;
	private JButton btnGreen;
	private JButton btnYellow;
	private JButton btnBlue;
	private JButton btnMagenta;
	private JButton btnOrange;
	private JButton btnPink;
	private JSlider penSlider;
	private JButton btnEraser;
	private JButton btnClear;
	private JButton btnUpload;
	private JTextField addFriendField;
	private JButton btnAddFriend;
	private JButton btnListFriends;
	private JButton btnBanUser;
	private JTextField textField;
	private JList friendList;
	private DefaultListModel model = new DefaultListModel();
	private List<String> friendsList = new ArrayList<String>();
	
	private JTextArea friendsTextBox;
    private boolean friendsTextBoxFlag = false;
    private JButton btnExport;
    
    // Default Color is black
    private String paintColor = "BLACK";
    private String clientPaintColor;
    
    
	
    /**
     * This method will first initialize the canvas and all the GUI components of the JPanel
     * such as the Buttons, slider,etc. The method will also initialize all the ActionListener and 
     * ChangeListener for all the mouse events and all the action events.
     * @param dstPort
     * @param srcPort
     * @param stringIP
     * @throws Exception 
     */
    public DrawingCanvas(CanvasClient c) 
    {
		this.client = c;
		InitializeCanvas();
		this.client.start();
		
		// Listening for whenever the mouse is pressed. If the mouse is pressed,
        // when pressed we will know where on the canvas the line will start using
        // the get methods.
        //SOURCE: http://www.tutorialspoint.com/awt/awt_mouseadapter.htm
		addMouseListener(new MouseAdapter()
        {
        	// ActionListener for when the user presses the mouse this will get the coordinates of when the user
        	// presses the mouse so that it can be paired with the end point of the line
            public void mousePressed(MouseEvent e)
            {
                // save coord x,y when mouse is pressed
                oldXCoord = e.getX();
                oldYCoord = e.getY();
            }
        });

        // Listening for whenever the mouse is being dragged across the JPanel
        // new cordinates of the image/line will be calculated using the get methods
        // SOURCE: http://www.tutorialspoint.com/awt/awt_mouseadapter.htm
        addMouseMotionListener(new MouseMotionAdapter()
        {
        	// ActionListener for the mouse dragged event. Calculates the the current X and Y coordinates when
        	// the mouse has stop 
            public void mouseDragged(MouseEvent e)
            {
                // coord x,y when drag mouse
                currentXCoord = e.getX();
                currentYCoord = e.getY();

                if (drawing != null)
                {
                    // draw line if drawing context not null
                    drawing.setStroke(new BasicStroke(intSizeOfPen));
                	drawing.drawLine(oldXCoord, oldYCoord, currentXCoord, currentYCoord);
					try
					{
						client.updateCanvas(oldXCoord, oldYCoord, currentXCoord, currentYCoord,getPaintColor(),intSizeOfPen);
					}
					catch(Exception ex)
					{
						//ignore
					}
                    // refresh draw area to repaint
                    repaint();

                    // Storing the old x and y coordinates as the current x and y
                    // coordinates
                    oldXCoord = currentXCoord;
                    oldYCoord = currentYCoord;
					try
					{
						client.updateCanvas(oldXCoord, oldYCoord, currentXCoord, currentYCoord,getPaintColor(),intSizeOfPen);
					}
					catch(Exception ex)
					{
						//ignore
					}
					repaint();
                }
            }
        });

        // Listening for whenever the mouse is being dragged across the JPanel
        // new cordinates of the image/line will be calculated using the get methods
        // SOURCE: http://www.tutorialspoint.com/awt/awt_mouseadapter.htm
        addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent e)
            {
                // coord x,y when drag mouse
                currentXCoord = e.getX();
                currentYCoord = e.getY();

                if (drawing != null)
                {
                    // draw line if drawing context not null
                    drawing.setStroke(new BasicStroke(intSizeOfPen));
                    drawing.drawLine(oldXCoord, oldYCoord, currentXCoord, currentYCoord);
                    // refresh draw area to repaint
                    
                    repaint();

                    // Storing the old x and y coordinates as the current x and y
                    // coordinates
                    oldXCoord = currentXCoord;
                    oldYCoord = currentYCoord;
					try
					{
						client.updateCanvas(oldXCoord, oldYCoord, currentXCoord, currentYCoord,getPaintColor(),intSizeOfPen);
					}
					catch(Exception ex)
					{
						//ignore
					}
                }
            }
        });
    }

	
       

    protected  synchronized void paintComponent(Graphics g)
    {
    	super.paintComponent(g);
        if (image == null)
        {
            // image to draw null initializing the image
            image = createImage(getSize().width, getSize().height);
            drawing = (Graphics2D) image.getGraphics();
            // enable initializing - smoother Lines
            drawing.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // clear draw area

            try {
				NewClear();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        g.drawImage(image, 0, 0, null);
        repaint();
    }

    /**
     * Clear method will clear the whole canvas. This will be implemented with
     * the clear button Date Last Modified: 02/07/2016
     * @throws IOException 
     */
    public void clear() throws IOException
    {
        drawing.setPaint(Color.white);
        // draw white on entire draw area to clear
        drawing.fillRect(0, 0, getSize().width, getSize().height);
        drawing.setPaint(Color.black);
        client.clearCanvas();
        repaint();
    }
    
    /**
     * Clear method will clear the whole canvas. This will be implemented with
     * the clear button Date Last Modified: 02/07/2016
     * @throws IOException 
     */
    public void NewClear() throws IOException
    {
        drawing.setPaint(Color.white);
        // draw white on entire draw area to clear
        drawing.fillRect(0, 0, getSize().width, getSize().height);
        drawing.setPaint(Color.black);
        repaint();
    }
    
    public void clearOther()
    {
        drawing.setPaint(Color.white);
        // draw white on entire draw area to clear
        drawing.fillRect(0, 0, getSize().width, getSize().height);
        drawing.setPaint(Color.black);
        repaint();
    }
    
    public void ChangePenSize(int penSize)
    {
    	doubleSizeOfPen = (penSize)/10;
    	intSizeOfPen = (int) Math.round(doubleSizeOfPen);
    	System.out.println(doubleSizeOfPen);
    }
    
    public void UpdatedLine(int oldX, int oldY, int newX, int newY, String color, int penSize)
    {
    	// Switch statement will determine what color was used for the client side.
    	// After the colors are set the image will painted on the client side.
    	switch(color)
    	{
	    	case "RED":
	    		drawing.setPaint(Color.RED);
	    		break;
	    	case "BLUE":
	    		drawing.setPaint(Color.BLUE);
	    		break;
	    	case "GREEN":
	    		drawing.setPaint(Color.GREEN);
	    		break;
	    	case "YELLOW":
	    		drawing.setPaint(Color.YELLOW);
	    		break;
	    	case "ORANGE":
	    		drawing.setPaint(Color.ORANGE);
	    		break;
	    	case "MAGENTA":
	    		drawing.setPaint(Color.MAGENTA);
	    		break;
	    	case "PINK":
	    		drawing.setPaint(Color.PINK);
	    		break;
	    	case "WHITE":
	    		drawing.setPaint(Color.WHITE);
	    		break;
	    	case "BLACK":
	    		drawing.setPaint(Color.BLACK);
	    		break;
    	}
    	
    	if(image != null)
    	{
	        drawing.setStroke(new BasicStroke(penSize));
	    	drawing.drawLine(oldX, oldY, newX, newY);
	    	repaint();
    	}
    }
    
    /**
     * This method will save the current drawing on the user canvas and then save it in the project directory 
     */
    public void save()
    {

    	BufferedImage bi = new BufferedImage(this.getSize().width,this.getSize().height, BufferedImage.TYPE_INT_ARGB);	
    	drawing = bi.createGraphics();
        drawing.setClip(122, 0, getSize().width, getSize().height);
    	drawing.drawImage(image, 0, 0, null);
        try {
			ImageIO.write(bi, "PNG", new File("CollaborativeDrawingImage.PNG"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        repaint();

    }
    /**
     * This image will take the passed in argument (File path to image) and then uploads the image.
     * @param path
     * @throws IOException 
     */
    public void Upload(String path) throws IOException
    {
    	ImageIcon img = new ImageIcon(path);
		imageToUpload = img.getImage();
		drawing.drawImage(imageToUpload,450,0,null);
		repaint();
		SendUploadImage(imageToUpload);
    }
    
    public void SendUploadImage(Image imageToUpload2) throws IOException
    {
    	client.uploadImage(imageToUpload2);
    }
    

    /**
     * Initializes canvas
     */
    public void InitializeCanvas()
    {
    	panel = new JPanel();
    	panel.setBackground(Color.DARK_GRAY);
    	GroupLayout groupLayout = new GroupLayout(this);
    	groupLayout.setHorizontalGroup(
    		groupLayout.createParallelGroup(Alignment.LEADING)
    			.addGroup(groupLayout.createSequentialGroup()
    				.addComponent(panel, GroupLayout.PREFERRED_SIZE, 253, GroupLayout.PREFERRED_SIZE)
    				.addContainerGap(848, Short.MAX_VALUE))
    	);
    	groupLayout.setVerticalGroup(
    		groupLayout.createParallelGroup(Alignment.LEADING)
    			.addComponent(panel, GroupLayout.DEFAULT_SIZE, 708, Short.MAX_VALUE)
    	);
    	
    	btnBlack = new JButton("");
    	btnBlack.addActionListener(this);
    	btnBlack.setBackground(Color.BLACK);
    	
    	btnRed = new JButton("");
    	btnRed.addActionListener(this);
    	btnRed.setBackground(Color.RED);
    	
    	btnGreen = new JButton("");
    	btnGreen.addActionListener(this);
    	btnGreen.setBackground(Color.GREEN);
    	
    	btnYellow = new JButton("");
    	btnYellow.addActionListener(this);
    	btnYellow.setBackground(Color.YELLOW);
    	
    	btnBlue = new JButton("");
    	btnBlue.addActionListener(this);
    	btnBlue.setBackground(Color.BLUE);
    	
    	btnMagenta = new JButton("");
    	btnMagenta.addActionListener(this);
    	btnMagenta.setBackground(Color.MAGENTA);
    	
    	btnOrange = new JButton("");
    	btnOrange.addActionListener(this);
    	btnOrange.setBackground(Color.ORANGE);
    	
    	btnPink = new JButton("");
    	btnPink.addActionListener(this);
    	btnPink.setBackground(Color.PINK);
    	
    	penSlider = new JSlider();
    	penSlider.addChangeListener(this);
    	
    	JLabel lblPenSize = new JLabel("Pen Size");
    	lblPenSize.setForeground(Color.WHITE);
    	lblPenSize.setFont(new Font("Letter Gothic Std", Font.BOLD, 20));
    	
    	btnEraser = new JButton("");
    	btnEraser.addActionListener(this);
    	btnEraser.setIcon(new ImageIcon("Eraser-512.png"));
    	btnEraser.setFont(new Font("Tahoma", Font.BOLD, 7));
    	
    	btnClear = new JButton("Clear");
    	btnClear.setForeground(Color.WHITE);
    	btnClear.setBackground(Color.LIGHT_GRAY);
    	btnClear.addActionListener(this);
    	btnClear.setFont(new Font("Letter Gothic Std", Font.PLAIN, 17));
    	
    	btnUpload = new JButton("Upload");
    	btnUpload.setForeground(Color.WHITE);
    	btnUpload.setBackground(Color.LIGHT_GRAY);
    	btnUpload.addActionListener(this);
    	btnUpload.setFont(new Font("Letter Gothic Std", Font.PLAIN, 17));
    	
    	addFriendField = new JTextField();
    	addFriendField.setColumns(10);
    	
    	btnAddFriend = new JButton("Add Friend");
    	btnAddFriend.setForeground(Color.WHITE);
    	btnAddFriend.setBackground(Color.LIGHT_GRAY);
    	btnAddFriend.setFont(new Font("Letter Gothic Std", Font.PLAIN, 27));
    	btnAddFriend.addActionListener(this);
    	
    	btnListFriends = new JButton("List Friends:");
    	btnListFriends.setForeground(Color.WHITE);
    	btnListFriends.setFont(new Font("Letter Gothic Std", Font.PLAIN, 27));
    	btnListFriends.setBackground(Color.LIGHT_GRAY);
    	btnListFriends.addActionListener(this);
    	
    	btnBanUser = new JButton("Ban User");
    	btnBanUser.setForeground(Color.WHITE);
    	btnBanUser.setFont(new Font("Letter Gothic Std", Font.PLAIN, 27));
    	btnBanUser.setBackground(Color.LIGHT_GRAY);
    	btnBanUser.addActionListener(this);
    	
    	
    	textField = new JTextField();
    	textField.setColumns(10);
    	
    	friendList = new JList(model);
    	friendList.setVisible(false);
    	
    	btnExport = new JButton("Export ");
    	btnExport.setForeground(Color.WHITE);
    	btnExport.setBackground(Color.LIGHT_GRAY);
    	btnExport.addActionListener(this);
    	btnExport.setFont(new Font("Letter Gothic Std", Font.PLAIN, 17));
    	
    	GroupLayout gl_panel = new GroupLayout(panel);
    	gl_panel.setHorizontalGroup(
    		gl_panel.createParallelGroup(Alignment.LEADING)
    			.addGroup(gl_panel.createSequentialGroup()
    				.addContainerGap()
    				.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
    					.addGroup(gl_panel.createSequentialGroup()
    						.addComponent(btnMagenta, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
    						.addPreferredGap(ComponentPlacement.UNRELATED)
    						.addComponent(btnOrange, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE))
    					.addGroup(gl_panel.createSequentialGroup()
    						.addComponent(btnBlack, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
    						.addGap(35)
    						.addComponent(btnRed, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
    						.addPreferredGap(ComponentPlacement.UNRELATED)
    						.addComponent(btnGreen, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)))
    				.addGap(36)
    				.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
    					.addComponent(btnYellow, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
    					.addComponent(btnPink, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE))
    				.addContainerGap(78, Short.MAX_VALUE))
    			.addGroup(gl_panel.createSequentialGroup()
    				.addComponent(penSlider, GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
    				.addGap(20))
    			.addGroup(gl_panel.createSequentialGroup()
    				.addGap(2)
    				.addComponent(btnListFriends, GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
    				.addContainerGap())
    			.addGroup(gl_panel.createSequentialGroup()
    				.addGap(2)
    				.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
    					.addGroup(gl_panel.createSequentialGroup()
    						.addGap(1)
    						.addComponent(btnBanUser, GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE))
    					.addGroup(gl_panel.createSequentialGroup()
    						.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
    							.addGroup(gl_panel.createSequentialGroup()
    								.addPreferredGap(ComponentPlacement.RELATED)
    								.addComponent(addFriendField, GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE))
    							.addComponent(btnUpload, GroupLayout.PREFERRED_SIZE, 249, Short.MAX_VALUE)
    							.addComponent(btnExport, GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE)
    							.addComponent(btnClear, GroupLayout.PREFERRED_SIZE, 249, Short.MAX_VALUE)
    							.addComponent(btnAddFriend, GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE))
    						.addGap(18)))
    				.addGap(37))
    			.addGroup(gl_panel.createSequentialGroup()
    				.addContainerGap()
    				.addComponent(friendList, GroupLayout.PREFERRED_SIZE, 187, GroupLayout.PREFERRED_SIZE)
    				.addContainerGap(109, Short.MAX_VALUE))
    			.addGroup(gl_panel.createSequentialGroup()
    				.addContainerGap()
    				.addComponent(btnBlue, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
    				.addContainerGap(275, Short.MAX_VALUE))
    			.addGroup(gl_panel.createSequentialGroup()
    				.addComponent(textField, GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
    				.addGap(53))
    			.addGroup(gl_panel.createSequentialGroup()
    				.addComponent(lblPenSize, GroupLayout.PREFERRED_SIZE, 115, GroupLayout.PREFERRED_SIZE)
    				.addContainerGap())
    			.addGroup(gl_panel.createSequentialGroup()
    				.addContainerGap()
    				.addComponent(btnEraser, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
    				.addContainerGap(254, Short.MAX_VALUE))
    	);
    	gl_panel.setVerticalGroup(
    		gl_panel.createParallelGroup(Alignment.LEADING)
    			.addGroup(gl_panel.createSequentialGroup()
    				.addContainerGap()
    				.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
    					.addComponent(btnBlack, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE)
    					.addComponent(btnGreen, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE)
    					.addComponent(btnYellow, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE)
    					.addComponent(btnRed, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE))
    				.addPreferredGap(ComponentPlacement.RELATED)
    				.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
    					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
    						.addComponent(btnPink, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE)
    						.addComponent(btnBlue, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE)
    						.addComponent(btnOrange, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE))
    					.addComponent(btnMagenta, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE))
    				.addGap(18)
    				.addComponent(btnEraser, GroupLayout.PREFERRED_SIZE, 37, GroupLayout.PREFERRED_SIZE)
    				.addGap(56)
    				.addComponent(lblPenSize)
    				.addGap(8)
    				.addComponent(penSlider, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
    				.addPreferredGap(ComponentPlacement.UNRELATED)
    				.addComponent(btnClear)
    				.addPreferredGap(ComponentPlacement.RELATED)
    				.addComponent(btnUpload)
    				.addGap(6)
    				.addComponent(btnExport)
    				.addPreferredGap(ComponentPlacement.UNRELATED)
    				.addComponent(btnAddFriend)
    				.addGap(7)
    				.addComponent(addFriendField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
    				.addGap(18)
    				.addComponent(btnBanUser)
    				.addGap(6)
    				.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
    				.addGap(38)
    				.addComponent(btnListFriends)
    				.addGap(2)
    				.addComponent(friendList, GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE)
    				.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    	);
    	panel.setLayout(gl_panel);
    	setLayout(groupLayout);
    }

	@Override
	/**
	 * Action listeners for all the buttons on the drawing canvas page.
	 */
	public void actionPerformed(ActionEvent e) 
	{
		if(e.getSource() == btnBlack)
		{
			drawing.setPaint(Color.BLACK);
			setColor("BLACK");
		}
		else if(e.getSource()==btnRed)
		{
			drawing.setPaint(Color.RED);
			setColor("RED");
		}
		else if(e.getSource()==btnBlue)
		{
			drawing.setPaint(Color.BLUE);
			setColor("BLUE");
		}
		else if(e.getSource()==btnYellow)
		{
			drawing.setPaint(Color.YELLOW);
			setColor("YELLOW");
		}
		else if(e.getSource()==btnOrange)
		{
			drawing.setPaint(Color.ORANGE);
			setColor("ORANGE");
		}
		else if(e.getSource()==btnMagenta)
		{
			drawing.setPaint(Color.MAGENTA);
			setColor("MAGENTA");
		}
		else if(e.getSource()==btnPink)
		{
			drawing.setPaint(Color.PINK);
			setColor("PINK");
		}
		else if(e.getSource()==btnGreen)
		{
			drawing.setPaint(Color.GREEN);
			setColor("GREEN");
		}
		else if(e.getSource() == btnClear)
		{
			try {
				clear();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		else if(e.getSource() == btnEraser)
		{
			drawing.setPaint(Color.WHITE);
			setColor("WHITE");
		}
		else if(e.getSource() == btnAddFriend)
		{
			try{
				client.addFriend(addFriendField.getText());
			}
			catch(Exception ex)
			{
				//ignore
			}
		}
		else if(e.getSource() == btnListFriends)
		{
			//Only grab list of friends if flag is false, otherwise the list just closes and clears list
			if(friendsTextBoxFlag == false)
			{
				try{
					client.listFriends();
				}
				catch(Exception ex)
				{
					//ignore
				}
				friendsTextBoxFlag = true;
				friendList.setVisible(true);
		    	
			}
			else
			{
		    	friendList.setVisible(false);
		    	friendsTextBoxFlag = false;
				model.clear();
			}

		}
		else if(e.getSource() == btnBanUser)
		{
			System.out.println(textField.getText());
			try{
				client.banUser(textField.getText());
			}
			catch(Exception ex) {
				//ignore
			}
		}
		else if(e.getSource() == btnUpload)
		{
	    	uploadFrame = new UploadImageFrame(this);
	    	uploadFrame.setVisible(true);
		}
		else if(e.getSource() == btnExport)
		{ 
			save();
		}
		
	}

	@Override
	/**
	 * State change for the pen slider on the interface
	 */
	public void stateChanged(ChangeEvent e) 
	{
		JSlider source = (JSlider)e.getSource();
		int sliderNum;
		if(source.getValueIsAdjusting())
		{
			if((int)source.getValue() == 0)
			{
				sliderNum = 10;
				ChangePenSize(sliderNum);
			}
			else
			{	
				sliderNum = (int)source.getValue();
				ChangePenSize(sliderNum);
			}
		}
	}
	
	/**
	 * The method will set the canvas paint color to the appropriate color depending on the button 
	 * pressed.
	 * @param colorToSet
	 */
	public void setColor(String colorToSet)
	{
		paintColor = colorToSet;
	}
	
	/**
	 * Return the current paint color of the canvas.
	 * @return
	 */
	public String getPaintColor()
	{
		return paintColor;
	}

	
	/**
	 * The method will list all the online friends on the canvas screen.
	 * @param list
	 */
	//Populate the friends list array with string from server
	public void listFriends(String list)
	{
		System.out.println(list);
		String[] friendsList = list.split("\\.");
		for(int i = 0; i < friendsList.length; i++)
		{
			model.addElement(friendsList[i]);
		}
	}

	public void closeApplication()
	{
		System.exit(0);
	}

}
