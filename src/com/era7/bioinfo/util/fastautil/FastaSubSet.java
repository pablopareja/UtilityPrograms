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
package com.era7.bioinfo.util.fastautil;

import com.era7.lib.bioinfo.bioinfoutil.Executable;
import com.era7.lib.bioinfo.bioinfoutil.fasta.FastaUtil;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pablo Pareja Tobes
 */
public class FastaSubSet implements Executable{

    public static final int SEQUENCE_LINE_LEGTH = 60;
    
    @Override
    public void execute(ArrayList<String> array) {
        String[] args = new String[array.size()];
        for (int i = 0; i < array.size(); i++) {
            args[i] = array.get(i);
        }
        main(args);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        if (args.length != 4) {
            System.out.println("This program expects four parameters: \n"
                    + "1. Input fasta filename \n"
                    + "2. Subset initial position (inclusive) \n"
                    + "3. Subset final position (inclusive) \n"
                    + "4. Output resulting fasta filename");
        } else {

            String inFileString = args[0];
            String outFileString = args[3];

            boolean wrongNumberFormat = false;
            int posInicio = 0;
            int posFin = 0;

            try{
                
                posInicio = Integer.parseInt(args[1]);
                posFin = Integer.parseInt(args[2]);

            }catch(NumberFormatException ex){
                System.out.println("Subset positions do not have a correct number format");
                ex.printStackTrace();
                wrongNumberFormat = true;
            }

            if(!wrongNumberFormat){
                BufferedReader reader = null;
                try {
                    //Resto uno al indice inicial ya que la primera letra de la
                    //secuencia de cara al usuario del programa es la de indice 1
                    posInicio--;
                    //El indice final no se resta porque el indice final del metodo substr() 
                    //es exclusivo

                    File inFile;
                    File outFile;
                    inFile = new File(inFileString);
                    reader = new BufferedReader(new FileReader(inFile));

                    //FASTA file header
                    String cabecera = reader.readLine();
                    if(cabecera == null){
                        System.out.println("The file does not have header :P");
                        System.exit(-1);
                    }else{
                        StringBuilder wholeSeq = new StringBuilder();
                        String lineSeq;
                        while ((lineSeq = reader.readLine()) != null) {
                            wholeSeq.append(lineSeq);
                        }
                        if(posInicio > posFin){
                            System.out.println("Initial position cannot be greater than final position");
                            System.exit(-1);
                        }else{
                            if(posFin > wholeSeq.length()){
                                System.out.println("The final position provided is greather than sequence length");
                            }else{
                                String subSeq = wholeSeq.substring(posInicio,posFin);
                                String nuevaCabecera = cabecera + " begin=" + (posInicio+1) + " end=" + posFin + "\n";

                                outFile = new File(outFileString);
                                FileWriter fileWriter = new FileWriter(outFile);
                                BufferedWriter buffWriter = new BufferedWriter(fileWriter);

                                //Writing the new header
                                buffWriter.write(nuevaCabecera);

                                //writing sequences
                                FastaUtil.writeSequenceToFileInFastaFormat(subSeq, SEQUENCE_LINE_LEGTH, buffWriter);
//                                for(int contador = 0; contador < subSeq.length(); contador+=SEQUENCE_LINE_LEGTH){
//                                    if(contador+SEQUENCE_LINE_LEGTH > subSeq.length()){
//                                        buffWriter.write(subSeq.substring(contador,subSeq.length()) + "\n");
//                                    }else{
//                                        buffWriter.write(subSeq.substring(contador, contador+SEQUENCE_LINE_LEGTH) + "\n");
//                                    }
//                                }

                                buffWriter.close();
                                fileWriter.close();

                                System.out.println("Fasta file created with the name: " + outFileString);
                            }
                        }
                    }

                    
                } catch (IOException ex) {
                    Logger.getLogger(FastaSubSet.class.getName()).log(Level.SEVERE, null, ex);
                }finally {
                    try {
                        reader.close();
                    } catch (IOException ex) {
                        Logger.getLogger(FastaSubSet.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }      

            
        }
    }

}
