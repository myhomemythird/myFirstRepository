package com.acn.gzmcs.smscombiner.common;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Properties;

public class PropertyLoader
{
  private static final String propFilepath = System.getProperty("user.dir") + "/config.properties";
  private Properties prop = new Properties();

  public PropertyLoader() {
    File file = new File(propFilepath);

    InputStream in = null;
    try {
      in = new BufferedInputStream(new FileInputStream(file));
      this.prop.load(in);

      in.close();
    }
    catch (FileNotFoundException e1)
    {
      e1.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  public PropertyLoader(String filepath)
  {
    File file = new File(filepath);

    InputStream in = null;
    try {
      in = new BufferedInputStream(new FileInputStream(file));
      this.prop.load(in);

      in.close();
    }
    catch (FileNotFoundException e1)
    {
      e1.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String getValue(String key)
  {
    if ((key != null) && (!"".equals(key)))
    {
      return this.prop.getProperty(key);
    }

    return null;
  }

  public Properties getProp()
  {
    return this.prop;
  }

  public void setProp(Properties prop)
  {
    this.prop = prop;
  }

  public static String getPropfilepath()
  {
    return propFilepath;
  }

  public static void main(String[] args)
  {
    PropertyLoader pLoader = new PropertyLoader();
    Enumeration em = pLoader.getProp().propertyNames();
    while (em.hasMoreElements()) {
      String key = (String)em.nextElement();
      String value = pLoader.getValue(key);
      System.out.println(key + "  : " + value);
    }

    System.out.println(pLoader.getValue("hk.sendFrom"));
  }
}
