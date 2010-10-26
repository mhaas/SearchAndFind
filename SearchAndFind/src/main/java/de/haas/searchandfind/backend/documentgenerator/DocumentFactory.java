package de.haas.searchandfind.backend.documentgenerator;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatch;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;
import org.apache.lucene.document.Document;

/**
 *
 * Creates Documents given a File.
 *
 * @author Michael Haas <haas@cl.uni-heidelberg.de<
 */
public class DocumentFactory {

    private static boolean initialized = false;

    /**
     *
     * @param f
     * @return Document or null in case of error
     */
    public static Document getDocument(File f) {

        if (!initialized) {
            try {
                Magic.initialize();
            } catch (MagicParseException ex) {
                Logger.getLogger(DocumentFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
            initialized = true;
            // oops.
        }
        // use extension to optimize test order
        boolean useExtensionHints = true;
        try {
            MagicMatch match = Magic.getMagicMatch(f, useExtensionHints);
            return getDocumentForMimeType(match.getMimeType(), f);


        } catch (MagicParseException ex) {
            Logger.getLogger(DocumentFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MagicMatchNotFoundException ex) {
            Logger.getLogger(DocumentFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MagicException ex) {
            Logger.getLogger(DocumentFactory.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;

    }

    private static Document getDocumentForMimeType(String mimeType, File f) {
        // TODO: find out what mime types we're looking for
        if (mimeType.startsWith("text/plain")) {
            FileDocumentGenerator dg = new TextFileDocumentGenerator();
            return dg.makeDocument(f);
        }
        // base case
        return null;
    }
}
