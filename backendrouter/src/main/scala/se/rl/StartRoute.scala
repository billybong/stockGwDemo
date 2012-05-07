package se.rl

import org.apache.camel.main.Main
import org.apache.camel.scala.dsl.builder.RouteBuilderSupport
import org.apache.activemq.camel.component.ActiveMQComponent
import org.apache.activemq.ActiveMQConnectionFactory

/**
 * A Main to run Camel
 */
object StartRoute extends RouteBuilderSupport {

  def main(args: Array[String]) {
    val main = new Main()
    // enable hangup support so you need to use ctrl + c to stop the running app
    main.enableHangupSupport();
    
    //Add activeMQ component
    var cf = new ActiveMQConnectionFactory()
    cf.setBrokerURL("tcp://localhost:61616")
    
    var amqComponent = new ActiveMQComponent()
    amqComponent.setConnectionFactory(cf)
    
    val contextIt = main.getCamelContexts().iterator();
    while (contextIt.hasNext()) {
    	contextIt.next().addComponent("activemq", amqComponent);
    }
    
    main.addRouteBuilder(new BackEndrouter)
    // must use run to start the main application
    main.run();
  }
}

