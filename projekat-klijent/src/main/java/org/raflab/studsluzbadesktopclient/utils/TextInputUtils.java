package org.raflab.studsluzbadesktopclient.utils;

import java.time.LocalTime;

import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.scene.text.Text;

public class TextInputUtils {
	
	public static String getTextIfNotEmpty(TextInputControl tf) {
		if(tf.getText().equals("")) return null;
		else return tf.getText();
	}
	
	public static Float getFloatIfNotEmpty(TextInputControl tf) {
		if(tf.getText().equals("")) return (float)0.0;
		else {
			try {
				return Float.parseFloat(tf.getText());				
			}catch(NumberFormatException e) {			
				return (float)0.0;
			}
		}
	}
	
	public static Integer getIntIfNotEmpty(TextInputControl tf) {
		if(tf.getText().equals("")) return 0;
		else {
			try {
				return Integer.parseInt(tf.getText());				
			}catch(NumberFormatException e) {
				
				return 0;
			}			
		}	
	}
	
	// iz tekstualnog polja uzima tekst u formatu hh:mm, parsira i vraca LocalTime
	public static LocalTime getLocalTime(TextInputControl tf) {
		try {			
			String vremeStr = tf.getText(); // ocekuje se vreme u formatu hh:mm
			String[] vremeParts = vremeStr.split(":");
			int sati = Integer.parseInt(vremeParts[0]);
			int minuti = Integer.parseInt(vremeParts[1]);
			return LocalTime.of(sati, minuti);
		}catch(Exception e) {
			return null;
		}
	}
}