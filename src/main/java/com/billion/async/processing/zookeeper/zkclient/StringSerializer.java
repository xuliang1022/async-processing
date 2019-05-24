package com.billion.async.processing.zookeeper.zkclient;

import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;

import java.nio.charset.Charset;

/**
 * @author xuliang
 * @since 2019/05/13
 */
public class StringSerializer implements ZkSerializer {

    private static final Charset CHARSET = Charset.forName("UTF-8");

    @Override
    public byte[] serialize(Object data) throws ZkMarshallingError {
        return String.valueOf(data).getBytes(CHARSET);
    }

    @Override
    public Object deserialize(byte[] bytes) throws ZkMarshallingError {
        return new String(bytes, CHARSET);
    }
}
