/*
 * Copyright (c) 2024-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.flux.impl.importdata;

import com.marklogic.flux.api.AvroFilesImporter;
import com.marklogic.flux.api.ReadTabularFilesOptions;
import com.marklogic.flux.api.StructuredDataImporter;
import com.marklogic.flux.api.WriteStructuredDocumentsOptions;
import picocli.CommandLine;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@CommandLine.Command(
    name = "import-avro-files",
    description = "Read Avro files from supported file locations using Spark's support defined at" +
        "%nhttps://spark.apache.org/docs/latest/sql-data-sources-avro.html, and write JSON or XML documents " +
        "to MarkLogic."
)
public class ImportAvroFilesCommand extends AbstractImportTabularFilesCommand<AvroFilesImporter> implements AvroFilesImporter {

    @CommandLine.Mixin
    private ReadAvroFilesParams readParams = new ReadAvroFilesParams();

    @CommandLine.Mixin
    private WriteStructuredDocumentParams writeParams = new WriteStructuredDocumentParams();

    @Override
    protected String getReadFormat() {
        return "avro";
    }

    @Override
    protected ReadTabularFilesParams getTabularReadParams() {
        return readParams;
    }

    @Override
    protected WriteStructuredDocumentParams getTabularWriteParams() {
        return writeParams;
    }

    public static class ReadAvroFilesParams extends ReadTabularFilesParams {

        @CommandLine.Option(
            names = "--spark-prop",
            description = "Specify any Spark Avro data source option defined at " +
                "%nhttps://spark.apache.org/docs/latest/sql-data-sources-avro.html; e.g. --spark-prop ignoreExtension=true. " +
                "Spark configuration options must be defined via '--spark-conf'."
        )
        private Map<String, String> additionalOptions = new HashMap<>();

        @Override
        protected Map<String, String> getAdditionalOptions() {
            return additionalOptions;
        }

        @Override
        protected void setAdditionalOptions(Map<String, String> options) {
            this.additionalOptions = options;
        }
    }

    @Override
    public AvroFilesImporter from(Consumer<ReadTabularFilesOptions> consumer) {
        return applyFrom(consumer);
    }

    @Override
    public AvroFilesImporter from(String... paths) {
        return applyFromPaths(paths);
    }

    @Override
    public AvroFilesImporter to(Consumer<WriteStructuredDocumentsOptions> consumer) {
        return applyTo(consumer);
    }

    @Override
    public AvroFilesImporter where(String expression) {
        return applyWhere(expression);
    }

    @Override
    public AvroFilesImporter groupBy(String columnName, Consumer<StructuredDataImporter.GroupByOptions<?>> consumer) {
        return applyGroupBy(columnName, consumer);
    }
}
