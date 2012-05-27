package jp.sourceforge.hotchpotch.coopie.util;

public class DurationMeasure implements Duration {

    private long begin_ = -1;
    private long end_ = -1;

    @Override
    public long getBegin() {
        return begin_;
    }

    @Override
    public long getEnd() {
        return end_;
    }

    @Override
    public long getElapsed() {
        if (-1 == begin_) {
            throw new IllegalStateException();
        }
        if (-1 != end_) {
            return end_ - begin_;
        }
        final long l = now() - begin_;
        return l;
    }

    public void begin() {
        if (-1 != begin_) {
            throw new IllegalStateException();
        }
        begin_ = now();
    }

    public void end() {
        if (-1 == begin_) {
            throw new IllegalStateException();
        }
        if (-1 != end_) {
            throw new IllegalStateException();
        }
        end_ = now();
    }

    protected long now() {
        return System.currentTimeMillis();
    }

}
