package com.example.imIO4;/*
 * imIO4
 * Created by Aditya Gholba on 18/2/17.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;


public class ToJPEG extends ToolKit
{
    private static final Logger LOG = LoggerFactory.getLogger(ToJPEG.class);
    private  void toJPEG(byte[] byteImage)
    {
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            InputStream in = new ByteArrayInputStream(byteImage);
            bufferedImage = ImageIO.read(in);
            ImageIO.write(bufferedImage, "jpg", baos );
            byte[] imageInByte = baos.toByteArray();
            output.emit(imageInByte);
        }
        catch (Exception e){}
    }

    @Override
    void processTuple(byte[] byteArray)
    {
        toJPEG(byteArray);
    }
}
