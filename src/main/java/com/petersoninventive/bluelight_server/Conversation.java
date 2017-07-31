/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.petersoninventive.bluelight_server;

import java.util.Stack;
import javax.websocket.Session;

/**
 *
 * @author ellie
 */
public class Conversation {
    private Stack<Message> messages;
    private long convoID;
    private Session session;
    
    public Conversation(long convoID, Session session) {
        this.convoID = convoID;
        this.session = session;
    }
    
    public void addMessage(Message message) {
        this.messages.push(message);
    }
    
    public Stack<Message> getMessages() {
        return this.messages;
    }
    
    public String getMostRecentMessage() {
        return this.messages.peek().getBody();
    }
    
    public long getConvoID() {
    
        return this.convoID;
    }
    
    public Session getSession() {
        return this.session;
    }
}
