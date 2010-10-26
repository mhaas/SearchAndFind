

package de.haas.searchandfind.backend.documentgenerator;

import java.io.File;
import org.apache.lucene.document.Document;

/**
 *
 * @author Michael Haas <haas@cl.uni-heidelberg.de>
 */
public class PdfFileDocumentGenerator implements FileDocumentGenerator {

    @Override
    public Document makeDocument(File i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
