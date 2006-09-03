// odf4j: OASIS Open Document Library for Java.
// Copyright (C) 2006 Michael Locher <michael.locher@acm.org>
package net.sf.odf4j.pkg;

import net.sf.odf4j.AbstractDocumentTestCase;

/**
 * @author Michael Locher (michael.locher@acm.org)
 * @version $Version:$
 */
public class ManifestTest extends AbstractDocumentTestCase {

    public void testParseSimple() throws Exception
    {
        Package pkg = Package.read(this.getSimpleTextDocument());
        assertEquals("text/xml", pkg.getFile("content.xml").getMetadataProperties().get(Manifest.MEDIA_TYPE_PROPERTY));
        assertTrue(!pkg.getFile("content.xml").isEncrypted());        
    }
    
    public void testParseEncrypted() throws Exception
    {
        Package pkg = Package.read(this.getDocument("/encriptedWithPwdFOOBAR.odt"));
        assertEquals("text/xml", pkg.getFile("content.xml").getMetadataProperties().get(Manifest.MEDIA_TYPE_PROPERTY));
        
        assertTrue(!pkg.getFile("meta.xml").isEncrypted());
        assertTrue(pkg.getFile("content.xml").isEncrypted());        
    }    
    
}
