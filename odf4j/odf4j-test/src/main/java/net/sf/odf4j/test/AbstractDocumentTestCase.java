// odf4j: OASIS Open Document Library for Java.
// Copyright (C) 2006 Michael Locher <michael.locher@acm.org>
package net.sf.odf4j.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

/**
 * @author Michael Locher (michael.locher@acm.org)
 * @version $Version:$
 */
public abstract class AbstractDocumentTestCase extends ODF4JTestCase {

    private final static int ONE_MB = 1048576;

    protected InputStream getDocument(String name) {
        return AbstractDocumentTestCase.class.getResourceAsStream(name);
    }

    protected InputStream getSimpleTextDocument() {
        return this.getDocument("/simple.odt");
    }

    protected InputStream getSimpleTextDocumentWithImage() {
        return this.getDocument("/simpleWithImage.odt");
    }

    protected InputStream getSignedTextDocument() {
        return this.getDocument("/signed.odt");
    }

    protected InputStream getEncryptedTextDocument() {
        return this.getDocument("/encriptedWithPwdFOOBAR.odt");
    }

    protected File createTempFile(InputStream input) throws IOException {
        File tmp = File.createTempFile("test", ".odf");
        tmp.deleteOnExit();
        FileOutputStream out = new FileOutputStream(tmp);
        FileChannel outChannel = out.getChannel();
        ReadableByteChannel inChannel = Channels.newChannel(input);
        long pos = 0;
        do {
            pos += outChannel.transferFrom(inChannel, pos, ONE_MB);
        } while (input.available() > 0);
        outChannel.close();
        out.close();
        return tmp;
    }

}
