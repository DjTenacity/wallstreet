package com.yeepbank.android;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

/**
 * Created by WW on 2015/9/28.
 */
public class CreateXML {

    private static String  rootPath = "C:/Users/WW/Desktop/values/values-{0}X{1}/";
    private final static float dw = 320f;
    private final static float dh = 480f;

    private final static String WTemplate = "<dimen name=\"x{0}\">{1}px</dimen>\n";
    private final static String HTemplate = "<dimen name=\"y{0}\">{1}px</dimen>\n";

    public static void main(String[] args){
        makeString(320,480);
        makeString(480,800);
        makeString(480,854);
        makeString(540,960);
        makeString(600,1024);
        makeString(720,1184);
        makeString(720,1196);
        makeString(768,1024);
        makeString(800,1280);
        makeString(1080,1812);
        makeString(1080,1920);
        makeString(1440,2560);
    }

    private static void makeString(int w,int h){
        StringBuffer sbW = new StringBuffer();
        sbW.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        sbW.append("<resources>");
        float cellW = w/dw;
        for(int i = 1; i <= 320; i++){
            sbW.append(WTemplate.replace("{0}",i+"").replace("{1}",cellW*i+""));
        }
        sbW.append("</resources>");

        StringBuffer sbH = new StringBuffer();
        sbH.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        sbH.append("<resources>");
        float cellH = h/dh;
        for(int i = 1; i <= 640; i++){
            sbH.append(HTemplate.replace("{0}",i+"").replace("{1}",cellH*i+""));
        }
        sbH.append("</resources>");

        String path = rootPath.replace("{0}",h+"").replace("{1}",w+"");
        File dirFile = new File(path);
        if(!dirFile.exists()){
            dirFile.mkdirs();
        }

        File layXFile = new File(dirFile,"lay_x.xml");
        File layYFile = new File(dirFile,"lay_y.xml");

        try {
            PrintWriter printWriter = new PrintWriter(new FileOutputStream(layXFile));
            printWriter.print(sbW.toString());
            printWriter.close();
            printWriter = new PrintWriter(new FileOutputStream(layYFile));
            printWriter.print(sbH.toString());
            printWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }
}
