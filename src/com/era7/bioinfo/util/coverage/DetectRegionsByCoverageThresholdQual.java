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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
            System.out.println("El programa espera 4 parametros: \n"
                    + "1. Nombre del archivo txt de entrada \n"
                    + "2. Umbral de coverage (numero entero) \n"
                    + "3. Longitud minima para considerar que existe un agujero (numero entero) \n"
                    + "4. Nombre del archivo xml de salida");
        } else {

            File inFile = new File(args[0]);
            File outFile = new File(args[3]);
            int umbral = Integer.parseInt(args[1]);
            int longitudMinima = Integer.parseInt(args[2]);

            try {

                BufferedWriter outBuff = new BufferedWriter(new FileWriter(outFile));
                outBuff.write("<contigs>\n");
                //Fecha actual
                XMLElement dateElem = new XMLElement(new Element("date"));
                dateElem.setText(new Date().toString());
                outBuff.write(dateElem.toString());
                //umbral
                XMLElement thresholdElem = new XMLElement(new Element("threshold"));
                thresholdElem.setText("" + umbral);
                outBuff.write(thresholdElem.toString());
                //fileName
                XMLElement fileElem = new XMLElement(new Element("file_name"));
                fileElem.setText(inFile.getName());
                outBuff.write(fileElem.toString());


                BufferedReader inBuff = new BufferedReader(new FileReader(inFile));
                String line = null;
                ArrayList<Integer> positions = new ArrayList<Integer>();
                boolean contigParsed = false;
                String lastHeader = null;

                System.out.println("Analizando contigs...");

                while ((line = inBuff.readLine()) != null) {
                    if (line.charAt(0) == '>') {
                        if (contigParsed) {
                            //Aqui ahora tengo que ver las posiciones que sean menores que el umbral
                            ContigXML contig = new ContigXML();
                            contig.setId(lastHeader);
                            boolean hayGaps = analizaContig(contig, positions, umbral, longitudMinima);
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

                //Aqui estaria el ultimo de los contigs a analizar
                if (contigParsed) {
                    ContigXML contig = new ContigXML();
                    contig.setId(lastHeader);
                    boolean hayGaps = analizaContig(contig, positions, umbral, longitudMinima);
                    outBuff.write(contig.toString() + "\n");                    

                }

                inBuff.close();
                outBuff.write("</contigs>\n");
                outBuff.close();

                System.out.println("Contigs analizados y fichero creado! :)");


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private boolean analizaContig(ContigXML contig,
            ArrayList<Integer> positions,
            int umbral,
            int longitudMinima) {

        boolean hayGaps = false;

        for (int i = 0; i < positions.size(); i++) {
            int value = positions.get(i);
            if (value <= umbral) {
                
                Gap gap = new Gap();
                gap.setStartPosition(i + 1);
                boolean agujeroContinua = true;
                for (int j = i; j < positions.size() && agujeroContinua; j++) {
                    int valueSiguiente = positions.get(j);
                    if (valueSiguiente <= umbral) {
                        agujeroContinua = true;
                    } else {
                        //le pongo fin al gap que ya no sigue
                        gap.setEndPosition(j);
                        //Adelanto el indice i para no reanalizar posiciones
                        i = j;
                        agujeroContinua = false;
                    }
                }
                if (agujeroContinua) {
                    //Este es el caso en que el agujero llega hasta el final del todo
                    i = positions.size();
                    gap.setEndPosition(positions.size());
                }

                if((gap.getEndPosition() - gap.getStartPosition() + 1) >= longitudMinima ){
                    hayGaps = true;
                    contig.addGap(gap);
                }                
            }
        }

        return hayGaps;
    }
}
