package net.sf.lnn.util;

import java.util.Properties;

public class BooleanPropertyChecker {

  private final static String [] FALSE_NAMES = { "false", "no", "0" };

  public static boolean getProperty(Properties properties, String key) {

    String value = properties.getProperty(key);
    if(value == null)
      return false;

    value = value.trim();

    for(int i = 0; i < FALSE_NAMES.length; i++)
      if(value.equalsIgnoreCase(FALSE_NAMES[i]))
        return false;

    return true;
  }
}