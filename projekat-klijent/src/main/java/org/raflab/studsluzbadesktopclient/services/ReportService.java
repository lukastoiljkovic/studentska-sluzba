package org.raflab.studsluzbadesktopclient.services;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.dtos.IspitRezultatResponse;
import org.raflab.studsluzbadesktopclient.dtos.ZapisnikHeaderDTO;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {

    /*
    public void generateZapisnikSaIspita(
            ZapisnikHeaderDTO header,
            List<IspitRezultatResponse> rezultati,
            String fileName) throws JRException {

        // 1. Učitaj JRXML template
        InputStream reportStream = getClass()
                .getResourceAsStream("/reports/zapisnikSaIspita.jrxml");

        if (reportStream == null) {
            throw new JRException("Nije pronađen fajl zapisnikSaIspita.jrxml");
        }

        // 2. Kompajliraj report
        JasperReport jasperReport = JasperCompileManager
                .compileReport(reportStream);

        // 3. Pripremi parametre za header
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("predmetNaziv", header.getPredmetNaziv());
        parameters.put("ispitniRokNaziv", header.getIspitniRokNaziv());
        parameters.put("datumIspita", header.getDatumIspita());
        parameters.put("nastavnikImePrezime", header.getNastavnikImePrezime());
        parameters.put("ukupnoPrijavljenih", header.getUkupnoPrijavljenih());
        parameters.put("ukupnoPolozilo", header.getUkupnoPolozilo());
        parameters.put("prosecnaOcena", header.getProsecnaOcena());

        // 4. Pripremi data source (lista studenata)
        JRBeanCollectionDataSource dataSource =
                new JRBeanCollectionDataSource(rezultati);

        // 5. Popuni report sa podacima
        JasperPrint jasperPrint = JasperFillManager
                .fillReport(jasperReport, parameters, dataSource);

        // 6. Eksportuj u PDF
        JasperExportManager.exportReportToPdfFile(jasperPrint, fileName);
    }


     Generiše zapisnik i otvara ga u default PDF vieweru

    public void generateAndOpenZapisnik(
            ZapisnikHeaderDTO header,
            List<IspitRezultatResponse> rezultati) throws Exception {

        String fileName = "zapisnik_" +
                System.currentTimeMillis() + ".pdf";

        // 1. Generiši PDF
        generateZapisnikSaIspita(header, rezultati, fileName);

        // 2. Proveri da li Desktop API postoji
        if (!java.awt.Desktop.isDesktopSupported()) {
            throw new Exception(
                    "PDF je sačuvan kao: " + fileName +
                            "\nAli sistem ne podržava automatsko otvaranje fajlova."
            );
        }

        // 3. Proveri da li je fajl kreiran
        java.io.File pdfFile = new java.io.File(fileName);
        if (!pdfFile.exists()) {
            throw new Exception("PDF fajl nije kreiran: " + fileName);
        }

        // 4. Otvori PDF
        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
        desktop.open(pdfFile);
    }*/
}