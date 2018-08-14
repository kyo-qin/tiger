package org.tiger.storm.common.trident;

import java.util.Map;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.trident.TridentTopology;
import org.apache.storm.trident.operation.BaseFilter;
import org.apache.storm.trident.operation.TridentOperationContext;
import org.apache.storm.trident.operation.builtin.Count;
import org.apache.storm.trident.testing.FixedBatchSpout;
import org.apache.storm.trident.testing.Split;
import org.apache.storm.trident.tuple.TridentTuple;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TridentAppSimple {

    private static final Logger logger = LoggerFactory.getLogger(TridentAppSimple.class);

    public static void main(String[] args) {
        FixedBatchSpout spout = new FixedBatchSpout(new Fields("sentence"), 3, new Values("the cow jumped over the moon"),
                new Values("the man went to the store and bought some candy"), new Values("four score and seven years ago"),
                new Values("how many apples can you eat"));
        spout.setCycle(true);

        TridentTopology topology = new TridentTopology();

        // topology.newStream("spout1", spout).each(new Fields("sentence"), new
        // Split(), new Fields("word"))
        // .each(new Fields("sentence", "word"), new Print());

        // aggregate计算的是每个批次里面的数据
        topology.newStream("spout1", spout).each(new Fields("sentence"), new Split(), new Fields("word")).groupBy(new Fields("word"))
                .aggregate(new Fields("word"), new Count(), new Fields("count")).each(new Fields("word", "count"), new Print())
                .parallelismHint(1);

        Config conf = new Config();
        conf.setMaxSpoutPending(20);
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("Count", conf, topology.build());
    }

    public static class Print extends BaseFilter {

        // 分区索引号从0开始标示
        private int partitionIndex;
        // 总的分区数
        private int numPartitions;

        @Override
        public void prepare(Map conf, TridentOperationContext context) {
            // 获取当前分区以及总的分区数
            this.partitionIndex = context.getPartitionIndex();
            this.numPartitions = context.numPartitions();
        }

        // 过滤条件，其实这边就是用来打印输出，对最后的tuple元数据没有任何改变
        @Override
        public boolean isKeep(TridentTuple tuple) {
            System.out.println(tuple);
            // System.err.println(String.format("Partition idx: %s out of %s
            // partitions got %s/%s", partitionIndex, numPartitions,
            // tuple.get(0).toString(), tuple.get(1).toString()));
            return true;
        }

    }
}
