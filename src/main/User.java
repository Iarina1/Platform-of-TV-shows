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
    private final ArrayList<String> favoriteMovies;
    private int nrRatings;

    @Override
    public String toString() {
        return "User{"
                + "username='" + username + '\''
                + ", subscriptionType='" + subscriptionType + '\''
                + ", history=" + history
                + ", favoriteMovies=" + favoriteMovies
                + '}';
    }

    public User(final String username, final String subscriptionType,
                final Map<String, Integer> history, final ArrayList<String> favoriteMovies) {
        this.username = username;
        this.subscriptionType = subscriptionType;
        this.history = new LinkedHashMap<String, Integer>();
        for (String historyString : history.keySet()) {
            this.history.put(historyString, history.get(historyString));
        }
        this.favoriteMovies = favoriteMovies;
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
    public ArrayList<String> getFavoriteMovies() {
        return favoriteMovies;
    }

    public String addFavoriteList(String title) {
        for (int i = 0; i < this.getFavoriteMovies().size(); i++) {
            if (this.getFavoriteMovies().get(i).equals(title)) {
                return Constants.ERROR + title + Constants.IS_IN_FAVORITE;
            }
        }

        if (this.getHistory().containsKey(title)) {
            this.favoriteMovies.add(title);
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
}
