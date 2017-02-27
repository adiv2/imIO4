package com.example.imIO4;/*
 * imIO4
 * Created by Aditya Gholba on 20/2/17.
 */

import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class Resize extends ToolKit
{
    private int width=300;
    private int height=300;
    private static final Logger LOG = LoggerFactory.getLogger(Resize.class);

    private void resize(byte[] byteImage)
    {
        try
        {
            InputStream in = new ByteArrayInputStream(byteImage);
            bufferedImage = ImageIO.read(in);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //BufferedImage resizedImage = Thumbnails.of(bufferedImage).size(width, height).asBufferedImage();
            BufferedImage resizedImage = Thumbnails.of(bufferedImage).scale(0.25).asBufferedImage();
            ImageIO.write(resizedImage, fileType, baos);
            byte[] imageInByte = baos.toByteArray();
            output.emit(imageInByte);
        }
        catch (Exception e){LOG.info(e.getMessage());}
    }

    @Override
    void processTuple(byte[] byteArray)
    {
        resize(byteArray);
    }

}
