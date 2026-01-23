package org.raflab.studsluzba.model.entities;

// ne koristi se trenutno

public enum VrstaStudija {
    OSS("Osnovne strukovne studije"),
    MSS("Master strukovne studije"),
    OAS("Osnovne akademske studije"),
    MAS("Master akademske studije"),
    DAS("Doktorske akademske studije");
    private final String puniNaziv;

    // konstruktor se poziva sa Stringom kao argumentom
    VrstaStudija(String puniNaziv) {
        this.puniNaziv = puniNaziv;
    }

    // u bazi se cuva samo skracenica
    // ako zelimo da negde prikazemo naziv, zovemo ovaj getter
    public String getPuniNaziv() {
        return puniNaziv;
    }
}

