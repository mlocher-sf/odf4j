// odf4j: OASIS Open Document Library for Java.
// Copyright (C) 2006 Michael Locher <michael.locher@acm.org>
package net.sf.odf4j;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

/**
 * @author Michael Locher (michael.locher@acm.org)
 * @version $Version:$
 */
public class Util {

    private Util() {
        super();
    }

    public static SAXParser getParser() throws ParserConfigurationException,
            SAXException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        factory
                .setFeature(
                        "http://apache.org/xml/features/nonvalidating/load-external-dtd",
                        false);

        return factory.newSAXParser();
    }

}
