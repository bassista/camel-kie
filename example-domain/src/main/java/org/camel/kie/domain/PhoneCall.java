package org.camel.kie.domain;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

public class PhoneCall {
  public enum Type {
    Call, SMS
  }
  
  private Country fromCountry;
  private String from;
  private Country toCountry;
  private String to;
  private Type type;
  private long duration;

  @XmlAttribute()
  public Country getFromCountry() {
    return fromCountry;
  }
  public void setFromCountry(Country fromCountry) {
    this.fromCountry=fromCountry;
  }
  @XmlAttribute
  public Country getToCountry() {
    return toCountry;
  }
  public void setToCountry(Country toCountry) {
    this.toCountry=toCountry;
  }
  @XmlAttribute
  public String getFrom() {
    return from;
  }
  public void setFrom(String from) {
    this.from=from;
  }
  @XmlAttribute
  public String getTo() {
    return to;
  }
  public void setTo(String to) {
    this.to=to;
  }
  @XmlAttribute
  public Type getType() {
    return type;
  }
  public void setType(Type type) {
    this.type=type;
  }
  @XmlAttribute
  public long getDuration() {
    return duration;
  }
  public void setDuration(long duration) {
    this.duration=duration;
  }
  public String toString(){
    return "PhoneCall[from="+from+", to="+to+", duration="+duration+"]";
  }
}
