package org.camel.kie.maven;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Repository;
import org.apache.maven.settings.Server;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.providers.http.LightweightHttpWagon;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.LocalRepositoryManager;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RepositoryPolicy;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.transport.wagon.WagonProvider;
import org.eclipse.aether.util.repository.AuthenticationBuilder;

public class Maven {
  private static final Logger log=Logger.getLogger(Maven.class);
  private List<RemoteRepository> remoteRepositories;
  private LocalRepository localRepository;
  
  public Maven() {
    this.remoteRepositories=new ArrayList<RemoteRepository>();
    this.remoteRepositories.add(MavenRepositoryFactory.newCentralRepository());
  }

  public Maven addRepository(RemoteRepository newRemoteRepo) {
    log.debug("adding maven remote repository: "+newRemoteRepo.getUrl());
    remoteRepositories.add(newRemoteRepo);
    return this;
  }

  private RemoteRepository toRemoteRepository(Repository r, Server server) {
    RemoteRepository.Builder builder=new RemoteRepository.Builder(r.getId(), r.getLayout(), r.getUrl());
    builder.setSnapshotPolicy(new RepositoryPolicy(r.getSnapshots().isEnabled(), r.getSnapshots().getUpdatePolicy(), r.getSnapshots().getChecksumPolicy()));
    builder.setReleasePolicy(new RepositoryPolicy(r.getReleases().isEnabled(), r.getReleases().getUpdatePolicy(), r.getReleases().getChecksumPolicy()));
    if (server!=null) builder.setAuthentication(new AuthenticationBuilder().addUsername(server.getUsername()).addPassword(server.getPassword()).build());
    return builder.build();
  }

  public Maven applySettingsXml() {
    localRepository=new LocalRepository(MavenSettings.getSettings().getLocalRepository());
    log.debug("setting local repository: "+localRepository.getBasedir().getPath());

    for (Profile profile : MavenSettings.getSettings().getProfiles()) {
      if (MavenSettings.getSettings().getActiveProfiles().contains(profile.getId())
          ||(profile.getActivation()!=null&&profile.getActivation().isActiveByDefault())) {
        for (Repository repository : profile.getRepositories()) {
          addRepository(toRemoteRepository(repository, MavenSettings.getSettings().getServer(repository.getId())));
        }
      }
    }
    return this;
  }

  public Artifact resolveArtifact(String gav) { // gav in format
                                                // "org.eclipse.aether:aether-util:[0,)"
    if (gav.split(":").length!=3) throw new RuntimeException("Format for GAV should be 'groupId:artifactId:version' where version can be a literal or a range");
    RepositorySystem system=MavenRepositoryFactory.newRepositorySystem();
    RepositorySystemSession session=MavenRepositoryFactory.newRepositorySystemSession(system, localRepository);
    Artifact artifact=new DefaultArtifact(gav);
    try {
      if (isVersionRange(artifact.getVersion())) {
        VersionRangeRequest rangeRequest=new VersionRangeRequest().setArtifact(artifact).setRepositories(remoteRepositories);
        VersionRangeResult rangeResult=system.resolveVersionRange(session, rangeRequest);
        artifact=artifact.setVersion(rangeResult.getHighestVersion().toString());
      }
      ArtifactRequest artifactRequest=new ArtifactRequest().setArtifact(artifact).setRepositories(remoteRepositories);
      ArtifactResult artifactResult=system.resolveArtifact(session, artifactRequest);
      log.debug(artifactResult.getArtifact()+" resolved to "+artifactResult.getArtifact().getFile());
      return artifactResult.getArtifact();
    } catch (ArtifactResolutionException e) {
      throw new RuntimeException(e.getMessage(), e);
    } catch (VersionRangeResolutionException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  private static boolean isVersionRange(String version) {
    return version.contains("(")||version.contains(")")||version.contains("[")||version.contains("]");
  }

  private static class MavenRepositoryFactory {
    public static RepositorySystemSession newRepositorySystemSession(RepositorySystem system, LocalRepository localRepository) {
      DefaultRepositorySystemSession session=MavenRepositorySystemUtils.newSession();
      LocalRepositoryManager x=system.newLocalRepositoryManager(session, localRepository);
      session.setLocalRepositoryManager(x);
      // session.setTransferListener(new ConsoleTransferListener());
      // session.setRepositoryListener(new ConsoleRepositoryListener());
      return session;
    }

    public static RepositorySystem newRepositorySystem() {
      DefaultServiceLocator locator=MavenRepositorySystemUtils.newServiceLocator();
      locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
      locator.addService(TransporterFactory.class, FileTransporterFactory.class);
      locator.addService(TransporterFactory.class, HttpTransporterFactory.class);
      locator.setServices(WagonProvider.class,new Maven.ManualWagonProvider());
      locator.setErrorHandler(new DefaultServiceLocator.ErrorHandler() {
        @Override
        public void serviceCreationFailed(Class<?> type, Class<?> impl, Throwable exception) {
          exception.printStackTrace();
        }
      });
      return locator.getService(RepositorySystem.class);
    }

    public static RemoteRepository newCentralRepository() {
      return new RemoteRepository.Builder("central", "default", "http://central.maven.org/maven2/").build();
    }
  }
  
  public static class ManualWagonProvider implements WagonProvider {
    public Wagon lookup(String roleHint) throws Exception {
      if ("http".equals(roleHint)) {
        return new LightweightHttpWagon();
      }else if ("file".equals(roleHint)) {
        return new org.apache.maven.wagon.providers.file.FileWagon();
      }
      return null;
    }

    public void release(Wagon wagon) {
    }
  }
}
