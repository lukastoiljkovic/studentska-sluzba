package org.raflab.studsluzba.app;

import lombok.AllArgsConstructor;
import org.raflab.studsluzba.model.entities.*;
import org.raflab.studsluzba.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@AllArgsConstructor
@Component
public class Seeder implements CommandLineRunner {

    private final StudijskiProgramRepository studijskiProgramRepository;
    private final PredmetRepository predmetRepository;
    private final NastavnikRepository nastavnikRepository;
    private final NastavnikZvanjeRepository nastavnikZvanjeRepository;
    private final StudentPodaciRepository studentPodaciRepository;
    private final StudentIndeksRepository studentIndeksRepository;
    private final DrziPredmetRepository drziPredmetRepository;
    private final SlusaPredmetRepository slusaPredmetRepository;
    private final GrupaRepository grupaRepository;

    private final SkolskaGodinaRepository skolskaGodinaRepository;
    private final IspitniRokRepository ispitniRokRepository;
    private final IspitRepository ispitRepository;
    private final IspitPrijavaRepository ispitPrijavaRepository;
    private final IspitIzlazakRepository ispitIzlazakRepository;
    private final PolozenPredmetRepository polozenPredmetRepository;

    private final PredispitnaObavezaRepository predispitnaObavezaRepository;
    private final PredispitnaIzlazakRepository predispitnaIzlazakRepository;

    private final SrednjaSkolaRepository srednjaSkolaRepository;
    private final VisokoskolskaUstanovaRepository visokoskolskaUstanovaRepository;

    private final UpisGodineRepository upisGodineRepository;
    private final ObnovaGodineRepository obnovaGodineRepository;
    private final TokStudijaRepository tokStudijaRepository;
    private final UplataRepository uplataRepository;

    @Override
    public void run(String... args) {

        if (studijskiProgramRepository.count() > 0L) return;

        // ŠKOLSKA GODINA (aktivna)
        SkolskaGodina sg = new SkolskaGodina();
        sg.setNaziv("2024/2025.");
        sg.setAktivna(true);
        sg = skolskaGodinaRepository.save(sg);

        // SREDNJE ŠKOLE + VŠ USTANOVE
        SrednjaSkola gim = new SrednjaSkola();
        gim.setNaziv("Gimnazija 'Sveti Sava'");
        gim.setMesto("Beograd");
        gim.setVrsta(SrednjaSkola.VrstaSkole.GIMNAZIJA);
        gim = srednjaSkolaRepository.save(gim);

        VisokoskolskaUstanova raf = new VisokoskolskaUstanova();
        raf.setNaziv("Računarski fakultet");
        raf.setMesto("Beograd");
        raf.setVrsta(VisokoskolskaUstanova.Vrsta.FAKULTET);
        raf = visokoskolskaUstanovaRepository.save(raf);

        // STUDIJSKI PROGRAMI
        StudijskiProgram si = new StudijskiProgram();
        si.setOznaka("SI");
        si.setNaziv("Softversko inženjerstvo");
        si.setGodinaAkreditacije(2023);
        si.setZvanje("Dipl. inž. softvera");
        si.setTrajanjeGodina(4);
        si.setTrajanjeSemestara(8);
        si.setVrstaStudija("OAS");
        si.setUkupnoEspb(240);
        si = studijskiProgramRepository.save(si);

        StudijskiProgram ri = new StudijskiProgram();
        ri.setOznaka("RI");
        ri.setNaziv("Računarsko inženjerstvo");
        ri.setGodinaAkreditacije(2022);
        ri.setZvanje("Dipl. inž. računarstva");
        ri.setTrajanjeGodina(4);
        ri.setTrajanjeSemestara(8);
        ri.setVrstaStudija("OAS");
        ri.setUkupnoEspb(240);
        ri = studijskiProgramRepository.save(ri);

        StudijskiProgram rn = new StudijskiProgram();
        rn.setOznaka("RN");
        rn.setNaziv("Računarske nauke");
        rn.setGodinaAkreditacije(2022);
        rn.setZvanje("Dipl. inž. računarskih nauka");
        rn.setTrajanjeGodina(4);
        rn.setTrajanjeSemestara(8);
        rn.setVrstaStudija("OAS");
        rn.setUkupnoEspb(240);
        rn = studijskiProgramRepository.save(rn);

        // PREDMETI
        Predmet p1 = new Predmet();
        p1.setSifra("SI101");
        p1.setNaziv("Uvod u programiranje");
        p1.setOpis("Osnove programiranja u Javi");
        p1.setEspb(8);
        p1.setObavezan(true);
        p1.setSemestar(1);
        p1.setFondPredavanja(3);
        p1.setFondVezbi(2);
        p1.setStudProgram(si);
        p1 = predmetRepository.save(p1);

        Predmet p2 = new Predmet();
        p2.setSifra("SI202");
        p2.setNaziv("Operativni sistemi");
        p2.setOpis("Procesi, niti, memorija");
        p2.setEspb(7);
        p2.setObavezan(true);
        p2.setSemestar(3);
        p2.setFondPredavanja(3);
        p2.setFondVezbi(2);
        p2.setStudProgram(si);
        p2 = predmetRepository.save(p2);

        Predmet p3 = new Predmet();
        p3.setSifra("RI101");
        p3.setNaziv("Matematika 1");
        p3.setOpis("Diferencijalni i integralni račun");
        p3.setEspb(8);
        p3.setObavezan(true);
        p3.setSemestar(1);
        p3.setFondPredavanja(3);
        p3.setFondVezbi(3);
        p3.setStudProgram(ri);
        p3 = predmetRepository.save(p3);

        // NASTAVNICI + ZVANJA
        Nastavnik n1 = new Nastavnik();
        n1.setIme("Nikola");
        n1.setPrezime("Jovanović");
        n1.setSrednjeIme("Petar");
        n1.setEmail("njovanovic2324@raf.rs");
        n1.setBrojTelefona("060111222");
        n1.setAdresa("Bulevar 1, Beograd");
        n1.setDatumRodjenja(LocalDate.of(1980, 5, 10));
        n1.setPol('M');
        n1.setJmbg("8010051234567");
        n1.setZavrseneUstanove(Set.of(raf));
        n1 = nastavnikRepository.save(n1);

        Nastavnik n2 = new Nastavnik();
        n2.setIme("Milena");
        n2.setPrezime("Milić");
        n2.setSrednjeIme("Ana");
        n2.setEmail("mmilic6324@raf.rs");
        n2.setBrojTelefona("060333444");
        n2.setAdresa("Zmaj Jovina 2, Novi Sad");
        n2.setDatumRodjenja(LocalDate.of(1983, 7, 2));
        n2.setPol('F');
        n2.setJmbg("8307029876543");
        n2.setZavrseneUstanove(Set.of(raf));
        n2 = nastavnikRepository.save(n2);

        NastavnikZvanje z1 = new NastavnikZvanje();
        z1.setNastavnik(n1);
        z1.setZvanje("Docent");
        z1.setNaucnaOblast("Računarske nauke");
        z1.setUzaNaucnaOblast("Algoritmi");
        z1.setDatumIzbora(LocalDate.of(2020, 2, 2));
        z1.setAktivno(true);
        nastavnikZvanjeRepository.save(z1);

        NastavnikZvanje z2 = new NastavnikZvanje();
        z2.setNastavnik(n2);
        z2.setZvanje("Profesor");
        z2.setNaucnaOblast("Softversko inženjerstvo");
        z2.setUzaNaucnaOblast("Arhitekture softvera");
        z2.setDatumIzbora(LocalDate.of(2021, 3, 3));
        z2.setAktivno(true);
        nastavnikZvanjeRepository.save(z2);

        // STUDENTI + INDEKSI
        StudentPodaci s1 = new StudentPodaci();
        s1.setIme("Luka"); s1.setPrezime("Marković"); s1.setSrednjeIme("A");
        s1.setJmbg("0201011234567");
        s1.setPol('M');
        s1.setDatumRodjenja(LocalDate.of(2002,6,7));
        s1.setDrzavaRodjenja("Srbija"); s1.setMestoRodjenja("Beograd");
        s1.setDrzavljanstvo("Srbija"); s1.setNacionalnost("Srpska");
        s1.setMestoPrebivalista("Beograd"); s1.setAdresaPrebivalista("Ulica 67");
        s1.setBrojTelefonaMobilni("06767676"); s1.setBrojTelefonaFiksni("06767676");
        s1.setEmailFakultetski("lmarkovic10124rn@raf.rs"); s1.setEmailPrivatni("luka@gmail.com");
        s1.setBrojLicneKarte("LM123456"); s1.setLicnuKartuIzdao("MUP BG");
        s1.setSrednjaSkola(gim); s1.setUspehSrednjaSkola(4.35); s1.setUspehPrijemni(89.0);
        s1 = studentPodaciRepository.save(s1);

        StudentPodaci s2 = new StudentPodaci();
        s2.setIme("Mina"); s2.setPrezime("Jovanović"); s2.setSrednjeIme("B");
        s2.setJmbg("0202022234567");
        s2.setPol('F');
        s2.setDatumRodjenja(LocalDate.of(2002,2,2));
        s2.setDrzavaRodjenja("Srbija"); s2.setMestoRodjenja("Novi Sad");
        s2.setDrzavljanstvo("Srbija");
        s2.setMestoPrebivalista("Novi Sad"); s2.setAdresaPrebivalista("Ulica 2");
        s2.setEmailFakultetski("mjovanovic10224si@raf.rs"); s2.setEmailPrivatni("mina@gmail.com");
        s2.setBrojLicneKarte("MJ223456"); s2.setLicnuKartuIzdao("MUP NS");
        s2.setSrednjaSkola(gim); s2.setUspehSrednjaSkola(4.80); s2.setUspehPrijemni(94.0);
        s2 = studentPodaciRepository.save(s2);

        StudentPodaci s3 = new StudentPodaci();
        s3.setIme("Petar"); s3.setPrezime("Ilić"); s3.setSrednjeIme("V");
        s3.setJmbg("0203033234567");
        s3.setPol('M');
        s3.setDatumRodjenja(LocalDate.of(2002,3,3));
        s3.setDrzavaRodjenja("Srbija"); s3.setMestoRodjenja("Niš");
        s3.setDrzavljanstvo("Srbija");
        s3.setMestoPrebivalista("Niš"); s3.setAdresaPrebivalista("Ulica 3");
        s3.setEmailFakultetski("pilic20124ri@raf.rs"); s3.setEmailPrivatni("petar@gmail.com");
        s3.setBrojLicneKarte("PI323456"); s3.setLicnuKartuIzdao("MUP NI");
        s3.setSrednjaSkola(gim); s3.setUspehSrednjaSkola(4.60); s3.setUspehPrijemni(90.0);
        s3 = studentPodaciRepository.save(s3);

        StudentIndeks i1 = new StudentIndeks();
        i1.setBroj(101); i1.setGodina(2024);
        i1.setStudProgramOznaka(rn.getOznaka());
        i1.setNacinFinansiranja("Budzet");
        i1.setAktivan(true); i1.setVaziOd(LocalDate.of(2024,10,1)); i1.setOstvarenoEspb(0);
        i1.setStudent(s1); i1.setStudijskiProgram(rn);
        i1 = studentIndeksRepository.save(i1);

        StudentIndeks i2 = new StudentIndeks();
        i2.setBroj(102); i2.setGodina(2024);
        i2.setStudProgramOznaka(si.getOznaka());
        i2.setNacinFinansiranja("Budzet");
        i2.setAktivan(true); i2.setVaziOd(LocalDate.of(2024,10,1)); i2.setOstvarenoEspb(0);
        i2.setStudent(s2); i2.setStudijskiProgram(si);
        i2 = studentIndeksRepository.save(i2);

        StudentIndeks i3 = new StudentIndeks();
        i3.setBroj(201); i3.setGodina(2024);
        i3.setStudProgramOznaka(ri.getOznaka());
        i3.setNacinFinansiranja("Samofinansiranje");
        i3.setAktivan(true); i3.setVaziOd(LocalDate.of(2024,10,1)); i3.setOstvarenoEspb(0);
        i3.setStudent(s3); i3.setStudijskiProgram(ri);
        i3 = studentIndeksRepository.save(i3);

        // GRUPE
        Grupa gSI = new Grupa();
        gSI.setNaziv("311");
        gSI.setStudijskiProgram(si);
        gSI.setSkolskaGodina(sg);
        gSI = grupaRepository.save(gSI);

        Grupa gRI = new Grupa();
        gRI.setNaziv("111");
        gRI.setStudijskiProgram(ri);
        gRI.setSkolskaGodina(sg);
        gRI = grupaRepository.save(gRI);

        // NASTAVNICI DRŽE PREDMETE (po školskoj godini)
        DrziPredmet dp1 = new DrziPredmet();
        dp1.setNastavnik(n1); dp1.setPredmet(p1); dp1.setSkolskaGodina(sg);
        dp1 = drziPredmetRepository.save(dp1);

        DrziPredmet dp2 = new DrziPredmet();
        dp2.setNastavnik(n1); dp2.setPredmet(p2); dp2.setSkolskaGodina(sg);
        dp2 = drziPredmetRepository.save(dp2);

        DrziPredmet dp3 = new DrziPredmet();
        dp3.setNastavnik(n2); dp3.setPredmet(p3); dp3.setSkolskaGodina(sg);
        dp3 = drziPredmetRepository.save(dp3);

        // STUDENTI SLUŠAJU PREDMETE
        SlusaPredmet sp1 = new SlusaPredmet();
        sp1.setStudentIndeks(i1); sp1.setDrziPredmet(dp1); sp1.setSkolskaGodina(sg); sp1.setGrupa(gSI);
        sp1 = slusaPredmetRepository.save(sp1);

        SlusaPredmet sp2 = new SlusaPredmet();
        sp2.setStudentIndeks(i1); sp2.setDrziPredmet(dp2); sp2.setSkolskaGodina(sg); sp2.setGrupa(gSI);
        sp2 = slusaPredmetRepository.save(sp2);

        SlusaPredmet sp3 = new SlusaPredmet();
        sp3.setStudentIndeks(i2); sp3.setDrziPredmet(dp1); sp3.setSkolskaGodina(sg); sp3.setGrupa(gSI);
        sp3 = slusaPredmetRepository.save(sp3);

        SlusaPredmet sp4 = new SlusaPredmet();
        sp4.setStudentIndeks(i3); sp4.setDrziPredmet(dp3); sp4.setSkolskaGodina(sg); sp4.setGrupa(gRI);
        sp4 = slusaPredmetRepository.save(sp4);

        /*// PREDISPITNE OBAVEZE + POENI
        PredispitnaObaveza kol1 = new PredispitnaObaveza();
        kol1.setPredmet(p1); kol1.setVrsta("Kolokvijum 1"); kol1.setMaxPoena(20);
        kol1 = predispitnaObavezaRepository.save(kol1);

        PredispitnaObaveza kol2 = new PredispitnaObaveza();
        kol2.setPredmet(p1); kol2.setVrsta("Kolokvijum 2"); kol2.setMaxPoena(20);
        kol2 = predispitnaObavezaRepository.save(kol2);

        // Luka ima 10 + 12 poena predispitnih iz SI101 : )
        PredispitnaIzlazak pe1 = new PredispitnaIzlazak();
        pe1.setSlusaPredmet(sp1); pe1.setPredispitnaObaveza(kol1);
        pe1.setPoeni(10); pe1.setDatum(LocalDate.of(2024,12,1));
        predispitnaIzlazakRepository.save(pe1);

        PredispitnaIzlazak pe2 = new PredispitnaIzlazak();
        pe2.setSlusaPredmet(sp1); pe2.setPredispitnaObaveza(kol2);
        pe2.setPoeni(12); pe2.setDatum(LocalDate.of(2025,1,15));
        predispitnaIzlazakRepository.save(pe2);*/

        // UPISI / OBNOVE / TOK STUDIJA
        UpisGodine upis1 = new UpisGodine();
        upis1.setStudentIndeks(i1);
        upis1.setSkolskaGodina(sg);
        upis1.setGodinaStudija(1);
        upis1.setDatum(LocalDate.of(2024,10,1));
        upis1.setNapomena("Prvi upis");
        upis1 = upisGodineRepository.save(upis1);

        UpisGodine upis2 = new UpisGodine();
        upis2.setStudentIndeks(i2);
        upis2.setSkolskaGodina(sg);
        upis2.setGodinaStudija(1);
        upis2.setDatum(LocalDate.of(2024,10,1));
        upis2.setNapomena("Prvi upis - student i2");
        upis2 = upisGodineRepository.save(upis2);

        UpisGodine upis3 = new UpisGodine();
        upis3.setStudentIndeks(i3);
        upis3.setSkolskaGodina(sg);
        upis3.setGodinaStudija(1);
        upis3.setDatum(LocalDate.of(2024,10,1));
        upis3.setNapomena("Prvi upis - student i3");
        upis3 = upisGodineRepository.save(upis3);

// --- UPLATE ZA TA DVA UPISA (JEDNA 1500€, DRUGA 3000€) ---
        double middle = fetchEurMiddleRate();

        Uplata uplataZaUpis2 = makeUplata(upis2, 1500.0, middle); // 1500€ za jedan od nova dva upisa
        Uplata uplataZaUpis3 = makeUplata(upis3, 3000.0, middle); // 3000€ za drugi

        uplataRepository.save(uplataZaUpis2);
        uplataRepository.save(uplataZaUpis3);

        ObnovaGodine obnovaDummy = new ObnovaGodine();
        obnovaDummy.setStudentIndeks(i2);
        obnovaDummy.setSkolskaGodina(sg);
        obnovaDummy.setGodinaStudija(1);
        obnovaDummy.setDatum(LocalDate.of(2024,10,1));
        obnovaDummy.setNapomena("Primer obnove");
        obnovaDummy = obnovaGodineRepository.save(obnovaDummy);

        ObnovaGodine obnovaLuka = new ObnovaGodine();
        obnovaLuka.setStudentIndeks(i1);
        obnovaLuka.setSkolskaGodina(sg);
        obnovaLuka.setGodinaStudija(1);
        obnovaLuka.setDatum(LocalDate.of(2024,10,1));
        obnovaLuka.setNapomena("Primer obnove za Lukу");
        obnovaGodineRepository.save(obnovaLuka);

        TokStudija tok1 = new TokStudija();
        tok1.setStudentIndeks(i1);
        tok1.setUpisi(Set.of(upis1));
        tok1.setObnove(Set.of()); // za sada prazno
        tok1RepositorySafeSave(tok1);

        // ISPITNI ROK / ISPIT / PRIJAVA / IZLAZAK / POLOŽEN
        IspitniRok sept = new IspitniRok();
        sept.setNaziv("Septembarski");
        sept.setDatumPocetka(LocalDateTime.of(2025,9,1,0,0));
        sept.setDatumZavrsetka(LocalDateTime.of(2025,9,30,23,59));
        sept.setSkolskaGodina(sg);
        sept = ispitniRokRepository.save(sept);

        Ispit ispitP1 = new Ispit();
        ispitP1.setIspitniRok(sept);
        ispitP1.setNastavnik(n1);
        ispitP1.setPredmet(p1);
        ispitP1.setDatumVremePocetka(LocalDateTime.of(2025,9,10,10,0));
        ispitP1.setZakljucen(false);
        ispitP1 = ispitRepository.save(ispitP1);

        IspitPrijava prijava = new IspitPrijava();
        prijava.setIspit(ispitP1);
        prijava.setStudentIndeks(i1);
        prijava.setDatum(LocalDate.of(2025,9,5));
        prijava = ispitPrijavaRepository.save(prijava);

        IspitIzlazak izlazak = new IspitIzlazak();
        izlazak.setIspitPrijava(prijava);
        izlazak.setStudentIndeks(i1);
        // predispitni 10+12=22; sa ispitom 33 => ukupno 55 → položen (ocena 6)
        izlazak.setBrojPoena(33);
        izlazak.setPonistava(false);
        izlazak.setNapomena("Uspesan izlazak");
        izlazak = ispitIzlazakRepository.save(izlazak);

        PolozenPredmet pp = new PolozenPredmet();
        pp.setStudentIndeks(i1);
        pp.setPredmet(p1);
        pp.setOcena(6);
        pp.setPriznat(false);
        pp.setIspitIzlazak(izlazak);
        polozenPredmetRepository.save(pp);

        // POLOŽENI PREDMETI

// --- Student i1 (Luka) ---
        PolozenPredmet pp1 = new PolozenPredmet();
        pp1.setStudentIndeks(i1);
        pp1.setPredmet(p1);
        pp1.setOcena(6);
        pp1.setPriznat(false);
        pp1.setIspitIzlazak(izlazak); // postoji iz prethodnog koda
        polozenPredmetRepository.save(pp1);

// --- Student i2 (Mina) ---
        Ispit ispitP2 = new Ispit();
        ispitP2.setIspitniRok(sept);
        ispitP2.setNastavnik(n1);
        ispitP2.setPredmet(p2); // Operativni sistemi
        ispitP2.setDatumVremePocetka(LocalDateTime.of(2025,9,12,10,0));
        ispitP2.setZakljucen(true);
        ispitP2 = ispitRepository.save(ispitP2);

        IspitPrijava prijava2 = new IspitPrijava();
        prijava2.setIspit(ispitP2);
        prijava2.setStudentIndeks(i2);
        prijava2.setDatum(LocalDate.of(2025,9,10));
        prijava2 = ispitPrijavaRepository.save(prijava2);

        IspitIzlazak izlazak2 = new IspitIzlazak();
        izlazak2.setIspitPrijava(prijava2);
        izlazak2.setStudentIndeks(i2);
        izlazak2.setBrojPoena(40);
        izlazak2.setPonistava(false);
        izlazak2.setNapomena("Uspesan izlazak");
        izlazak2 = ispitIzlazakRepository.save(izlazak2);

        PolozenPredmet pp2 = new PolozenPredmet();
        pp2.setStudentIndeks(i2);
        pp2.setPredmet(p2);
        pp2.setOcena(8);
        pp2.setPriznat(false);
        pp2.setIspitIzlazak(izlazak2);
        polozenPredmetRepository.save(pp2);

// --- Student i3 (Petar) ---
        Ispit ispitP3 = new Ispit();
        ispitP3.setIspitniRok(sept);
        ispitP3.setNastavnik(n2);
        ispitP3.setPredmet(p3); // Matematika 1
        ispitP3.setDatumVremePocetka(LocalDateTime.of(2025,9,15,10,0));
        ispitP3.setZakljucen(true);
        ispitP3 = ispitRepository.save(ispitP3);

        IspitPrijava prijava3 = new IspitPrijava();
        prijava3.setIspit(ispitP3);
        prijava3.setStudentIndeks(i3);
        prijava3.setDatum(LocalDate.of(2025,9,12));
        prijava3 = ispitPrijavaRepository.save(prijava3);

        IspitIzlazak izlazak3 = new IspitIzlazak();
        izlazak3.setIspitPrijava(prijava3);
        izlazak3.setStudentIndeks(i3);
        izlazak3.setBrojPoena(45);
        izlazak3.setPonistava(false);
        izlazak3.setNapomena("Uspesan izlazak");
        izlazak3 = ispitIzlazakRepository.save(izlazak3);

        PolozenPredmet pp3 = new PolozenPredmet();
        pp3.setStudentIndeks(i3);
        pp3.setPredmet(p3);
        pp3.setOcena(10);
        pp3.setPriznat(false);
        pp3.setIspitIzlazak(izlazak3);
        polozenPredmetRepository.save(pp3);

    // PREDISPITNE OBAVEZE + POENI
        PredispitnaObaveza kol1_p1 = new PredispitnaObaveza();
        kol1_p1.setPredmet(p1); kol1_p1.setVrsta("Kolokvijum 1"); kol1_p1.setMaxPoena(20);
        kol1_p1 = predispitnaObavezaRepository.save(kol1_p1);

        PredispitnaObaveza kol2_p1 = new PredispitnaObaveza();
        kol2_p1.setPredmet(p1); kol2_p1.setVrsta("Kolokvijum 2"); kol2_p1.setMaxPoena(20);
        kol2_p1 = predispitnaObavezaRepository.save(kol2_p1);

    // Predmet p2 (SI202 - Operativni sistemi)
        PredispitnaObaveza kol1_p2 = new PredispitnaObaveza();
        kol1_p2.setPredmet(p2); kol1_p2.setVrsta("Kolokvijum 1"); kol1_p2.setMaxPoena(20);
        kol1_p2 = predispitnaObavezaRepository.save(kol1_p2);

        PredispitnaObaveza kol2_p2 = new PredispitnaObaveza();
        kol2_p2.setPredmet(p2); kol2_p2.setVrsta("Kolokvijum 2"); kol2_p2.setMaxPoena(20);
        kol2_p2 = predispitnaObavezaRepository.save(kol2_p2);

    // Predmet p3 (RI101 - Matematika 1)
        PredispitnaObaveza kol1_p3 = new PredispitnaObaveza();
        kol1_p3.setPredmet(p3); kol1_p3.setVrsta("Kolokvijum 1"); kol1_p3.setMaxPoena(20);
        kol1_p3 = predispitnaObavezaRepository.save(kol1_p3);

        PredispitnaObaveza kol2_p3 = new PredispitnaObaveza();
        kol2_p3.setPredmet(p3); kol2_p3.setVrsta("Kolokvijum 2"); kol2_p3.setMaxPoena(20);
        kol2_p3 = predispitnaObavezaRepository.save(kol2_p3);

        // Luka ima 10 + 12 poena predispitnih iz SI101
        PredispitnaIzlazak pi1 = new PredispitnaIzlazak();
        pi1.setSlusaPredmet(sp1); pi1.setPredispitnaObaveza(kol1_p1);
        pi1.setPoeni(10); pi1.setDatum(LocalDate.of(2024,12,1));
        predispitnaIzlazakRepository.save(pi1);

        PredispitnaIzlazak pi2 = new PredispitnaIzlazak();
        pi2.setSlusaPredmet(sp1); pi2.setPredispitnaObaveza(kol2_p1);
        pi2.setPoeni(12); pi2.setDatum(LocalDate.of(2025,1,15));
        predispitnaIzlazakRepository.save(pi2);

    }

    // TokStudija repo je opciono
    private void tok1RepositorySafeSave(TokStudija tok) {
        try {
            tokStudijaRepository.save(tok);
        } catch (Exception ignored) {}
    }

    private double fetchEurMiddleRate() {
        var rest = new org.springframework.web.client.RestTemplate();
        String url = "https://kurs.resenje.org/api/v1/currencies/eur/rates/today";
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> map = rest.getForObject(url, java.util.Map.class);
        if (map == null || !map.containsKey("exchange_middle")) {
            throw new IllegalStateException("Nije moguće dohvatiti exchange_middle kurs EUR.");
        }
        Object val = map.get("exchange_middle");
        return (val instanceof Number) ? ((Number) val).doubleValue() : Double.parseDouble(String.valueOf(val));
    }

    private Uplata makeUplata(UpisGodine upis, double eurAmount, double middleRate) {
        Uplata u = new Uplata();
        u.setUpisGodine(upis);
        u.setDatum(LocalDate.now());
        u.setKurs(middleRate);
        u.setIznosRSD(eurAmount * middleRate);
        return u;
    }
}
