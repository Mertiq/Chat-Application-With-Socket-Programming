/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Logger;
import Message.Message;
import UI.MainScreen;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 *
 * @author Mert
 */
public class Client {

    public int id;
    public String name = "";
    public Socket socket;
    public static ObjectInputStream streamReader;
    public static ObjectOutputStream streamWriter;
    public Listen listen;
    public MainScreen mainScreen;
    
    
    public Client(String ip, int port,String name){
        listen = new Listen(this);
        this.name = name;
        Start(ip,port);
    }
    
    ///<summary>
    /// Connects client to server and start thread listen.
    /// Sends its name info to server.Opens main screeen. 
    /// Opens main screeen. 
    /// Send a request to server to get online clients.
    ///</summary>
    ///<param name="ip">the ip adress that client will connect</param>
    ///<param name="port">the port that client will connect</param>
    public void Start(String ip, int port) {
        try {
            socket = new Socket(ip, port);
            streamReader = new ObjectInputStream(socket.getInputStream());
            streamWriter = new ObjectOutputStream(socket.getOutputStream());
            listen.start();
            
            System.out.println("Client connected");
            
            Send(new Message(0,Message.Message_Type.Name, this.name));
            mainScreen = new MainScreen();
            mainScreen.setClient(this);
            mainScreen.setVisible(true);
            Send(new Message(0,Message.Message_Type.GetContactsInfo,""));
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    ///<summary>
    /// Sends message to server.
    ///</summary>
    ///<param name="msg">The message that will be sent</param>
    public void Send(Message msg) {
        try {
            streamWriter.writeObject(msg);
            streamWriter.reset();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }   
    
}
class Listen extends Thread {

    Client client;

    public Listen(Client client){
        this.client=client;
    }

    public void run() {
        while (client.socket.isConnected()) {
            try {
                Message receivedMessage = (Message) (client.streamReader.readObject());                 //reads incoming message   
                switch (receivedMessage.type)                                                       
                {
                    case SetClientID:                                                               
                        client.id = Integer.parseInt(receivedMessage.content.toString());               //sets client id
                        break;
                    case GetContactsInfo:
                        client.mainScreen.ShowContacts((ArrayList<FakeClient>)receivedMessage.content); //calls ShowContacts metodh of mainscreeen
                        break;
                    case GetMessagesInfo:
                        client.mainScreen.ShowMessages((ArrayList<Message>) receivedMessage.content);   //calls ShowMessages metodh of mainscreeen
                        break;
                    case SendFile:
                        String home = System.getProperty("user.home");                                  //user home
                        File fx = new File(home + "/Downloads/" + receivedMessage.fileName);            //decide file path      
                        OutputStream os = new FileOutputStream(fx);                                     //
                        byte[] b = (byte[])receivedMessage.content;                                     //
                        os.write(b);                                                                    //write file
                        break;    
                }
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(Listen.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}


