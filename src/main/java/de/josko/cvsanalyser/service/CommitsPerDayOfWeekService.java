package de.josko.cvsanalyser.service;

import com.orientechnologies.orient.core.db.record.OTrackedSet;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.Vertex;
import org.joda.time.DateTime;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class CommitsPerDayOfWeekService extends OrientDBService {
    private List<Map<String, Integer>> result;
    private Map<Integer, Integer> statistics;

    @RequestMapping("/commitsPerDayOfWeek")
    public @ResponseBody
    List<Map<String, Integer>> index() {
        openConnection();

        result = new ArrayList<>();
        statistics = new HashMap<>();

        String query = "SELECT revision, out_committedOn.date as date FROM Revision ORDER BY date DESC";
        Iterable<Vertex> queryResult = (Iterable<Vertex>) orientGraph.command(new OCommandSQL(query)).execute();

        Iterator<Vertex> iterator = queryResult.iterator();
        while(iterator.hasNext()) {
            Vertex next = iterator.next();
            OTrackedSet dates = next.getProperty("date");
            String dateTimeString = dates.iterator().next().toString();
            DateTime dateTime = DateTime.parse(dateTimeString);

            int dayOfWeek = dateTime.getDayOfWeek();

            int amount = 0;
            if(statistics.containsKey(dayOfWeek)) {
                amount = statistics.get(dayOfWeek);
            }
            statistics.put(dayOfWeek, amount + 1);
        }

        closeConnection();


        statistics.keySet().forEach(dayOfWeek -> {
            Map<String, Integer> resultObject = new HashMap<>();
            resultObject.put("dayOfWeek", dayOfWeek);
            resultObject.put("commits", statistics.get(dayOfWeek));
            result.add(resultObject);
        });

        return result;
    }
}