/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import Message.Message;

/**
 *
 * @author Mert
 */
public class ServerClient {
    
    public int id;
    public String name = "";
    Socket socket;
    ObjectOutputStream streamWriter;
    ObjectInputStream streamReader;
    Listen listenThread;
    public boolean paired = false;
    
    public ServerClient(Socket gelenSocket, int id) {
        this.socket = gelenSocket;
        this.id = id;
        try {
            this.streamWriter = new ObjectOutputStream(this.socket.getOutputStream());
            this.streamReader = new ObjectInputStream(this.socket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(ServerClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.listenThread = new Listen(this);
    }
}

class Listen extends Thread {

    ServerClient client;

    Listen(ServerClient TheClient) {
        this.client = TheClient;
    }

    public void run() {

        while (client.socket.isConnected()) {
            try {
                Message receivedMessage = (Message) (client.streamReader.readObject());
                switch (receivedMessage.type) 
                {
                    case Name:
                        Server.clients.get(Server.clients.size()-1).name = receivedMessage.content.toString();  //sets last client name
                        break;
                    case GetContactsInfo:
                        for (ServerClient _client : Server.clients) {
                            Server.ShowClients(_client);                                                        //calls ShowClients metodh of Server for each client
                        }
                        break;
                    case CreateChat:
                        Server.CreateChat(client.id, Integer.parseInt(receivedMessage.content.toString()));     //calls CreateChat metodh of Server
                        break;
                    case SendChatMessage:
                        Server.SendChatMessage(receivedMessage);                                                //calls SendChatMessage metodh of Server
                        for (ServerClient _client : Server.clients) {
                            Server.ShowClients(_client);                                                        //calls ShowClients metodh of Server for each client
                        }
                        break;
                    case SendFile:
                        Server.Send(client,receivedMessage);                                                    //calls Send metodh of Server
                        break;  
                }
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(ServerClient.class.getName()).log(Level.SEVERE, null, ex);
                Server.clients.remove(client);

            }
        }
    }
}
