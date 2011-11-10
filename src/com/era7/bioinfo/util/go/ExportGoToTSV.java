/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.era7.bioinfo.util.go;

import com.era7.lib.bioinfo.bioinfoutil.Executable;
import com.era7.lib.bioinfo.bioinfoutil.go.GOExporter;
import com.era7.lib.bioinfoxml.go.GoAnnotationXML;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 *
 * @author ppareja
 */
public class ExportGoToTSV implements Executable{
    

    public void execute(ArrayList<String> array) {
        String[] args = new String[array.size()];
        for(int i=0;i<array.size();i++){
            args[i] = array.get(i);
        }
        main(args);
    }

    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("This program expects one parameter: \n"
                    + "1. GO annotation data XML file name \n");
        } else {

            String inFileString = args[0];
            String name = inFileString.split("\\.")[0];
            File inFile = new File(inFileString);            

            System.out.println("Starting...");

            try {

                
                BufferedReader reader = new BufferedReader(new FileReader(inFile));
                String tempSt;
                StringBuilder stBuilder = new StringBuilder();
                while ((tempSt = reader.readLine()) != null) {
                    stBuilder.append(tempSt);
                }
                //Closing input file
                reader.close();

                GoAnnotationXML goAnnotation = new GoAnnotationXML(stBuilder.toString());
                stBuilder.delete(0, stBuilder.length());

                GOExporter.calculateFrequenciesAndExportToFiles(goAnnotation, name);


            }catch(Exception e){
                e.printStackTrace();
            }

            System.out.println("Finished! :)");

        }
    }

}
