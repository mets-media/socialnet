package socialnet.utils;

import lombok.Getter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import socialnet.exception.ParserException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class CbRfParser extends DefaultHandler {
    String element = "";

    @Getter
    String curValue;
    @Getter
    Boolean isCompleted = false;

    public static SAXParser createParser() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            return factory.newSAXParser();
        } catch (ParserConfigurationException | SAXException e) {
            throw new ParserException(e.getMessage());
        }
    }

    @Override
    public void startDocument() throws SAXException {
        isCompleted = false;
    }

    @Override
    public void endDocument() throws SAXException {
        isCompleted = true;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        element = qName;
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        element = "";
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (element.equals("Value")) {
            curValue = new String(ch, start, length);
        }
    }


}
