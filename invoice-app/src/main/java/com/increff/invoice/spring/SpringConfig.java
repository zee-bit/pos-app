package com.increff.invoice.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@ComponentScan("com.increff.invoice")
@PropertySources({ //
		@PropertySource(value = "file:./invoice-app.properties", ignoreResourceNotFound = true) //
})
public class SpringConfig {


}
