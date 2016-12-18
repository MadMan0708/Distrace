package cz.cuni.mff.d3s.distrace.storage;

import cz.cuni.mff.d3s.distrace.api.Span;

public abstract class SpanSaver {

    public abstract void saveSpan(Span span);

    public static SpanSaver fromString(String saverType) {
        if (saverType.startsWith("directZipkin")) {
            String ipPort = parseArgs(saverType);
            return new DirectZipkinSaver(ipPort);
        } else if (saverType.equals("disk")) {
            String path = parseArgs(saverType);
            return new JSONDiskSaver(path);
        } else {
            throw new RuntimeException("Should not happen since this check should have been performed by native agent");
        }
    }

    private static String parseArgs(String saverType){
        return saverType.substring(saverType.indexOf('(')+1, saverType.lastIndexOf(')'));
    }
}
