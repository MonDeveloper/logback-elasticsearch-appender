package com.linagora.logback.elasticsearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.linagora.logback.elasticsearch.config.ElasticsearchProperties;
import com.linagora.logback.elasticsearch.config.HttpRequestHeaders;
import com.linagora.logback.elasticsearch.config.Property;
import com.linagora.logback.elasticsearch.config.Settings;
import com.linagora.logback.elasticsearch.util.AbstractPropertyAndEncoder;
import com.linagora.logback.elasticsearch.util.ClassicPropertyAndEncoder;
import com.linagora.logback.elasticsearch.util.ErrorReporter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Context;


public class ClassicElasticsearchPublisher extends AbstractElasticsearchPublisher<ILoggingEvent> {

    public ClassicElasticsearchPublisher(Context context, ErrorReporter errorReporter, Settings settings, ElasticsearchProperties properties, HttpRequestHeaders headers) throws IOException {
        super(context, errorReporter, settings, properties, headers);
    }

    @Override
    protected AbstractPropertyAndEncoder<ILoggingEvent> buildPropertyAndEncoder(Context context, Property property) {
        return new ClassicPropertyAndEncoder(property, context);
    }

    @Override
    protected void serializeCommonFields(JsonGenerator gen, ILoggingEvent event) throws IOException {
        gen.writeObjectField("@timestamp", getTimestamp(event.getTimeStamp()));

        if (settings.isRawJsonMessage()) {
            gen.writeFieldName("message");
            gen.writeRawValue(event.getFormattedMessage());
        } else {
            String formattedMessage = event.getFormattedMessage();
            if (settings.getMaxMessageSize() > 0 && formattedMessage.length() > settings.getMaxMessageSize()) {
                formattedMessage = formattedMessage.substring(0, settings.getMaxMessageSize()) + "..";
            }
            gen.writeObjectField("message", formattedMessage);
        }

        if (settings.isIncludeMdc()) {
            List<String> excludedKeys = getExcludedMdcKeys();
            for (Map.Entry<String, String> entry : event.getMDCPropertyMap().entrySet()) {
                if (!excludedKeys.contains(entry.getKey())) {
                    gen.writeObjectField(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    private List<String> getExcludedMdcKeys() {
        /*
         * using a List instead of a Map because the assumption is that
         * the number of excluded keys will be very small and not cause
         * a performance issue
         */
        List<String> result = new ArrayList<>();
        if (settings.getExcludedMdcKeys() != null) {
            String[] parts = settings.getExcludedMdcKeys().split(",");
            for (String part : parts) {
                result.add(part.trim());
            }
        }
        return result;
    }
}
