package com.example.imIO4;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.datatorrent.api.DefaultInputPort;

//Modified to write images
public class BytesFileWriter extends com.example.imIO4.AbstractFileOutputOperator<byte[]>
{
    private static final transient Logger LOG = LoggerFactory.getLogger(BytesFileWriter.class);
    private static final char START_FILE = FileReader.START_FILE,
            FINISH_FILE = FileReader.FINISH_FILE;
    private String fileName; // current file name
    private boolean eof;
    private transient ArrayList<byte[]> bytesArray = new ArrayList<>();
    private ArrayList<String> fileList = new ArrayList<>();
    private int windowCount=0;
    public final transient DefaultInputPort<String> control = new DefaultInputPort<String>()
  {
    @Override
    public void process(String tuple)
    {
        LOG.info("winCount "+currentWindow);
        processControlTuple(tuple);
    }
  };

    private void processControlTuple(final String tuple)
    {
        if (START_FILE == tuple.charAt(0)) {
        // sanity check
        if (null != fileName)
        {
        throw new RuntimeException(String.format("Error: fileName = %s, expected null", fileName));
        }
        fileName = tuple.substring(1);
        LOG.info("fileatPT "+fileName);
        return;
    }

        final int last = tuple.length() - 1;
        if (FINISH_FILE == tuple.charAt(last))
        {
            // end of file
            String name = tuple.substring(0, last);
            LOG.info("Closing file: " + name);
            if (null == fileName || !fileName.equals(name))
            {
                throw new RuntimeException(String.format("Error: fileName = %s != %s = tuple", fileName, tuple));
            }
            eof = true;
            return;
        }
    }

    @Override
    public void endWindow()
    {
        if (!eof) {return;}
        if (null == fileName)
        {
            throw new RuntimeException("Error: fileName empty");
        }
        try{finalizeFile(fileName);}catch (Exception e){LOG.info("Finalize err "+e.getMessage());}
        super.endWindow();
        eof = false;
        fileName = null;
    }

    @Override
    protected String getFileName(byte[] tuple)
  {
    return fileName;
  }

    @Override
    protected byte[] getBytesForTuple(byte[] tuple)
  {
    return tuple;
  }

    @Override
    public void processTuple(byte[] tuple)
    {
        LOG.info("Added to bytesArray "+fileName);
        super.processTuple(tuple);
    }

}
