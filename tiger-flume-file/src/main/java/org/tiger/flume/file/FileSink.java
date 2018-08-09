package org.tiger.flume.file;

import org.apache.flume.Channel;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.Transaction;
import org.apache.flume.conf.Configurable;
import org.apache.flume.sink.AbstractSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSink extends AbstractSink implements Configurable {

    private static final Logger logger = LoggerFactory.getLogger(FileSink.class);

    private static final String PROP_KEY_ROOTPATH = "fileName";

    private String fileName;

    @Override
    public Status process() throws EventDeliveryException {
        Channel ch = getChannel();
        Transaction txn = ch.getTransaction();
        Event event = null;
        txn.begin();
        while (true) {
            event = ch.take();
            if (event != null) {
                break;
            }
        }
        try {
            String body = new String(event.getBody());
            logger.info("event.getBody()-----" + body);
            txn.commit();
            return Status.READY;
        } catch (Throwable e) {
            logger.error(e.getLocalizedMessage(), e);
            txn.rollback();
            if (e instanceof Error) {
                throw (Error) e;
            } else {
                throw new EventDeliveryException(e);
            }
        } finally {
            txn.close();
        }
    }

    @Override
    public void configure(Context context) {
        fileName = context.getString(PROP_KEY_ROOTPATH);
    }

}
