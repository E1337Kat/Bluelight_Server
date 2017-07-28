/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.petersoninventive.bluelight_server;

import java.util.Stack;

/**
 *
 * @author ellie
 */
public class Conversation {
    private Stack<Message> messages;
    private String sessionID;
    
    public Conversation(String sessionID) {
        this.sessionID = sessionID;
    }
    
    public void addMessage(Message message) {
        messages.push(message);
    }
    
    public Stack<Message> getMessages() {
        return this.messages;
    }
    
    public String getMostRecentMessage() {
        return this.messages.peek().getBody();
    }
    
    public String getSessionID() {
    
        return this.messages.peek().getSessionID();
    }
}
