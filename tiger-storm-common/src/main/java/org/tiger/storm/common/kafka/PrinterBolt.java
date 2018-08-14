package org.tiger.storm.common.kafka;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrinterBolt extends BaseBasicBolt {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(PrinterBolt.class);

    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        logger.info(input.getValue(0).toString());
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }

}
