/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author Andrew Dong
 */
public class DrawingScreen extends javax.swing.JFrame {

    // Initializing Variables
    
    // The drawing of the user
    private Image image;
    
    // Variable for the drawing Will get updated everytime a 
    // mouseAction is recoreded.
    private Graphics2D drawing;
    
    // Corrdinates for the current and old X/Y coordinates - used to calculate the
    // drawing.
    private int currentXCoord;
    private int currentYCoord;
    private int oldXCoord;
    private int oldYCoord;
    
    
    /**
     * Creates new form DrawingScreen
     */
    public DrawingScreen() {
        // Intializing the intial drawing application
        // this is handle by netBeans design feature. DO NOT
        // touch any code in the initComponents method!!!
        initComponents();
        //setDoubleBuffered(false);

    }
    
    protected void paintComponent(Graphics g)
    {
        if(image == null)
        {
            image = createImage(getSize().width, getSize().height);
            drawing = (Graphics2D) image.getGraphics();
            drawing.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON );
            clearCanvas();
        }
        g.drawImage(image, 0, 0, null);
    }
    
    private void clearCanvas() 
    {
        drawing.setPaint(Color.white);
        drawing.fillRect(0, 0, getSize().width, getSize().height);
        drawing.setPaint(Color.black);
        repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     * This will contain all the initialize element of our canvas
     * @Author: Group 34
     * DATE LAST MODIFIED: 02/07/2016
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        redButton = new javax.swing.JButton();
        blueButton = new javax.swing.JButton();
        greenButton = new javax.swing.JButton();
        clearButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("CPSC441 - GROUP 34 - Collaborative Drawing");

        redButton.setText("RED");

        blueButton.setText("BLUE");

        greenButton.setText("GREEN");

        clearButton.setText("CLEAR");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 860, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 432, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(redButton)
                .addGap(26, 26, 26)
                .addComponent(blueButton)
                .addGap(18, 18, 18)
                .addComponent(greenButton)
                .addGap(28, 28, 28)
                .addComponent(clearButton)
                .addContainerGap(516, Short.MAX_VALUE))
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(redButton)
                    .addComponent(blueButton)
                    .addComponent(greenButton)
                    .addComponent(clearButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton blueButton;
    private javax.swing.JButton clearButton;
    private javax.swing.JButton greenButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton redButton;
    // End of variables declaration//GEN-END:variables


}
