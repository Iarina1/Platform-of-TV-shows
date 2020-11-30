package main;

import common.Constants;
import fileio.Input;
import fileio.Writer;
import org.json.simple.JSONArray;

import java.io.IOException;
import java.util.ArrayList;

public final class NewActionQuery {
    private NewActionQuery() { };

    /**
     * Accesare fct pt interogari a 2a parte
     * @param input
     * @param fileWriter
     * @param arrayResult
     * @param usersList
     * @param moviesList
     * @param serialsList
     * @param actorList
     * @param showList
     * @param i
     * @throws IOException
     */
    public static void queryPart2(final Input input, final Writer fileWriter,
                                  final JSONArray arrayResult,
                                  final ArrayList<User> usersList,
                                  final ArrayList<Movie> moviesList,
                                  final  ArrayList<Serial> serialsList,
                                  final ArrayList<Actor> actorList,
                                  final ArrayList<Show> showList,
                                  final int i) throws IOException {
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
        }
    }
}
