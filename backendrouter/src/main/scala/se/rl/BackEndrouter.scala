package se.rl

import org.apache.camel.scala.dsl.builder.RouteBuilder
import scala.xml.XML
import net.liftweb.json._
import net.liftweb.json.JsonDSL._
import scala.xml.Node

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
     bean(new WebTransformer)
     log("Wiretapping to web..: \n${body}")
     to("activemq:topic:web")
   }
}

class WebTransformer {  
  /**
   * Transforms to JSON String by navigating XML tree
   */
  def transform(xml: Node) = {
    pretty(render( 
        ("stock" -> 
    		("name" -> (xml \ "stockName").text) ~ 
    		("value" -> (xml \\ "current").text) ~ 
    		("currency" -> (xml \\ "@currency").text)
    	)
    ))
  }
}