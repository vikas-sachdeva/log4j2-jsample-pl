package log4j2.jsample.camel;

import org.apache.camel.builder.RouteBuilder;

public class StreamRouteBuilder extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		from("stream:in").to("log:log4j2.jsample?level=INFO").process(new StreamProcessor()).to("stream:err")
				.to("log:log4j2.jsample?level=INFO");
	}
}
