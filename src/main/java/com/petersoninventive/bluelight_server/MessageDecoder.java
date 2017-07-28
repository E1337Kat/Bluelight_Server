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

import java.io.StringReader;
import java.util.Date;
import javax.json.Json;
import javax.json.JsonObject;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

/**
 *
 * @author Elliekat
 */
public class MessageDecoder implements Decoder.Text<Message> {

    @Override
    public Message decode(final String textMessage) throws DecodeException {
        Message message = new Message();
        JsonObject jsonObject = Json.createReader(new StringReader(textMessage)).readObject();
        message.setBody(jsonObject.getString("body"));
        message.setSender(jsonObject.getString("sender"));
        message.setSessionID(jsonObject.getString("session_id"));
        message.setVerification(jsonObject.getInt("verificaton"));
        message.setReceived(new Date());
        return message;
    }

    @Override
    public boolean willDecode(final String s) {
        return true;
    }

    @Override
    public void init(EndpointConfig ec) {}

    @Override
    public void destroy() {}

}
