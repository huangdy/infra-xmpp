package com.leidos.xchangecore.core.infrastructure.xmpp.communications.ping;

import org.jivesoftware.smack.packet.IQ;

public class Ping extends IQ implements PingConstants {

    public Ping() {

    }

    public Ping(String from, String to) {

        // server to client ping
        /*
        */
        setFrom(from + ResourceName);
        setTo(to + ResourceName);
        /*
         * server to server from = from.replaceAll("uicds@", ""); to = to.replaceAll("uicds@", "");
         * setTo(to); setFrom(from);
         */
        setType(IQ.Type.GET);
        setPacketID(getPacketID());
    }

    @Override
    public String getChildElementXML() {

        return "<" + PingManager.ELEMENT + " xmlns=\'" + PingManager.NAMESPACE + "\' />";
    }
}
