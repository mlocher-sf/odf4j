// odf4j: OASIS Open Document Library for Java.
// Copyright (C) 2006 Michael Locher <michael.locher@acm.org>
package net.sf.odf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Michael Locher (michael.locher@acm.org)
 * @version $Version:$
 */
public class Container {

    private Map entriesByName;

    private Map filesByName;

    protected Container() {
        super();
        this.entriesByName = new HashMap();
        this.filesByName = new HashMap();
    }

    /**
     * @param doc
     * @throws IOException
     */
    public static Container read(InputStream doc) throws IOException {
        Container result = new Container();
        ZipInputStream archive = new ZipInputStream(doc);
        ZipEntry entry;
        byte[] buffer = new byte[4096];
        do {
            entry = archive.getNextEntry();
            if (entry == null) {
                break;
            }
            if (entry.isDirectory()) {
                result.addDirectory(new Directory(entry.getName()));
            } else {
                int estimatedSize = (int) entry.getSize();
                if (estimatedSize < 0) {
                    estimatedSize = 4096;
                }
                ByteArrayOutputStream data = new ByteArrayOutputStream(
                        estimatedSize);
                while (archive.available() > 0) {
                    int count = archive.read(buffer);
                    if (count > 0) {
                        data.write(buffer, 0, count);
                    }
                }
                archive.closeEntry();
                result.addFile(new File(entry.getName(), data.toByteArray()));
            }
        } while (entry != null);
        return result;
    }

    /**
     * @param entry
     * @param bs
     */
    private void addFile(File fileEntry) {
        this.entriesByName.put(fileEntry.getName(), fileEntry);
        this.filesByName.put(fileEntry.getName(), fileEntry);
    }

    /**
     * @param dirEntry
     */
    private void addDirectory(Directory dirEntry) {
        this.entriesByName.put(dirEntry.getName(), dirEntry);
    }

    public Map getFiles() {
        return Collections.unmodifiableMap(this.filesByName);
    }

    public String toString() {
        return this.entriesByName.keySet().toString();
    }

    // ------------------------------------------------------------------------

    public static interface Entry {
        public String getName();
    }

    protected static class AbstractEntry implements Entry {
        private String name;

        protected AbstractEntry(String name) {
            super();
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public String toString() {
            return this.name;
        }
    }

    public static class Directory extends AbstractEntry {

        protected Directory(String name) {
            super(name);
        }

    }

    public static class File extends AbstractEntry {

        private byte[] data;

        protected File(String name, byte[] data) {
            super(name);
            this.data = data;
        }

        public byte[] getData() {
            return this.data;
        }

    }

}
