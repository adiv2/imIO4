package com.example.imIO4;

import java.awt.image.BufferedImage;
import java.io.*;

import com.datatorrent.api.Context;
import org.apache.hadoop.fs.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datatorrent.api.annotation.OutputPortFieldAnnotation;
import com.datatorrent.api.DefaultOutputPort;
import com.datatorrent.lib.io.fs.AbstractFileInputOperator;

import javax.imageio.ImageIO;

/**
 * read lines from input file and emit them on output port; if end-of-file is reached, a control tuple
 * is emitted on the control port
 */
//Modified to send Images
public class FileReader extends AbstractFileInputOperator<byte[]>
{
    private String fileType;
    private static final Logger LOG = LoggerFactory.getLogger(FileReader.class);

  /**
   * prefix for file start and finish control tuples
   */
    public static final char START_FILE = '(', FINISH_FILE = ')';

  /**
   * output port for file data
   */
    @OutputPortFieldAnnotation(optional = false)
    public final transient DefaultOutputPort<byte[]> output  = new DefaultOutputPort<>();

  /**
   * output port for control data
   */
    @OutputPortFieldAnnotation(optional = false)
    public final transient DefaultOutputPort<String> control = new DefaultOutputPort<>();
    public  int countFileOpen=0;
    public  int countImageSent=0;
    private transient BufferedImage br = null;

    // Path is not serializable so convert to/from string for persistance
    private transient Path filePath;
    private String filePathStr;

    // set to true when end-of-file occurs, to prevent emission of addditional tuples in current window
    private boolean stop;

    // pause for this many milliseconds after end-of-file
    private transient int pauseTime;

    @Override
    public void setup(Context.OperatorContext context)
    {
        super.setup(context);
        pauseTime = context.getValue(Context.OperatorContext.SPIN_MILLIS);

        if (null != filePathStr)
        {      // restarting from checkpoint
            filePath = new Path(filePathStr);
        }
    }

    @Override
    public void endWindow()
    {
        super.endWindow();
        stop = false;
    }

    @Override
    public void emitTuples()
    {
        if ( ! stop )
        {        // normal processing
        super.emitTuples();
        return;
        }

    // we have end-of-file, so emit no further tuples till next window; relax for a bit
        try
        {
            Thread.sleep(pauseTime);
        }
        catch (InterruptedException e) {LOG.info("Sleep interrupted");}
    }

    @Override
    protected InputStream openFile(Path curPath) throws IOException
    {
        LOG.debug("openFile: curPath = {}", curPath);
        filePath = curPath;
        filePathStr = filePath.toString();
        if (filePath.getName().toString().contains(".png")){fileType="png";}
        if (filePath.getName().toString().contains(".jpg")){fileType="jpg";}
        // new file started, send control tuple on control port
        control.emit(START_FILE + filePath.getName());
        countFileOpen++;
        LOG.info("readOpen "+START_FILE + filePath.getName());

        InputStream is = super.openFile(filePath);
        br = ImageIO.read(is);
        return is;
    }

    @Override
    protected void closeFile(InputStream is) throws IOException
    {
        LOG.debug("closeFile: filePath = {}", filePath);
        super.closeFile(is);

        // reached end-of-file, send control tuple on control port
        control.emit(filePath.getName() + FINISH_FILE);
        LOG.info("readClose "+filePath.getName() + FINISH_FILE);
        br.flush();
        br = null;
        filePath = null;
        filePathStr = null;
        stop = true;
    }

    @Override
    protected byte[] readEntity() throws IOException
    {
        //try{Thread.sleep(500);}catch (Exception e){LOG.info("Read Sleep"+e.getMessage());}
        LOG.info("read entity was called"+currentFile);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(br, fileType, baos );
        byte[] imageInByte = baos.toByteArray();
        baos.reset();
        baos.close();
        if(countImageSent<1)
        {
            countImageSent++;
            LOG.info("returned Image "+countImageSent+" s"+baos.size()+" br "+currentFile);
            return imageInByte;
        }
        LOG.info("readEntity: EOF for {}", filePath);
        countImageSent=0;
        return null;
    }

    @Override
    protected void emit(byte[] image)
    {
        output.emit(image);
        LOG.info("send data from read");
    }

}
