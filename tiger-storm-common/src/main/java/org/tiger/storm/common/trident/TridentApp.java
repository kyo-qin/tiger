package org.tiger.storm.common.trident;

import java.util.Map;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.LocalDRPC;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.trident.TridentState;
import org.apache.storm.trident.TridentTopology;
import org.apache.storm.trident.operation.BaseFilter;
import org.apache.storm.trident.operation.TridentOperationContext;
import org.apache.storm.trident.operation.builtin.Count;
import org.apache.storm.trident.operation.builtin.FilterNull;
import org.apache.storm.trident.operation.builtin.MapGet;
import org.apache.storm.trident.operation.builtin.Sum;
import org.apache.storm.trident.operation.builtin.TupleCollectionGet;
import org.apache.storm.trident.testing.FixedBatchSpout;
import org.apache.storm.trident.testing.MemoryMapState;
import org.apache.storm.trident.testing.Split;
import org.apache.storm.trident.tuple.TridentTuple;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TridentApp {

    private static final Logger logger = LoggerFactory.getLogger(TridentApp.class);

    public static void main(String[] args) {
        FixedBatchSpout spout = new FixedBatchSpout(new Fields("sentence"), 3, new Values("the cow jumped over the moon"),
                new Values("the man went to the store and bought some candy"), new Values("four score and seven years ago"),
                new Values("how many apples can you eat"));
        spout.setCycle(true);

        TridentTopology topology = new TridentTopology();
        
//        topology.newStream("spout1", spout).each(new Fields("sentence"), new Split(), new Fields("word"))
//                .groupBy(new Fields("word")).aggregate(new Fields("word"), new Count(),new Fields("count"));
                
        
        TridentState wordCounts = topology.newStream("spout1", spout).each(new Fields("sentence"), new Split(), new Fields("word"))
                .groupBy(new Fields("word"))
                .persistentAggregate(new MemoryMapState.Factory(), new Fields("word"), new Count(), new Fields("count")).parallelismHint(5);// 并行度

        LocalDRPC drpc = new LocalDRPC();
        // topology.newDRPCStream("words-print", drpc).each(new Fields("args"),
        // new Split(), new Fields("word")).groupBy(new Fields("word"))
        // .stateQuery(wordCounts, new Fields("word"), new MapGet(), new
        // Fields("count")).each(new Fields("count"), new FilterNull())
        // .aggregate(new Fields("count"), new Sum(), new
        // Fields("sum")).each(new Fields("sum"), new PrintSingle());
        topology.newDRPCStream("words-print", drpc).stateQuery(wordCounts, new TupleCollectionGet(), new Fields("word", "count"))
                .each(new Fields("word", "count"), new Print());

        // 创建一个topology
        StormTopology stormTopology = topology.build();

        Config conf = new Config();
        // 这里设置的是整个demotop所占用的槽位数，也就是worker的数量
        // conf.setNumWorkers(4);
        // conf.setDebug(true);
        // conf.setNumAckers(0);
        try {
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("tridentAppTopo", conf, stormTopology);
            // StormSubmitter.submitTopology("demotopo", conf, demotop);
            for (int i = 0; i < 10; i++) {
                System.out.println(drpc.execute("words-print", ""));
                Thread.sleep(1000);
            }
            cluster.deactivate("tridentAppTopo");
            cluster.killTopology("tridentAppTopo");
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
        }
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
            System.err.println(String.format("Partition idx: %s out of %s partitions got %s/%s", partitionIndex, numPartitions,
                    tuple.get(0).toString(), tuple.get(1).toString()));
            return true;
        }

    }

    public static class PrintSingle extends BaseFilter {

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
            System.err.println(
                    String.format("Partition idx: %s out of %s partitions got %s", partitionIndex, numPartitions, tuple.get(0).toString()));
            return true;
        }

    }
}
