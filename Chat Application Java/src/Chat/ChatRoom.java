/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Chat;

import Client.FakeClient;
import Message.Message;
import Server.ServerClient;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Mert
 */
public class ChatRoom implements Serializable{
    public ArrayList<FakeClient> clients = new ArrayList<FakeClient>();
    public ArrayList<Message> messages;
    public int chatRoomID;

    public ChatRoom(ArrayList<FakeClient> _clients, int id)
    {
        this.messages = new ArrayList<Message>();
        this.clients = _clients;
        chatRoomID = id;
    }
}
