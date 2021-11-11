import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        //Задача 1: CSV - JSON
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        // читаем файл csv
        List<Employee> list = parseCSV(columnMapping, fileName);
        // полученный список преобразуем в формат json
        String json = listToJson(list);
        String jsonFile = "data.json";
        // запись json в файл
        writeString(json, jsonFile);

        //Задача 2: XML - JSON
        String xmlFile = "data.xml";
        // читаем файл xml
        List<Employee> list2 = parseXML(xmlFile);
        // полученный список преобразуем в формат json
        String json2 = listToJson(list2);
        String jsonFile2 = "data2.json";
        // запись json в файл
        writeString(json2, jsonFile2);
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> staff = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            staff = csv.parse();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return staff;
    }

    public static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    public static void writeString(String json, String jsonFile) {
        try (FileWriter file = new FileWriter(jsonFile)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Employee> parseXML(String xmlFile) throws ParserConfigurationException, IOException, org.xml.sax.SAXException {
        List<Employee> list3 = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(xmlFile));
        NodeList nodeList = doc.getElementsByTagName("employee");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node elementNodeList = nodeList.item(i);
            if (elementNodeList.getNodeType() == Node.ELEMENT_NODE) {
                Element employeeElement = (Element) elementNodeList;
                list3.add(new Employee(
                        Integer.parseInt(employeeElement.getElementsByTagName("id").item(0).getTextContent()),
                        employeeElement.getElementsByTagName("firstName").item(0).getTextContent(),
                        employeeElement.getElementsByTagName("lastName").item(0).getTextContent(),
                        employeeElement.getElementsByTagName("country").item(0).getTextContent(),
                        Integer.parseInt(employeeElement.getElementsByTagName("age").item(0).getTextContent())));
            }
        }
        return list3;
    }
}



