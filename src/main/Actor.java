package main;

import actor.ActorsAwards;
import common.Constants;
import utils.Utils;

import java.util.*;

public class Actor {
    private String name;
    private String careerDescription;
    private ArrayList<String> filmography;
    private Map<ActorsAwards, Integer> awards;

    @Override
    public String toString() {
        return "Actor{" +
                "name='" + name + '\'' +
                ", careerDescription='"
                + careerDescription + '\'' +
                ", filmography=" + filmography +
//                ", awards=" + awards +
                '}';
    }

    public Actor(String name, String careerDescription, ArrayList<String> filmography, Map<ActorsAwards, Integer> awards) {
        this.name = name;
        this.careerDescription = careerDescription;
        this.filmography = filmography;
        this.awards = new LinkedHashMap<ActorsAwards, Integer>();
        for (ActorsAwards actorsAwards : awards.keySet()) {
            this.awards.put(actorsAwards, awards.get(actorsAwards));
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCareerDescription() {
        return careerDescription;
    }

    public void setCareerDescription(String careerDescription) {
        this.careerDescription = careerDescription;
    }

    public ArrayList<String> getFilmography() {
        return filmography;
    }

    public void setFilmography(ArrayList<String> filmography) {
        this.filmography = filmography;
    }

    public Map<ActorsAwards, Integer> getAwards() {
        return awards;
    }

    public void setAwards(Map<ActorsAwards, Integer> awards) {
        this.awards = awards;
    }

    public static String getFilterDescription(ArrayList<Actor> actors, List<String> filters, String sortType) {
        ArrayList<String> actorsNames = new ArrayList<String>();
        for (Actor actor : actors) {
            actorsNames.add(actor.getName());
        }
        for (Actor actor : actors) {
            int isGood = 1;
            String[] actorDescription;
            actorDescription = actor.getCareerDescription().toLowerCase().split("[^a-zA-Z]");
            ArrayList<String> actorDescriptionArrayList = new ArrayList<>(Arrays.asList(actorDescription));
            for (String filter : filters) {
                if (!actorDescriptionArrayList.contains(filter)) {
                    isGood = 0;
                }
            }
            if (isGood == 0) {
                actorsNames.remove(actor.getName());
            }
        }

        Collections.sort(actorsNames);
        if (sortType.equals("desc")) {
            Collections.reverse(actorsNames);
        }

        return Constants.QUERY_RESULT + actorsNames.toString();
    }

    public static String getQueryAwards(ArrayList<Actor> actors, List<String> awards, String sortType) {
        LinkedHashMap<String, Integer> actorsAwards = new LinkedHashMap<>();

        for (Actor actor : actors) {
            int isGood = 1;

            for (String award : awards) {
                if (!actor.getAwards().containsKey(Utils.stringToAwards(award))) {
                    isGood = 0;
                }
            }

            if (isGood == 1) {
                int numberAwards = 0;
                for (ActorsAwards key : actor.getAwards().keySet()) {
                    numberAwards += actor.getAwards().get(key);
                }
                actorsAwards.put(actor.getName(), numberAwards);
            }
        }

        List<Map.Entry<String, Integer>> actorsSorted = new ArrayList<Map.Entry<String, Integer>>(actorsAwards.entrySet());

        Collections.sort(actorsSorted, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                if (o1.getValue() == o2.getValue()) {
                    return o1.getKey().compareTo(o2.getKey());
                } else {
                    return o1.getValue() - o2.getValue();
                }
            }
        });

        if (sortType.equals("desc")) {
            Collections.reverse(actorsSorted);
        }

        ArrayList<String> actorsName = new ArrayList<>();

        for (int i = 0; i < actorsSorted.size(); i++) {
            actorsName.add(actorsSorted.get(i).getKey());
        }

        return Constants.QUERY_RESULT + actorsName.toString();
    }
}
