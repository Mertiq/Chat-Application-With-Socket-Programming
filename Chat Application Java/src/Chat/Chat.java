/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Chat;

import Message.Message;
import Server.ServerClient;
import java.util.ArrayList;

/**
 *
 * @author Mert
 */
public class Chat
{
    public ArrayList<ServerClient> clients;
    public ArrayList<Message> messages;
    public int chatID;

    public Chat(ServerClient client1, ServerClient client2, int id)
    {
        messages = new ArrayList<Message>();
        clients = new ArrayList<ServerClient>();
        clients.add(client1);
        clients.add(client2);
        chatID = id;
    }
}
