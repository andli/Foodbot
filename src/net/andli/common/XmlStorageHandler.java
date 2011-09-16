/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.andli.common;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;


public class XmlStorageHandler {
	public static void writeCollection(Collection<?> saveList, String filename) throws Exception {
		XMLEncoder encoder =
			new XMLEncoder(
					new BufferedOutputStream(
							new FileOutputStream(filename)));
		encoder.writeObject(saveList);
		encoder.close();
	}

	public static Collection<?> readCollection(String filename) throws Exception {
		
		XMLDecoder decoder =
			new XMLDecoder(new BufferedInputStream(
					new FileInputStream(filename)));
		
		decoder.close();
		ArrayList<Object> returnList = new ArrayList<Object>();
		returnList.addAll((Collection<?>)decoder.readObject());
		return returnList;
	}
}