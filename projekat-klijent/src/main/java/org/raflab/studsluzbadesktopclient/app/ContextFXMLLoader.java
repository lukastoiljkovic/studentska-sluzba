package org.raflab.studsluzbadesktopclient.app;

import java.net.URL;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import javafx.fxml.FXMLLoader;

@Component
public class ContextFXMLLoader {

	private ConfigurableApplicationContext context;

	public ContextFXMLLoader(ConfigurableApplicationContext context) {
		this.context = context;
	}

    public FXMLLoader getLoader(URL url) {
	  FXMLLoader loader = new FXMLLoader(url);
	  loader.setControllerFactory(context::getBean);
	  return loader;
	}
	  
	public FXMLLoader getLoader(String fxmlPath) {
	  return getLoader(this.getClass().getResource(fxmlPath));
	 }
}
