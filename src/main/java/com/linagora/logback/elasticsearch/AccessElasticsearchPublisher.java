package com.linagora.logback.elasticsearch;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.linagora.logback.elasticsearch.config.ElasticsearchProperties;
import com.linagora.logback.elasticsearch.config.HttpRequestHeaders;
import com.linagora.logback.elasticsearch.config.Property;
import com.linagora.logback.elasticsearch.config.Settings;
import com.linagora.logback.elasticsearch.util.AbstractPropertyAndEncoder;
import com.linagora.logback.elasticsearch.util.AccessPropertyAndEncoder;
import com.linagora.logback.elasticsearch.util.ErrorReporter;
import ch.qos.logback.access.spi.IAccessEvent;
import ch.qos.logback.core.Context;

public class AccessElasticsearchPublisher extends AbstractElasticsearchPublisher<IAccessEvent> {

	public AccessElasticsearchPublisher(Context context, ErrorReporter errorReporter, Settings settings, ElasticsearchProperties properties, HttpRequestHeaders httpRequestHeaders) throws IOException {
		super(context, errorReporter, settings, properties, httpRequestHeaders);
	}

	@Override
	protected AbstractPropertyAndEncoder<IAccessEvent> buildPropertyAndEncoder(Context context, Property property) {
		return new AccessPropertyAndEncoder(property, context);
	}

	@Override
	protected void serializeCommonFields(JsonGenerator gen, IAccessEvent event) throws IOException {
		gen.writeObjectField("@timestamp", getTimestamp(event.getTimeStamp()));
	}
}
