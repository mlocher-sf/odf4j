package net.sf.odf4j;

// odf4j: OASIS Open Document Library for Java.
// Copyright (C) 2006 Michael Locher <michael.locher@acm.org>

import java.io.InputStream;

import junit.framework.TestCase;

/**
 * @author Michael Locher (michael.locher@acm.org)
 * @version $Version:$
 */
public abstract class AbstractDocumentTestCase extends TestCase {

    protected InputStream getDocument(String name) {
        return AbstractDocumentTestCase.class.getResourceAsStream(name);
    }

    protected InputStream getSimpleTextDocument() {
        return this.getDocument("/simple.odt");
    }

}
