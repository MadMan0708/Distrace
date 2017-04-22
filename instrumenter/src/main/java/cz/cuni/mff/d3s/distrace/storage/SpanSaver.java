package cz.cuni.mff.d3s.distrace.storage;

import cz.cuni.mff.d3s.distrace.tracing.Span;
import cz.cuni.mff.d3s.distrace.utils.NativeAgentUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * SpanSaver class is used to process spans when they are closed.
 */
public abstract class SpanSaver {

    private ExecutorService executor = Executors.newCachedThreadPool();

    // allow the implementations to use the debug flag
    protected static boolean debug = false;

    static {
        debug = NativeAgentUtils.isDebugging();
    }

    /**
     * Save span. This method has to be implemented by the implementation.
     *
     * @param span span to save
     */
    public abstract void saveSpan(Span span);

    /**
     * This method parses and sets arguments passed to saver on corresponding saver class
     * It expects arguments passed to the span saver in the configuration.
     * <p>
     * Saver type is always specified as saverType(args). In case of the default Distrace saver types
     * it's ok to just specify saver name - directZipkin(args) or disk(args). In case of
     * custom saver type it is necessary to specify full class name of saver, such as
     * custom.span.saver(args)
     * <p>
     * If the saver doesn't have any arguments than empty string is passes to this method
     *
     * @param args saver arguments
     */
    public abstract void parseAndSetArgs(String args);

    /**
     * Submit span saving task. Spans are saved asynchronously.
     *
     * @param spanSavingTask span saving task
     */
    protected final void submitSpanTask(Runnable spanSavingTask) {
        executor.submit(spanSavingTask);
    }

    /**
     * Create Span Saver based on the string configuration passed to the native agent.
     *
     * @param saverType string representing the saver type
     * @return SpanSaver implementation
     */
    public static SpanSaver fromString(String saverType) {
        if (saverType.startsWith("directZipkin")) {
            return new DirectZipkinSaver(getArgs(saverType));
        } else if (saverType.equals("disk")) {
            return new JSONDiskSaver(getArgs(saverType));
        } else {
            String customSaverClass = saverType.substring(0, saverType.indexOf('('));
            try {
                final Class<?> customSaverClazz = Class.forName(customSaverClass);
                return (SpanSaver) customSaverClazz
                        .getConstructor(String.class).newInstance(getArgs(saverType));
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Custom saver class " + customSaverClass + " does" +
                        " not exist. Are you sure this is correct saver class ?");
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException("Custom saver class has to have constructor which expects one string argument for arguments.");
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        throw new RuntimeException("Should not happen since this check should have been performed by native agent");
    }

    /**
     * get arguments from the string representing the span saver type
     */
    private static String getArgs(String saverTypeStr) {
        return saverTypeStr.substring(saverTypeStr.indexOf('(') + 1, saverTypeStr.lastIndexOf(')'));
    }
}
