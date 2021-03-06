package de.josko.cvsanalyser.service;

import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.Vertex;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class CommitterPerFileService extends OrientDBService {
    private List<Map<String, Object>> result;
    private Map<String, Integer> statistics;

    @RequestMapping("/committerPerFile")
    public  @ResponseBody  List<Map<String, Object>> index(@RequestParam(value = "limit", required = false, defaultValue = "10") String limit) {
        openConnection();

        result = new ArrayList<>();
        statistics = new LinkedHashMap<>();

        // FIXME: What about SQL injection?!
        String query = "SELECT *, set(in('contains').in('committed').Committer).asSet().size() as Committer " +
                "FROM Class " +
                "WHERE Class LIKE '%.java' " +
                "GROUP BY Class " +
                "ORDER BY Committer DESC " +
                "LIMIT " + limit;

        Iterable<Vertex> queryResult = (Iterable<Vertex>) orientGraph.command(new OCommandSQL(query)).execute();

        Iterator<Vertex> iterator = queryResult.iterator();
        while (iterator.hasNext()) {
            Vertex next = iterator.next();

            int committer = next.getProperty("Committer");

            statistics.put(next.getProperty("Class").toString(), committer);
        }

        closeConnection();

        statistics.keySet().forEach(file -> {
            Map<String, Object> resultObject = new HashMap<>();
            resultObject.put("file", file);
            resultObject.put("committer", statistics.get(file));

            result.add(resultObject);
        });

        return result;
    }
}