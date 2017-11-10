package hw8.serializers;

import hw8.Main;
import hw8.XmlValidator;
import hw8.model.*;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class XmlSaxSerializer implements Serializer {

    private static final File schemaFile = new File(Main.class.getResource("scheme.xml").getFile());
    private static final String PUBLISHER_NAME="name";
    private static final String PUBLISHER_TITLE="Publisher";
    private static final String BOOKS_TITLE ="Books";
    private static final String BOOK_TITLE="Book";
    private static final String AUTHORS_TITLE="Authors";
    private static final String AUTHOR_TITLE="Author";
    private static final String BOOK_NAME="name";
    private static final String BOOK_YEAR="year";
    private static final String AUTHOR_NAME="name";
    private static final String AUTHOR_BIRTH="birth";
    private static final String AUTHOR_DEATH="death";
    private static final String AUTHOR_GENDER="gender";

    @Override
    public void serialize(Publisher p, File writeTo) throws IOException {
        throw new UnsupportedOperationException("This implementation is not intended for this purpose.");
    }

    @Override
    public Publisher deserialize(File readFrom) throws IOException {
        try {
            XmlValidator.validate(readFrom, schemaFile);
            SaxHandler saxHandler = new SaxHandler();
            XMLReader reader = XMLReaderFactory.createXMLReader();
            reader.setContentHandler(saxHandler);
            reader.parse(new InputSource(new FileInputStream(readFrom)));
            return saxHandler.getParsedPublisher();
        } catch(SAXException | IOException e) {
            System.out.println("Problem while deserialize file: " + e.getMessage());
            return null;
        }
    }

    private class SaxHandler extends DefaultHandler{

        private Publisher publisher;
        private List<Book> books;
        private List<Author> currentAuthors;
        private List<Author> authorsRead = new ArrayList<>();
        private String currentBookName;
        private Integer currentBookYear;
        private String currentAuthorName;
        private LocalDate currentAuthorBirth;
        private LocalDate currentAuthorDeath;
        private Author.Gender currentAuthorGender;
        private String publisherName;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if(PUBLISHER_TITLE.equals(localName)){
                publisherName = attributes.getValue(PUBLISHER_NAME);
            } else if (BOOKS_TITLE.equals(localName)){
                books = new ArrayList<>();
            } else if (BOOK_TITLE.equals(localName)){
                currentBookName = attributes.getValue(BOOK_NAME);
                currentBookYear = Integer.valueOf(attributes.getValue(BOOK_YEAR));
            } else if (AUTHORS_TITLE.equals(localName)){
                currentAuthors = new ArrayList<>();
            } else if (AUTHOR_TITLE.equals(localName)){
                currentAuthorName = attributes.getValue(AUTHOR_NAME);
                currentAuthorGender = defineGender(attributes.getValue(AUTHOR_GENDER));
                currentAuthorBirth = LocalDate.parse(attributes.getValue(AUTHOR_BIRTH)
                        , DateTimeFormatter.ISO_LOCAL_DATE);
                String deathStr = attributes.getValue(AUTHOR_DEATH);
                currentAuthorDeath = (deathStr==null)
                        ? null
                        : LocalDate.parse(deathStr, DateTimeFormatter.ISO_LOCAL_DATE);
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (BOOK_TITLE.equals(localName)) {
                Book b = new Book(currentBookName, currentBookYear, currentAuthors);
                books.add(b);
            } else if (AUTHOR_TITLE.equals(localName)) {
                Author a = new Author(currentAuthorName, currentAuthorBirth, currentAuthorDeath, currentAuthorGender);
                currentAuthors.add(getReadAuthor(a));
            } else if (PUBLISHER_TITLE.equals(localName)){
                publisher = new Publisher(publisherName, books);
            }
        }

        /**
         * Метод избегания дублирования объектов при десериализации
         * @param author Добавляемый автор
         * @return Ссылку на автора, если он был прочтён ранее,
         * либо текущий параметр, если он встречен в документе впервые
         */
        Author getReadAuthor(Author author){
            for (Author a : authorsRead){
                if (a.equals(author)) return a;
            }
            return author;
        }

        /**
         * Очистка списка прочтённых авторов
         */
        private void dataFlush(){
            authorsRead.clear();
        }

        private Author.Gender defineGender(String s){
            if ("male".equals(s)) return Author.Gender.MALE;
            else if ("female".equals(s)) return Author.Gender.FEMALE;
            else return null;
        }

        public Publisher getParsedPublisher(){
            return publisher;
        }
    }

}