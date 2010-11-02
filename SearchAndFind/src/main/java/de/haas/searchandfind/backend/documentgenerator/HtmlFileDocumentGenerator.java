package de.haas.searchandfind.backend.documentgenerator;

import de.haas.searchandfind.backend.Backend;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.htmlparser.jericho.CharacterReference;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTagType;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

/**
 *
 * @author Michael Haas <haas@cl.uni-heidelberg.de>
 */
public class HtmlFileDocumentGenerator extends FileDocumentGenerator {

    // This class borrows heavily from the examples found at
    // http://jericho.htmlparser.net/docs/javadoc/
    // Thanks for the great examples!

    private static final Logger l = Logger.getLogger("HtmlFileDocumentGenerator");

    @Override
    public Document makeDocument(File i) {

        InputStream is = null;

        try {

            is = new FileInputStream(i);

            Document doc = new Document();
            l.info("Processing file: " + i.getPath());
            Field fileNameField = super.makeFileNameField(i.getPath());
            doc.add(fileNameField);
            NumericField lastModField = super.makeLastModifiedField(i.lastModified());
            doc.add(lastModField);
            // HTML parsing
            Source source = new Source(is);
            source.fullSequentialParse();
            Element titleElement = source.getFirstElement(HTMLElementName.TITLE);
            if (titleElement != null) {
                String title = CharacterReference.decodeCollapseWhiteSpace(titleElement.getContent());
                l.info("Document title is: " + title);
                Field titlefield = super.makeTitlefield(title);
                doc.add(titlefield);
            } else {
                l.info("HTML document does not have title element");
            }

            // Meta data
            StringBuffer keyWordsBuffer = new StringBuffer();
            List<Element> metaTags = source.getAllElements(HTMLElementName.META);
            for (Element metaTag : metaTags) {
                String name = metaTag.getAttributeValue("name");
                if (name != null && name.equals("keywords")) {
                    String keyWords = metaTag.getAttributeValue("content");
                    if (keyWords != null) {
                        l.info("Document meta keywords: " + keyWords);
                        keyWordsBuffer.append(keyWords);
                        break;
                    }
                }
            }

            String completeContent = source.getTextExtractor().toString();
            Field contentField = super.makeContentField(completeContent);
            doc.add(contentField);

            // headers
            // we add these as FIELD_KEYWORDS
            List<Element> headers = source.getAllElements(HTMLElementName.H1);
            headers.addAll(source.getAllElements(HTMLElementName.H2));
            headers.addAll(source.getAllElements(HTMLElementName.H3));
            headers.addAll(source.getAllElements(HTMLElementName.H4));
            headers.addAll(source.getAllElements(HTMLElementName.H5));
            headers.addAll(source.getAllElements(HTMLElementName.H6));


            for (Element header : headers) {
                // TODO: check for null?
                // TODO: strip elements inside h element!
                String heading = CharacterReference.decodeCollapseWhiteSpace(header.getContent());
                keyWordsBuffer.append(heading);
                keyWordsBuffer.append(",");
            }

            Field keyWordsField = super.makeKeywordsField(keyWordsBuffer.toString());
            doc.add(keyWordsField);


            return doc;
        } catch (IOException ex) {
            Logger.getLogger(HtmlFileDocumentGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
                Logger.getLogger(HtmlFileDocumentGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return null;
    }
}
