package org.camel.kie.component;

import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;

public class DroolsComponent extends DefaultComponent {
  public DroolsComponent() {}
  public DroolsComponent(CamelContext context) {
    super(context);
  }

  @Override
  protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
    DroolsEndpoint endpoint=new DroolsEndpoint(uri, remaining, this);
    setProperties(endpoint, parameters);
    endpoint.setReleaseId(remaining);
    return endpoint;
  }
  
  @Override
  protected void validateParameters(String uri, Map<String, Object> parameters, String optionPrefix) {
    System.out.println("validating...");
  }

}
