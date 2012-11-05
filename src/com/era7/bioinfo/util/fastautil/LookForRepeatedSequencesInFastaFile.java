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
import com.era7.lib.bioinfo.bioinfoutil.fasta.FastaUtil;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class LookForRepeatedSequencesInFastaFile implements Executable {

    public static final int FASTA_SEQ_LINE_LENGTH = 60;

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




        if (args.length != 3) {
            System.out.println("This program expects the following paramaters: \n"
                    + "1. Input FASTA file 1 \n"
                    + "2. Output filtered FASTA file \n"
                    + "3. Output repeated seqs FASTA file\n");
        } else {

            File inFile = new File(args[0]);
            File outFile = new File(args[1]);
            File repSeqsFile = new File(args[2]);

            try {

                BufferedReader reader = new BufferedReader(new FileReader(inFile));
                BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
                BufferedWriter repSeqsWriter = new BufferedWriter(new FileWriter(repSeqsFile));

                Set<String> seqsFound = new HashSet<String>();

                String line;
                StringBuilder seqBuilder = new StringBuilder();
                String lastHeader = "";
                int seqCounter = 0;
                int repeatedSeqsCounter = 0;

                while ((line = reader.readLine()) != null) {
                    if (line.startsWith(">")) {

                        if (seqBuilder.length() > 0) {

                            String seq = seqBuilder.toString().toLowerCase();

                            if (seqsFound.contains(seq)) {

                                System.out.println("WARNING! repeated seq found! :O");

                                repSeqsWriter.write(">" + lastHeader + "\n" + FastaUtil.formatSequenceWithFastaFormat(seqBuilder.toString(), FASTA_SEQ_LINE_LENGTH));

                                repeatedSeqsCounter++;

                            } else {
                                seqsFound.add(seq);
                                writer.write(">" + lastHeader + "\n" + FastaUtil.formatSequenceWithFastaFormat(seqBuilder.toString(), FASTA_SEQ_LINE_LENGTH));
                            }

                        }
                        lastHeader = line.substring(1);
                        seqBuilder.delete(0, seqBuilder.length());

                        seqCounter++;

                        if (seqCounter % 1000 == 0) {
                            System.out.println(seqCounter + " sequences were analyzed ...");
                        }

                    } else {
                        seqBuilder.append(line);
                    }
                }


                reader.close();
                writer.close();
                repSeqsWriter.close();

                System.out.println("Done! :)");
                System.out.println("There were " + repeatedSeqsCounter + " repeated seqs found out of " + seqCounter + " total seqs");

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
