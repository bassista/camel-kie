package org.camel.kie.executor;

import org.apache.log4j.Logger;
import org.camel.kie.maven.Scanner;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

@SuppressWarnings("unused")
public class DroolsExecutorMaven extends DroolsExecutor{
  private static final Logger log=Logger.getLogger(DroolsExecutorMaven.class);
  
  @Override
  public KieSession initKieSession() {
    if (null==e.getReleaseId()) throw new RuntimeException("KieReleaseId cannot be null. Please set this property prior to using the "+this.getClass().getSimpleName());
    if (e.getReleaseId().split(":").length!=3) throw new RuntimeException("KieReleaseId must be in the format \"groupId:artifactId:version\".");
    
    log.info("Using "+this.getClass().getSimpleName());
    log.debug(String.format("Using Kie ReleaseId '%s'", e.getReleaseId()));
    
    KieServices ks=KieServices.Factory.get();
    ReleaseId gav=new ReleaseIdImpl(e.getReleaseId());
    KieBaseConfiguration kConfiguration=ks.newKieBaseConfiguration();
    
    // TODO: mallen - add any config on the KieBaseConfiguration from the camel endpoint here...
    
    KieBase kBase=null;
    if (null!=e.getKieMavenSettings() && !"".equals(e.getKieMavenSettings())){
      System.setProperty("kie.maven.settings.custom", e.getKieMavenSettings());
      log.debug(String.format("Setting system property kie.maven.settings.custom to '%s'", e.getKieMavenSettings()));
    }
    
    // TODO: mallen - need to make this a singleton at some point
//    Scanner scanner=new Scanner(gav).start(Long.parseLong(e.getKieMavenScanInterval()));
    Scanner scanner=new Scanner(gav).scanNow();
    KieContainer kContainer=ks.newKieContainer(gav);
    
    kBase=e.getKieBaseName()!=null?kContainer.newKieBase(e.getKieBaseName(), kConfiguration):kContainer.newKieBase(kConfiguration);
    return kBase.newKieSession();
  }

}
