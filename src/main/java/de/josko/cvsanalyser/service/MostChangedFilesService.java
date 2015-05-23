package de.josko.cvsanalyser.service;

import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.Vertex;
import org.joda.time.DateTime;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class MostChangedFilesService extends OrientDBService {
    private List<Map<String, Object>> result;

    @RequestMapping("/mostChangedFiles")
    public
    @ResponseBody
    List<Map<String, Object>> index(@RequestParam(value = "limit", required = false, defaultValue = "10") String limit) {
        result = new ArrayList<>();

        openConnection();

        String query = "SELECT Class, in('contains').size() AS commits " +
                        "FROM Class " +
                        "ORDER BY commits DESC " +
                        "LIMIT " + limit;

        Iterable<Vertex> queryResult = (Iterable<Vertex>) orientGraph.command(new OCommandSQL(query)).execute();

        Iterator<Vertex> iterator = queryResult.iterator();
        while (iterator.hasNext()) {
            Vertex next = iterator.next();

            Map<String, Object> resultObject = new HashMap<>();
            resultObject.put("class", next.getProperty("Class"));
            resultObject.put("commits", next.getProperty("commits"));
            result.add(resultObject);
        }

        closeConnection();

        return result;
    }
}