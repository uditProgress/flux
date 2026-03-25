/*
 * Copyright (c) 2024-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.flux.impl.importdata;

import com.marklogic.flux.api.ParquetFilesImporter;
import com.marklogic.flux.api.ReadTabularFilesOptions;
import com.marklogic.flux.api.StructuredDataImporter;
import com.marklogic.flux.api.WriteStructuredDocumentsOptions;
import picocli.CommandLine;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@CommandLine.Command(
    name = "import-parquet-files",
    description = "Read Parquet files from supported file locations using Spark's support " +
        "defined at %nhttps://spark.apache.org/docs/latest/sql-data-sources-parquet.html, and write JSON or XML " +
        "documents to MarkLogic."
)
public class ImportParquetFilesCommand extends AbstractImportTabularFilesCommand<ParquetFilesImporter> implements ParquetFilesImporter {

    @CommandLine.Mixin
    private ReadParquetFilesParams readParams = new ReadParquetFilesParams();

    @CommandLine.Mixin
    private WriteStructuredDocumentParams writeParams = new WriteStructuredDocumentParams();

    @Override
    protected String getReadFormat() {
        return "parquet";
    }

    @Override
    protected ReadTabularFilesParams getTabularReadParams() {
        return readParams;
    }

    @Override
    protected WriteStructuredDocumentParams getTabularWriteParams() {
        return writeParams;
    }

    public static class ReadParquetFilesParams extends ReadTabularFilesParams {

        @CommandLine.Option(
            names = "--spark-prop",
            description = "Specify any Spark Parquet data source option defined at " +
                "%nhttps://spark.apache.org/docs/latest/sql-data-sources-parquet.html; e.g. --spark-prop mergeSchema=true. " +
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
    public ParquetFilesImporter from(Consumer<ReadTabularFilesOptions> consumer) {
        return applyFrom(consumer);
    }

    @Override
    public ParquetFilesImporter from(String... paths) {
        return applyFromPaths(paths);
    }

    @Override
    public ParquetFilesImporter to(Consumer<WriteStructuredDocumentsOptions> consumer) {
        return applyTo(consumer);
    }

    @Override
    public ParquetFilesImporter where(String expression) {
        return applyWhere(expression);
    }

    @Override
    public ParquetFilesImporter groupBy(String columnName, Consumer<StructuredDataImporter.GroupByOptions<?>> consumer) {
        return applyGroupBy(columnName, consumer);
    }
}
