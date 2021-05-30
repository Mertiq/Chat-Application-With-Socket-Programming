using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Message
{
    public enum MessageType { Name, ContactInformation, SendChatMessage, CreateChat, AllChatMessages, ChatsInformation}

    public string content;
    public int fromClientID;
    public int toClientID;
    public MessageType messageType;
   
    public Message(MessageType messageType, string content, int fromClientID, int toClientID)
    {
        this.messageType = messageType;
        this.content = content;
        this.fromClientID = fromClientID;
        this.toClientID = toClientID;
    }

}
