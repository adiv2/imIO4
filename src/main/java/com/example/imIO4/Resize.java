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
    protected int width=0;
    protected int height=0;

    public double getScale()
    {
        return scale;
    }

    public void setScale(double scale)
    {
        this.scale = scale;
    }

    protected double scale=1;
    public int getWidth()
    {
        return width;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public int getHeight()
    {
        return height;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }


    private static final Logger LOG = LoggerFactory.getLogger(Resize.class);

    private void resize(Data data)
    {
        try
        {
            byte[] byteImage = data.bytesImage;
            InputStream in = new ByteArrayInputStream(byteImage);
            bufferedImage = ImageIO.read(in);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BufferedImage resizedImage;
            LOG.info("fileTypeIs:"+ToolKit.fileType+" scale:"+scale);
            //BufferedImage resizedImage = Thumbnails.of(bufferedImage).size(width, height).asBufferedImage();
            if (height==width && width==0)
            {
                resizedImage = Thumbnails.of(bufferedImage).scale(scale).asBufferedImage();
            }
            else
            {
               resizedImage = Thumbnails.of(bufferedImage).size(width, height).asBufferedImage();
            }
            ImageIO.write(resizedImage, ToolKit.fileType, baos);
            data.bytesImage = baos.toByteArray();
            output.emit(data);
        }
        catch (Exception e){LOG.info(e.getMessage());}
    }

    @Override
    void processTuple(Data data)
    {
        resize(data);
    }

}
