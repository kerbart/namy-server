package com.kerbart.namyserver.rest;

import com.kerbart.namyserver.repositories.LikeRepository;
import com.kerbart.namyserver.repositories.PrenomMetaDataRepository;
import com.kerbart.namyserver.repositories.PrenomRepository;
import com.kerbart.namyserver.services.LikeService;
import com.kerbart.namyserver.services.PrenomService;
import com.kerbart.namyserver.services.UserService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by damien on 12/05/2017.
 */
@RestController
@RequestMapping("/feed")
public class FeedController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeedController.class);

    @Inject
    PrenomMetaDataRepository firstNameRepository;

    @Inject
    ResourceLoader resourceLoader;

    @Inject
    PrenomRepository prenomRepository;

    @Inject
    PrenomService prenomService;

    @Inject
    UserService userSerivce;

    @Inject
    LikeService likeService;

    @Inject
    LikeRepository likeRepository;

    @Value("${database.driver}")
    String databaseDriver;

    @Value("${database.url}")
    String databaseUrl;

    @Value("${database.login}")
    String databaseLogin;

    @Value("${hibernate.dialect}")
    String hibernateDialiect;

    @Value("${hibernate.hbm2ddl.auto}")
    String hibernateHbm2ddlAuto;




    private static String translateOrigine(String origine) {
        switch(origine) {
            case "portugais":
                return "PORTUGAIS";
            case "espagnols":
                return "ESPAGNOL";
            case "anglais":
                return "ANGLAIS";
            case "italiens":
                return "ITALIEN";
            case "allemands":
                return "ALLEMAND";
            case "polonais":
                return "POLONAIS";
            case "africains":
                return "AFRICAIN";
            case "hebraique":
                return "HEBRAIQUE";
            default :
                return origine;
        }

    }



    @ApiOperation("Feed Occurence From CSV base firstname file")
    @GetMapping("/loadfile")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Long> feedOccurence() {
        Long numberInserted = 0L;
        HashMap<String, List<HashMap<String, String>>> tree = new HashMap<>();
        try {
            ClassLoader classLoader = getClass().getClassLoader();


            Resource resource = resourceLoader.getResource("classpath:base-prenoms.csv");
            InputStream is = resource.getInputStream();

            List<String> lines = IOUtils.readLines(is, StandardCharsets.UTF_8);
            lines.remove(0);

            // building map Prenom/List occurences firt...
            for (String line : lines) {
                String[] elements = line.split(";");

                HashMap<String, String> occurences = new HashMap<>();
                occurences.put("femininMasculin", elements[0]);
                occurences.put("anneeStr", elements[2]);
                occurences.put("nombreStr", elements[3].replaceAll(".0000", ""));

                tree.putIfAbsent(elements[1], new ArrayList<HashMap<String, String>>());
                tree.get(elements[1]).add(occurences);
                numberInserted++;
                LOGGER.info(" Cached " + numberInserted + " elements. map size : " + tree.keySet().size());
            }
            // sorting
            SortedSet<String> keys = new TreeSet<String>(tree.keySet());
            // storing
            for (String key : keys) {
                String prenom = key;
                prenomService.addPrenomAndOccurence(prenom, tree.get(prenom));
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return new ResponseEntity<Long>(numberInserted, HttpStatus.OK);
    }

    @ApiOperation("Grab external informations on firstnames")
    @PostMapping(value = "/extranal/info", produces = "application/json")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> readExternalDep() throws IOException {
        // france
        Document webPage = null;
        Elements prenoms = null;
        String[] alphabet = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

        webPage = Jsoup.connect("http://www.signification-prenom.net/prenom_francais.htm").get();
        prenoms = webPage.select(".style22 a");
        for (Element prenom : prenoms) {
            LOGGER.info("Prenom trouvé (Français) : " + prenom.text());
            prenomService.addPrenomOrigine(StringUtils.stripAccents(prenom.text()).toUpperCase(),"FRANCE", "FRANCE");
        }

        webPage = Jsoup.connect("http://www.signification-prenom.net/prenom_basque.htm").get();
        prenoms = webPage.select(".style22 a");
        for (Element prenom : prenoms) {
            LOGGER.info("Prenom trouvé (Basque) : " + prenom.text());
            prenomService.addPrenomOrigine(StringUtils.stripAccents(prenom.text()).toUpperCase(),"FRANCE", "BASQUE");
        }

        webPage = Jsoup.connect("http://www.signification-prenom.net/prenom_breton.htm").get();
        prenoms = webPage.select(".style22 a");
        for (Element prenom : prenoms) {
            LOGGER.info("Prenom trouvé (Breton) : " + prenom.text());
            prenomService.addPrenomOrigine(StringUtils.stripAccents(prenom.text()).toUpperCase(),"FRANCE", "BRETON");
        }

        webPage = Jsoup.connect("http://www.signification-prenom.net/prenom_catalan.htm").get();
        prenoms = webPage.select(".style22 a");
        for (Element prenom : prenoms) {
            LOGGER.info("Prenom trouvé (Catalan) : " + prenom.text());
            prenomService.addPrenomOrigine(StringUtils.stripAccents(prenom.text()).toUpperCase(),"FRANCE", "CATALAN");
        }

        webPage = Jsoup.connect("http://www.signification-prenom.net/prenom_provence.htm").get();
        prenoms = webPage.select(".style22 a");
        for (Element prenom : prenoms) {
            LOGGER.info("Prenom trouvé (Provence) : " + prenom.text());
            prenomService.addPrenomOrigine(StringUtils.stripAccents(prenom.text()).toUpperCase(),"FRANCE", "PROVENCE");
        }

        webPage = Jsoup.connect("http://www.signification-prenom.net/prenom_classique.htm").get();
        prenoms = webPage.select(".style22 a");
        for (Element prenom : prenoms) {
            LOGGER.info("Prenom trouvé (Classique) : " + prenom.text());
            prenomService.addPrenomOrigine(StringUtils.stripAccents(prenom.text()).toUpperCase(),"FRANCE", "CLASSIQUE");
        }

        webPage = Jsoup.connect("http://www.signification-prenom.net/prenom_rares.htm").get();
        prenoms = webPage.select(".style22 a");
        for (Element prenom : prenoms) {
            LOGGER.info("Prenom trouvé (Classique) : " + prenom.text());
            prenomService.addPrenomOrigine(StringUtils.stripAccents(prenom.text()).toUpperCase(),"FRANCE", "RARE");
        }


        for (String letter : alphabet) {
            String url = "http://www.signification-prenom.net/prenom-medievaux/prenom_lettre__THE_LETTER_.htm".replaceAll("_THE_LETTER_", letter);
            // find all french firstname and tag them
            Document docPORT = Jsoup.connect(url).get();
            Elements prenomsPORT = docPORT.select(".style22 a");
            for (Element prenom : prenomsPORT) {
                LOGGER.info("Prenom trouvé (medieval) : " + prenom.text());
                prenomService.addPrenomOrigine(StringUtils.stripAccents(prenom.text()).toUpperCase(),"FRANCE", "MEDIEVAL");
            }
        }

        // arabe
        Document docRB = Jsoup.connect("http://www.signification-prenom.net/prenom_arabe.htm").get();
        Elements prenomsRB = docRB.select(".style22 a");
        for (Element prenom : prenomsRB) {
            LOGGER.info("Prenom trouvé (Arabe) : " + prenom.text());
            prenomService.addPrenomOrigine(StringUtils.stripAccents(prenom.text()).toUpperCase(), "ARABE", "");
        }

        // berber
        Document docBERBER = Jsoup.connect("http://www.signification-prenom.net/prenom-berbere.htm").get();
        Elements prenomsBERBER = docBERBER.select(".style22 a");
        for (Element prenom : prenomsBERBER) {
            LOGGER.info("Prenom trouvé (berber) : " + prenom.text());
            prenomService.addPrenomOrigine(StringUtils.stripAccents(prenom.text()).toUpperCase(), "BERBER", "");
        }



        String[] origines = {"portugais", "espagnols", "anglais", "italiens", "allemands", "polonais", "africains", "hebraique"};

        for (String pays : origines) {
            for (String letter : alphabet) {
                String url = "http://www.signification-prenom.net/prenom-__THE_PAYS__/prenom_lettre__THE_LETTER_.htm".replaceAll("_THE_LETTER_", letter).replaceAll("__THE_PAYS__", pays);
                // find all french firstname and tag them
                Document docPORT = Jsoup.connect(url).get();
                Elements prenomsPORT = docPORT.select(".style22 a");
                for (Element prenom : prenomsPORT) {
                    LOGGER.info("Prenom trouvé (" + pays+ ") : " + prenom.text());
                    prenomService.addPrenomOrigine(StringUtils.stripAccents(prenom.text()).toUpperCase(),  translateOrigine(pays), "");

                }
            }
        }



        return new ResponseEntity<String>("ok", HttpStatus.OK);
    }
}
