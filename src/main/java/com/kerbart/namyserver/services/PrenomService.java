package com.kerbart.namyserver.services;

import com.google.common.primitives.Longs;
import com.kerbart.namyserver.model.*;
import com.kerbart.namyserver.repositories.PrenomRepository;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Repository("prenomService")
@Transactional
public class PrenomService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PrenomService.class);

	@Value("${hibernate.jdbc.batch_size:100}")
	private int batchSize;

	final private static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.FRENCH);

	@PersistenceContext
	private EntityManager em;

	@Inject
	PrenomRepository prenomRepository;

	@Inject
	UserService userService;

	public Prenom flagMetaData(Prenom p) {
		p.setMetaDataExists(true);
		em.merge(p);
		return p;
	}

	private static HashMap<String, Date> getDateFromDateTo(PrenomAge ageEnum) {
		HashMap<String, Date> fromTo = new HashMap<>();
		switch (ageEnum) {
			case ANNEE30:
				fromTo.put("from", new DateTime(1925, 1, 1, 0, 0).toDate());
				fromTo.put("to", new DateTime(1940, 1, 1, 0, 0).toDate());
				break;
			case ANNEE50:
				fromTo.put("from", new DateTime(1945, 1, 1, 0, 0).toDate());
				fromTo.put("to", new DateTime(1955, 1, 1, 0, 0).toDate());
				break;
			case ANNEE80:
				fromTo.put("from", new DateTime(1979, 1, 1, 0, 0).toDate());
				fromTo.put("to", new DateTime(1989, 1, 1, 0, 0).toDate());
				break;
			case ANNEE90:
				fromTo.put("from", new DateTime(1980, 1, 1, 0, 0).toDate());
				fromTo.put("to", new DateTime(2000, 1, 1, 0, 0).toDate());
				break;
			case ENVOGUE:
				fromTo.put("from", new DateTime(2010, 1, 1, 0, 0).toDate());
				fromTo.put("to", new Date());
				break;
			case VIEUX:
				fromTo.put("from", new DateTime(1900, 1, 1, 0, 0).toDate());
				fromTo.put("to", new DateTime(1950, 1, 1, 0, 0).toDate());
				break;
			case RECENT:
				fromTo.put("from", new DateTime(1990, 1, 1, 0, 0).toDate());
				fromTo.put("to", new Date());
				break;
			default:
				fromTo.put("from", new Date());
				fromTo.put("to", new Date());
				break;
		}
		return fromTo;


	}

	public List<Prenom> getRandomsPrenoms(Boolean masculin, Boolean feminin, List<String> originesList, PrenomAge ageEnum, String userToken) {

		User user = userService.findUserByToken(userToken);
		if (user == null) {
			return null;
			// TODO throw Exception
		}

		// TODO get origine and age Map, iterate over this map to generate randoms firstnames
		if (originesList.size() == 0) {
			originesList.add("FRANCE");
		}

		String query = "  SELECT DISTINCT prenomoccurence.prenom_id, prenom.prenom, prenom.token, prenom.masculin, prenom.feminin, sum(nombre) as total\n" +
				"    FROM prenomoccurence, prenom, prenomorigine " +
				"    WHERE annee > ?1  and annee < ?2 " +
				"    and prenom.id = prenomoccurence.prenom_id ";

		if (masculin == true && feminin == false) {
			query += "    and prenomoccurence.masculin = true ";
		}
		if (feminin == true && masculin == false) {
			query += "    and prenomoccurence.feminin = true ";

		}
		query += "    and prenomorigine.prenom = prenom.prenomsansaccent " +
				"    and prenomorigine.origine IN (?3) " +
				"    and prenom.id not in (select prenom_id from likes where user_id = ?4) " +
				"    group by prenomoccurence.prenom_id, prenom.prenom, prenom.masculin, prenom.feminin, prenom.token " +
				"    order by total DESC " +
				"    limit ?5 ";

		List<Object> prenomObjects = em.createNativeQuery(query)
				.setParameter(1, getDateFromDateTo(ageEnum).get("from"))
				.setParameter(2, getDateFromDateTo(ageEnum).get("to"))
				.setParameter(3, originesList)
				.setParameter(4, user.getId())
				.setParameter(5, 50)
				.getResultList();

		List<Prenom> prenoms = new ArrayList<>();

		for (Object object : prenomObjects) {
			Object[] result = (Object[]) object;
			Prenom prenom = new Prenom();
			prenom.setId(((BigInteger) result[0]).longValue());
			prenom.setPrenom((String) result[1]);
			prenom.setMasculin((Boolean) result[3]);
			prenom.setFeminin((Boolean) result[4]);
			prenom.setToken((String) result[2]);
			prenom.setTotalOccurences(((BigDecimal) result[5]).longValue());

			prenoms.add(prenom);
		}

		return prenoms;
	}


	public Prenom addMetaData(Prenom prenom, String prenomOrigine, String origine, Double frequence) {
		PrenomMetaData metaData = new PrenomMetaData();
		metaData.setPrenom(prenom);
		metaData.setPrenomOrigin(prenomOrigine);
		metaData.setFrequence(frequence);
		metaData.setOrigine(origine);
		em.persist(metaData);
		return prenom;
	}

	public PrenomOrigine addPrenomOrigine(String prenomSansAccent, String origine, String subOrigine) {
		PrenomOrigine pOrigine = new PrenomOrigine();
		pOrigine.setOrigine(origine);
		pOrigine.setSubOrigine(subOrigine);
		pOrigine.setPrenom(prenomSansAccent);
		em.persist(pOrigine);
		return pOrigine;
	}

	public void addPrenomAndOccurence(String prenomString, List<HashMap<String, String>> occurences) {
		Prenom prenom = new Prenom();
		prenom.setPrenomSansAccent(StringUtils.stripAccents(prenomString).toUpperCase());
		prenom.setPrenom(prenomString);
		prenom.setMasculin(false);
		prenom.setFeminin(false);
		prenom.setTotalOccurences(0L);
		prenom.setTotalOccurencesMasculin(0L);
		prenom.setTotalOccurencesFeminin(0L);
		em.persist(prenom);

		Long bestNombre = 0L;
		for (HashMap<String, String> occurenceMap : occurences) {
			Date dateAnnee = new Date();
			try {
				dateAnnee = DATE_FORMAT.parse(occurenceMap.get("anneeStr") + "-12-31");
			} catch (ParseException e) {
				//dateAnnee = DATE_FORMAT.parse("1965-12-31");
			}
			PrenomOccurence occurence = new PrenomOccurence();
			occurence.setPrenom(prenom);
			occurence.setAnnee(dateAnnee);
			occurence.setNombre(Longs.tryParse(occurenceMap.get("nombreStr")));
			occurence.setMasculin("1".equals(occurenceMap.get("femininMasculin")));
			occurence.setFeminin("2".equals(occurenceMap.get("femininMasculin")));
			em.persist(occurence);
			prenom.setTotalOccurences(prenom.getTotalOccurences() + occurence.getNombre());
			prenom.setTotalOccurencesMasculin(prenom.getTotalOccurencesMasculin() + (occurence.getMasculin() ? occurence.getNombre() : 0L));
			prenom.setTotalOccurencesFeminin(prenom.getTotalOccurencesFeminin() + (occurence.getFeminin() ? occurence.getNombre() : 0L));
			prenom.setMasculin(occurence.getMasculin() || prenom.getMasculin());
			prenom.setFeminin(occurence.getFeminin() || prenom.getFeminin());
			if (occurence.getNombre() > bestNombre) {
				bestNombre = occurence.getNombre();
				prenom.setMeilleurDate(dateAnnee);
			}
		}
		// false positive for gender
		if (prenom.getTotalOccurencesMasculin().doubleValue() / prenom.getTotalOccurences().doubleValue() < 0.05) {
			prenom.setMasculin(false);
		}
		// false positive for gender
		if (prenom.getTotalOccurencesFeminin().doubleValue() / prenom.getTotalOccurences().doubleValue() < 0.05) {
			prenom.setFeminin(false);
		}

		em.merge(prenom);
		LOGGER.info("Everyting inserted for " + prenom.getPrenom());
	}


}
