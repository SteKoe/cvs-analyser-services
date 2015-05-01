package de.josko.cvsanalyser.service;

import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.Vertex;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class CommitsPerCommitterService extends OrientDBService {
    private List<Map<String, Object>> result;
    private Map<String, Long> statistics;

    @RequestMapping("/commitsPerCommitter")
    public
    @ResponseBody
    List<Map<String, Object>> index() {
        openConnection();

        result = new ArrayList<>();
        statistics = new HashMap<>();


        String query = "SELECT committedBy, COUNT(committedBy) as commits FROM (SELECT revision, in('committed')[0].name as committedBy FROM Revision) GROUP BY committedBy";
        Iterable<Vertex> queryResult = (Iterable<Vertex>) orientGraph.command(new OCommandSQL(query)).execute();

        Iterator<Vertex> iterator = queryResult.iterator();
        while (iterator.hasNext()) {
            Vertex next = iterator.next();
            String committer = next.getProperty("committedBy");
            long commits = next.getProperty("commits");

            Map<String, Object> resultObject = new HashMap<>();
            resultObject.put("committer", committer);
            resultObject.put("commits", commits);

            result.add(resultObject);
        }

        closeConnection();

        return result;
    }
}