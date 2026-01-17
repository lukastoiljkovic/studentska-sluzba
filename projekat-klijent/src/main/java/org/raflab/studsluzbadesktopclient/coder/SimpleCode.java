package org.raflab.studsluzbadesktopclient.coder;

/**
 * Šifarnik koji nema vrednost, samo šifru, na primer zvanje, ispitni rok, drzava
 * @author bojanads
 *
 */
public class SimpleCode extends AbstractCode {

    public SimpleCode(String code) {
        super(code);
        // TODO Auto-generated constructor stub
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public String toString() {
        return getCode();
    }
}