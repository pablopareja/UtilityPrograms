/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
public class DetectRegionsByCoverageThresholdWig implements Executable {

    public static void main(String[] args) {
        DetectRegionsByCoverageThresholdWig dac = new DetectRegionsByCoverageThresholdWig();
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

        if (args.length != 5) {
            System.out.println("This program expects the following parameters: \n"
                    + "1. Input TXT file \n"
                    + "2. Coverage threshold (integer) \n"
                    + "3. Minimum length for gaps (integer) \n"
                    + "4. Output XML file \n"
                    + "5. Number of contigs to be analyzed\n");
        } else {

            File inFile = new File(args[0]);
            File outFile = new File(args[3]);
            int threshold = Integer.parseInt(args[1]);
            int minimumLength = Integer.parseInt(args[2]);

            int numberOfContigsToBeAnalyzed = Integer.parseInt(args[4]);
            int numberOfContigsAnalyzed=  0;

            try {

                BufferedWriter outBuff = new BufferedWriter(new FileWriter(outFile));
                outBuff.write("<contigs>\n");
                //Current date
                XMLElement dateElem = new XMLElement(new Element("date"));
                dateElem.setText(new Date().toString());
                outBuff.write(dateElem.toString());
                //threshold
                XMLElement thresholdElem = new XMLElement(new Element("threshold"));
                thresholdElem.setText("" + threshold);
                outBuff.write(thresholdElem.toString());
                //fileName
                XMLElement fileElem = new XMLElement(new Element("file_name"));
                fileElem.setText(inFile.getName());
                outBuff.write(fileElem.toString());


                BufferedReader inBuff = new BufferedReader(new FileReader(inFile));
                String line = null;
                ArrayList<Integer> positions = new ArrayList<Integer>();
                boolean contigParsed = false;
                String lastHeader = "";

                System.out.println("Analyzing contigs...");

                while ( ((line = inBuff.readLine()) != null) && numberOfContigsAnalyzed < numberOfContigsToBeAnalyzed) {
                    try{
                        int value = Integer.parseInt(line);
                        //If we can get over this this it means that we already are in a coverage line
                        positions.add(value);
                        contigParsed = true;
                        
                    }catch(NumberFormatException ex){
                        //We're at a line with vars
                        if (contigParsed) {

                            System.out.println("lastHeader = " + lastHeader);

                            //Let's look for the positions that are smaller than the threshold
                            ContigXML contig = new ContigXML();
                            contig.setId(lastHeader);
                            boolean gapsFound = analyzeContig(contig, positions, threshold, minimumLength);
                            outBuff.write(contig.toString() + "\n");

                            numberOfContigsAnalyzed++;
                            lastHeader = "";
                            positions = new ArrayList<Integer>();
                            contigParsed = false;
                        }                        
                        lastHeader += line;                        
                    }
                }

                //This would be the last ocntig to be analyzed
                if (contigParsed && numberOfContigsAnalyzed < numberOfContigsToBeAnalyzed) {
                    ContigXML contig = new ContigXML();
                    contig.setId(lastHeader);
                    boolean gapsFound = analyzeContig(contig, positions, threshold, minimumLength);
                    outBuff.write(contig.toString() + "\n");

                }

                inBuff.close();
                outBuff.write("</contigs>\n");
                outBuff.close();

                System.out.println("Contigs analyzed and output file created successfully! :)");


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private boolean analyzeContig(ContigXML contig,
            ArrayList<Integer> positions,
            int threshold,
            int minimumLength) {

        boolean hayGaps = false;

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
                    hayGaps = true;
                    contig.addGap(gap);
                }
            }
        }

        return hayGaps;
    }
}
