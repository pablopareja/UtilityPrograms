/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.era7.bioinfo.util.blast;

import com.era7.lib.bioinfo.bioinfoutil.Executable;
import com.era7.lib.bioinfo.bioinfoutil.uniprot.UniprotProteinRetreiver;
import com.era7.lib.bioinfoxml.BlastOutput;
import com.era7.lib.bioinfoxml.Hit;
import com.era7.lib.bioinfoxml.Hsp;
import com.era7.lib.bioinfoxml.Iteration;
import com.era7.lib.bioinfoxml.PredictedGene;
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

    public static final String READS_FILE_HEADER = "Read_ID\tLength\tOrganism\tGenBank_ID\tE_value\tIdentity\n";
    public static final String QUANTIFICATION_FILE_HEADER = "Organism\tGenBank_ID\tReads_Abs\tReads_Rel\n";
    public static final String SEPARATOR = "\t";

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
                    + "2. Output species quantification file\n");
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
                        readLine += iteration.getQueryLen() + SEPARATOR;
                        
                        String[] hitDefColumns = hit.getHitDef().split("\\|");

                        System.out.println("hit.getHitDef() = " + hit.getHitDef());

                        String organismSt = hitDefColumns[1].split("\\(T\\)")[0];

                        //---updating reads counter per organism---
                        Integer organismCounter = readsPerOrganism.get(organismSt);
                        if(organismCounter == null){
                            readsPerOrganism.put(organismSt, new Integer(1));
                        }else{
                            readsPerOrganism.put(organismSt, (organismCounter+1));
                        }

                        readLine += organismSt;

                        String genBankIdSt = hitDefColumns[3];
                        organismGenBankIdMap.put(organismSt, genBankIdSt);
                        
                        readLine += genBankIdSt;

                        List<Hsp> hsps = hit.getHitHsps();
                        double minEvalue = 1000000;
                        double identitySum = 0;
                        for (Hsp hsp : hsps) {
                            double tempEvalue = hsp.getEvalueDoubleFormat();
                            if(tempEvalue < minEvalue){
                                minEvalue = tempEvalue;
                            }
                            identitySum += Double.parseDouble(hsp.getIdentity());
                        }

                        readLine += minEvalue + SEPARATOR;
                        readLine += identitySum + SEPARATOR;

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
                    quantificationLine += organismGenBankIdMap.get(organism) + SEPARATOR;
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