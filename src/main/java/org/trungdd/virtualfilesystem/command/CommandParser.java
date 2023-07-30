package org.trungdd.virtualfilesystem.command;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Scope("prototype")
public class CommandParser {

    private static final String VALID_NAME_REGEX = "^[a-zA-Z0-9 _-]+$";
    private static final String COMMAND_REGEX = "(\\w+)";
    private static final String FLAGS_REGEX = "(?:\\s*-(\\w+))?";
    private static final String PARAMS_REGEX = "\\s*(.*)";

    public static boolean isValidName(String name) {
        return Pattern.matches(VALID_NAME_REGEX, name);
    }

    public static List<String> parseParams(String input) {
        List<String> tokens = new ArrayList<>();
        Pattern pattern = Pattern.compile("\"([^\"]*)\"|(\\S+)");
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            String token = matcher.group(1);
            if (token == null) {
                token = matcher.group(2);
            }
            tokens.add(token);
        }

        return tokens;
    }

    public Command parseCommand(String commandText) {
        // Initialize the command object
        Command command = new Command();

        // Define the regex pattern for the whole command text
        String regexPattern = "^" + COMMAND_REGEX + FLAGS_REGEX + PARAMS_REGEX + "$";
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(commandText);

        if (matcher.matches()) {
            // Extract command type
            String commandType = matcher.group(1);
            switch (commandType) {
                case "cd":
                    command.setCommand(CommandType.CD);
                    break;
                case "cr":
                    command.setCommand(CommandType.CR);
                    break;
                case "cat":
                    command.setCommand(CommandType.CAT);
                    break;
                case "ls":
                    command.setCommand(CommandType.LS);
                    break;
                case "find":
                    command.setCommand(CommandType.FIND);
                    break;
                case "up":
                    command.setCommand(CommandType.UP);
                    break;
                case "mv":
                    command.setCommand(CommandType.MV);
                    break;
                case "rm":
                    command.setCommand(CommandType.RM);
                    break;
                default:
                    command.setCommand(CommandType.UNKNOWN);
                    break;
            }

            // Extract flags (if present)
            String flagsString = matcher.group(2);
            List<String> flags = new ArrayList<>();
            if (flagsString != null && !flagsString.isEmpty()) {
                for (int i = 0; i < flagsString.length(); ++i) {
                    flags.add(flagsString.substring(i, 1));
                }
            }
            command.setFlags(flags);

            // Extract parameters
            List<String> params;
            String paramsText = matcher.group(3);
            paramsText = paramsText.trim();

            if (paramsText != null && !paramsText.isEmpty()) {
                params = parseParams(paramsText);
            } else {
                params = new ArrayList<>();
            }
            command.setParams(params);
        }

        return command;
    }
}
