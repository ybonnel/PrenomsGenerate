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

import fr.ybonnel.csvengine.adapter.AdapterInteger;
import fr.ybonnel.csvengine.annotation.CsvColumn;
import fr.ybonnel.csvengine.annotation.CsvFile;

@CsvFile(separator = ",")
public class PrenomCsv {
    //annee,quantite,id,prenom
    @CsvColumn(value = "annee", adapter = AdapterInteger.class)
    public Integer annee;
    @CsvColumn(value = "quantite", adapter = AdapterInteger.class)
    public Integer quantite;
    @CsvColumn(value = "id", adapter = AdapterInteger.class)
    public Integer id;
    @CsvColumn("prenom")
    public String prenom;

    @Override
    public String toString() {
        return "PrenomCsv{" +
                "annee=" + annee +
                ", quantite=" + quantite +
                ", id=" + id +
                ", prenom='" + prenom + '\'' +
                '}';
    }
}
