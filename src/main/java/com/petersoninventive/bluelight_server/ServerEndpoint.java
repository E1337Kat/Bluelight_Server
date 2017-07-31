/*
 * Copyright 2017 Elliekat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.petersoninventive.bluelight_server;

import java.io.IOException;
import java.util.Date;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
//import javax.websocket.server.ServerEndpoint;
import static java.lang.String.format;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.CloseReason;

import com.relops.snowflake.Snowflake;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.websocket.OnError;

/**
 *
 * @author Elliekat
 */
@javax.websocket.server.ServerEndpoint(value = "/chat", 
                encoders = MessageEncoder.class, 
                decoders = MessageDecoder.class)
public class ServerEndpoint {

    private static final Logger LOG = Logger.getLogger(ServerEndpoint.class.getName());
    
    private Snowflake convoID = new Snowflake(1);
    private Snowflake userID = new Snowflake(1);
    private long DISPATCH_ID = 1;
    private Session dispatch;
    static Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());
    private static HashMap<Long, Conversation> convos = new HashMap<Long, Conversation>();
    

    @OnOpen
    public void onOpen(Session sn) {
        peers.add(sn);
        LOG.info("something is done.");
        if (peers.size() == 1) {
            LOG.info("dispatch connected.");
            dispatch = sn;
        } else {
            long c_id = this.convoID.next();
            long u_id = this.userID.next();
            Conversation c = new Conversation(c_id, sn);
            convos.put(c_id, c);
            LOG.info(String.format(
                                   "%d has initiated a request.", 
                                   convos.get(c_id).getConvoID()));
            sn.getAsyncRemote().sendObject(new Message(c_id, u_id));
        }
        
    }

    @OnMessage
    public void onMessage(Message message, Session session) throws IOException, EncodeException {
        String user = (String) session.getUserProperties().get("user");
        if (user == null) {
            session.getUserProperties().put("user", message.getSender());
        }
        if ("quit".equalsIgnoreCase(message.getBody())) {
            session.close();
        }     
        
        LOG.info(String.format("[%s:%s] %s", session.getId(), message.getReceived(), message.getBody()));
        
        //forward message to appropriate place
        if (message.getUserID() == this.DISPATCH_ID){
            LOG.info("message from dispatch");
            long convoID = message.getConvoID();
            Session client = convos.get(convoID).getSession();
            client.getAsyncRemote().sendObject(message);
        } else {
            LOG.info("message from client");
            long convoID = message.getConvoID();
            dispatch.getAsyncRemote().sendObject(message);
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        LOG.info(String.format("%s left the chat room.", session.getId()));
        peers.remove(session);
        //notify peers about leaving the chat room
        peers.stream().forEach((peer) -> {
            Message message = new Message();
            message.setSender("Server");
            message.setBody(format("%s left the chat room", (String) session.getUserProperties().get("user")));
            message.setReceived(new Date());
            try {
                peer.getBasicRemote().sendObject(message);
            } catch (IOException | EncodeException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        });
    }
    
    @OnError
    public void onError(Session session, Throwable throwable) {
        LOG.severe("Error reported for session " + session + " reporting: \n" + throwable);
    }
}
