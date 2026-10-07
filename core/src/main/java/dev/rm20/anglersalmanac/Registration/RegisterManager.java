package dev.rm20.anglersalmanac.Registration;

import com.google.common.reflect.ClassPath;
import dev.rm20.anglersalmanac.AnglersAlmanac;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;

public class RegisterManager {

    public static void registerCommands(AnglersAlmanac plugin) {
        final String PACKAGE_NAME = "dev.rm20.anglersalmanac.Commands";
        try {
            ClassPath classPath = ClassPath.from(plugin.getClass().getClassLoader());
            java.util.Map<String, AbstractCommand> allCommands = new java.util.LinkedHashMap<>();
            java.util.Map<String, CommandInfo> commandInfos = new java.util.HashMap<>();
            java.util.List<AbstractCommand> rootCommands = new java.util.ArrayList<>();
            java.util.Map<String, java.util.List<AbstractCommand>> subCommandsByParent = new java.util.LinkedHashMap<>();

            for (ClassPath.ClassInfo classInfo : classPath.getTopLevelClassesRecursive(PACKAGE_NAME)) {
                Class<?> loadedClass = classInfo.load();
                if (AbstractCommand.class.isAssignableFrom(loadedClass) && loadedClass.isAnnotationPresent(CommandInfo.class)) {
                    CommandInfo info = loadedClass.getAnnotation(CommandInfo.class);
                    try {
                        AbstractCommand command;
                        try {
                            command = (AbstractCommand) loadedClass
                                    .getConstructor(String.class, String.class)
                                    .newInstance(info.name(), info.description());
                        } catch (NoSuchMethodException e1) {
                            try {
                                command = (AbstractCommand) loadedClass
                                        .getConstructor()
                                        .newInstance();
                            } catch (NoSuchMethodException e2) {
                                command = (AbstractCommand) loadedClass
                                        .getConstructor(String.class)
                                        .newInstance(info.name());
                            }
                        }

                        // Apply aliases if present
                        if (info.aliases().length > 0) {
                            command.addAliases(info.aliases());
                        }

                        // Apply permission if present
                        if (!info.permission().isEmpty()) {
                            command.requirePermission(info.permission());
                        }

                        String cmdName = info.name().toLowerCase(java.util.Locale.ROOT);
                        allCommands.put(cmdName, command);
                        commandInfos.put(cmdName, info);

                        for (String alias : info.aliases()) {
                            allCommands.putIfAbsent(alias.toLowerCase(java.util.Locale.ROOT), command);
                        }

                        if (info.parent().isEmpty()) {
                            rootCommands.add(command);
                        } else {
                            String parentKey = info.parent().toLowerCase(java.util.Locale.ROOT);
                            subCommandsByParent.computeIfAbsent(parentKey, k -> new java.util.ArrayList<>()).add(command);
                        }
                    } catch (Exception e) {
                        plugin.getLogger().atSevere().withCause(e).log("Could not instantiate command class: " + loadedClass.getSimpleName());
                    }
                }
            }

            // Attach subcommands to their respective parents
            int subCount = 0;
            for (java.util.Map.Entry<String, java.util.List<AbstractCommand>> entry : subCommandsByParent.entrySet()) {
                String parentName = entry.getKey();
                AbstractCommand parentCommand = allCommands.get(parentName);

                if (parentCommand != null) {
                    for (AbstractCommand subCommand : entry.getValue()) {
                        try {
                            parentCommand.addSubCommand(subCommand);
                            plugin.getLogger().atInfo().log("Registered subcommand '" + subCommand.getName() + "' under '" + parentCommand.getName() + "'");
                            subCount++;
                        } catch (Exception e) {
                            plugin.getLogger().atSevere().withCause(e).log("Failed to add subcommand '" + subCommand.getName() + "' to parent '" + parentName + "'");
                        }
                    }
                } else {
                    plugin.getLogger().atWarning().log("Parent command '" + parentName + "' not found! Registering children as root commands.");
                    for (AbstractCommand subCommand : entry.getValue()) {
                        rootCommands.add(subCommand);
                    }
                }
            }

            // Register all root commands with plugin command registry
            int rootCount = 0;
            for (AbstractCommand rootCommand : rootCommands) {
                try {
                    plugin.getCommandRegistry().registerCommand(rootCommand);
                    plugin.getLogger().atInfo().log("Successfully registered root command: " + rootCommand.getName());
                    rootCount++;
                } catch (Exception e) {
                    plugin.getLogger().atSevere().withCause(e).log("Failed to register root command: " + rootCommand.getName());
                }
            }

            plugin.getLogger().atInfo().log("Registered " + rootCount + " root commands and " + subCount + " subcommands automatically.");
        } catch (Exception e) {
            plugin.getLogger().atSevere().withCause(e).log("Failed to load command classes.");
        }
    }

    public static void registerEvents(AnglersAlmanac plugin) {
        final String PACKAGE_NAME = "dev.rm20.anglersalmanac.Events";
        try {
            ClassPath classPath = ClassPath.from(plugin.getClass().getClassLoader());
            int count = 0;

            for (ClassPath.ClassInfo classInfo : classPath.getTopLevelClassesRecursive(PACKAGE_NAME)) {
                Class<?> loadedClass = classInfo.load();

                if (loadedClass.isAnnotationPresent(EventInfo.class)) {
                    EventInfo info = loadedClass.getAnnotation(EventInfo.class);
                    Class eventType = info.value(); // Use raw Class here for easier interop

                    try {
                        // Use a raw cast to register the event without generic conflicts
                        plugin.getEventRegistry().registerGlobal(eventType, (event) -> {
                            try {
                                loadedClass.getMethod("handle", eventType).invoke(null, event);
                            } catch (Exception e) {
                                plugin.getLogger().atSevere()
                                        .withCause(e)
                                        .log("Failed to execute event handler in: " + loadedClass.getSimpleName());
                            }
                        });

                        plugin.getLogger().atInfo().log("Successfully registered event: " + loadedClass.getSimpleName());
                        count++;
                    } catch (Exception e) {
                        plugin.getLogger().atSevere().log("Could not find valid 'handle' method in: " + loadedClass.getSimpleName());
                        e.printStackTrace();
                    }
                }
            }
            plugin.getLogger().atInfo().log("Registered " + count + " events automatically.");
        } catch (Exception e) {
            plugin.getLogger().atSevere().log("Failed to load event classes.");
            e.printStackTrace();
        }
    }

}