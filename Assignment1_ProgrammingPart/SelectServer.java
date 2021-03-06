/*
 * A simple TCP select server that accepts multiple connections and echo message back to the clients
 * For use in CPSC 441 lectures
 * Instructor: Prof. Mea Wang
 */

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.*;

public class SelectServer {
    public static int BUFFERSIZE = 10000;
    public static void main(String args[]) throws Exception 
    {
        if (args.length != 1)
        {
            System.out.println("Usage: UDPServer <Listening Port>");
            System.exit(1);
        }

        // Initialize buffers and coders for channel receive and send
        String line = "";
        Charset charset = Charset.forName( "us-ascii" );  
        CharsetDecoder decoder = charset.newDecoder();  
        CharsetEncoder encoder = charset.newEncoder();
        ByteBuffer inBuffer = null;
        CharBuffer cBuffer = null;
        int bytesSent, bytesRecv;     // number of bytes sent or received
        
        // Initialize the selector
        Selector selector = Selector.open();

        // Create a tcp server channel and make it non-blocking
        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.configureBlocking(false);
       
        // Get the port number and bind the socket
        InetSocketAddress isa = new InetSocketAddress(Integer.parseInt(args[0]));
        channel.socket().bind(isa);

        // Register that the server selector is interested in connection requests
        channel.register(selector, SelectionKey.OP_ACCEPT);

        //Initialize the UDP Server
        DatagramChannel udpServer = DatagramChannel.open();
        DatagramPacket myPacket = null;
        ByteBuffer udpBuffer = null;

        //Configure the UDP Server
        udpServer.configureBlocking(false);
        udpServer.register(selector, SelectionKey.OP_READ);
        udpServer.socket().bind(isa);



        // Wait for something happen among all registered sockets
        try {
            boolean terminated = false;
            while (!terminated) 
            {
                if (selector.select(500) < 0)
                {
                    System.out.println("select() failed");
                    System.exit(1);
                }

                // Get set of ready sockets
                Set readyKeys = selector.selectedKeys();
                Iterator readyItor = readyKeys.iterator();


                // Walk through the ready set
                while (readyItor.hasNext()) 
                {
                    // Get key from set
                    SelectionKey key = (SelectionKey)readyItor.next();

                    // Remove current entry
                    readyItor.remove();


                    // Accept new connections, if any
                    if (key.isAcceptable() && (key.channel() == channel))
                    {
                        
                        SocketChannel cchannel = ((ServerSocketChannel)key.channel()).accept();
                        cchannel.configureBlocking(false);
                        System.out.println("Accept conncection from " + cchannel.socket().toString());
                        
                        // Register the new connection for read operation
                        cchannel.register(selector, SelectionKey.OP_READ);
                    } 
                    else if(key.channel() == udpServer)
                    {
                        //Allocate the bytebuffer size
                        udpBuffer = ByteBuffer.allocate(BUFFERSIZE);
                        udpBuffer.clear();

                        // Receive message from the UDP client
                        SocketAddress remoteAddr = udpServer.receive(udpBuffer);
                        udpBuffer.flip();
                        //Convert the byte into appropriate charset
                        line = new String(udpBuffer.array(), Charset.forName("UTF-8"));


                        System.out.println("UDPClient: " + line);

                        // Echo message
                        udpServer.send(udpBuffer, remoteAddr);

                    }
                    else 
                    {
                        SocketChannel cchannel = (SocketChannel)key.channel();
                        if (key.isReadable())
                        {
                            Socket socket = cchannel.socket();
                        
                            // Open input and output streams
                            inBuffer = ByteBuffer.allocateDirect(BUFFERSIZE);
                            cBuffer = CharBuffer.allocate(BUFFERSIZE);
                             
                            // Read from socket
                            bytesRecv = cchannel.read(inBuffer);
                            if (bytesRecv <= 0)
                            {
                                System.out.println("read() error, or connection closed");
                                key.cancel();  // deregister the socket
                                continue;
                            }
                             
                            inBuffer.flip();      // make buffer available  
                            decoder.decode(inBuffer, cBuffer, false);
                            cBuffer.flip();
                            line = cBuffer.toString();
                            System.out.print("TCPClient: " + line);
                            
                            if (line.equals("terminate\n"))
                            {
                                terminated = true;
                            }
							else if(line.equalsIgnoreCase("list\n"))
							{
								String workingDirect = System.getProperty("user.dir");
								File folder = new File(workingDirect);
								//Array of all the files in the current directory 
								File[] filesInDirectory = folder.listFiles();
								
								
								ByteBuffer fileBuffer = null;
                                fileBuffer = ByteBuffer.allocate(BUFFERSIZE);
                                String[] fileArray = folder.list();
                                String fileList = "";
                                for(int i = 0; i < fileArray.length; i++)
                                {
                                    fileList +=  fileArray[i] + "\n";
                                }
                                fileList = fileList + "\n";
                                fileBuffer = encoder.encode(CharBuffer.wrap(fileList));
                                bytesSent = cchannel.write(fileBuffer); 
							}

                            // Get file from server and send to client
                            else if(line.length() > 4 && line.substring(0, 3).equalsIgnoreCase("get"))
                            {
                                FileInputStream fis = null;
                                BufferedInputStream bis = null;
                                String workingDirect = System.getProperty("user.dir");
                                File folder = new File(workingDirect);
                                //Array of all the files in the current directory 
                                File[] filesInDirectory = folder.listFiles();
                                String fileName = line.substring(4, line.length()-1);
                                File checkFile = new File(fileName);

                                // If file does not exist, send error message to client
                                if(!checkFile.exists())
                                {
                                    String unknownMessage = "Error in opening file " + fileName + "\n";
                                    ByteBuffer outputBuffer = ByteBuffer.allocate(BUFFERSIZE);
                                    outputBuffer = encoder.encode(CharBuffer.wrap(unknownMessage));
                                    bytesSent = cchannel.write(outputBuffer);
                                }
                                else
                                {
                                    //Start transfer
                                    String startMessage = "start file transfer\n";
                                    ByteBuffer outputBuffer = ByteBuffer.allocate(BUFFERSIZE);
                                    outputBuffer = encoder.encode(CharBuffer.wrap(startMessage));
                                    bytesSent = cchannel.write(outputBuffer);

                                    byte[] fileBuffer = new byte[(int)checkFile.length()];
                                    fis = new FileInputStream(checkFile);
                                    bis = new BufferedInputStream(fis);
                                    bis.read(fileBuffer, 0, fileBuffer.length);
                                    ByteBuffer buf = ByteBuffer.wrap(fileBuffer);
                                    bytesSent = cchannel.write(buf);

                                    // End of file
                                    String fileDoneMessage = "done\n";
                                    outputBuffer = ByteBuffer.allocate(BUFFERSIZE);
                                    outputBuffer = encoder.encode(CharBuffer.wrap(fileDoneMessage));
                                    bytesSent = cchannel.write(outputBuffer);
                                }

                            }
                            // Unknown command from client -- send message back 
                            else
                            {
                                String unknownMessage = "Unknown Command: " + line;
                                ByteBuffer outputBuffer = ByteBuffer.allocate(BUFFERSIZE);
                                outputBuffer = encoder.encode(CharBuffer.wrap(unknownMessage));
                                bytesSent = cchannel.write(outputBuffer);
                            }
                         }
                    }
                } // end of while (readyItor.hasNext()) 
            } // end of while (!terminated)
        }
        catch (IOException e) {
            System.out.println(e);
        }
 

        // close all connections
        Set keys = selector.keys();
        Iterator itr = keys.iterator();
        while (itr.hasNext()) 
        {
            SelectionKey key = (SelectionKey)itr.next();
            //itr.remove();
            if ((key.channel() == channel))
                ((ServerSocketChannel)key.channel()).socket().close();
        }
    }
}
