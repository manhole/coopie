package jp.sourceforge.hotchpotch.coopie.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class DurationMeasures {

    private final String name_;

    private final List<Long> values_ = new ArrayList<>();

    public DurationMeasures(final String name) {
        name_ = name;
    }

    public void measure(final Callable<?> callable) throws Exception {
        final DurationMeasure measure = new DurationMeasure();
        measure.begin();
        callable.call();
        measure.end();
        values_.add(measure.getElapsed());
    }

    @Override
    public String toString() {
        return name_ + values_;
    }
}
