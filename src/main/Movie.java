package main;

import common.Constants;
import fileio.ShowInput;
import org.w3c.dom.ls.LSOutput;

import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.util.*;

public class Movie extends ShowInput {
    private final int duration;
    private ArrayList<String> userArrayList;
    private int sum;
    private double average;

    @Override
    public String toString() {
        return "MovieInputData{" + "title= "
                + super.getTitle() + "year= "
                + super.getYear() + "duration= "
                + duration + "cast {"
                + super.getCast() + " }\n"
                + "genres {" + super.getGenres() + " }\n ";
    }

    public Movie(final String title, final ArrayList<String> cast, final ArrayList<String> genres,
                 final int year, final int duration) {
        super(title, year, cast, genres);
        this.duration = duration;
        userArrayList = new ArrayList<String>();
        this.sum = 0;
        this.average = 0;
    }

    public ArrayList<String> getUserArrayList() {
        return userArrayList;
    }
    public void setUserArrayList(ArrayList<String> userArrayList) {
        this.userArrayList = userArrayList;
    }
    public void setSum(int sum) {
        this.sum = sum;
    }
    public void setAverage(double average) {
        this.average = average;
    }
    public int getSum() {
        return sum;
    }
    public double getAverage() {
        return average;
    }
    public int getDuration() {
        return duration;
    }

    public String getRating(String title, double grade, User user) {
        if (this.userArrayList.contains(user.getUsername())) {
            return Constants.ERROR + title + Constants.HAS_BEEN_RATED;
        } else if (!user.getHistory().containsKey(title)) {
            return Constants.ERROR + title + Constants.NOT_SEEN;
        } else {
            this.userArrayList.add(user.getUsername());
            this.sum += grade;
            this.average = this.sum / userArrayList.size();
            user.setNrRatings(user.getNrRatings() + 1);
            return Constants.SUCCESS + title + Constants.WAS_RATED + grade + Constants.BY + user.getUsername();
        }
    }

    public static ArrayList<String> getResult(LinkedHashMap<String, Integer> moviesLinkedHashMap, String sortType, int n) {
        ArrayList<String> moviesNames = new ArrayList<String >();
        List<Map.Entry<String, Integer>> moviesSorted = new ArrayList<Map.Entry<String, Integer>>(moviesLinkedHashMap.entrySet());

        Collections.sort(moviesSorted, new Comparator<Map.Entry<String, Integer>>() {
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

    public static String getSortedMovies(ArrayList<Movie> movies, ArrayList<User> users, int n, String sortType) {
        LinkedHashMap<String, Integer> moviesLinkedHashMap = new LinkedHashMap<String, Integer>();
        ArrayList<String> moviesNames = new ArrayList<String >();

        for (User user : users) {
            for (Movie movie : movies) {
                if (user.getHistory().containsKey(movie.getTitle())) {
                    if (moviesLinkedHashMap.containsKey(movie.getTitle())) {
                        moviesLinkedHashMap.put(movie.getTitle(), moviesLinkedHashMap.get(movie.getTitle())
                                + user.getHistory().get(movie.getTitle()));
                    } else {
                        moviesLinkedHashMap.put(movie.getTitle(), user.getHistory().get(movie.getTitle()));
                    }
                }
            }
        }

        moviesNames = getResult(moviesLinkedHashMap, sortType, n);

        return Constants.QUERY_RESULT + moviesNames.toString();
    }

    public static Comparator<Movie> getMovieListSortedByDuration() {
        return new Comparator<Movie>() {
            public int compare(Movie m1, Movie m2) {
                if (m1.getDuration() == m2.getDuration()) {
                    return m1.getTitle().compareTo(m2.getTitle());
                } else {
                    return m1.getDuration() - m2.getDuration();
                }
            }
        };
    }

    public static String getLongestMovies(ArrayList<Movie> movies, int n, String sortType) {
        ArrayList<String> moviesNames = new ArrayList<String >();

        movies.sort(Movie.getMovieListSortedByDuration());

        if (sortType.equals("desc")) {
            Collections.reverse(movies);
        }

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

    public static String getFavoriteMovies(ArrayList<Movie> movies, ArrayList<User> users, int n, String sortType) {
        ArrayList<String> moviesNames = new ArrayList<>();
        LinkedHashMap<String, Integer> moviesFavourite = new LinkedHashMap<String, Integer>();

//        for (Movie movie : movies) {
//            System.out.println(movie.getTitle());
//        }
//        System.out.println(n);
//        System.out.println(movies.size());
        for (int i = 0; i < movies.size(); i++) {
            for (User user : users) {
                if (user.getFavoriteMovies().contains(movies.get(i).getTitle())) {
                    if (moviesFavourite.containsKey(movies.get(i).getTitle())) {
                        moviesFavourite.put(movies.get(i).getTitle(), moviesFavourite.get(movies.get(i).getTitle()) + 1);
                    } else {
                        moviesFavourite.put(movies.get(i).getTitle(), 1);
                    }
                }
            }
        }

        moviesNames = getResult(moviesFavourite, sortType, n);

        return Constants.QUERY_RESULT + moviesNames.toString();
    }

    public static ArrayList<String> getResultRating(LinkedHashMap<String, Double> moviesLinkedHashMap, String sortType, int n) {
        List<Map.Entry<String, Double>> moviesSorted = new ArrayList<Map.Entry<String, Double>>(moviesLinkedHashMap.entrySet());
        ArrayList<String> moviesNames = new ArrayList<String >();

        Collections.sort(moviesSorted, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                if (o1.getValue() == o2.getValue()) {
                    return (o1.getKey().compareTo(o2.getKey()));
                } else {
                    return (int)((o1.getValue() - o2.getValue()) * 100);
                }
            }
        });

        if (sortType.equals("desc")) {
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

    public static String getRatingMovies(ArrayList<Movie> movies, int n, String sortType) {
        ArrayList<String> moviesNames = new ArrayList<String>();

        LinkedHashMap<String, Double> moviesRated = new LinkedHashMap<String, Double>();

        for (Movie movie :  movies) {
            if(!moviesRated.containsKey(movie.getTitle()) && movie.getAverage() != 0) {
                moviesRated.put(movie.getTitle(), movie.getAverage());
            }
        }

        moviesNames = getResultRating(moviesRated, sortType, n);
        return Constants.QUERY_RESULT + moviesNames;
    }
}