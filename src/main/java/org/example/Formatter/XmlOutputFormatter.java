package org.example.Formatter;

import org.example.FileStats;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class XmlOutputFormatter implements OutputFormatter{
    @XmlRootElement(name = "stats")
    public static class FileStatsWrapper {
        private List<FileStats> stats;

        public FileStatsWrapper() {
        }

        public FileStatsWrapper(List<FileStats> stats) {
            this.stats = stats;
        }

        @XmlElement(name = "fileStat")
        public List<FileStats> getStats() {
            return stats;
        }

        public void setStats(List<FileStats> stats) {
            this.stats = stats;
        }
    }

    public void format(Map<String, FileStats> stats) {
        try {
            List<FileStats> fileStatsList = stats.values().stream().collect(Collectors.toList());
            FileStatsWrapper wrapper = new FileStatsWrapper(fileStatsList);
            JAXBContext context = JAXBContext.newInstance(FileStatsWrapper.class, FileStats.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            StringWriter writer = new StringWriter();
            marshaller.marshal(wrapper, writer);
            System.out.println(writer.toString());
        } catch (JAXBException e) {
            throw new RuntimeException("Error formatting XML output", e);
        }
    }
}
