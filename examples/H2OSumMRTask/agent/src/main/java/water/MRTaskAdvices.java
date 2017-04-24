package water;

import cz.cuni.mff.d3s.distrace.examples.SumMRTask;
import cz.cuni.mff.d3s.distrace.instrumentation.StackTraceUtils;
import cz.cuni.mff.d3s.distrace.instrumentation.StorageUtils;
import cz.cuni.mff.d3s.distrace.tracing.TraceContext;
import jsr166y.CountedCompleter;
import net.bytebuddy.asm.Advice;

public abstract class MRTaskAdvices {
    /**
     * dfork method starts an computation on the initiating node, it's the entry point where the trace
     * context is created. The span created by dfork is closed by getResult method
     */
    public static class dfork {
        @Advice.OnMethodEnter
        public static void enter(@Advice.This Object o) {
            if (o instanceof SumMRTask) {
                TraceContext tc = TraceContext.create().attachOnCurrentThread().attachOnObject(o);
                tc.openNestedSpan("H2O Node" + H2O.SELF.index() + " - Complete MRTask Computation")
                        .setIpPort(H2O.getIpPortString())
                        .add("ipPort", H2O.getIpPortString());
            }
        }
    }

    /**
     * This is closing side of the span opened by the dfork method.
     */
    public static class getResult {
        @Advice.OnMethodExit
        public static void exit(@Advice.This Object o) {
            if (o instanceof SumMRTask) {
                TraceContext tc = TraceContext.getFromObject(o).attachOnCurrentThread();
                tc.closeCurrentSpan();
            }
        }
    }

    /**
     * This method opens an span which traces splitting to smaller tasks and sending remote tasks
     * This span is closed by the onCompletion method once all the sub-tasks on all nodes have been
     * completed.
     */
    public static class setupLocal0 {
        @Advice.OnMethodEnter
        public static void enter(@Advice.This Object o) {
            if (o instanceof SumMRTask) {
                TraceContext tc = TraceContext.getFromObject(o).deepCopy();
                tc.openNestedSpan("H2O Node" + H2O.SELF.index() + " - Setting and Splitting")
                        .setIpPort(H2O.getIpPortString());
                tc.getCurrentSpan().add("setupLocal0 entry", o.toString())
                        .addFlag("setup");
                tc.attachOnObject(o);
                StorageUtils.addToList("setupLocal", o);
            }
        }
    }

    /**
     * This method opens span tracing the remote computation in case when no remote computation is actually done.
     * It is closed by the following method
     */
    public static class remote_compute {
        @Advice.OnMethodEnter
        public static void enter(@Advice.This Object o, @Advice.Argument(0) Integer nlo, @Advice.Argument(1) Integer nhi) {
            if (o instanceof SumMRTask) {
                if (nlo >= nhi) {
                    TraceContext tc = TraceContext.getFromObject(o).deepCopy();
                    tc.openNestedSpan("H2O Node" + H2O.SELF.index() + " - Remote Work - none")
                            .setIpPort(H2O.getIpPortString());
                    tc.attachOnObject(o);
                }
            }
        }

        @Advice.OnMethodExit
        public static void exit(@Advice.This Object o, @Advice.Return RPC ret) {
            if (o instanceof SumMRTask) {
                if (ret == null) {
                    TraceContext.getFromObject(o).closeCurrentSpan();
                }
            }
        }
    }

    /**
     * This method traces the real computation on a single node. The span created by this method
     * is closed by the onCompletion method
     */
    public static class compute2 {
        @Advice.OnMethodEnter
        public static void enter(@Advice.This Object o) {
            if (o instanceof SumMRTask) {
                MRTask tsk = (MRTask) o;
                TraceContext tc = TraceContext.getFromObject(o).deepCopy();
                tc.openNestedSpan("H2O Node" + H2O.SELF.index() + " - Local work - chunks : " + (tsk._hi - tsk._lo));
                tc.getCurrentSpan().add("compute2 entry", o.toString());
                tc.getCurrentSpan().addFlag("compute");
                tc.attachOnObject(o);
                StorageUtils.addToList("compute2", o);
                if (StackTraceUtils.numMethodCalls("compute2") >= 2) {
                    tc.getCurrentSpan().appendToName(" - same thread");
                } else {
                    tc.getCurrentSpan().appendToName(" - new thread");
                }
            }
        }
    }

    /**
     * The following methods are used to open and close span denoting task reducing
     */
    public static class reduce2 {
        @Advice.OnMethodEnter
        public static void enter(@Advice.This MRTask thizz, @Advice.Argument(0) MRTask mrt) {
            if (thizz instanceof SumMRTask) {
                if (mrt != null) {
                    String str;
                    if (thizz._left == null) {
                        str = "right";
                    } else if (thizz._rite == null) {
                        str = "left";
                    } else {
                        str = thizz._left.equals(mrt) ? "left" : "right";
                    }
                    TraceContext tc = TraceContext.getFromObject(thizz);
                    tc.openNestedSpan("H2O Node" + H2O.SELF.index() + " - Reducing " + str);
                }
            }
        }

        @Advice.OnMethodExit
        public static void exit(@Advice.This MRTask thizz, @Advice.Argument(0) MRTask mrt) {
            if (mrt != null) {
                TraceContext tc = TraceContext.getFromObject(thizz);
                tc.closeCurrentSpan();
            }
        }
    }


    /**
     * The following methods are used to open and close span denoting task mapping
     */
    public static class map {
        @Advice.OnMethodEnter
        public static void enter(@Advice.This MRTask thizz) {
            if (thizz instanceof SumMRTask) {
                TraceContext tc = TraceContext.getFromObject(thizz);
                tc.openNestedSpan("H2O Node" + H2O.SELF.index() + " - mapping ");
            }
        }

        @Advice.OnMethodExit
        public static void exit(@Advice.This MRTask thizz) {
            TraceContext tc = TraceContext.getFromObject(thizz);
            tc.closeCurrentSpan();
        }
    }

    /**
     * This method closes computation and setulLocal0 spans.
     */
    public static class onCompletion {
        @Advice.OnMethodExit
        public static void exit(@Advice.This Object o, @Advice.Argument(0) CountedCompleter caller) {
            if (o instanceof SumMRTask) {
                MRTask task = (MRTask) o;
                TraceContext tc = TraceContext.getFromObject(task);

                if (StorageUtils.listContains("compute2", task)
                        && tc.getCurrentSpan().hasFlag("compute")) {
                    StorageUtils.removeFromList("compute2", task);
                    tc.getCurrentSpan().add("compute2 exit", task.toString());
                    tc.closeCurrentSpan();
                }


                // setupLocal0 span finishes when there are no more pending task on this node for this MRTask
                if (StorageUtils.listContains("setupLocal", task)
                        && tc.getCurrentSpan().hasFlag("setup")) {
                    StorageUtils.removeFromList("setupLocal", task);
                    tc.getCurrentSpan()
                            .add("left", task._nleft == null ? "local" : task._nleft._target.getIpPortString())
                            .add("right", task._nrite == null ? "local" : task._nrite._target.getIpPortString());

                    tc.getCurrentSpan().add("setupLocal0 exit", task.toString());
                    tc.closeCurrentSpan();
                }
            }
        }
    }
}
