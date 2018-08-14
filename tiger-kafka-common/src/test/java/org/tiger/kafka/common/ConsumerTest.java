package org.tiger.kafka.common;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;

public class ConsumerTest extends TestCase {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerTest.class);

    public void testApp() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "1");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        final Consumer<String, String> consumer = new KafkaConsumer<String, String>(props);
        consumer.subscribe(Arrays.asList("qintao"), new ConsumerRebalanceListener() {
            public void onPartitionsRevoked(Collection<TopicPartition> collection) {

            }

            public void onPartitionsAssigned(Collection<TopicPartition> collection) {
                // 将偏移设置到最开始
                consumer.seekToBeginning(collection);
            }
        });
        // consumer.seekToBeginning(new ArrayList<TopicPartition>());

        Map<String, List<PartitionInfo>> listTopics = consumer.listTopics();

        Set<Map.Entry<String, List<PartitionInfo>>> entries = listTopics.entrySet();

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000 * 60));
            for (ConsumerRecord<String, String> record : records) {
                logger.info("[fetched from partition " + record.partition() + ", offset: " + record.offset() + ", message: " + record.key()
                        + ",value:" + record.value() + "]");
            }
        }
    }
}
