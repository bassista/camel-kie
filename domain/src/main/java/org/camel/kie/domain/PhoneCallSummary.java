package org.camel.kie.domain;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="call-details")
public class PhoneCallSummary {
  private List<PhoneCall> phoneCalls;
  
  public PhoneCallSummary(){}
  
  public List<PhoneCall> getPhoneCalls() {
    if (null==phoneCalls) phoneCalls=new ArrayList<PhoneCall>();
    return phoneCalls;
  }
  
  @XmlElement(name="call")
  public void setPhoneCalls(List<PhoneCall> phoneCalls) {
    this.phoneCalls=phoneCalls;
  }
}
