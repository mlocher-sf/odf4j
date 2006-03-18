// odf4j: OASIS Open Document Library for Java.
// Copyright (C) 2006 Michael Locher <michael.locher@acm.org>
package net.sf.odf4j.pkg;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import net.sf.odf4j.AbstractDocumentTestCase;

/**
 * @author Michael Locher (michael.locher@acm.org)
 * @version $Version:$
 */
public class PackageTest extends AbstractDocumentTestCase {
    
    public void testSimple() throws Exception
    {
        loadAndAssertCommon(this.getSimpleTextDocument(), 7);
    }
    
    public void testSimpleWithImage() throws Exception
    {
        loadAndAssertCommon(this.getSimpleTextDocumentWithImage(), 8);
    }
    
    public void testSigned() throws Exception
    {
        Package pkg = loadAndAssertCommon(this.getSignedTextDocument(), 8);
        Map files = pkg.getFiles();
        assertTrue(files.containsKey("META-INF/documentsignatures.xml"));
    }
    
    public void testEncrypted() throws Exception
    {
        loadAndAssertCommon(this.getEncryptedTextDocument(), 7);
    }

    private Package loadAndAssertCommon(InputStream document, int expectedNumFiles) throws IOException {
        Package pkg = Package.read(this.createTempFile(document));
        Map files = pkg.getFiles();
        assertEquals(expectedNumFiles, files.size());
        assertTrue(files.containsKey("mimetype"));
        assertTrue(files.containsKey("META-INF/manifest.xml"));
        assertNotNull(pkg.getThumbnail());
        return pkg;
    }

}
