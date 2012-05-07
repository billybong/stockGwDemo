package se.rl.tickergateway.java;

import org.apache.camel.builder.RouteBuilder;

public class NasdaqWsRoute extends RouteBuilder {

	private static final String wsEndpoint = "cxf:http://0.0.0.0:8080/nasdaqService" +
											"?wsdlURL=ws/nasdaq.wsdl" +
											"&dataFormat=PAYLOAD";
	
	private static final String okResponse = "<TransmitStockUpdateResponse xmlns=\"http://www.nasdaq.com/services/\">" +
												"<Status>OK</Status>" +
											 "</TransmitStockUpdateResponse>";
	
	@Override
	public void configure() throws Exception {
		
		from(wsEndpoint).routeId("NasdaqRoute")
			.log("Incoming ticker from Nasdaq: \n${body}")
			.to("xslt:transform/nasdaqToCanonical.xsl")
			.setHeader("from").constant("Nasdaq")
			.inOnly("activemq:ticker") //inOnly as JMS would otherwise expect request/reply
			.transform(constant(okResponse)); //For response
	}
}
