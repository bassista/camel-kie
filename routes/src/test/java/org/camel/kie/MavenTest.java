package org.camel.kie;

import org.camel.kie.maven.Maven;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class MavenTest {
  
  @Before
  public void before(){
    System.setProperty("kie.maven.settings.custom", "src/main/resources/client-settings.xml");  
  }
  
  @Test
  public void testManualRepositories() throws Exception{
    new Maven(new LocalRepository("target/local-repo"))
      .addRepository(new RemoteRepository.Builder("snapshots", "default", "http://localhost:9000/nexus/content/repositories/snapshots").build())
      .addRepository(new RemoteRepository.Builder("releases", "default", "http://localhost:9000/nexus/content/repositories/releases").build())
      .addRepository(new RemoteRepository.Builder("local", "default", "file:///home/mallen/.m2/repository").build())
      .resolveArtifact("com.redhat.fuse:camel-kie-example-rules:[1.0-SNAPSHOT,)");
  }
  
  @Test
  public void testSettingsXml() throws Exception{
    new Maven().applySettingsXml()
      .resolveArtifact("com.redhat.fuse:camel-kie-example-rules:[1.0,)")
      ;
  }
}
