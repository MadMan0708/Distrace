package cz.cuni.mff.d3s.distrace.storage;

import cz.cuni.mff.d3s.distrace.tracing.Span;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public abstract class SpanSaver {

    private ExecutorService executor = Executors.newCachedThreadPool();

    protected static boolean debug = false;

    static{
        debug = isDebugging();
    }

    private static native boolean isDebugging();

    public abstract void saveSpan(Span span);

    /**
     * This method parses and sets arguments passed to saver on corresponding saver class
     * Expects arguments passes to saver type.
     *
     * Saver type is always specified as saverType(args). In case of distrace saver types
     * it's ok to just specify saver name - directZipkin(args) or disk(args). In case of
     * custom saver type it is necessary to specify full class name of saver, such as
     * custom.span.saver(args)
     *
     * If the saver doesn't have any arguments than ampty string is passes to this method
     * @param args saver arguments
     */
    public abstract void parseAndSetArgs(String args);

    protected final void submitSpanTask(Runnable spanSavingTask){
        executor.submit(spanSavingTask);
    }

    public static SpanSaver fromString(String saverType) {
        if (saverType.startsWith("directZipkin")) {
            return new DirectZipkinSaver(getArgs(saverType));
        } else if (saverType.equals("disk")) {
            return new JSONDiskSaver(getArgs(saverType));
        } else {
            String customSaverClass = saverType.substring(0, saverType.indexOf('('));
            try {
                final Class<?> customSaverClazz = Class.forName(customSaverClass);
                return (SpanSaver)customSaverClazz
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

    private static String getArgs(String saverTypeStr){
        return saverTypeStr.substring(saverTypeStr.indexOf('(')+1, saverTypeStr.lastIndexOf(')'));
    }
}
