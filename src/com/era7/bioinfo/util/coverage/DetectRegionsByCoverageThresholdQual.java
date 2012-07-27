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
package com.era7.bioinfo.util.coverage;

import com.era7.lib.bioinfo.bioinfoutil.Executable;
import com.era7.lib.bioinfoxml.ContigXML;
import com.era7.lib.bioinfoxml.Gap;
import com.era7.lib.era7xmlapi.model.XMLElement;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import org.jdom.Element;

/**
 *
 * @author ppareja
 */
public class DetectRegionsByCoverageThresholdQual implements Executable {

    public static void main(String[] args) {
        DetectRegionsByCoverageThresholdQual dac = new DetectRegionsByCoverageThresholdQual();
        dac.run(args);
    }

    @Override
    public void execute(ArrayList<String> array) {
        String[] args = new String[array.size()];
        for (int i = 0; i < array.size(); i++) {
            args[i] = array.get(i);
        }
        run(args);
    }

    public void run(String[] args) {

        if (args.length != 4) {
            System.out.println("This program expects the following parameters: \n"
                    + "1. Input TXT file \n"
                    + "2. Coverage threshold (integer) \n"
                    + "3. Minimum length for gaps (integer) \n"
                    + "4. Output XML file");
        } else {

            File inFile = new File(args[0]);
            File outFile = new File(args[3]);
            int threshold = Integer.parseInt(args[1]);
            int minimumLength = Integer.parseInt(args[2]);

            try {

                BufferedWriter outBuff = new BufferedWriter(new FileWriter(outFile));
                outBuff.write("<contigs>\n");
                //Current date
                XMLElement dateElem = new XMLElement(new Element("date"));
                dateElem.setText(new Date().toString());
                outBuff.write(dateElem.toString());
                //Threshold
                XMLElement thresholdElem = new XMLElement(new Element("threshold"));
                thresholdElem.setText("" + threshold);
                outBuff.write(thresholdElem.toString());
                //fileName
                XMLElement fileElem = new XMLElement(new Element("file_name"));
                fileElem.setText(inFile.getName());
                outBuff.write(fileElem.toString());


                BufferedReader inBuff = new BufferedReader(new FileReader(inFile));
                String line;
                ArrayList<Integer> positions = new ArrayList<Integer>();
                boolean contigParsed = false;
                String lastHeader = null;

                System.out.println("Analyzing contigs...");

                while ((line = inBuff.readLine()) != null) {
                    if (line.charAt(0) == '>') {
                        if (contigParsed) {
                            //I have to check here the positions that are smaller than the threshold
                            ContigXML contig = new ContigXML();
                            contig.setId(lastHeader);
                            boolean gapsFound = analizaContig(contig, positions, threshold, minimumLength);
                            outBuff.write(contig.toString() + "\n");                         

                        }
                        contigParsed = true;
                        lastHeader = line;
                        positions = new ArrayList<Integer>();
                    } else {
                        String[] positionsString = line.split(" ");
                        for (String string : positionsString) {
                            positions.add(Integer.parseInt(string));
                        }
                    }
                }

                //This is the last contig to be analyzed
                if (contigParsed) {
                    ContigXML contig = new ContigXML();
                    contig.setId(lastHeader);
                    boolean gapsFound = analizaContig(contig, positions, threshold, minimumLength);
                    outBuff.write(contig.toString() + "\n");                    

                }

                inBuff.close();
                outBuff.write("</contigs>\n");
                outBuff.close();

                System.out.println("Analysis performed and output file created! :)");


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private boolean analizaContig(ContigXML contig,
            ArrayList<Integer> positions,
            int threshold,
            int minimumLength ){

        boolean gapsFound = false;

        for (int i = 0; i < positions.size(); i++) {
            int value = positions.get(i);
            if (value <= threshold) {
                
                Gap gap = new Gap();
                gap.setStartPosition(i + 1);
                boolean agujeroContinua = true;
                for (int j = i; j < positions.size() && agujeroContinua; j++) {
                    int valueSiguiente = positions.get(j);
                    if (valueSiguiente <= threshold) {
                        agujeroContinua = true;
                    } else {
                        //assigning end position to gap
                        gap.setEndPosition(j);
                        //moving the index so that I don't re-analyze the same positions
                        i = j;
                        agujeroContinua = false;
                    }
                }
                if (agujeroContinua) {
                    //This is the case where the gap reaches the very end
                    i = positions.size();
                    gap.setEndPosition(positions.size());
                }

                if((gap.getEndPosition() - gap.getStartPosition() + 1) >= minimumLength ){
                    gapsFound = true;
                    contig.addGap(gap);
                }                
            }
        }

        return gapsFound;
    }
}
