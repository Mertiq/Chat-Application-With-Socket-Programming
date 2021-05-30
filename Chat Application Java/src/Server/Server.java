/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Chat.Chat;
import Chat.ChatRoom;
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
    public static int chatRoomID = 0;
    public static int port = 0;

    public static ServerSocket serverSocket;
    public static ServerThread runThread;

    public static ArrayList<ServerClient> clients = new ArrayList<>();
    public static ArrayList<Chat> chats = new ArrayList<>();
    public static ArrayList<ChatRoom> chatRooms = new ArrayList<>();

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

    public static void Send(ServerClient client, Message message) {

        try {
            client.streamWriter.writeObject(message);
            client.streamWriter.reset();
        } catch (IOException ex) {
            Logger.getLogger(ServerClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

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

    public static void ShowClients(ServerClient _client) {

        ArrayList<FakeClient> _clients = new ArrayList<>();

        for (ServerClient client : clients) {
            if (client != _client) {
                _clients.add(new FakeClient(client.id, client.name));
            }
        }
        Send(_client, new Message(Message_Type.GetContactsInfo, _clients));

    }
    
    public static void ShowChatRooms(ServerClient _client) {

        ArrayList<ChatRoom> _chatRooms = new ArrayList<>();

        for (ChatRoom chatRoom : Server.chatRooms) {
            for (FakeClient client : chatRoom.clients) {
                
                if(client.id == _client.id){
                    _chatRooms.add(chatRoom);
                }
            }
        }
        Send(_client, new Message(Message_Type.GetChatRoomsInfo, _chatRooms));

    }

    public static void ShowChats(ServerClient _client) {

        for (Chat chat : chats) {
            if (chat.clients.get(0) == _client || chat.clients.get(1) == _client) {
                Send(_client, new Message(0,Message_Type.GetChatsInfo, chat));
            }
        }
    }

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
            if (chats.size() == 0) {
                Chat chat = new Chat(sc1, sc2, chatID);
                chats.add(chat);
                chatID++;
                System.out.println("Chat created");
            } else {
                found=false;
                for (int i = 0; i < chats.size(); i++) {
                    if ((chats.get(i).clients.get(0) == sc1 && chats.get(i).clients.get(1) == sc2) || (chats.get(i).clients.get(0) == sc2 && chats.get(i).clients.get(1) == sc1)) {
                       
                        Send(sc1, new Message(0,Message_Type.GetMessagesInfo, chats.get(i).messages));
                        
                        System.out.println("Chat already created");
                        found=true;
                        break;
                    }
                }
                if(!found){
                     
                    Chat chat = new Chat(sc1, sc2, chatID);
                    chats.add(chat);
                    chatID++;
                    System.out.println("Chat created");
                    
                }
            }
        }
    }
    
    public static void SendChatMessage(Message message){
        ServerClient sc1 = null;
        ServerClient sc2 = null;
        boolean found = false;

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
    
    public static void CreateChatRoom(ArrayList<FakeClient> _clients){
        
        ArrayList<FakeClient> chatRoomClients = new ArrayList<FakeClient>();
        ArrayList<FakeClient> tempChatRoomClients = new ArrayList<FakeClient>();
        
        for (FakeClient fc : _clients) {
            for (ServerClient sc : Server.clients) {
                if(fc.id == sc.id){
                    chatRoomClients.add(fc);
                }
            }
        }
        
        if(chatRooms.size() == 0){
            ChatRoom chatRoom = new ChatRoom(chatRoomClients, chatRoomID++);
            chatRooms.add(chatRoom);
            System.out.println("3- server 208");
        }else{
            for (ChatRoom chatRoom : chatRooms) {
                tempChatRoomClients = chatRoomClients;
                for (FakeClient client : chatRoom.clients) {
                    for (FakeClient chatRoomClient : tempChatRoomClients) {
                        if(chatRoomClient == client){
                            tempChatRoomClients.remove(chatRoomClient);
                        }
                    }
                }
            }
            System.out.println("tempChatRoomClients.size() " + tempChatRoomClients.size());
            if(tempChatRoomClients.size() > 0){
                ChatRoom chatRoom = new ChatRoom(chatRoomClients, chatRoomID++);
                chatRooms.add(chatRoom);
                System.out.println("3- server 208");
            }
        }

        
    }

    public static String GetUserNameByID(int id){
        for (ServerClient client : clients) {
            if(client.id == id)
                return client.name;
        }
        return "";
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
