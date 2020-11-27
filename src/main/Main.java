package main;

import checker.Checkstyle;
import checker.Checker;
import common.Constants;
import fileio.*;
import org.json.simple.JSONArray;

import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

/**
 * The entry point to this homework. It runs the checker that tests your implentation.
 */
public final class Main {
    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * Call the main checker and the coding style checker
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(Constants.TESTS_PATH);
        Path path = Paths.get(Constants.RESULT_PATH);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        File outputDirectory = new File(Constants.RESULT_PATH);

        Checker checker = new Checker();
        checker.deleteFiles(outputDirectory.listFiles());

        for (File file : Objects.requireNonNull(directory.listFiles())) {

            String filepath = Constants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getAbsolutePath(), filepath);
            }
        }

        checker.iterateFiles(Constants.RESULT_PATH, Constants.REF_PATH, Constants.TESTS_PATH);
        Checkstyle test = new Checkstyle();
        test.testCheckstyle();
    }

    /**
     * @param filePath1 for input file
     * @param filePath2 for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePath1,
                              final String filePath2) throws IOException {
        InputLoader inputLoader = new InputLoader(filePath1);
        Input input = inputLoader.readData();

        Writer fileWriter = new Writer(filePath2);
        JSONArray arrayResult = new JSONArray();

        //TODO add here the entry point to your implementation
        ArrayList<String> movieArrayList = new ArrayList<String>();

        ArrayList<User> usersList = new ArrayList<>();
        for (UserInputData userInputData : input.getUsers()) {
            User user = new User(userInputData.getUsername(), userInputData.getSubscriptionType(),
                    userInputData.getHistory(), userInputData.getFavoriteMovies());
            usersList.add(user);
        }

        ArrayList<Movie> moviesList = new ArrayList<>();
        for (MovieInputData movieInputData : input.getMovies()) {
            Movie movie = new Movie(movieInputData.getTitle(), movieInputData.getCast(),
                    movieInputData.getGenres(), movieInputData.getYear(), movieInputData.getDuration());
            moviesList.add(movie);
        }

        ArrayList<Serial> serialsList = new ArrayList<>();
        for (SerialInputData serialInputData : input.getSerials()) {
            Serial serial = new Serial(serialInputData.getTitle(), serialInputData.getCast(),
                    serialInputData.getGenres(), serialInputData.getNumberSeason(),
                    serialInputData.getSeasons(), serialInputData.getYear());
            serialsList.add(serial);
        }

        ArrayList<Actor> actorList = new ArrayList<>();
        for (ActorInputData actorInputData : input.getActors()) {
            Actor actor = new Actor(actorInputData.getName(), actorInputData.getCareerDescription(),
                    actorInputData.getFilmography(), actorInputData.getAwards());
            actorList.add(actor);
        }

        for (int i = 0; i < input.getCommands().size(); i++) {
            if (input.getCommands().get(i).getActionType().equals(Constants.COMMAND)) {
                if (input.getCommands().get(i).getType().equals((Constants.FAVORITE))) {
                    for (int j = 0; j < usersList.size(); j++) {
                        if (input.getCommands().get(i).getUsername()
                                .equals(usersList.get(j).getUsername())) {
                            arrayResult.add(fileWriter.writeFile(input.getCommands().get(i)
                                            .getActionId(), null,
                                    usersList.get(j).addFavoriteList(input.getCommands().get(i)
                                            .getTitle())));
                        }
                    }
                } else if (input.getCommands().get(i).getType().equals((Constants.VIEW))) {
                    for (int j = 0; j < usersList.size(); j++) {
                        if (input.getCommands().get(i).getUsername().equals(usersList.get(j).getUsername())) {
                            arrayResult.add(fileWriter.writeFile(input.getCommands().get(i)
                                            .getActionId(), null,
                                    usersList.get(j).timesViewed(input.getCommands().get(i)
                                            .getTitle())));
                        }
                    }
                } else if (input.getCommands().get(i).getType().equals(Constants.RATING)) {
                    for (int j = 0; j < usersList.size(); j++) {
                        if (input.getCommands().get(i).getUsername().equals(usersList.get(j).getUsername())) {
                            for (int k = 0; k < input.getMovies().size(); k++) {
                                if (input.getMovies().get(k).getTitle().equals(input.getCommands()
                                        .get(i).getTitle())) {
                                    for (Movie movie : moviesList) {
                                        if (movie.getTitle().equals(input.getMovies().get(k).getTitle())) {
                                            arrayResult.add(fileWriter.writeFile(input.getCommands()
                                                        .get(i).getActionId(), null,
                                                movie.getRating(input.getCommands().get(i).getTitle(),
                                                        input.getCommands().get(i).getGrade(),
                                                        usersList.get(j))));
                                        }
                                    }
                                }
                            }
                            for (int k = 0; k < input.getSerials().size(); k++) {
                                if (input.getSerials().get(k).getTitle().equals(input.getCommands()
                                        .get(i).getTitle())) {
                                    Serial serial = new Serial(input.getSerials().get(k).getTitle(),
                                            input.getSerials().get(k).getCast(),
                                            input.getSerials().get(k).getGenres(), input.getSerials()
                                            .get(k).getNumberSeason(),
                                            input.getSerials().get(k).getSeasons(), input.getSerials()
                                            .get(k).getYear());
                                    arrayResult.add(fileWriter.writeFile(input.getCommands().get(i)
                                                    .getActionId(), null,
                                            serial.getRating(input.getCommands().get(i).getTitle(),
                                                    input.getCommands().get(i).getGrade(),
                                                    usersList.get(j), input.getCommands()
                                                            .get(i).getSeasonNumber())));
                                }
                            }
                        }
                    }
                }
            } if (input.getCommands().get(i).getActionType().equals(Constants.QUERY)) {
                if (input.getCommands().get(i).getObjectType().equals(Constants.USERS)) {
                    arrayResult.add(fileWriter.writeFile(input.getCommands().get(i)
                            .getActionId(), null, User.getSortedList(usersList,
                            input.getCommands().get(i).getNumber(), input.getCommands().get(i).getSortType()).toString()));
                } else if (input.getCommands().get(i).getCriteria().equals(Constants.MOST_VIEWED)) {
                    if (input.getCommands().get(i).getObjectType().equals(Constants.MOVIES)) {
                        for (int j = moviesList.size() - 1; j >= 0; j--) {
                            if ((input.getCommands().get(i).getFilters().get(0).get(0) != null) && (!input
                                    .getCommands().get(i).getFilters().get(0).get(0)
                                    .equals(Integer.toString(moviesList.get(j).getYear())))) {
                                moviesList.remove(j);
                                continue;
                            } else if ((input.getCommands().get(i).getFilters().get(1).get(0) != null) && (!moviesList
                                    .get(j).getGenres().contains(input.getCommands()
                                            .get(i).getFilters().get(1).get(0)))) {
                                moviesList.remove(j);
                            }
                        }
                        arrayResult.add(fileWriter.writeFile(input.getCommands().get(i)
                                .getActionId(), null, Movie.getSortedMovies(moviesList, usersList,
                                input.getCommands().get(i).getNumber(), input.getCommands().get(i)
                        .getSortType())));
                    } else if (input.getCommands().get(i).getObjectType().equals(Constants.SHOWS)) {
                        for (int j = serialsList.size() - 1; j >= 0; j--) {
                            if ((input.getCommands().get(i).getFilters().get(0).get(0) != null) && (!input
                                    .getCommands().get(i).getFilters().get(0).get(0)
                                    .equals(Integer.toString(serialsList.get(j).getYear())))) {
                                serialsList.remove(j);
                                continue;
                            } else if ((input.getCommands().get(i).getFilters().get(1).get(0) != null) && (!serialsList
                                    .get(j).getGenres().contains(input.getCommands()
                                            .get(i).getFilters().get(1).get(0)))) {
                                serialsList.remove(j);
                            }
                        }
                        arrayResult.add(fileWriter.writeFile(input.getCommands().get(i)
                                .getActionId(), null, Serial.getSortedSerials(serialsList, usersList,
                                input.getCommands().get(i).getNumber(), input.getCommands().get(i)
                                        .getSortType())));
                    }
                } else if (input.getCommands().get(i).getCriteria().equals(Constants.LONGEST)) {
                    if (input.getCommands().get(i).getObjectType().equals(Constants.MOVIES)) {
                        for (int j = moviesList.size() - 1; j >= 0; j--) {
                            if ((input.getCommands().get(i).getFilters().get(0).get(0) != null) && (!input
                                    .getCommands().get(i).getFilters().get(0).get(0)
                                    .equals(Integer.toString(moviesList.get(j).getYear())))) {
                                moviesList.remove(j);
                                continue;
                            } else if ((input.getCommands().get(i).getFilters().get(1).get(0) != null) && (!moviesList
                                    .get(j).getGenres().contains(input.getCommands()
                                            .get(i).getFilters().get(1).get(0)))) {
                                moviesList.remove(j);
                            }
                        }
                        arrayResult.add(fileWriter.writeFile(input.getCommands().get(i)
                                .getActionId(), null, Movie.getLongestMovies(moviesList, input
                                .getCommands().get(i).getNumber(), input.getCommands().get(i).getSortType())));
                    } else if (input.getCommands().get(i).getObjectType().equals(Constants.SHOWS)) {
                        for (int j = serialsList.size() - 1; j >= 0; j--) {
                            if ((input.getCommands().get(i).getFilters().get(0).get(0) != null) && (!input
                                    .getCommands().get(i).getFilters().get(0).get(0)
                                    .equals(Integer.toString(serialsList.get(j).getYear())))) {
                                serialsList.remove(j);
                                continue;
                            } else if ((input.getCommands().get(i).getFilters().get(1).get(0) != null) && (!serialsList
                                    .get(j).getGenres().contains(input.getCommands()
                                            .get(i).getFilters().get(1).get(0)))) {
                                serialsList.remove(j);
                            }
                        }
                        arrayResult.add(fileWriter.writeFile(input.getCommands().get(i)
                                .getActionId(), null, Serial.getLongestSerials(serialsList, input
                        .getCommands().get(i).getNumber(), input.getCommands().get(i).getSortType())));
                    }
                } else if (input.getCommands().get(i).getCriteria().equals(Constants.FAVORITE)) {
                    if (input.getCommands().get(i).getObjectType().equals(Constants.MOVIES)) {
                        for (int j = moviesList.size() - 1; j >= 0; j--) {
                            if ((input.getCommands().get(i).getFilters().get(0).get(0) != null) && (!input
                                    .getCommands().get(i).getFilters().get(0).get(0)
                                    .equals(Integer.toString(moviesList.get(j).getYear())))) {
                                moviesList.remove(j);
                                continue;
                            } else if ((input.getCommands().get(i).getFilters().get(1).get(0) != null) && (!moviesList
                                    .get(j).getGenres().contains(input.getCommands()
                                            .get(i).getFilters().get(1).get(0)))) {
                                moviesList.remove(j);
                            }
                        }
                        arrayResult.add(fileWriter.writeFile(input.getCommands().get(i)
                                .getActionId(), null, Movie.getFavoriteMovies(moviesList,
                                usersList, input.getCommands().get(i).getNumber(),
                                input.getCommands().get(i).getSortType())));
                    } else if (input.getCommands().get(i).getObjectType().equals(Constants.SHOWS)) {
                        for (int j = serialsList.size() - 1; j >= 0; j--) {
                            if ((input.getCommands().get(i).getFilters().get(0).get(0) != null) && (!input
                                    .getCommands().get(i).getFilters().get(0).get(0)
                                    .equals(Integer.toString(serialsList.get(j).getYear())))) {
                                serialsList.remove(j);
                                continue;
                            } else if ((input.getCommands().get(i).getFilters().get(1).get(0) != null) && (!serialsList
                                    .get(j).getGenres().contains(input.getCommands()
                                            .get(i).getFilters().get(1).get(0)))) {
                                serialsList.remove(j);
                            }
                        }

                        arrayResult.add(fileWriter.writeFile(input.getCommands().get(i)
                                .getActionId(), null, Serial.getFavoriteSerials(serialsList,
                                usersList, input.getCommands().get(i).getNumber(),
                                input.getCommands().get(i).getSortType())));
                    }
                } else if (input.getCommands().get(i).getCriteria().equals(Constants.RATINGS)) {
                    if (input.getCommands().get(i).getObjectType().equals(Constants.MOVIES)) {
                        for (int j = moviesList.size() - 1; j >= 0; j--) {
                            if ((input.getCommands().get(i).getFilters().get(0).get(0) != null) && (!input
                                    .getCommands().get(i).getFilters().get(0).get(0)
                                    .equals(Integer.toString(moviesList.get(j).getYear())))) {
                                moviesList.remove(j);
                                continue;
                            } else if ((input.getCommands().get(i).getFilters().get(1).get(0) != null) && (!moviesList
                                    .get(j).getGenres().contains(input.getCommands()
                                            .get(i).getFilters().get(1).get(0)))) {
                                moviesList.remove(j);
                            }
                        }
                        Movie.getRatingMovies(moviesList, input.getCommands().get(i).getNumber(), input.getCommands().get(i).getSortType());
                        arrayResult.add(fileWriter.writeFile(input.getCommands().get(i)
                                .getActionId(), null, Movie.getRatingMovies(moviesList, input.getCommands().get(i).getNumber(),
                                input.getCommands().get(i).getSortType())));
                    } else if (input.getCommands().get(i).getObjectType().equals(Constants.SHOWS)) {
                        for (int j = serialsList.size() - 1; j >= 0; j--) {
                            if ((input.getCommands().get(i).getFilters().get(0).get(0) != null) && (!input
                                    .getCommands().get(i).getFilters().get(0).get(0)
                                    .equals(Integer.toString(serialsList.get(j).getYear())))) {
                                serialsList.remove(j);
                                continue;
                            } else if ((input.getCommands().get(i).getFilters().get(1).get(0) != null) && (!serialsList
                                    .get(j).getGenres().contains(input.getCommands()
                                            .get(i).getFilters().get(1).get(0)))) {
                                serialsList.remove(j);
                            }
                        }

                        arrayResult.add(fileWriter.writeFile(input.getCommands().get(i)
                                .getActionId(), null, Serial.getRatingSerials(serialsList,
                                input.getCommands().get(i).getNumber(),
                                input.getCommands().get(i).getSortType())));
                    }
                } if (input.getCommands().get(i).getObjectType().equals(Constants.ACTORS)) {
                    if (input.getCommands().get(i).getCriteria().equals(Constants.FILTER_DESCRIPTIONS)) {
                        arrayResult.add(fileWriter.writeFile(input.getCommands().get(i)
                                .getActionId(), null, Actor.getFilterDescription(actorList,
                                input.getCommands().get(i).getFilters().get(2),
                                input.getCommands().get(i).getSortType())));
                    } else if (input.getCommands().get(i).getCriteria().equals(Constants.AWARDS)) {
                        arrayResult.add(fileWriter.writeFile(input.getCommands().get(i)
                                .getActionId(), null, Actor.getQueryAwards( actorList,
                                input.getCommands().get(i).getFilters().get(3), input
                                .getCommands().get(i).getSortType())));
                    }
                }
            }
        }
        fileWriter.closeJSON(arrayResult);
    }
}



