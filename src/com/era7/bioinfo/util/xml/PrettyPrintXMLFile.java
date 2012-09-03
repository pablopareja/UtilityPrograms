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
package com.era7.bioinfo.util.xml;

import com.era7.lib.bioinfo.bioinfoutil.blast.BlastExporter;
import java.io.*;

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
