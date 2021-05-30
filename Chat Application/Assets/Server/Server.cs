using System;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Net.Sockets;
using UnityEngine;

public class Server : MonoBehaviour
{
    //connecting port
    int port = 5000;

    //connected clients list
    List<ServerClient> clients;
    //disconnected clients list
    List<ServerClient> disconnectedClients;

    //server
    TcpListener server;
    bool serverStarted;

    //id
    int idCounter=1;

    List<Chat> chats;

    private void Start()
    {
        //server start
        try
        {
            server = new TcpListener(IPAddress.Any, port);
            server.Start();
            serverStarted = true;
            clients = new List<ServerClient>();
            disconnectedClients = new List<ServerClient>();
            chats = new List<Chat>();
            Debug.Log("Server started");
            //start listening
            server.BeginAcceptTcpClient(AcceptTcpClient, server);
        }
        catch 
        {
            return;
        }
    }

    private void AcceptTcpClient(IAsyncResult ar)
    {
        TcpListener listener = (TcpListener)ar.AsyncState;
        //client geldi
        ServerClient serverClient = new ServerClient(listener.EndAcceptTcpClient(ar), idCounter);
        clients.Add(serverClient);
        Debug.Log("Client connected");

        //start listening
        server.BeginAcceptTcpClient(AcceptTcpClient, server);
    }

    void Update()
    {
        if (!serverStarted)
            return;

        foreach (ServerClient serverClient in clients)
        {
            if (!IsConnected(serverClient.tcpClient))
            {
                serverClient.tcpClient.Close();
                disconnectedClients.Add(serverClient);
                continue;
            }
            else
            {
                NetworkStream networkStream = serverClient.tcpClient.GetStream();

                if (networkStream.DataAvailable)
                {

                    BinaryReader binaryReader = new BinaryReader(networkStream);
                    BinaryWriter binaryWriter= new BinaryWriter(networkStream);

                    Message receivedMessage = JsonUtility.FromJson<Message>(binaryReader.ReadString());

                    switch (receivedMessage.messageType)
                    {
                        case Message.MessageType.Name:

                            string messageToSend = "";

                            serverClient.name = receivedMessage.content;
                            serverClient.id = idCounter;

                            messageToSend += serverClient.id;
                            messageToSend += "-";
                            messageToSend += serverClient.name;

                            SendMessage(new Message(Message.MessageType.Name, messageToSend, 0, 0), binaryWriter);

                            idCounter++;

                            Debug.Log("Server: Kullanıcının isim bilgisi alındı ve isim ve idsi gönderildi");

                            break;
                        case Message.MessageType.ContactInformation:

                            
                            messageToSend = null;

                            foreach (var item in clients)
                            {
                                messageToSend += item.id;
                                messageToSend += "-";
                                messageToSend += item.name;
                                messageToSend += " ";
                            }

                            SendMessage(new Message(Message.MessageType.ContactInformation, messageToSend, 0, 0), binaryWriter);
                            Debug.Log("Server: contact biligi gönderildi");
                            break;
                        case Message.MessageType.CreateChat:

                            ServerClient _fromC = null;
                            ServerClient _toC = null;

                            foreach (var client in clients)
                            {
                                if (client.id == receivedMessage.fromClientID && _fromC == null)
                                {
                                    _fromC = client;
                                }
                                if (client.id == receivedMessage.toClientID)
                                {
                                    _toC = client;
                                }
                            }

                            if (_fromC != null && _toC != null)
                            {
                                //Debug.Log("from " + _fromC.name + " to " + _toC.name);

                                if (chats.Count == 0)
                                {
                                    Chat chat = new Chat(_fromC, _toC);
                                    chats.Add(chat);
                                    //Debug.Log("Chat created");
                                }
                                else
                                {
                                    for (int i = 0; i < chats.Count; i++)
                                    {
                                        if ((chats[i].clients[0] == _fromC && chats[i].clients[1] == _toC) || (chats[i].clients[0] == _toC && chats[i].clients[1] == _fromC))
                                        {
                                            messageToSend = null;

                                            foreach (var item in chats[i].messages)
                                            {
                                                messageToSend += item.fromClientID;
                                                messageToSend += "-";
                                                messageToSend += item.content;
                                                messageToSend += " ";
                                            }

                                            Debug.Log("165: " + messageToSend);

                                            SendMessage(new Message(Message.MessageType.AllChatMessages, messageToSend, 0, 0), binaryWriter);
                                        }
                                        else
                                        {
                                            Chat chat = new Chat(_fromC, _toC);
                                            chats.Add(chat);
                                            //Debug.Log("Chat created 174");
                                        }
                                    }
                                }
                            }

                            Debug.Log("client: mesajlar gönderiliyor");
                            break;
                        case Message.MessageType.SendChatMessage:

                            _fromC = null;
                            _toC = null;

                            foreach (var client in clients)
                            {
                                if (client.id == receivedMessage.fromClientID)
                                {
                                    _fromC = client;
                                }
                                if (client.id == receivedMessage.toClientID)
                                {
                                    _toC = client;
                                }
                            }

                            if (_fromC != null && _toC != null)
                            {
                                foreach (var _chat in chats)
                                {
                                    if ((_chat.clients[0] == _fromC && _chat.clients[1] == _toC) || (_chat.clients[0] == _toC && _chat.clients[1] == _fromC))
                                    {
                                        _chat.messages.Add(new Message(Message.MessageType.SendChatMessage, receivedMessage.content, _fromC.id, _toC.id));
                                        messageToSend = null;

                                        foreach (var item in _chat.messages)
                                        {
                                            messageToSend += item.fromClientID;
                                            messageToSend += "-";
                                            messageToSend += item.content;
                                            messageToSend += " ";
                                        }

                                        SendMessage(new Message(Message.MessageType.AllChatMessages, messageToSend, 0, 0), binaryWriter);
                                    }
                                }
                            }

                            Debug.Log("server: bir yeni mesaj geldi ve karşı tarafa gidiyor");
                            break;
                        case Message.MessageType.ChatsInformation:

                            messageToSend = "";

                            
                            foreach (var item in chats)
                            {
                                if(item.messages.Count == 0)
                                {
                                    continue;
                                }
                                else
                                {
                                    messageToSend += item.clients[1].id;
                                    messageToSend += "-";
                                    messageToSend += item.messages[item.messages.Count - 1].content;
                                    messageToSend += " ";
                                }
                            }

                            SendMessage(new Message(Message.MessageType.ChatsInformation, messageToSend, 0, 0), binaryWriter);

                            Debug.Log("Server: chat bilgisi gitti");
                            break;
                    }
                }
            }
        }
    }

    bool IsConnected(TcpClient tcpClient)
    {
        if (tcpClient != null && tcpClient.Client != null && tcpClient.Client.Connected)
        {
            if (tcpClient.Client.Poll(0, SelectMode.SelectRead))
            {
                return !(tcpClient.Client.Receive(new byte[1], SocketFlags.Peek) == 0);
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    public void SendMessage(Message message, BinaryWriter binaryWriter)
    {
        string messageToSend = JsonUtility.ToJson(message);
        Debug.Log("269: " + messageToSend);
        binaryWriter.Write(messageToSend);
        binaryWriter.Flush();
    }
}

public class ServerClient
{
    public TcpClient tcpClient;
    public int id;
    public string name;

    public ServerClient(TcpClient tcpClient, int id)
    {
        this.tcpClient = tcpClient;
        this.id = id;
    }

}
