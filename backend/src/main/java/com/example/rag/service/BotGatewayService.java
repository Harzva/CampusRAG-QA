package com.example.rag.service;

import com.example.rag.config.BotGatewayProperties;
import com.example.rag.dto.AnswerWithSources;
import com.example.rag.dto.BotMessageRequest;
import com.example.rag.dto.BotMessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
public class BotGatewayService {
    private final BotGatewayProperties properties;
    private final RagService ragService;
    private final LLMWikiService wikiService;
    private final BotIdempotencyService idempotencyService;
    private final BotRateLimitService rateLimitService;

    public BotGatewayService(BotGatewayProperties properties,
                             RagService ragService,
                             LLMWikiService wikiService,
                             BotIdempotencyService idempotencyService,
                             BotRateLimitService rateLimitService) {
        this.properties = properties;
        this.ragService = ragService;
        this.wikiService = wikiService;
        this.idempotencyService = idempotencyService;
        this.rateLimitService = rateLimitService;
    }

    public BotMessageResponse handle(BotMessageRequest request) {
        String channel = normalize(request.getChannel(), "unknown");
        String mode = normalize(request.getMode(), properties.getDefaultMode());
        String text = request.getText();
        if (!StringUtils.hasText(text)) {
            return BotMessageResponse.failure(channel, request.getConversationId(), mode, "text is required");
        }

        assertModeAllowed(channel, mode);
        String conversationId = StringUtils.hasText(request.getConversationId())
                ? request.getConversationId()
                : channel + ":" + normalize(request.getSenderId(), "anonymous");

        String messageId = request.getMessageId();
        String tenantId = normalize(request.getTenantId(), "default");

        if (!rateLimitService.isAllowed(tenantId, channel)) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Bot rate limit exceeded");
        }

        boolean idempotencyAcquired = false;
        if (StringUtils.hasText(messageId)) {
            idempotencyAcquired = idempotencyService.acquire(tenantId, channel, messageId);
            if (!idempotencyAcquired) {
                return BotMessageResponse.success(channel, conversationId, mode, "Duplicate message ignored.");
            }
        }

        try {
            AnswerWithSources result = switch (mode) {
                case "wiki" -> wikiService.queryWithSources(text, tenantId);
                case "rag" -> ragService.askWithSources(conversationId, text, tenantId);
                default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported bot mode: " + mode);
            };
            return BotMessageResponse.successWithSources(channel, conversationId, mode, result.getAnswer(), result.getSources());
        } catch (RuntimeException e) {
            if (idempotencyAcquired) {
                idempotencyService.release(tenantId, channel, messageId);
            }
            throw e;
        }
    }

    private void assertModeAllowed(String channel, String mode) {
        var allowedModes = properties.channel(channel).getAllowedModes();
        if (!allowedModes.isEmpty() && allowedModes.stream().noneMatch(allowed -> mode.equalsIgnoreCase(allowed))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Mode is not allowed for channel: " + mode);
        }
    }

    private String normalize(String value, String fallback) {
        String candidate = StringUtils.hasText(value) ? value : fallback;
        return candidate == null ? "" : candidate.trim().toLowerCase(Locale.ROOT);
    }
}
