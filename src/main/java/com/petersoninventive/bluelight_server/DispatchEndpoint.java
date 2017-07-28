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

import static java.lang.String.format;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import static java.lang.String.format;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;

/**
 *
 * @author Elliekat
 */
//@ServerEndpoint(value = "/chat/{user}", 
//                configurator = P2PConfigurator.class,
//                encoders = MessageEncoder.class, 
//                decoders = MessageDecoder.class)
public class DispatchEndpoint extends javax.websocket.Endpoint {

    private Session session;
    static Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());

    @Override
    public void onOpen(Session sn, EndpointConfig ec) {
        this.session = sn;
        System.out.println(format("%s joined the chat room.", sn.getId()));
        peers.add(sn);
    }

    public void onMessage(Message message, Session session) throws IOException, EncodeException {
        String user = (String) session.getUserProperties().get("user");
        if (user == null) {
            session.getUserProperties().put("user", message.getSender());
        }
        if ("quit".equalsIgnoreCase(message.getBody())) {
            session.close();
        }

        System.out.println(format("[%s:%s] %s", session.getId(), message.getReceived(), message.getBody()));

        //broadcast the message
        for (Session peer : peers) {
            if (!session.getId().equals(peer.getId())) { // do not resend the message to its sender
                peer.getBasicRemote().sendObject(message);
            }
        }
    }

    @Override
    public void onClose(Session session, CloseReason reason) {
        System.out.println(format("%s left the chat room.", session.getId()));
        peers.remove(session);
        //notify peers about leaving the chat room
        for (Session peer : peers) {
            Message message = new Message();
            message.setSender("Server");
            message.setBody(format("%s left the chat room", (String) session.getUserProperties().get("user")));
            message.setReceived(new Date());
            try {
                peer.getBasicRemote().sendObject(message);
            } catch (IOException | EncodeException ex) {
                Logger.getLogger(DispatchEndpoint.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @Override
    public void onError(Session session, Throwable throwable) {
        
    }
}
