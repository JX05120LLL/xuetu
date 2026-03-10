package com.star.ai.skill;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 技能定义 —— 对应 Nacos skill-registry.json 中每条技能的结构
 *
 * invokeType 取值：
 *   java_http        : 调用本系统微服务（直接 HTTP POST invokeUrl）
 *   openclaw_gateway : 调用 OpenClaw Gateway（POST invokeUrl，body={tool,action,args}）
 */
@Data
public class SkillDefinition {

    /** 技能唯一 ID，如 xuetu-rag */
    private String id;

    /** 技能名称，如 学途课程知识库检索 */
    private String name;

    /** 技能描述 */
    private String description;

    /** 关键词触发列表，编排层可用于意图匹配 */
    private List<String> triggers;

    /** 调用类型：java_http | openclaw_gateway */
    private String invokeType;

    /** HTTP 调用地址 */
    private String invokeUrl;

    /** HTTP 方法，默认 POST */
    private String method;

    /** OpenClaw Gateway 技能时使用的工具名（toolName） */
    private String toolName;

    /** 参数 Schema，描述技能接受哪些参数（供文档/编排参考） */
    private Map<String, Object> argsSchema;
}
