package GUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.Color;

/**
 * This class will upload the image to the canvas
 * @author Group 34
 * Last Modified: March 24th 2016
 *
 */
public class UploadImageFrame extends JFrame implements ActionListener{

	private JPanel contentPane;
	private JTextField imageFilePath;
	private JButton btnBack;
	private JButton btnUpload;
	private String filePath;
	private DrawingCanvas theCanvas;
	private JLabel lblThatYouWant;

	/**
	 * Create the frame.
	 * @param drawingCanvas 
	 */
	public UploadImageFrame(DrawingCanvas drawingCanvas) 
	{
		theCanvas = drawingCanvas;
		initialize();
	}
	
	public String getFilePath()
	{
		return filePath;
	}
	
	public void initialize()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 580, 366);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		btnBack = new JButton("Back");
		btnBack.setForeground(Color.WHITE);
		btnBack.setFont(new Font("Letter Gothic Std", Font.PLAIN, 27));
		btnBack.setBackground(Color.DARK_GRAY);
		btnBack.addActionListener(this);
		
		btnUpload = new JButton("Upload");
		btnUpload.setForeground(Color.WHITE);
		btnUpload.setBackground(Color.DARK_GRAY);
		btnUpload.setFont(new Font("Letter Gothic Std", Font.PLAIN, 27));
		btnUpload.addActionListener(this);

		
		imageFilePath = new JTextField();
		imageFilePath.setFont(new Font("Letter Gothic Std", Font.PLAIN, 20));
		imageFilePath.setColumns(10);
		
		JLabel lblEnterTheFile = new JLabel("Enter the file path of the  image source");
		lblEnterTheFile.setFont(new Font("Letter Gothic Std", Font.PLAIN, 20));
		
		lblThatYouWant = new JLabel("that you want to upload to the canvas");
		lblThatYouWant.setFont(new Font("Letter Gothic Std", Font.PLAIN, 20));
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(22)
							.addComponent(lblEnterTheFile))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(37)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(lblThatYouWant)
								.addComponent(imageFilePath, GroupLayout.PREFERRED_SIZE, 462, GroupLayout.PREFERRED_SIZE)))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(90)
							.addComponent(btnUpload, GroupLayout.PREFERRED_SIZE, 154, GroupLayout.PREFERRED_SIZE)
							.addGap(53)
							.addComponent(btnBack, GroupLayout.PREFERRED_SIZE, 145, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(36, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(49)
					.addComponent(lblEnterTheFile, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE)
					.addGap(2)
					.addComponent(lblThatYouWant)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(imageFilePath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnBack)
						.addComponent(btnUpload))
					.addContainerGap(117, Short.MAX_VALUE))
		);
		contentPane.setLayout(gl_contentPane);
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if(e.getSource() == btnBack)
		{
			this.dispose();
		}
		else if(e.getSource() == btnUpload)
		{
			filePath = imageFilePath.getText();
			System.out.println(filePath);
			
			this.dispose();
			
			try {
				theCanvas.Upload(filePath);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
}
