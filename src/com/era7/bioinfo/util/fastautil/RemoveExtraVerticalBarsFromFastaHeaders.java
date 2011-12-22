/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.era7.bioinfo.util.fastautil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class RemoveExtraVerticalBarsFromFastaHeaders {

    public static void main(String[] args) throws Exception{
        if (args.length != 2) {
            System.out.println("The parameteres for this program are:" + "\n"
                    + "1.- Name of the Fasta input file" + "\n"
                    + "4.- Name of the Fasta output file");
        } else {
            
            
            BufferedReader reader = new BufferedReader(new FileReader(new File(args[0])));
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(args[1])));
            String line = null;

            System.out.println("Reading file input file....");

            while ((line = reader.readLine()) != null) {
                
                if (line.trim().startsWith(">")) {
                    
                    String result = "";
                    String[] columns = line.split("\\|");
                    
                    if(columns.length <= 3){
                        result = line;
                    }else{
                        result = columns[0] + "|" + columns[1] + "|" + columns[2];
                    }
                    
                    writer.write(result + "\n");
                    
                }else{
                    writer.write(line + "\n"); 
                }               

            }
            
            System.out.println("Closing reader and writer...");
            
            writer.close();
            reader.close();
            
            System.out.println("done! ;)");
            
        }
    }
}
