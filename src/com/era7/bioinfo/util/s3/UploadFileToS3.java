/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
        if (args.length != 2) {
            System.out.println("This program expects the following parameters:\n"
                    + "1. Name of file to be uploaded \n"
                    + "2. Bucket name \n");
        } else {
            try {
                System.out.println("Creating transfer manager...");

                TransferManager transferManager = new TransferManager(CredentialsRetriever.getBasicAWSCredentialsFromOurAMI());

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
