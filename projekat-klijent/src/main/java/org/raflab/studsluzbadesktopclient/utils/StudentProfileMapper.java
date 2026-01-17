package org.raflab.studsluzbadesktopclient.utils;

import org.raflab.studsluzba.dtos.*;
import org.springframework.stereotype.Component;

/**
 * Mapper koji pretvara server DTOs u klijent DTOs
 */
@Component
public class StudentProfileMapper {

    /**
     * Pretvara server StudentProfileDTO + dodatne podatke u kompletan klijent StudentProfileDTO
     */
    public org.raflab.studsluzbadesktopclient.dtos.StudentProfileDTO mapToClientDTO(
            org.raflab.studsluzba.dtos.StudentProfileDTO serverDTO,
            StudentIndeksResponse indeksResponse,
            StudentPodaciResponse podaciResponse,
            java.util.List<PolozenPredmetResponse> polozeni,
            java.util.List<NepolozenPredmetResponse> nepolozeni,
            java.util.List<UpisGodineResponse> upisi,
            java.util.List<ObnovaGodineResponse> obnove,
            java.util.List<UplataResponse> uplate
    ) {
        org.raflab.studsluzbadesktopclient.dtos.StudentProfileDTO clientDTO =
                new org.raflab.studsluzbadesktopclient.dtos.StudentProfileDTO();

        // ID-jevi
        if (podaciResponse != null) {
            clientDTO.setStudentId(podaciResponse.getId());
        }
        if (indeksResponse != null) {
            clientDTO.setStudentIndeksId(indeksResponse.getId());
            if (indeksResponse.getStudijskiProgram() != null) {
                clientDTO.setStudProgramId(indeksResponse.getStudijskiProgram().getId());
            }
        }

        // Osnovni podaci iz StudentPodaciResponse
        if (podaciResponse != null) {
            clientDTO.setIme(podaciResponse.getIme());
            clientDTO.setPrezime(podaciResponse.getPrezime());
            clientDTO.setSrednjeIme(podaciResponse.getSrednjeIme());
            clientDTO.setJmbg(podaciResponse.getJmbg());
            clientDTO.setPol(podaciResponse.getPol());

            clientDTO.setDatumRodjenja(podaciResponse.getDatumRodjenja());
            clientDTO.setMestoRodjenja(podaciResponse.getMestoRodjenja());
            clientDTO.setDrzavaRodjenja(podaciResponse.getDrzavaRodjenja());
            clientDTO.setDrzavljanstvo(podaciResponse.getDrzavljanstvo());
            clientDTO.setNacionalnost(podaciResponse.getNacionalnost());

            clientDTO.setMestoPrebivalista(podaciResponse.getMestoPrebivalista());
            clientDTO.setAdresaPrebivalista(podaciResponse.getAdresaPrebivalista());
            clientDTO.setBrojTelefonaMobilni(podaciResponse.getBrojTelefonaMobilni());
            clientDTO.setBrojTelefonaFiksni(podaciResponse.getBrojTelefonaFiksni());
            clientDTO.setEmailFakultetski(podaciResponse.getEmailFakultetski());
            clientDTO.setEmailPrivatni(podaciResponse.getEmailPrivatni());

            clientDTO.setBrojLicneKarte(podaciResponse.getBrojLicneKarte());
            clientDTO.setLicnuKartuIzdao(podaciResponse.getLicnuKartuIzdao());

            clientDTO.setSrednjaSkola(podaciResponse.getSrednjaSkola());
            clientDTO.setUspehSrednjaSkola(podaciResponse.getUspehSrednjaSkola());
            clientDTO.setUspehPrijemni(podaciResponse.getUspehPrijemni());
            clientDTO.setPrethodnaUstanova(podaciResponse.getPrethodnaUstanova());
        }

        // Indeks podaci iz StudentIndeksResponse
        if (indeksResponse != null) {
            clientDTO.setBroj(indeksResponse.getBroj());
            clientDTO.setGodina(indeksResponse.getGodina());
            clientDTO.setStudProgramOznaka(indeksResponse.getStudProgramOznaka());
            clientDTO.setNacinFinansiranja(indeksResponse.getNacinFinansiranja());
            clientDTO.setAktivanIndeks(indeksResponse.isAktivan());
            clientDTO.setVaziOd(indeksResponse.getVaziOd());
            clientDTO.setOstvarenoEspb(indeksResponse.getOstvarenoEspb());

            if (indeksResponse.getStudijskiProgram() != null) {
                clientDTO.setStudProgramNaziv(indeksResponse.getStudijskiProgram().getNaziv());
            }
        }

        // Izračunaj prosečnu ocenu
        if (polozeni != null && !polozeni.isEmpty()) {
            double prosek = polozeni.stream()
                    .filter(p -> p.getOcena() != null)
                    .mapToInt(PolozenPredmetResponse::getOcena)
                    .average()
                    .orElse(0.0);
            clientDTO.setProsecnaOcena(prosek);
        } else {
            clientDTO.setProsecnaOcena(0.0);
        }

        // Liste
        clientDTO.setPolozeniPredmeti(polozeni);
        clientDTO.setNepolozeniPredmeti(nepolozeni);
        clientDTO.setUpisaneGodine(upisi);
        clientDTO.setObnoveGodine(obnove);
        clientDTO.setUplate(uplate);

        return clientDTO;
    }
}