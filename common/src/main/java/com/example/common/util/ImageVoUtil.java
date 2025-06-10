package com.example.common.util;

import com.example.common.domain.ImageVo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageVoUtil {
    public static ImageVo transformObj(String pic) {
        if (null == pic || "".equals(pic)) {
            return null;
        }

        ImageVo imageVo = new ImageVo();
        imageVo.setSrc(pic);
        if (pic.contains(".")) {
            String fileSuffix = pic.contains("?") ?
                    pic.substring(pic.lastIndexOf(".") + 1, pic.indexOf("?")) :
                    pic.substring(pic.lastIndexOf(".") + 1);

            if (!"".equals(fileSuffix)) {
                String regex = "_([0-9]+x[0-9]+)\\." + fileSuffix; // ，匹配一个或多个数字
                Pattern pattern = Pattern.compile(regex); // 编译正则表达式
                Matcher matcher = pattern.matcher(pic);
                // 检查是否匹配
                if (matcher.find()) {
                    String dimensions = matcher.group(1); // 捕获组的索引从 1 开始，获取第一个捕获组的内容，定义正则表达式 ([0-9]+x[0-9]+) 捕获组
                    String[] split = dimensions.split("x");
                    if (2 == split.length) {
                        float v = Float.parseFloat(split[1]);
                        if (v != 0f) {
                            imageVo.setAr(Float.parseFloat(split[0]) / v);
                        }
                    }
                }
            }
        }
        return imageVo;
    }
}
