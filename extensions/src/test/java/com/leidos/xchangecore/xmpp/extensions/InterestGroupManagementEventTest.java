package com.leidos.xchangecore.xmpp.extensions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jivesoftware.smack.packet.Message;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.leidos.xchangecore.core.infrastructure.xmpp.extensions.interestgroupmgmt.InterestGrpManagementEventFactory;

/**
 * Unit test for simple App.
 */
public class InterestGroupManagementEventTest {

    final static String coreName = "danzig";
    final static String uuid = "uuid";
    final static String from = "clash";
    final static String topic = "interestgroup";
    final static String content = "contents";
    final static String contents = "<contents>" + content + "</contents>";
    static final String elementName = "interestgroupmgmt";
    static final String namespace = "http://uicds.saic.com/xmpp/extensions/interestgroupmgmt#event";
    static final String sendUpdateMessage = "<intestergroupmgmt xmlns='http://uicds.saic.com/xmpp/extensions/interestgroupmgmt#event'><uuid>uuid</uuid><topic>interestgroup</topic><interestgroupmgmt_content><contents>contents</contents></interestgroupmgmt_content></interestgroupmgmt>";

    // sendUpdateToOwner
    @Test
    public void testSendUpdateToOwner() {

        Message message = InterestGrpManagementEventFactory.sendUpdateToOwner(coreName,
            uuid,
            from,
            topic,
            contents);
        assertNotNull(message.getExtension(namespace));
        checkUpdateMessage(message.getExtension(namespace).toXML());
    }

    private void checkUpdateMessage(String xml) {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();
            try {
                Document doc = db.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
                checkIsInterestGroupMgmt(doc);
                checkElementContent(doc, "uuid", uuid);
                checkElementContent(doc, "topic", topic);
                checkElementContent(doc, "interestgroupmgmt_content", content);
                checkElementContent(doc, "contents", content);
            } catch (SAXException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void checkElementContent(Document doc, String nodeName, String nodeContent) {

        NodeList nodeList = doc.getElementsByTagName(nodeName);
        assertNotNull("no " + nodeName + " elements found", nodeList);
        assertTrue("no " + nodeName + " element", nodeList.getLength() > 0);
        assertNotNull("node is null", nodeList.item(0));
        assertEquals("wrong content for " + nodeName,
            nodeContent,
            nodeList.item(0).getTextContent());

    }

    private void checkIsInterestGroupMgmt(Document doc) {

        NodeList nodeList = doc.getElementsByTagName("interestgroupmgmt");
        assertNotNull("no interestgroupmgmt element", nodeList);
    }
}
