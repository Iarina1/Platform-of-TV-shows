package main;

import common.Constants;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Comparator;
import java.util.Map;
import java.util.Collections;
import java.util.List;

public class Movie extends Show {
    private final int duration;
    private ArrayList<String> userArrayList;
    private Double sum;
    private Double average;

    public Movie(final String title, final ArrayList<String> cast, final ArrayList<String> genres,
                 final int year, final int duration) {
        super(title, year, cast, genres);
        this.duration = duration;
        userArrayList = new ArrayList<String>();
        this.sum = 0.0;
        this.average = 0.0;
    }

    public final ArrayList<String> getUserArrayList() {
        return userArrayList;
    }

    public final void setUserArrayList(final ArrayList<String> userArrayList) {
        this.userArrayList = userArrayList;
    }

    public final void setSum(final Double sum) {
        this.sum = sum;
    }

    public final void setAverage(final Double average) {
        this.average = average;
    }

    public final Double getSum() {
        return sum;
    }

    @Override
    public final Double Average() {
        return this.average;
    }

    public final int getDuration() {
        return duration;
    }

    /**
     * @param title
     * @param grade
     * @param user
     * @return
     */
    public final String getRating(final String title,
                                  final Double grade, final User user) {
        if (this.userArrayList.contains(user.getUsername())) {
            return Constants.ERROR + title + Constants.HAS_BEEN_RATED;
        } else if (!user.getHistory().containsKey(title)) {
            return Constants.ERROR + title + Constants.NOT_SEEN;
        } else {
            this.userArrayList.add(user.getUsername());
            this.sum += grade;
            // media notelor primite de la toti utilizatorii
            this.average = this.sum / userArrayList.size();
            user.setNrRatings(user.getNrRatings() + 1);
            return Constants.SUCCESS + title + Constants.WAS_RATED + grade + Constants.BY
                    + user.getUsername();
        }
    }

    /**
     * @param moviesLinkedHashMap
     * @param sortType
     * @param n
     * @return
     */
    private static ArrayList<String> getResult(final LinkedHashMap<String, Integer>
                                                      moviesLinkedHashMap, final String sortType,
                                              final int n) {
        ArrayList<String> moviesNames = new ArrayList<String>();
        List<Map.Entry<String, Integer>> moviesSorted
                = new ArrayList<Map.Entry<String, Integer>>(moviesLinkedHashMap.entrySet());

        Collections.sort(moviesSorted, new Comparator<Map.Entry<String, Integer>>() {
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
            Collections.reverse(moviesSorted);
        }

        if (moviesSorted.size() < n) {
            for (int i = 0; i < moviesSorted.size(); i++) {
                moviesNames.add(moviesSorted.get(i).getKey());
            }
            return moviesNames;
        } else {
            for (int i = 0; i < n; i++) {
                moviesNames.add(moviesSorted.get(i).getKey());
            }
            return moviesNames;
        }
    }

    /**
     *
     * @return
     */
    private static Comparator<Movie> getLongestMovieList() {
        return new Comparator<Movie>() {
            public int compare(final Movie m1, final Movie m2) {
                if (m1.getDuration() == m2.getDuration()) {
                    return m1.getTitle().compareTo(m2.getTitle());
                } else {
                    return m1.getDuration() - m2.getDuration();
                }
            }
        };
    }

    /**
     * @param movies
     * @param n
     * @param sortType
     * @return
     */
    public static String getLongestMovies(final ArrayList<Movie> movies,
                                          final int n, final String sortType) {
        ArrayList<String> moviesNames = new ArrayList<String>();

        // sortare dupa durata + alfabetic
        movies.sort(Movie.getLongestMovieList());

        if (sortType.equals(Constants.DESC)) {
            Collections.reverse(movies);
        }

        // titlurile filmelor din output
        if (movies.size() < n) {
            for (Movie movie : movies) {
                moviesNames.add(movie.getTitle());
            }
        } else {
            for (int i = 0; i < n; i++) {
                moviesNames.add(movies.get(i).getTitle());
            }
        }
        return Constants.QUERY_RESULT + moviesNames.toString();
    }

    /**
     * @param movies
     * @param users
     * @param n
     * @param sortType
     * @return
     */
    public static String getFavoriteMovies(final ArrayList<Movie> movies,
                                           final ArrayList<User> users,
                                           final int n, final String sortType) {
        ArrayList<String> moviesNames = new ArrayList<>();
        LinkedHashMap<String, Integer> moviesFavourite = new LinkedHashMap<String, Integer>();

        for (int i = 0; i < movies.size(); i++) {
            for (User user : users) {
                if (user.getFavoriteShows().contains(movies.get(i).getTitle())) {
                    if (moviesFavourite.containsKey(movies.get(i).getTitle())) {
                        moviesFavourite.put(movies.get(i).getTitle(),
                                // actualizez nr de aparitii in lista de fav
                                moviesFavourite.get(movies.get(i).getTitle()) + 1);
                    } else {
                        // adaug filmul in LinkedHashMap
                        moviesFavourite.put(movies.get(i).getTitle(), 1);
                    }
                }
            }
        }

        // sortare
        moviesNames = getResult(moviesFavourite, sortType, n);

        return Constants.QUERY_RESULT + moviesNames.toString();
    }

    /**
     * @param moviesLinkedHashMap
     * @param sortType
     * @param n
     * @return
     */
    private static ArrayList<String> getResultRatingMovies(
            final LinkedHashMap<String, Double> moviesLinkedHashMap,
                                                          final String sortType, final int n) {
        List<Map.Entry<String, Double>> moviesSorted
                = new ArrayList<Map.Entry<String, Double>>(moviesLinkedHashMap.entrySet());
        ArrayList<String> moviesNames = new ArrayList<String>();

        Collections.sort(moviesSorted, new Comparator<Map.Entry<String, Double>>() {
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
            Collections.reverse(moviesSorted);
        }

        if (moviesSorted.size() < n) {
            for (int i = 0; i < moviesSorted.size(); i++) {
                moviesNames.add(moviesSorted.get(i).getKey());
            }
            return moviesNames;
        } else {
            for (int i = 0; i < n; i++) {
                moviesNames.add(moviesSorted.get(i).getKey());
            }
            return moviesNames;
        }
    }

    /**
     * @param movies
     * @param n
     * @param sortType
     * @return
     */
    public static String getRatingMovies(final ArrayList<Movie> movies,
                                         final int n, final String sortType) {
        ArrayList<String> moviesNames = new ArrayList<String>();

        LinkedHashMap<String, Double> moviesRated = new LinkedHashMap<String, Double>();

        for (Movie movie : movies) {
            if (!moviesRated.containsKey(movie.getTitle()) && movie.Average() != 0) {
                // pentru fiecare film pastrez nota
                moviesRated.put(movie.getTitle(), movie.Average());
            }
        }

        // sortarea
        moviesNames = getResultRatingMovies(moviesRated, sortType, n);
        return Constants.QUERY_RESULT + moviesNames;
    }
}
