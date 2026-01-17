package org.raflab.studsluzbadesktopclient.utils;

import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextInputControl;

public class ValidationHelper {

    public static boolean isEmpty(TextInputControl field) {
        return field.getText() == null || field.getText().trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) return false;
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    public static boolean isValidJMBG(String jmbg) {
        if (jmbg == null || jmbg.length() != 13) return false;
        return jmbg.matches("\\d{13}");
    }

    public static boolean isValidInteger(TextInputControl field) {
        if (isEmpty(field)) return false;
        try {
            Integer.parseInt(field.getText());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidDouble(TextInputControl field) {
        if (isEmpty(field)) return false;
        try {
            Double.parseDouble(field.getText());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static Integer getIntegerOrNull(TextInputControl field) {
        if (isEmpty(field)) return null;
        try {
            return Integer.parseInt(field.getText());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Double getDoubleOrNull(TextInputControl field) {
        if (isEmpty(field)) return null;
        try {
            return Double.parseDouble(field.getText());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static String getTextOrNull(TextInputControl field) {
        return isEmpty(field) ? null : field.getText().trim();
    }

    public static <T> T getSelectedOrNull(ComboBox<T> comboBox) {
        return comboBox.getSelectionModel().getSelectedItem();
    }

    public static boolean isSelected(ComboBox<?> comboBox) {
        return comboBox.getSelectionModel().getSelectedItem() != null;
    }

    public static boolean isSelected(DatePicker datePicker) {
        return datePicker.getValue() != null;
    }

    public static String validateStudentForm(TextInputControl ime,
                                             TextInputControl prezime,
                                             TextInputControl jmbg,
                                             TextInputControl emailFak) {
        if (isEmpty(ime)) return "Ime je obavezno!";
        if (isEmpty(prezime)) return "Prezime je obavezno!";
        if (isEmpty(jmbg) || !isValidJMBG(jmbg.getText())) {
            return "JMBG mora imati tačno 13 cifara!";
        }
        if (isEmpty(emailFak) || !isValidEmail(emailFak.getText())) {
            return "Email fakulteta nije validan!";
        }
        return null;
    }
}
