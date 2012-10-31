/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.era7.bioinfo.util.fastautil;

import com.era7.lib.bioinfo.bioinfoutil.Executable;
import com.era7.lib.bioinfo.bioinfoutil.fasta.FastaUtil;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class GetRidOfNs implements Executable{

    public static final int SEQUENCE_LINE_LEGTH = 60;
    
    @Override
    public void execute(ArrayList<String> array) {
        String[] args = new String[array.size()];
        for (int i = 0; i < array.size(); i++) {
            args[i] = array.get(i);
        }
        main(args);
    }

    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("This program expects the following parameters: \n"
                    + "1. Input fasta filename \n"
                    + "2. Output resulting fasta filename");
        } else {


            int initialNumberOfReads = 0;
            int numberOfReadsWithNs = 0;
            int newReadsCounter = 1;

            BufferedReader reader;
            try {

                BufferedWriter logWriter = new BufferedWriter(new FileWriter(new File("GetRidOfNsLog.log")));
                BufferedWriter outWriter = new BufferedWriter(new FileWriter(new File(args[1])));

                File inFile = new File(args[0]);
                reader = new BufferedReader(new FileReader(inFile));
                String line;
                String lastID = "";
                StringBuilder stBuilder = new StringBuilder();

                while ((line = reader.readLine()) != null) {

                    if (line.startsWith(">")) {

                        initialNumberOfReads++;


                        if (stBuilder.length() > 0) {
                            List<SequenceTract> seqTracts = splitSequenceByNTracts(stBuilder.toString());
                            if (seqTracts.size() > 1) {
                                numberOfReadsWithNs++;
                            }
                            for (SequenceTract seqTract : seqTracts) {
                                outWriter.write(">" + formatNumber(newReadsCounter) + "|" + lastID + "|" + seqTract.getRelativeStartPosition() + "\n");
                                outWriter.write(FastaUtil.formatSequenceWithFastaFormat(seqTract.getSequence(), SEQUENCE_LINE_LEGTH));
                                newReadsCounter++;
                            }
                            //--freeing up the stbuilder--
                            stBuilder.delete(0, stBuilder.length());
                        }


                        lastID = line.substring(1).trim();

                    } else {
                        stBuilder.append(line);
                    }
                }

                //---------------last read included in the file------------------
                if (stBuilder.length() > 0) {
                    List<SequenceTract> seqTracts = splitSequenceByNTracts(stBuilder.toString());
                    if (seqTracts.size() > 1) {
                        numberOfReadsWithNs++;
                    }
                    for (SequenceTract seqTract : seqTracts) {
                        outWriter.write(">" + formatNumber(newReadsCounter) + "|" + lastID + "|" + seqTract.getRelativeStartPosition() +"\n");
                        outWriter.write(FastaUtil.formatSequenceWithFastaFormat(seqTract.getSequence(), SEQUENCE_LINE_LEGTH));
                        newReadsCounter++;
                    }
                    //--freeing up the stbuilder--
                    stBuilder.delete(0, stBuilder.length());
                }

                reader.close();

                outWriter.close();

                logWriter.write("There were " + initialNumberOfReads + " reads included in the file provided\n");
                logWriter.write("The number of final reads after getting rid of the Ns is: " + newReadsCounter + "\n");
                logWriter.write("There were " + numberOfReadsWithNs + " reads with Ns in the file provided\n");

                logWriter.close();

            } catch (Exception ex) {
                Logger.getLogger(GetRidOfNs.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    public static List<SequenceTract> splitSequenceByNTracts(String sequence) {

        List<SequenceTract> result = new LinkedList<SequenceTract>();

        int currentPos = 0;
        int startPos = 1;
        StringBuilder stBuilder = new StringBuilder();
        boolean updateStartPos = false;

        while (currentPos < sequence.length()) {
            char currentChar = sequence.charAt(currentPos);

            if (currentChar == 'n' || currentChar == 'N') {

                if (stBuilder.length() > 0) {
                    result.add(new SequenceTract(stBuilder.toString(), startPos));
                    stBuilder.delete(0, stBuilder.length());
                }
                
                updateStartPos = true;

            } else {
                stBuilder.append(currentChar);
                
                if(updateStartPos){
                    startPos = currentPos;
                    updateStartPos = false;
                }
            }
            currentPos++;
        }
        
        if(stBuilder.length() > 0){
            result.add(new SequenceTract(stBuilder.toString(), startPos));
        }

        return result;

    }

    private static String formatNumber(int number) {
        String result = "";
        result += String.valueOf(number);
        while (result.length() < 8) {
            result = "0" + result;
        }
        return result;
    }
}