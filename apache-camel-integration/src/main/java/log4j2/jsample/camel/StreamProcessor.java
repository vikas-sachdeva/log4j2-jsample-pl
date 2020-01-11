package log4j2.jsample.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class StreamProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		String requestBody = (String) exchange.getIn().getBody();
		String responseBody = "Camel - " + requestBody;
		exchange.getMessage().setBody(responseBody);
	}
}
