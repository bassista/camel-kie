package org.camel.kie.component;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.camel.kie.executor.DroolsExecutor;

public class DroolsEndpoint extends DefaultEndpoint {
  private String method=DroolsExecutor.Method.MAVEN.name(); // default is to use maven to resolve the kie modules
  private String facts="${body}"; // default is entire body object
  private String releaseId;
  private String kieBaseName;
//  private String server;
//  private String username;
//  private String password;
  private String kieMavenSettings;
  private String kieMavenScanInterval="60000"; // default = every 60 seconds 
  
  public String getKieBaseName(){ return this.kieBaseName; }
  public String getMethod(){ return this.method; }
  public String getReleaseId(){ return this.releaseId; }
  public String getFacts(){  return this.facts; }
//  public String getServer(){  return this.server; }
//  public String getUsername(){  return this.username; }
//  public String getPassword(){  return this.password; }
  public String getKieMavenSettings() { return this.kieMavenSettings; }
  public String getKieMavenScanInterval() { return this.kieMavenScanInterval; }

  public void setKieBaseName(String value) {
    this.kieBaseName=value;
  }
  public void setMethod(String value) {
    this.method=value;
  }
  public void setFacts(String value) {
    this.facts=value;
  }
  public void setReleaseId(String value) {
    this.releaseId=value;
  }
//  public void setServer(String value) {
//    this.server=value;
//  }
//  public void setUsername(String value) {
//    this.username=value;
//  }
//  public void setPassword(String value) {
//    this.password=value;
//  }
  public void setKieMavenSettings(String value){
    this.kieMavenSettings=value;
  }
  public void setKieMavenScanInterval(String value){
    this.kieMavenScanInterval=value;
  }

  public DroolsEndpoint(String endpointUri, String remaining, DroolsComponent component) {
    super(endpointUri, component);
    // configure(component, remaining);
  }

  public Consumer createConsumer(Processor arg0) throws Exception {
    return null;
  }

  public Producer createProducer() throws Exception {
    return new DroolsProducer(this);
  }

  public boolean isSingleton() {
    return true;
  }
}
