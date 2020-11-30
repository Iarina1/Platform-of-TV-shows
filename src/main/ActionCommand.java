package main;

import common.Constants;
import fileio.Input;
import fileio.Writer;
import org.json.simple.JSONArray;

import java.io.IOException;
import java.util.ArrayList;

public final class ActionCommand {
    private ActionCommand() { };

    /**
     * Accesare fct pt comenzi
     * @param input
     * @param fileWriter
     * @param arrayResult
     * @param usersList
     * @param moviesList
     * @param serialsList
     * @param i
     * @throws IOException
     */
    public static void command(final Input input, final Writer fileWriter,
                               final JSONArray arrayResult,
                               final ArrayList<User> usersList,
                               final ArrayList<Movie> moviesList,
                               final ArrayList<Serial> serialsList,
                               final int i) throws IOException {
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
    }
}
