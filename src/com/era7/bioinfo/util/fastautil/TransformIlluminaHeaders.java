/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.era7.bioinfo.util.fastautil;

import com.era7.lib.bioinfo.bioinfoutil.Executable;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class TransformIlluminaHeaders implements Executable{
    
    
    @Override
    public void execute(ArrayList<String> array) {
        main(array.toArray(new String[0]));
    }
    
    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("This program expects the following parameters: \n"
                    + "1. Input fasta filename \n"
                    + "2. Output resulting fasta filename");
        } else {
            
            File inputFile = new File(args[0]);
            File outputFile = new File(args[1]);
                        
            try {
                
                BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
                String line;
                
                System.out.println("Processing files....");
                
                while((line = reader.readLine()) != null){
                    if(line.startsWith(">")){
                        String header = line.substring(1).replaceAll(" ", "_").replaceAll("\\:", "_").replaceAll("-", "_");                        
                        writer.write(">" + header + "\n");
                    }else{
                        writer.write(line + "\n");
                    }
                }
                
                writer.close();
                reader.close();
                
                System.out.println("Done! ;)");
                
            } catch (Exception ex) {
                Logger.getLogger(TransformIlluminaHeaders.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
            
            
        }
        
    }

}
