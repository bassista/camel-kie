package org.camel.kie.executor;

import org.apache.log4j.Logger;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class DroolsExecutorClasspath extends DroolsExecutor{
  private static final Logger log=Logger.getLogger(DroolsExecutorClasspath.class);
  
  @Override
  public KieSession initKieSession() {
    KieServices ks=KieServices.Factory.get();
    KieBaseConfiguration kConfiguration=ks.newKieBaseConfiguration();
    
    log.debug("Initialising KieSession using the classpath only");
    log.warn("ignoring ReleaseId"+(e.getReleaseId()!=null?" ("+e.getReleaseId()+")":""));
    KieContainer kContainer=ks.newKieClasspathContainer();
    
    KieBase kBase=e.getKieBaseName()!=null?kContainer.newKieBase(e.getKieBaseName(), kConfiguration):kContainer.newKieBase(kConfiguration);
    return kBase.newKieSession();
  }

}
