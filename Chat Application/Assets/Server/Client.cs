using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Net.Sockets;
using UnityEditor;
using UnityEngine;
using UnityEngine.EventSystems;
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
    List<GameObject> chatMessages = new List<GameObject>();
    List<Button> messagesPanelMessages = new List<Button>();
    List<Button> contacts = new List<Button>();
    string name;
    int id;

    [Header("enterance panel")]
    [SerializeField] InputField nameInputField;

    [Header("contact panel")]
    [SerializeField] Button contactPrefab;
    [SerializeField] GameObject contactParent;

    [Header("message panel")]
    [SerializeField] Button messageButtonPrefab;
    [SerializeField] GameObject messageButtonParent;

    [Header("chat panel")]
    [SerializeField] GameObject messagePrefab;
    [SerializeField] GameObject messageParent;
    [SerializeField] InputField messsageInputField;
    [SerializeField] Text clientNameField;
    [SerializeField] Text clientIDField;
    [SerializeField] Text console;

    [Header("others")]
    [SerializeField] PanelController panelController;

    private void Update()
    {
        GameObject selectedGameObject = EventSystem.current.currentSelectedGameObject;

        if (selectedGameObject != null && selectedGameObject.name.Equals("contact button prefab(Clone)"))
        {
            string[] tempinfos = EventSystem.current.currentSelectedGameObject.GetComponentInChildren<Text>().text.Split(' ');
           
            ContactButtonClick(Int32.Parse(tempinfos[0]), tempinfos[1]);
               
        }

        if (selectedGameObject != null && selectedGameObject.name.Equals("message button prefab(Clone)"))
        {
            string[] tempinfos = EventSystem.current.currentSelectedGameObject.GetComponentInChildren<Text>().text.Split(':');

            ContactButtonClick(Int32.Parse(tempinfos[0]), "");
               
        }
        

        if (socketReady)
        {
            if (networkStream.DataAvailable)
            {
                BinaryReader binaryReader = new BinaryReader(networkStream);

                string[] tempinfos= null;
                string s = binaryReader.ReadString();

                Message receivedMessage = JsonUtility.FromJson<Message>(s);
               //console.text = receivedMessage.messageType.ToString();

                switch (receivedMessage.messageType)
                {
                    case Message.MessageType.Name:

                        tempinfos = receivedMessage.content.Split('-');
                        id = Int32.Parse(tempinfos[0]);
                        name = tempinfos[1];

                        Debug.Log("client: Kullanıcının isim ve id bilgisi alındı");
                        break;
                    case Message.MessageType.ContactInformation:

                        foreach (var item in contacts)
                        {
                            if(item != null)
                                Destroy(item.gameObject);
                        }

                        string[] tempClients = receivedMessage.content.Split(' ');
                        for (int i = 0; i < tempClients.Length - 1; i++)
                        {
                            tempinfos = tempClients[i].Split('-');
                            Button b = Instantiate(contactPrefab, contactParent.transform);
                            b.GetComponentInChildren<Text>().text = tempinfos[0] + " " + tempinfos[1];
                            //Debug.Log("90: " + "id: " + tempinfos[0] + " name: " + tempinfos[1]);
                            contacts.Add(b);
                        }
                        Debug.Log("client: contact bilgileri alındı ve butonlar oluştu");

                        break;
                    case Message.MessageType.AllChatMessages:

                        foreach (GameObject gameObject in chatMessages)
                        {
                            Destroy(gameObject);
                        }

                        string[] tempMessages = receivedMessage.content.Split(' ');

                        for (int i = 0; i < tempMessages.Length - 1; i++)
                        {
                            tempinfos = tempMessages[i].Split('-');

                            if (Int32.Parse(tempinfos[0]) == id)
                            {
                                DisplayMessages(tempinfos[1], TextAnchor.MiddleRight);
                            }
                            else
                            {
                                DisplayMessages(tempinfos[1], TextAnchor.MiddleLeft);
                            }
                        }
                        Debug.Log("client: mesajlar geldi ve ekrana basıldı");

                        break;
                    case Message.MessageType.ChatsInformation:

                        foreach (var item in messagesPanelMessages)
                        {
                            if (item != null)
                                Destroy(item.gameObject);
                        }

                        tempMessages = receivedMessage.content.Split(' ');

                        for (int i = 0; i < tempMessages.Length - 1; i++)
                        {
                            tempinfos = tempMessages[i].Split('-');

                            Button messageButton = Instantiate(messageButtonPrefab, messageButtonParent.transform);
                            messageButton.GetComponentInChildren<Text>().text = tempinfos[0] + ":" + tempinfos[1];
                            messagesPanelMessages.Add(messageButton);
                        }
                        Debug.Log("client: chat bilgisi geldi");

                        break;
                    default:
                        return;
                }
            }
        }
    }

    void DisplayMessages(string content, TextAnchor textAnchor)
    {
        GameObject message = Instantiate(messagePrefab, messageParent.transform);
        message.GetComponent<Text>().text = content;
        message.GetComponent<Text>().alignment = textAnchor;
        chatMessages.Add(message);
    }

    public void GetContacts()
    {
        SendMessage(new Message(Message.MessageType.ContactInformation, "", 0, 0));
        panelController.ShowPanel("contacts panel");
        Debug.Log("client: contact bilgisi isteniyor");
    }

    public void SendMessageButton()
    {
        SendMessage(new Message(Message.MessageType.SendChatMessage, messsageInputField.text, id, Int32.Parse(clientIDField.text)));
        messsageInputField.text = "";

        Debug.Log("client: bir yeni mesaj gönderiliyor");
    }

    public void SendMessage(Message message)
    {
        string messageToSend = JsonUtility.ToJson(message);

        binaryWriter.Write(messageToSend);

        binaryWriter.Flush();
    }
    
    public void ContactButtonClick(int _id, string name)
    {
        //Debug.Log("180: " + "id: " + _id + " name: " + name);
        panelController.ShowPanel("chat panel");
        SendMessage(new Message(Message.MessageType.CreateChat, "", id, _id));
        clientNameField.text = name;
        clientIDField.text = _id.ToString();
        Debug.Log("client: chat ekranı açılıyor ve mesajlar isteniyor");
        //Debug.Log("ContactButtonClick function: chat with this person = " + clientNameField.text);
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

            SendMessage(new Message(Message.MessageType.Name, nameInputField.text, 0, 0));
            GoMessagePanel();
            Debug.Log("client: Client bağlandı, mesajlar ekranına gidiliyor");
        }
    }

    public void GoMessagePanel()
    {
        SendMessage(new Message(Message.MessageType.ChatsInformation, "", 0, 0));

        panelController.ShowPanel("message panel");
        Debug.Log("client: mesajlar ekranına geldik");
    }

}
