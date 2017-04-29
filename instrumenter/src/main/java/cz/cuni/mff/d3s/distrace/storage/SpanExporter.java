package cz.cuni.mff.d3s.distrace.storage;

import cz.cuni.mff.d3s.distrace.tracing.Span;
import cz.cuni.mff.d3s.distrace.utils.NativeAgentUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * SpanExporter class is used to process spans when they are closed.
 */
public abstract class SpanExporter {

    private ExecutorService executor = Executors.newCachedThreadPool();

    // allow the implementations to use the debug flag
    protected static boolean debug = false;

    static {
        debug = NativeAgentUtils.isDebugging();
    }

    /**
     * Export span. This method has to be implemented by the implementation.
     *
     * @param span span to export
     */
    public abstract void export(Span span);

    /**
     * This method parses and sets arguments passed to exporter on corresponding exporter class
     * It expects the arguments passed to the span exporter in the configuration.
     * <p>
     * Exporter type is always specified as exporterType(args). In case of the default Distrace exporter types
     * it's ok to just specify exporter name - directZipkin(args) or disk(args). In case of
     * custom exporter type it is necessary to specify full class name of exporter, such as
     * custom.span.exporter(args)
     * <p>
     * If the exporter doesn't have any arguments than empty string is passed to this method
     *
     * @param args exporter arguments
     */
    public abstract void parseAndSetArgs(String args);

    /**
     * Submit span saving task. Spans are exported asynchronously.
     *
     * @param spanSavingTask span saving task
     */
    protected final void submitSpanTask(Runnable spanSavingTask) {
        executor.submit(spanSavingTask);
    }

    /**
     * Create Span Exporter based on the string configuration passed to the native agent.
     *
     * @param exporterType string representing the exporter type
     * @return SpanExporter implementation
     */
    public static SpanExporter fromString(String exporterType) {
        if (exporterType.startsWith("directZipkin")) {
            return new DirectZipkinExporter(getArgs(exporterType));
        } else if (exporterType.equals("disk")) {
            return new JSONDiskExporter(getArgs(exporterType));
        } else {
            String customExporterClassName = exporterType.substring(0, exporterType.indexOf('('));
            try {
                final Class<?> customExporterClazz = Class.forName(customExporterClassName);
                return (SpanExporter) customExporterClazz
                        .getConstructor(String.class).newInstance(getArgs(exporterType));
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Custom span exporter class " + customExporterClassName + " does" +
                        " not exist. Are you sure this is a correct span export class?");
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException("Custom Span exporter class has to have the constructor," +
                        " which expects single string argument for arguments.");
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        throw new RuntimeException("Can't happend, since this check should have been performed by native agent!");
    }

    /**
     * get arguments from the string representing the span exporter type
     */
    private static String getArgs(String exporterTypeStr) {
        return exporterTypeStr.substring(exporterTypeStr.indexOf('(') + 1, exporterTypeStr.lastIndexOf(')'));
    }
}
