package org.raflab.studsluzba.repositories;

import java.util.List;
import java.util.Optional;

import org.raflab.studsluzba.model.entities.DrziPredmet;
import org.raflab.studsluzba.model.entities.Predmet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DrziPredmetRepository extends JpaRepository<DrziPredmet, Long> {

	@Query("select dp.predmet from DrziPredmet dp " +
			"where dp.nastavnik.id = :idNastavnika " +
			"and dp.skolskaGodina.aktivna = true")
	List<Predmet> getPredmetiZaNastavnikaUAktivnojSkolskojGodini(Long idNastavnika);

	@Query("select dp from DrziPredmet dp where dp.nastavnik.id = :idNastavnik and dp.predmet.id = :idPredmet")
	DrziPredmet getDrziPredmetNastavnikPredmet(Long idPredmet, Long idNastavnik);

    List<DrziPredmet> findBySkolskaGodinaId(Long id);
}
