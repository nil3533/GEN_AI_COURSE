package com.sani.ragdocs.service;

import com.sani.ragdocs.config.RagProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class TextChunker {

    private final RagProperties properties;

    public TextChunker(RagProperties properties) {
        this.properties = properties;
    }

    public List<String> split(String content) {
        if (!StringUtils.hasText(content)) {
            throw new IllegalArgumentException("content is required.");
        }

        var chunkSize = properties.getChunkSize();
        var overlap = properties.getChunkOverlap();
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("app.rag.chunk-size must be greater than 0.");
        }
        if (overlap < 0 || overlap >= chunkSize) {
            throw new IllegalArgumentException("app.rag.chunk-overlap must be between 0 and chunk-size - 1.");
        }

        var normalized = content.replace("\r\n", "\n")
                .replace('\r', '\n')
                .replaceAll("[ \\t]+", " ")
                .trim();

        if (normalized.length() <= chunkSize) {
            return List.of(normalized);
        }

        var chunks = new ArrayList<String>();
        var start = 0;
        while (start < normalized.length()) {
            var end = Math.min(start + chunkSize, normalized.length());
            if (end < normalized.length()) {
                end = chooseNaturalBreak(normalized, start, end, chunkSize);
            }

            var chunk = normalized.substring(start, end).trim();
            if (StringUtils.hasText(chunk)) {
                chunks.add(chunk);
            }

            if (end >= normalized.length()) {
                break;
            }

            var nextStart = Math.max(0, end - overlap);
            if (nextStart <= start) {
                nextStart = start + 1;
            }
            start = nextStart;
        }

        return List.copyOf(chunks);
    }

    private int chooseNaturalBreak(String text, int start, int proposedEnd, int chunkSize) {
        var minimumBreak = start + Math.max(1, chunkSize / 2);
        var paragraphBreak = text.lastIndexOf("\n\n", proposedEnd);
        if (paragraphBreak > minimumBreak) {
            return paragraphBreak + 2;
        }

        var sentenceBreak = text.lastIndexOf(". ", proposedEnd);
        if (sentenceBreak > minimumBreak) {
            return sentenceBreak + 1;
        }

        var lineBreak = text.lastIndexOf('\n', proposedEnd);
        if (lineBreak > minimumBreak) {
            return lineBreak + 1;
        }

        var wordBreak = text.lastIndexOf(' ', proposedEnd);
        if (wordBreak > minimumBreak) {
            return wordBreak;
        }

        return proposedEnd;
    }
}
