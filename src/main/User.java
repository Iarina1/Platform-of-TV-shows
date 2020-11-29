package main;

import com.sun.source.tree.BreakTree;
import common.Constants;
import net.sf.json.filters.FalsePropertyFilter;

import java.sql.SQLOutput;
import java.util.*;

public class User {
    private final String username;
    private final String subscriptionType;
    private final Map<String, Integer> history;
    private final ArrayList<String> favoriteShows;
    private int nrRatings;

    @Override
    public String toString() {
        return "User{"
                + "username='" + username + '\''
                + ", subscriptionType='" + subscriptionType + '\''
                + ", history=" + history
                + ", favoriteShows=" + favoriteShows
                + '}';
    }

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

    public int getNrRatings() {
        return nrRatings;
    }
    public void setNrRatings(int nrRatings) {
        this.nrRatings = nrRatings;
    }
    public String getUsername() {
        return username;
    }
    public String getSubscriptionType() {
        return subscriptionType;
    }
    public Map<String, Integer> getHistory() {
        return history;
    }
    public ArrayList<String> getFavoriteShows() {
        return favoriteShows;
    }

    public String addFavoriteList(String title) {
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

    public String timesViewed(String title) {
        if (!this.getHistory().containsKey(title)) {
            this.getHistory().put(title, 1);
        } else {
            this.getHistory().put(title, this.getHistory().get(title) + 1);
        }
        return Constants.SUCCESS + title + Constants.WAS_VIEWED + this.getHistory().get(title);
    }

    public static Comparator<User> getUserRatingsList() {
        return new Comparator<User>() {
            public int compare(User u1, User u2) {
                if (u1.getNrRatings() == u2.getNrRatings()) {
                    return u1.getUsername().compareTo(u2.getUsername());
                } else {
                    return u1.getNrRatings() - u2.getNrRatings();
                }
            }
        };
    }

    public static String getSortedList(ArrayList<User> userList, int n, String sortType) {
        ArrayList<User> userRatings = new ArrayList<User>();
        ArrayList<String> userNames = new ArrayList<String >();


        for (User user : userList) {
            if (user.getNrRatings() != 0) {
                userRatings.add(user);
            }
        }

        if (sortType.equals("asc")) {
            userRatings.sort(User.getUserRatingsList());
        } else {
            userRatings.sort(User.getUserRatingsList());
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

    public static String RecommendationStandard(ArrayList<User> users, ArrayList<Show> shows, String username) {
        for(User user : users) {
            if(user.getUsername().equals(username)) {
                for(Show show : shows) {
                    if(!user.getHistory().containsKey(show.getTitle())) {
                        return Constants.STANDARD_RESULT + show.getTitle();
                    }
                }
            }
        }
        return Constants.STANDARD_NOT_APPLIED;
    }

    public static String BestUnseenRecommendation(ArrayList<User> users, ArrayList<Show> shows, String username) {
        LinkedHashMap<String, Double> showsBackup = new LinkedHashMap<>();
        for(Show show : shows) {
            showsBackup.put(show.getTitle(), show.Average());
        }

        List<Map.Entry<String, Double>> showSorted = new ArrayList<Map.Entry<String, Double>>(showsBackup.entrySet());
        ArrayList<String> showsNames = new ArrayList<String >();

        Collections.sort(showSorted, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                return (int)((o2.getValue() - o1.getValue()) * 100);
            }
        });

        for(User user : users) {
            if(user.getUsername().equals(username)) {
                for(int i = 0; i < showSorted.size(); i++) {
                    if(!user.getHistory().containsKey(showSorted.get(i).getKey())) {
                        return Constants.BEST_RESULT + showSorted.get(i).getKey();
                    }
                }
            }
        }
        return Constants.BEST_UNSEEN_NOT_APPLIED;
    }

    public static ArrayList<String> getSearchSorted(LinkedHashMap<String, Double> showsLinkedHashMap) {
        List<Map.Entry<String, Double>> showsSorted = new ArrayList<Map.Entry<String, Double>>(showsLinkedHashMap.entrySet());
        ArrayList<String> showsNames = new ArrayList<String >();

        Collections.sort(showsSorted, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                if (o1.getValue().equals(o2.getValue())) {
                    return (o1.getKey().compareTo(o2.getKey()));
                } else {
                    return (int)((o1.getValue() - o2.getValue()) * 100);
                }
            }
        });

        for (int i = 0; i < showsSorted.size(); i++) {
            showsNames.add(showsSorted.get(i).getKey());
        }
        return showsNames;
    }

    public static String SearchRecommendation(ArrayList<User> users, ArrayList<Show> shows, String username, String genre) {
        LinkedHashMap<String, Double> showsBackup = new LinkedHashMap<>();
        ArrayList<String> showsNames = new ArrayList<String >();
        ArrayList<String> showsRecommendation = new ArrayList<String >();
        int sem = 0;

        for(Show show : shows) {
            if(show.getGenres().contains(genre)) {
                showsBackup.put(show.getTitle(), show.Average());
            }
        }

        for(User user : users) {
            if(user.getUsername().equals(username)) {
                if(user.getSubscriptionType().equals(Constants.PREMIUM)) {
                    showsNames = getSearchSorted(showsBackup);
                    for (int i = 0; i < showsNames.size(); i++) {
                        if(!user.getHistory().containsKey(showsNames.get(i))) {
                            showsRecommendation.add(showsNames.get(i));
                            sem  = 1;
                        }
                    }
                }
            }
        }

        if(sem == 0) {
            return Constants.SEARCH_NOT_APPLIED;
        } else {
            return Constants.SEARCH_RESULT + showsRecommendation;
        }
    }

    public static ArrayList<String> getFavouriteSorted(LinkedHashMap<String, Integer> showsLinkedHashMap) {
        ArrayList<String> showsNames = new ArrayList<String >();
        List<Map.Entry<String, Integer>> showsSorted = new ArrayList<Map.Entry<String, Integer>>(showsLinkedHashMap.entrySet());

        Collections.sort(showsSorted, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });

        for (int i = 0; i < showsSorted.size(); i++) {
            showsNames.add(showsSorted.get(i).getKey());
        }
        return showsNames;

    }

    public static String getFavourite(ArrayList<User> users, ArrayList<Show> shows, String username) {
        LinkedHashMap<String, Integer> showsFavorite = new LinkedHashMap<>();
        ArrayList<String> showsNames = new ArrayList<>();
        for (User user : users) {
            for(int i = 0;i < user.getFavoriteShows().size(); i++) {
                if(!showsFavorite.containsKey(user.getFavoriteShows().get(i))) {
                    showsFavorite.put(user.getFavoriteShows().get(i), 1);
                } else {
                    showsFavorite.put(user.getFavoriteShows().get(i), (showsFavorite.get(user.getFavoriteShows().get(i)) + 1));
                }
            }
        }


        for(User user : users) {
            if(user.getUsername().equals(username)) {
                if(user.getSubscriptionType().equals(Constants.PREMIUM)) {
                    showsNames = getFavouriteSorted(showsFavorite);
                    for (int i = 0; i < showsNames.size(); i++) {
                        if(!user.getHistory().containsKey(showsNames.get(i))) {
                            return Constants.FAVORITE_RESULT + showsNames.get(i);
                        }
                    }
                }
            }
        }
        return Constants.FAVORITE_NOT_APPLIED;
    }
}
