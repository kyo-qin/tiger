package org.tiger.storm.common.trident;

import java.io.Serializable;
import java.util.List;

import org.apache.storm.generated.GlobalStreamId;
import org.apache.storm.grouping.CustomStreamGrouping;
import org.apache.storm.shade.com.google.common.collect.ImmutableList;
import org.apache.storm.task.WorkerTopologyContext;

public class CustomRepartition implements CustomStreamGrouping, Serializable {

    private int tasks = 0;

    @Override
    public void prepare(WorkerTopologyContext context, GlobalStreamId stream, List<Integer> targetTasks) {
        tasks = targetTasks.size();
    }

    @Override
    public List<Integer> chooseTasks(int taskId, List<Object> values) {
        //送到哪几个task里面去
        return null;
    }

}
