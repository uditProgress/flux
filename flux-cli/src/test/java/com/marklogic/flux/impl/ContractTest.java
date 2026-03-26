/*
 * Copyright (c) 2024-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.flux.impl;

import com.marklogic.flux.api.Flux;
import com.marklogic.flux.cli.Main;
import picocli.CommandLine;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Contract tests that validate the CLI command registry and the public API surface
 * against golden files. These tests do not need MarkLogic, Spark, or any external
 * infrastructure — they only use reflection and PicocLI metadata.
 *
 * <h3>Updating the contract</h3>
 * <p>If you intentionally add or remove a CLI command or API method:</p>
 * <ol>
 *   <li>Run this test — it will fail and print the expected vs. actual list.</li>
 *   <li>Update the golden file under {@code src/test/resources/contract/}.</li>
 *   <li>Re-run and confirm green.</li>
 * </ol>
 */
class ContractTest {

    /**
     * Validates that the set of CLI subcommand names registered in {@link Main} matches
     * the golden file {@code contract/cli-commands.txt}.
     */
    @Test
    void cliCommandRegistryMatchesContract() throws IOException {
        List<String> expectedCommands = readGoldenLines("contract/cli-commands.txt");

        CommandLine commandLine = new CommandLine(new Main());
        List<String> actualCommands = commandLine.getSubcommands().keySet().stream()
            .filter(name -> !name.equals("help")) // built-in PicocLI command, not part of our contract
            .sorted()
            .collect(Collectors.toList());

        assertEquals(expectedCommands, actualCommands,
            "CLI command registry does not match contract/cli-commands.txt. "
                + "If you added or removed a command, update the golden file.");
    }

    /**
     * Validates that the public static factory methods on the {@link Flux} interface match
     * the golden file {@code contract/api-methods.txt}.
     */
    @Test
    void fluxApiSurfaceMatchesContract() throws IOException {
        Map<String, String> expectedMethods = readGoldenMap("contract/api-methods.txt");

        Map<String, String> actualMethods = new TreeMap<>();
        for (Method method : Flux.class.getDeclaredMethods()) {
            if (Modifier.isStatic(method.getModifiers()) && Modifier.isPublic(method.getModifiers())) {
                actualMethods.put(method.getName(), method.getReturnType().getSimpleName());
            }
        }

        assertEquals(expectedMethods, actualMethods,
            "Flux API surface does not match contract/api-methods.txt. "
                + "If you added or removed an API method, update the golden file.");
    }

    /**
     * Validates that every CLI command responds to {@code help <command>} without error.
     * This is a smoke test ensuring no command has a broken PicocLI definition.
     */
    @Test
    void everyCommandHasValidHelp() throws IOException {
        List<String> commands = readGoldenLines("contract/cli-commands.txt");

        for (String command : commands) {
            int exitCode = Main.run(null, null, "help", command);
            assertEquals(CommandLine.ExitCode.USAGE, exitCode,
                "help " + command + " should return USAGE exit code");
        }
    }

    // --- helpers ---

    private List<String> readGoldenLines(String resourcePath) throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            assertNotNull(is, "Golden file not found: " + resourcePath);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                return reader.lines()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                    .sorted()
                    .collect(Collectors.toList());
            }
        }
    }

    private Map<String, String> readGoldenMap(String resourcePath) throws IOException {
        Map<String, String> map = new TreeMap<>();
        for (String line : readGoldenLines(resourcePath)) {
            String[] parts = line.split("=", 2);
            assertEquals(2, parts.length, "Invalid golden file line: " + line);
            map.put(parts[0].trim(), parts[1].trim());
        }
        return map;
    }
}
