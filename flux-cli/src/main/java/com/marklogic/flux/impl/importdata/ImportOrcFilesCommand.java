/*
 * Copyright (c) 2024-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.flux.impl.importdata;

import com.marklogic.flux.api.OrcFilesImporter;
import com.marklogic.flux.api.ReadTabularFilesOptions;
import com.marklogic.flux.api.StructuredDataImporter;
import com.marklogic.flux.api.WriteStructuredDocumentsOptions;
import picocli.CommandLine;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@CommandLine.Command(
    name = "import-orc-files",
    description = "Read ORC files from supported file locations using Spark's support " +
        "defined at %nhttps://spark.apache.org/docs/latest/sql-data-sources-orc.html, and write JSON or " +
        "XML documents to MarkLogic."
)
public class ImportOrcFilesCommand extends AbstractImportTabularFilesCommand<OrcFilesImporter> implements OrcFilesImporter {

    @CommandLine.Mixin
    private ReadOrcFilesParams readParams = new ReadOrcFilesParams();

    @CommandLine.Mixin
    private WriteStructuredDocumentParams writeParams = new WriteStructuredDocumentParams();

    @Override
    protected String getReadFormat() {
        return "orc";
    }

    @Override
    protected ReadTabularFilesParams getTabularReadParams() {
        return readParams;
    }

    @Override
    protected WriteStructuredDocumentParams getTabularWriteParams() {
        return writeParams;
    }

    public static class ReadOrcFilesParams extends ReadTabularFilesParams {

        @CommandLine.Option(
            names = "--spark-prop",
            description = "Specify any Spark ORC data source option defined at " +
                "%nhttps://spark.apache.org/docs/latest/sql-data-sources-orc.html; e.g. --spark-prop mergeSchema=true. " +
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
    public OrcFilesImporter from(Consumer<ReadTabularFilesOptions> consumer) {
        return applyFrom(consumer);
    }

    @Override
    public OrcFilesImporter from(String... paths) {
        return applyFromPaths(paths);
    }

    @Override
    public OrcFilesImporter to(Consumer<WriteStructuredDocumentsOptions> consumer) {
        return applyTo(consumer);
    }

    @Override
    public OrcFilesImporter where(String expression) {
        return applyWhere(expression);
    }

    @Override
    public OrcFilesImporter groupBy(String columnName, Consumer<StructuredDataImporter.GroupByOptions<?>> consumer) {
        return applyGroupBy(columnName, consumer);
    }
}
