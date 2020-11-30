package main;


import common.Constants;
import fileio.Input;
import fileio.Writer;
import org.json.simple.JSONArray;

import java.io.IOException;
import java.util.ArrayList;

public final class ActionQuery {
    private ActionQuery() { };

    /**
     * Accesare fct pt interogari prima parte
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
    public static void queryPart1(final Input input, final Writer fileWriter,
                             final JSONArray arrayResult,
                             final  ArrayList<User> usersList,
                             final ArrayList<Movie> moviesList,
                             final  ArrayList<Serial> serialsList,
                             final ArrayList<Actor> actorList,
                             final ArrayList<Show> showList,
                             final int i) throws IOException {
        if (input.getCommands().get(i).getActionType().equals(Constants.QUERY)) {
            if (input.getCommands().get(i).getCriteria().equals(Constants.LONGEST)) {
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
        }
    }
}
