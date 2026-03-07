package com.star.ai.config;

import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RAG 知识库配置
 *
 * ChatModel / VectorStore / EmbeddingModel 由 Spring AI starter 自动装配。
 * 本类只补充需要手动配置的辅助 Bean。
 */
@Configuration
public class RagConfig {

    /**
     * 文本分块器：用于将长文档切成合适大小的 chunk 再写入向量库。
     *
     * 参数说明：
     *   defaultChunkSize   = 512  token（每块默认大小）
     *   minChunkSizeChars  = 350  字符（低于此长度不再分割）
     *   minChunkLengthToEmbed = 5 字符（过短的 chunk 直接丢弃）
     *   maxNumChunks       = 10000（单文档最多允许的 chunk 数，防止超长文档撑爆内存）
     *   keepSeparator      = true（保留段落分隔符，方便阅读）
     */
    @Bean
    public TokenTextSplitter tokenTextSplitter() {
        return new TokenTextSplitter(512, 350, 5, 10000, true);
    }
}
