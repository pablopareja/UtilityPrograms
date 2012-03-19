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

import com.era7.lib.bioinfo.bioinfoutil.fasta.FastaUtil;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class ToMultifasta {

    public static final int SEQUENCE_LINE_LENGTH = 60;

    public static void main(String[] args){

         if (args.length != 2) {
            System.out.println("This program expects two parameters: \n"
                    + "1. Input folder name with sequence files \n"
                    + "2. Output Multifasta filename\n");
        } else {


             File inFolder = new File(args[0]);
             File outFile = new File(args[1]);

             if(!inFolder.isDirectory()){
                 System.out.println("The folder name provided does not correspond to an actual folder");
             }else{

                BufferedWriter outBuff = null;
                try {

                    outBuff = new BufferedWriter(new FileWriter(outFile));

                    File[] subFiles = inFolder.listFiles();
                    for (File file : subFiles) {
                        if(file.isFile()){
                            BufferedReader reader = new BufferedReader(new FileReader(file));
                            //writing header
                            outBuff.write(">" + file.getName() + "\n");
                            String line = null;
                            StringBuilder seqBuilder = new StringBuilder();
                            while((line = reader.readLine()) != null){
                                seqBuilder.append(line);
                            }
                            String seq = seqBuilder.toString().replaceAll("\\s", "");
                            //writing lines of subsequence
                            FastaUtil.writeSequenceToFileInFastaFormat(seq, SEQUENCE_LINE_LENGTH, outBuff);
//                            for (int counter = 0; counter < seq.length(); counter += SEQUENCE_LINE_LENGTH) {
//                                if (counter + SEQUENCE_LINE_LENGTH > seq.length()) {
//                                    outBuff.write(seq.substring(counter, seq.length()) + "\n");
//                                } else {
//                                    outBuff.write(seq.substring(counter, counter + SEQUENCE_LINE_LENGTH) + "\n");
//                                }
//                            }

                            reader.close();
                        }
                    }

                } catch (IOException ex) {
                    Logger.getLogger(ToMultifasta.class.getName()).log(Level.SEVERE, null, ex);
                } finally {

                    try {
                        outBuff.close();

                        System.out.println("Multifasta file created successfully! :)");

                    } catch (IOException ex) {
                        Logger.getLogger(ToMultifasta.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

             }


        }

    }

}
