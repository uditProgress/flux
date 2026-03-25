/*
 * Copyright (c) 2024-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.flux.impl.importdata;

import com.marklogic.flux.api.ReadTabularFilesOptions;
import com.marklogic.flux.api.StructuredDataImporter;
import com.marklogic.flux.api.WriteStructuredDocumentsOptions;
import com.marklogic.flux.impl.SparkUtil;
import com.marklogic.flux.impl.TdeHelper;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.function.Consumer;

/**
 * Shared base for tabular file import commands (Parquet, Avro, ORC).
 * Consolidates the identical {@code afterDatasetLoaded}, {@code from}, {@code to},
 * {@code where}, and {@code groupBy} implementations.
 *
 * @param <T> the public API importer type (e.g. {@code ParquetFilesImporter})
 */
public abstract class AbstractImportTabularFilesCommand<T extends StructuredDataImporter<T>>
    extends AbstractImportFilesCommand<T> {

    protected abstract ReadTabularFilesParams getTabularReadParams();

    protected abstract WriteStructuredDocumentParams getTabularWriteParams();

    @Override
    protected IReadFilesParams getReadParams() {
        return getTabularReadParams();
    }

    @Override
    protected WriteStructuredDocumentParams getWriteParams() {
        return getTabularWriteParams();
    }

    @Override
    protected Dataset<Row> afterDatasetLoaded(Dataset<Row> dataset) {
        ReadTabularFilesParams readParams = getTabularReadParams();
        if (readParams.isUriIncludeFilePath()) {
            dataset = SparkUtil.addFilePathColumn(dataset);
        }

        dataset = readParams.getStructuredDataParams().applyTransformations(dataset);

        TdeHelper.Result result = getTabularWriteParams().newTdeHelper()
            .logOrLoadTemplate(dataset.schema(), getConnectionParams());
        if (TdeHelper.Result.TEMPLATE_LOGGED.equals(result)) {
            return null;
        }

        return dataset;
    }

    @SuppressWarnings("unchecked")
    protected T applyFrom(Consumer<ReadTabularFilesOptions> consumer) {
        consumer.accept(getTabularReadParams());
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    protected T applyFromPaths(String... paths) {
        getTabularReadParams().paths(paths);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    protected T applyTo(Consumer<WriteStructuredDocumentsOptions> consumer) {
        consumer.accept(getTabularWriteParams());
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    protected T applyWhere(String expression) {
        getTabularReadParams().getStructuredDataParams().where(expression);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    protected T applyGroupBy(String columnName, Consumer<StructuredDataImporter.GroupByOptions<?>> consumer) {
        getTabularReadParams().getStructuredDataParams().setGroupBy(columnName);
        consumer.accept(getTabularReadParams().getStructuredDataParams());
        return (T) this;
    }
}
