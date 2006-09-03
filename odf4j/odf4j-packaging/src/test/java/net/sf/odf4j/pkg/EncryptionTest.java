// odf4j: OASIS Open Document Library for Java.
// Copyright (C) 2006 Michael Locher <michael.locher@acm.org>
package net.sf.odf4j.pkg;

import net.sf.odf4j.AbstractDocumentTestCase;

/**
 * @author Michael Locher (michael.locher@acm.org)
 * @version $Version:$
 */
public class EncryptionTest extends AbstractDocumentTestCase {

    public void testDecryption() throws Exception {
        Package pkg = Package.read(this.getEncryptedTextDocument());
        try {
            pkg.validate();
            fail("exception expected");
        } catch (IllegalStateException e) {
            // ok
        }
        pkg.setPassword("FOOBAR");
        pkg.validate();
    }

}
