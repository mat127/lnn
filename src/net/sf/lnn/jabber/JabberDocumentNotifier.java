package net.sf.lnn.jabber;

import java.util.Properties;

import net.sf.lnn.DocumentNotifier;
import net.sf.lnn.DocumentNotifierException;
import net.sf.lnn.util.BooleanPropertyChecker;

import org.jivesoftware.smack.SSLXMPPConnection;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import lotus.domino.Document;
import lotus.domino.NotesException;

public class JabberDocumentNotifier extends DocumentNotifier {

  private final static String   CFG_DOC_ITEM_DELIMITER = ",";

  private final static String   CFG_JABBER_SERVER =   "jabber.server";
  private final static String   CFG_JABBER_SERVER_SSL = "jabber.server.ssl";
  private final static String   CFG_JABBER_USER =     "jabber.user.name";
  private final static String   CFG_JABBER_PASSWORD = "jabber.user.password";
  private final static String   CFG_JABBER_TARGET =   "jabber.target";
  private final static String   CFG_DOC_ITEM_NAMES =  "jabber.doc.items";

  public JabberDocumentNotifier(Properties cfg) {
    super(cfg);
  }
  
  protected Properties getDefaultConfig() {
    Properties cfg = super.getDefaultConfig();
    cfg.setProperty(CFG_JABBER_SERVER, "jabber.org");
    cfg.setProperty(CFG_DOC_ITEM_NAMES, "From,Subject");
    return cfg;
  }

  public void notify(Document doc) throws DocumentNotifierException {
    try {
      sendNotification(buildNotification(doc));
    }
    catch(Exception e) {
      throw new DocumentNotifierException("notification failed", e);
    }
  }

  public String buildNotification(Document doc) throws NotesException {
    StringBuffer msg = new StringBuffer("new mail arrived");

    String itemList = cfg.getProperty(CFG_DOC_ITEM_NAMES);
    if(itemList == null)
      return msg.toString();

    String [] itemNames = itemList.split(CFG_DOC_ITEM_DELIMITER);
    for(int i = 0; i < itemNames.length; i++) {
      String name = itemNames[i].trim();
      msg.append("\n").append(name).append(": ").append(doc.getItemValueString(name));
    }

    return msg.toString();
  }

  public void sendNotification(String msg) throws XMPPException {
    
    XMPPConnection connection = getConnection();

    String target = getTarget();

    connection.createChat(target.toString()).sendMessage(msg);
    if(isDebugEnabled())
      System.out.println("message sent to " + target);

    connection.close();
    if(isDebugEnabled())
      System.out.println("connection closed");
  }

  private String getTarget() {

    String target = cfg.getProperty(CFG_JABBER_TARGET);
    if(target != null)
      return target;

    StringBuffer buffer = new StringBuffer();
    buffer.append(cfg.getProperty(CFG_JABBER_USER));
    buffer.append('@').append(cfg.getProperty(CFG_JABBER_SERVER));
    return buffer.toString();
  }

  private XMPPConnection getConnection() throws XMPPException {

    String server = cfg.getProperty(CFG_JABBER_SERVER);
    XMPPConnection connection = BooleanPropertyChecker.getProperty(cfg, CFG_JABBER_SERVER_SSL) ?
      new SSLXMPPConnection(server) : new XMPPConnection(server);
    if(isDebugEnabled())
      System.out.println("connection opened");

    String name = cfg.getProperty(CFG_JABBER_USER);
    String password = cfg.getProperty(CFG_JABBER_PASSWORD);

    connection.login(name, password);
    if(isDebugEnabled())
      System.out.println("logged in");

    return connection; 
  }
}