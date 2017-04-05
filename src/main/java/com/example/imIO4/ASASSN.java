package com.example.imIO4;
/*
 * imIO4
 * Created by Aditya Gholba on 23/3/17.
 * Read image from byte[] using readMat(byte[] byteImage)
 * Write image from Mat using writeMat(Mat destination)
 * Write IP logic in ovcFunc()
 *
 */

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgproc.Imgproc.rectangle;


public class ASASSN extends ToolKit
{
    public String getSoPath()
    {
        return soPath;
    }

    public void setSoPath(String soPath)
    {
        this.soPath = soPath;
    }

    protected static String soPath ="/home/aditya/opencv-3.2.0/build/lib/libopencv_java320.so";
    protected static final Logger LOG = LoggerFactory.getLogger(ASASSN.class);
    protected int matches=0;
    protected int notMatch=0;
    protected int dense=0;
    //static {System.load(soPath);}
    protected int bufferedImageType;
    protected ArrayList<Mat> referenceList = new ArrayList<>();
    protected ArrayList<Mat> templateList = new ArrayList<>();
    protected String refPath;
    public String getRefPath()
    {
        return refPath;
    }

    public void setRefPath(String refPath)
    {
        this.refPath = refPath;
    }


    protected Mat readMat(byte[] bytesImage)
    {
        System.load(soPath);
        InputStream src = new ByteArrayInputStream(bytesImage);
        BufferedImage bufferedImage=null;
        try
        {
            bufferedImage = ImageIO.read(src);
            bufferedImageType = bufferedImage.getType();
        }
        catch (Exception e){System.out.print(e.getMessage());}
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try{ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);}catch (Exception e){System.out.print(e.getMessage());}
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return  Imgcodecs.imdecode(new MatOfByte(bytes), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);

    }

    private void writeMat(Mat destination,Data data2)
    {
        System.load(soPath);
        BufferedImage bufferedImage1 = new BufferedImage(destination.width(),destination.height(),bufferedImageType);
        byte[] data = new byte[ ((int) destination.total() * destination.channels()) ];
        destination.get(0, 0, data);
        byte b;
        for(int i=0; i<data.length; i=i+3)
        {
            b = data[i];
            data[i] = data[i+2];
            data[i+2] = b;
        }
        bufferedImage1.getRaster().setDataElements(0,0,destination.cols(),destination.rows(),data);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try
        {
            ImageIO.write(bufferedImage1, fileType, byteArrayOutputStream);
        }
        catch (Exception e){System.out.print(e.getMessage());}
        data2.bytesImage = byteArrayOutputStream.toByteArray();
        if(matches>(dense/2))
        {
            dense=0;
            matches=0;
            output1.emit(data2);
        }
        else
        {
            dense=0;
            matches=0;
            output.emit(data2);
        }


    }

    protected  BufferedImage convertToRGB(BufferedImage image)
    {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = newImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }

    protected  void compute(Data data)
    {
        System.load(soPath);
        byte[] bytesImage = data.bytesImage;
        Mat source = readMat(bytesImage);
        BufferedImage  bufferedImage= null;
        dense=0;
        float blackPixels = 0;
        float whitePixels = 0;
        float whitePixelsInGrid=0;
        float blackPixelsInGrid=0;
        referenceList.clear();
        ArrayList<String> whiteRange = new ArrayList<>();
        InputStream src = new ByteArrayInputStream(bytesImage);
        try
        {
            bufferedImage = ImageIO.read(src);
        }
        catch (Exception e){System.out.print(e.getMessage());}
        BufferedImage bufferedImage1 = convertToRGB(bufferedImage);
        for (int y = 0; y < bufferedImage1.getHeight(); y = y + 64)
        {
            for (int x = 0; x < bufferedImage1.getWidth(); x = x + 64)
            {
                for (int i = x; i < x + 64; i++)
                {
                    for (int j = y; j < y + 64; j++)
                    {
                        Color c = new Color(bufferedImage1.getRGB(i, j));
                        String hex = "#" + Integer.toHexString(c.getRGB()).substring(2);
                        //System.out.println("Pixel at "+i+","+j+" "+hex);
                        if (hex.equals("#000000") || hex.equals("#191919") || hex.equals("#0c0c0c"))
                        {
                            blackPixels++;
                            blackPixelsInGrid++;
                        } else
                        {
                            whitePixels++;
                            whitePixelsInGrid++;
                            if (!whiteRange.contains(hex))
                            {
                                whiteRange.add(hex);
                            }
                        }

                    }
                }
                int totalPixelsInGrid = (int)whitePixelsInGrid + (int)blackPixelsInGrid;
                float whitePixelDensity = whitePixelsInGrid/totalPixelsInGrid;
                if ((whitePixelDensity*100)>96)
                {//System.out.println(x+","+y);
                    for (int p1=x;p1<x+64;p1++)
                    {
                        for (int p2=y;p2<y+64;p2++)
                        {
                            Color pink = new Color	(255,104,150);
                            int rgb = pink.getRGB();
                            //bufferedImage1.setRGB(p1,p2,rgb);
                        }
                    }
                    dense++;

                    Mat sub = source.submat(y, y + 64, x, x + 64);
                    //writeMat(sub,destination);
                    templateList.add(sub);

                    //System.out.println(x+" "+y);
                    //referenceList.add(sub);

                }
                whitePixelsInGrid=0;
                blackPixelsInGrid=0;

            }
        }

        System.out.println("Black pixels:" + blackPixels);
        System.out.println("White pixels:" + whitePixels);/*
        System.out.println("White area:" + (whitePixels * 100 / (whitePixels + blackPixels)));
        System.out.println("Black area:" + (blackPixels * 100 / (whitePixels + blackPixels)));
        System.out.println("Total:" + (whitePixels + blackPixels) + " should be :" + (2048 * 2048));
        System.out.println("White hex range size:" + whiteRange.size());
        System.out.println("White hex range:" + whiteRange);
        */
        //System.out.println("Dense blocks:"+dense);
        LOG.info("Dense blocks:"+dense+" temList:"+templateList.size());
        LOG.info("matchCalled");
        match(source,data);
        //firstConcat(data);

    }

    protected void match(Mat source,Data data)
    {
        System.load(soPath);
        File[] files = new File(refPath).listFiles();
        for(File file: files)
        {
            Mat source2 = imread(file.getAbsoluteFile().toString());
            if(matches<dense)
            {
                for (Mat template:templateList)
                {
                    String refImagePath = file.getAbsolutePath();
                    Mat result = new Mat();
                    //Mat template = templateList.get(i);
                    //System.out.println(templateList.indexOf(template));
                    Imgproc.matchTemplate(source2, template, result, Imgproc.TM_CCOEFF_NORMED);
                    //Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());
                    Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
                    if (mmr.maxVal >= 0.90)
                    {
                        matches++;
                        //System.out.println(refImagePath);
                        //System.out.println(templateList.indexOf(template));
                    }
                    if (mmr.maxVal < 0.89)
                    {
                        notMatch++;
                        //if (refImagePath.contains(fileName))
                        {
                            //System.out.println("Should Match but did NOT! "+refImagePath);
                        }
                    }
                    if (mmr.maxVal >= 0.85 && mmr.maxVal < 0.90)
                    {
                        referenceList.add(template);
                    }
                    Point matchLoc;
                    matchLoc = mmr.maxLoc;
                    rectangle(source2, matchLoc, new Point(matchLoc.x + template.cols(), matchLoc.y + template.rows()), new Scalar(0, 255, 0));
                }
            }

            LOG.info("matches "+matches+"ref image"+file.getName().toString()+" source"+data.fileName);
        }
        templateList.clear();
        writeMat(source,data);
    }


    protected  void firstConcat(Data data)
    {
        Mat r2 = new Mat();
        System.out.println("ref list size "+referenceList.size());
        if(referenceList.size()<1024)
        {
            Core.hconcat(referenceList,r2);
        }
        else
        {
            ArrayList <Mat> subReferenceList = new ArrayList <> (referenceList.subList(0,1023));
            Core.hconcat(subReferenceList,r2);
        }
        writeMat(r2,data);
    }

    @Override
    void processTuple(Data data)
    {
        compute(data);
    }



}
