package io.quarkus.websockets.next;

import io.vertx.core.buffer.Buffer;

/**
 * Used to encode and decode binary messages.
 *
 * <h2>Special types of messages</h2>
 *
 * Some types of messages bypass the encoding/decoding process:
 * <ul>
 * <li>{@code io.vertx.core.buffer.Buffer},</li>
 * <li>{@code byte[]},</li>
 * <li>{@code java.lang.String},</li>
 * <li>{@code io.vertx.core.json.JsonObject}.</li>
 * <li>{@code io.vertx.core.json.JsonArray}.</li>
 * </ul>
 * The encoding/decoding details are described in {@link OnBinaryMessage}.
 *
 * <h2>CDI beans</h2>
 * Implementation classes must be CDI beans. Qualifiers are ignored. {@link jakarta.enterprise.context.Dependent} beans are
 * reused during encoding/decoding.
 *
 * <h2>Lifecycle and concurrency</h2>
 * Codecs are shared accross all WebSocket connections. Therefore, implementations should be either stateless or thread-safe.
 *
 * @param <T>
 * @see OnBinaryMessage
 */
public interface BinaryMessageCodec<T> extends MessageCodec<T, Buffer> {

}
