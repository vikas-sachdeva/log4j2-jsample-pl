package log4j2.jsample;

import log4j2.jsample.camel.CamelConfigurer;

public class App {
	public static void main(String[] args) throws Exception {

		CamelConfigurer camelConfigurer = new CamelConfigurer();
		camelConfigurer.configure();

	}
}