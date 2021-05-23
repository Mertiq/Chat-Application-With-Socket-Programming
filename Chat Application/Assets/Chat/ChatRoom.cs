using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class ChatRoom
{
    public List<Message> messages;
    public List<Client> clients;

    public ChatRoom(List<Client> clients)
    {
        messages = new List<Message>();
        this.clients = clients;
    }
}
