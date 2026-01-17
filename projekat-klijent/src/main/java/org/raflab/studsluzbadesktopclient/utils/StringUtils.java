package org.raflab.studsluzbadesktopclient.utils;

public class StringUtils {

	public static boolean allContains(String filter, Object ...objects ) {
		for(Object obj: objects) {
			if(obj!=null && !obj.toString().contains(filter))
				return false;
		}
		return true;
	}
	
	public static boolean atLeastOneContains(String filter, Object ...objects ) {
		for(Object obj: objects) {
			if(obj!=null && obj.toString().toLowerCase().contains(filter.toLowerCase()))
				return true;
		}
		return false;
	}
}
