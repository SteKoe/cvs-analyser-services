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
public class ChangedFilesPerCommitService extends OrientDBService {
    private List<Map<String, Object>> result;

    @RequestMapping("/changedFilesPerCommit")
    public
    @ResponseBody
    List<Map<String, Object>> index(@RequestParam(value = "limit", required = false, defaultValue = "10") String limit) {
        result = new ArrayList<>();

        openConnection();

        String query = "SELECT date, set(in('committedOn').revision)[0] AS revision, set(in('committedOn').out('contains')).size() AS changedFiles,  in('committedOn').message[0] AS message FROM Date GROUP BY date ORDER BY date DESC LIMIT " + limit;
        Iterable<Vertex> queryResult = (Iterable<Vertex>) orientGraph.command(new OCommandSQL(query)).execute();

        Iterator<Vertex> iterator = queryResult.iterator();
        while (iterator.hasNext()) {
            Vertex next = iterator.next();

            DateTime dateTime = DateTime.parse(next.getProperty("date").toString());
            String date = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM).format(dateTime.toDate());

            Map<String, Object> resultObject = new HashMap<>();
            resultObject.put("revision", next.getProperty("revision"));
            resultObject.put("changes", next.getProperty("changedFiles"));
            resultObject.put("message", next.getProperty("message"));
            resultObject.put("date", date);
            result.add(resultObject);
        }

        closeConnection();

        return result;
    }
}