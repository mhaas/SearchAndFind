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
    private static final Logger l = Logger.getLogger("DocumentFatory");

    /**
     * Return Document instance for File f.
     * 
     * Uses heuristics internally such as the JMimemagic library
     * to find out the correct file type, then dispatches
     * the file to the correct DocumentGenerator.
     * 
     * May return null if the file type can't be determined
     * or if the DocumentGenerator instance encountered an error.
     *
     * @param f File for which a document is to be generated
     * @return Document or null in case of error
     */
    public static Document getDocument(final File f) {

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
            l.info("Magic for file not found. Skipping this file for now.");
            return null;
        } catch (MagicException ex) {
            Logger.getLogger(DocumentFactory.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;

    }


    /**
     * Dispatches file to appropriate DocumentGenerator instance, based on MIME type.
     *
     * Will return null if the file type is unknown or if an error has occurred
     * in the DocumentGenerator.
     *
     * @param mimeType String MIME type such as text/html
     * @param f File handle
     * @return Document instance or null if an error occured
     */
    private static Document getDocumentForMimeType(final String mimeType, final File f) {
        l.info("Making Document for MIME type: " + mimeType);
        if (mimeType.startsWith("text/plain")) {
            FileDocumentGenerator dg = new TextFileDocumentGenerator();
            return dg.makeDocument(f);
        } else if (mimeType.startsWith("text/html")) {
            FileDocumentGenerator dg = new HtmlFileDocumentGenerator();
            return dg.makeDocument(f);

        } else if (mimeType.startsWith("text/sgml")) {
            FileDocumentGenerator dg = new HtmlFileDocumentGenerator();
            return dg.makeDocument(f);
        } else if(mimeType.startsWith("application/pdf")) {
            FileDocumentGenerator dg = new PdfFileDocumentGenerator();
            return dg.makeDocument(f);
        }
        // base case
        return null;
    }
}
