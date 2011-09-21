/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.era7.bioinfo.util;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class DetectNumberOfSequencesInFastQ {
    
    public static void main(String args[]) throws Exception{
        
        int atCounter = 0;
        int linesCounter = 0;
        
        BufferedReader reader = new BufferedReader(new FileReader(args[0]));
        String line = null;
        while((line = reader.readLine()) != null){
            if(line.startsWith("@")){
                atCounter++;
            }
            linesCounter++;
            
            if(linesCounter % 10000 == 0){
                System.out.println("linesCounter = " + linesCounter);
            }
        }
        
        reader.close();
        
        System.out.println("atCounter = " + atCounter);
        System.out.println("linesCounter = " + linesCounter);
        
    }
    
}
