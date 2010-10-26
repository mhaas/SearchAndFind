

package de.haas.searchandfind.backend.documentgenerator;

import java.io.File;
import org.apache.lucene.document.Document;

/**
 *
 * @author laga
 */
public interface FileDocumentGenerator {

    // TODO: specify schema for Document instances
    /**
     * Creates Document instance from a given File.
     * May return null if File has unsupported type.
     * 
     * Document instance must have at least "fileName" field.
     * 
     * @param i
     * @return
     */
    public Document makeDocument(File i);



}