/**
 *
 * Copyright (c) 2009-2013 Freedomotic team
 * http://freedomotic.com
 *
 * This file is part of Freedomotic
 *
 * This Program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This Program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Freedomotic; see the file COPYING.  If not, see
 * <http://www.gnu.org/licenses/>.
 */
package it.freedomotic.util;

import it.freedomotic.app.Freedomotic;

import org.w3c.dom.Document;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class DOMValidateDTD {

    public static String validate(File xmlFile, String absolutePathToDtd)
            throws IOException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder;

            documentBuilder = factory.newDocumentBuilder();

            Document doc = documentBuilder.parse(xmlFile);
            DOMSource source = new DOMSource(doc);

            //now use a transformer to add the DTD element declaration at top
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();

            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,
                    new File(absolutePathToDtd).getAbsolutePath());

            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            transformer.transform(source, result);

            //create a validating factory with error handling
            factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(true);
            documentBuilder = factory.newDocumentBuilder();
            documentBuilder.setErrorHandler(new org.xml.sax.ErrorHandler() {
                public void fatalError(SAXParseException fatal)
                        throws SAXException {
                    //enable when validator feature is fully implemented
                    //LOG.warning(fatal.getMessage());
                }

                public void error(SAXParseException e)
                        throws SAXParseException {
                    //enable when validator feature is fully implemented
                    //LOG.warning("Error at line " + e.getLineNumber() + ". " + e.getMessage());
                }

                public void warning(SAXParseException err)
                        throws SAXParseException {
                    //enable when validator feature is fully implemented
                    //LOG.warning("Warning at line " + err.getLineNumber() + ". " + err.getMessage());
                }
            });
            //finally parse the result. 
            //this will throw an exception if the doc is invalid
            documentBuilder.parse(new InputSource(new StringReader(writer.toString())));

            return writer.toString();
        } catch (SAXException ex) {
            throw new RuntimeException(ex);
        } catch (ParserConfigurationException ex) {
            throw new RuntimeException(ex);
        } catch (TransformerConfigurationException ex) {
            throw new RuntimeException(ex);
        } catch (TransformerException ex) {
            throw new RuntimeException(ex);
        }
    }

    private DOMValidateDTD() {
    }
    private static final Logger LOG = Logger.getLogger(DOMValidateDTD.class.getName());
}
