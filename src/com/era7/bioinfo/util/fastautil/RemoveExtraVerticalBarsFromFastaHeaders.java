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
import java.io.*;
import java.util.ArrayList;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class RemoveExtraVerticalBarsFromFastaHeaders implements Executable {
    
    @Override
    public void execute(ArrayList<String> array) {
        String[] args = new String[array.size()];
        for (int i = 0; i < array.size(); i++) {
            args[i] = array.get(i);
        }
        try{
            main(args);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception{
        if (args.length != 2) {
            System.out.println("The parameteres for this program are:" + "\n"
                    + "1.- Name of the Fasta input file" + "\n"
                    + "2.- Name of the Fasta output file");
        } else {
            
            
            BufferedReader reader = new BufferedReader(new FileReader(new File(args[0])));
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(args[1])));
            String line;

            System.out.println("Reading file input file....");

            while ((line = reader.readLine()) != null) {
                
                if (line.trim().startsWith(">")) {
                    
                    String result;
                    String[] columns = line.split("\\|");
                    
                    if(columns.length <= 3){
                        result = line;
                    }else{
                        result = columns[0] + "|" + columns[1] + "|" + columns[2] + " ";
                        for(int i=3; i<columns.length; i++){
                            result += columns[i] + " ";
                        }
                        result = result.substring(0, result.length() - 1); //getting rid of the last whitespace
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
