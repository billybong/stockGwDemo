package se.rl

import org.apache.camel.main.Main
import org.apache.camel.scala.dsl.builder.RouteBuilderSupport
import org.apache.activemq.camel.component.ActiveMQComponent
import org.apache.activemq.ActiveMQConnectionFactory

import scala.collection.JavaConversions._

/**
 * A Main to run Camel
 * 
 * RouteBuilderSupport adds implicit type conversion between Scala RouteBuilders to Java ones
 */
object StartRoute extends RouteBuilderSupport {

  def main(args: Array[String]) {
    val main = new Main()
    // enable hangup support so you need to use ctrl + c to stop the running app
    main.enableHangupSupport();
    
    //Add activeMQ component
    main.getCamelContexts().foreach(_.addComponent("activemq", ActiveMQComponent.activeMQComponent("tcp://localhost:61616")));
    
    //Add our routes, works as RouteBuilderSupport implicitly type converts between Scala DSL -> Java DSL
    main.addRouteBuilder(new BackEndrouter)
    
    // must use run to start the main application
    main.run();
  }
}

