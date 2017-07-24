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

import javax.json.Json;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 *
 * @author ellie
 */
public class MessageEncoder implements Encoder.Text<Message> {

    @Override
    public String encode(Message message) throws EncodeException {
        return Json.createObjectBuilder()
                    .add("body", message.getBody())
                    .add("sender", message.getSender())
                    .add("verification", message.getVerification())
                    .add("received", "")
                    .build().toString();
    }

    @Override
    public void init(EndpointConfig ec) {}

    @Override
    public void destroy() {}
    
}
