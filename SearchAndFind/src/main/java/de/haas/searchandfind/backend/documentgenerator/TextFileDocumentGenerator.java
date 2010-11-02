package de.haas.searchandfind.backend.documentgenerator;

import de.haas.searchandfind.backend.Backend;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.document.DateTools.Resolution;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;

/**
 *
 * @author Michael Haas <haas@cl.uni-heidelberg.de>
 */
public class TextFileDocumentGenerator extends FileDocumentGenerator {

    @Override
    public Document makeDocument(File i) {

        Document doc = new Document();
        Field fileNameField = super.makeFileNameField(i.getPath());
        doc.add(fileNameField);

        NumericField lastModField = super.makeLastModifiedField(i.lastModified());
        // last-modified date
        doc.add(lastModField);

        try {
            // TODO: how is the content indexed now?
            Field contentField = super.makeContentField(i);
            doc.add(contentField);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TextFileDocumentGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }

        return doc;


    }
}
