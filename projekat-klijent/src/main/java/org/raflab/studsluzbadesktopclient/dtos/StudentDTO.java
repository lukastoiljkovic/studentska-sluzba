package org.raflab.studsluzbadesktopclient.dtos;

import lombok.Data;

@Data
public class StudentDTO extends org.raflab.studsluzba.dtos.StudentDTO {

    // Dodatna polja koja su potrebna u klijentu
    private String indeksFormatirano;

    /**
     * Vraća formatiran indeks u obliku RN123/24
     */
    public String getIndeksFormatirano() {
        if (indeksFormatirano == null && getStudProgramOznaka() != null) {
            return String.format("%s%d/%02d",
                    getStudProgramOznaka(),
                    getBroj(),
                    getGodinaUpisa() % 100);
        }
        return indeksFormatirano;
    }
}