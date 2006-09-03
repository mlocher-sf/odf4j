// odf4j: OASIS Open Document Library for Java.
// Copyright (C) 2006 Michael Locher <michael.locher@acm.org>
package net.sf.odf4j.schema;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import net.sf.odf4j.util.XMLUtil;

import org.iso_relax.verifier.Verifier;
import org.iso_relax.verifier.VerifierConfigurationException;
import org.iso_relax.verifier.VerifierFactory;
import org.iso_relax.verifier.VerifierFilter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.sun.msv.verifier.jarv.TheFactoryImpl;
import com.sun.msv.verifier.util.ErrorHandlerImpl;

/**
 * @author Michael Locher (michael.locher@acm.org)
 * @version $Version:$
 */
public final class Schema {

    private static final String ODF_SCHEMA = "/OpenDocument-schema-v1.0-os.rng";

    private static final String MANIFEST_SCHEMA = "/OpenDocument-manifest-schema-v1.0-os.rng";

    public static final String MANIFEST_NAMESPACE = "urn:oasis:names:tc:opendocument:xmlns:manifest:1.0";

    private static Schema UNIQUE_INSTANCE;

    private org.iso_relax.verifier.Schema odfSchema;

    private org.iso_relax.verifier.Schema manifestSchema;

    private Schema() throws VerifierConfigurationException, SAXException,
            IOException {
        super();
        VerifierFactory factory = new TheFactoryImpl();
        this.manifestSchema = factory.compileSchema(getManifestSchema());
        this.odfSchema = factory.compileSchema(getODFSchema());
    }

    public synchronized static Schema getInstance() {
        if (Schema.UNIQUE_INSTANCE == null) {
            try {
                Schema.UNIQUE_INSTANCE = new Schema();
            } catch (VerifierConfigurationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SAXException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return Schema.UNIQUE_INSTANCE;
    }

    public synchronized static void dispose() {
        Schema.UNIQUE_INSTANCE = null;
    }

    public static InputStream getODFSchema() {
        return Schema.class.getResourceAsStream(ODF_SCHEMA);
    }

    public Verifier getODFVerifier() throws VerifierConfigurationException {
        return this.odfSchema.newVerifier();
    }

    public static InputStream getManifestSchema() {
        return Schema.class.getResourceAsStream(MANIFEST_SCHEMA);
    }

    public Verifier getManifestVerifier() throws VerifierConfigurationException {
        return this.manifestSchema.newVerifier();
    }

    public void validateManifest(InputSource manifest) throws SAXException,
            IOException, VerifierConfigurationException,
            ParserConfigurationException {
        this.verify(this.getManifestVerifier(), manifest);
    }

    public void validateDocument(InputSource doc) throws SAXException,
            IOException, VerifierConfigurationException,
            ParserConfigurationException {
        this.verify(this.getODFVerifier(), doc);
    }

    private void verify(Verifier verifier, InputSource input)
            throws SAXException, ParserConfigurationException, IOException {
        verifier.setErrorHandler(ErrorHandlerImpl.theInstance);
        VerifierFilter filter = verifier.getVerifierFilter();
        XMLReader reader = XMLUtil.getParser().getXMLReader();
        filter.setParent(reader);
        filter.parse(input);
    }
   
}
