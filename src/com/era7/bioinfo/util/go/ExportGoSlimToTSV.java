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
package com.era7.bioinfo.util.go;

import com.era7.lib.bioinfo.bioinfoutil.Executable;
import com.era7.lib.bioinfoxml.go.GOSlimXML;
import com.era7.lib.bioinfoxml.go.GoTermXML;
import com.era7.lib.bioinfoxml.go.SlimSetXML;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import org.jdom.Element;

/**
 *
 * @author ppareja
 */
public class ExportGoSlimToTSV implements Executable{

    public static final String SEPARATOR = "\t";
    public static String HEADER_1 = "sample_gene_number" + SEPARATOR + 
                        "sample_annotated_gene_number" + SEPARATOR +
                        "go_terms_lost_not_included_in_slim_set" + "\n";
    
    public static String HEADER_2 = "id" + SEPARATOR + 
                        "name" + SEPARATOR + "annotations_count" + SEPARATOR;

    public void execute(ArrayList<String> array) {
        String[] args = new String[array.size()];
        for(int i=0;i<array.size();i++){
            args[i] = array.get(i);
        }
        main(args);
    }

    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("El programa espera un parametro: \n"
                    + "1. Nombre del archivo xml con los datos de GOSlim \n");
        } else {

            String inFileString = args[0];
            String name = inFileString.split("\\.")[0];
            File inFile = new File(inFileString);

            File molFuncFile = new File(name + "MolFunc" + ".txt");
            File cellCompFile = new File(name + "CellComp" + ".txt");
            File bioProcFile = new File(name + "BioProc" + ".txt");

            System.out.println("Patatizando fichero xml...");

            try {

                BufferedWriter molFuncBuff = new BufferedWriter(new FileWriter(molFuncFile));
                BufferedWriter cellCompBuff = new BufferedWriter(new FileWriter(cellCompFile));
                BufferedWriter bioProcBuff = new BufferedWriter(new FileWriter(bioProcFile));


                //Escribo la cabecera 1 en primer lugar
                molFuncBuff.write(HEADER_1);
                cellCompBuff.write(HEADER_1);
                bioProcBuff.write(HEADER_1);        
                

                BufferedReader reader = new BufferedReader(new FileReader(inFile));
                String tempSt;
                StringBuilder stBuilder = new StringBuilder();
                while ((tempSt = reader.readLine()) != null) {
                    stBuilder.append(tempSt);
                }
                //Cerrar archivo de entrada
                reader.close();

                GOSlimXML goSlim = new GOSlimXML(stBuilder.toString());
                stBuilder.delete(0, stBuilder.length());
                
                String secondLine = goSlim.getSampleGeneNumber() + SEPARATOR +
                                goSlim.getSampleAnnotatedGeneNumber() + SEPARATOR +
                                goSlim.getGoTermsLostNotIncludedInSlimSet().size() + "\n";
                
                molFuncBuff.write(secondLine);
                cellCompBuff.write(secondLine);
                bioProcBuff.write(secondLine); 
                
                //Cabecera de la tabla en la tercera linea
                molFuncBuff.write(HEADER_2);
                cellCompBuff.write(HEADER_2);
                bioProcBuff.write(HEADER_2);    

                SlimSetXML slimSet = goSlim.getSlimSet();
                List<Element> gos = slimSet.asJDomElement().getChildren(GoTermXML.TAG_NAME);

                TreeSet<GoTermXML> bioTreeSet = new TreeSet<GoTermXML>();
                TreeSet<GoTermXML> molTreeSet = new TreeSet<GoTermXML>();
                TreeSet<GoTermXML> cellTreeSet = new TreeSet<GoTermXML>();

                for (Element elem : gos) {
                    GoTermXML goTerm = new GoTermXML(elem);
                    if(goTerm.getAspect().equals(GoTermXML.ASPECT_COMPONENT)){
                        cellTreeSet.add(goTerm);
                    }else if(goTerm.getAspect().equals(GoTermXML.ASPECT_FUNCTION)){
                        molTreeSet.add(goTerm);
                    }else if(goTerm.getAspect().equals(GoTermXML.ASPECT_PROCESS)){
                        bioTreeSet.add(goTerm);
                    }
                }

                
                //----------------------BIOLOGICAL PROCESS-----------------------------

                while(bioTreeSet.size() > 0){
                    GoTermXML goTerm = bioTreeSet.pollLast();
                    String line = "\n" + goTerm.getId() + SEPARATOR + goTerm.getGoName() + SEPARATOR
                            + goTerm.getAnnotationsCount();
                                   
                    bioProcBuff.write(line);  
                }

                //----------------------MOLECULAR FUNCTION-----------------------------

                while(molTreeSet.size() > 0){
                    GoTermXML goTerm = molTreeSet.pollLast();
                    String line = "\n" + goTerm.getId() + SEPARATOR + goTerm.getGoName() + SEPARATOR
                            + goTerm.getAnnotationsCount();
                   
                    molFuncBuff.write(line);
                }

                //----------------------CELLULAR FUNCTION-----------------------------

                while(cellTreeSet.size() > 0){
                    GoTermXML goTerm = cellTreeSet.pollLast();
                    String line = "\n" + goTerm.getId() + SEPARATOR + goTerm.getGoName() + SEPARATOR
                            + goTerm.getAnnotationsCount();

                    cellCompBuff.write(line);
                }


                molFuncBuff.close();
                cellCompBuff.close();
                bioProcBuff.close();



            }catch(Exception e){
                e.printStackTrace();
            }

            System.out.println("Ficheros patatizados con exito! :)");

        }
    }

}