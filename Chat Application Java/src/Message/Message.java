/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Message;

/**
 *
 * @author Mert
 */
public class Message implements java.io.Serializable {
    public enum Message_Type {Name, GetChatsInfo, GetChatRoomsInfo,GetContactsInfo, CreateChat, CreateChatRoom, GetMessagesInfo, SetClientID, SendChatMessage}
    
    public Message_Type type;
    public Object content;
    public int receiverClientID;
    
    public int fromClientID;
    public int toClientID;
    
    
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
