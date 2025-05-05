package com.asaki0019.enterprisemanagementsb.core.utils;

// MessageConstructor.java 增强版
public class MessageConstructor {
    private static final String TITLE_BORDER = "=".repeat(60);
    private static final String ROW_FORMAT = "│ %-25s │ %-28s │%n";

    public static String constructMessage(String title, Object... params) {
        StringBuilder sb = new StringBuilder();

        // 构造标题区块
        appendTitle(sb, title);

        // 参数校验
        if (params.length % 2 != 0) {
            throw new IllegalArgumentException("参数必须成对出现（键值对格式）");
        }

        // 构造内容表格
        appendContent(sb, params);

        // 添加结束线
        sb.append(TITLE_BORDER).append("\n");
        return sb.toString();
    }

    public static String constructPlainMessage(String operation, Object... params) {
        // 参数校验增强
        if (operation == null || operation.trim().isEmpty()) {
            throw new IllegalArgumentException("操作类型不能为空");
        }
        if (params.length % 2 != 0) {
            throw new IllegalArgumentException("参数必须成对出现（键值对格式）");
        }

        StringBuilder sb = new StringBuilder();
        // 添加操作类型标识
        sb.append("【").append(operation).append("】");

        // 构建键值对
        for (int i = 0; i < params.length; i += 2) {
            sb.append(" | ");
            String key = String.valueOf(params[i]);
            Object value = params[i + 1];
            sb.append(key).append(": ").append(value);
        }
        return sb.toString();
    }

    private static void appendTitle(StringBuilder sb, String title) {
        sb.append("\n").append(TITLE_BORDER).append("\n");
        sb.append(String.format("‖ %-54s ‖%n", centerAlign(title, 54)));
        sb.append(TITLE_BORDER).append("\n");
    }

    private static void appendContent(StringBuilder sb, Object[] params) {
        for (int i = 0; i < params.length; i += 2) {
            String key = String.valueOf(params[i]);
            Object value = (i + 1 < params.length) ? params[i + 1] : "N/A";
            sb.append(String.format(ROW_FORMAT, key, value));
        }
    }

    private static String centerAlign(String text, int width) {
        if (text.length() >= width) return text;
        int padding = (width - text.length()) / 2;
        return String.format("%" + (padding + text.length()) + "s", text)
                + String.format("%" + (width - padding - text.length()) + "s", "");
    }
}