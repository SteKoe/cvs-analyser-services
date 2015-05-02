package de.josko.cvsanalyser.service;

import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.Vertex;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class CommitMessageWordCloudService extends OrientDBService {
    private List<Map<String, Object>> result;
    private Map<String, Integer> statistics;

    private static final List<String> ENGLISH_STOP_WORDS = Arrays.asList(
            "a", "an", "and", "are", "as", "at", "be", "but", "by",
            "for", "if", "in", "into", "is", "it",
            "no", "not", "of", "on", "or", "such",
            "that", "the", "their", "then", "there", "these",
            "they", "this", "to", "was", "will", "with"
    );

    private static final List<String> GERMAN_STOP_WORDS = Arrays.asList(
            "einer", "eine", "eines", "einem", "einen",
            "keine", "auch", "kann", "beim", "sich", "sind",
            "nur", "ein", "und", "ob", "so", "muss",
            "dem", "des", "da", "bei", "nach", "nun",
            "werden", "nicht", "den", "wenn", "vom", "über", "hat",
            "der", "die", "das", "dass", "daß",
            "du", "er", "sie", "es",
            "was", "wer", "wie", "wir",
            "und", "oder", "ohne", "mit",
            "am", "im", "in", "aus", "auf",
            "ist", "sein", "war", "wird",
            "ihr", "ihre", "ihres",
            "als", "für", "von", "mit",
            "dich", "dir", "mich", "mir",
            "mein", "sein", "kein",
            "durch", "wegen", "wird"
    );

    @RequestMapping("/commitMessageWordCloud")
    public @ResponseBody List<Map<String, Object>> index() {
        openConnection();

        result = new ArrayList<>();
        statistics = new TreeMap<>();

        String query = "SELECT message FROM Revision";
        Iterable<Vertex> queryResult = (Iterable<Vertex>) orientGraph.command(new OCommandSQL(query)).execute();

        Iterator<Vertex> iterator = queryResult.iterator();
        while (iterator.hasNext()) {
            Vertex next = iterator.next();
            String message = next.getProperty("message");

            if(message != null) {
                Arrays.stream(message.split("\\s"))
                        .forEach(word -> {
                            word = word.replaceAll("\\.|:|,|;|\\(|\\)", "");

                            boolean isEnglishStopWord = ENGLISH_STOP_WORDS.contains(word.toLowerCase());
                            boolean isGermanStopWord = GERMAN_STOP_WORDS.contains(word.toLowerCase());
                            if(isEnglishStopWord == false && isGermanStopWord == false) {
                                int amount = 0;
                                if (statistics.containsKey(word)) {
                                    amount = statistics.get(word);
                                }
                                statistics.put(word, amount + 1);
                            }
                        });
            }
        }

        statistics = sortByValues(statistics);

        statistics.keySet().forEach(word -> {
            Map<String, Object> resultObject = new HashMap<>();
            resultObject.put("word", word);
            resultObject.put("count", statistics.get(word));

            result.add(resultObject);
        });

        closeConnection();

        return result;
    }

    public static <K extends Comparable,V extends Comparable> Map<K,V> sortByValues(Map<K,V> map){
        List<Map.Entry<K,V>> entries = new LinkedList<Map.Entry<K,V>>(map.entrySet());

        Collections.sort(entries, new Comparator<Map.Entry<K,V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return o1.getValue().compareTo(o2.getValue()) * -1;
            }
        });

        //LinkedHashMap will keep the keys in the order they are inserted
        //which is currently sorted on natural ordering
        Map<K,V> sortedMap = new LinkedHashMap<K,V>();

        for(Map.Entry<K,V> entry: entries){
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
}