package org.tiger.storm.common.kafka;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.List;

import org.apache.storm.spout.Scheme;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageScheme implements Scheme {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(MessageScheme.class);

    @Override
    public List<Object> deserialize(ByteBuffer buffer) {
        Charset charset = Charset.forName("UTF-8");
        CharsetDecoder decoder = charset.newDecoder();
        CharBuffer charBuffer;
        try {
            charBuffer = decoder.decode(buffer);
            buffer.flip();
            String msg_0 = "hello";
            return new Values(msg_0, charBuffer.toString());
        } catch (CharacterCodingException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public Fields getOutputFields() {
        return new Fields("key", "message");
    }

}
