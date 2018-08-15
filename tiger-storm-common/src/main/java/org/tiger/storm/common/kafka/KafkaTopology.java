package org.tiger.storm.common.kafka;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.kafka.spout.Func;
import org.apache.storm.kafka.spout.KafkaSpoutConfig;
import org.apache.storm.kafka.spout.KafkaSpoutRetryExponentialBackoff;
import org.apache.storm.kafka.spout.KafkaSpoutRetryExponentialBackoff.TimeInterval;
import org.apache.storm.kafka.spout.KafkaSpoutRetryService;
import org.apache.storm.kafka.spout.trident.KafkaTridentSpoutOpaque;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.trident.Stream;
import org.apache.storm.trident.TridentState;
import org.apache.storm.trident.TridentTopology;
import org.apache.storm.trident.operation.builtin.Count;
import org.apache.storm.trident.operation.builtin.Debug;
import org.apache.storm.trident.testing.MemoryMapState;
import org.apache.storm.trident.testing.Split;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.storm.kafka.spout.KafkaSpoutConfig.FirstPollOffsetStrategy.EARLIEST;

public class KafkaTopology {

    private static final Logger logger = LoggerFactory.getLogger(KafkaTopology.class);

    private static Func<ConsumerRecord<String, String>, List<Object>> JUST_VALUE_FUNC = new Func<ConsumerRecord<String, String>, List<Object>>() {
        @Override
        public List<Object> apply(ConsumerRecord<String, String> record) {
            return new Values(record.value());
        }
    };

    private KafkaTridentSpoutOpaque<String, String> newKafkaTridentSpoutOpaque() {
        return new KafkaTridentSpoutOpaque<String, String>(newKafkaSpoutConfig());
    }

    protected KafkaSpoutConfig<String, String> newKafkaSpoutConfig() {
        return KafkaSpoutConfig.builder("127.0.0.1:9092", "qintao").setProp(ConsumerConfig.GROUP_ID_CONFIG, "kafkaSpoutTestGroup")
                .setProp(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, 200).setRecordTranslator(JUST_VALUE_FUNC, new Fields("str"))
                .setRetry(newRetryService()).setOffsetCommitPeriodMs(10000).setFirstPollOffsetStrategy(EARLIEST)
                .setMaxUncommittedOffsets(250).build();
    }

    protected KafkaSpoutRetryService newRetryService() {
        return new KafkaSpoutRetryExponentialBackoff(new TimeInterval(500L, TimeUnit.MICROSECONDS), TimeInterval.milliSeconds(2),
                Integer.MAX_VALUE, TimeInterval.seconds(10));
    }

    // // 配置kafka spout参数
    // public static Integer kafka_zk_port = 2181;
    // public static String topic = null;
    // public static String kafka_zk_rootpath = null;
    // public static BrokerHosts brokerHosts;
    // public static String kafka_consume_from_start = null;

    public StormTopology buildTopology() {
        // kafka_consume_from_start = "true";
        // kafka_zk_rootpath = "/kafka";
        // brokerHosts = new ZkHosts("127.0.0.1:2181/kafka/brokers");
        //
        // SpoutConfig spoutConf = new SpoutConfig(brokerHosts, "qintao",
        // kafka_zk_rootpath, "kafkaSpout");
        // spoutConf.scheme = new SchemeAsMultiScheme(new MessageScheme());
        // spoutConf.zkPort = kafka_zk_port;
        // spoutConf.zkRoot = kafka_zk_rootpath;
        // spoutConf.zkServers = Arrays.asList(new String[] { "127.0.0.1" });
        //
        // TopologyBuilder builder = new TopologyBuilder();
        // builder.setSpout("spout", new KafkaSpout(spoutConf));
        TridentTopology tridentTopology = new TridentTopology();
        final Stream spoutStream = tridentTopology.newStream("spout1", newKafkaTridentSpoutOpaque()).parallelismHint(2);
        // TridentState tridentState = spoutStream.each(new Fields("str"), new
        // Split(), new Fields("word")).groupBy(new Fields("word"))
        // .persistentAggregate(new MemoryMapState.Factory(), new Count(), new
        // Fields("count"));
        final Stream countStream = spoutStream.each(new Fields("str"), new Split(), new Fields("word"));
        return tridentTopology.build();
    }

    public static void main(String[] args) {
        KafkaTopology topoM = new KafkaTopology();
        StormTopology topology = topoM.buildTopology();
        
    }
}
