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
public class CommitsPerHourService extends OrientDBService {
    private List<Map<String, Integer>> result;
    private Map<Integer, Integer> statistics;

    @RequestMapping("/commitsPerHour")
    public
    @ResponseBody
    List<Map<String, Integer>> index() {
        result = new ArrayList<>();
        statistics = new HashMap<>();

        openConnection();

        String query = "SELECT revision, out_committedOn.date as date FROM Revision ORDER BY date DESC";
        Iterable<Vertex> queryResult = (Iterable<Vertex>) orientGraph.command(new OCommandSQL(query)).execute();

        Iterator<Vertex> iterator = queryResult.iterator();
        while (iterator.hasNext()) {
            Vertex next = iterator.next();
            OTrackedSet dates = next.getProperty("date");
            String dateTimeString = dates.iterator().next().toString();
            DateTime dateTime = DateTime.parse(dateTimeString);

            int hour = dateTime.getHourOfDay();

            int amount = 0;
            if (statistics.containsKey(hour)) {
                amount = statistics.get(hour);
            }
            statistics.put(hour, amount + 1);
        }

        closeConnection();


        statistics.keySet().forEach(hour -> {
            Map<String, Integer> asd = new HashMap<>();
            asd.put("hour", hour);
            asd.put("commits", statistics.get(hour));
            result.add(asd);
        });

        return result;
    }
}