package com.example.imIO4;/*
 * imIO5.1
 * Created by Aditya Gholba on 2/5/17.
 */

import com.datatorrent.lib.codec.KryoSerializableStreamCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImStreamCodec extends KryoSerializableStreamCodec
{
    protected static final Logger LOG = LoggerFactory.getLogger(ASASSN.class);
    private  int tupleNum;
    private  int partitions;
    ImStreamCodec(int partitions)
    {
        this.partitions=partitions;
    }
    @Override
    public int getPartition(Object o)
    {

        int part = tupleNum%10;
        if(part>partitions)
        {
            part=part-(partitions+1);
        }
        LOG.info("TupNumIs "+part);
        tupleNum++;
        return part;
    }
}
