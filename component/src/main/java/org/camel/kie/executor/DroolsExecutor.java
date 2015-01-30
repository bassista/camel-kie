package org.camel.kie.executor;

import org.apache.log4j.Logger;
import org.camel.kie.component.DroolsEndpoint;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.KieSession;

public abstract class DroolsExecutor {
  private static final Logger log=Logger.getLogger(DroolsExecutor.class);
  protected DroolsEndpoint e;
  public enum Method{/*HTTP,*/MAVEN,CLASSPATH}
  
  public void setEndpoint(DroolsEndpoint e){
    this.e=e;
  }
  
  public static class Factory{
    public static DroolsExecutor get(String method){
      switch(Method.valueOf(method.toUpperCase())){
      case CLASSPATH:
        return new DroolsExecutorClasspath();
//      case HTTP:
//        return new KieExecutorHttp();
      case MAVEN:
        return new DroolsExecutorMaven();
      default:
        throw new RuntimeException("Unknown method provided - "+method);
      }
    }
  }
  
  public abstract KieSession initKieSession();
  
  @SuppressWarnings("rawtypes")
  public void fireRules(Object body){
    log.info("Executing rules body(Class="+body.getClass().getCanonicalName()+")...");
    
    KieSession kSession=initKieSession();
    
    try {
      if (Iterable.class.isAssignableFrom(body.getClass())) {
        log.debug("Detected \"Iterable\" facts, iterating through inserting each item");
        for (Object fact : (Iterable) body) {
          kSession.insert(fact);
        }
      } else
        kSession.insert(body);
      
      if (0==kSession.getKieBase().getKiePackages().size()) log.warn("WARNING: No rule packages (and therefore rules) in kieSession");
      for(KiePackage p:kSession.getKieBase().getKiePackages())
        for(Rule r:p.getRules())
          log.debug("KiePackage["+p.getName()+"].["+r.getName()+"]");
      for(Object fact:kSession.getObjects()){
        log.debug("Fact["+fact.toString()+"]");
      }
      
      
      log.debug("firing rules");
      int ruleFiredCount=kSession.fireAllRules();
      log.debug(String.format("fired %s rules", ruleFiredCount));
      
    } finally {
      log.debug("disposing of the kSession");
      kSession.dispose();
      if (null!=e.getKieMavenSettings()){
        log.debug("Clearing system property 'kie.maven.settings.custom'");
        System.clearProperty("kie.maven.settings.custom");
      }
    }
  }
}
