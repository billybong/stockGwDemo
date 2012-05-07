package se.rl

import org.apache.camel.scala.dsl.builder.RouteBuilder
import scala.xml.XML

import net.liftweb.json._
import net.liftweb.json.JsonDSL._

class BackEndrouter extends RouteBuilder {
   
   // from the ticker gateway
   "activemq:ticker" ==> {
      log("BackendRouter received ticker: ${body}")
      wireTap("seda:toWeb")
      to("activemq:backend")
   }
   
   // wiretap to our web, reformat to json
   "seda:toWeb" ==> {
     bean(new Transformer)
     log("Wiretapping to web..: ${body}")
     to("activemq:topic:web")
   }
}

class Transformer {  
  /**
   * Reformats to JSON by navigating XML tree
   */
  def transform(xmlString: String) = {
    val xml = XML.loadString(xmlString)
    val json = ("stock" -> 
    				("name" -> (xml \ "stockName").text) ~ 
    				("value" -> (xml \\ "current").text) ~ 
    				("currency" -> (xml \\ "@currency").text)
    			)
    pretty(render(json))
  }
}