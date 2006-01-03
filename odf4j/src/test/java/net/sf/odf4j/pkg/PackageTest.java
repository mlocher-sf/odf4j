// odf4j: OASIS Open Document Library for Java.
// Copyright (C) 2006 Michael Locher <michael.locher@acm.org>
package net.sf.odf4j.pkg;

import java.util.Map;

import net.sf.odf4j.AbstractDocumentTestCase;

/**
 * @author Michael Locher (michael.locher@acm.org)
 * @version $Version:$
 */
public class PackageTest extends AbstractDocumentTestCase {
    
    public void testSimple() throws Exception
    {
        Package pkg = Package.read(this.getSimpleTextDocument());
        Map files = pkg.getFiles();
        assertEquals(7, files.size());
        assertTrue(files.containsKey("mimetype"));
        assertTrue(files.containsKey("META-INF/manifest.xml"));
        assertNotNull(pkg.getThumbnail());
    }

}
