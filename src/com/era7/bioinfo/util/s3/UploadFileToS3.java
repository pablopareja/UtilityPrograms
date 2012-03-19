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
package com.era7.bioinfo.util.s3;

import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.era7.bioinfo.bioinfoaws.util.CredentialsRetriever;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class UploadFileToS3 {

    public static void main(String[] args)  {
        if (args.length != 3) {
            System.out.println("This program expects the following parameters:\n"
                    + "1. Name of file to be uploaded \n"
                    + "2. Bucket name \n"
                    + "3. AWS credentials file name + path (enter '-' for using our super AMIs ;) )\n");
        } else {
            try {
                System.out.println("Creating transfer manager...");

                TransferManager transferManager = null;
                
                if(args[2].equals("-")){
                    transferManager = new TransferManager(CredentialsRetriever.getBasicAWSCredentialsFromOurAMI());
                }else{
                    transferManager = new TransferManager(CredentialsRetriever.getCredentialsFromPropertiesFile(new File(args[2])));
                }
                
                

                File file = new File(args[0]);
                Upload myUpload = transferManager.upload(args[1], file.getName(), file);
                
                while (myUpload.isDone() == false) {
                    System.out.println("Transfer: " + myUpload.getDescription());
                    System.out.println("  - State: " + myUpload.getState());
                    System.out.println("  - Progress: " + myUpload.getProgress().getBytesTransfered());
                    // Do work while we wait for our upload to complete...
                    Thread.sleep(500);
                }
                System.out.println("Done!");

            } catch (InterruptedException ex) {
                Logger.getLogger(UploadFileToS3.class.getName()).log(Level.SEVERE, null, ex);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(UploadFileToS3.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(UploadFileToS3.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
