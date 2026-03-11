package com.star.ai.agent;

import java.util.ArrayList;
import java.util.List;

/**
 * 线程安全的工具调用记录器，用于追踪 Agent 每一轮调了哪些工具
 */
public class ToolCallRecorder {

    private static final ThreadLocal<List<AgentResponse.ToolCallRecord>> RECORDS =
            ThreadLocal.withInitial(ArrayList::new);

    public static void clear() {
        RECORDS.get().clear();
    }

    public static void record(String name, String arguments, String result) {
        RECORDS.get().add(AgentResponse.ToolCallRecord.builder()
                .name(name).arguments(arguments).result(result).build());
    }

    public static List<AgentResponse.ToolCallRecord> getAndClear() {
        List<AgentResponse.ToolCallRecord> list = new ArrayList<>(RECORDS.get());
        RECORDS.get().clear();
        return list;
    }
}
