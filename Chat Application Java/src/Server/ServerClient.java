/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Client.FakeClient;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import Message.Message;
import java.io.Serializable;
import java.util.ArrayList;

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
                            Server.clients.get(Server.clients.size()-1).name = receivedMessage.content.toString();
                            break;
                        case GetContactsInfo:
                            for (ServerClient _client : Server.clients) {
                                Server.ShowClients(_client); 
                            }
                            break;
                        case CreateChat:
                            String msg = receivedMessage.content.toString();
                            Server.CreateChat(client.id, Integer.parseInt(receivedMessage.content.toString()));
                        case SendChatMessage:
                            Server.SendChatMessage(receivedMessage);
                            break;
                        case CreateChatRoom:
                            Server.CreateChatRoom((ArrayList<FakeClient>)receivedMessage.content);
                            System.out.println("2-server client 77 ");
                            break;
                        case GetChatRoomsInfo:
                            for (ServerClient _client : Server.clients) {
                                Server.ShowChatRooms(_client); 
                            }
                             System.out.println("GetChatRoomsInfo");
                            break;
                    }   
                    
                } catch (IOException ex) {
                    Logger.getLogger(ServerClient.class.getName()).log(Level.SEVERE, null, ex);
                    Server.clients.remove(client);

                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(ServerClient.class.getName()).log(Level.SEVERE, null, ex);
                    Server.clients.remove(client);
                }
            }

        }
}
