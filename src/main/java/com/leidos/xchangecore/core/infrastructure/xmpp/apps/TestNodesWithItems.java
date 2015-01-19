/**
 * 
 */
package com.leidos.xchangecore.core.infrastructure.xmpp.apps;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.XMPPError;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.leidos.xchangecore.core.infrastructure.xmpp.apps.CoreXMPPUtils.XMPPNode;
import com.leidos.xchangecore.core.infrastructure.xmpp.communications.CommandWithReply;
import com.leidos.xchangecore.core.infrastructure.xmpp.communications.CommandWithReplyImpl;
import com.leidos.xchangecore.core.infrastructure.xmpp.communications.CoreConnection;
import com.leidos.xchangecore.core.infrastructure.xmpp.communications.InterestManager;
import com.leidos.xchangecore.core.infrastructure.xmpp.communications.XmppConnection;
import com.leidos.xchangecore.core.infrastructure.xmpp.communications.NodeManagerImpl.NODE_ITEM_TYPE;
import com.leidos.xchangecore.core.infrastructure.xmpp.extensions.pubsub.PubSubIQFactory;
import com.leidos.xchangecore.core.infrastructure.xmpp.extensions.util.PubSubIQ;

/**
 * @author roger
 * 
 */
public class TestNodesWithItems {

    Logger logger = Logger.getLogger(TestNodesWithItems.class);

    XmppConnection coreConnection;
    InterestManager interestManager;
    CoreXMPPUtils coreXMPPUtils;

    private final String interestGroupRoot = "test";
    private final String interestGroupNode = "incident";

    public void setCoreConnection(XmppConnection c) {
        coreConnection = c;
    }

    public void setInterestManager(InterestManager m) {
        interestManager = m;
    }

    public void setCoreXMPPUtils(CoreXMPPUtils u) {
        coreXMPPUtils = u;
    }

    public TestNodesWithItems() {

    }

//    public IQ retrieveItems(String nodeName, String[] idList) {
//
//        StringBuffer sb = new StringBuffer();
//        ArbitraryIQ iq = new ArbitraryIQ();
//        iq.setType(IQ.Type.GET);
//        iq.setTo(coreConnection.getPubSubSvc());
//
//        sb.append("<pubsub xmlns='http://jabber.org/protocol/pubsub'>");
//
//        sb.append("<items");
//        if (nodeName != null)
//            sb.append(" node='" + nodeName + "'");
//        sb.append(" >");
//
//        if (idList != null) {
//            for (String tmpID : idList) {
//                sb.append("<item id='" + tmpID + "'/>");
//            }
//        }
//
//        sb.append("</items>");
//
//        sb.append("</pubsub>");
//
//        iq.setChildElementXML(sb.toString());
//
//        return iq;
//
//    }

    private ArrayList<String> getItems(IQ iq) {
        CommandWithReply cmd = null;
		try {
			cmd = coreConnection.createCommandWithReply(iq);
		} catch (XMPPException e) {
			logger.error("Error creating command to get items: " + e.getMessage());
			return new ArrayList<String>();
		}
		
        if (cmd.waitForSuccessOrFailure()) {
            // logger.info("Getting items from " + node);
            // logger.info(iq.toXML());

        } else {
            logger.error("  message: " + cmd.getErrorMessage());
            logger.error("     code: " + cmd.getErrorCode());
            logger.error("     type: " + cmd.getErrorType());
            logger.error("condition: " + cmd.getErrorCondition());
            if (cmd.getResult() != null) {
                logger.error(cmd.getResult().toXML());
            }
            if (cmd.getErrorType() == XMPPError.Type.AUTH) {
                logger.error("   Not authorized for this action.");
            }
        }

        // Parse out returned items into a list
        ArrayList<String> messages = new ArrayList<String>();

        IQ iqResponse = cmd.getResult();
        if (iqResponse != null) {

            if (iqResponse instanceof PubSubIQ) {
                // logger.info(
                // "GOT a PubSubIQ from initial updates in NodeSubscription:getAllNodeItems");
                PubSubIQ pubsub = (PubSubIQ) iqResponse;
                Iterator<com.leidos.xchangecore.core.infrastructure.xmpp.extensions.util.Item> it = pubsub.getItems();
                while (it.hasNext()) {
                     String item = it.next().toXML();
//                     logger.info("ITEM: "+item);
//                     logger.info("XML: "+iqResponse.toXML());
                    if (!item.isEmpty()) {
                    	messages.add(item);
                    }
                }
            } else {
                messages.add(iqResponse.getChildElementXML());
            }

            // logger.info("CHILD ELEMENT: "+iqResponse.getChildElementXML());
            // ArbitraryPacketExtension arb = new
            // ArbitraryPacketExtension("event", "http://jabber.org/protocol/pubsub",
            // iqResponse.getChildElementXML());
            // Message msg = new Message();
            // msg.addExtension(arb);
            // messages.add(msg.toXML());

        }

        return messages;
    }

    private void printSubs() {
        IQ iq = PubSubIQFactory.retrieveSubscriptions(coreConnection.getPubSubSvc());

        System.out.println(iq.toXML());
        CommandWithReply cmd = null;
		try {
			cmd = coreConnection.createCommandWithReply(iq);
		} catch (XMPPException e) {
			logger.error("Error creating command to retrieve subscriptions: " + e.getMessage());
			return;
		}
		
        if (cmd.waitForSuccessOrFailure()) {

        } else {
            logger.error("  message: " + cmd.getErrorMessage());
            logger.error("     code: " + cmd.getErrorCode());
            logger.error("     type: " + cmd.getErrorType());
            logger.error("condition: " + cmd.getErrorCondition());
            if (cmd.getResult() != null) {
                logger.error(cmd.getResult().toXML());
            }
            if (cmd.getErrorType() == XMPPError.Type.AUTH) {
                logger.error("   Not authorized for this action.");
            }
        }

        IQ iqResponse = cmd.getResult();
        if (iqResponse != null) {

            if (iqResponse instanceof PubSubIQ) {
            	System.out.println("GOT Subscriptions:");
                // logger.info(
                // "GOT a PubSubIQ from initial updates in NodeSubscription:getAllNodeItems");
                PubSubIQ pubsub = (PubSubIQ) iqResponse;
                System.out.println(pubsub.toXML());
            } else {
                System.out.println("not " + iqResponse.toXML());
            }
        } else {
            System.out.println("GOT NO");
        }
    }

    private void run() {
        // Clean out all the nodes
        coreXMPPUtils.deleteAllNodesRecursivly();

        // Create a base collection
        if (!interestManager.addCollection(coreConnection.getPubSubSvc(),interestGroupRoot)) {
        	System.err.println("ERROR - adding a collection to the InterestManager");
        }

        // Add a leaf node
        if (!interestManager.addNode(coreConnection.getPubSubSvc(),interestGroupRoot, interestGroupNode, NODE_ITEM_TYPE.ITEM_LIST, "")) {
        	System.err.println("ERROR - adding leaf node failed");
        }

        // Add items to the node
        String xml1 = "<thing1>stuff</thing1>";
        String item1ID = "item1";
        String itemText = PubSubIQFactory.createItemXML(xml1, item1ID);
        if (!interestManager.publishToNode(coreConnection.getPubSubSvc(),interestGroupNode, itemText)) {
        	System.err.println("ERROR - publishing stuff item to leaf node failed");
        }

        String xml2 = "<thing2>other stuff</thing2>";
        String item2ID = "item2";
        itemText = PubSubIQFactory.createItemXML(xml2, item2ID);
        if (!interestManager.publishToNode(coreConnection.getPubSubSvc(),interestGroupNode, itemText)) {
        	System.err.println("ERROR - publishing stuff item to leaf node failed");
        }
        
        interestManager.getNodeManager(coreConnection.getPubSubSvc()).updateSubscriptionMap();

        if (interestManager.getNodeManager(coreConnection.getPubSubSvc()).isSubscribedTo(interestGroupNode)) {
            logger.info("is subed to " + interestGroupNode);
            String subID = interestManager.getNodeManager(coreConnection.getPubSubSvc()).getSubscriptionID(interestGroupNode);
            String[] ids = new String[1];
            ids[0] = item1ID;
            IQ iq = PubSubIQFactory.retrieveItem(coreConnection.getPubSubSvc(), interestGroupNode,
                    subID, item1ID);
            logger.info(iq.toXML());

            ArrayList<String> messages = getItems(iq);
            boolean found = false;
            for (String item : messages) {
                System.out.println("ITEMS: " + item);
                if (item.contains("stuff")) {
                	found = true;
                }
            }
            if (!found) System.err.println("ERROR - didn't find stuff element");

            Set<XMPPNode> nodes = coreXMPPUtils.getXMPPNodesRecusivly(interestGroupRoot);
            if (nodes.size() != 1) {
            	System.err.println("ERROR - wrong number of nodes");
            }
            if (!nodes.isEmpty()) {
                Iterator<XMPPNode> it = nodes.iterator();
                while (it.hasNext()) {
                	System.out.println("RECURSIVE: "+it.next());
                }
            }
        }
        else {
        	System.err.println("ERROR - not subscribed to " + interestGroupNode);
        }

        // show subscriptions
        printSubs();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        BasicConfigurator.configure();

        ApplicationContext context = new ClassPathXmlApplicationContext(
                new String[] { "contexts/applicationContext.xml" });

        CoreConnection coreConnection = (CoreConnection) context.getBean("coreConnection");

        TestNodesWithItems test = new TestNodesWithItems();
        test.setCoreConnection(coreConnection);

        InterestManager im = (InterestManager) context.getBean("interestManager");
        im.setCoreConnection(coreConnection);
        im.initialize();
        im.addNodeManager(coreConnection.getPubSubSvc());
        test.setInterestManager(im);

        if (coreConnection.isConnected()) {
            CoreXMPPUtils cu = new CoreXMPPUtils();
            cu.setCoreConnection(coreConnection);
            cu.setInterestManager(im);
            test.setCoreXMPPUtils(cu);

            test.run();
        }
        else {
        	System.err.println("ERROR connecting to XMPP server");
        }

    }

}
