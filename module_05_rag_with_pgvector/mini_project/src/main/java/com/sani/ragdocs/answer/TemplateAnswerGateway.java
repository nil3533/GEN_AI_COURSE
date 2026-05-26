package com.sani.ragdocs.answer;

import com.sani.ragdocs.store.RetrievedChunk;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(name = "app.rag.chat-provider", havingValue = "template", matchIfMissing = true)
public class TemplateAnswerGateway implements AnswerGateway {

    private static final Pattern SENTENCE_BOUNDARY = Pattern.compile("(?<=[.!?])\\s+");

    private static final Set<String> STOP_WORDS = Set.of(
            "a", "an", "and", "are", "as", "for", "from", "how", "in", "is", "it",
            "of", "on", "or", "the", "to", "what", "when", "where", "which", "why", "with"
    );

    @Override
    public String answer(String question, List<RetrievedChunk> context) {
        if (context == null || context.isEmpty()) {
            return "I do not have enough retrieved context to answer this question.";
        }

        var keywords = keywords(question);
        var selectedSentences = context.stream()
                .flatMap(chunk -> Arrays.stream(SENTENCE_BOUNDARY.split(chunk.content())))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .filter(sentence -> containsAnyKeyword(sentence, keywords))
                .limit(3)
                .toList();

        if (selectedSentences.isEmpty()) {
            selectedSentences = context.stream()
                    .map(RetrievedChunk::content)
                    .map(this::firstSentence)
                    .filter(StringUtils::hasText)
                    .limit(2)
                    .toList();
        }

        var sourceMarkers = context.stream()
                .limit(3)
                .map(chunk -> "%s chunk %d".formatted(chunk.documentId(), chunk.chunkIndex()))
                .collect(Collectors.joining("; "));

        return "%s Source context: %s.".formatted(String.join(" ", selectedSentences), sourceMarkers).trim();
    }

    private Set<String> keywords(String question) {
        if (!StringUtils.hasText(question)) {
            return Set.of();
        }

        return Arrays.stream(question.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", " ").split("\\s+"))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .filter(token -> token.length() > 2)
                .filter(token -> !STOP_WORDS.contains(token))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private boolean containsAnyKeyword(String sentence, Set<String> keywords) {
        if (keywords.isEmpty()) {
            return true;
        }

        var lower = sentence.toLowerCase(Locale.ROOT);
        return keywords.stream().anyMatch(lower::contains);
    }

    private String firstSentence(String content) {
        var sentences = SENTENCE_BOUNDARY.split(content);
        return sentences.length == 0 ? content.trim() : sentences[0].trim();
    }
}
