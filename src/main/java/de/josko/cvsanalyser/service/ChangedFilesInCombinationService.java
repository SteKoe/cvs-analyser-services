package de.josko.cvsanalyser.service;

import com.orientechnologies.orient.core.db.record.OTrackedList;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.Vertex;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class ChangedFilesInCombinationService extends OrientDBService {
    private List<Map<String, Object>> result;
    private FilePairCounter fpc = new FilePairCounter();

    @RequestMapping("/changedFilesInCombination")
    public
    @ResponseBody
    List<Map<String, Object>> index(@RequestParam(value = "limit", required = false, defaultValue = "10") String limit) {
        result = new ArrayList<>();

        openConnection();

        String query = "select Revision, out('contains').Class as classes from Revision "
                + "ORDER BY Revision ASC "
                + "LIMIT " + limit;

        Iterable<Vertex> queryResult = (Iterable<Vertex>) orientGraph.command(new OCommandSQL(query)).execute();

        Iterator<Vertex> iterator = queryResult.iterator();
        while (iterator.hasNext()) {
            Vertex vertex = iterator.next();
            String revision = vertex.getProperty("Revision");
            OTrackedList<String> classes = vertex.getProperty("classes");
            List<String> collect = classes.stream().filter(clazz -> {
                return clazz.endsWith(".java") || clazz.endsWith(".pom");
            }).collect(Collectors.toList());

            if(collect.size() > 1) {
                for(int i = 0; i < collect.size(); i++) {
                    for(int j = i + 1; j < collect.size(); j++) {
                        fpc.add(collect.get(i), collect.get(j));
                    }
                }
            }
        }

        fpc.getList().keySet().stream().forEach(key -> {
            Map<String, Object> resultObject = new HashMap<>();
            resultObject.put("key", key);
            resultObject.put("value", fpc.getList().get(key));

            result.add(resultObject);
        });

        closeConnection();

        return result;
    }
}