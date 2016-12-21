package cz.cuni.mff.d3s.distrace.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

/**
 * This Service Loader is using the same logic as {@link ServiceLoader}, but instead of loading new instances
 * it just loads classes of the specified files.
 * @param <S>
 */
public final class ClassServiceLoader<S>
{

    /**
     * Creates a new service loader for the given service type and class
     * loader.
     *
     * @param  service
     *         The interface or abstract class representing the service
     *
     * @param  loader
     *         The class loader to be used to load provider-configuration files
     *         and provider classes, or <tt>null</tt> if the system class
     *         loader (or, failing that, the bootstrap class loader) is to be
     *         used
     *
     * @return A new service loader
     */
    public static <S> ArrayList<Class<?>> load(Class<S> service, ClassLoader loader) {
        ArrayList<Class<?>> classes = new ArrayList<>();
        ClassServiceLoader.ClassIterator it = new ClassServiceLoader<S>().new ClassIterator(service, loader);
        while (it.hasNext()){
            classes.add(it.next());
        }
        return classes;
    }

    /**
     * Creates a new service loader for the given service type, using the
     * current thread's {@linkplain java.lang.Thread#getContextClassLoader
     * context class loader}.
     *
     * <p> An invocation of this convenience method of the form
     *
     * <blockquote><pre>
     * ServiceLoader.load(<i>service</i>)</pre></blockquote>
     *
     * is equivalent to
     *
     * <blockquote><pre>
     * ServiceLoader.load(<i>service</i>,
     *                    Thread.currentThread().getContextClassLoader())</pre></blockquote>
     *
     * @param  service
     *         The interface or abstract class representing the service
     *
     * @return A new service loader
     */
    public static <S> ArrayList<Class<?>> load(Class<S> service) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        return ClassServiceLoader.load(service, cl);
    }

    private class ClassIterator implements Iterator<Class<?>> {

        private static final String PREFIX = "META-INF/services/";


        private void fail(Class service, String msg, Throwable cause)
                throws ServiceConfigurationError
        {
            throw new ServiceConfigurationError(service.getName() + ": " + msg,
                    cause);
        }

        private void fail(Class service, String msg)
                throws ServiceConfigurationError
        {
            throw new ServiceConfigurationError(service.getName() + ": " + msg);
        }

        private void fail(Class service, URL u, int line, String msg)
                throws ServiceConfigurationError
        {
            fail(service, u + ":" + line + ": " + msg);
        }
        // Parse a single line from the given configuration file, adding the name
        // on the line to the names list.
        //
        private int parseLine(Class service, URL u, BufferedReader r, int lc,
                              List<String> names)
                throws IOException, ServiceConfigurationError
        {
            String ln = r.readLine();
            if (ln == null) {
                return -1;
            }
            int ci = ln.indexOf('#');
            if (ci >= 0) ln = ln.substring(0, ci);
            ln = ln.trim();
            int n = ln.length();
            if (n != 0) {
                if ((ln.indexOf(' ') >= 0) || (ln.indexOf('\t') >= 0))
                    fail(service, u, lc, "Illegal configuration-file syntax");
                int cp = ln.codePointAt(0);
                if (!Character.isJavaIdentifierStart(cp))
                    fail(service, u, lc, "Illegal provider-class name: " + ln);
                for (int i = Character.charCount(cp); i < n; i += Character.charCount(cp)) {
                    cp = ln.codePointAt(i);
                    if (!Character.isJavaIdentifierPart(cp) && (cp != '.'))
                        fail(service, u, lc, "Illegal provider-class name: " + ln);
                }
                if (!names.contains(ln))
                    names.add(ln);
            }
            return lc + 1;
        }

        // Parse the content of the given URL as a provider-configuration file.
        //
        // @param  service
        //         The service type for which providers are being sought;
        //         used to construct error detail strings
        //
        // @param  u
        //         The URL naming the configuration file to be parsed
        //
        // @return A (possibly empty) iterator that will yield the provider-class
        //         names in the given configuration file that are not yet members
        //         of the returned set
        //
        // @throws ServiceConfigurationError
        //         If an I/O error occurs while reading from the given URL, or
        //         if a configuration-file format error is detected
        //
        private Iterator<String> parse(Class service, URL u)
                throws ServiceConfigurationError
        {
            InputStream in = null;
            BufferedReader r = null;
            ArrayList<String> names = new ArrayList<>();
            try {
                in = u.openStream();
                r = new BufferedReader(new InputStreamReader(in, "utf-8"));
                int lc = 1;
                while ((lc = parseLine(service, u, r, lc, names)) >= 0);
            } catch (IOException x) {
                fail(service, "Error reading configuration file", x);
            } finally {
                try {
                    if (r != null) r.close();
                    if (in != null) in.close();
                } catch (IOException y) {
                    fail(service, "Error closing configuration file", y);
                }
            }
            return names.iterator();
        }

        Class<S> service;
        ClassLoader loader;
        Enumeration<URL> configs = null;
        Iterator<String> pending = null;
        String nextName = null;

        private ClassIterator(Class<S> service, ClassLoader loader) {
            this.service = service;
            this.loader = loader;

            try {
                String fullName = PREFIX + service.getName();
                if (loader == null) {
                    configs = ClassLoader.getSystemResources(fullName);
                } else {
                    configs = loader.getResources(fullName);
                }
            } catch (IOException x) {
                fail(service, "Error locating configuration files", x);
            }


        }

        public boolean hasNext() {
            if (nextName != null) {
                return true;
            }
            while ((pending == null) || !pending.hasNext()) {
                if (!configs.hasMoreElements()) {
                    return false;
                }
                pending = parse(service, configs.nextElement());
            }
            nextName = pending.next();
            return true;
        }

        public Class<?> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            String cn = nextName;
            nextName = null;
            try {
                Class<?> c = Class.forName(cn, false, loader);
                if (!service.isAssignableFrom(c)) {
                    fail(service, "Provider " + cn  + " not a subtype");
                }
                return c;
            } catch (ClassNotFoundException x) {
                fail(service, "Provider " + cn + " not found");
            }

            throw new Error();          // This cannot happen
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

}
