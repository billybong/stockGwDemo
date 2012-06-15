package se.rl.tickergateway.java;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;

public class NasdaqRouteTest extends CamelTestSupport {

	private static final String MOCK_FROM = "direct:nasdaq";
	private static final String MOCK_TO = "mock:queue";
	
    private static final String SAMPLE_REQUEST = "<TransmitStockUpdate xmlns=\"http://www.nasdaq.com/services/\">"+
											         "<Feed timestamp=\"2012-04-08:10.00.00\" currency=\"SEK\" symbol=\"ERIC\">" +
											            "<dailyLow>5</dailyLow>" +
											            "<dailyHigh>40</dailyHigh>" +
											            "<current>20</current>" +
											         "</Feed>" +
											      "</TransmitStockUpdate>";
	
	
    private static final String EXPECTED_BODY = "<stockTicker xmlns=\"http://mycorp.stocks.com\">" +
													"<time>2012-04-08:10.00.00</time>" +
													"<stockName>ERIC</stockName>" +
													"<price currency=\"SEK\">" +
														"<current>20</current>" +
													"</price>" +
												"</stockTicker>";

    @Test
    public void testHappyPath() throws Exception {
    	//Setup mock endpoint expectations
        MockEndpoint mockEndpoint = getMockEndpoint(MOCK_TO);
        mockEndpoint.expectedMessageCount(1);
        
        //Send the message to the direct: from endpoint
        template.sendBody(MOCK_FROM, SAMPLE_REQUEST);
        
        //Assert all expectations
        assertMockEndpointsSatisfied();
        
        //Check XML syntax after transformation. Should probably also validate against schema here...
        String outMessage = mockEndpoint.getReceivedExchanges().remove(0).getIn().getBody(String.class);
        XMLUnit.setIgnoreWhitespace(true);
        assertXMLEqual("Expected out message is not equal to output test", EXPECTED_BODY, outMessage);
    }
    
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        NasdaqWsRoute route = new NasdaqWsRoute();
        route.setWsEndpoint(MOCK_FROM);
        route.setToEndpoint(MOCK_TO);
        return route;
    }
}
