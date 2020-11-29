package main;

import actor.ActorsAwards;
import common.Constants;
import utils.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Arrays;

public class Actor {
    private String name;
    private String careerDescription;
    private ArrayList<String> filmography;
    private Map<ActorsAwards, Integer> awards;

    public Actor(final String name, final String careerDescription,
                 final ArrayList<String> filmography,
                 final Map<ActorsAwards, Integer> awards) {
        this.name = name;
        this.careerDescription = careerDescription;
        this.filmography = filmography;
        this.awards = new LinkedHashMap<ActorsAwards, Integer>();
        for (ActorsAwards actorsAwards : awards.keySet()) {
            this.awards.put(actorsAwards, awards.get(actorsAwards));
        }
    }

    public final String getName() {
        return name;
    }

    public final void setName(final String name) {
        this.name = name;
    }

    public final String getCareerDescription() {
        return careerDescription;
    }

    public final void setCareerDescription(final String careerDescription) {
        this.careerDescription = careerDescription;
    }

    public final ArrayList<String> getFilmography() {
        return filmography;
    }

    public final void setFilmography(final ArrayList<String> filmography) {
        this.filmography = filmography;
    }

    public final Map<ActorsAwards, Integer> getAwards() {
        return awards;
    }

    public final void setAwards(final Map<ActorsAwards, Integer> awards) {
        this.awards = awards;
    }

    /**
     * @param actors
     * @param filters
     * @param sortType
     * @return
     */
    public static String getFilterDescription(final ArrayList<Actor> actors,
                                              final List<String> filters, final String sortType) {
        ArrayList<String> actorsNames = new ArrayList<String>();
        for (Actor actor : actors) {
            actorsNames.add(actor.getName());
        }
        for (Actor actor : actors) {
            int isGood = 1;
            String[] actorDescription;
            actorDescription = actor.getCareerDescription().toLowerCase().split("[^a-zA-Z]");
            ArrayList<String> actorDescriptionArrayList
                    = new ArrayList<>(Arrays.asList(actorDescription));
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
        if (sortType.equals(Constants.DESC)) {
            Collections.reverse(actorsNames);
        }

        return Constants.QUERY_RESULT + actorsNames.toString();
    }

    /**
     * @param actors
     * @param awards
     * @param sortType
     * @return
     */
    public static String getQueryAwards(final ArrayList<Actor> actors,
                                        final List<String> awards, final String sortType) {
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

        List<Map.Entry<String, Integer>> actorsSorted
                = new ArrayList<Map.Entry<String, Integer>>(actorsAwards.entrySet());

        Collections.sort(actorsSorted, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(final Map.Entry<String, Integer> o1,
                               final Map.Entry<String, Integer> o2) {
                if (o1.getValue().equals(o2.getValue())) {
                    return o1.getKey().compareTo(o2.getKey());
                } else {
                    return o1.getValue() - o2.getValue();
                }
            }
        });

        if (sortType.equals(Constants.DESC)) {
            Collections.reverse(actorsSorted);
        }

        ArrayList<String> actorsName = new ArrayList<>();

        for (int i = 0; i < actorsSorted.size(); i++) {
            actorsName.add(actorsSorted.get(i).getKey());
        }

        return Constants.QUERY_RESULT + actorsName.toString();
    }

    /**
     * @param actorsLinkedHashMap
     * @param sortType
     * @param n
     * @return
     */
    private static ArrayList<String> getResultAverage(
            final LinkedHashMap<String, Double> actorsLinkedHashMap,
            final String sortType, final int n) {
        List<Map.Entry<String, Double>> actorsSorted
                = new ArrayList<Map.Entry<String, Double>>(actorsLinkedHashMap.entrySet());
        ArrayList<String> actorsNames = new ArrayList<String>();

        Collections.sort(actorsSorted, new Comparator<Map.Entry<String, Double>>() {
            public int compare(final Map.Entry<String, Double> o1,
                               final Map.Entry<String, Double> o2) {
                if (o1.getValue().equals(o2.getValue())) {
                    return (o1.getKey().compareTo(o2.getKey()));
                } else {
                    return (int) ((o1.getValue() - o2.getValue()) * Constants.ONE_HUNDRED);
                }
            }
        });

        if (sortType.equals(Constants.DESC)) {
            Collections.reverse(actorsSorted);
        }

        if (actorsSorted.size() < n) {
            for (int i = 0; i < actorsSorted.size(); i++) {
                actorsNames.add(actorsSorted.get(i).getKey());
            }
            return actorsNames;
        } else {
            for (int i = 0; i < n; i++) {
                actorsNames.add(actorsSorted.get(i).getKey());
            }
            return actorsNames;
        }
    }

    /**
     * @param actors
     * @param n
     * @param sortType
     * @param shows
     * @return
     */
    public static String getQueryAverage(final ArrayList<Actor> actors,
                                         final int n, final String sortType,
                                         final ArrayList<Show> shows) {
        LinkedHashMap<String, Double> actorsAverage = new LinkedHashMap<>();
        ArrayList<String> actorsNames = new ArrayList<>();
        for (Actor actor : actors) {
            double sum = 0;
            int nr = 0;
            for (int i = 0; i < actor.getFilmography().size(); i++) {
                for (Show show : shows) {
                    if (show.getTitle().equals(actor.getFilmography().get(i))
                            && (show.Average() != 0)) {
                        sum += show.Average();
                        nr++;
                    }
                }
            }
            double average = 0;
            if (nr != 0) {
                average = sum / nr;
            }
            actorsAverage.put(actor.getName(), average);
        }

        while (actorsAverage.values().remove(0.0)) {
            continue;
        }

        actorsNames = getResultAverage(actorsAverage, sortType, n);
        return Constants.QUERY_RESULT + actorsNames.toString();
    }
}

