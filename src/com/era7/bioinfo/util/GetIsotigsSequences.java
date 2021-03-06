/*
 * Copyright (C) 2010-2012  "Oh no sequences!"
 *
 * This is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.era7.bioinfo.util;

import com.era7.lib.bioinfo.bioinfoutil.Executable;
import com.era7.lib.bioinfo.bioinfoutil.seq.SeqUtil;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class GetIsotigsSequences implements Executable {

    public static final String SEPARATOR = "\t";

    @Override
    public void execute(ArrayList<String> array) {
        String[] args = new String[array.size()];
        for (int i = 0; i < array.size(); i++) {
            args[i] = array.get(i);
        }
        main(args);
    }

    public static void main(String[] args) {
        if (args.length != 5) {
            System.out.println("This program expects five parameters: \n"
                    + "1. Input TXT with uniprot ids\n"
                    + "2. Input Multifasta file with isotigs sequences\n"
                    + "3. Input isotigs annotation file\n"
                    + "4. Complementary inverted sequence for - strand (true/false)\n"
                    + "5. Output filename\n");
        } else {


            File set1File = new File(args[0]);

            File multifastaFile = new File(args[1]);
            File annotationFile = new File(args[2]);

            File outFile = new File(args[4]);

            String line = null;

            try {

                boolean returnComplementaryInverted = Boolean.parseBoolean(args[3]);
                
                System.out.println("returnComplementaryInverted = " + returnComplementaryInverted);

                System.out.println("Reading uniprot ids file...");

                //------------------read set 1 uniprot ids---------------
                HashSet<String> uniprotIds = new HashSet<String>();

                BufferedReader inBuff = new BufferedReader(new FileReader(set1File));
                while ((line = inBuff.readLine()) != null) {
                    uniprotIds.add(line.trim());
                }
                inBuff.close();

                System.out.println("Getting isotigs sequences...");

                //---------------getting isotigs sequences--------------------
                HashMap<String, String> isotigsSequencesMap = new HashMap<String, String>();

                inBuff = new BufferedReader(new FileReader(multifastaFile));

                StringBuilder sequenceStBuilder = null;
                String currentIsotigId = null;

                while ((line = inBuff.readLine()) != null) {
                    if (line.charAt(0) == '>') {

                        if (sequenceStBuilder != null) {
                            //storing isotig sequence
                            isotigsSequencesMap.put(currentIsotigId, sequenceStBuilder.toString());
                        }

                        sequenceStBuilder = new StringBuilder();
                        currentIsotigId = line.substring(1).split("\\|")[0];
                    } else {
                        sequenceStBuilder.append(line);
                    }
                }
                inBuff.close();

                System.out.println("Reading annotation file...");

                //---------getting lines from annotation file and storing them in map------
                HashMap<String, ArrayList<String>> uniprotLinesMap = new HashMap<String, ArrayList<String>>();

                //-----------filling uniprot lines map with uniprot ids-------
                for (String uniprotId : uniprotIds) {
                    ArrayList<String> array = new ArrayList<String>();
                    uniprotLinesMap.put(uniprotId, array);
                }

                inBuff = new BufferedReader(new FileReader(annotationFile));

                //reading the header
                String header = inBuff.readLine();

                while ((line = inBuff.readLine()) != null) {

                    String[] columns = line.split("\t");
                    String uniprotId = columns[4];
                    String isotigId = columns[0];
                    String strand = columns[3];

                    if (uniprotIds.contains(uniprotId)) {

                        String seqSt = isotigsSequencesMap.get(isotigId);
                        
                        //System.out.println("isotigId = " + isotigId);

                        if (returnComplementaryInverted && strand.equals("-")) {                              
                            seqSt = SeqUtil.getComplementaryInverted(seqSt);
                        }

                        uniprotLinesMap.get(uniprotId).add(line + SEPARATOR + seqSt);


                    }
                }
                inBuff.close();


                System.out.println("Writing output file...");

                //-------Now I just have to write the output file----
                BufferedWriter outBuff = new BufferedWriter(new FileWriter(outFile));

                outBuff.write(header + SEPARATOR + "sequence" + "\n");

                for (String key : uniprotLinesMap.keySet()) {

                    ArrayList<String> array = uniprotLinesMap.get(key);
                    for (String lineSt : array) {
                        outBuff.write(lineSt + "\n");
                    }

                }

                outBuff.close();

                System.out.println("File created successfully! :)");


            } catch (Exception ex) {
                Logger.getLogger(GetIsotigsSequences.class.getName()).log(Level.SEVERE, null, ex);
            }


        }

    }
}
