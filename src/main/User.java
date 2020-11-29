package main;

import common.Constants;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.List;

public class User {
    private final String username;
    private final String subscriptionType;
    private final Map<String, Integer> history;
    private final ArrayList<String> favoriteShows;
    private int nrRatings;

    public User(final String username, final String subscriptionType,
                final Map<String, Integer> history, final ArrayList<String> favoriteShows) {
        this.username = username;
        this.subscriptionType = subscriptionType;
        this.history = new LinkedHashMap<String, Integer>();
        for (String historyString : history.keySet()) {
            this.history.put(historyString, history.get(historyString));
        }
        this.favoriteShows = favoriteShows;
        this.nrRatings = 0;
    }

    public  final int getNrRatings() {
        return nrRatings;
    }

    public  final void setNrRatings(final int nrRatings) {
        this.nrRatings = nrRatings;
    }

    public final String getUsername() {
        return username;
    }

    public final String getSubscriptionType() {
        return subscriptionType;
    }

    public final Map<String, Integer> getHistory() {
        return history;
    }

    public final ArrayList<String> getFavoriteShows() {
        return favoriteShows;
    }

    /**
     * @param title
     * @return
     */
    public final String addFavoriteList(final String title) {
        for (int i = 0; i < this.getFavoriteShows().size(); i++) {
            if (this.getFavoriteShows().get(i).equals(title)) {
                return Constants.ERROR + title + Constants.IS_IN_FAVORITE;
            }
        }

        if (this.getHistory().containsKey(title)) {
            this.favoriteShows.add(title);
            return Constants.SUCCESS + title + Constants.ADD_TO_FAVORITE;
        }

        return Constants.ERROR + title + Constants.NOT_SEEN;
    }

    /**
     * @param title
     * @return
     */
    public final String timesViewed(final String title) {
        if (!this.getHistory().containsKey(title)) {
            this.getHistory().put(title, 1);
        } else {
            this.getHistory().put(title, this.getHistory().get(title) + 1);
        }
        return Constants.SUCCESS + title + Constants.WAS_VIEWED + this.getHistory().get(title);
    }

    /**
     * @return
     */
    private static Comparator<User> getUserRatingsList() {
        return new Comparator<User>() {
            public int compare(final User u1, final User u2) {
                if (u1.getNrRatings() == u2.getNrRatings()) {
                    return u1.getUsername().compareTo(u2.getUsername());
                } else {
                    return u1.getNrRatings() - u2.getNrRatings();
                }
            }
        };
    }

    /**
     *
     * @param userList
     * @param n
     * @param sortType
     * @return
     */
    public static String getSortedList(final ArrayList<User> userList,
                                       final int n, final String sortType) {
        ArrayList<User> userRatings = new ArrayList<User>();
        ArrayList<String> userNames = new ArrayList<String>();

        for (User user : userList) {
            if (user.getNrRatings() != 0) {
                userRatings.add(user);
            }
        }

        userRatings.sort(User.getUserRatingsList());

        if (sortType.equals(Constants.DESC)) {
            Collections.reverse(userRatings);
        }

        if (userRatings.size() < n) {
            for (User user : userRatings) {
                userNames.add(user.getUsername());
            }
            return Constants.QUERY_RESULT + userNames.toString();
        } else {
            for (int i = 0; i < n; i++) {
                userNames.add(userRatings.get(i).getUsername());
            }
            return Constants.QUERY_RESULT + userNames.toString();
        }
    }

    /**
     * @param users
     * @param shows
     * @param username
     * @return
     */
    public static String RecommendationStandard(final ArrayList<User> users,
                                                final ArrayList<Show> shows,
                                                final String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                for (Show show : shows) {
                    if (!user.getHistory().containsKey(show.getTitle())) {
                        return Constants.STANDARD_RESULT + show.getTitle();
                    }
                }
            }
        }
        return Constants.STANDARD_NOT_APPLIED;
    }

    /**
     * @param users
     * @param shows
     * @param username
     * @return
     */
    public static String BestUnseenRecommendation(final ArrayList<User> users,
                                                  final ArrayList<Show> shows,
                                                  final String username) {
        LinkedHashMap<String, Double> showsBackup = new LinkedHashMap<>();
        for (Show show : shows) {
            showsBackup.put(show.getTitle(), show.Average());
        }

        List<Map.Entry<String, Double>> showSorted
                = new ArrayList<Map.Entry<String, Double>>(showsBackup.entrySet());

        Collections.sort(showSorted, new Comparator<Map.Entry<String, Double>>() {
            public int compare(final Map.Entry<String, Double> o1,
                               final Map.Entry<String, Double> o2) {
                return (int) ((o2.getValue() - o1.getValue()) * Constants.ONE_HUNDRED);
            }
        });

        for (User user : users) {
            if (user.getUsername().equals(username)) {
                for (int i = 0; i < showSorted.size(); i++) {
                    if (!user.getHistory().containsKey(showSorted.get(i).getKey())) {
                        return Constants.BEST_RESULT + showSorted.get(i).getKey();
                    }
                }
            }
        }
        return Constants.BEST_UNSEEN_NOT_APPLIED;
    }

    /**
     * @param showsLinkedHashMap
     * @return
     */
    private static ArrayList<String>
    getSearchSorted(final LinkedHashMap<String, Double> showsLinkedHashMap) {
        List<Map.Entry<String, Double>> showsSorted
                = new ArrayList<Map.Entry<String, Double>>(showsLinkedHashMap.entrySet());
        ArrayList<String> showsNames = new ArrayList<String>();

        Collections.sort(showsSorted, new Comparator<Map.Entry<String, Double>>() {
            public int compare(final Map.Entry<String, Double> o1,
                               final Map.Entry<String, Double> o2) {
                if (o1.getValue().equals(o2.getValue())) {
                    return (o1.getKey().compareTo(o2.getKey()));
                } else {
                    return (int) ((o1.getValue() - o2.getValue()) * Constants.ONE_HUNDRED);
                }
            }
        });

        for (int i = 0; i < showsSorted.size(); i++) {
            showsNames.add(showsSorted.get(i).getKey());
        }
        return showsNames;
    }

    /**
     * @param users
     * @param shows
     * @param username
     * @param genre
     * @return
     */
    public static String getSearchRecommendation(final ArrayList<User> users,
                                                 final ArrayList<Show> shows,
                                                 final String username, final String genre) {
        LinkedHashMap<String, Double> showsBackup = new LinkedHashMap<>();
        ArrayList<String> showsNames = new ArrayList<String>();
        ArrayList<String> showsRecommendation = new ArrayList<String>();
        int sem = 0;

        for (Show show : shows) {
            if (show.getGenres().contains(genre)) {
                showsBackup.put(show.getTitle(), show.Average());
            }
        }

        for (User user : users) {
            if (user.getUsername().equals(username)) {
                if (user.getSubscriptionType().equals(Constants.PREMIUM)) {
                    showsNames = getSearchSorted(showsBackup);
                    for (int i = 0; i < showsNames.size(); i++) {
                        if (!user.getHistory().containsKey(showsNames.get(i))) {
                            showsRecommendation.add(showsNames.get(i));
                            sem = 1;
                        }
                    }
                }
            }
        }

        if (sem == 0) {
            return Constants.SEARCH_NOT_APPLIED;
        } else {
            return Constants.SEARCH_RESULT + showsRecommendation;
        }
    }

    /**
     * @param showsLinkedHashMap
     * @param showDB
     * @return
     */
    private static ArrayList<String>
    getFavouriteSorted(final LinkedHashMap<String, Integer>
                               showsLinkedHashMap,
                       final ArrayList<String> showDB) {
        ArrayList<String> showsNames = new ArrayList<String>();
        List<Map.Entry<String, Integer>> showsSorted
                = new ArrayList<Map.Entry<String, Integer>>(showsLinkedHashMap.entrySet());

        Collections.sort(showsSorted, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(final Map.Entry<String, Integer> o1,
                               final Map.Entry<String, Integer> o2) {
                if (o2.getValue().equals(o1.getValue())) {
                    return showDB.indexOf(o1.getKey()) - showDB.indexOf(o2.getKey());
                } else {
                    return o2.getValue().compareTo(o1.getValue());
                }
            }
        });

        for (int i = 0; i < showsSorted.size(); i++) {
            showsNames.add(showsSorted.get(i).getKey());
        }
        return showsNames;
    }

    /**
     * @param users
     * @param shows
     * @param username
     * @return
     */
    public static String getFavourite(final ArrayList<User> users,
                                      final ArrayList<Show> shows, final String username) {
        ArrayList<String> showNames = new ArrayList<>();
        for (Show show : shows) {
            showNames.add(show.getTitle());
        }
        LinkedHashMap<String, Integer> showsFavorite = new LinkedHashMap<>();
        ArrayList<String> showsNames = new ArrayList<>();
        for (User user : users) {
            for (int i = 0; i < user.getFavoriteShows().size(); i++) {
                if (!showsFavorite.containsKey(user.getFavoriteShows().get(i))) {
                    showsFavorite.put(user.getFavoriteShows().get(i), 1);
                } else {
                    showsFavorite.put(user.getFavoriteShows().get(i), (showsFavorite
                            .get(user.getFavoriteShows().get(i)) + 1));
                }
            }
        }

        for (User user : users) {
            if (user.getUsername().equals(username)) {
                if (user.getSubscriptionType().equals(Constants.PREMIUM)) {
                    showsNames = getFavouriteSorted(showsFavorite, showNames);
                    for (int i = 0; i < showsNames.size(); i++) {
                        if (!user.getHistory().containsKey(showsNames.get(i))) {
                            return Constants.FAVORITE_RESULT + showsNames.get(i);
                        }
                    }
                }
            }
        }
        return Constants.FAVORITE_NOT_APPLIED;
    }

    /**
     * @param genresPopularity
     * @return
     */
    private static List<Map.Entry<String, Integer>>
    getPopularSorted(final LinkedHashMap<String, Integer> genresPopularity) {
        List<Map.Entry<String, Integer>> genresSorted
                = new ArrayList<Map.Entry<String, Integer>>(genresPopularity.entrySet());

        Collections.sort(genresSorted, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(final Map.Entry<String, Integer> o1,
                               final Map.Entry<String, Integer> o2) {
                return (int) ((o2.getValue() - o1.getValue()) * Constants.ONE_HUNDRED);
            }
        });

        return genresSorted;
    }

    /**
     * @param users
     * @param shows
     * @param username
     * @return
     */
    public static String getPopularRecommandation(final ArrayList<User> users,
                                                  final ArrayList<Show> shows,
                                                  final String username) {
        LinkedHashMap<String, Integer> genresPopularity = new LinkedHashMap<>();
        ArrayList<String> genresPopularitySorted = new ArrayList<>();

        for (Show show : shows) {
            ArrayList<String> genres = show.getGenres();
            for (int i = 0; i < genres.size(); i++) {
                if (!genresPopularity.containsKey(genres.get(i))) {
                    genresPopularity.put(genres.get(i), 0);
                }
            }
        }

        for (Show show : shows) {
            for (User user : users) {
                if (user.getHistory().containsKey(show.getTitle())) {
                    for (String genre : show.getGenres()) {
                        genresPopularity.put(genre, genresPopularity.get(genre)
                                + user.getHistory().get(show.getTitle()));
                    }
                }
            }
        }

        List<Map.Entry<String, Integer>> genresSorted =  getPopularSorted(genresPopularity);

        for (int i = 0; i < genresSorted.size(); i++) {
            genresPopularitySorted.add(genresSorted.get(i).getKey());
        }

        for (String genrePopularitySorted : genresPopularitySorted) {
            for (User user : users) {
                if (user.getUsername().equals(username) && user.getSubscriptionType()
                        .equals(Constants.PREMIUM)) {
                    for (Show show : shows) {
                        if (show.getGenres().contains(genrePopularitySorted)
                                && (!user.getHistory().containsKey(show.getTitle()))) {
                            return Constants.POPULAR_RESULT + show.getTitle();
                        }
                    }
                }
            }
        }
        return Constants.POPULAR_NOT_APPLIED;
    }
}
