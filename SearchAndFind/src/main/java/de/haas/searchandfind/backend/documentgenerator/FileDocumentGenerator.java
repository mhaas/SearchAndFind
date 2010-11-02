package de.haas.searchandfind.backend.documentgenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import de.haas.searchandfind.common.Constants;

/**
 *
 * @author Michael Haas <haas@cl.uni-heidelberg.de<
 */
public abstract class FileDocumentGenerator {

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
    public abstract Document makeDocument(File i);

    public Field makeKeywordsField(String keyWords) {
        Field keyWordsField = new Field(Constants.FIELD_KEYWORDS, keyWords, Field.Store.YES, Field.Index.ANALYZED);
        return keyWordsField;
    }

    public Field makeContentField(String content) {
        Field contentField = new Field(Constants.FIELD_CONTENT, content, Field.Store.NO, Field.Index.ANALYZED);
        return contentField;
    }

    public Field makeFileNameField(String pathName) {
        Field fileNameField = new Field(Constants.FIELD_FILE_NAME, pathName, Field.Store.YES, Field.Index.NOT_ANALYZED);
        return fileNameField;
    }

    public NumericField makeLastModifiedField(long lastModifiedDate) {
        boolean indexWithTokenStream = true;
        NumericField lastModField = new NumericField(Constants.FIELD_LAST_MODIFIED, Field.Store.YES, indexWithTokenStream);
        lastModField.setLongValue(lastModifiedDate);
        return lastModField;
    }

    public Field makeContentField(File i) throws FileNotFoundException {
        Field contentField = new Field(Constants.FIELD_CONTENT, new FileReader(i));
        return contentField;

    }

    public Field makeTitlefield(String title) {
        Field titleField = new Field(Constants.FIELD_TITLE, title, Field.Store.YES, Field.Index.ANALYZED);
        return titleField;
    }
}
