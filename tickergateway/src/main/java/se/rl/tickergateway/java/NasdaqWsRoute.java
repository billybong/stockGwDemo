package se.rl.tickergateway.java;

import org.apache.camel.builder.RouteBuilder;

public class NasdaqWsRoute extends RouteBuilder {

	private String wsEndpoint = "cxf:http://0.0.0.0:8080/nasdaqService" +
											"?wsdlURL=ws/nasdaq.wsdl" +
											"&dataFormat=PAYLOAD";
	
	private String amqEndpoint = "activemq:ticker";
	
	private static final String OK_RESPONSE = "<TransmitStockUpdateResponse xmlns=\"http://www.nasdaq.com/services/\">" +
												"<Status>OK</Status>" +
											 "</TransmitStockUpdateResponse>";
	
	@Override
	public void configure() throws Exception {
		
		from(wsEndpoint).routeId("NasdaqRoute")
			.log("Incoming ticker from Nasdaq: \n${body}")
			.to("xslt:transform/nasdaqToCanonical.xsl")
			.setHeader("from").constant("Nasdaq")
			.log("After transformation: \n${body}")
			.inOnly(amqEndpoint) //inOnly as JMS would otherwise expect request/reply
			.transform(constant(OK_RESPONSE)); //For response
	}

	public void setWsEndpoint(String wsEndpoint) {
		this.wsEndpoint = wsEndpoint;
	}

	public void setAmqEndpoint(String amqEndpoint) {
		this.amqEndpoint = amqEndpoint;
	}

	public String getWsEndpoint() {
		return wsEndpoint;
	}

	public String getAmqEndpoint() {
		return amqEndpoint;
	}
	
	
	
}
