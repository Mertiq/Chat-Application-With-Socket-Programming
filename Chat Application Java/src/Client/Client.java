/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import Chat.Chat;
import Chat.ChatRoom;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Logger;
import Message.Message;
import Server.Server;
import Server.ServerClient;
import UI.MainScreen;
import java.io.Serializable;
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
    
    public void Start(String ip, int port) {
        try {
            socket = new Socket(ip, port);
            streamReader = new ObjectInputStream(socket.getInputStream());
            streamWriter = new ObjectOutputStream(socket.getOutputStream());
            listen.start();
            
            System.out.println("Client connected");
            
            Send(new Message(0,Message.Message_Type.Name, this.name));
            mainScreen = new MainScreen();
            System.out.println(this.id);
            mainScreen.setClient(this);
            mainScreen.setVisible(true);
            Send(new Message(0,Message.Message_Type.GetContactsInfo,""));
            Send(new Message(0,Message.Message_Type.GetChatRoomsInfo,""));
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

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
                Message receivedMessage = (Message) (client.streamReader.readObject());
                    
                switch (receivedMessage.type) 
                {
                    case SetClientID:
                        client.id = Integer.parseInt(receivedMessage.content.toString());
                        break;
                    case GetContactsInfo:
                        client.mainScreen.ShowContacts((ArrayList<FakeClient>)receivedMessage.content);
                        break;
                    case GetMessagesInfo:
                        client.mainScreen.ShowMessages((ArrayList<Message>) receivedMessage.content);
                        break;
                    case GetChatRoomsInfo:
                        client.mainScreen.ShowChatRoom((ArrayList<ChatRoom>)receivedMessage.content);
                        break;
                            
                }
                
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(Listen.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}


