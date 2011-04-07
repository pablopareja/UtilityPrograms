/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.era7.bioinfo.util.fastautil;

import com.era7.lib.bioinfo.bioinfoutil.fasta.FastaUtil;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pablo Pareja Tobes
 */
public class FastaSubSet {

    public static final int SEQUENCE_LINE_LEGTH = 60;

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
                        StringBuffer wholeSeq = new StringBuffer();
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
