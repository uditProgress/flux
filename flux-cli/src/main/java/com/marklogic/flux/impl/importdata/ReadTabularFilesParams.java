/*
 * Copyright (c) 2024-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.flux.impl.importdata;

import com.marklogic.flux.api.ReadTabularFilesOptions;
import picocli.CommandLine;

import java.util.HashMap;
import java.util.Map;

/**
 * Shared base for the inner Read*FilesParams classes in the tabular import commands
 * (Parquet, Avro, ORC). Each subclass only needs to declare its own {@code --spark-prop}
 * option with a format-specific description string.
 */
public abstract class ReadTabularFilesParams extends ReadFilesParams<ReadTabularFilesOptions> implements ReadTabularFilesOptions {

    @CommandLine.Option(
        names = "--uri-include-file-path",
        description = "If true, each document URI will include the path of the originating file."
    )
    private boolean uriIncludeFilePath;

    @CommandLine.Mixin
    private StructuredDataParams structuredDataParams = new StructuredDataParams();

    boolean isUriIncludeFilePath() {
        return uriIncludeFilePath;
    }

    StructuredDataParams getStructuredDataParams() {
        return structuredDataParams;
    }

    /**
     * Subclasses must provide the format-specific additional options map
     * (populated by their own {@code --spark-prop} CLI option).
     */
    protected abstract Map<String, String> getAdditionalOptions();

    protected abstract void setAdditionalOptions(Map<String, String> options);

    @Override
    public Map<String, String> makeOptions() {
        Map<String, String> options = super.makeOptions();
        options.putAll(getAdditionalOptions());
        return options;
    }

    @Override
    public ReadTabularFilesOptions additionalOptions(Map<String, String> options) {
        setAdditionalOptions(options);
        return this;
    }

    @Override
    @Deprecated
    public ReadTabularFilesOptions groupBy(String columnName) {
        structuredDataParams.setGroupBy(columnName);
        return this;
    }

    @Override
    @Deprecated
    public ReadTabularFilesOptions aggregateColumns(String aggregationName, String... columns) {
        structuredDataParams.aggregateColumns(aggregationName, columns);
        return this;
    }

    @Override
    @Deprecated
    public ReadTabularFilesOptions orderAggregation(String aggregationName, String columnName, boolean ascending) {
        structuredDataParams.orderAggregation(aggregationName, columnName, ascending);
        return this;
    }

    @Override
    public ReadTabularFilesOptions uriIncludeFilePath(boolean value) {
        this.uriIncludeFilePath = value;
        return this;
    }

    @Override
    public ReadTabularFilesOptions drop(String... columns) {
        structuredDataParams.drop(columns);
        return this;
    }
}
