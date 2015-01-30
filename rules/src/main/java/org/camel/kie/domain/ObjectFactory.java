package org.camel.kie.domain;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {
  public ObjectFactory() {
  }

  public PhoneCallSummary createPhoneCallSummary() {
    return new PhoneCallSummary();
  }

}
