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
package com.era7.bioinfo.util.blast;

import com.era7.lib.bioinfo.bioinfoutil.Executable;
import com.era7.lib.bioinfoxml.BlastOutput;
import com.era7.lib.bioinfoxml.Hit;
import com.era7.lib.bioinfoxml.Hsp;
import com.era7.lib.bioinfoxml.Iteration;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class Analyze16SData implements Executable {

    public static final String READS_FILE_HEADER = "Read_ID\tOrganism\tE_value\tLength\tIdentity_Abs\tIdentity_Rel\n";
    public static final String QUANTIFICATION_FILE_HEADER = "Organism\tReads_Abs\tReads_Rel\n";
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
        if (args.length != 3) {
            System.out.println("This program expects three parameters: \n"
                    + "1. Input Blast XML file \n"
                    + "2. Output reads annotation file\n"
                    + "3. Output species quantification file\n");
        } else {


            File blastFile = new File(args[0]);
            File readsOutFile = new File(args[1]);
            File speciesOutFile = new File(args[2]);

            try {

                BufferedWriter readsOutBuff = new BufferedWriter(new FileWriter(readsOutFile));
                //writing header
                readsOutBuff.write(READS_FILE_HEADER);

                BufferedWriter quantificationOutBuff = new BufferedWriter(new FileWriter(speciesOutFile));
                //writing header
                quantificationOutBuff.write(QUANTIFICATION_FILE_HEADER);

                BufferedReader inBuff = new BufferedReader(new FileReader(blastFile));
                String line = null;
                StringBuilder stBuilder = new StringBuilder();

                while ((line = inBuff.readLine()) != null) {
                    stBuilder.append(line);
                }

                BlastOutput blastOutput = new BlastOutput(stBuilder.toString());

                //freeing some memory up
                stBuilder.delete(0, stBuilder.length());

                int readsNumber = 0;
                HashMap<String,Integer> readsPerOrganism = new HashMap<String, Integer>();
                HashMap<String,String> organismGenBankIdMap = new HashMap<String, String>();

                System.out.println("Writing reads file...");

                for (Iteration iteration : blastOutput.getBlastOutputIterations()) {

                    ArrayList<Hit> hits = iteration.getIterationHits();
                    if (hits.size() > 0) {

                        Hit hit = hits.get(0);

                        readsNumber++;

                        String readLine = "";
                        readLine += iteration.getQueryDef().split(" ")[0] + SEPARATOR;
                                                
                        String[] hitDefColumns = hit.getHitDef().split("\\|");

                        //System.out.println("hit.getHitDef() = " + hit.getHitDef());

                        String organismSt = hitDefColumns[1].split("\\(T\\)")[0];

                        //---updating reads counter per organism---
                        Integer organismCounter = readsPerOrganism.get(organismSt);
                        if(organismCounter == null){
                            readsPerOrganism.put(organismSt, new Integer(1));
                        }else{
                            readsPerOrganism.put(organismSt, (organismCounter+1));
                        }

                        readLine += organismSt + SEPARATOR;

                        String genBankIdSt = hitDefColumns[3];
                        organismGenBankIdMap.put(organismSt, genBankIdSt);
//                        
//                        readLine += genBankIdSt + SEPARATOR;

                        List<Hsp> hsps = hit.getHitHsps();
                        double minEvalue = 1000000;
                        double identitySum = 0;

                        boolean thereIsOverlapping = false;

                        boolean[] overlappingArray = new boolean[hit.getHitLen()];
                       
                        for (int i=0; i<hsps.size();i++) {

                            Hsp hsp = hsps.get(i);
                            double tempEvalue = hsp.getEvalueDoubleFormat();
                            if(tempEvalue < minEvalue){
                                minEvalue = tempEvalue;
                            }
                            identitySum += Double.parseDouble(hsp.getIdentity());

                            int fromPos, toPos;
                            fromPos = hsp.getQueryFrom();
                            toPos = hsp.getQueryTo();
                            if(fromPos > toPos){
                                int swap = fromPos;
                                fromPos = toPos;
                                toPos = swap;
                            }

                            for(int j = fromPos  - 1; j < toPos; j++){
                                if(overlappingArray[j]){
                                    thereIsOverlapping = true;
                                }
                                overlappingArray[j] = true;
                            }

                        }
                        
                        //--------------evalue-----------                        
                        readLine += minEvalue + SEPARATOR;
                        
                        //----------length--------------
                        readLine += iteration.getQueryLen() + SEPARATOR;
                        
                        //----------identity--------------
                        if(!thereIsOverlapping){
                            
                            //-----absolute-----
                            readLine += identitySum + SEPARATOR;
                            
                            //-----relative------
                            readLine += (identitySum/Double.parseDouble(iteration.getQueryLen()))*100.0;
                            
                            
                        }else{
                            readLine += SEPARATOR;
                            System.out.println("There was overlapping for: " + hit.getHitDef());
                            System.out.println("minEvalue = " + minEvalue);
                        }
                        

                        readsOutBuff.write(readLine + "\n");

                    }

                }

                readsOutBuff.close();
                System.out.println("reads file created successfully :)");

                //closing reads output file
                readsOutBuff.close();

                System.out.println("Writing quantification file....");

                for(String organism : organismGenBankIdMap.keySet()){

                    String quantificationLine = "";
                    quantificationLine += organism + SEPARATOR;
                    //quantificationLine += organismGenBankIdMap.get(organism) + SEPARATOR;
                    quantificationLine += readsPerOrganism.get(organism) + SEPARATOR;
                    quantificationLine += ((readsPerOrganism.get(organism) * 100.0)/readsNumber) + SEPARATOR + "\n";

                    quantificationOutBuff.write(quantificationLine);

                }

                quantificationOutBuff.close();


                System.out.println("Quantification file created successfully :)");

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
