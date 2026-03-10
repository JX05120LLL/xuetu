package com.star.ai.skill;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 技能注册中心客户端
 *
 * 启动时从 Nacos 配置中心拉取 skill-registry.json，
 * 解析后缓存到内存 Map；同时注册监听器，配置变更时自动热更新。
 */
@Slf4j
@Service
public class SkillRegistryService {

    private static final String DATA_ID = "skill-registry.json";
    private static final String GROUP   = "DEFAULT_GROUP";
    private static final long   TIMEOUT = 5000L;

    @Value("${spring.cloud.nacos.config.server-addr:localhost:8848}")
    private String nacosServerAddr;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /** skillId -> SkillDefinition */
    private final Map<String, SkillDefinition> skillCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        try {
            ConfigService configService = NacosFactory.createConfigService(nacosServerAddr);

            // 启动时立即拉取一次
            String content = configService.getConfig(DATA_ID, GROUP, TIMEOUT);
            parseAndCache(content);

            // 注册监听器，配置变更时自动刷新
            configService.addListener(DATA_ID, GROUP, new Listener() {
                @Override
                public Executor getExecutor() {
                    return Executors.newSingleThreadExecutor();
                }

                @Override
                public void receiveConfigInfo(String configInfo) {
                    log.info("[SkillRegistry] 检测到 Nacos 配置变更，重新加载技能注册表");
                    parseAndCache(configInfo);
                }
            });

        } catch (Exception e) {
            log.error("[SkillRegistry] 初始化失败，技能注册表为空: {}", e.getMessage());
        }
    }

    /**
     * 根据 skillId 获取技能定义，不存在则返回 Optional.empty()
     */
    public Optional<SkillDefinition> findById(String skillId) {
        return Optional.ofNullable(skillCache.get(skillId));
    }

    /**
     * 获取全部已注册技能列表
     */
    public List<SkillDefinition> listAll() {
        return new ArrayList<>(skillCache.values());
    }

    /**
     * 根据关键词在 triggers 列表中模糊匹配技能（用于意图识别）
     */
    public Optional<SkillDefinition> matchByKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return Optional.empty();
        }
        String lowerKeyword = keyword.toLowerCase();
        return skillCache.values().stream()
                .filter(skill -> skill.getTriggers() != null &&
                        skill.getTriggers().stream()
                                .anyMatch(t -> lowerKeyword.contains(t.toLowerCase())))
                .findFirst();
    }

    // ── 私有方法 ──────────────────────────────────────────────────────────

    private void parseAndCache(String content) {
        if (content == null || content.isBlank()) {
            log.warn("[SkillRegistry] Nacos 返回的配置内容为空，跳过解析");
            return;
        }
        try {
            JsonNode root = objectMapper.readTree(content);
            JsonNode skillsNode = root.get("skills");
            if (skillsNode == null || !skillsNode.isArray()) {
                log.warn("[SkillRegistry] skill-registry.json 中缺少 'skills' 数组，跳过解析");
                return;
            }

            Map<String, SkillDefinition> newCache = new ConcurrentHashMap<>();
            for (JsonNode node : skillsNode) {
                SkillDefinition skill = objectMapper.treeToValue(node, SkillDefinition.class);
                newCache.put(skill.getId(), skill);
                log.debug("[SkillRegistry] 加载技能: id={}, name={}, invokeType={}",
                        skill.getId(), skill.getName(), skill.getInvokeType());
            }

            skillCache.clear();
            skillCache.putAll(newCache);
            log.info("[SkillRegistry] 技能注册表加载完成，共 {} 个技能", skillCache.size());

        } catch (Exception e) {
            log.error("[SkillRegistry] 解析 skill-registry.json 失败: {}", e.getMessage());
        }
    }
}
