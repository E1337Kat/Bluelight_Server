/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.petersoninventive.bluelight_server;

import java.util.List;
import javax.websocket.Extension;
import javax.websocket.server.ServerEndpointConfig;


/**
 *
 * @author ellie
 */
class P2PConfigurator extends ServerEndpointConfig.Configurator {
    
    public List<Extension> getNegotiatedExtentions(List<Extension> installed, List<Extension> requested) {
        List<Extension> empty = null;
        return empty;
    }
}
