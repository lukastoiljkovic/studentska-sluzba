// File: main/java/org/raflab/studsluzbadesktopclient/reports/ReportService.java
package org.raflab.studsluzbadesktopclient.reports;

import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.raflab.studsluzba.dtos.IspitRezultatResponse;
import org.raflab.studsluzbadesktopclient.dtos.PolozeniIspitiDTO;
import org.raflab.studsluzbadesktopclient.dtos.UverenjeOStudiranjuDTO;
import org.raflab.studsluzbadesktopclient.dtos.ZapisnikHeaderDTO;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {

    // ========== ZAPISNIK SA ISPITA ==========
    public void generateZapisnikSaIspita(
            ZapisnikHeaderDTO header,
            List<IspitRezultatResponse> rezultati,
            String fileName) throws JRException {

        InputStream reportStream = getClass()
                .getResourceAsStream("/reports/zapisnikSaIspita.jrxml");

        if (reportStream == null) {
            throw new JRException("Nije pronađen fajl zapisnikSaIspita.jrxml");
        }

        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("predmetNaziv", header.getPredmetNaziv());
        parameters.put("ispitniRokNaziv", header.getIspitniRokNaziv());
        parameters.put("datumIspita", header.getDatumIspita());
        parameters.put("nastavnikImePrezime", header.getNastavnikImePrezime());
        parameters.put("ukupnoPrijavljenih", header.getUkupnoPrijavljenih());
        parameters.put("ukupnoPolozilo", header.getUkupnoPolozilo());
        parameters.put("prosecnaOcena", header.getProsecnaOcena());

        JRBeanCollectionDataSource dataSource =
                new JRBeanCollectionDataSource(rezultati);

        JasperPrint jasperPrint = JasperFillManager
                .fillReport(jasperReport, parameters, dataSource);

        JasperExportManager.exportReportToPdfFile(jasperPrint, fileName);
    }

    public void generateAndOpenZapisnik(
            ZapisnikHeaderDTO header,
            List<IspitRezultatResponse> rezultati) throws Exception {

        String fileName = "zapisnik_" + System.currentTimeMillis() + ".pdf";
        generateZapisnikSaIspita(header, rezultati, fileName);
        openPdfFile(fileName);
    }

    // ========== UVERENJE O STUDIRANJU ==========
    public void generateUverenjeOStudiranju(
            UverenjeOStudiranjuDTO dto,
            String fileName) throws JRException {

        InputStream reportStream = getClass()
                .getResourceAsStream("/reports/uverenjeOStudiranju.jrxml");

        if (reportStream == null) {
            throw new JRException("Nije pronađen fajl uverenjeOStudiranju.jrxml");
        }

        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ime", dto.getIme());
        parameters.put("prezime", dto.getPrezime());
        parameters.put("jmbg", dto.getJmbg());
        parameters.put("indeksFormatirano", dto.getIndeksFormatirano());
        parameters.put("studProgramNaziv", dto.getStudProgramNaziv());
        parameters.put("prosecnaOcena", dto.getProsecnaOcena());
        parameters.put("ostvarenoEspb", dto.getOstvarenoEspb());

        // Format datuma
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy.");
        String datumIzdavanja = dto.getDatumIzdavanja();
        if (datumIzdavanja == null) {
            datumIzdavanja = sdf.format(new Date());
        }
        parameters.put("datumIzdavanja", datumIzdavanja);

        JREmptyDataSource dataSource = new JREmptyDataSource(1);

        JasperPrint jasperPrint = JasperFillManager
                .fillReport(jasperReport, parameters, dataSource);

        JasperExportManager.exportReportToPdfFile(jasperPrint, fileName);
    }

    public void generateAndOpenUverenjeOStudiranju(
            UverenjeOStudiranjuDTO dto) throws Exception {

        String fileName = "uverenje_studiranje_" +
                System.currentTimeMillis() + ".pdf";
        generateUverenjeOStudiranju(dto, fileName);
        openPdfFile(fileName);
    }

    // ========== POLOŽENI ISPITI ==========
    public void generatePolozeniIspiti(
            PolozeniIspitiDTO dto,
            String fileName) throws JRException {

        InputStream reportStream = getClass()
                .getResourceAsStream("/reports/polozeniIspiti.jrxml");

        if (reportStream == null) {
            throw new JRException("Nije pronađen fajl polozeniIspiti.jrxml");
        }

        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ime", dto.getIme());
        parameters.put("prezime", dto.getPrezime());
        parameters.put("indeksFormatirano", dto.getIndeksFormatirano());
        parameters.put("prosecnaOcena", dto.getProsecnaOcena());
        parameters.put("ukupnoEspb", dto.getUkupnoEspb());

        JRBeanCollectionDataSource dataSource =
                new JRBeanCollectionDataSource(dto.getIspiti());

        JasperPrint jasperPrint = JasperFillManager
                .fillReport(jasperReport, parameters, dataSource);

        JasperExportManager.exportReportToPdfFile(jasperPrint, fileName);
    }

    public void generateAndOpenPolozeniIspiti(
            PolozeniIspitiDTO dto) throws Exception {

        String fileName = "polozeni_ispiti_" +
                System.currentTimeMillis() + ".pdf";
        generatePolozeniIspiti(dto, fileName);
        openPdfFile(fileName);
    }

    // ========== HELPER METODA ZA OTVARANJE PDF-a ==========
    private void openPdfFile(String fileName) throws Exception {
        File pdfFile = new File(fileName).getAbsoluteFile();

        if (!pdfFile.exists()) {
            throw new Exception("PDF fajl nije kreiran: " + fileName);
        }

        String os = System.getProperty("os.name").toLowerCase();

        try {
            if (os.contains("win")) {
                new ProcessBuilder("cmd", "/c", "start", "",
                        pdfFile.getAbsolutePath()).start();
            } else if (os.contains("mac")) {
                new ProcessBuilder("open", pdfFile.getAbsolutePath()).start();
            } else {
                new ProcessBuilder("xdg-open", pdfFile.getAbsolutePath()).start();
            }
        } catch (Exception e) {
            throw new Exception("PDF kreiran: " + fileName +
                    "\nNe mogu automatski otvoriti fajl.", e);
        }
    }
}