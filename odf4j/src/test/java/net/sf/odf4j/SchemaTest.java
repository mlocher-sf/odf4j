// odf4j: OASIS Open Document Library for Java.
// Copyright (C) 2006 Michael Locher <michael.locher@acm.org>
package net.sf.odf4j;

import net.sf.odf4j.pkg.Manifest;
import net.sf.odf4j.pkg.Package;

/**
 * @author Michael Locher (michael.locher@acm.org)
 * @version $Version:$
 */
public class SchemaTest extends AbstractDocumentTestCase {


    public void testValidateManifest() throws Exception
    {
        Package pkg = Package.read(this.getSimpleTextDocument());
        Schema.getInstance().validateManifest(pkg.getFile(Manifest.MANIFEST_PATH).getInputSource());
    }

    public void testValidateTextContent() throws Exception
    {
        Package pkg = Package.read(this.getSimpleTextDocument());
        Schema.getInstance().validateDocument(pkg.getFile("content.xml").getInputSource());
    }
    
}
