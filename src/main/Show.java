package main;

import common.Constants;

import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Collections;
import java.util.List;
import java.util.Comparator;

public class Show {
    private final String title;
    private final int year;
    private final ArrayList<String> cast;
    private final ArrayList<String> genres;
    private final Double average = 0.0;

    public Show(final String title, final int year, final ArrayList<String> cast,
                final ArrayList<String> genres) {
        this.title = title;
        this.year = year;
        this.cast = cast;
        this.genres = genres;
    }

    public final String getTitle() {
        return title;
    }

    public final int getYear() {
        return year;
    }

    public final ArrayList<String> getCast() {
        return cast;
    }

    public final ArrayList<String> getGenres() {
        return genres;
    }

    /**
     *
     * @return
     */
    public Double Average() {
        return average;
    }

    /**
     * @param showLinkedHashMap
     * @param sortType
     * @param n
     * @return
     */
    private static ArrayList<String> getNamesSortedShow(
            final LinkedHashMap<String, Integer> showLinkedHashMap,
            final String sortType, final int n) {
        ArrayList<String> showsNames = new ArrayList<String>();
        List<Map.Entry<String, Integer>> showsSorted
                = new ArrayList<Map.Entry<String, Integer>>(showLinkedHashMap.entrySet());

        Collections.sort(showsSorted, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(final Map.Entry<String, Integer> o1,
                               final Map.Entry<String, Integer> o2) {
                if (o1.getValue() == o2.getValue()) {
                    return o1.getKey().compareTo(o2.getKey());
                } else {
                    return o1.getValue() - o2.getValue();
                }
            }
        });

        if (sortType.equals(Constants.DESC)) {
            Collections.reverse(showsSorted);
        }

        if (showsSorted.size() < n) {
            for (int i = 0; i < showsSorted.size(); i++) {
                showsNames.add(showsSorted.get(i).getKey());
            }
            return showsNames;
        } else {
            for (int i = 0; i < n; i++) {
                showsNames.add(showsSorted.get(i).getKey());
            }
            return showsNames;
        }
    }

    /**
     * @param shows
     * @param users
     * @param n
     * @param sortType
     * @return
     */
    public static String getSortedShow(final ArrayList<Show> shows,
            final ArrayList<User> users,
                                       final int n, final String sortType) {
        LinkedHashMap<String, Integer> showsLinkedHashMap = new LinkedHashMap<String, Integer>();
        ArrayList<String> showsNames = new ArrayList<String>();

        for (User user : users) {
            for (Show show : shows) {
                if (user.getHistory().containsKey(show.getTitle())) {
                    if (showsLinkedHashMap.containsKey(show.getTitle())) {
                        // actualizat nr de vizualizari
                        showsLinkedHashMap.put(show.getTitle(),
                                showsLinkedHashMap.get(show.getTitle())
                                + user.getHistory().get(show.getTitle()));
                    } else {
                        // adaug videoclipul in dictionar
                        showsLinkedHashMap.put(show.getTitle(),
                                user.getHistory().get(show.getTitle()));
                    }
                }
            }
        }

        // sortare
        showsNames = getNamesSortedShow(showsLinkedHashMap, sortType, n);

        return Constants.QUERY_RESULT + showsNames.toString();
    }
}
