package org.tiger.storm.common.trident.state;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.storm.shade.com.google.common.collect.Maps;
import org.apache.storm.task.IMetricsContext;
import org.apache.storm.trident.state.ITupleCollection;
import org.apache.storm.trident.state.State;
import org.apache.storm.trident.state.StateFactory;
import org.apache.storm.trident.state.ValueUpdater;
import org.apache.storm.trident.state.snapshot.Snapshottable;

public class MyState implements State, Snapshottable<Long>, ITupleCollection {

    private Map<String, Long> result = Maps.newConcurrentMap();

    @Override
    public Long get() {
        return result.get("key");
    }

    @Override
    public Long update(ValueUpdater updater) {
        Long l =  (Long) updater.update(get());
        System.out.println(l);
        set(l);
        return l;
    }

    @Override
    public void set(Long o) {
        result.put("key", o);
    }

    @Override
    public void beginCommit(Long txid) {

    }

    @Override
    public void commit(Long txid) {

    }

    public static class Factory implements StateFactory {

        @Override
        public State makeState(Map conf, IMetricsContext metrics, int partitionIndex, int numPartitions) {
            return new MyState();
        }
    }

    @Override
    public Iterator<List<Object>> getTuples() {
        return null;
    }

}
