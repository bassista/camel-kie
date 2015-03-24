package org.camel.kie;

import java.util.ArrayList;
import java.util.List;

import org.camel.kie.component.DroolsEndpoint;
import org.camel.kie.domain.PhoneCall;
import org.camel.kie.executor.DroolsExecutor;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class KieExecutorMavenTest {
  
  @Test
  public void test() throws Exception{
    String endpointUri="drools:com.redhat.fuse:camel-kie-example-rules:1.0-SNAPSHOT?facts=${body.phoneCalls}&amp;method=maven&amp;kieBaseName=defaultKieBase&amp;kieMavenSettings=/home/mallen/Work/poc/camel-kie-example/routes/src/main/resources/client-settings.xml";
    String remaining="com.redhat.fuse:camel-kie-example-rules:1.0-SNAPSHOT";
    DroolsEndpoint e=new DroolsEndpoint(endpointUri, remaining, new org.camel.kie.component.DroolsComponent());
    e.setMethod("maven");
    e.setKieBaseName("KBase1");
    e.setReleaseId(remaining);
    
    DroolsExecutor test=DroolsExecutor.Factory.get(e.getMethod());
    test.setEndpoint(e);
    List<PhoneCall> facts=new ArrayList<PhoneCall>();
    facts.add(new PhoneCall());
    facts.get(0).setFrom("01234567");
    facts.get(0).setTo("9874353987");
    test.fireRules(facts);
    System.out.println(facts);
    
    for(PhoneCall p:facts)
      Assert.assertEquals("987435XXXX", p.getTo());
    
  }
}
