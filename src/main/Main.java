package main;

import checker.Checkstyle;
import checker.Checker;
import common.Constants;
import fileio.ActorInputData;
import fileio.Input;
import fileio.InputLoader;
import fileio.MovieInputData;
import fileio.SerialInputData;
import fileio.UserInputData;
import fileio.Writer;
import org.json.simple.JSONArray;

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
     *
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

        ArrayList<User> usersList = new ArrayList<>();
        for (UserInputData userInputData : input.getUsers()) {
            User user = new User(userInputData.getUsername(), userInputData.getSubscriptionType(),
                    userInputData.getHistory(), userInputData.getFavoriteMovies());
            usersList.add(user);
        }

        ArrayList<Show> showList = new ArrayList<>();

        ArrayList<Movie> moviesList = new ArrayList<>();
        for (MovieInputData movieInputData : input.getMovies()) {
            Movie movie = new Movie(movieInputData.getTitle(), movieInputData.getCast(),
                    movieInputData.getGenres(), movieInputData.getYear(),
                    movieInputData.getDuration());
            moviesList.add(movie);
            showList.add(movie);
        }

        ArrayList<Serial> serialsList = new ArrayList<>();
        for (SerialInputData serialInputData : input.getSerials()) {
            Serial serial = new Serial(serialInputData.getTitle(), serialInputData.getCast(),
                    serialInputData.getGenres(), serialInputData.getNumberSeason(),
                    serialInputData.getSeasons(), serialInputData.getYear());
            serialsList.add(serial);
            showList.add(serial);
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
                    for (User user : usersList) {
                        if (input.getCommands().get(i).getUsername()
                                .equals(user.getUsername())) {
                            arrayResult.add(fileWriter.writeFile(input.getCommands().get(i)
                                            .getActionId(), null,
                                    user.addFavoriteList(input.getCommands().get(i)
                                            .getTitle())));
                        }
                    }
                } else if (input.getCommands().get(i).getType().equals((Constants.VIEW))) {
                    for (User user : usersList) {
                        if (input.getCommands().get(i).getUsername().equals(user.getUsername())) {
                            arrayResult.add(fileWriter.writeFile(input.getCommands().get(i)
                                            .getActionId(), null,
                                    user.timesViewed(input.getCommands().get(i)
                                            .getTitle())));
                        }
                    }
                } else if (input.getCommands().get(i).getType().equals(Constants.RATING)) {
                    for (User user : usersList) {
                        if (input.getCommands().get(i).getUsername().equals(user.getUsername())) {
                            for (Movie movie : moviesList) {
                                if (movie.getTitle().equals(input.getCommands()
                                        .get(i).getTitle())) {
                                    arrayResult.add(fileWriter.writeFile(input.getCommands()
                                                    .get(i).getActionId(), null,
                                            movie.getRating(input.getCommands().get(i).getTitle(),
                                                    input.getCommands().get(i).getGrade(), user)));
                                }
                            }
                            for (Serial serial : serialsList) {
                                if (serial.getTitle().equals(input.getCommands()
                                        .get(i).getTitle())) {
                                    arrayResult.add(fileWriter.writeFile(input.getCommands().get(i)
                                                    .getActionId(), null,
                                            serial.getRating(input.getCommands().get(i).getTitle(),
                                                    input.getCommands().get(i).getGrade(),
                                                    user, input.getCommands().get(i)
                                                            .getSeasonNumber())));
                                }
                            }
                        }
                    }
                }
            }
            if (input.getCommands().get(i).getActionType().equals(Constants.QUERY)) {
                if (input.getCommands().get(i).getObjectType().equals(Constants.USERS)) {
                    arrayResult.add(fileWriter.writeFile(input.getCommands().get(i)
                            .getActionId(), null, User.getSortedList(usersList,
                            input.getCommands().get(i).getNumber(),
                            input.getCommands().get(i).getSortType())));
                } else if (input.getCommands().get(i).getCriteria().equals(Constants.MOST_VIEWED)) {
                    if (input.getCommands().get(i).getObjectType().equals(Constants.MOVIES)) {
                        ArrayList<Show> moviesListBackup = new ArrayList<>(moviesList);
                        for (int j = moviesListBackup.size() - 1; j >= 0; j--) {
                            if ((input.getCommands().get(i).getFilters()
                                    .get(0).get(0) != null) && (!input
                                    .getCommands().get(i).getFilters().get(0).get(0)
                                    .equals(Integer.toString(moviesListBackup.get(j).getYear())))) {
                                moviesListBackup.remove(j);
                                continue;
                            } else if ((input.getCommands().get(i).getFilters()
                                    .get(1).get(0) != null) && (!moviesListBackup
                                    .get(j).getGenres().contains(input.getCommands()
                                            .get(i).getFilters().get(1).get(0)))) {
                                moviesListBackup.remove(j);
                            }
                        }
                        arrayResult.add(fileWriter.writeFile(input.getCommands().get(i)
                                .getActionId(), null, Show.getSortedShow(
                                        moviesListBackup, usersList,
                                input.getCommands().get(i).getNumber(), input.getCommands().get(i)
                                        .getSortType())));
                    } else if (input.getCommands().get(i).getObjectType().equals(Constants.SHOWS)) {
                        ArrayList<Show> serialsListBackup = new ArrayList<>(serialsList);
                        for (int j = serialsList.size() - 1; j >= 0; j--) {
                            if ((input.getCommands().get(i).getFilters()
                                    .get(0).get(0) != null) && (!input
                                    .getCommands().get(i).getFilters().get(0).get(0)
                                    .equals(Integer.toString(serialsListBackup.get(j)
                                            .getYear())))) {
                                serialsListBackup.remove(j);
                                continue;
                            } else if ((input.getCommands().get(i).getFilters()
                                    .get(1).get(0) != null) && (!serialsListBackup
                                    .get(j).getGenres().contains(input.getCommands()
                                            .get(i).getFilters().get(1).get(0)))) {
                                serialsListBackup.remove(j);
                            }
                        }
                        arrayResult.add(fileWriter.writeFile(input.getCommands().get(i)
                                .getActionId(), null, Show.getSortedShow(
                                        serialsListBackup, usersList,
                                input.getCommands().get(i).getNumber(), input.getCommands().get(i)
                                        .getSortType())));
                    }
                } else if (input.getCommands().get(i).getCriteria().equals(Constants.LONGEST)) {
                    if (input.getCommands().get(i).getObjectType().equals(Constants.MOVIES)) {
                        ArrayList<Movie> moviesListBackup = new ArrayList<>(moviesList);
                        for (int j = moviesListBackup.size() - 1; j >= 0; j--) {
                            if ((input.getCommands().get(i).getFilters()
                                    .get(0).get(0) != null) && (!input
                                    .getCommands().get(i).getFilters().get(0).get(0)
                                    .equals(Integer.toString(moviesListBackup.get(j).getYear())))) {
                                moviesListBackup.remove(j);
                                continue;
                            } else if ((input.getCommands().get(i).getFilters()
                                    .get(1).get(0) != null) && (!moviesListBackup
                                    .get(j).getGenres().contains(input.getCommands()
                                            .get(i).getFilters().get(1).get(0)))) {
                                moviesListBackup.remove(j);
                            }
                        }
                        arrayResult.add(fileWriter.writeFile(input.getCommands().get(i)
                                .getActionId(), null, Movie.getLongestMovies(moviesListBackup, input
                                .getCommands().get(i).getNumber(), input.getCommands()
                                .get(i).getSortType())));
                    } else if (input.getCommands().get(i).getObjectType().equals(Constants.SHOWS)) {
                        ArrayList<Serial> serialsListBackup = new ArrayList<>(serialsList);
                        for (int j = serialsListBackup.size() - 1; j >= 0; j--) {
                            if ((input.getCommands().get(i).getFilters()
                                    .get(0).get(0) != null) && (!input
                                    .getCommands().get(i).getFilters().get(0).get(0)
                                    .equals(Integer.toString(serialsListBackup.get(j)
                                            .getYear())))) {
                                serialsListBackup.remove(j);
                                continue;
                            } else if ((input.getCommands().get(i).getFilters()
                                    .get(1).get(0) != null) && (!serialsListBackup
                                    .get(j).getGenres().contains(input.getCommands()
                                            .get(i).getFilters().get(1).get(0)))) {
                                serialsListBackup.remove(j);
                            }
                        }
                        arrayResult.add(fileWriter.writeFile(input.getCommands().get(i)
                                .getActionId(), null, Serial.getLongestSerials(
                                        serialsListBackup, input
                                .getCommands().get(i).getNumber(), input.getCommands()
                                .get(i).getSortType())));
                    }
                } else if (input.getCommands().get(i).getCriteria().equals(Constants.FAVORITE)) {
                    if (input.getCommands().get(i).getObjectType().equals(Constants.MOVIES)) {
                        ArrayList<Movie> moviesListBackup = new ArrayList<>(moviesList);
                        for (int j = moviesListBackup.size() - 1; j >= 0; j--) {
                            if ((input.getCommands().get(i).getFilters()
                                    .get(0).get(0) != null) && (!input
                                    .getCommands().get(i).getFilters().get(0).get(0)
                                    .equals(Integer.toString(moviesListBackup.get(j).getYear())))) {
                                moviesListBackup.remove(j);
                                continue;
                            } else if ((input.getCommands().get(i).getFilters()
                                    .get(1).get(0) != null) && (!moviesListBackup
                                    .get(j).getGenres().contains(input.getCommands()
                                            .get(i).getFilters().get(1).get(0)))) {
                                moviesListBackup.remove(j);
                            }
                        }
                        arrayResult.add(fileWriter.writeFile(input.getCommands().get(i)
                                .getActionId(), null, Movie.getFavoriteMovies(moviesListBackup,
                                usersList, input.getCommands().get(i).getNumber(),
                                input.getCommands().get(i).getSortType())));
                    } else if (input.getCommands().get(i).getObjectType().equals(Constants.SHOWS)) {
                        ArrayList<Serial> serialsListBackup = new ArrayList<>(serialsList);
                        for (int j = serialsListBackup.size() - 1; j >= 0; j--) {
                            if ((input.getCommands().get(i).getFilters()
                                    .get(0).get(0) != null) && (!input
                                    .getCommands().get(i).getFilters().get(0).get(0)
                                    .equals(Integer.toString(serialsListBackup
                                            .get(j).getYear())))) {
                                serialsListBackup.remove(j);
                                continue;
                            } else if ((input.getCommands().get(i).getFilters()
                                    .get(1).get(0) != null) && (!serialsListBackup
                                    .get(j).getGenres().contains(input.getCommands()
                                            .get(i).getFilters().get(1).get(0)))) {
                                serialsListBackup.remove(j);
                            }
                        }
                        arrayResult.add(fileWriter.writeFile(input.getCommands().get(i)
                                .getActionId(), null, Serial.getFavoriteSerials(serialsListBackup,
                                usersList, input.getCommands().get(i).getNumber(),
                                input.getCommands().get(i).getSortType())));
                    }
                } else if (input.getCommands().get(i).getCriteria().equals(Constants.RATINGS)) {
                    if (input.getCommands().get(i).getObjectType().equals(Constants.MOVIES)) {
                        ArrayList<Movie> moviesListBackup = new ArrayList<>(moviesList);
                        for (int j = moviesListBackup.size() - 1; j >= 0; j--) {
                            if ((input.getCommands().get(i).getFilters()
                                    .get(0).get(0) != null) && (!input
                                    .getCommands().get(i).getFilters().get(0).get(0)
                                    .equals(Integer.toString(moviesListBackup.get(j).getYear())))) {
                                moviesListBackup.remove(j);
                                continue;
                            } else if ((input.getCommands().get(i).getFilters()
                                    .get(1).get(0) != null) && (!moviesListBackup
                                    .get(j).getGenres().contains(input.getCommands()
                                            .get(i).getFilters().get(1).get(0)))) {
                                moviesListBackup.remove(j);
                            }
                        }
                        arrayResult.add(fileWriter.writeFile(input.getCommands().get(i)
                                .getActionId(), null, Movie.getRatingMovies(moviesListBackup,
                                input.getCommands().get(i).getNumber(),
                                input.getCommands().get(i).getSortType())));
                    } else if (input.getCommands().get(i).getObjectType().equals(Constants.SHOWS)) {
                        ArrayList<Serial> serialsListBackup = new ArrayList<>(serialsList);
                        for (int j = serialsListBackup.size() - 1; j >= 0; j--) {
                            if ((input.getCommands().get(i).getFilters()
                                    .get(0).get(0) != null) && (!input
                                    .getCommands().get(i).getFilters().get(0).get(0)
                                    .equals(Integer.toString(serialsListBackup
                                            .get(j).getYear())))) {
                                serialsListBackup.remove(j);
                                continue;
                            } else if ((input.getCommands().get(i).getFilters()
                                    .get(1).get(0) != null) && (!serialsListBackup
                                    .get(j).getGenres().contains(input.getCommands()
                                            .get(i).getFilters().get(1).get(0)))) {
                                serialsListBackup.remove(j);
                            }
                        }

                        arrayResult.add(fileWriter.writeFile(input.getCommands().get(i)
                                .getActionId(), null, Serial.getRatingSerials(serialsListBackup,
                                input.getCommands().get(i).getNumber(),
                                input.getCommands().get(i).getSortType())));
                    }
                }
                if (input.getCommands().get(i).getObjectType().equals(Constants.ACTORS)) {
                    if (input.getCommands().get(i).getCriteria()
                            .equals(Constants.FILTER_DESCRIPTIONS)) {
                        arrayResult.add(fileWriter.writeFile(input.getCommands().get(i)
                                .getActionId(), null, Actor.getFilterDescription(actorList,
                                input.getCommands().get(i).getFilters().get(Constants.SINGLE_TEST),
                                input.getCommands().get(i).getSortType())));
                    } else if (input.getCommands().get(i).getCriteria().equals(Constants.AWARDS)) {
                        arrayResult.add(fileWriter.writeFile(input.getCommands().get(i)
                                .getActionId(), null, Actor.getQueryAwards(actorList,
                                input.getCommands().get(i).getFilters()
                                        .get(Constants.LARGE_TEST), input
                                        .getCommands().get(i).getSortType())));
                    } else if (input.getCommands().get(i).getCriteria().equals(Constants.AVERAGE)) {
                        arrayResult.add(fileWriter.writeFile(input.getCommands().get(i)
                                .getActionId(), null, Actor
                                .getQueryAverage(actorList, input.getCommands()
                                .get(i).getNumber(), input.getCommands()
                                        .get(i).getSortType(), showList)));
                    }
                }
            } else if (input.getCommands().get(i).getActionType()
                    .equals(Constants.RECOMMENDATION)) {
                if (input.getCommands().get(i).getType().equals(Constants.STANDARD)) {
                    arrayResult.add(fileWriter.writeFile(input.getCommands().get(i)
                            .getActionId(), null, User.getRecommendationStandard(usersList, showList,
                            input.getCommands().get(i).getUsername())));
                } else if (input.getCommands().get(i).getType().equals(Constants.BEST_UNSEEN)) {
                    arrayResult.add(fileWriter.writeFile(input.getCommands().get(i)
                            .getActionId(), null, User.getBestUnseenRecommendation(usersList, showList,
                            input.getCommands().get(i).getUsername())));
                } else if (input.getCommands().get(i).getType().equals(Constants.SEARCH)) {
                    arrayResult.add(fileWriter.writeFile(input.getCommands().get(i)
                            .getActionId(), null, User.getSearchRecommendation(usersList, showList,
                            input.getCommands().get(i).getUsername(), input.getCommands().get(i)
                                    .getGenre())));
                } else if (input.getCommands().get(i).getType().equals(Constants.FAVORITE)) {
                    arrayResult.add(fileWriter.writeFile(input.getCommands().get(i)
                            .getActionId(), null, User.getFavourite(usersList, showList,
                            input.getCommands().get(i).getUsername())));
                } else if (input.getCommands().get(i).getType().equals(Constants.POPULAR)) {
                    arrayResult.add(fileWriter.writeFile(input.getCommands().get(i)
                            .getActionId(), null, User.getPopularRecommandation(usersList,
                            showList, input.getCommands().get(i).getUsername())));
                }
            }
        }
        fileWriter.closeJSON(arrayResult);
    }
}
