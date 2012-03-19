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

import java.io.BufferedReader;
import java.io.FileReader;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class DetectPotatos {

    public static void main(String[] args) throws Exception {



        if (args.length != 4) {
            System.out.println("The parameteres for this program are:" + "\n"
                    + "1.- Name of the XML input file" + "\n"
                    + "2.- Number of characters expected" + "\n"
                    + "3.- Character to look for" + "\n"
                    + "4.- Name of the tag in which text the character is searched");
        } else {


            BufferedReader reader = new BufferedReader(new FileReader(args[0]));
            String line = null;

            int counter = 0;
            int numberOfCharacters = Integer.parseInt(args[1]);
            char character = args[2].charAt(0);

            while ((line = reader.readLine()) != null) {
                counter++;
                if (line.trim().startsWith("<" + args[3] + ">")) {
                    
                    char[] array = line.trim().toCharArray();
                    int barsCounter = 0;
                    for (char c : array) {
                        if (c == character) {
                            barsCounter++;
                        }
                    }

                    if (barsCounter != numberOfCharacters) {
                        System.out.println("Line: " + counter);
                    }
                    
                }

            }
            
            reader.close();


        }
    }
}
