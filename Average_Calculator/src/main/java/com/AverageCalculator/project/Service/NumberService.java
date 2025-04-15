package com.AverageCalculator.project.Service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Array;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NumberService {

    private final int win_size = 10;
    private final Map<String, Deque<Integer>> win_map = new ConcurrentHashMap<>();

    private final Map<String, String> apis = Map.of(
            "p", "http://20.244.56.144/evaluation-service/primes",
            "f", "http://20.244.56.144/evaluation-service/fibo",
            "e", "http://20.244.56.144/evaluation-service/even",
            "r", "http://20.244.56.144/evaluation-service/rand"
    );

    public Map<String, Object> process_req(String s) {
        Map<String, Object> res = new HashMap<>();

        if (!apis.containsKey(s)) {
            res.put("error", "Invalid number type");
            return res;
        }

        Deque<Integer> sliding = win_map.getOrDefault(s, new ArrayDeque<>());

        List<Integer> prev_win = new ArrayList<>(sliding);
        List<Integer> fetched = fetchFromThirdParty(apis.get(s));
        List<Integer> post_win = new ArrayList<>();

        for (int num : fetched) {
            if (!sliding.contains(num)) {
                sliding.addLast(num);
                post_win.add(num);

                if (sliding.size() > win_size) {
                    sliding.removeFirst();
                }
            }
        }

        win_map.put(s, sliding);

        double avg = sliding.stream().mapToInt(i -> i).average().orElse(0.0);

        res.put("windowprevState", prev_win);
        res.put("windowsCurrState", new ArrayList<>(sliding));
        res.put("numbers", post_win);
        res.put("avg", Math.round(avg * 100.0) / 100.0);

        return res;
    }

    private List<Integer> fetchFromThirdParty(String numberType) {
        String url = "http://20.244.56.144/evaluation-service/" + numberType;
        RestTemplate restTemplate = new RestTemplate();

        // Add the authorization header
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer <YOUR_TOKEN_HERE>");  // Replace with your real token

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            Map<String, Object> body = response.getBody();

            if (body != null && body.containsKey("numbers")) {
                return (List<Integer>) body.get("numbers");
            }
        } catch (Exception e) {
            System.out.println("Error fetching from test API: " + e.getMessage());
        }

        return Collections.emptyList();
    }

}
