package jp.sourceforge.hotchpotch.coopie;

import org.t2framework.commons.util.task.Task;

public interface TaskExecutable {

    <V, E extends Throwable> V execute(Task<V, E> task) throws E;

}
