package com._5icodes.starter.feign.encoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.lang.reflect.ParameterizedType;

import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.lang3.ArrayUtils;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.encoding.HttpEncoding;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.cloud.openfeign.support.FeignEncoderProperties;
import org.springframework.util.MultiValueMap;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.http.MediaType.MULTIPART_MIXED;
import static org.springframework.http.MediaType.MULTIPART_RELATED;

/**
 * 主要解决了SpringEncoder可以解析Map<String, Object>参数类型的form表单提交
 *
 * @see org.springframework.cloud.openfeign.support.SpringEncoder
 */
@SuppressWarnings("rawtypes")
public class CustomSpringEncoder implements Encoder {

	private static final Log log = LogFactory.getLog(CustomSpringEncoder.class);

	private final SpringFormEncoder springFormEncoder;

	private final ObjectFactory<HttpMessageConverters> messageConverters;

	private final FeignEncoderProperties encoderProperties;

	public CustomSpringEncoder(ObjectFactory<HttpMessageConverters> messageConverters) {
		this(new SpringFormEncoder(), messageConverters);
	}

	public CustomSpringEncoder(SpringFormEncoder springFormEncoder,
			ObjectFactory<HttpMessageConverters> messageConverters) {
		this(springFormEncoder, messageConverters, new FeignEncoderProperties());
	}

	public CustomSpringEncoder(SpringFormEncoder springFormEncoder,
			ObjectFactory<HttpMessageConverters> messageConverters,
			FeignEncoderProperties encoderProperties) {
		this.springFormEncoder = springFormEncoder;
		this.messageConverters = messageConverters;
		this.encoderProperties = encoderProperties;
	}

	@Override
	public void encode(Object requestBody, Type bodyType, RequestTemplate request)
			throws EncodeException {
		// template.body(conversionService.convert(object, String.class));
		if (requestBody != null) {
			Collection<String> contentTypes = request.headers()
					.get(HttpEncoding.CONTENT_TYPE);

			MediaType requestContentType = null;
			if (contentTypes != null && !contentTypes.isEmpty()) {
				String type = contentTypes.iterator().next();
				requestContentType = MediaType.valueOf(type);
			}

			if (isFormRelatedContentType(requestContentType)) {
				springFormEncoder.encode(requestBody, bodyType, request);
				return;
			}
			else {
				if (bodyType == MultipartFile.class) {
					log.warn(
							"For MultipartFile to be handled correctly, the 'consumes' parameter of @RequestMapping "
									+ "should be specified as MediaType.MULTIPART_FORM_DATA_VALUE");
				} else {
					Type springFormEncoderType = getSpringFormEncoderType(requestContentType, bodyType, requestBody.getClass());
					if (null != springFormEncoderType) {
						springFormEncoder.encode(requestBody, springFormEncoderType, request);
						return;
					}
				}
			}
			encodeWithMessageConverter(requestBody, bodyType, request,
					requestContentType);
		}
	}

	private void encodeWithMessageConverter(Object requestBody, Type bodyType,
			RequestTemplate request, MediaType requestContentType) {
		for (HttpMessageConverter messageConverter : messageConverters.getObject()
				.getConverters()) {
			FeignOutputMessage outputMessage;
			try {
				if (messageConverter instanceof GenericHttpMessageConverter) {
					outputMessage = checkAndWrite(requestBody, bodyType,
							requestContentType,
							(GenericHttpMessageConverter) messageConverter, request);
				}
				else {
					outputMessage = checkAndWrite(requestBody, requestContentType,
							messageConverter, request);
				}
			}
			catch (IOException | HttpMessageConversionException ex) {
				throw new EncodeException("Error converting request body", ex);
			}
			if (outputMessage != null) {
				// clear headers
				request.headers(null);
				// converters can modify headers, so update the request
				// with the modified headers
				request.headers(getHeaders(outputMessage.getHeaders()));

				// do not use charset for binary data and protobuf
				Charset charset;

				MediaType contentType = outputMessage.getHeaders().getContentType();
				Charset charsetFromContentType = contentType != null
						? contentType.getCharset() : null;

				if (encoderProperties != null
						&& encoderProperties.isCharsetFromContentType()
						&& charsetFromContentType != null) {
					charset = charsetFromContentType;
				}
				else if (shouldHaveNullCharset(messageConverter, outputMessage)) {
					charset = null;
				}
				else {
					charset = StandardCharsets.UTF_8;
				}
				request.body(outputMessage.getOutputStream().toByteArray(), charset);
				return;
			}
		}
		String message = "Could not write request: no suitable HttpMessageConverter "
				+ "found for request type [" + requestBody.getClass().getName() + "]";
		if (requestContentType != null) {
			message += " and content type [" + requestContentType + "]";
		}
		throw new EncodeException(message);
	}

	private boolean shouldHaveNullCharset(HttpMessageConverter messageConverter,
			FeignOutputMessage outputMessage) {
		return binaryContentType(outputMessage)
				|| messageConverter instanceof ByteArrayHttpMessageConverter
				|| messageConverter instanceof ProtobufHttpMessageConverter
						&& ProtobufHttpMessageConverter.PROTOBUF.isCompatibleWith(
								outputMessage.getHeaders().getContentType());
	}

	@SuppressWarnings("unchecked")
	private FeignOutputMessage checkAndWrite(Object body, MediaType contentType,
			HttpMessageConverter converter, RequestTemplate request) throws IOException {
		if (converter.canWrite(body.getClass(), contentType)) {
			logBeforeWrite(body, contentType, converter);
			FeignOutputMessage outputMessage = new FeignOutputMessage(request);
			converter.write(body, contentType, outputMessage);
			return outputMessage;
		}
		else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private FeignOutputMessage checkAndWrite(Object body, Type genericType,
			MediaType contentType, GenericHttpMessageConverter converter,
			RequestTemplate request) throws IOException {
		if (converter.canWrite(genericType, body.getClass(), contentType)) {
			logBeforeWrite(body, contentType, converter);
			FeignOutputMessage outputMessage = new FeignOutputMessage(request);
			converter.write(body, genericType, contentType, outputMessage);
			return outputMessage;
		}
		else {
			return null;
		}
	}

	private void logBeforeWrite(Object requestBody, MediaType requestContentType,
			HttpMessageConverter messageConverter) {
		if (log.isDebugEnabled()) {
			if (requestContentType != null) {
				log.debug("Writing [" + requestBody + "] as \"" + requestContentType
						+ "\" using [" + messageConverter + "]");
			}
			else {
				log.debug(
						"Writing [" + requestBody + "] using [" + messageConverter + "]");
			}
		}
	}

	private boolean isFormRelatedContentType(MediaType requestContentType) {
		return isMultipartType(requestContentType)
				|| isFormUrlEncoded(requestContentType);
	}

	private boolean isMultipartType(MediaType requestContentType) {
		return Arrays.asList(MULTIPART_FORM_DATA, MULTIPART_MIXED, MULTIPART_RELATED)
				.contains(requestContentType);
	}

	private boolean isFormUrlEncoded(MediaType requestContentType) {
		return Objects.equals(APPLICATION_FORM_URLENCODED, requestContentType);
	}

	private boolean binaryContentType(FeignOutputMessage outputMessage) {
		MediaType contentType = outputMessage.getHeaders().getContentType();
		return contentType == null || Stream
				.of(MediaType.APPLICATION_CBOR, MediaType.APPLICATION_OCTET_STREAM,
						MediaType.APPLICATION_PDF, MediaType.IMAGE_GIF,
						MediaType.IMAGE_JPEG, MediaType.IMAGE_PNG)
				.anyMatch(mediaType -> mediaType.includes(contentType));
	}

    private Type getSpringFormEncoderType(MediaType requestContentType, Type bodyType, Class<?> requestType) {
        if (!Objects.equals(requestContentType, MediaType.MULTIPART_FORM_DATA) && !Objects.equals(requestContentType, MediaType.APPLICATION_FORM_URLENCODED)) {
            return null;
        }
        //MultiValueMap由FormHttpMessageConverter处理
        if (MultiValueMap.class.isAssignableFrom(requestType)) {
            return null;
        }
        if (!(bodyType instanceof ParameterizedType)) {
            return bodyType;
        }
        Type rawType = ((ParameterizedType) bodyType).getRawType();
        if (!(rawType instanceof Class)) {
            return bodyType;
        }
        if (!Map.class.isAssignableFrom((Class<?>) rawType)) {
            return bodyType;
        }
        Type[] actualTypeArguments = ((ParameterizedType) bodyType).getActualTypeArguments();
        if (ArrayUtils.getLength(actualTypeArguments) != 2) {
            return bodyType;
        }
        if (actualTypeArguments[0].equals(String.class)) {
            return Encoder.MAP_STRING_WILDCARD;
        }
        return bodyType;
    }

    private static HttpHeaders getHttpHeaders(Map<String, Collection<String>> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();
        for (Map.Entry<String, Collection<String>> entry : headers.entrySet()) {
            httpHeaders.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return httpHeaders;
    }

    private static Map<String, Collection<String>> getHeaders(HttpHeaders httpHeaders) {
        LinkedHashMap<String, Collection<String>> headers = new LinkedHashMap<>();

        for (Map.Entry<String, List<String>> entry : httpHeaders.entrySet()) {
            headers.put(entry.getKey(), entry.getValue());
        }

        return headers;
    }

	private final class FeignOutputMessage implements HttpOutputMessage {

		private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		private final HttpHeaders httpHeaders;

		private FeignOutputMessage(RequestTemplate request) {
			httpHeaders = getHttpHeaders(request.headers());
		}

		@Override
		public OutputStream getBody() throws IOException {
			return outputStream;
		}

		@Override
		public HttpHeaders getHeaders() {
			return httpHeaders;
		}

		public ByteArrayOutputStream getOutputStream() {
			return outputStream;
		}

	}

}
