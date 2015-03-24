package org.camel.kie.domain;

import org.camel.kie.domain.PhoneCall.Type;

public class PhoneCallBuilder {
  private Country fromCountry;
  private String from;
  private Country toCountry;
  private String to;
  private Type type;
  private long duration;
  public PhoneCallBuilder from(Country cty, String value){this.from=value; this.fromCountry=cty; return this;}
  public PhoneCallBuilder to(Country cty, String value){this.to=value; this.toCountry=cty; return this;}
  public PhoneCallBuilder type(Type value){this.type=value; return this;}
  public PhoneCallBuilder duration(long value){this.duration=value; return this;}
  public PhoneCall build(){
    PhoneCall result=new PhoneCall();
    result.setFromCountry(fromCountry);
    result.setFrom(from.replaceAll(" ", ""));
    result.setToCountry(toCountry);
    result.setTo(to.replaceAll(" ", ""));
    result.setType(type);
    result.setDuration(duration);
    return result;
  }
}
