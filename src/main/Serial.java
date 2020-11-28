package main;

import common.Constants;
import entertainment.Season;

import java.util.*;

public class Serial extends Show {
    private final int numberOfSeasons;
    private final ArrayList<Season> seasons;
    private Map<User, ArrayList<Integer>> userEvidence;
    private LinkedHashMap<Integer, ArrayList<Double>> gradeSeasons; // pt fiecare sezon avem suma, nr de ratinguri primite, media
    private Double average;

    @Override
    public String toString() {
        return "Serial{" +
                "numberOfSeasons=" + numberOfSeasons +
                ", seasons=" + seasons +
                ", userEvidence=" + userEvidence +
                '}';
    }

    public Serial(final String title, final ArrayList<String> cast, final ArrayList<String> genres,
                  final int numberOfSeasons, final ArrayList<Season> seasons, final int year) {
        super(title, year, cast, genres);
        this.numberOfSeasons = numberOfSeasons;
        this.seasons = seasons;
        userEvidence = new LinkedHashMap<User, ArrayList<Integer>>();
        gradeSeasons = new LinkedHashMap<Integer, ArrayList<Double>>();
        for (int i = 0; i < numberOfSeasons; i++) {
            ArrayList<Double> newGrades = new ArrayList<>();
            newGrades.add((double)0);
            newGrades.add((double)0);
            newGrades.add((double)0);
            gradeSeasons.put(i, newGrades);
        }
        average = 0.0;
    }

    public void setUserEvidence(Map<User, ArrayList<Integer>> userEvidence) {
        this.userEvidence = userEvidence;
    }


    public Double Average() {
        return this.average;
    }
    public void setAverage(Double average) {
        this.average = average;
    }

    public Map<User, ArrayList<Integer>> getUserEvidence() {
        return userEvidence;
    }
    public int getNumberOfSeasons() {
        return numberOfSeasons;
    }
    public ArrayList<Season> getSeasons() {
        return seasons;
    }

    public String getRating(String title, Double grade, User user, int season) {
        if (this.userEvidence.containsKey(user)) {
            if (this.userEvidence.get(user).contains(season)) {
                return Constants.ERROR + title + Constants.HAS_BEEN_RATED;
            } else {
                this.userEvidence.get(user).add(season);
                if (gradeSeasons.containsKey(season)) {
                    ArrayList<Double> newGrades = new ArrayList<Double>();
                    newGrades.add(gradeSeasons.get(season).get(0) + grade);
                    newGrades.add(gradeSeasons.get(season).get(1) + 1);
                    newGrades.add(newGrades.get(0) / newGrades.get(1));
                    gradeSeasons.put(season, newGrades);
                } else {
                    ArrayList<Double> newGrades = new ArrayList<>();
                    newGrades.add(grade);
                    newGrades.add((double)1);
                    newGrades.add(grade);
                    gradeSeasons.put(season, newGrades);
                }
                Double sum = 0.0;
                for (int i = 0; i < gradeSeasons.size(); i++) {
                    sum += gradeSeasons.get(i).get(0);
                }
                Double average = sum / gradeSeasons.size();
                this.setAverage(average);
                user.setNrRatings(user.getNrRatings() + 1);
                return Constants.SUCCESS + title + Constants.WAS_RATED + grade + Constants.BY + user.getUsername();
            }
        } else {
            if (user.getHistory().containsKey(title)) {
                ArrayList<Integer> seasonRated = new ArrayList<Integer>();
                seasonRated.add(season);
                userEvidence.put(user, seasonRated);
                if (gradeSeasons.containsKey(season)) {
                    ArrayList<Double> newGrades = new ArrayList<Double>();
                    newGrades.add(gradeSeasons.get(season).get(0) + grade);
                    newGrades.add(gradeSeasons.get(season).get(1) + 1);
                    newGrades.add(newGrades.get(0) / newGrades.get(1));
                    gradeSeasons.put(season, newGrades);
                } else {
                    ArrayList<Double> newGrades = new ArrayList<>();
                    newGrades.add(grade);
                    newGrades.add((double)1);
                    newGrades.add(grade);
                    gradeSeasons.put(season, newGrades);
                }
                Double sum = 0.0;
                for (int i = 0; i < gradeSeasons.size(); i++) {
                    sum += gradeSeasons.get(i).get(0);
                }
                Double average = sum / gradeSeasons.size();
                this.setAverage(average);
                user.setNrRatings(user.getNrRatings() + 1);
                return Constants.SUCCESS + title + Constants.WAS_RATED + grade + Constants.BY + user.getUsername();
            } else {
                return Constants.ERROR + title + Constants.NOT_SEEN;
            }
        }
    }

    public static ArrayList<String> getResult(LinkedHashMap<String, Integer> serialsLinkedHashMap, String sortType, int n) {
        List<Map.Entry<String, Integer>> serialsSorted = new ArrayList<Map.Entry<String, Integer>>(serialsLinkedHashMap.entrySet());
        ArrayList<String> serialsNames = new ArrayList<String >();

        Collections.sort(serialsSorted, new Comparator<Map.Entry<String, Integer>>() {
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

    public static String getSortedSerials(ArrayList<Serial> serials, ArrayList<User> users,
                                          int n, String sortType) {
        LinkedHashMap<String, Integer> serialsLinkedHashMap = new LinkedHashMap<String, Integer>();
        ArrayList<String> serialsNames = new ArrayList<String >();

        for (User user : users) {
            for (Serial serial : serials) {
                if (user.getHistory().containsKey(serial.getTitle())) {
                    if (serialsLinkedHashMap.containsKey(serial.getTitle())) {
                        serialsLinkedHashMap.put(serial.getTitle(), serialsLinkedHashMap.get(serial.getTitle())
                                + user.getHistory().get(serial.getTitle()));
                    } else {
                        serialsLinkedHashMap.put(serial.getTitle(), user.getHistory().get(serial.getTitle()));
                    }
                }
            }
        }

        serialsNames = getResult(serialsLinkedHashMap, sortType, n);

        return Constants.QUERY_RESULT + serialsNames.toString();
    }

    public int getDuration(Serial s1) {
        int duration = 0;
        for (int i = 0; i < s1.getNumberOfSeasons(); i++) {
            duration += s1.getSeasons().get(i).getDuration();
        }
        return duration;
    }

    public static Comparator<Serial> getLongestSerialList() {
        return new Comparator<Serial>() {
            public int compare(Serial s1, Serial s2) {
                if (s1.getDuration(s1) == s2.getDuration(s2)) {
                    return s1.getTitle().compareTo(s2.getTitle());
                } else {
                    return s1.getDuration(s1) - s2.getDuration(s2);
                }
            }
        };
    }

    public static String getLongestSerials(ArrayList<Serial> serials, int n, String sortType) {
        ArrayList<String> serialsNames = new ArrayList<String >();

        if (sortType.equals("asc")) {
            serials.sort(Serial.getLongestSerialList());
        } else {
            serials.sort(Serial.getLongestSerialList());
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

    public static String getFavoriteSerials(ArrayList<Serial> serials, ArrayList<User> users, int n, String sortType) {
        ArrayList<String> serialsNames = new ArrayList<>();
        LinkedHashMap<String, Integer> serialsFavourite = new LinkedHashMap<String, Integer>();

        for (int i = 0; i < serials.size(); i++) {
            for (User user : users) {
                if (user.getFavoriteMovies().contains(serials.get(i).getTitle())) {
                    if (serialsFavourite.containsKey(serials.get(i).getTitle())) {
                        serialsFavourite.put(serials.get(i).getTitle(), serialsFavourite.get(serials.get(i).getTitle()) + 1);
                    } else {
                        serialsFavourite.put(serials.get(i).getTitle(), 1);
                    }
                }
            }
        }

        serialsNames = getResult(serialsFavourite, sortType, n);

        return Constants.QUERY_RESULT + serialsNames.toString();
    }

    public static ArrayList<String> getResultRatingSerial(LinkedHashMap<String, Double> serialsLinkedHashMap, String sortType, int n) {
        List<Map.Entry<String, Double>> serialsSorted = new ArrayList<Map.Entry<String, Double>>(serialsLinkedHashMap.entrySet());

        Collections.sort(serialsSorted, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                if (o1.getValue().equals(o2.getValue())) {
                    return (o1.getKey().compareTo(o2.getKey()));
                } else {
                    return (int)((o1.getValue() - o2.getValue()) * 100);
                }
            }
        });

        if (sortType.equals("desc")) {
            Collections.reverse(serialsSorted);
        }

        ArrayList<String> serilasNames = new ArrayList<String >();

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

    public static String getRatingSerials(ArrayList<Serial> serials, int n, String sortType) {
        ArrayList<String> serialsNames = new ArrayList<String>();

        LinkedHashMap<String, Double> serialsRated = new LinkedHashMap<String, Double>();

        for (Serial serial :  serials) {
            if(!serialsRated.containsKey(serial.getTitle()) && serial.Average() != 0) {
                serialsRated.put(serial.getTitle(), serial.Average());
            }
        }

        serialsNames = getResultRatingSerial(serialsRated, sortType, n);
        return Constants.QUERY_RESULT + serialsNames;
    }
}
