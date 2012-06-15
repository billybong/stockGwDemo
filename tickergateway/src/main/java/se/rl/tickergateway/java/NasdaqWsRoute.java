package se.rl.tickergateway.java;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

public class NasdaqWsRoute extends RouteBuilder {

	private String wsEndpoint = "cxf:http://0.0.0.0:8080/nasdaqService" +
											"?wsdlURL=ws/nasdaq.wsdl" +
											"&dataFormat=PAYLOAD";
	
	private String toEndpoint = "direct:aggregation";
	
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
			.inOnly(toEndpoint) //inOnly as JMS would otherwise expect request/reply
			.transform(constant(OK_RESPONSE)); //For response
	}

	public void setWsEndpoint(String wsEndpoint) {
		this.wsEndpoint = wsEndpoint;
	}

	public void setToEndpoint(String toEndpoint) {
		this.toEndpoint = toEndpoint;
	}

	public String getWsEndpoint() {
		return wsEndpoint;
	}

	public String getToEndpoint() {
		return toEndpoint;
	}
	
	
	
}
