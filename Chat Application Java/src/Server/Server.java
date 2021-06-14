/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Chat.Chat;
import Client.FakeClient;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import Message.Message;
import Message.Message.Message_Type;
import static Server.Server.clients;

/**
 *
 * @author Mert
 */
public class Server {

    public static int clientID = 0;
    public static int chatID = 0;
    public static int port = 0;

    public static ServerSocket serverSocket;
    public static ServerThread runThread;

    public static ArrayList<ServerClient> clients = new ArrayList<>();
    public static ArrayList<Chat> chats = new ArrayList<>();
    
    ///<summary>
    ///Starts thread
    ///</summary>
    ///<param port>port that client will connect</param>
    public static void Start(int port) {
        try {
            Server.port = port;
            Server.serverSocket = new ServerSocket(Server.port);
            Server.runThread = new ServerThread();
            Server.runThread.start();
            System.out.println("Server started!");

        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    ///<summary>
    ///Sends message to client
    ///</summary>
    ///<param client>the client</param>
    ///<param message>the message</param>
    public static void Send(ServerClient client, Message message) {

        try {
            client.streamWriter.writeObject(message);
            client.streamWriter.reset();
        } catch (IOException ex) {
            Logger.getLogger(ServerClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    ///<summary>
    ///Sends message to everyone
    ///</summary>
    ///<param message>the message</param>
    public static void Send(Message message) {

        for (ServerClient client : Server.clients) {

            try {
                client.streamWriter.writeObject(message);
                client.streamWriter.reset();
            } catch (IOException ex) {
                Logger.getLogger(ServerClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    ///<summary>
    ///Sends clients infos to client
    ///</summary>
    ///<param _client>the client</param>
    public static void ShowClients(ServerClient _client) {

        ArrayList<FakeClient> _clients = new ArrayList<>();

        for (ServerClient client : clients) {
            if (client != _client) {
                _clients.add(new FakeClient(client.id, client.name));
            }
        }
        Send(_client, new Message(Message_Type.GetContactsInfo, _clients));

    }
    
    ///<summary>
    ///Sends chats infos to client
    ///</summary>
    ///<param _client>the client</param>
    public static void ShowChats(ServerClient _client) {

        for (Chat chat : chats) {
            if (chat.clients.get(0) == _client || chat.clients.get(1) == _client) {
                Send(_client, new Message(0,Message_Type.GetChatsInfo, chat));
            }
        }
    }

    ///<summary>
    ///Creates chat
    ///</summary>
    ///<param clientID1>first client</param>
    ///<param clientID2>secoond client</param>
    public static void CreateChat(int clientID1, int clientID2) {

        ServerClient sc1 = null;
        ServerClient sc2 = null;
        boolean found = false;

        for (ServerClient client : Server.clients) {
            if (client.id == clientID1) {
                sc1 = client;
            } else if (client.id == clientID2) {
                sc2 = client;
            }
        }

        if (sc1 != null && sc2 != null) {
            if (chats.isEmpty()) {
                Chat chat = new Chat(sc1, sc2, chatID);
                chats.add(chat);
                chatID++;
            } else {
                found=false;
                for (int i = 0; i < chats.size(); i++) {
                    if ((chats.get(i).clients.get(0) == sc1 && chats.get(i).clients.get(1) == sc2) || (chats.get(i).clients.get(0) == sc2 && chats.get(i).clients.get(1) == sc1)) {
                       
                        Send(sc1, new Message(0,Message_Type.GetMessagesInfo, chats.get(i).messages));
                        found=true;
                        break;
                    }
                }
                if(!found){
                     
                    Chat chat = new Chat(sc1, sc2, chatID);
                    chats.add(chat);
                    chatID++;
                    
                }
            }
        }
    }
    
    ///<summary>
    ///Sends chat message
    ///</summary>
    ///<param message>the message</param>
    public static void SendChatMessage(Message message){
        ServerClient sc1 = null;
        ServerClient sc2 = null;

        for (ServerClient client : Server.clients) {
            if (client.id == message.fromClientID) {
                sc1 = client;
            } else if (client.id == message.toClientID) {
                sc2 = client;
            }
        }
        
        for (int i = 0; i < chats.size(); i++) {
            if ((chats.get(i).clients.get(0) == sc1 && chats.get(i).clients.get(1) == sc2) || (chats.get(i).clients.get(0) == sc2 && chats.get(i).clients.get(1) == sc1)) {
                chats.get(i).messages.add(message);
                Send(sc1, new Message(0,Message_Type.GetMessagesInfo, chats.get(i).messages));
                        
                break;
            }
        }
    }
    
    public static void SendFile(Message message){
        ServerClient sc1 = null;
        ServerClient sc2 = null;

        for (ServerClient client : Server.clients) {
            if (client.id == message.toClientID) {
                sc2 = client;
            }
        }
        
        Send(sc2, message);
          
    }
}

class ServerThread extends Thread {

    public void run() {
        while (!Server.serverSocket.isClosed()) {
            try {

                Socket socket = Server.serverSocket.accept();
                ServerClient client = new ServerClient(socket, Server.clientID);
                Server.clients.add(client);
                Server.Send(new Message(0,Message_Type.SetClientID, Server.clientID++));
                client.listenThread.start();

            } catch (IOException ex) {
                Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
