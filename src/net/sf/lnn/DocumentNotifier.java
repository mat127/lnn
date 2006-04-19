package net.sf.lnn;

import java.util.Properties;

import net.sf.lnn.util.BooleanPropertyChecker;

import lotus.domino.Document;

public abstract class DocumentNotifier {

  protected Properties cfg;
  
  public DocumentNotifier(Properties cfg) {
    loadConfig(cfg);
  }

  public void loadConfig(Properties cfg) {
    this.cfg = getDefaultConfig();
    this.cfg.putAll(cfg);
  }

  protected Properties getDefaultConfig() {
    return new Properties();
  }

  protected boolean isDebugEnabled() {
    return BooleanPropertyChecker.getProperty(cfg, LotusNotifierAgent.CFG_DEBUG);
  }

  public abstract void notify(Document doc) throws DocumentNotifierException;
}