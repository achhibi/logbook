package org.zalando.logbook.core.attributes;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;
import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.HttpResponse;
import org.zalando.logbook.attributes.AttributeExtractor;
import org.zalando.logbook.attributes.HttpAttributes;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
@AllArgsConstructor
@EqualsAndHashCode
@Slf4j
public final class CompositeAttributeExtractor implements AttributeExtractor {

    private final List<AttributeExtractor> attributeExtractors;

    @Nonnull
    @Override
    public HttpAttributes extract(final HttpRequest request) {
        final Map<String, String> map = new HashMap<>();
        for (final AttributeExtractor attributeExtractor : attributeExtractors) {
            map.putAll(safeRequestExtractor(attributeExtractor, request));
        }
        return new HttpAttributes(map);
    }

    @Nonnull
    @Override
    public HttpAttributes extract(final HttpRequest request, HttpResponse response) {
        final Map<String, String> map = new HashMap<>();
        for (final AttributeExtractor attributeExtractor : attributeExtractors) {
            map.putAll(safeRequestExtractor(attributeExtractor, request, response));
        }
        return new HttpAttributes(map);
    }

    @Nonnull
    private HttpAttributes safeRequestExtractor(final AttributeExtractor attributeExtractor,
                                                final HttpRequest request) {
        try {
            return attributeExtractor.extract(request);
        } catch (Exception e) {
            log.trace(
                    "{} encountered error while extracting attributes: `{}`",
                    attributeExtractor.getClass(),
                    (Optional.ofNullable(e.getCause()).orElse(e)).getMessage()
            );
            return HttpAttributes.EMPTY;
        }
    }

    @Nonnull
    private HttpAttributes safeRequestExtractor(final AttributeExtractor attributeExtractor,
                                                final HttpRequest request,
                                                final HttpResponse response) {
        try {
            return attributeExtractor.extract(request, response);
        } catch (Exception e) {
            log.trace(
                    "{} encountered error while extracting attributes: `{}`",
                    attributeExtractor.getClass(),
                    (Optional.ofNullable(e.getCause()).orElse(e)).getMessage()
            );
            return HttpAttributes.EMPTY;
        }
    }
}
