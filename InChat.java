package com.codepuran.inChatApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

/**
 * @author Akshay Pethani
 *
 */
public class InChat {

  public static final int listeningPort = 3000;

  /**
   * @param args
   */
  public static void main(String args[]) {

    Scanner sc = new Scanner(System.in);

    System.out.println("Enter Ip: ");
    String serverIp = sc.nextLine();
    System.out.println("Enter Port No: ");
    int serverPort = sc.nextInt();

    //Thread for sending message.
    Thread sender = new Thread(new Runnable() {
      @Override
      public void run(){
        try {
          ServerSocket sersock = null;
          Socket sock = null;
          OutputStream ostream = null;
          BufferedReader keyRead = new BufferedReader(new InputStreamReader(System.in));
          PrintWriter pwrite = null;
          String sendMessage="Error";
          sersock = new ServerSocket(listeningPort);
          sock = sersock.accept();
          sock.setSoTimeout(0);
          ostream = sock.getOutputStream();
          pwrite = new PrintWriter(ostream, true);
          System.out.println("Connection Established. You Can Chat Now.");
          while(true)
          {
            sendMessage = keyRead.readLine();
            if(sendMessage.equals("pquit")) {
              pwrite.println(sendMessage);
              System.out.println("You are leaving the chat.");
              System.exit(0);
              pwrite.flush();
              sc.close();
              sersock.close();
              break;
            }
            pwrite.println(sendMessage);
            pwrite.flush();
          }
        }catch(Exception e) {
          System.out.println("Exception In Sender");
          e.printStackTrace();
        }
      }
    });

    //Thread for receiving the message.
    Thread receiver= new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          Socket sock= new Socket();
          sock.connect(new InetSocketAddress(serverIp, serverPort),999999);
          InputStream istream = sock.getInputStream();
          BufferedReader receiveRead = new BufferedReader(new InputStreamReader(istream));
          String receiveMessage;
          while(true)
          {
            if((receiveMessage = receiveRead.readLine()) != null) //receive from server
            {
              if(receiveMessage.equals("pquit")) {
                System.out.println("Your Opponent Has Left the chat.");
                System.exit(0);
                sc.close();
                sock.close();
                break;
              }
              System.out.println(receiveMessage); // displaying sender's message
            }
          }
        }catch (ConnectException e) {
          //Reconnect in case of the timeout.
          System.out.println("Trying to reconnect the system.");
          run();
        }catch(SocketException e) {
          //Exit the program if the opponent left the chat.
          System.out.println("Your Opponent has been left the chat unexpctedly.");
          System.exit(0);
        }catch (IOException e) {
          System.out.println("Exception In Io.");
          //e.printStackTrace();
        }
        catch (Exception e) {
          System.out.println("Exception In Receiver");
          //e.printStackTrace();
        }
      }
    });
    sender.start();
    receiver.start();
  }
}
