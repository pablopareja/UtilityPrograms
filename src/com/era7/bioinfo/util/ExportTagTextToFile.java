package com.era7.bioinfo.util;

import com.era7.lib.era7xmlapi.model.XMLElement;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class ExportTagTextToFile {

    public static void main(String[] args) throws Exception {



        if (args.length != 3) {
            System.out.println("The parameteres for this program are:" + "\n"
                    + "1.- Name of the XML input file" + "\n"
                    + "2.- Name of the tag\n"
                    + "3.- Output file name");
        } else {


            BufferedReader reader = new BufferedReader(new FileReader(new File(args[0])));
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(args[2])));
            String line = null;

            int counter = 0;

            while ((line = reader.readLine()) != null) {
                counter++;
                if (line.trim().startsWith("<" + args[1] + ">")) {
                    
                    try{
                        XMLElement element = new XMLElement(line);
                        element.getText().split("\\|")[2].split("OS=")[1].split("GN=")[0].trim();
                    }catch(Exception e){
                        System.out.println("line number: " + counter + "\n" + line );
                    }
                    
                }

            }
            
            reader.close();
            writer.close();


        }
    }
}
