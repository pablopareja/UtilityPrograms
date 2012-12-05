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
package com.era7.bioinfo.util.fastautil;

import com.era7.lib.bioinfo.bioinfoutil.Executable;
import com.era7.lib.bioinfo.bioinfoutil.Pair;
import com.era7.lib.bioinfo.bioinfoutil.fasta.FastaUtil;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class ExtractCommonSequencesFromFastaSets implements Executable {

    public static final String TSV_FILE_HEADER = "RDP_ID\tNT_ID\n";
    public static final int FASTA_LINE_LENGTH = 60;
    private static final Logger logger = Logger.getLogger("ExtractCommonSequencesFromFastaSets");
    private static FileHandler fh;

    @Override
    public void execute(ArrayList<String> array) {
        String[] args = new String[array.size()];
        for (int i = 0; i < array.size(); i++) {
            args[i] = array.get(i);
        }
        main(args);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        if (args.length != 6) {
            System.out.println("This program expects the following paramaters: \n"
                    + "1. Input FASTA file 1 \n"
                    + "2. Input FASTA file 2 \n"
                    + "3. Outupt FASTA file name \n"
                    + "4. Output TSV file name (including Set1 --> Set2 id pairs) \n"
                    + "5. Number of chars for prefix/sufix filter (int) \n"
                    + "6. Statistics file name");
        } else {

            File inFastaFile1 = new File(args[0]);
            File inFastaFile2 = new File(args[1]);
            File outFastaFile = new File(args[2]);
            File outTSVFile = new File(args[3]);
            int numberOfCharsForPrefixSufix = Integer.parseInt(args[4]);
            File outStatsFile = new File(args[5]);

            try {

                fh = new FileHandler("ExtractCommonSequencesFromFastaSets.log", true);
                SimpleFormatter formatter = new SimpleFormatter();
                fh.setFormatter(formatter);
                logger.addHandler(fh);
                logger.setLevel(Level.ALL);

                System.out.println("Initializing buffered writers...");
                BufferedWriter outFastaBuff = new BufferedWriter(new FileWriter(outFastaFile));
                BufferedWriter outTSVBuff = new BufferedWriter(new FileWriter(outTSVFile));
                outTSVBuff.write(TSV_FILE_HEADER);
                System.out.println("done!");

                HashSet<Integer> seqLengthsFoundInFasta1 = new HashSet<Integer>();
                HashSet<String> prefixesFound = new HashSet<String>();
                HashSet<String> sufixesFound = new HashSet<String>();

                System.out.println("Reading fasta input file 1...");

                BufferedReader reader = new BufferedReader(new FileReader(inFastaFile1));
                String line;
                StringBuilder stBuilder = new StringBuilder();
                int seqLengthCounter = 0;
                int lineCounter = 0;

                while ((line = reader.readLine()) != null) {

                    if (line.charAt(0) == '>') {
                        if (seqLengthCounter != 0) {
                            String seq = stBuilder.toString();

                            String prefix = seq.substring(0, numberOfCharsForPrefixSufix).toLowerCase();
                            prefixesFound.add(prefix);

                            String sufix = seq.substring(seq.length() - numberOfCharsForPrefixSufix, seq.length()).toLowerCase();
                            sufixesFound.add(sufix);

                            seqLengthsFoundInFasta1.add(seqLengthCounter);
                            seqLengthCounter = 0;

                            stBuilder.delete(0, seq.length());
                        }
                    } else {
                        seqLengthCounter += line.trim().length();
                        stBuilder.append(line.trim());
                    }

                    lineCounter++;

                    if (lineCounter % 1000000 == 0) {
                        System.out.println(lineCounter + " lines parsed...");
                    }

                }
                seqLengthsFoundInFasta1.add(seqLengthCounter);
                //-------------------------last seq-----------------------------------
                if (stBuilder.length() > 0) {
                    String seq = stBuilder.toString();

                    String prefix = seq.substring(0, numberOfCharsForPrefixSufix).toLowerCase();
                    prefixesFound.add(prefix);

                    String sufix = seq.substring(seq.length() - numberOfCharsForPrefixSufix, seq.length()).toLowerCase();
                    sufixesFound.add(sufix);

                    stBuilder.delete(0, seq.length());
                }
                //-----------------------------------------------------------

                int fasta2DiscardedSeqs = 0;
                int tempFasta2FilteredSetSize = 0;

                File tempOutFile = new File("tempOutFile.txt");
                BufferedWriter tempOutBuff = new BufferedWriter(new FileWriter(tempOutFile));

                System.out.println("Reading fasta set 2....");

                //--------------discarding seqs with different length than fasta 1-----------------
                reader = new BufferedReader(new FileReader(inFastaFile2));
                stBuilder = new StringBuilder();
                lineCounter = 0;
                String lastHeader = "";
                while ((line = reader.readLine()) != null) {

                    if (line.charAt(0) == '>') {
                        if (stBuilder.length() > 0) {

                            if (seqLengthsFoundInFasta1.contains(stBuilder.length())) {

                                boolean passedFilter = false;

                                String seq = stBuilder.toString();

                                if (prefixesFound.contains(seq.substring(0, numberOfCharsForPrefixSufix).toLowerCase())) {

                                    String sufix = seq.substring(seq.length() - numberOfCharsForPrefixSufix, seq.length()).toLowerCase();
                                    
                                    if (sufixesFound.contains(sufix)) {
                                        tempOutBuff.write(lastHeader + "\n" + stBuilder.toString() + "\n");
                                        tempFasta2FilteredSetSize++;
                                        passedFilter = true;
                                        tempFasta2FilteredSetSize++;
                                    }
                                }
                                if (!passedFilter) {
                                    fasta2DiscardedSeqs++;
                                }

                            } else {
                                fasta2DiscardedSeqs++;
                            }

                            stBuilder.delete(0, stBuilder.length());
                        }
                        
                        lastHeader = line;
                        
                    } else {
                        stBuilder.append(line.trim());
                    }

                    lineCounter++;

                    if (lineCounter % 1000000 == 0) {
                        System.out.println(lineCounter + " lines parsed...");
                    }

                }
                reader.close();
                //-------------------------last seq-----------------------------------
                if (stBuilder.length() > 0) {

                    if (seqLengthsFoundInFasta1.contains(stBuilder.length())) {
                        tempOutBuff.write(lastHeader + "\n" + stBuilder.toString() + "\n");
                        tempFasta2FilteredSetSize++;
                    } else {
                        fasta2DiscardedSeqs++;
                    }

                    stBuilder.delete(0, stBuilder.length());
                }
                //---------------------------------------------------------------------
                System.out.println("Done!");
                tempOutBuff.close();

                System.out.println("There were " + fasta2DiscardedSeqs + " sequences discarded after filtering...");
                System.out.println("The filtered set has " + tempFasta2FilteredSetSize + " sequences now...");

                System.out.println("Doing some cleaning...");
                prefixesFound.clear();
                sufixesFound.clear();
                seqLengthsFoundInFasta1.clear();
                System.out.println("Done!");

                System.out.println("Let's look for exact matches now!");


                HashMap<String, LinkedList<Pair<String, String>>> seqMap = new HashMap<String, LinkedList<Pair<String, String>>>();
                System.out.println("Reading filtered set 2....");

                System.out.println("Doing some indexing for the search...");

                //--------------discarding seqs with different length than fasta 1-----------------
                reader = new BufferedReader(new FileReader(tempOutFile));
                stBuilder = new StringBuilder();
                lineCounter = 0;
                String fastaSet2ID = "";
                while ((line = reader.readLine()) != null) {
                    
                    if (line.charAt(0) == '>') {

                        if (stBuilder.length() > 0) {
                                                        
                            String seq = stBuilder.toString();
                            String prefix = seq.substring(0, numberOfCharsForPrefixSufix).toLowerCase();

                            LinkedList<Pair<String, String>> list = seqMap.get(prefix);
                            if (list == null) {
                                list = new LinkedList<Pair<String, String>>();
                                seqMap.put(prefix, list);
                            }
                            //System.out.println("fastaSet2ID = " + fastaSet2ID);
                            //System.out.println("seq = " + seq);
                            
                            list.add(new Pair<String, String>(fastaSet2ID, seq));

                            stBuilder.delete(0, stBuilder.length());
                        }

                        fastaSet2ID = line.trim().substring(1);

                    } else {
                        stBuilder.append(line.trim());
                    }

                    lineCounter++;

                    if (lineCounter % 100000 == 0) {
                        System.out.println(lineCounter + " lines parsed...");
                    }

                }
                reader.close();

                //--------------------last seq---------------------------
                if (stBuilder.length() > 0) {
                    String seq = stBuilder.toString();
                    String prefix = seq.substring(0, numberOfCharsForPrefixSufix).toLowerCase();

                    LinkedList<Pair<String, String>> list = seqMap.get(prefix);
                    if (list == null) {
                        list = new LinkedList<Pair<String, String>>();
                        seqMap.put(prefix, list);
                    }
                    list.add(new Pair<String, String>(fastaSet2ID, seq));

                    stBuilder.delete(0, stBuilder.length());
                }
                //-------------------------------------------------
                System.out.println("Done!");

                int seqsFound = 0;
                int seqsNotFound = 0;

                reader = new BufferedReader(new FileReader(inFastaFile1));
                lineCounter = 0;

                String fastaSet1ID = "";
                String fastaSet1Header = "";

                while ((line = reader.readLine()) != null) {

                    if (line.charAt(0) == '>') {

                        if (stBuilder.length() > 0) {
                            String seq = stBuilder.toString();
                            String prefix = seq.substring(0, numberOfCharsForPrefixSufix).toLowerCase();

                            LinkedList<Pair<String, String>> list = seqMap.get(prefix);
                            if (list == null) {
                                seqsNotFound++;
                            } else {
                                boolean found = false;
                                for (int i = 0; i < list.size(); i++) {

                                    fastaSet2ID = list.get(i).getValue1();
                                    String fastaSet2Seq = list.get(i).getValue2();
                                    
                                    if (seq.toLowerCase().equals(list.get(i).getValue2().toLowerCase())) {
                                        found = true;
                                        seqsFound++;


                                        outTSVBuff.write(fastaSet1Header.replaceAll("\t", "") + "\t" + fastaSet2ID.replaceAll("\t", "") + "\n");
                                        outFastaBuff.write(">" + fastaSet2ID + "|" + fastaSet1ID + "\n" + FastaUtil.formatSequenceWithFastaFormat(fastaSet2Seq, FASTA_LINE_LENGTH));

                                    }
                                }
                                if (!found) {
                                    seqsNotFound++;
                                }
                            }

                            stBuilder.delete(0, stBuilder.length());
                        }

                        fastaSet1ID = line.substring(1).trim().split(" ")[0];
                        fastaSet1Header = line.substring(1);

                    } else {
                        stBuilder.append(line);
                    }

                    lineCounter++;

                    if (lineCounter % 1000 == 0) {
                        System.out.println(lineCounter + " lines parsed...");
                        System.out.println("seqsNotFound = " + seqsNotFound);
                        System.out.println("seqsFound = " + seqsFound);
                    }

                }

                //---------------last seq--------------------------
                if (stBuilder.length() > 0) {
                    String seq = stBuilder.toString();
                    String prefix = seq.substring(0, numberOfCharsForPrefixSufix).toLowerCase();

                    LinkedList<Pair<String, String>> list = seqMap.get(prefix);
                    if (list == null) {
                        seqsNotFound++;
                    } else {
                        boolean found = false;
                        for (int i = 0; i < list.size(); i++) {
                            if (seq.toLowerCase().equals(list.get(i).getValue2().toLowerCase())) {
                                found = true;
                                seqsFound++;

                                fastaSet2ID = list.get(i).getValue1();
                                String fastaSet2Seq = list.get(i).getValue2();

                                outTSVBuff.write(fastaSet1ID + "\t" + fastaSet2ID + "\n");
                                outFastaBuff.write(">" + fastaSet2ID + "|" + fastaSet1ID + "\n" + FastaUtil.formatSequenceWithFastaFormat(fastaSet2Seq, FASTA_LINE_LENGTH));

                            }
                        }
                        if (!found) {
                            seqsNotFound++;
                        }
                    }

                    stBuilder.delete(0, stBuilder.length());
                }
                //--------------------------------------------------

                System.out.println("Closing output files...");
                outFastaBuff.close();
                outTSVBuff.close();
                System.out.println("Done!");

                System.out.println("Deleting temp out file...");
                tempOutFile.delete();
                System.out.println("Done!");

                System.out.println("Writing stats file...");
                BufferedWriter statsBuff = new BufferedWriter(new FileWriter(outStatsFile));
                statsBuff.write("There were " + seqsFound + " sequences found in set 2... :)\n");
                statsBuff.write(seqsNotFound + " were NOT found in set 2... :(\n");
                statsBuff.close();
                System.out.println("Done!");


                System.out.println("The program finished!");
                System.out.println("There were " + seqsFound + " sequences found in set 2... :)");
                System.out.println(seqsNotFound + " were NOT found in set 2... :(");


            } catch (Exception ex) {
                ex.printStackTrace();
                logger.log(Level.SEVERE, ex.getMessage());
                StackTraceElement[] trace = ex.getStackTrace();
                for (StackTraceElement stackTraceElement : trace) {
                    logger.log(Level.SEVERE, stackTraceElement.toString());
                }
            }

            fh.close();
        }
    }
}
