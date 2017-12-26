package org.thermocutie.thermostat.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.metamx.common.logger.Logger;
import com.metamx.emitter.EmittingLogger;
import com.metamx.emitter.core.LoggingEmitter;
import com.metamx.emitter.core.NoopEmitter;
import com.metamx.emitter.service.ServiceEmitter;
import io.druid.client.cache.CacheConfig;
import io.druid.client.cache.LocalCacheProvider;
import io.druid.collections.DefaultBlockingPool;
import io.druid.collections.StupidPool;
import io.druid.data.input.InputRow;
import io.druid.data.input.Row;
import io.druid.data.input.impl.*;
import io.druid.jackson.DefaultObjectMapper;
import io.druid.java.util.common.granularity.Granularities;
import io.druid.java.util.common.guava.Sequence;
import io.druid.java.util.common.guava.Sequences;
import io.druid.query.*;
import io.druid.query.aggregation.AggregatorFactory;
import io.druid.query.aggregation.CountAggregatorFactory;
import io.druid.query.aggregation.DoubleMaxAggregatorFactory;
import io.druid.query.groupby.*;
import io.druid.query.groupby.strategy.GroupByStrategySelector;
import io.druid.query.groupby.strategy.GroupByStrategyV1;
import io.druid.query.groupby.strategy.GroupByStrategyV2;
import io.druid.query.metadata.SegmentMetadataQueryConfig;
import io.druid.query.metadata.SegmentMetadataQueryQueryToolChest;
import io.druid.query.metadata.SegmentMetadataQueryRunnerFactory;
import io.druid.query.metadata.metadata.SegmentMetadataQuery;
import io.druid.query.search.SearchQueryQueryToolChest;
import io.druid.query.search.SearchQueryRunnerFactory;
import io.druid.query.search.search.SearchQuery;
import io.druid.query.search.search.SearchQueryConfig;
import io.druid.query.select.*;
import io.druid.query.spec.QuerySegmentSpecs;
import io.druid.query.timeboundary.TimeBoundaryQuery;
import io.druid.query.timeboundary.TimeBoundaryQueryRunnerFactory;
import io.druid.query.timeseries.TimeseriesQuery;
import io.druid.query.timeseries.TimeseriesQueryEngine;
import io.druid.query.timeseries.TimeseriesQueryQueryToolChest;
import io.druid.query.timeseries.TimeseriesQueryRunnerFactory;
import io.druid.query.topn.TopNQuery;
import io.druid.query.topn.TopNQueryConfig;
import io.druid.query.topn.TopNQueryQueryToolChest;
import io.druid.query.topn.TopNQueryRunnerFactory;
import io.druid.segment.IndexIO;
import io.druid.segment.IndexMerger;
import io.druid.segment.column.ColumnConfig;
import io.druid.segment.indexing.DataSchema;
import io.druid.segment.indexing.RealtimeTuningConfig;
import io.druid.segment.indexing.granularity.UniformGranularitySpec;
import io.druid.segment.loading.DataSegmentPusher;
import io.druid.segment.realtime.FireDepartmentMetrics;
import io.druid.segment.realtime.NoopSegmentPublisher;
import io.druid.segment.realtime.plumber.Committers;
import io.druid.segment.realtime.plumber.NoopSegmentHandoffNotifierFactory;
import io.druid.segment.realtime.plumber.RealtimePlumber;
import io.druid.server.coordination.DataSegmentAnnouncer;
import io.druid.timeline.DataSegment;
import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.ReadWritableInterval;
import org.joda.time.convert.ConverterManager;
import org.joda.time.convert.IntervalConverter;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * TODO: add description
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
public class Test {
    public static void main(String[] args) throws IOException, InterruptedException {

        ObjectMapper objectMapper = new DefaultObjectMapper();
        LoggingEmitter loggingEmitter = new LoggingEmitter(new Logger("emitterLogger"), LoggingEmitter.Level.DEBUG, objectMapper);
        loggingEmitter.start();
        EmittingLogger.registerEmitter(new ServiceEmitter("emitter", "localhost", loggingEmitter));
        ColumnConfig columnConfig = () -> 0;
        IndexIO indexIO = new IndexIO(objectMapper, columnConfig );
        IndexMerger merger = new IndexMerger(objectMapper, indexIO);

//        List<String> columns = Arrays.asList("Timestamp", "temperature");
//        List<String> metrics = Arrays.asList("temperature");
//        List<String> dimensions = new ArrayList<>(columns);
//        dimensions.removeAll(metrics);

        List<DimensionSchema> dimensionSchemas = new ArrayList<>();
//        for (String dimension : dimensions)
        dimensionSchemas.add(new LongDimensionSchema("Timestamp"));
        dimensionSchemas.add(new FloatDimensionSchema("Temperature"));
//        DimensionsSpec dimensionsSpec = new DimensionsSpec(dimensionSchemas, null, null);
        AggregatorFactory[] metricsAgg = new AggregatorFactory[] {
//            new DoubleMaxAggregatorFactory("agg_max", null, "Temperature", null),
//                new CountAggregatorFactory("cnt")
        };

        MapInputRowParser inputRowParser = new MapInputRowParser(new TimeAndDimsParseSpec(
                new TimestampSpec("Timestamp", null, null),
                new DimensionsSpec(dimensionSchemas, null, null)
        ));

//        new IncrementalIndexSchema(0, null, Granularities.ALL, null, dimensionsSpec, metricsAgg, false);
//        IncrementalIndexSchema indexSchema = new IncrementalIndexSchema.Builder()
//                .withDimensionsSpec(inputRowParser)
//                .withTimestampSpec(inputRowParser)
//                .withMetrics(metricsAgg)
//                .build();
//
//        IncrementalIndex<?> incIndex = new IncrementalIndex.Builder()
//                .setIndexSchema(indexSchema)
//                .setMaxRowCount(Integer.MAX_VALUE)
////                .setSimpleTestingIndexSchema(metricsAgg)
//                .buildOnheap();

//       	    for (InputRow row : loader) {
//       	      incIndex.add(row);
//       	    }



        HashMap<Class<? extends Query>, QueryRunnerFactory> map =
      	        Maps.newHashMap();

      	    // Register all query runner factories.
      	    map.put(GroupByQuery.class, getGroupByQueryRunnerFactory());
      	    map.put(TopNQuery.class, getTopNQueryRunnerFactory());
      	    map.put(SearchQuery.class, getSearchQueryRunnerFactory());
      	    map.put(SelectQuery.class, getSelectQueryRunnerFactory());
      	    map.put(SegmentMetadataQuery.class, getSegmentMetadataQueryRunnerFactory());
      	    map.put(TimeseriesQuery.class, getTimeseriesQueryRunnerFactory());
      	    map.put(TimeBoundaryQuery.class, getTimeBoundaryQueryRunnerFactory());


      	    DefaultQueryRunnerFactoryConglomerate conglomerate =
      	        new DefaultQueryRunnerFactoryConglomerate(map);

        RealtimePlumber plumber = new RealtimePlumber(
                new DataSchema("source", null, metricsAgg, new UniformGranularitySpec(null, Granularities.ALL, null), objectMapper),
                RealtimeTuningConfig.makeDefaultTuningConfig(new File("./druid/")),
                new FireDepartmentMetrics(),
                new ServiceEmitter("service", "localhost", new NoopEmitter()),
                conglomerate,
                new NoopDataSegmentAnnouncer(),
                Executors.newSingleThreadExecutor(),
//                new LocalDataSegmentPusher(new LocalDataSegmentPusherConfig(), objectMapper),
                new NoopDataSegmentPusher(),
                new NoopSegmentPublisher(),
//                new CoordinatorBasedSegmentHandoffNotifier("source", null, new CoordinatorBasedSegmentHandoffNotifierConfig()),
                new NoopSegmentHandoffNotifierFactory().createSegmentHandoffNotifier(null),
                merger,
                indexIO,
                new LocalCacheProvider().get(),
                new CacheConfig(),
                objectMapper
        )
        {
            @Override
            protected File computePersistDir(DataSchema schema, Interval interval) {
                String fileName = interval.toString();
                fileName = fileName.replace("/", "_");
                fileName = fileName.replace("+", "_p_");
                fileName = fileName.replace(":", "_s_");
                return new File(computeBaseDir(schema), fileName);
            }
        };

        IntervalConverter stringConverter = ConverterManager.getInstance().getIntervalConverter("");

        ConverterManager.getInstance().addIntervalConverter(new IntervalConverter() {
            @Override
            public boolean isReadableInterval(Object object, Chronology chrono) {
                return stringConverter.isReadableInterval(object, chrono);
            }

            @Override
            public void setInto(ReadWritableInterval writableInterval, Object object, Chronology chrono) {
                object = ((String)object).replace("/p/", "+");
                object = ((String)object).replace("/s/", ":");
                stringConverter.setInto(writableInterval, object, chrono);
            }

            @Override
            public Class<?> getSupportedType() {
                return String.class;
            }
        });

        plumber.startJob();

        long l = System.currentTimeMillis();

        for (int i = 0; i < 1000; i++) {
            Map<String, Object> event = new HashMap<>();
            event.put("Timestamp", l + i);
            event.put("Temperature", i*2);

            InputRow row = inputRowParser.parse(event);
            plumber.add(row, Committers::nil);
        }

//        for (Sink sink  : plumber.getSinks().values())
//        {
//            sink.getInterval().

//        }



//        File tmpIndexDir = new File("c:/tmp/druid" + System.currentTimeMillis());
//
//        merger.persist(incIndex, tmpIndexDir, new IndexSpec());
//
//        QueryableIndex index = indexIO.loadIndex(tmpIndexDir);


//        List<DimFilter> filters = new ArrayList<>();
//        filters.add(DimFilters.dimEquals("Page", "JB"));
//        filters.add(DimFilters.dimEquals("Gender", "Male"));
//        filters.add(DimFilters.dimEquals("metric", "CharsAdded"));
        GroupByQuery query = GroupByQuery.builder()
                .setDataSource("source")
                .setQuerySegmentSpec(QuerySegmentSpecs.create(new Interval(0, new DateTime().getMillis())))
//                .setGranularity(Granularities.ALL)
                .setGranularity(Granularities.SECOND)
//                .addDimension("Temperature")
//                .addAggregator(new LongSumAggregatorFactory("agg_count", "Temperature"))
                .addAggregator(new DoubleMaxAggregatorFactory("tmp", "Temperature" ))
                .addAggregator(new CountAggregatorFactory("cnt"))
                .build();
        Sequence<Row> sequence =
                plumber.getQueryRunner(query)
//                conglomerate.findFactory(query)
//                .createRunner(new QueryableIndexSegment("", index))
                .run(QueryPlus.wrap(query), null);
        ArrayList<Row> results = Sequences.toList(sequence, Lists.newArrayList());

        System.out.println(results);


//        l = System.currentTimeMillis();
//
//        for (int i = 0; i < 1000; i++) {
//            Map<String, Object> event = new HashMap<>();
//            event.put("Timestamp", l + i);
//            event.put("Temperature", i*2);
//
//            InputRow row = inputRowParser.parse(event);
//            incIndex.add(row);
//        }
//
//        sequence = conglomerate.findFactory(query)
//                .createRunner(new QueryableIndexSegment("", index))
//                .run(QueryPlus.wrap(query), null);
//        results = Sequences.toList(sequence, Lists.newArrayList());
//
//        System.out.println(results);

        Thread.sleep(1000);
        plumber.finishJob();
        loggingEmitter.close();
    }

    private static TimeseriesQueryRunnerFactory getTimeseriesQueryRunnerFactory() {
   		TimeseriesQueryQueryToolChest toolChest =
   	        new TimeseriesQueryQueryToolChest(NoopIntervalChunkingQueryRunnerDecorator());
   	    TimeseriesQueryEngine engine = new TimeseriesQueryEngine();
        return new TimeseriesQueryRunnerFactory(toolChest, engine, NOOP_QUERYWATCHER);
   	}

   	private static TimeBoundaryQueryRunnerFactory getTimeBoundaryQueryRunnerFactory() {
        return new TimeBoundaryQueryRunnerFactory(NOOP_QUERYWATCHER);
   	}

   	private static SegmentMetadataQueryRunnerFactory getSegmentMetadataQueryRunnerFactory() {
   		SegmentMetadataQueryConfig smqc = new SegmentMetadataQueryConfig();
   	    SegmentMetadataQueryQueryToolChest toolChest = new SegmentMetadataQueryQueryToolChest(smqc);
        return new SegmentMetadataQueryRunnerFactory(toolChest, NOOP_QUERYWATCHER);
   	}

   	private static SelectQueryRunnerFactory getSelectQueryRunnerFactory() {
        //noinspection Guava
        Supplier<SelectQueryConfig> configSupplier = () -> new SelectQueryConfig(null);
        SelectQueryQueryToolChest toolChest =
   	        new SelectQueryQueryToolChest(new ObjectMapper(),
   	            NoopIntervalChunkingQueryRunnerDecorator(), configSupplier);
   	    SelectQueryEngine engine = new SelectQueryEngine(configSupplier);
        return new SelectQueryRunnerFactory(toolChest, engine, NOOP_QUERYWATCHER);
   	}

   	private static SearchQueryRunnerFactory getSearchQueryRunnerFactory() {
   		SearchQueryQueryToolChest toolChest =
   	        new SearchQueryQueryToolChest(new SearchQueryConfig(),
   	            NoopIntervalChunkingQueryRunnerDecorator());
        return new SearchQueryRunnerFactory(null, toolChest, NOOP_QUERYWATCHER);
   	}

   	private static TopNQueryRunnerFactory getTopNQueryRunnerFactory() {
   	    TopNQueryQueryToolChest toolchest =
   	        new TopNQueryQueryToolChest(new TopNQueryConfig(),
   	            NoopIntervalChunkingQueryRunnerDecorator());
        return new TopNQueryRunnerFactory(getBufferPool(), toolchest, NOOP_QUERYWATCHER);
   	}

   	private static GroupByQueryRunnerFactory getGroupByQueryRunnerFactory() {
   		ObjectMapper mapper = new DefaultObjectMapper();
   		GroupByQueryConfig config = new GroupByQueryConfig();
   		config.setMaxIntermediateRows(10000);

        //noinspection Guava
   		Supplier<GroupByQueryConfig> configSupplier = Suppliers.ofInstance(config);
   		GroupByQueryEngine engine = new GroupByQueryEngine(configSupplier, getBufferPool());

        DefaultBlockingPool<ByteBuffer> mergeBufferPool = new DefaultBlockingPool<>(new ByteBufferSupplier(MAX_TOTAL_BUFFER_SIZE / 2), 1);
        DruidProcessingConfig processingConfig = new DruidProcessingConfig() {
            @Override
            public String getFormatString() {
                return "format";
            }
        };
        GroupByStrategyV1 strategyV1 = new GroupByStrategyV1(configSupplier, engine, NOOP_QUERYWATCHER, getBufferPool());
        GroupByStrategyV2 strategyV2 = new GroupByStrategyV2(processingConfig, configSupplier, getBufferPool(), mergeBufferPool, mapper, NOOP_QUERYWATCHER);
        GroupByStrategySelector strategySelector = new GroupByStrategySelector(configSupplier, strategyV1, strategyV2);
        return new GroupByQueryRunnerFactory(strategySelector,
                new GroupByQueryQueryToolChest(strategySelector, NoopIntervalChunkingQueryRunnerDecorator())
);
   	}
   	
    private static final int MAX_TOTAL_BUFFER_SIZE = 1024*1024*1024;
   	
   	private static class ByteBufferSupplier implements Supplier<ByteBuffer> {
   		int capacity;
   
   		public ByteBufferSupplier(int capacity) {
   			this.capacity = capacity;
   		}
   
   		public ByteBuffer get() {
   			return ByteBuffer.allocate(capacity);
   		}
   	}
   	
//   	public static ServiceEmitter NOOP_SERVICE_EMITTER = new ServiceEmitter("service", null, null) {
//   		@Override
//   	    public void emit(Event event) {}
//   	};
   
   	private static final QueryWatcher NOOP_QUERYWATCHER = (query, future) -> {};
   	
   	private static StupidPool<ByteBuffer> getBufferPool() {
   	    return new StupidPool<>("stupid", new ByteBufferSupplier(MAX_TOTAL_BUFFER_SIZE / 2));
   	}
   
   	private static IntervalChunkingQueryRunnerDecorator NoopIntervalChunkingQueryRunnerDecorator() {
   		return new IntervalChunkingQueryRunnerDecorator(null, null, null) {
   			@Override
   			public <T> QueryRunner<T> decorate(final QueryRunner<T> delegate,
   					QueryToolChest<T, ? extends Query<T>> toolChest) {
   			    return delegate;
//   				return new QueryRunner<T>() {
//   		          public Sequence<T> run(Query<T> query, Map<String, Object> responseContext) {
//   		            return delegate.run(query, responseContext);
//   		          }
//   				};
   			}
   		};
   	}


    private static class NoopDataSegmentPusher implements DataSegmentPusher
    {

      @Override
      public String getPathForHadoop()
      {
        return "noop";
      }

      @Deprecated
      @Override
      public String getPathForHadoop(String dataSource)
      {
        return getPathForHadoop();
      }

      @Override
      public DataSegment push(File file, DataSegment segment) {
        return segment;
      }

      @Override
      public Map<String, Object> makeLoadSpec(URI uri)
      {
        return ImmutableMap.of();
      }
    }

    private static class NoopDataSegmentAnnouncer implements DataSegmentAnnouncer
    {
      @Override
      public void announceSegment(DataSegment segment) {
        // do nothing
      }

      @Override
      public void unannounceSegment(DataSegment segment) {
        // do nothing
      }

      @Override
      public void announceSegments(Iterable<DataSegment> segments) {
        // do nothing
      }

      @Override
      public void unannounceSegments(Iterable<DataSegment> segments) {
        // do nothing
      }
    }
}
