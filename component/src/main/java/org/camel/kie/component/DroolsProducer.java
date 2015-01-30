package org.camel.kie.component;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.apache.camel.language.simple.SimpleExpressionParser;
import org.camel.kie.executor.DroolsExecutor;

public class DroolsProducer extends DefaultProducer{

  public DroolsProducer(Endpoint endpoint) {
    super(endpoint);
  }

  public void process(Exchange exchange) throws Exception {
    DroolsEndpoint e=(DroolsEndpoint)getEndpoint();
    DroolsExecutor bean=DroolsExecutor.Factory.get(e.getMethod());
    bean.setEndpoint(e);
    Object facts=new SimpleExpressionParser(e.getFacts(),false).parseExpression().evaluate(exchange, Object.class);
    bean.fireRules(facts);
//      exchange.getOut().setBody(exchange.getIn().getBody());
//      exchange.getOut().setHeaders(exchange.getIn().getHeaders());
  }

}
