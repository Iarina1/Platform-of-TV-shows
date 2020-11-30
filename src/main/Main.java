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
            ActionCommand.command(input, fileWriter, arrayResult,
                    usersList, moviesList, serialsList, i);
            ActionQuery.queryPart1(input, fileWriter, arrayResult,
                    usersList, moviesList, serialsList, actorList, showList, i);
            NewActionQuery.queryPart2(input, fileWriter, arrayResult,
                    usersList, moviesList, serialsList, actorList, showList, i);
            ActionRecommendation.recommendation(input, fileWriter, arrayResult,
                    usersList, showList, i);
        }
        fileWriter.closeJSON(arrayResult);
    }
}
