package com.leidos.xchangecore.core.infrastructure.xmpp.communications;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.packet.StreamError;

public class ConnectionCleanup implements ConnectionListener {

    private final Logger logger = Logger.getLogger(this.getClass());

    private String name = "";

    CoreConnectionImpl coreConnection;

    public ConnectionCleanup(String name, CoreConnectionImpl coreConnection) {

        this.name = name;
        this.coreConnection = coreConnection;

    }

    @Override
    public void connectionClosed() {

        logger.info("connectionClosed => " + name);
        coreConnection.setConnected(false);
    }

    @Override
    public void connectionClosedOnError(Exception e) {

        logger.error("connectionClosedOnError: " + name);
        if (e instanceof org.jivesoftware.smack.XMPPException) {
            org.jivesoftware.smack.XMPPException xmppEx = (org.jivesoftware.smack.XMPPException) e;
            StreamError error = xmppEx.getStreamError();

            // Make sure the error is not null
            if (error != null) {
                String reason = error.getCode();
                logger.error("connectionClosedOnError: " + reason);

                if ("conflict".equals(reason)) {
                    // on conflict smack will not automatically try to reconnect
                    coreConnection.setConnected(false);
                    logger.error("connectionClosedOnError: another XMPP client logged in with the core's full JID so we cannot try to reconnect.");
                    return;
                }
            }
        }
    }

    @Override
    public void reconnectingIn(int arg0) {

        logger.info("reconnectingIn: " + name);

    }

    @Override
    public void reconnectionFailed(Exception arg0) {

        logger.error("reconnectionFailed: " + name);

    }

    @Override
    public void reconnectionSuccessful() {

        logger.info("reconnectionSuccessful: " + name);
        coreConnection.setConnected(true);
        coreConnection.sendHeartBeat();
        coreConnection.setConnected(true);
    }

}
