package org.camel.kie.maven;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Repository;
import org.apache.maven.settings.Server;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RepositoryPolicy;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.util.repository.AuthenticationBuilder;

public class Maven {
  private static final Logger log=Logger.getLogger(Maven.class);
  private List<RemoteRepository> remoteRepositories;
  private LocalRepository localRepository;
  
  public Maven(){
    this.remoteRepositories=new ArrayList<RemoteRepository>();
    this.remoteRepositories.add(Booter.newCentralRepository());
  }
  public Maven(LocalRepository localRepository){
    this();
    log.debug("setting local repository: "+localRepository.getBasedir().getPath());
    this.localRepository=localRepository;
  }
  
  public Maven addRepository(RemoteRepository newRemoteRepo){
    log.debug("adding maven remote repository: "+newRemoteRepo.getUrl());
    remoteRepositories.add(newRemoteRepo); return this;
  }
  
  private RemoteRepository toRemoteRepository(Repository r, Server server){
    RemoteRepository.Builder builder=new RemoteRepository.Builder(r.getId(), r.getLayout(), r.getUrl());
    builder.setSnapshotPolicy(new RepositoryPolicy(r.getSnapshots().isEnabled(), r.getSnapshots().getUpdatePolicy(), r.getSnapshots().getChecksumPolicy()));
    builder.setReleasePolicy(new RepositoryPolicy(r.getReleases().isEnabled(), r.getReleases().getUpdatePolicy(), r.getReleases().getChecksumPolicy()));
    if (server!=null)
      builder.setAuthentication(new AuthenticationBuilder().addUsername(server.getUsername()).addPassword(server.getPassword()).build());
    return builder.build();
  }
  
  public Maven applySettingsXml(){
    localRepository=new LocalRepository(MavenSettings.getSettings().getLocalRepository());
    log.debug("setting local repository: "+localRepository.getBasedir().getPath());
    
    for(Profile profile:MavenSettings.getSettings().getProfiles()){
      if (MavenSettings.getSettings().getActiveProfiles().contains(profile.getId()) ||
          (profile.getActivation()!=null && profile.getActivation().isActiveByDefault())
          ){
        for (Repository repository:profile.getRepositories()){
          addRepository(toRemoteRepository(repository, MavenSettings.getSettings().getServer(repository.getId())));
        }
      }
    }
    return this;
  }
  
  public Artifact resolveArtifact(String gav){ // gav in format "org.eclipse.aether:aether-util:[0,)"
    if (gav.split(":").length!=3) throw new RuntimeException("Format for GAV should be 'groupId:artifactId:version' where version can be a literal or a range");
    RepositorySystem system = Booter.newRepositorySystem();
    RepositorySystemSession session = Booter.newRepositorySystemSession(system, localRepository);
    Artifact artifact=new DefaultArtifact(gav);
    try{
      if (isVersionRange(artifact.getVersion())){
        VersionRangeRequest rangeRequest=new VersionRangeRequest().setArtifact(artifact).setRepositories(remoteRepositories);
        VersionRangeResult rangeResult=system.resolveVersionRange(session, rangeRequest);
        artifact=artifact.setVersion(rangeResult.getHighestVersion().toString());
      }
      ArtifactRequest artifactRequest=new ArtifactRequest().setArtifact(artifact).setRepositories(remoteRepositories);
      ArtifactResult artifactResult=system.resolveArtifact(session, artifactRequest);
      log.debug(artifactResult.getArtifact()+" resolved to "+artifactResult.getArtifact().getFile());
      return artifactResult.getArtifact();
    }catch(ArtifactResolutionException e){
      throw new RuntimeException(e.getMessage(), e);
    }catch (VersionRangeResolutionException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
  
//  public Version findNewestVersion(String gav) throws VersionRangeResolutionException{ // gav in format "org.eclipse.aether:aether-util:[0,)"
//    if (gav.split(":").length!=3) throw new RuntimeException("Format for GAV should be 'groupId:artifactId:version' where version can be a literal or a range");
//    RepositorySystem system = Booter.newRepositorySystem();
//    RepositorySystemSession session = Booter.newRepositorySystemSession(system, localRepository);
//    Artifact artifact = new DefaultArtifact(gav);
//    VersionRangeRequest rangeRequest = new VersionRangeRequest();
//    rangeRequest.setArtifact(artifact);
////    rangeRequest.setRepositories( Booter.newRepositories( system, session ) );
//    rangeRequest.setRepositories(remoteRepositories);
//    VersionRangeResult rangeResult = system.resolveVersionRange(session, rangeRequest);
//    Version newestVersion = rangeResult.getHighestVersion();
//    log.debug("Newest version " + newestVersion + " from repository " + rangeResult.getRepository( newestVersion));
//    return newestVersion;
//  }
  
//  public Artifact resolveArtifact2(String gav) throws ArtifactResolutionException{ // gav in format "org.eclipse.aether:aether-util:[0,)"
//    if (gav.split(":").length!=3) throw new RuntimeException("Format for GAV should be 'groupId:artifactId:version' where version can be a literal or a range");
//    RepositorySystem system = Booter.newRepositorySystem();
//    RepositorySystemSession session = Booter.newRepositorySystemSession(system, localRepository);
//    Artifact artifact = new DefaultArtifact(gav);
//    ArtifactRequest artifactRequest = new ArtifactRequest();
//    artifactRequest.setArtifact(artifact);
////  rangeRequest.setRepositories( Booter.newRepositories( system, session ) );
//    artifactRequest.setRepositories(remoteRepositories);
//    ArtifactResult artifactResult = system.resolveArtifact(session, artifactRequest);
//    artifact = artifactResult.getArtifact();
//    log.debug(artifact + " resolved to  " + artifact.getFile());
//    return artifact;
//  }
  
  private static boolean isVersionRange(String version){
    return version.contains("(") || version.contains(")") ||
        version.contains("[") || version.contains("]");
  }
}
