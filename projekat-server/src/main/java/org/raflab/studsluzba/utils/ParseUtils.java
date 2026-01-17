package org.raflab.studsluzba.utils;
import org.raflab.studsluzba.dtos.*;

public class ParseUtils {
	// za indekse

	/*
	 * dobija indeks oblika rn1923 i vraca niz stringova [RN,19,23]
	 */
    public static String[] parseIndeks(String indeksShort) {
        if (indeksShort.length() < 5) return null;

        String[] retVal = new String[3];
        indeksShort = indeksShort.toUpperCase();
        StringBuilder sb = new StringBuilder();

        int i = 0;

        // stud program
        while (i < indeksShort.length() && Character.isAlphabetic(indeksShort.charAt(i))) {
            sb.append(indeksShort.charAt(i++));
        }
        if (i >= indeksShort.length()) return null;
        retVal[0] = sb.toString();
        sb.setLength(0);

        // broj indeksa (sve cifre osim poslednje 2)
        while (i < indeksShort.length() - 2) {
            sb.append(indeksShort.charAt(i++));
        }
        retVal[2] = sb.toString();
        sb.setLength(0);

        // godina (poslednje 2 cifre)
        sb.append(indeksShort.charAt(i++));
        sb.append(indeksShort.charAt(i));
        retVal[1] = sb.toString();

        return retVal;
    }

	/*
	 * dobija email studenta na primer ppetrovic1220rn@raf.rs, a vraca indeks u obliku  [rn, 20, 12]
	 */

    public static String[] parseEmail(String studEmail) {
        if (studEmail == null || !studEmail.endsWith("@raf.rs")) return null;

        String emailStr = studEmail.substring(0, studEmail.indexOf('@'));
        if (emailStr.length() < 3) return null; // minimalna dužina

        StringBuilder sb = new StringBuilder();
        int i = 0;

        while (i < emailStr.length() && Character.isAlphabetic(emailStr.charAt(i))) {
            i++;
        }

        while (i < emailStr.length() && Character.isDigit(emailStr.charAt(i))) {
            sb.append(emailStr.charAt(i++));
        }

        String cifre = sb.toString();
        if (cifre.length() < 2) return null;

        String[] retVal = new String[3];
        retVal[2] = cifre.substring(0, cifre.length() - 2);
        retVal[1] = cifre.substring(cifre.length() - 2);
        retVal[0] = emailStr.substring(i).toUpperCase();

        return retVal;
    }

}
