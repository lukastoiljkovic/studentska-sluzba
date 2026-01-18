// File: main/java/org/raflab/studsluzbadesktopclient/dtos/UverenjeOStudiranjuDTO.java
package org.raflab.studsluzbadesktopclient.dtos;

/**
 * DTO za generisanje Uverenja o studiranju
 * VAŽNO: EKSPLICITNI GETTERI za JasperReports!
 */
public class UverenjeOStudiranjuDTO {
    private String ime;
    private String prezime;
    private String jmbg;
    private String indeksFormatirano;
    private String studProgramNaziv;
    private Double prosecnaOcena;
    private Integer ostvarenoEspb;
    private String datumIzdavanja;

    public UverenjeOStudiranjuDTO() {
    }

    public UverenjeOStudiranjuDTO(String ime, String prezime, String jmbg,
                                  String indeksFormatirano, String studProgramNaziv,
                                  Double prosecnaOcena, Integer ostvarenoEspb,
                                  String datumIzdavanja) {
        this.ime = ime;
        this.prezime = prezime;
        this.jmbg = jmbg;
        this.indeksFormatirano = indeksFormatirano;
        this.studProgramNaziv = studProgramNaziv;
        this.prosecnaOcena = prosecnaOcena;
        this.ostvarenoEspb = ostvarenoEspb;
        this.datumIzdavanja = datumIzdavanja;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public String getJmbg() {
        return jmbg;
    }

    public void setJmbg(String jmbg) {
        this.jmbg = jmbg;
    }

    public String getIndeksFormatirano() {
        return indeksFormatirano;
    }

    public void setIndeksFormatirano(String indeksFormatirano) {
        this.indeksFormatirano = indeksFormatirano;
    }

    public String getStudProgramNaziv() {
        return studProgramNaziv;
    }

    public void setStudProgramNaziv(String studProgramNaziv) {
        this.studProgramNaziv = studProgramNaziv;
    }

    public Double getProsecnaOcena() {
        return prosecnaOcena;
    }

    public void setProsecnaOcena(Double prosecnaOcena) {
        this.prosecnaOcena = prosecnaOcena;
    }

    public Integer getOstvarenoEspb() {
        return ostvarenoEspb;
    }

    public void setOstvarenoEspb(Integer ostvarenoEspb) {
        this.ostvarenoEspb = ostvarenoEspb;
    }

    public String getDatumIzdavanja() {
        return datumIzdavanja;
    }

    public void setDatumIzdavanja(String datumIzdavanja) {
        this.datumIzdavanja = datumIzdavanja;
    }

    @Override
    public String toString() {
        return "UverenjeOStudiranjuDTO{" +
                "ime='" + ime + '\'' +
                ", prezime='" + prezime + '\'' +
                ", jmbg='" + jmbg + '\'' +
                ", indeksFormatirano='" + indeksFormatirano + '\'' +
                ", studProgramNaziv='" + studProgramNaziv + '\'' +
                ", prosecnaOcena=" + prosecnaOcena +
                ", ostvarenoEspb=" + ostvarenoEspb +
                ", datumIzdavanja='" + datumIzdavanja + '\'' +
                '}';
    }
}