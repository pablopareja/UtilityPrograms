/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.era7.bioinfo.util.fastautil;

import com.era7.lib.bioinfo.bioinfoutil.Executable;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class ProcessWeirdCharsFromFastaHeaders implements Executable{

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



            BufferedReader reader;
            try {

                BufferedWriter outWriter = new BufferedWriter(new FileWriter(new File(args[1])));

                File inFile = new File(args[0]);
                reader = new BufferedReader(new FileReader(inFile));
                String line;

                while ((line = reader.readLine()) != null) {

                    if (line.startsWith(">")) {

                        line = line.replaceAll("\\|", "_").replaceAll("\\.", "_").replaceAll("/", "_");

                    } 
                    
                    outWriter.write(line + "\n");
                }

                

                reader.close();
                outWriter.close();
                
                System.out.println("Done! ;)");


            } catch (Exception ex) {
                Logger.getLogger(ProcessWeirdCharsFromFastaHeaders.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

}
