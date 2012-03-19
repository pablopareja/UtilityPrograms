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
