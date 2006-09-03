// odf4j: OASIS Open Document Library for Java.
// Copyright (C) 2006 Michael Locher <michael.locher@acm.org>
package net.sf.odf4j;

import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.TestCase;

/**
 * @author Michael Locher (michael.locher@acm.org)
 * @version $Version:$
 */
public abstract class ODF4JTestCase extends TestCase {
    
    static {
        try {
            boolean tracing = false;
            Level level = tracing ? Level.FINEST : Level.FINE;
            Logger.getLogger("").setLevel(level);
            Logger.getLogger("").getHandlers()[0].setLevel(level);
        } catch (Exception e) {
            System.err.println("failed to setup loggers for testing");
        }
    }

}
