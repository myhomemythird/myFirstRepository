package com.acn.gzmcs.smscombiner.bean;

import java.util.Vector;

public class MailBean
{
  private String to;
  private String cc;
  private String from;
  private String host;
  private String username;
  private String password;
  private String subject;
  private String content;
  Vector<String> file;
  private String filename;

  public String getTo()
  {
    return this.to;
  }

  public void setTo(String to) {
    this.to = to;
  }

  public String getCc() {
    return this.cc;
  }

  public void setCc(String cc) {
    this.cc = cc;
  }

  public String getFrom() {
    return this.from;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  public String getHost() {
    return this.host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return this.password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getSubject() {
    return this.subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getContent() {
    return this.content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getFilename() {
    return this.filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public Vector<String> getFile() {
    return this.file;
  }

  public void attachFile(String fileName) {
    if (this.file == null)
      this.file = new Vector();
    this.file.addElement(fileName);
  }
}
