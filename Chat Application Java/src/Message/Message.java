/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Message;

import Client.FakeClient;
import java.util.ArrayList;

/**
 *
 * @author Mert
 */
public class Message implements java.io.Serializable {
    public enum Message_Type {Name, SendFile, GetChatsInfo, GetContactsInfo, CreateChat, GetMessagesInfo, SetClientID, SendChatMessage}
    
    public Message_Type type;
    public Object content;
    public String fileName;
    public int receiverClientID;
    
    public int fromClientID;
    public int toClientID;
    public ArrayList<FakeClient> toClientIDs;
    
    public Message(int _fromClientID, int _toClientID, Message_Type type, Object content, String _fileName)
    {
        this.type=type;
        this.content = content;
        this.fileName = _fileName;
        this.fromClientID = _fromClientID;
        this.toClientID = _toClientID;
    }
     
    public Message(int id, Message_Type type, Object content)
    {
        this.type=type;
        this.content = content;
        this.receiverClientID =id;
    }
    
    public Message(int _fromClientID, int _toClientID, Message_Type type, Object content)
    {
        this.type=type;
        this.content = content;
        this.fromClientID = _fromClientID;
        this.toClientID = _toClientID;
    }
    
    public Message(int _fromClientID, ArrayList<FakeClient> _toClientIDs, Message_Type type, Object content)
    {
        this.type=type;
        this.content = content;
        this.fromClientID = _fromClientID;
        this.toClientIDs = _toClientIDs;
    }
    
    public Message(Message_Type type, Object content)
    {
        this.type=type;
        this.content = content;
    }
    
    public Message(Message_Type type)
    {
        this.type=type;
    }
    
    
}
