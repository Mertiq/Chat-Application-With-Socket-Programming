using System.Collections.Generic;

public class Chat
{
    public List<ServerClient> clients;
    public List<Message> messages;

    public Chat(ServerClient client1, ServerClient client2)
    {
        messages = new List<Message>();
        clients = new List<ServerClient>();
        clients.Add(client1);
        clients.Add(client2);
    }
}
