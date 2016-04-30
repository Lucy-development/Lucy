package pdfcreator;

import com.lowagie.text.DocumentException;
import lucytest.Report;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Class for creating PDF for results.
 * <p>
 * Uses Flying Saucer library (available at: https://github.com/flyingsaucerproject/flyingsaucer)
 */

public class CreatePDF {

    /**
     * Method creates results PDF based on inserted object Report.
     *
     * @return pdf as bytes
     */
    public byte[] createResultsPDF(Report report) throws ParserConfigurationException, DocumentException, SAXException, XPathExpressionException, IOException, URISyntaxException {
        return convertDocumentToPDF(createResultsPDFHelper(report));
    }

    /**
     * Method creates results PDF based on inserted object Report.
     * <p>
     * PDF will be created according to the given filepath (output)
     */
    public void createResultsPDF(Report report, String outputPath) throws ParserConfigurationException, DocumentException, SAXException, XPathExpressionException, IOException, URISyntaxException {
        convertDocumentToPDF(createResultsPDFHelper(report), outputPath);
    }

    /**
     * Method returns Document with values that must be in the pdf.
     */
    private Document createResultsPDFHelper(Report report) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, URISyntaxException {
        File xhtmlFile = new File(CreatePDF.class.getClassLoader().getResource("results.xhtml").toURI());
        Document document = convertXMLtoDocument(xhtmlFile);
        insertDatabaseObjectIntoDocument(document, getInsertableValues(report));
        return document;
    }


    /**
     * Private method for converting Document to the PDF and returning it as byte[].
     *
     * @param document to be converted to PDF
     * @return pdf as bytes.
     */
    private byte[] convertDocumentToPDF(Document document) throws DocumentException, IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            SharedContext sharedContext = renderer.getSharedContext();
            setSharedContext(sharedContext);

            renderer.setDocument(document, null);
            renderer.layout();
            renderer.createPDF(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }
    }


    /**
     * Private method for converting Document to the PDF and writing on the disk.
     *
     * @param document   to be converted into pdf
     * @param outputPath - where the pdf will be created
     */
    private void convertDocumentToPDF(Document document, String outputPath) throws IOException, DocumentException {
        ITextRenderer renderer = new ITextRenderer();
        SharedContext sharedContext = renderer.getSharedContext();
        setSharedContext(sharedContext);
        renderer.setDocument(document, null);

        try (OutputStream os = new FileOutputStream(outputPath)) {
            renderer.layout();
            renderer.createPDF(os);
        }
    }


    /**
     * Method returns List containing all the objects that contain data that will be entered to the final pdf.
     */
    private List<WritableToXML> getInsertableValues(Report report) {
        return new ArrayList<>(Collections.singletonList(report));
    }


    private void setSharedContext(SharedContext sharedContext) {
        sharedContext.setPrint(true);
        sharedContext.setInteractive(false);
        sharedContext.getTextRenderer().setSmoothingThreshold(0);
    }


    /**
     * Method converts XHTML to a Document format. Original XHTML file will not be changed during the process.
     */
    private Document convertXMLtoDocument(File xhtml) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        // http://stackoverflow.com/questions/10728909/infinite-loop-while-parsing-xhtml-using-documentbuilder-parse
        docFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        return docBuilder.parse(xhtml);
    }


    /**
     * Method inserts all the data from elements (int the list) into the document.
     */
    private Document insertDatabaseObjectIntoDocument(Document document, List<WritableToXML> objectsToInsert) throws IOException, XPathExpressionException {
        for (WritableToXML element : objectsToInsert) {
            if (element == null) continue;
            Map<String, String> idMap = element.getContentForXML();
            idMap.keySet().forEach(id -> updateDocument(document, id, idMap.get(id)));
        }
        return document;
    }


    /**
     * Method finds  and returns node by ID (in the document).
     */
    private Node getNodeXML(Document document, String id) throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        return (Node) xpath.evaluate("//*[@id='" + id + "']", document, XPathConstants.NODE);
    }


    /**
     * @param document - which content will be updated
     * @param id       - the id of the row that content will be updated
     * @param content  - that will be inserted into the document
     */
    private void updateDocument(Document document, String id, String content) {
        try {
            Node node = getNodeXML(document, id);
            // If id does not exist
            if (node == null) {
                System.err.print("UpdateXML - detected null Node, check inserted content idMap and base XML");
                return;
            }
            node.setTextContent(content);
        } catch (XPathExpressionException e) {
            //Siis on see hea lahendus, kui programm ei suuda jätkata pärast seda erindit.
            // Kui aga kuidagi saab, siis võiks logida ja uuesti proovida.
            // Papli: Ja siis pärast n-ndat katset crash&burnida.
            throw new RuntimeException(e);
        }
    }


}
