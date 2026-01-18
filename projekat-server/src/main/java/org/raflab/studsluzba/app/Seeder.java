package org.raflab.studsluzba.app;

import lombok.AllArgsConstructor;
import org.raflab.studsluzba.model.entities.*;
import org.raflab.studsluzba.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

    private final Random rand = new Random(42);

    @Override
    public void run(String... args) {
        if (studijskiProgramRepository.count() > 0L) return;

        // ŠKOLSKA GODINA
        SkolskaGodina sg = createSkolskaGodina("2024/2025.", true);

        // SREDNJE ŠKOLE I VŠ USTANOVE
        List<SrednjaSkola> srednjeSkole = createSrednjeSkole();
        List<VisokoskolskaUstanova> ustanove = createVisokoskolskeUstanove();

        // STUDIJSKI PROGRAMI
        List<StudijskiProgram> programi = createStudijskePrograme();

        // PREDMETI
        List<Predmet> predmeti = createPredmete(programi);

        // NASTAVNICI
        List<Nastavnik> nastavnici = createNastavnike(ustanove);
        createNastavnickaZvanja(nastavnici);

        // STUDENTI
        List<StudentPodaci> studenti = createStudente(srednjeSkole);
        List<StudentIndeks> indeksi = createIndekse(studenti, programi, sg);

        // GRUPE
        List<Grupa> grupe = createGrupe(programi, sg);

        // NASTAVNICI DRŽE PREDMETE
        List<DrziPredmet> drziPredmete = createDrziPredmete(nastavnici, predmeti, sg);

        // STUDENTI SLUŠAJU PREDMETE
        List<SlusaPredmet> slusaPredmete = createSlusaPredmete(indeksi, drziPredmete, grupe, sg);

        // PREDISPITNE OBAVEZE
        Map<Predmet, List<PredispitnaObaveza>> predispitneMap = createPredispitneObaveze(predmeti);
        createPredispitneIzlaske(slusaPredmete, predispitneMap);

        // UPISI I OBNOVE
        double eurRate = fetchEurMiddleRate();
        createUpiseIObnove(indeksi, sg, eurRate);

        // ISPITNI ROKOVI I ISPITI
        List<IspitniRok> rokovi = createIspitneRokove(sg);
        List<Ispit> ispiti = createIspite(rokovi, drziPredmete, nastavnici);

        // PRIJAVE, IZLASCI, POLOŽENI
        createIspitePrijaveIzlaskePolozene(ispiti, slusaPredmete, indeksi);

        // MINA (RI 102/24) - spec data za testiranje
        seedOneStudentWithLotsOfData("RI", 102, 2024, indeksi, programi, grupe, drziPredmete, sg, eurRate, ispiti);
    }

    private SkolskaGodina createSkolskaGodina(String naziv, boolean aktivna) {
        SkolskaGodina sg = new SkolskaGodina();
        sg.setNaziv(naziv);
        sg.setAktivna(aktivna);
        return skolskaGodinaRepository.save(sg);
    }

    private List<SrednjaSkola> createSrednjeSkole() {
        List<SrednjaSkola> liste = new ArrayList<>();
        String[][] data = {
                {"Gimnazija 'Sveti Sava'", "Beograd", "GIMNAZIJA"},
                {"Matematička gimnazija", "Beograd", "GIMNAZIJA"},
                {"Filološka gimnazija", "Beograd", "GIMNAZIJA"},
                {"Gimnazija 'Jovan Jovanović Zmaj'", "Novi Sad", "GIMNAZIJA"},
                {"Tehnička škola 'Nikola Tesla'", "Niš", "STRUCNA"},
                {"Elektrotehnička škola", "Beograd", "STRUCNA"},
                {"Medicinska škola", "Beograd", "STRUCNA"},
                {"Muzička škola", "Kragujevac", "UMETNICKA"}
        };
        for (String[] d : data) {
            SrednjaSkola s = new SrednjaSkola();
            s.setNaziv(d[0]);
            s.setMesto(d[1]);
            s.setVrsta(SrednjaSkola.VrstaSkole.valueOf(d[2]));
            liste.add(srednjaSkolaRepository.save(s));
        }
        return liste;
    }

    private List<VisokoskolskaUstanova> createVisokoskolskeUstanove() {
        List<VisokoskolskaUstanova> liste = new ArrayList<>();
        String[][] data = {
                {"Računarski fakultet", "Beograd", "FAKULTET"},
                {"Elektrotehnički fakultet", "Beograd", "FAKULTET"},
                {"Matematički fakultet", "Beograd", "FAKULTET"},
                {"Fakultet organizacionih nauka", "Beograd", "FAKULTET"},
                {"SFIT", "Beograd", "FAKULTET"}
        };
        for (String[] d : data) {
            VisokoskolskaUstanova u = new VisokoskolskaUstanova();
            u.setNaziv(d[0]);
            u.setMesto(d[1]);
            u.setVrsta(VisokoskolskaUstanova.Vrsta.valueOf(d[2]));
            liste.add(visokoskolskaUstanovaRepository.save(u));
        }
        return liste;
    }

    private List<StudijskiProgram> createStudijskePrograme() {
        List<StudijskiProgram> liste = new ArrayList<>();
        String[][] data = {
                {"SI", "Softversko inženjerstvo", "2023", "Dipl. inž. softvera", "4", "8", "OAS", "240"},
                {"RI", "Računarsko inženjerstvo", "2022", "Dipl. inž. računarstva", "4", "8", "OAS", "240"},
                {"RN", "Računarske nauke", "2022", "Dipl. inž. računarskih nauka", "4", "8", "OAS", "240"},
                {"IIS", "Informacioni inženjering i sistemi", "2023", "Dipl. inž. informacionih sistema", "4", "8", "OAS", "240"},
                {"AI", "Veštačka inteligencija", "2024", "Dipl. inž. veštačke inteligencije", "4", "8", "OAS", "240"}
        };
        for (String[] d : data) {
            StudijskiProgram sp = new StudijskiProgram();
            sp.setOznaka(d[0]);
            sp.setNaziv(d[1]);
            sp.setGodinaAkreditacije(Integer.parseInt(d[2]));
            sp.setZvanje(d[3]);
            sp.setTrajanjeGodina(Integer.parseInt(d[4]));
            sp.setTrajanjeSemestara(Integer.parseInt(d[5]));
            sp.setVrstaStudija(d[6]);
            sp.setUkupnoEspb(Integer.parseInt(d[7]));
            liste.add(studijskiProgramRepository.save(sp));
        }
        return liste;
    }

    private List<Predmet> createPredmete(List<StudijskiProgram> programi) {
        List<Predmet> liste = new ArrayList<>();
        String[][] data = {
                // SI predmeti
                {"SI101", "Uvod u programiranje", "Osnove Java", "8", "1", "3", "2", "SI"},
                {"SI102", "Matematika 1", "Kalkulus", "7", "1", "3", "3", "SI"},
                {"SI103", "Osnove računarstva", "Digitalna elektronika", "6", "1", "2", "2", "SI"},
                {"SI201", "Objektno programiranje", "Java OOP", "8", "2", "3", "2", "SI"},
                {"SI202", "Operativni sistemi", "Procesi, niti", "7", "3", "3", "2", "SI"},
                {"SI203", "Baze podataka", "SQL, relacione BP", "8", "3", "2", "3", "SI"},
                {"SI301", "Softversko inženjerstvo", "SDLC, Agile", "7", "5", "3", "2", "SI"},
                {"SI302", "Web programiranje", "HTML, CSS, JS", "8", "5", "2", "3", "SI"},

                // RI predmeti
                {"RI101", "Matematika 1", "Diferencijalni račun", "8", "1", "3", "3", "RI"},
                {"RI102", "Fizika", "Mehanika", "7", "1", "3", "2", "RI"},
                {"RI103", "Programiranje 1", "C jezik", "8", "1", "2", "3", "RI"},
                {"RI201", "Digitalna elektronika", "Logička kola", "7", "2", "3", "2", "RI"},
                {"RI202", "Strukture podataka", "Stabla, grafovi", "8", "3", "2", "3", "RI"},
                {"RI301", "Računarske mreže", "TCP/IP", "7", "5", "3", "2", "RI"},

                // RN predmeti
                {"RN101", "Diskretna matematika", "Kombinatorika", "8", "1", "3", "3", "RN"},
                {"RN102", "Linearna algebra", "Matrice, vektori", "7", "1", "3", "3", "RN"},
                {"RN103", "Algoritmi 1", "Osnovni algoritmi", "8", "1", "2", "3", "RN"},
                {"RN201", "Verovatnoća i statistika", "Statistička analiza", "7", "2", "3", "2", "RN"},
                {"RN202", "Algoritmi 2", "Napredni algoritmi", "8", "3", "2", "3", "RN"},
                {"RN301", "Teorija grafova", "Primene grafova", "7", "5", "3", "2", "RN"},

                // IIS predmeti
                {"IIS101", "Osnove informacionih sistema", "Uvod u IS", "7", "1", "3", "2", "IIS"},
                {"IIS102", "Programiranje", "Python osnove", "8", "1", "2", "3", "IIS"},
                {"IIS201", "Upravljanje projektima", "PM metodologije", "7", "3", "3", "2", "IIS"},
                {"IIS301", "Poslovna inteligencija", "BI alati", "8", "5", "2", "3", "IIS"},

                // AI predmeti
                {"AI101", "Uvod u AI", "Istorija AI", "7", "1", "3", "2", "AI"},
                {"AI102", "Python za AI", "NumPy, Pandas", "8", "1", "2", "3", "AI"},
                {"AI201", "Mašinsko učenje", "Supervised learning", "8", "3", "3", "2", "AI"},
                {"AI301", "Duboko učenje", "Neural networks", "8", "5", "2", "3", "AI"},
                {"AI302", "NLP", "Obrada jezika", "7", "5", "3", "2", "AI"}
        };

        Map<String, StudijskiProgram> progMap = new HashMap<>();
        for (StudijskiProgram sp : programi) {
            progMap.put(sp.getOznaka(), sp);
        }

        for (String[] d : data) {
            Predmet p = new Predmet();
            p.setSifra(d[0]);
            p.setNaziv(d[1]);
            p.setOpis(d[2]);
            p.setEspb(Integer.parseInt(d[3]));
            p.setSemestar(Integer.parseInt(d[4]));
            p.setFondPredavanja(Integer.parseInt(d[5]));
            p.setFondVezbi(Integer.parseInt(d[6]));
            p.setObavezan(true);
            p.setStudProgram(progMap.get(d[7]));
            liste.add(predmetRepository.save(p));
        }
        return liste;
    }

    private List<Nastavnik> createNastavnike(List<VisokoskolskaUstanova> ustanove) {
        List<Nastavnik> liste = new ArrayList<>();
        String[][] data = {
                {"Nikola", "Jovanović", "Petar", "njovanovic@raf.rs", "060111222", "Bulevar 1, Beograd", "1980-05-10", "M", "8005101234567"},
                {"Milena", "Milić", "Ana", "mmilic@raf.rs", "060333444", "Zmaj Jovina 2, Novi Sad", "1983-07-02", "F", "8307029876543"},
                {"Marko", "Marković", "Ivan", "mmarkovic@raf.rs", "060555666", "Kneza Miloša 5, Beograd", "1975-03-15", "M", "7503151122334"},
                {"Ana", "Anić", "Marija", "aanic@raf.rs", "060777888", "Cara Dušana 10, Niš", "1985-11-20", "F", "8511205544332"},
                {"Petar", "Petrović", "Jovan", "ppetrovic@raf.rs", "060999000", "Svetog Save 3, Kragujevac", "1978-08-25", "M", "7808256677889"},
                {"Jovana", "Jović", "Sofija", "jjovic@raf.rs", "061111222", "Partizanska 7, Beograd", "1982-01-12", "F", "8201129988776"},
                {"Stefan", "Stefanović", "Lazar", "sstefanovic@raf.rs", "061333444", "Kralja Petra 15, Novi Sad", "1977-06-30", "M", "7706301122334"},
                {"Jelena", "Jelenković", "Milica", "jjelenkovic@raf.rs", "061555666", "Nemanjina 22, Beograd", "1984-09-18", "F", "8409185566778"},
                {"Milan", "Milanović", "Nikola", "mmilanovic@raf.rs", "061777888", "Takovska 8, Beograd", "1979-12-05", "M", "7912059900112"},
                {"Teodora", "Teodorović", "Jovana", "tteodorovic@raf.rs", "061999000", "Vojvode Stepe 12, Beograd", "1986-04-22", "F", "8604223344556"},
                {"Vladimir", "Vuković", "Milan", "vvukovic@raf.rs", "062111222", "Knez Mihailova 30, Beograd", "1981-02-14", "M", "8102147788990"},
                {"Katarina", "Kostić", "Ana", "kkostic@raf.rs", "062333444", "Bulevar Kralja Aleksandra 50, Beograd", "1987-07-08", "F", "8707086655443"},
                {"Nemanja", "Nikolić", "Đorđe", "nnikolic@raf.rs", "062555666", "Makedonska 25, Beograd", "1976-10-11", "M", "7610115544332"},
                {"Ivana", "Ilić", "Marija", "iilic@raf.rs", "062777888", "Resavska 18, Beograd", "1988-05-29", "F", "8805294433221"},
                {"Aleksandar", "Aleksić", "Petar", "aaleksic@raf.rs", "062999000", "Terazije 5, Beograd", "1974-03-17", "M", "7403172211009"}
        };

        for (String[] d : data) {
            Nastavnik n = new Nastavnik();
            n.setIme(d[0]);
            n.setPrezime(d[1]);
            n.setSrednjeIme(d[2]);
            n.setEmail(d[3]);
            n.setBrojTelefona(d[4]);
            n.setAdresa(d[5]);
            n.setDatumRodjenja(LocalDate.parse(d[6]));
            n.setPol(d[7].charAt(0));
            n.setJmbg(d[8]);
            n.setZavrseneUstanove(Set.of(ustanove.get(rand.nextInt(ustanove.size()))));
            liste.add(nastavnikRepository.save(n));
        }
        return liste;
    }

    private void createNastavnickaZvanja(List<Nastavnik> nastavnici) {
        String[] zvanja = {"Asistent", "Docent", "Vanredni profesor", "Redovni profesor"};
        String[] oblasti = {"Računarske nauke", "Softversko inženjerstvo", "Informacioni sistemi", "Veštačka inteligencija"};
        String[] uzeOblasti = {"Algoritmi", "Baze podataka", "Web tehnologije", "Mašinsko učenje", "Računarske mreže"};

        for (Nastavnik n : nastavnici) {
            NastavnikZvanje z = new NastavnikZvanje();
            z.setNastavnik(n);
            z.setZvanje(zvanja[rand.nextInt(zvanja.length)]);
            z.setNaucnaOblast(oblasti[rand.nextInt(oblasti.length)]);
            z.setUzaNaucnaOblast(uzeOblasti[rand.nextInt(uzeOblasti.length)]);
            z.setDatumIzbora(LocalDate.of(2015 + rand.nextInt(9), 1 + rand.nextInt(12), 1 + rand.nextInt(28)));
            z.setAktivno(true);
            nastavnikZvanjeRepository.save(z);
        }
    }

    private List<StudentPodaci> createStudente(List<SrednjaSkola> srednjeSkole) {
        List<StudentPodaci> liste = new ArrayList<>();
        String[][] imena = {
                {"Luka", "Marković", "A", "M"}, {"Mina", "Jovanović", "B", "F"}, {"Petar", "Ilić", "V", "M"},
                {"Ana", "Nikolić", "G", "F"}, {"Stefan", "Petrović", "D", "M"}, {"Jelena", "Đorđević", "Đ", "F"},
                {"Nikola", "Stefanović", "E", "M"}, {"Milica", "Stanković", "Ž", "F"}, {"Marko", "Popović", "Z", "M"},
                {"Ivana", "Kostić", "I", "F"}, {"Dušan", "Pavlović", "J", "M"}, {"Teodora", "Mladenović", "K", "F"},
                {"Nemanja", "Živković", "L", "M"}, {"Jovana", "Đurić", "Lj", "F"}, {"Vladimir", "Vuković", "M", "M"},
                {"Katarina", "Simić", "N", "F"}, {"Aleksandar", "Milošević", "Nj", "M"}, {"Milena", "Tomić", "O", "F"},
                {"Filip", "Radovanović", "P", "M"}, {"Sara", "Antić", "R", "F"}, {"Miloš", "Lazić", "S", "M"},
                {"Tamara", "Stojanović", "T", "F"}, {"Jovan", "Arsić", "Ć", "M"}, {"Maja", "Vasić", "U", "F"},
                {"Andrija", "Ristić", "F", "M"}, {"Nina", "Marić", "H", "F"}, {"Uroš", "Savić", "C", "M"},
                {"Anastasija", "Cvijović", "Č", "F"}, {"Nikola", "Mitrović", "Dž", "M"}, {"Jovana", "Filipović", "Š", "F"},
                {"Lazar", "Todorović", "A", "M"}, {"Emilija", "Krstić", "B", "F"}, {"Danilo", "Nikolić", "V", "M"},
                {"Anđela", "Zdravković", "G", "F"}, {"Dimitrije", "Janković", "D", "M"}, {"Kristina", "Petrović", "Đ", "F"},
                {"Vuk", "Radenković", "E", "M"}, {"Dragana", "Maksimović", "Ž", "F"}, {"Strahinja", "Milenković", "Z", "M"},
                {"Isidora", "Aleksić", "I", "F"}, {"Bojan", "Mihajlović", "J", "M"}, {"Aleksandra", "Stanojević", "K", "F"},
                {"Matija", "Ilić", "L", "M"}, {"Dunja", "Obradović", "Lj", "F"}, {"Ivan", "Živanović", "M", "M"},
                {"Sofija", "Đokić", "N", "F"}, {"David", "Milovanović", "Nj", "M"}, {"Jelisaveta", "Petković", "O", "F"},
                {"Pavle", "Stojanović", "P", "M"}, {"Natalija", "Mladenović", "R", "F"}
        };

        String[] gradovi = {"Beograd", "Novi Sad", "Niš", "Kragujevac", "Subotica", "Pančevo"};
        String[] ulice = {"Ulica 1", "Ulica 2", "Ulica 3", "Bulevar 10", "Trg 5", "Kneza Miloša 15"};

        for (int i = 0; i < imena.length; i++) {
            StudentPodaci s = new StudentPodaci();
            s.setIme(imena[i][0]);
            s.setPrezime(imena[i][1]);
            s.setSrednjeIme(imena[i][2]);
            s.setPol(imena[i][3].charAt(0));

            int year = 2000 + rand.nextInt(4); // 2000-2003
            int month = 1 + rand.nextInt(12);
            int day = 1 + rand.nextInt(28);
            s.setDatumRodjenja(LocalDate.of(year, month, day));
            s.setJmbg(String.format("%02d%02d%03d%06d", day, month, year % 1000, 100000 + i));

            String grad = gradovi[rand.nextInt(gradovi.length)];
            s.setDrzavaRodjenja("Srbija");
            s.setMestoRodjenja(grad);
            s.setDrzavljanstvo("Srbija");
            s.setNacionalnost("Srpska");
            s.setMestoPrebivalista(grad);
            s.setAdresaPrebivalista(ulice[rand.nextInt(ulice.length)]);
            s.setBrojTelefonaMobilni("06" + (10000000 + rand.nextInt(90000000)));
            s.setBrojTelefonaFiksni("01" + (1000000 + rand.nextInt(9000000)));

            String emailPrefix = s.getIme().toLowerCase() + s.getPrezime().toLowerCase();
            s.setEmailFakultetski(emailPrefix + (10000 + i) + "@raf.rs");
            s.setEmailPrivatni(emailPrefix + "@gmail.com");

            s.setBrojLicneKarte(s.getIme().substring(0, 2).toUpperCase() + (100000 + i));
            s.setLicnuKartuIzdao("MUP " + grad.substring(0, 2).toUpperCase());

            s.setSrednjaSkola(srednjeSkole.get(rand.nextInt(srednjeSkole.size())));
            s.setUspehSrednjaSkola(3.5 + rand.nextDouble() * 1.5); // 3.5 - 5.0
            s.setUspehPrijemni(60.0 + rand.nextDouble() * 40.0); // 60 - 100

            liste.add(studentPodaciRepository.save(s));
        }
        return liste;
    }

    private List<StudentIndeks> createIndekse(List<StudentPodaci> studenti, List<StudijskiProgram> programi, SkolskaGodina sg) {
        List<StudentIndeks> liste = new ArrayList<>();
        String[] finansiranja = {"Budzet", "Samofinansiranje"};

        for (int i = 0; i < studenti.size(); i++) {
            StudentIndeks idx = new StudentIndeks();
            idx.setBroj(101 + i);
            idx.setGodina(2024);

            StudijskiProgram prog = programi.get(i % programi.size());
            idx.setStudProgramOznaka(prog.getOznaka());
            idx.setStudijskiProgram(prog);
            idx.setNacinFinansiranja(finansiranja[rand.nextInt(finansiranja.length)]);
            idx.setAktivan(true);
            idx.setVaziOd(LocalDate.of(2024, 10, 1));
            idx.setOstvarenoEspb(0);
            idx.setStudent(studenti.get(i));

            // **GENERISANJE EMAILA**
            StudentPodaci s = studenti.get(i);
            String fakultetskiEmail =
                    s.getIme().substring(0,1).toLowerCase() +
                            s.getPrezime().toLowerCase() +
                            idx.getBroj() +
                            idx.getGodina() +
                            prog.getOznaka().toLowerCase() + "@raf.rs";
            s.setEmailFakultetski(fakultetskiEmail);

            liste.add(studentIndeksRepository.save(idx));
        }

        return liste;
    }

    private List<Grupa> createGrupe(List<StudijskiProgram> programi, SkolskaGodina sg) {
        List<Grupa> liste = new ArrayList<>();
        String[] naziviGrupa = {"101", "102", "201", "202", "301", "302", "311", "312", "111", "112"};

        for (StudijskiProgram prog : programi) {
            for (int i = 0; i < 3; i++) {
                Grupa g = new Grupa();
                g.setNaziv(naziviGrupa[rand.nextInt(naziviGrupa.length)]);
                g.setStudijskiProgram(prog);
                g.setSkolskaGodina(sg);
                liste.add(grupaRepository.save(g));
            }
        }
        return liste;
    }

    private List<DrziPredmet> createDrziPredmete(List<Nastavnik> nastavnici, List<Predmet> predmeti, SkolskaGodina sg) {
        List<DrziPredmet> liste = new ArrayList<>();

        for (Predmet p : predmeti) {
            DrziPredmet dp = new DrziPredmet();
            dp.setNastavnik(nastavnici.get(rand.nextInt(nastavnici.size())));
            dp.setPredmet(p);
            dp.setSkolskaGodina(sg);
            liste.add(drziPredmetRepository.save(dp));
        }
        return liste;
    }
    private List<SlusaPredmet> createSlusaPredmete(
            List<StudentIndeks> indeksi,
            List<DrziPredmet> drziPredmete,
            List<Grupa> grupe,
            SkolskaGodina sg
    ) {
        List<SlusaPredmet> liste = new ArrayList<>();

        for (StudentIndeks idx : indeksi) {
            StudijskiProgram prog = idx.getStudijskiProgram();

            // grupe tog programa
            List<Grupa> grupePrograma = grupe.stream()
                    .filter(g -> g.getStudijskiProgram().equals(prog))
                    .collect(Collectors.toList());


            Grupa grupa = grupePrograma.isEmpty()
                    ? grupe.get(0)
                    : grupePrograma.get(rand.nextInt(grupePrograma.size()));

            // svi predmeti tog programa
            List<DrziPredmet> predmetiPrograma = drziPredmete.stream()
                    .filter(dp -> dp.getPredmet().getStudProgram().equals(prog))
                    .collect(Collectors.toList());


            Collections.shuffle(predmetiPrograma);

            int brojPredmeta = 3 + rand.nextInt(4); // 3–6

            Set<Long> dodatiPredmeti = new HashSet<>();

            for (DrziPredmet dp : predmetiPrograma) {
                if (dodatiPredmeti.size() >= brojPredmeta) break;

                Long predmetId = dp.getPredmet().getId();
                if (dodatiPredmeti.contains(predmetId)) continue;

                dodatiPredmeti.add(predmetId);

                SlusaPredmet sp = new SlusaPredmet();
                sp.setStudentIndeks(idx);
                sp.setDrziPredmet(dp);
                sp.setSkolskaGodina(sg);
                sp.setGrupa(grupa);

                liste.add(slusaPredmetRepository.save(sp));
            }
        }
        return liste;
    }


    private Map<Predmet, List<PredispitnaObaveza>> createPredispitneObaveze(List<Predmet> predmeti) {
        Map<Predmet, List<PredispitnaObaveza>> mapa = new HashMap<>();

        for (Predmet p : predmeti) {
            List<PredispitnaObaveza> obaveze = new ArrayList<>();

            PredispitnaObaveza kol1 = new PredispitnaObaveza();
            kol1.setPredmet(p);
            kol1.setVrsta("Kolokvijum 1");
            kol1.setMaxPoena(20);
            obaveze.add(predispitnaObavezaRepository.save(kol1));

            PredispitnaObaveza kol2 = new PredispitnaObaveza();
            kol2.setPredmet(p);
            kol2.setVrsta("Kolokvijum 2");
            kol2.setMaxPoena(20);
            obaveze.add(predispitnaObavezaRepository.save(kol2));

            if (rand.nextBoolean()) {
                PredispitnaObaveza dz = new PredispitnaObaveza();
                dz.setPredmet(p);
                dz.setVrsta("Domaći zadatak");
                dz.setMaxPoena(10);
                obaveze.add(predispitnaObavezaRepository.save(dz));
            }

            mapa.put(p, obaveze);
        }
        return mapa;
    }

    private void createPredispitneIzlaske(List<SlusaPredmet> slusaPredmete, Map<Predmet, List<PredispitnaObaveza>> predispitneMap) {
        for (SlusaPredmet sp : slusaPredmete) {
            Predmet predmet = sp.getDrziPredmet().getPredmet();
            List<PredispitnaObaveza> obaveze = predispitneMap.get(predmet);

            if (obaveze == null) continue;

            for (PredispitnaObaveza ob : obaveze) {
                if (rand.nextDouble() > 0.3) { // 70% šanse da student izađe
                    PredispitnaIzlazak pi = new PredispitnaIzlazak();
                    pi.setSlusaPredmet(sp);
                    pi.setPredispitnaObaveza(ob);
                    pi.setPoeni((int) (rand.nextDouble() * ob.getMaxPoena()));
                    pi.setDatum(LocalDate.of(2024, 11 + rand.nextInt(2), 1 + rand.nextInt(28)));
                    predispitnaIzlazakRepository.save(pi);
                }
            }
        }
    }

    private void createUpiseIObnove(List<StudentIndeks> indeksi, SkolskaGodina sg, double eurRate) {
        for (StudentIndeks idx : indeksi) {
            int godina = 1 + rand.nextInt(4); // 1-4

            UpisGodine upis = new UpisGodine();
            upis.setStudentIndeks(idx);
            upis.setSkolskaGodina(sg);
            upis.setGodinaStudija(godina);
            upis.setDatum(LocalDate.of(2024, 10, 1));
            upis.setNapomena("Upis u " + godina + ". godinu");
            upis = upisGodineRepository.save(upis);

            if (idx.getNacinFinansiranja().equals("Samofinansiranje")) {
                double iznos = 1000.0 + rand.nextDouble() * 2500.0; // 1000-3500 EUR
                Uplata uplata = makeUplata(upis, iznos, eurRate);
                uplataRepository.save(uplata);
            }

            if (rand.nextDouble() > 0.8) { // 20% šanse za obnovu
                ObnovaGodine obnova = new ObnovaGodine();
                obnova.setStudentIndeks(idx);
                obnova.setSkolskaGodina(sg);
                obnova.setGodinaStudija(godina);
                obnova.setDatum(LocalDate.of(2024, 10, 1));
                obnova.setNapomena("Obnova " + godina + ". godine");
                obnovaGodineRepository.save(obnova);
            }

            TokStudija tok = new TokStudija();
            tok.setStudentIndeks(idx);
            tok.setUpisi(Set.of(upis));
            tok.setObnove(Set.of());
            tokStudijaRepositorySafeSave(tok);
        }
    }

    private List<IspitniRok> createIspitneRokove(SkolskaGodina sg) {
        List<IspitniRok> liste = new ArrayList<>();
        Object[][] rokovi = {
                {"Januarski", LocalDateTime.of(2025, 1, 15, 0, 0), LocalDateTime.of(2025, 2, 5, 23, 59)},
                {"Februarski", LocalDateTime.of(2025, 2, 10, 0, 0), LocalDateTime.of(2025, 2, 28, 23, 59)},
                {"Aprilski", LocalDateTime.of(2025, 4, 1, 0, 0), LocalDateTime.of(2025, 4, 20, 23, 59)},
                {"Junski", LocalDateTime.of(2025, 6, 15, 0, 0), LocalDateTime.of(2025, 7, 10, 23, 59)},
                {"Avgustovski", LocalDateTime.of(2025, 8, 20, 0, 0), LocalDateTime.of(2025, 9, 10, 23, 59)},
                {"Septembarski", LocalDateTime.of(2025, 9, 1, 0, 0), LocalDateTime.of(2025, 9, 30, 23, 59)}
        };

        for (Object[] r : rokovi) {
            IspitniRok rok = new IspitniRok();
            rok.setNaziv((String) r[0]);
            rok.setDatumPocetka((LocalDateTime) r[1]);
            rok.setDatumZavrsetka((LocalDateTime) r[2]);
            rok.setSkolskaGodina(sg);
            liste.add(ispitniRokRepository.save(rok));
        }
        return liste;
    }

    private List<Ispit> createIspite(List<IspitniRok> rokovi, List<DrziPredmet> drziPredmete, List<Nastavnik> nastavnici) {
        List<Ispit> liste = new ArrayList<>();

        for (IspitniRok rok : rokovi) {
            int danStart = rok.getDatumPocetka().getDayOfMonth();
            int mesec = rok.getDatumPocetka().getMonthValue();
            int godina = rok.getDatumPocetka().getYear();

            List<DrziPredmet> shuffled = new ArrayList<>(drziPredmete);
            Collections.shuffle(shuffled);

            int brojIspita = 10 + rand.nextInt(10); // 10-20 ispita po roku
            for (int i = 0; i < Math.min(brojIspita, shuffled.size()); i++) {
                DrziPredmet dp = shuffled.get(i);

                Ispit ispit = new Ispit();
                ispit.setIspitniRok(rok);
                ispit.setNastavnik(dp.getNastavnik());
                ispit.setPredmet(dp.getPredmet());

                int dan = danStart + rand.nextInt(10);
                int sat = 9 + rand.nextInt(6); // 9-14h
                ispit.setDatumVremePocetka(LocalDateTime.of(godina, mesec, dan, sat, 0));
                ispit.setZakljucen(rand.nextBoolean());

                liste.add(ispitRepository.save(ispit));
            }
        }
        return liste;
    }

    private void createIspitePrijaveIzlaskePolozene(List<Ispit> ispiti, List<SlusaPredmet> slusaPredmete, List<StudentIndeks> indeksi) {
        Map<StudentIndeks, List<SlusaPredmet>> indexToSlusaMap = new HashMap<>();
        for (SlusaPredmet sp : slusaPredmete) {
            indexToSlusaMap.computeIfAbsent(sp.getStudentIndeks(), k -> new ArrayList<>()).add(sp);
        }

        for (Ispit ispit : ispiti) {
            Predmet predmet = ispit.getPredmet();
            int brojPrijava = 5 + rand.nextInt(15); // 5-20 prijava po ispitu

            List<StudentIndeks> kandidati = new ArrayList<>();
            for (StudentIndeks idx : indeksi) {
                List<SlusaPredmet> slusaList = indexToSlusaMap.get(idx);
                if (slusaList != null) {
                    boolean slusaPredmet = slusaList.stream()
                            .anyMatch(sp -> sp.getDrziPredmet().getPredmet().equals(predmet));
                    if (slusaPredmet) {
                        kandidati.add(idx);
                    }
                }
            }

            if (kandidati.isEmpty()) continue;

            Collections.shuffle(kandidati);
            int actualPrijava = Math.min(brojPrijava, kandidati.size());

            for (int i = 0; i < actualPrijava; i++) {
                StudentIndeks idx = kandidati.get(i);

                IspitPrijava prijava = new IspitPrijava();
                prijava.setIspit(ispit);
                prijava.setStudentIndeks(idx);
                prijava.setDatum(ispit.getDatumVremePocetka().toLocalDate().minusDays(3 + rand.nextInt(10)));
                prijava = ispitPrijavaRepository.save(prijava);

                if (rand.nextDouble() > 0.2) { // 80% izlazi na ispit
                    int poeniIspit = rand.nextInt(61); // 0-60 poena sa ispita

                    List<SlusaPredmet> slusaList = indexToSlusaMap.get(idx);
                    SlusaPredmet sp = slusaList.stream()
                            .filter(s -> s.getDrziPredmet().getPredmet().equals(predmet))
                            .findFirst()
                            .orElse(null);

                    int poeniPredispitni = 0;
                    if (sp != null) {
                        List<PredispitnaIzlazak> predispitni = predispitnaIzlazakRepository.findAllBySlusaPredmet(sp);
                        poeniPredispitni = predispitni.stream()
                                .mapToInt(PredispitnaIzlazak::getPoeni)
                                .sum();
                    }

                    int ukupno = poeniIspit + poeniPredispitni;

                    IspitIzlazak izlazak = new IspitIzlazak();
                    izlazak.setIspitPrijava(prijava);
                    izlazak.setStudentIndeks(idx);
                    izlazak.setBrojPoena(poeniIspit);
                    izlazak.setPonistava(false);
                    izlazak.setNapomena(ukupno >= 51 ? "Položio/la" : "Pao/la");
                    izlazak = ispitIzlazakRepository.save(izlazak);

                    if (ukupno >= 51) {
                        int ocena = 6;
                        if (ukupno >= 61) ocena = 7;
                        if (ukupno >= 71) ocena = 8;
                        if (ukupno >= 81) ocena = 9;
                        if (ukupno >= 91) ocena = 10;

                        PolozenPredmet pp = new PolozenPredmet();
                        pp.setStudentIndeks(idx);
                        pp.setPredmet(predmet);
                        pp.setOcena(ocena);
                        pp.setPriznat(false);
                        pp.setIspitIzlazak(izlazak);
                        polozenPredmetRepository.save(pp);

                        idx.setOstvarenoEspb(idx.getOstvarenoEspb() + predmet.getEspb());
                        studentIndeksRepository.save(idx);
                    }
                }
            }
        }
    }

    private void tokStudijaRepositorySafeSave(TokStudija tok) {
        try {
            tokStudijaRepository.save(tok);
        } catch (Exception ignored) {}
    }

    private double fetchEurMiddleRate() {
        try {
            var rest = new org.springframework.web.client.RestTemplate();
            String url = "https://kurs.resenje.org/api/v1/currencies/eur/rates/today";
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> map = rest.getForObject(url, java.util.Map.class);
            if (map == null || !map.containsKey("exchange_middle")) {
                return 117.0; // fallback
            }
            Object val = map.get("exchange_middle");
            return (val instanceof Number) ? ((Number) val).doubleValue() : Double.parseDouble(String.valueOf(val));
        } catch (Exception e) {
            return 117.0; // fallback u slučaju greške
        }
    }

    private Uplata makeUplata(UpisGodine upis, double eurAmount, double middleRate) {
        Uplata u = new Uplata();
        u.setUpisGodine(upis);
        u.setDatum(LocalDate.now());
        u.setKurs(middleRate);
        u.setIznosEUR(eurAmount);
        u.setIznosRSD(eurAmount * middleRate);
        return u;
    }

    private void seedOneStudentWithLotsOfData(
            String oznakaPrograma,
            int brojIndeksa,
            int godinaIndeksa,
            List<StudentIndeks> indeksi,
            List<StudijskiProgram> programi,
            List<Grupa> grupe,
            List<DrziPredmet> drziPredmeti,
            SkolskaGodina aktivnaSg,
            double eurRate,
            List<Ispit> ispiti
    ) {
        StudentIndeks idx = indeksi.stream()
                .filter(i -> oznakaPrograma.equalsIgnoreCase(i.getStudProgramOznaka()))
                .filter(i -> Objects.equals(i.getBroj(), brojIndeksa))
                .filter(i -> Objects.equals(i.getGodina(), godinaIndeksa))
                .findFirst()
                .orElse(null);

        if (idx == null) return;

        // Forsiraj samofinansiranje da ima uplate (bez obzira šta je random dao)
        idx.setNacinFinansiranja("Samofinansiranje");
        studentIndeksRepository.save(idx);

        // Dodatne školske godine da ima istoriju
        SkolskaGodina sgPrev1 = createSkolskaGodina("2022/2023.", false);
        SkolskaGodina sgPrev2 = createSkolskaGodina("2023/2024.", false);

        // Upisi / obnove (namerno “šareno”)
        UpisGodine upis1 = new UpisGodine();
        upis1.setStudentIndeks(idx);
        upis1.setSkolskaGodina(sgPrev1);
        upis1.setGodinaStudija(1);
        upis1.setDatum(LocalDate.of(2022, 10, 1));
        upis1.setNapomena("Test: upis 1. godine (2022/2023)");
        upis1 = upisGodineRepository.save(upis1);

        ObnovaGodine obnova1 = new ObnovaGodine();
        obnova1.setStudentIndeks(idx);
        obnova1.setSkolskaGodina(sgPrev2);
        obnova1.setGodinaStudija(1);
        obnova1.setDatum(LocalDate.of(2023, 10, 1));
        obnova1.setNapomena("Test: obnova 1. godine (2023/2024)");
        obnova1 = obnovaGodineRepository.save(obnova1);

        UpisGodine upis2 = new UpisGodine();
        upis2.setStudentIndeks(idx);
        upis2.setSkolskaGodina(aktivnaSg);
        upis2.setGodinaStudija(2);
        upis2.setDatum(LocalDate.of(2024, 10, 5));
        upis2.setNapomena("Test: upis 2. godine (2024/2025)");
        upis2 = upisGodineRepository.save(upis2);

        // Više uplata (različiti datumi) — super za filtere/izveštaje
        Uplata u1 = makeUplata(upis2, 900.0, eurRate);
        u1.setDatum(LocalDate.of(2024, 10, 10));
        uplataRepository.save(u1);

        Uplata u2 = makeUplata(upis2, 700.0, eurRate);
        u2.setDatum(LocalDate.of(2024, 11, 20));
        uplataRepository.save(u2);

        Uplata u3 = makeUplata(upis2, 500.0, eurRate);
        u3.setDatum(LocalDate.of(2025, 1, 15));
        uplataRepository.save(u3);

        // Tok studija (ako ti entitet očekuje oba seta)
        TokStudija tok = new TokStudija();
        tok.setStudentIndeks(idx);
        tok.setUpisi(new HashSet<>(Arrays.asList(upis1, upis2)));
        tok.setObnove(new HashSet<>(Collections.singletonList(obnova1)));
        tokStudijaRepositorySafeSave(tok);

        // Osiguraj da sluša sve predmete svog programa u aktivnoj godini
        StudijskiProgram prog = idx.getStudijskiProgram();

        List<Grupa> progGrupe = grupe.stream()
                .filter(g -> g.getStudijskiProgram().equals(prog))
                .filter(g -> g.getSkolskaGodina().equals(aktivnaSg))
                .collect(Collectors.toList());

        Grupa grupa = progGrupe.isEmpty() ? grupe.get(0) : progGrupe.get(0);

        List<DrziPredmet> dpProg = drziPredmeti.stream()
                .filter(dp -> dp.getSkolskaGodina().equals(aktivnaSg))
                .filter(dp -> dp.getPredmet().getStudProgram().equals(prog))
                .collect(Collectors.toList());

        // postojeće što već ima
        Set<Long> vecSlusa = StreamSupport
                .stream(slusaPredmetRepository.findAll().spliterator(), false)
                .filter(sp -> sp.getStudentIndeks().equals(idx))
                .filter(sp -> sp.getSkolskaGodina().equals(aktivnaSg))
                .map(sp -> sp.getDrziPredmet().getPredmet().getId())
                .collect(Collectors.toSet());

        for (DrziPredmet dp : dpProg) {
            Long predmetId = dp.getPredmet().getId();
            if (vecSlusa.contains(predmetId)) continue;

            SlusaPredmet sp = new SlusaPredmet();
            sp.setStudentIndeks(idx);
            sp.setDrziPredmet(dp);
            sp.setSkolskaGodina(aktivnaSg);
            sp.setGrupa(grupa);
            slusaPredmetRepository.save(sp);
        }

        // Ispitni scenariji: položio, pao, poništen, prijavio-nije izašao
        // Uzmi 3-4 ispita iz programa (ako postoje u listi)
        List<Ispit> ispitiPrograma = ispiti.stream()
                .filter(i -> i.getPredmet().getStudProgram().equals(prog))
                .limit(4)
                .collect(Collectors.toList());

        if (ispitiPrograma.size() >= 1) {
            addExamOutcome(idx, ispitiPrograma.get(0), 45, false, true, 9);   // položio
        }
        if (ispitiPrograma.size() >= 2) {
            addExamOutcome(idx, ispitiPrograma.get(1), 10, false, false, null); // pao
        }
        if (ispitiPrograma.size() >= 3) {
            addExamOutcome(idx, ispitiPrograma.get(2), 55, true, false, null); // poništen
        }
        if (ispitiPrograma.size() >= 4) {
            // prijava bez izlaska
            Ispit is = ispitiPrograma.get(3);
            IspitPrijava p = new IspitPrijava();
            p.setIspit(is);
            p.setStudentIndeks(idx);
            p.setDatum(is.getDatumVremePocetka().toLocalDate().minusDays(6));
            ispitPrijavaRepository.save(p);
        }
    }

    private void addExamOutcome(
            StudentIndeks idx,
            Ispit ispit,
            int poeniIspit,
            boolean ponistava,
            boolean polozen,
            Integer ocenaIfPolozen
    ) {
        IspitPrijava prijava = new IspitPrijava();
        prijava.setIspit(ispit);
        prijava.setStudentIndeks(idx);
        prijava.setDatum(ispit.getDatumVremePocetka().toLocalDate().minusDays(7));
        prijava = ispitPrijavaRepository.save(prijava);

        IspitIzlazak izlazak = new IspitIzlazak();
        izlazak.setIspitPrijava(prijava);
        izlazak.setStudentIndeks(idx);
        izlazak.setBrojPoena(poeniIspit);
        izlazak.setPonistava(ponistava);
        izlazak.setNapomena(ponistava ? "Test: poništen" : (polozen ? "Test: položio" : "Test: pao"));
        izlazak = ispitIzlazakRepository.save(izlazak);

        if (polozen && !ponistava) {
            PolozenPredmet pp = new PolozenPredmet();
            pp.setStudentIndeks(idx);
            pp.setPredmet(ispit.getPredmet());
            pp.setOcena(ocenaIfPolozen != null ? ocenaIfPolozen : 6);
            pp.setPriznat(false);
            pp.setIspitIzlazak(izlazak);
            polozenPredmetRepository.save(pp);

            idx.setOstvarenoEspb(idx.getOstvarenoEspb() + ispit.getPredmet().getEspb());
            studentIndeksRepository.save(idx);
        }
    }


}