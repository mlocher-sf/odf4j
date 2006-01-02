// odf4j: OASIS Open Document Library for Java.
// Copyright (C) 2006 Michael Locher <michael.locher@acm.org>
package net.sf.odf4j;

import junit.framework.TestCase;

/**
 * @author Michael Locher (michael.locher@acm.org)
 * @version $Version:$
 */
public class VersionTest extends TestCase {
    
    public void testNotYetReleased() {
        assertTrue(Double.parseDouble(Version.VERSION) < 1.0);
    }

}
