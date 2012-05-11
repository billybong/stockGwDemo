package se.rl.tickergateway.groovy

import groovy.json.JsonSlurper
import groovy.xml.MarkupBuilder

import org.apache.camel.Expression
import org.apache.camel.language.groovy.GroovyRouteBuilder
import org.apache.camel.processor.idempotent.MemoryIdempotentRepository;

class TickerTechRoute extends GroovyRouteBuilder {

	@Override
	public void configure() throws Exception {
		from("quartz://callTickerTech?cron=0/10+*+*+*+*+?").autoStartup(true).routeId("TickerTechGateway")
			.enrich("http://localhost/tickertech.json")
			.convertBodyTo(String.class) //Required as http endpoint returns non cacheable InputStream
			.split().method(FeedSplitter.class, "split")
				.log('Incoming ticker from TickerTech ${body}')
				.setHeader("from").constant('TickerTech')
				.bean(Transformer, "transform")
				.to("activemq:ticker")
	}
}

class Transformer {
	
	public String transform(feed){
		
		def writer = new StringWriter()
		def xml = new MarkupBuilder(writer)
		
		xml.stockTicker(xmlns: 'http://mycorp.stocks.com') {
		  time(feed.timestamp)
		  stockName(feed.symbol)
		  price(currency:feed.currency) {
			current(feed.current)
		  }
		}
		return writer.toString()
	}
}

class FeedSplitter {
	
	public split(String input){
		new JsonSlurper().parseText(input).feeds
	}
}