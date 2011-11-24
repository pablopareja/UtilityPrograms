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
                    + "1.- Name of the file" + "\n"
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


        }
    }
}
