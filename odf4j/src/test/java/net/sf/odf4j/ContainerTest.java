// odf4j: OASIS Open Document Library for Java.
// Copyright (C) 2006 Michael Locher <michael.locher@acm.org>
package net.sf.odf4j;

import java.io.InputStream;
import java.util.Map;

import junit.framework.TestCase;

/**
 * @author Michael Locher (michael.locher@acm.org)
 * @version $Version:$
 */
public class ContainerTest extends TestCase {

    private InputStream getDocument(String name) {
        return ContainerTest.class.getResourceAsStream(name);
    }
    
    public void testSimple() throws Exception
    {
        InputStream doc = this.getDocument("simple.odt");
        Container container = Container.read(doc);
        Map files = container.getFiles();
        assertEquals(7, files.size());
        assertTrue(files.containsKey("mimetype"));
        assertTrue(files.containsKey("META-INF/manifest.xml"));
    }

}
