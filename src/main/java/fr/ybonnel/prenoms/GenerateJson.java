/*
 * Copyright 2013- Yan Bonnel
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.ybonnel.prenoms;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.gson.GsonBuilder;
import fr.ybonnel.csvengine.CsvEngine;
import fr.ybonnel.csvengine.exception.CsvErrorsExceededException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;

import static com.google.common.collect.Lists.newArrayList;

public class GenerateJson {

    public static String unAccent(String s) {
        //
        // JDK1.5
        //   use sun.text.Normalizer.normalize(s, Normalizer.DECOMP, 0);
        //
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("");
    }

    public static void main(String[] args) throws CsvErrorsExceededException, IOException {

        CsvEngine engine = new CsvEngine(PrenomCsv.class, PrenomTxt.class);


        Multimap<String, PrenomTxt> prenomstxt = Multimaps.index(engine.parseInputStream(GenerateJson.class.getResourceAsStream("/Prenoms.txt"), PrenomTxt.class).getObjects(), new Function<PrenomTxt, String>() {
            @Override
            public String apply(PrenomTxt prenom) {
                return unAccent(prenom.prenom).toUpperCase();
            }
        });

        Multimap<String, PrenomCsv> prenomscsv = Multimaps.index(
                engine.parseInputStream(GenerateJson.class.getResourceAsStream("/prenoms.csv"), PrenomCsv.class).getObjects(),
                new Function<PrenomCsv, String>() {
                    @Override
                    public String apply(PrenomCsv prenomCsv) {
                        return unAccent(prenomCsv.prenom).toUpperCase();
                    }
                });

        List<PrenomJson> prenomJsons = newArrayList();

        for (String prenomKey : prenomstxt.keySet()) {

            for (PrenomTxt prenomTxt : prenomstxt.get(prenomKey)) {
                PrenomJson prenomJson = new PrenomJson();
                prenomJson.prenom = ("" + prenomTxt.prenom.charAt(0)).toUpperCase() + prenomTxt.prenom.substring(1).toLowerCase();
                if (prenomTxt.genre.equals("m")) {
                    prenomJson.sexe = Sexe.GARCON;
                } else if (prenomTxt.genre.equals("f")) {
                    prenomJson.sexe = Sexe.FILLE;
                } else {
                    prenomJson.sexe = Sexe.MIXTE;
                }
                prenomJson.languages = prenomTxt.langages;
                for (PrenomCsv prenomCsv : prenomscsv.get(prenomKey)) {
                    if (prenomJson.naissancesByYear == null) {
                        prenomJson.naissancesByYear = new HashMap<Integer, Integer>();
                    }
                    if (!prenomJson.naissancesByYear.containsKey(prenomCsv.annee)) {
                        prenomJson.naissancesByYear.put(prenomCsv.annee, prenomCsv.quantite);
                    } else {
                        prenomJson.naissancesByYear.put(prenomCsv.annee, prenomCsv.quantite + prenomJson.naissancesByYear.get(prenomCsv.annee));
                    }
                }
                prenomJsons.add(prenomJson);
            }
        }

        Collections.sort(prenomJsons, new Comparator<PrenomJson>() {
            @Override
            public int compare(PrenomJson o1, PrenomJson o2) {
                return o1.prenom.compareTo(o2.prenom);
            }
        });

        FileWriter fileWriter = new FileWriter(new File("D:/prenoms.json"));

        new GsonBuilder().setPrettyPrinting().create().toJson(prenomJsons, fileWriter);


        fileWriter = new FileWriter(new File("D:/prenoms.min.json"));

        new GsonBuilder().create().toJson(prenomJsons, fileWriter);


    }

}
