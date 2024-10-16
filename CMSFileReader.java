package com.main.ecommerceprototype.CMS;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Scanner;

public class CMSFileReader {

    public CMSFileReader(String fileName) {
        try {
            File cmsFile = new File(CMSApp.class.getResource(fileName).toURI());
            Scanner cmsReader = new Scanner(cmsFile);
            ArrayList fxIDarray = new ArrayList<String>();
            while (cmsReader.hasNextLine()) {
                String line = cmsReader.nextLine();
                if (line.contains("fx:id")) {
                    int idSpot = line.indexOf("fx:id");
                    int idSpotAfterSpace = line.indexOf(" ", idSpot + "fx:id=".length());
                    String fxid = line.substring(idSpot + "fx:id=".length(), idSpotAfterSpace).trim();
                    fxIDarray.add(fxid);
                }
            }
            System.out.println(fxIDarray);
            cmsReader.close();
        } catch (FileNotFoundException noFile) {
            System.out.println("File not found");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }


}
