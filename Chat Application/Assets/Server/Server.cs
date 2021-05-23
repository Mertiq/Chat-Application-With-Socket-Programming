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
            Debug.Log("Server started");
        }
        catch 
        {
            return;
        }
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
