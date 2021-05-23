using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Net.Sockets;
using UnityEditor;
using UnityEngine;
using UnityEngine.UI;


public class Client : MonoBehaviour
{
    [Header("Server Things")]
    bool socketReady;
    TcpClient socket;
    NetworkStream networkStream;
    BinaryWriter binaryWriter;
    BinaryReader binaryReader;
    string host = "127.0.0.1";
    int port = 5000;

    [Header("Client Things")]
    List<GameObject> messages = new List<GameObject>();
    string name;
    int id;

    [Header("enterance panel")]
    [SerializeField] InputField nameInputField;

    private void Update()
    {
        if (socketReady)
        {
            if (networkStream.DataAvailable)
            {
               
            }
        }
    }


    public void ConnectToServer()
    {
        if (socketReady)
            return;

        if (!nameInputField.text.Equals(""))
        {
            socket = new TcpClient("127.0.0.1", 5000);
            networkStream = socket.GetStream();
            binaryWriter = new BinaryWriter(networkStream);
            binaryReader = new BinaryReader(networkStream);
            socketReady = true;

        }

    }

}
