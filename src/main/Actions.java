package main;

import common.Constants;

public class Actions {
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
