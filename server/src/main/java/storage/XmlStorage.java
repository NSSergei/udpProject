package storage;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import model.StudyGroup;

import java.io.*;

import java.util.PriorityQueue;

public class XmlStorage {
    private final XmlMapper xmlMapper = new XmlMapper();
    private final File file = new File("db/XmlStorage.xml");

    public XmlStorage() throws IOException {
        xmlMapper.findAndRegisterModules();
    }

    private final PriorityQueue<StudyGroup> group = load();

    public PriorityQueue<StudyGroup> load() throws IOException {
        if (!file.exists()) {
            return new PriorityQueue<>();
        }
        return xmlMapper.readValue(
                file,
                new TypeReference<PriorityQueue<StudyGroup>>() {
                }
        );
    }

    public void save(PriorityQueue<StudyGroup> queue) throws IOException {
        xmlMapper.writeValue(file,queue);
    }
}
