package hw8.serializers;

import hw8.Main;
import hw8.XmlValidator;
import hw8.model.Author;
import hw8.model.Book;
import hw8.model.Publisher;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализация XML десериализации издательства
 */
public class XmlDomSerializer implements Serializer {

    private static final File schemaFile = new File(Main.class.getResource("scheme.xml").getFile());
    private static final String PUBLISHER_NAME="name";
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
    /**
     * Список прочтённых авторов для избежания дублирования объектов при десериализации
     */
    private List<Author> authorsRead = new ArrayList<>();

    @Override
    public void serialize(Publisher p, File writeTo) throws IOException {
        throw new UnsupportedOperationException("This implementation is not intended for this purpose.");
    }

    /**
     * Десериализация издательства их XML-файла
     * @param readFrom файл-источник
     * @return Соответствующее издательство
     * @throws IOException
     */
    @Override
    public Publisher deserialize(File readFrom) throws IOException {
        try {
            XmlValidator.validate(readFrom, schemaFile);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(readFrom);
            Element root = doc.getDocumentElement();
            String publisherName = root.getAttribute(PUBLISHER_NAME);
            Node booksNode = root.getElementsByTagName(BOOKS_TITLE).item(0);
            List<Book> books = readBooks(booksNode);
            dataFlush();
            return new Publisher(publisherName, books);
        } catch (SAXException | ParserConfigurationException e) {
            throw new RuntimeException("Problem while deserialize publisher: " + e.getClass().getName()+ ": "
                    + e.getMessage());
        }
    }

    /**
     * Чтение списка книг
     * @param booksNode корневой элемент списка книг
     * @return список книг
     */
    private List<Book> readBooks(Node booksNode){
        NodeList booksNodeList = booksNode.getChildNodes();
        List<Book> res = new ArrayList<>();
        for (int i = 0; i < booksNodeList.getLength(); i++) {
            Node node = booksNodeList.item(i);
            if (BOOK_TITLE.equals(node.getNodeName())){
                String bookName = node.getAttributes().getNamedItem(BOOK_NAME).getNodeValue();
                Integer bookYear = Integer.valueOf(node.getAttributes().getNamedItem(BOOK_YEAR).getNodeValue());
                int j = 0;
                Node authorsNode = node.getChildNodes().item(j++);
                while (!AUTHORS_TITLE.equals(authorsNode.getNodeName())){
                    authorsNode = node.getChildNodes().item(j++);
                }
                List<Author> authors = readAuthors(authorsNode);
                res.add(new Book(bookName,bookYear,authors));
            }
        }
        return res;
    }

    /**
     * Чтение списка авторов
     * @param authorsNode корневой элемент списка авторов
     * @return список авторов
     */
    private List<Author> readAuthors(Node authorsNode){
        NodeList authorsNodeList = authorsNode.getChildNodes();
        List<Author> res = new ArrayList<>();
        for (int i = 0; i < authorsNodeList.getLength(); i++) {
            Node node = authorsNodeList.item(i);
            if (AUTHOR_TITLE.equals(node.getNodeName())){
                String authorName = node.getAttributes().getNamedItem(AUTHOR_NAME).getNodeValue();
                String birthStr = node.getAttributes().getNamedItem(AUTHOR_BIRTH).getNodeValue();
                LocalDate birth = LocalDate.parse(birthStr, DateTimeFormatter.ISO_LOCAL_DATE);
                Node deathAttr = node.getAttributes().getNamedItem(AUTHOR_DEATH);
                LocalDate death = null;
                if (deathAttr!=null) {
                    death = LocalDate.parse(deathAttr.getNodeValue(), DateTimeFormatter.ISO_LOCAL_DATE);
                }
                String genderStr = node.getAttributes().getNamedItem(AUTHOR_GENDER).getNodeValue();
                Author.Gender gender = defineGender(genderStr);
                Author currentAuthor = new Author(authorName,birth,death,gender);
                res.add(currentAuthor);
            }
        }
        return res;
    }

    private Author.Gender defineGender(String s){
        if ("male".equals(s)) return Author.Gender.MALE;
        else if ("female".equals(s)) return Author.Gender.FEMALE;
        else return null;
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

}