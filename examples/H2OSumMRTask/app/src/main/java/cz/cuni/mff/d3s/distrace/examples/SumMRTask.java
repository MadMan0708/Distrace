package cz.cuni.mff.d3s.distrace.examples;

import water.MRTask;
import water.fvec.Chunk;

/**
 * Simple sum MR task
 */
public class SumMRTask extends MRTask<SumMRTask> {
    long sum = 0;

    public long getSum() {
        return sum;
    }

    @Override
    public void map(Chunk c) {
        for (int i = 0; i < c._len; i++) {
            sum += c.at8(i);
        }
    }

    @Override
    public void reduce(SumMRTask mrt) {
        sum += mrt.sum;
    }
}
