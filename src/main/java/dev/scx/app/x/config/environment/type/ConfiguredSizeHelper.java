package dev.scx.app.x.config.environment.type;

import java.text.DecimalFormat;
import java.util.regex.Pattern;

/// ConfiguredSizeHelper
///
/// @author scx567888
public final class ConfiguredSizeHelper {

    /// 文件大小格式化 正则表达式
    private final static Pattern DISPLAY_SIZE_PATTERN = Pattern.compile("^([\\d.]+) *([a-zA-Z]{0,2})$");

    /// 将 格式化后的大小转换为 long
    /// 如将 1KB 转换为 1024
    ///
    /// @param str 待转换的值 如 5MB 13.6GB
    /// @return a long.
    public static long displaySizeToLong(String str) {
        var matcher = DISPLAY_SIZE_PATTERN.matcher(str);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(str + " : 无法转换为 long !!!");
        }
        var amount = Double.parseDouble(matcher.group(1));
        var units = matcher.group(2);
        var s = switch (units) {
            case "", "B" -> 1L;
            case "KB" -> 1024L;
            case "MB" -> 1024 * 1024L;
            case "GB" -> 1024 * 1024 * 1024L;
            case "TB" -> 1024 * 1024 * 1024 * 1024L;
            default -> throw new IllegalArgumentException(units + " : 未知的数据单位 !!!");
        };
        return (long) (amount * s);
    }

    /// 将 long 类型的文件大小 格式化(转换为人类可以看懂的形式)
    /// 如 1024 转换为 1KB
    ///
    /// @param size a long.
    /// @return a [String] object.
    public static String longToDisplaySize(long size) {
        if (size <= 0) {
            return "0";
        }
        var units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

}
