/*package storage;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.StudyGroup;

import java.io.*;

import java.util.PriorityQueue;

public class XmlStorage {
    private final XmlMapper xmlMapper = new XmlMapper();
    private final File file;
    private final PriorityQueue<StudyGroup> group;

    public XmlStorage() throws IOException {
        xmlMapper.registerModule(new JavaTimeModule());
        xmlMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        file = new File ("C:\\Users\\serge\\IdeaProjects\\projectTwo\\server\\db\\studyGroups.xml");
        group = load();
    }

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

    public PriorityQueue<StudyGroup> getGroup() {
        return group;
    }
}*/
