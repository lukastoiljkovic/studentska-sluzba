package org.raflab.studsluzbadesktopclient.coder;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

import org.raflab.studsluzbadesktopclient.app.MainView;

/**
 * Šifarnici koji se čitaju iz tektualnog fajla gde je jedan red šifra u šifarniku
 * primer: države, mesta
 * @author bojanads
 *
 */
public class TextFileSimpleCoder extends Coder<SimpleCode> {

    private String filePath;

    public TextFileSimpleCoder(String filePath) {
        this.filePath = filePath;
        loadCodes();
    }

    @Override
    protected void loadCodes() {
        if (codes != null) return;

        codes = new ArrayList<>();

        try (InputStream is = MainView.class.getResourceAsStream("/" + filePath)) {
            if (is == null) {
                throw new FileNotFoundException("Resource not found: /" + filePath);
            }

            try (Scanner scanner = new Scanner(is, StandardCharsets.UTF_8)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();
                    if (!line.isEmpty()) {
                        codes.add(new SimpleCode(line));
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}