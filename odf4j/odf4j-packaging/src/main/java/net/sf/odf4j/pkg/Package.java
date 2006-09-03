// odf4j: OASIS Open Document Library for Java.
// Copyright (C) 2006 Michael Locher <michael.locher@acm.org>
package net.sf.odf4j.pkg;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.odf4j.schema.Schema;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.iso_relax.verifier.VerifierConfigurationException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Michael Locher (michael.locher@acm.org)
 * @version $Version:$
 */
public class Package {

    private static final Log LOG = LogFactory.getLog(Package.class);

    private static final String THUMBNAIL_PATH = "Thumbnails/thumbnail.png";

    private Manifest manifest;

    private Map entriesByName;

    private Map filesByName;

    private String password;

    protected Package() {
        super();
        this.entriesByName = new HashMap();
        this.filesByName = new HashMap();
    }

    private String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static Package read(java.io.File doc) throws IOException {
        LOG.debug("read odf package from file");
        FileInputStream input = null;
        FileChannel channel = null;
        try {
            input = new FileInputStream(doc);
            channel = input.getChannel();
            ByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0, doc.length());
            return read(buffer);
        } finally {
            if (channel != null) {
                channel.close();
            }
            if (input != null) {
                input.close();
            }
        }
    }

    private static Package read(final ByteBuffer buf) throws IOException {
        // Wraps the buffer with an InputStream
        // credit: http://javaalmanac.com/egs/java.nio/Buffer2Stream.html
        return read(new InputStream() {
            public synchronized int read() throws IOException {
                if (!buf.hasRemaining()) {
                    return -1;
                }
                return buf.get();
            }

            public synchronized int read(byte[] bytes, int off, int len)
                    throws IOException {
                // Read only what's left
                len = Math.min(len, buf.remaining());
                buf.get(bytes, off, len);
                return len;
            }
        });
    }

    public static Package read(InputStream doc) throws IOException {
        LOG.debug("read odf package from stream");
        Package result = new Package();
        ZipInputStream archive = new ZipInputStream(doc);
        ZipEntry entry;
        do {
            entry = archive.getNextEntry();
            if (LOG.isTraceEnabled()) {
                LOG.trace("processing zip entry: "
                        + (entry == null ? "null" : entry.toString()));
            }
            if (entry == null) {
                break;
            }
            if (entry.isDirectory()) {
                result.addDirectory(result.new Directory(entry.getName()));
            } else {
                int announcedSize = (int) entry.getSize();
                ByteBuffer data = (announcedSize < 0) ? readUnknownSizeFileEntry(archive)
                        : readFileEntry(archive, announcedSize);
                result.addFile(result.createFile(entry, data));
                archive.closeEntry();
            }
        } while (entry != null);
        return result;
    }

    private static ByteBuffer readFileEntry(InputStream archive,
            int announcedSize) throws IOException {
        LOG.trace("read zip entry with size " + announcedSize);
        ByteBuffer data = ByteBuffer.allocate(announcedSize);
        ReadableByteChannel src = Channels.newChannel(archive);
        while (data.hasRemaining()) {
            if (src.read(data) == -1) {
                // EOF detected
                break;
            }
        }
        return data;
    }

    private static ByteBuffer readUnknownSizeFileEntry(InputStream archive)
            throws IOException {
        LOG.trace("read zip entry with unknown size");
        ByteArrayOutputStream data = new ByteArrayOutputStream(8192);
        byte[] buffer = new byte[4096];
        while (archive.available() > 0) {
            int count = archive.read(buffer);
            if (count > 0) {
                data.write(buffer, 0, count);
            }
        }
        return ByteBuffer.wrap(data.toByteArray());
    }

    private void addDirectory(Directory dirEntry) {
        this.entriesByName.put(dirEntry.getName(), dirEntry);
    }

    private void addFile(File fileEntry) {
        this.entriesByName.put(fileEntry.getName(), fileEntry);
        this.filesByName.put(fileEntry.getName(), fileEntry);
    }

    private File createFile(ZipEntry entry, ByteBuffer data) {
        String name = entry.getName();
        if (Manifest.MANIFEST_PATH.equals(name)) {
            return new ManifestFile(name, data);
        } else {
            return new File(name, data);
        }
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

    public File getContent() throws FileNotFoundException {
        return this.getFile("content.xml");
    }

    public Manifest getManifest() {
        if (this.manifest == null) {
            this.manifest = Manifest.createFrom(this);
        }
        return this.manifest;
    }

    public BufferedImage getThumbnail() throws IOException {
        return ImageIO.read(this.getFile(THUMBNAIL_PATH).getInputStream());
    }

    public void validate() throws SAXException, IOException,
            VerifierConfigurationException, ParserConfigurationException {
        Schema.getInstance().validateDocument(
                this.getContent().getInputSource());
        Schema.getInstance().validateManifest(
                this.getFile(Manifest.MANIFEST_PATH).getInputSource());
    }

    public String toString() {
        return this.entriesByName.keySet().toString();
    }

    // ------------------------------------------------------------------------

    public static interface Entry {
        public String getName();

        public Map getMetadataProperties();
    }

    protected class AbstractEntry implements Entry {
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

        public Map getMetadataProperties() {
            return Package.this.getManifest().getProperties(this.getName());
        }

        public Object getMetadataProperty(String propertyName) {
            return this.getMetadataProperties().get(propertyName);
        }
    }

    public class Directory extends AbstractEntry {
        protected Directory(String name) {
            super(name);
        }
    }

    public class File extends AbstractEntry {

        private ByteBuffer data;

        protected File(String name, ByteBuffer data) {
            super(name);
            this.data = data;
        }

        public byte[] getData() {
            return this.isEncrypted() ? this.decript() : this.data.array();
        }

        public InputSource getInputSource() {
            return new InputSource(this.getInputStream());
        }

        public InputStream getInputStream() {
            return new ByteArrayInputStream(this.getData());
        }

        public Reader getReader() {
            // TODO encoding
            return new InputStreamReader(this.getInputStream());
        }

        public boolean isEncrypted() {
            return Boolean.TRUE.equals(this.getMetadataProperty("encrypted"));
        }

        private byte[] decript() {
            String pwd = Package.this.getPassword();
            if (pwd == null) {
                throw new IllegalStateException("no password set");
            }
            return this.data.array();
        }
    }

    public class ManifestFile extends File {

        protected ManifestFile(String name, ByteBuffer data) {
            super(name, data);
            //assert Manifest.MANIFEST_PATH.equals(this.getName());
        }

        public boolean isEncrypted() {
            return false;
        }
    }

}
