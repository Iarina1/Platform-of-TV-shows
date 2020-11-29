package main;

import common.Constants;
import entertainment.Season;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Comparator;
import java.util.Map;
import java.util.Collections;
import java.util.List;

public class Serial extends Show {
    private final int numberOfSeasons;
    private final ArrayList<Season> seasons;
    private Map<User, ArrayList<Integer>> userEvidence;
    // pt fiecare sezon avem suma, nr de ratinguri primite, media
    private LinkedHashMap<Integer, ArrayList<Double>> gradeSeasons;
    private Double average;

    public Serial(final String title, final ArrayList<String> cast, final ArrayList<String> genres,
                  final int numberOfSeasons, final ArrayList<Season> seasons, final int year) {
        super(title, year, cast, genres);
        this.numberOfSeasons = numberOfSeasons;
        this.seasons = seasons;
        userEvidence = new LinkedHashMap<User, ArrayList<Integer>>();
        gradeSeasons = new LinkedHashMap<Integer, ArrayList<Double>>();
        for (int i = 1; i < numberOfSeasons + 1; i++) {
            ArrayList<Double> newGrades = new ArrayList<>();
            newGrades.add((double) 0);
            newGrades.add((double) 0);
            newGrades.add((double) 0);
            gradeSeasons.put(i, newGrades);
        }
        average = 0.0;
    }

    public final void setUserEvidence(final Map<User, ArrayList<Integer>> userEvidence) {
        this.userEvidence = userEvidence;
    }

    @Override
    public final Double Average() {
        return this.average;
    }

    public final void setAverage(final Double average) {
        this.average = average;
    }

    public final Map<User, ArrayList<Integer>> getUserEvidence() {
        return userEvidence;
    }

    public final int getNumberOfSeasons() {
        return numberOfSeasons;
    }

    public final ArrayList<Season> getSeasons() {
        return seasons;
    }

    /**
     * @param title
     * @param grade
     * @param user
     * @param season
     * @return
     */
    public String getRating(final String title, final Double grade,
                            final User user, final int season) {
        if (this.userEvidence.containsKey(user)) {
            if (this.userEvidence.get(user).contains(season)) {
                return Constants.ERROR + title + Constants.HAS_BEEN_RATED;
            } else {
                this.userEvidence.get(user).add(season);
                ArrayList<Double> newGrades = new ArrayList<Double>();
                newGrades.add(gradeSeasons.get(season).get(0) + grade);
                newGrades.add(gradeSeasons.get(season).get(1) + 1);
                newGrades.add(newGrades.get(0) / newGrades.get(1));
                gradeSeasons.put(season, newGrades);
                Double sum = 0.0;
                for (int i = 1; i < gradeSeasons.size() + 1; i++) {
                    sum += gradeSeasons.get(i).get(0);
                }
                this.setAverage(sum / gradeSeasons.size());
                user.setNrRatings(user.getNrRatings() + 1);
                return Constants.SUCCESS + title + Constants.WAS_RATED
                        + grade + Constants.BY + user.getUsername();
            }
        } else {
            if (user.getHistory().containsKey(title)) {
                ArrayList<Integer> seasonRated = new ArrayList<Integer>();
                seasonRated.add(season);
                userEvidence.put(user, seasonRated);
                ArrayList<Double> newGrades = new ArrayList<Double>();
                newGrades.add(gradeSeasons.get(season).get(0) + grade);
                newGrades.add(gradeSeasons.get(season).get(1) + 1);
                newGrades.add(newGrades.get(0) / newGrades.get(1));
                gradeSeasons.put(season, newGrades);
                Double sum = 0.0;
                for (int i = 1; i < gradeSeasons.size() + 1; i++) {
                    sum += gradeSeasons.get(i).get(0);
                }
                this.setAverage(sum / gradeSeasons.size());
                user.setNrRatings(user.getNrRatings() + 1);
                return Constants.SUCCESS + title + Constants.WAS_RATED
                        + grade + Constants.BY + user.getUsername();
            } else {
                return Constants.ERROR + title + Constants.NOT_SEEN;
            }
        }
    }

    /**
     * @param serialsLinkedHashMap
     * @param sortType
     * @param n
     * @return
     */
    private static ArrayList<String> getResult(final LinkedHashMap<String, Integer>
                                                      serialsLinkedHashMap,
                                              final String sortType, final int n) {
        List<Map.Entry<String, Integer>> serialsSorted
                = new ArrayList<Map.Entry<String, Integer>>(serialsLinkedHashMap.entrySet());
        ArrayList<String> serialsNames = new ArrayList<String>();

        Collections.sort(serialsSorted, new Comparator<Map.Entry<String, Integer>>() {
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
            Collections.reverse(serialsSorted);
        }

        if (serialsSorted.size() < n) {
            for (int i = 0; i < serialsSorted.size(); i++) {
                serialsNames.add(serialsSorted.get(i).getKey());
            }
            return serialsNames;
        } else {
            for (int i = 0; i < n; i++) {
                serialsNames.add(serialsSorted.get(i).getKey());
            }
            return serialsNames;
        }
    }

    /**
     *
     * @param serial
     * @return
     */
    private static int getDuration(final Serial serial) {
        int duration = 0;
        for (int i = 0; i < serial.getNumberOfSeasons(); i++) {
            duration += serial.getSeasons().get(i).getDuration();
        }
        return duration;
    }

    /**
     * @return
     */
    private static Comparator<Serial> getLongestSerialList() {
        return new Comparator<Serial>() {
            public int compare(final Serial s1, final Serial s2) {
                if (s1.getDuration(s1) == s2.getDuration(s2)) {
                    return s1.getTitle().compareTo(s2.getTitle());
                } else {
                    return s1.getDuration(s1) - s2.getDuration(s2);
                }
            }
        };
    }

    /**
     * @param serials
     * @param n
     * @param sortType
     * @return
     */
    public static String getLongestSerials(final ArrayList<Serial> serials,
                                           final int n, final String sortType) {
        ArrayList<String> serialsNames = new ArrayList<String>();

        serials.sort(Serial.getLongestSerialList());

        if (sortType.equals(Constants.DESC)) {
            Collections.reverse(serials);
        }

        if (serials.size() < n) {
            for (Serial serial : serials) {
                serialsNames.add(serial.getTitle());
            }
        } else {
            for (int i = 0; i < n; i++) {
                serialsNames.add(serials.get(i).getTitle());
            }
        }
        return Constants.QUERY_RESULT + serialsNames.toString();
    }

    /**
     * @param serials
     * @param users
     * @param n
     * @param sortType
     * @return
     */
    public static String getFavoriteSerials(final ArrayList<Serial> serials,
                                            final ArrayList<User> users,
                                            final int n, final String sortType) {
        ArrayList<String> serialsNames = new ArrayList<>();
        LinkedHashMap<String, Integer> serialsFavourite = new LinkedHashMap<String, Integer>();

        for (int i = 0; i < serials.size(); i++) {
            for (User user : users) {
                if (user.getFavoriteShows().contains(serials.get(i).getTitle())) {
                    if (serialsFavourite.containsKey(serials.get(i).getTitle())) {
                        serialsFavourite.put(serials.get(i).getTitle(),
                                serialsFavourite.get(serials.get(i).getTitle()) + 1);
                    } else {
                        serialsFavourite.put(serials.get(i).getTitle(), 1);
                    }
                }
            }
        }

        serialsNames = getResult(serialsFavourite, sortType, n);

        return Constants.QUERY_RESULT + serialsNames.toString();
    }

    /**
     * @param serialsLinkedHashMap
     * @param sortType
     * @param n
     * @return
     */
    private static ArrayList<String> getResultRatingSerial(
            final LinkedHashMap<String, Double> serialsLinkedHashMap,
            final String sortType, final int n) {
        List<Map.Entry<String, Double>> serialsSorted
                = new ArrayList<Map.Entry<String, Double>>(serialsLinkedHashMap.entrySet());

        Collections.sort(serialsSorted, new Comparator<Map.Entry<String, Double>>() {
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
            Collections.reverse(serialsSorted);
        }

        ArrayList<String> serilasNames = new ArrayList<String>();

        if (serialsSorted.size() < n) {
            for (int i = 0; i < serialsSorted.size(); i++) {
                serilasNames.add(serialsSorted.get(i).getKey());
            }
            return serilasNames;
        } else {
            for (int i = 0; i < n; i++) {
                serilasNames.add(serialsSorted.get(i).getKey());
            }
            return serilasNames;
        }
    }

    /**
     * @param serials
     * @param n
     * @param sortType
     * @return
     */
    public static String getRatingSerials(
            final ArrayList<Serial> serials,
                                          final int n, final String sortType) {
        ArrayList<String> serialsNames = new ArrayList<String>();

        LinkedHashMap<String, Double> serialsRated = new LinkedHashMap<String, Double>();

        for (Serial serial : serials) {
            if (!serialsRated.containsKey(serial.getTitle()) && serial.Average() != 0) {
                serialsRated.put(serial.getTitle(), serial.Average());
            }
        }

        serialsNames = getResultRatingSerial(serialsRated, sortType, n);
        return Constants.QUERY_RESULT + serialsNames;
    }
}
