/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.era7.bioinfo.util;

import com.era7.lib.bioinfo.bioinfoutil.Executable;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class ClusterProfileProgram implements Executable{
    
    public static final String SEPARATOR = "\t";

    @Override
    public void execute(ArrayList<String> array) {
        String[] args = new String[array.size()];
        for (int i = 0; i < array.size(); i++) {
            args[i] = array.get(i);
        }
        main(args);
    }
    
    public static void main(String[] args) {
        if (args.length != 4) {
            System.out.println("This program expects the following parameters: \n"
                    + "1. Input plasmids TXT file\n"
                    + "2. Input clusters TXT file\n"
                    + "3. Output plasmids signature filename\n"
                    + "4. Output table filename\n");
        } else {
            
            BufferedReader reader = null;
            
            try {
                
                File plasmidsFile = new File(args[0]);
                File clustersFile = new File(args[1]);
                File signaturefile = new File(args[2]);
                File tableFile = new File(args[3]);
                
                Set<String> plasmidsSet = new HashSet<String>();
                
                //-----------plasmids file------------------
                String line = null;
                reader = new BufferedReader(new FileReader(plasmidsFile));
                while((line = reader.readLine()) != null){
                    plasmidsSet.add(line.trim());
                }
                reader.close();
                
                //-----------clusters file---------------------
                reader = new BufferedReader(new FileReader(clustersFile));
                while((line = reader.readLine()) != null){
                    
                }                
                reader.close();
                
                
                
                //--------------------signature file------------------------
                BufferedWriter outBuff = new BufferedWriter(new FileWriter(signaturefile));
                
                
                outBuff.close();
                
            } catch (Exception ex) {
                Logger.getLogger(ClusterProfileProgram.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }


            
    
}
