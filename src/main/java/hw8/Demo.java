package hw8;

import hw8.model.Author;
import hw8.model.Book;
import hw8.model.Publisher;
import hw8.serializers.Serializer;
import hw8.serializers.XmlDomSerializer;
import hw8.serializers.XmlSaxSerializer;

import java.io.File;
import java.io.IOException;

public class Demo {

    private static final String XML_PATH = Main.class.getResource("info.xml").getFile();
    private static final String XSD_PATH = Main.class.getResource("scheme.xml").getFile();

    public void launch(){
        File schemaFile = new File(XSD_PATH);
        File infoFile = new File(XML_PATH);
        Serializer serializer = new XmlDomSerializer();
        try {
            System.out.println("==== Десериализация DOM ====");
            Publisher p = serializer.deserialize(infoFile);
            printPublisher(p);
            System.out.println("==== Десериализация SAX ====");
            serializer = new XmlSaxSerializer();
            p = serializer.deserialize(infoFile);
            printPublisher(p);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void printPublisher(Publisher p){
        System.out.println("Издательство:\nНазвание: " + p.getName() );
        System.out.println("Список книг:");
        for(Book b : p.getPublishedBooks()){
            System.out.println("\nКнига:\nНазвание: " + b.getName() + "\nГод издательства: " + b.getPublishedYear());
            System.out.println("Список авторов:");
            for(Author a : b.getAuthors()){
                System.out.println("Автор:\nИмя: " + a.getName() + "\nПол: " + a.getGender()
                        +"\nДата рождения: " + a.getBirthDate() + "\nДата смерти: "
                + ((a.getDeathDate()==null)? "-" : a.getDeathDate()) );
            }
        }
    }

}
