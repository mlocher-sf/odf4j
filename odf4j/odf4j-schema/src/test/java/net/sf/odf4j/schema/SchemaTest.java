// odf4j: OASIS Open Document Library for Java.
// Copyright (C) 2006 Michael Locher <michael.locher@acm.org>
package net.sf.odf4j.schema;

import java.io.InputStream;

import net.sf.odf4j.pkg.Manifest;
import net.sf.odf4j.pkg.Package;
import net.sf.odf4j.schema.Schema;
import net.sf.odf4j.test.AbstractDocumentTestCase;

/**
 * @author Michael Locher (michael.locher@acm.org)
 * @version $Version:$
 */
public class SchemaTest extends AbstractDocumentTestCase {

    public void testValidateManifest() throws Exception {
        Package pkg = Package.read(this.getSimpleTextDocument());
        Schema.getInstance().validateManifest(
                pkg.getFile(Manifest.MANIFEST_PATH).getInputSource());
    }

    public void testValidateTextContent() throws Exception {
        Package pkg = Package.read(this.getSimpleTextDocument());
        Schema.getInstance().validateDocument(
                pkg.getFile("content.xml").getInputSource());
    }

    public void testSimpleDocument() throws Exception {
        loadAndValidate(this.getSimpleTextDocument());
    }

    public void testDocumentWithImage() throws Exception {
        loadAndValidate(this.getSimpleTextDocumentWithImage());
    }

    public void testSignedDocument() throws Exception {
        loadAndValidate(this.getSignedTextDocument());
    }

    private void loadAndValidate(InputStream document) throws Exception {
        Package.read(document).validate();
    }

}
