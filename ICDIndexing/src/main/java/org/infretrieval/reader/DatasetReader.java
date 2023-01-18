package org.infretrieval.reader;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.infretrieval.model.ICDCodeEntry;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

@RequiredArgsConstructor
public class DatasetReader implements Iterable<ICDCodeEntry> {
    private final String filePath;

    @SneakyThrows
    @Override
    public Iterator<ICDCodeEntry> iterator() {
        var fileStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
        if(fileStream == null) {
            throw new IllegalArgumentException("No dataset file found!");
        }

        var parser = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .build()
                .parse(new InputStreamReader(fileStream));

        return new DatasetIterator(parser);
    }

    @RequiredArgsConstructor
    public static class DatasetIterator implements Iterator<ICDCodeEntry>, Closeable {
        private final CSVParser parser;

        @Override
        public boolean hasNext() {
            return parser.iterator().hasNext();
        }

        @Override
        public ICDCodeEntry next() {
            var record = parser.iterator().next();
            return new ICDCodeEntry(record.getRecordNumber(), record.get(0),
                    record.get(0).substring(0, 3), record.get(1));
        }

        @Override
        public void close() throws IOException {
            if(!parser.isClosed()) {
                parser.close();
            }
        }
    }
}
