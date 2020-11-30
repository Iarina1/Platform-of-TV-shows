package main;

import common.Constants;
import fileio.Input;
import fileio.Writer;
import org.json.simple.JSONArray;

import java.io.IOException;
import java.util.ArrayList;

public final class ActionRecommendation {
    private ActionRecommendation() { };

    /**
     * Accesare fct pt recomandari
     * @param input
     * @param fileWriter
     * @param arrayResult
     * @param usersList
     * @param showList
     * @param i
     * @throws IOException
     */
    public static void recommendation(final Input input, final Writer fileWriter,
                             final JSONArray arrayResult,
                             final ArrayList<User> usersList,
                             final ArrayList<Show> showList,
                             final int i) throws IOException {
        if (input.getCommands().get(i).getActionType()
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
}
