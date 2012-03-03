/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.era7.bioinfo.util;

import com.era7.lib.bioinfo.bioinfoutil.Executable;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class ClusterProfileProgram implements Executable {

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

                //---  Map<PlasmidID,Map<ClusterNumber, Set<Protein>>> ----
                HashMap<String, HashMap<String, Set<String>>> superMap = new HashMap<String, HashMap<String, Set<String>>>();

                HashMap<String, String> plasmidsSignatures = new HashMap<String, String>();

                //-----------plasmids file------------------
                String line = null;
                reader = new BufferedReader(new FileReader(plasmidsFile));
                while ((line = reader.readLine()) != null) {
                    plasmidsSet.add(line.trim());
                }
                reader.close();

                int lineCounter = 1;

                //-----------clusters file---------------------
                reader = new BufferedReader(new FileReader(clustersFile));
                while ((line = reader.readLine()) != null) {
                    String[] protColumns = line.substring(1).split("\t");
                    for (String protColumn : protColumns) {
                        //System.out.println("protColumn = " + protColumn);
                        String[] barColumns = protColumn.split("\\|");
                        String plasmidId = barColumns[0];
                        String protId = protColumn.split("_cdsid_")[1].split("\\|")[0].trim();
                        System.out.println("plasmidId = " + plasmidId);
                        System.out.println("protId = " + protId);

                        HashMap<String, Set<String>> clusterMap = superMap.get(plasmidId);
                        if (clusterMap == null) {
                            clusterMap = new HashMap<String, Set<String>>();
                            superMap.put(plasmidId, clusterMap);
                        }

                        Set<String> proteinSet = clusterMap.get(String.valueOf(lineCounter));
                        if (proteinSet == null) {
                            proteinSet = new HashSet<String>();
                            clusterMap.put(String.valueOf(lineCounter), proteinSet);
                        }
                        proteinSet.add(protId);

                    }
                    lineCounter++;
                }
                reader.close();


                //--------------------table file------------------------
                BufferedWriter outBuff = new BufferedWriter(new FileWriter(tableFile));

                //----writing header----
                outBuff.write("\t");
                for (int i = 0; i < lineCounter - 1; i++) {
                    String clusterSt = "Cluster " + (i + 1);
                    outBuff.write(clusterSt + "\t");
                }
                outBuff.write("Cluster " + lineCounter + "\n");

                for (String plasmidID : plasmidsSet) {
                    
                    System.out.println("plasmidID = " + plasmidID);
                    
                    String plasmidLineSt = plasmidID + "\t";
                    String plasmidSignatureSt = "";
                    
                    for (int i = 0; i < lineCounter; i++) {
                        Set<String> proteins = superMap.get(plasmidID).get(String.valueOf(i+1));
                        if(proteins != null){
                            for (String protein : proteins) {
                                plasmidLineSt += protein + ",";
                            }
                            plasmidLineSt = plasmidLineSt.substring(0, plasmidLineSt.length()-1); //getting rid of the comma
                            plasmidSignatureSt += "1";
                        }else{
                            plasmidSignatureSt += "0";
                        }
                        plasmidLineSt += "\t";
                    }
                    plasmidLineSt = plasmidLineSt.substring(0, plasmidLineSt.length()-1); //getting rid of the last tab
                    
                    outBuff.write(plasmidLineSt + "\n");
                    
                    //storing signature
                    plasmidsSignatures.put(plasmidID, plasmidSignatureSt);
                }

                outBuff.close();


                //--------------------signature file------------------------
                outBuff = new BufferedWriter(new FileWriter(signaturefile));
                //----writing header----
                outBuff.write("\t");
                for (int i = 0; i < lineCounter - 1; i++) {
                    String clusterSt = "Cluster " + (i + 1);
                    outBuff.write(clusterSt + "\t");
                }
                outBuff.write("Cluster " + lineCounter + "\n");
                for (String plasmidID : plasmidsSet) {
                    outBuff.write(plasmidID + "\t" + plasmidsSignatures.get(plasmidID) + "\n");
                }
                outBuff.close();

            } catch (Exception ex) {
                Logger.getLogger(ClusterProfileProgram.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            System.out.println("Done! :)");

        }
    }
}
