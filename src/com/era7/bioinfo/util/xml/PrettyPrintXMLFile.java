/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.era7.bioinfo.util.xml;

import com.era7.lib.bioinfo.bioinfoutil.blast.BlastExporter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class PrettyPrintXMLFile {

    public static void main(String[] args) {

        if (args.length != 3) {
            System.out.println("This program expects three parameters: \n"
                    + "1. Input XML file \n"
                    + "2. Output XML (pretty printed) file\n"
                    + "3. Indentation amount (integer)\n");
        } else {

            String inFileString = args[0];
            String outFileString = args[1];
            int indentationAmount = Integer.parseInt(args[2]);

            File inFile = new File(inFileString);
            File outFile = new File(outFileString);

            try {

                BufferedWriter outBuff = new BufferedWriter(new FileWriter(outFile));

                System.out.println("Reading input file....");

                BufferedReader reader = new BufferedReader(new FileReader(inFile));
                String tempSt;
                StringBuilder stBuilder = new StringBuilder();
                while ((tempSt = reader.readLine()) != null) {
                    stBuilder.append(tempSt);
                }
                //closing input file reader
                reader.close();
                
                System.out.println("Writing output file...");
                
                outBuff.write(BlastExporter.prettyPrintBlast(stBuilder.toString(), indentationAmount));
                
                outBuff.close();
                
                System.out.println("Done!! ;)");
                

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
