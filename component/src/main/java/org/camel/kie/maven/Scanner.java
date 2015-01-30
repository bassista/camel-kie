package org.camel.kie.maven;

import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.setDefaultsforEmptyKieModule;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;
import org.drools.compiler.kie.builder.impl.ZipKieModule;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.eclipse.aether.artifact.Artifact;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;

public class Scanner {
  public static final Logger log=Logger.getLogger(Scanner.class);
  private ReleaseId releaseId;
//  private KieScanner kScanner;
  private Timer timer;
  
  public Scanner(ReleaseId releaseId){
    this.releaseId=releaseId;
//    this.kScanner=KieServices.Factory.get().newKieScanner(kContainer);
  }
  
  public Scanner start(long interval){
    stop();
    scanNow();
    timer=new Timer("KieScanner", true);
    timer.scheduleAtFixedRate(new ScanTask(), 0l, interval);
    return this;
  }
  
  public Scanner stop(){
    if (null!=timer) timer.cancel();
    return this;
  }
  
  public void scanNow(){
    log.debug("Scanning...");
    Artifact kjar=new Maven().applySettingsXml().resolveArtifact(getDelimitedGav(releaseId));
    KieModuleModel kModuleXml=getKieModuleModelFromJar(kjar.getFile());
    KieModule kModule=new ZipKieModule(releaseId, kModuleXml, kjar.getFile());
    KieServices.Factory.get().getRepository().addKieModule(kModule);
  }
  
  private static KieModuleModel getKieModuleModelFromJar(File jar) {
    ZipFile zipFile=null;
    try {
      zipFile=new ZipFile(jar);
      ZipEntry zipEntry=zipFile.getEntry(KieModuleModelImpl.KMODULE_JAR_PATH);
      KieModuleModel kieModuleModel=KieModuleModelImpl.fromXML(zipFile.getInputStream(zipEntry));
      setDefaultsforEmptyKieModule(kieModuleModel);
      return kieModuleModel;
    } catch (Exception e) {
    } finally {
      if (zipFile!=null) {
        try {
          zipFile.close();
        } catch (IOException e) {
        }
      }
    }
    return null;
  }
  
  public class ScanTask extends TimerTask{
    @Override
    public void run() {
      scanNow();
    }
  }
  
  private static String getDelimitedGav(ReleaseId gav){
    return String.format("%s:%s:%s", gav.getGroupId(), gav.getArtifactId(), gav.getVersion());
  }

}
