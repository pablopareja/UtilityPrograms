/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.era7.bioinfo.util.fastautil;

import com.era7.lib.bioinfo.bioinfoutil.Executable;
import com.era7.lib.bioinfo.bioinfoutil.fasta.FastaUtil;
import com.era7.lib.bioinfo.bioinfoutil.seq.SeqUtil;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class LostInTransalation implements Executable{
    
    @Override
    public void execute(ArrayList<String> array) {
        String[] args = new String[array.size()];
        for (int i = 0; i < array.size(); i++) {
            args[i] = array.get(i);
        }
        main(args);
    }

    public static void main(String[] args) {

        if (args.length != 4) {
            System.out.println("This program expects the following parameters:\n"
                    + "1. Input FASTA file \n"
                    + "2. Input TSV file \n"
                    + "3. Output FASTA file name\n"
                    + "4. Genetic code file");
        } else {

            String inFastaFileSt = args[0];
            String inTSVFileSt = args[1];
            String outFileSt = args[2];
            String geneticCodeFileSt = args[3];

            File inFastaFile = new File(inFastaFileSt);
            File inTSVFile = new File(inTSVFileSt);
            File outFile = new File(outFileSt);
            File geneticCodeFile = new File(geneticCodeFileSt);

            HashMap<String, Integer> positionsMap = new HashMap<String, Integer>();

            BufferedReader reader;
            try {
                reader = new BufferedReader(new FileReader(inTSVFile));
                String line;
                System.out.println("Reading TSV file...");
                while ((line = reader.readLine()) != null) {
                    String[] columns = line.split("\t");
                    positionsMap.put(columns[0], Integer.parseInt(columns[1]));
                }
                reader.close();
                System.out.println("Done!");

                BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));

                System.out.println("Reading FASTA file...");
                
                boolean firstSeq = true;
                String lastHeader = "";
                int startPosition = -1;
                
                StringBuilder seqStBuilder = new StringBuilder();
                reader = new BufferedReader(new FileReader(inFastaFile));
                
                while ((line = reader.readLine()) != null) {
                    
                    if (line.startsWith(">")) {        
                        
                        if (!firstSeq) {
                            writer.write(lastHeader + "\n");
                            writer.write(FastaUtil.formatSequenceWithFastaFormat(SeqUtil.translateDNAtoProtein(seqStBuilder.toString().substring(startPosition), geneticCodeFile), 60));
                            seqStBuilder.delete(0, seqStBuilder.length());
                        }
                        
                        lastHeader = line;
                        
                        for (String key : positionsMap.keySet()) {
                            if (line.substring(1).indexOf(key) >= 0) {
                                startPosition = positionsMap.get(key) - 1;
                                break;
                            }
                        }
                        
                        firstSeq = false;
                        
                    } else {
                        seqStBuilder.append(line);
                    }
                }
                
                reader.close();

                //---last seq---
                writer.write(lastHeader + "\n");
                writer.write(FastaUtil.formatSequenceWithFastaFormat(SeqUtil.translateDNAtoProtein(seqStBuilder.toString().substring(startPosition), geneticCodeFile), 60));
                seqStBuilder.delete(0, seqStBuilder.length());

                writer.close();

                System.out.println("Output file created! ;)");

            } catch (Exception ex) {
                Logger.getLogger(LostInTransalation.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }
}
