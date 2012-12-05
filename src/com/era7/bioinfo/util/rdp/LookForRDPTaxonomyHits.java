/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.era7.bioinfo.util.rdp;

import com.era7.bioinfo.bio4j.model.nodes.OrganismNode;
import com.era7.bioinfo.bio4j.model.util.Bio4jManager;
import com.era7.bioinfo.bio4j.model.util.NodeRetriever;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class LookForRDPTaxonomyHits {
    
    public static void main(String[] args){
        
        if(args.length != 3){
            System.out.println("This program expects the following parameters:\n"+
                    "1. Bio4j DB folder\n" +
                    "2. RDP fasta file\n" +
                    "3. Output stats TXT file");
        }else{
            
            BufferedReader reader = null;
            
            try {
                
                Bio4jManager manager = new Bio4jManager(args[0]);
                NodeRetriever nodeRetriever = new NodeRetriever(manager);
                File rdpFile = new File(args[1]);
                File outFile = new File(args[2]);
                
                int seqCounter = 0;
                int seqsFound = 0;
                
                BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
                
                reader = new BufferedReader(new FileReader(rdpFile));
                String line;
                while((line = reader.readLine()) != null){
                    if(line.startsWith(">")){
                        
                        String[] columns = line.substring(1).split(";");                        
                        String organismSt = columns[columns.length - 1].trim();
                        if(organismSt.trim().equals("genus")){
                            organismSt = columns[columns.length - 2].trim();
                        }
                        
                        
                        OrganismNode organismNode = nodeRetriever.getOrganismByScientificName(organismSt);
                        if(organismNode != null){
                            seqsFound++;
                        }
                        
                        seqCounter++;
                        
                        if(seqCounter % 1000 == 0){
                            System.out.println("last organismSt = " + organismSt);
                            System.out.println(seqCounter + " sequences analyzed already... (" + seqsFound + " seqs found)");
                        }
                    }
                }
                
                reader.close();
                
                System.out.println("Closing output file...");
                writer.close();
                System.out.println("Done! :)");
                
                
            } catch (Exception ex) {
                Logger.getLogger(LookForRDPTaxonomyHits.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
    }
    
}
