package org.camel.kie;

import java.io.IOException;
import java.util.Dictionary;
import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.apache.log4j.Logger;
import org.junit.Test;

public class MaskNumberRouteTest extends CamelBlueprintTestSupport {
	public static Logger log=Logger.getLogger(MaskNumberRouteTest.class);
	
	@Override
	protected String getBlueprintDescriptor() {
		return  ""
//				+",OSGI-INF/blueprint/activemq.xml"
//				+",OSGI-INF/blueprint/kie.xml"
				+",OSGI-INF/blueprint/properties.xml"
				+",OSGI-INF/blueprint/blueprint.xml"
				;
	}

	@Override @SuppressWarnings({ "rawtypes", "unchecked" })
	public String useOverridePropertiesWithConfigAdmin(Dictionary props) {
	  props.put("enable.trace", false);
		props.put("amq.username", "");
		props.put("amq.password", "");
		props.put("amq.brokerURL", "vm://localhost?broker.persistent=false");
		props.put("route1.in", "direct:in");
		
//		props.put("kie.releaseId", "com.redhat.brms.defects:version-update:1.0-SNAPSHOT");
		props.put("kie.maven.settings.custom", "src/test/resources/client-settings.xml");
		return "org.camel.kie.example";
	}
	
//	@Override
//  protected CamelContext createCamelContext() throws Exception {
//    CamelContext ctx=super.createCamelContext();
////	  CamelContext ctx=new DefaultCamelContext();
//    ctx.addComponent("drools", new org.camel.kie.camel.DroolsComponent());
//    return ctx;
//  }
	
	
  public static String generateExamplePayload() throws IOException{
	  StringBuffer sb=new StringBuffer();
	  sb.append("\n<call-details>");
	  sb.append("\n <call fromCountry=\"GBR\" from=\"07112284718\" toCountry=\"GBR\" to=\"07991384916\" type=\"Call\" duration=\"300\"/>");
	  sb.append("\n <call fromCountry=\"GBR\" from=\"07112284718\" toCountry=\"GBR\" to=\"07857138576\" type=\"Call\" duration=\"60\"/>");
	  sb.append("\n <call fromCountry=\"GBR\" from=\"07112284718\" toCountry=\"GBR\" to=\"07854257832\" type=\"SMS\" duration=\"0\"/>");
	  sb.append("\n</call-details>");
	  return sb.toString();
	}
	
  
	@Test
	public void shouldExcecuteKieModuleRules() throws Exception{
	  MockEndpoint mockOut=bootstrapRoute("KieExample-Route1", "{{route1.in}}", "{{route1.out}}");
	  
    template.sendBody("{{route1.in}}", generateExamplePayload());
//    List<Exchange> exchanges = mockOut.getExchanges();
//    Message messageOut = exchanges.get(0).getIn();
//    System.out.println("messageOut = "+messageOut);
    mockOut.expectedMessageCount(1);
    assertMockEndpointsSatisfied();
	}
	
	
  private MockEndpoint bootstrapRoute(String routeId, final String from, final String to) throws Exception{
    context.getRouteDefinition(routeId).adviceWith(context, new RouteBuilder() {
      public void configure() throws Exception {
        interceptFrom(from)
        .process(new Processor(){
          public void process(Exchange exchange) throws Exception {
            System.out.println("Body IN = "+exchange.getIn().getBody());
          }});
        
        interceptSendToEndpoint(to)
        // .skipSendToOriginalEndpoint()
            .process(new Processor() {
              public void process(Exchange exchange) throws Exception {
                System.out.println("Body OUT = "+exchange.getIn().getBody());
              }
            })
            .to("mock:result")
            ;
      }
    });
    return getMockEndpoint("mock:result");
  }
  
	
//	@Test
//	public void shouldApplyCDRNumberShieldingToGermanNumbersOnly() throws Exception {
//		
//		context.getRouteDefinition("CDRService-NumberShielding")
//	    	.adviceWith(context, new RouteBuilder() {
//	        @Override
//	        public void configure() throws Exception {
//	        	 interceptSendToEndpoint("{{cdr.persist}}")
////                    .skipSendToOriginalEndpoint()
//	        	 .process(new Processor() {
//	        		 @Override
//	        		 public void process(Exchange exchange) throws Exception {
////	        			 String payload = IOUtils.toString(this.getClass().getResourceAsStream("/accept/invalid.xml"));
////	        			 exchange.getOut().setBody(payload, String.class);
//	        			 System.out.println("BODY="+exchange.getIn().getBody());
//	        		 }
//	        		 
//	        	 })
//	        	 .to("mock:result")
//	        	 ;
//	        }
//	    });
//		
//		// shielding list
//		ShieldingList shieldingList = new ShieldingList();
//		shieldingList.getPreferences().putAll(
//				new PreferencesBuilder()
//				.withPreference("DE", "07775698521")
//				.withPreference("DE", "07775698533")
//				.build());
//		String shieldingFileLocation = System.getProperty("user.dir")+ "/src/test/resources/shielding/shielding.json";
//		new ObjectMapper().writeValue(new FileOutputStream(	shieldingFileLocation), shieldingList);
//
//		final FILE cdrFileRepresentation = new FILE();
//		cdrFileRepresentation.getUSAGE().addAll( new UsageGenerator().generate(8));
//		cdrFileRepresentation.getUSAGE().add(createUsage("DE", "07775698521", "07492759351")); // call should be shielded
//		cdrFileRepresentation.getUSAGE().add(createUsage("NL", "07775698533", "07947284372")); // call doesnt match pref country, so shouldnt be shielded
//
//		getMockEndpoint("mock:result").expectedMessageCount(1);
//		
//		new File("target/in").mkdirs();
//		new File("target/in", "file1.DAT").createNewFile();
////		IOUtils.write(TestBase.marshal(cdrFileRepresentation), new FileOutputStream(new File("target/in", "file1.DAT")));
//		String payload = IOUtils.toString(this.getClass().getResourceAsStream("/split/in.xml"));
//		IOUtils.write(payload, new FileOutputStream(new File("target/in", "file1.DAT")));
////		template.sendBody("direct:start", cdrFileRepresentation);
//
//		assertMockEndpointsSatisfied();
//
//		List<Exchange> exchanges = getMockEndpoint("mock:result").getExchanges();
//		Message message = exchanges.get(0).getIn();
//		String shieldingListLocation = message.getHeader("shieldingListLocation", String.class);
//
//		assertThat(exchanges, notNullValue());
//		assertEquals(shieldingListLocation,"src/test/resources/shielding/shielding.json");
//
//		FILE result = message.getBody(FILE.class);
//		List<USAGE> usages = result.getUSAGE();
//
//		String originalBNumber1 = "07492759351";
//		String shieldedBNumber1 = "";
//		for (USAGE usage : usages) {
//			// DE call + DE preference - should shield
//			if (usage.getOriginatingNumberA().equalsIgnoreCase("07775698521")&& usage.getAccountCountryCode().equalsIgnoreCase("DE")) {
//				shieldedBNumber1 = usage.getCalledNumberBShielded();
//				originalBNumber1 = usage.getCalledNumberB();
//			} else {
//				// NL call + DE preference - should not shield
//				Assert.assertEquals("", usage.getCalledNumberBShielded());
//			}
//		}
//		
//		Assert.assertEquals("07492759351", originalBNumber1);
//		Assert.assertEquals("07492759XXX", shieldedBNumber1);
//	}
//
//	class PreferencesBuilder {
//		Map<String, List<Preference>> p = new HashMap<String, List<Preference>>();
//
//		public PreferencesBuilder withPreference(String countryCode,
//				String msisdn) {
//			List<Preference> l = p.get(countryCode);
//			if (null == l)
//				l = new ArrayList<Preference>();
//			Preference pref = new Preference();
//			pref.setMsisdn(msisdn);
//			l.add(pref);
//			p.put(countryCode, l);
//			return this;
//		}
//
//		public Map<String, List<Preference>> build() {
//			return p;
//		}
//	}
//
//	private USAGE createUsage(String billingCountry, String aNumber,
//			String bNumber) {
//		FILE.USAGE u = new FILE.USAGE();
//		u.setAccountCountryCode(billingCountry);
//		u.setCalledNumberB(bNumber);
//		u.setCalledNumberBShielded("");
//		u.setOriginatingNumberA(aNumber);
//		return u;
//	}
//
//	// == Generator code
//	public enum CountryCodes {
//		NL, DE
//	}
//
//	private static final Generator<USAGE> usageGenerator = new RouteTest().new UsageGenerator();
//
//	public class UsageGenerator implements Generator<USAGE> {
//		@Override
//		public USAGE next() {
//			FILE.USAGE u = new FILE.USAGE();
//			u.setAccountCountryCode(enumValues(CountryCodes.class).next()
//					.name());
//			u.setCalledNumberB("0" + strings("0123456789", 10, 10).next());
//			u.setCalledNumberBShielded("");
//			u.setOriginatingNumberA("0" + strings("0123456789", 10, 10).next());
//			return u;
//		}
//
//		public List<USAGE> generate(int howMany) {
//			final List<USAGE> result = Lists.newArrayList();
//			forAll(howMany, usageGenerator,
//					new AbstractCharacteristic<USAGE>() {
//						@Override
//						protected void doSpecify(USAGE usage) throws Throwable {
//							result.add(usage);
//						}
//					});
//			return result;
//		}
//	}

}