// odf4j: OASIS Open Document Library for Java.
// Copyright (C) 2006 Michael Locher <michael.locher@acm.org>
package net.sf.odf4j.pkg;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;

import org.xml.sax.InputSource;

/**
 * @author Michael Locher (michael.locher@acm.org)
 * @version $Version:$
 */
public class Package {

    private static final String MANIFEST_PATH = "META-INF/manifest.xml";

    private static final String THUMBNAIL_PATH = "Thumbnails/thumbnail.png";

    private Map entriesByName;

    private Map filesByName;

    protected Package() {
        super();
        this.entriesByName = new HashMap();
        this.filesByName = new HashMap();
    }

    /**
     * @param doc
     * @throws IOException
     */
    public static Package read(InputStream doc) throws IOException {
        Package result = new Package();
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

    public File getFile(String name) throws FileNotFoundException {
        File result = (File) this.filesByName.get(name);
        if (result == null) {
            throw new FileNotFoundException(name);
        }
        return result;
    }

    public File getManifest() throws FileNotFoundException {
        return this.getFile(MANIFEST_PATH);
    }

    public BufferedImage getThumbnail() throws IOException {
        return ImageIO.read(this.getFile(THUMBNAIL_PATH).getInputStream());
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

        public InputSource getInputSource() {
            return new InputSource(this.getInputStream());
        }

        public InputStream getInputStream() {
            return new ByteArrayInputStream(this.data);
        }

        public Reader getReader() {
            // TODO encoding
            return new InputStreamReader(this.getInputStream());
        }

    }

}
