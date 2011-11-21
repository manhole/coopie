package jp.sourceforge.hotchpotch.coopie.util;

public abstract class FailureProtection<E extends Throwable> {

    public void execute() throws E {
        boolean success = false;
        try {
            protect();
            success = true;
        } finally {
            if (!success) {
                rescue();
            }
            ensure();
        }
    }

    protected abstract void protect() throws E;

    protected abstract void rescue();

    protected void ensure() {
    };

}
