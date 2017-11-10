package hw8;

import org.xml.sax.SAXException;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Класс, предназначенный для проверки валидности XML файлов в соответствии с XSD схемой
 */
public class XmlValidator {

    public static void validate(File infoFile, File schemaFile) throws IOException, SAXException {
        try (FileInputStream in = new FileInputStream(infoFile)) {
            Schema schema = (SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)).newSchema(schemaFile);
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(in));
        }
    }
}