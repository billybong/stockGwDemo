package se.rl

import org.apache.camel.scala.dsl.builder.RouteBuilder
import scala.xml.XML
import net.liftweb.json._
import net.liftweb.json.JsonDSL._
import scala.xml.Node
import org.apache.camel.Exchange

class BackEndrouter extends RouteBuilder {
   
   // from the ticker gateway
   "activemq:ticker" ==> {
      id("backendRoute")
      log("BackendRouter received ticker: \n${body}")
      wireTap("seda:toWeb")
      to("activemq:backend")
   }
   
   // wiretap to our web, reformat to json
   "seda:toWeb" ==> {
     id("webWiretapRoute")
     setHeader("stock", ((e: Exchange) => (e.getIn.getBody(classOf[Node]) \ "stockName").text))
     transform(xmlToJson)
     log("Wiretapping to web..: \n${body}")
     setHeader("CamelJmsDestinationName", simple("web.${header.from}.${header.stock}"))
     to("activemq:topic:dummy")
   }
   
   def xmlToJson(e: Exchange) : String = {
		val xml = e.getIn().getBody(classOf[Node])
		
		compact(render( 
	        "stock" -> 
	    		("name" -> (xml \ "stockName").text) ~ 
	    		("value" -> (xml \\ "current").text) ~ 
	    		("currency" -> (xml \\ "@currency").text)
    	))
   }
}