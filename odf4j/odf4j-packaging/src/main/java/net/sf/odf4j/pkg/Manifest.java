// odf4j: OASIS Open Document Library for Java.
// Copyright (C) 2006 Michael Locher <michael.locher@acm.org>
package net.sf.odf4j.pkg;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import net.sf.odf4j.schema.Schema;
import net.sf.odf4j.util.XMLUtil;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Encapsulates the manifest information.
 * 
 * @author Michael Locher (michael.locher@acm.org)
 * @version $Version:$
 */
public class Manifest {

    public static final String MANIFEST_PATH = "META-INF/manifest.xml";

    public static final String FULL_PATH_PROPERTY = "full-path";

    public static final String MEDIA_TYPE_PROPERTY = "media-type";

    public static final String SIZE_PROPERTY = "size";

    public static final String ENC_CHECKSUM_TYPE = "checksum-type";

    public static final String ENC_CHECKSUM = "checksum";

    public static final String ENC_ALGO_NAME = "algorithm-name";

    public static final String ENC_INITVECTOR = "initialisation-vector";

    public static final String ENC_KEY_DERIVATION = "key-derivation-name";

    public static final String ENC_SALT = "salt";

    public static final String ENC_ITER_COUNT = "iteration-count";

    private Map entries;

    public Manifest() {
        super();
        this.entries = new HashMap();
    }

    public static Manifest createFrom(Package pkg) {
        try {
            return parse(pkg.getFile(MANIFEST_PATH).getInputSource());
        } catch (FileNotFoundException e) {
            return new Manifest();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Manifest parse(InputSource source) throws SAXException,
            ParserConfigurationException, IOException {
        XMLReader reader = XMLUtil.getParser().getXMLReader();
        ManifestHandler handler = new ManifestHandler();
        reader.setContentHandler(handler);
        reader.parse(source);
        Manifest result = handler.getManifest();
        return result;
    }

    public void addEntry(Map properties) {
        Object key = properties.get(FULL_PATH_PROPERTY);
        this.entries.put(key, properties);
    }

    public Map getProperties(String fullPath) {
        Map result = (Map) this.entries.get(fullPath);
        if (result == null) {
            result = new HashMap();
            this.entries.put(fullPath, result);
        }
        return result;
    }

    // -------------------------------------------------------------------------

    /**
     * SAX2 ContentHandler for Open Document Manifest.
     * 
     * <pre>
     *              namespace manifest = &quot;urn:oasis:names:tc:opendocument:xmlns:manifest:1.0&quot;
     *             
     *              start = manifest
     *              manifest = element manifest:manifest { file-entry+ }
     *             
     *              file-entry = element manifest:file-entry { file-entry-attlist, encryption-data? }
     *              file-entry-attlist &amp;= attribute manifest:full-path { xsd:string }
     *              file-entry-attlist &amp;= attribute manifest:size { xsd:nonNegativeInteger }?
     *              file-entry-attlist &amp;= attribute manifest:media-type { xsd:string }
     *             
     *              encryption-data = element manifest:encryption-data {encryption-data-attlist, algorithm, key-derivation}
     *              encryption-data-attlist &amp;= attribute manifest:checksum-type { xsd:string }
     *              encryption-data-attlist &amp;= attribute manifest:checksum { xsd:base64Binary }
     *             
     *              algorithm = element manifest:algorithm { algorithm-attlist, empty }
     *              algorithm-attlist &amp;= attribute manifest:algorithm-name { xsd:string }
     *              algorithm-attlist &amp;= attribute manifest:initialisation-vector { xsd:base64Binary }
     *             
     *              key-derivation = element manifest:key-derivation { key-derivation-attlist, empty }
     *              key-derivation-attlist &amp;= attribute manifest:key-derivation-name { xsd:string }
     *              key-derivation-attlist &amp;= attribute manifest:salt { xsd:base64Binary }
     *              key-derivation-attlist &amp;= attribute manifest:iteration-count { xsd:nonNegativeInteger }
     * </pre>
     * 
     * @author Michael Locher (michael.locher@acm.org)
     * @version $Version:$
     */
    private static class ManifestHandler extends DefaultHandler {

        private final static String NAMESPACE = Schema.MANIFEST_NAMESPACE;

        private final static String ENTRY = "file-entry";

        private final static String ENCRYPTION = "encryption-data";

        private final static String ENCRYPTION_ALGO = "algorithm";

        private final static String ENCRYPTION_KEY = "key-derivation";

        private Manifest manifest;

        private Map currentEntry;

        private Boolean encrypted;

        public ManifestHandler() {
            super();
            this.manifest = new Manifest();
        }

        public Manifest getManifest() {
            return this.manifest;
        }

        public void startElement(String namespaceURI, String localName,
                String qname, Attributes attrs) throws SAXException {
            if (NAMESPACE.equals(namespaceURI))
                if (ENTRY.equals(localName)) {
                    push();
                    gatherAttributes(attrs);
                } else if (ENCRYPTION.equals(localName)) {
                    this.encrypted = Boolean.TRUE;
                    gatherAttributes(attrs);
                } else if (ENCRYPTION_ALGO.equals(localName)
                        || ENCRYPTION_KEY.equals(localName)) {
                    gatherAttributes(attrs);
                }
        }

        public void endElement(String namespaceURI, String localName,
                String qname) throws SAXException {
            if (NAMESPACE.equals(namespaceURI) && ENTRY.equals(localName)) {
                pop();
            }
        }

        private void gatherAttributes(Attributes attrs) {
            for (int i = 0; i < attrs.getLength(); i++) {
                this.currentEntry.put(attrs.getLocalName(i), attrs.getValue(i));
            }
        }

        private void push() {
            this.currentEntry = new HashMap();
            this.encrypted = Boolean.FALSE;
        }

        private void pop() {
            this.currentEntry.put("encrypted", encrypted);
            this.manifest.addEntry(this.currentEntry);
        }

    } // inner-class

}
