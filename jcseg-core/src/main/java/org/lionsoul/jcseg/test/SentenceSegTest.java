package org.lionsoul.jcseg.test;

import java.io.IOException;
import java.io.StringReader;

import org.lionsoul.jcseg.tokenizer.Sentence;
import org.lionsoul.jcseg.tokenizer.SentenceSeg;

/**
 * sentence seg test program
 * 
 * @author chenxin<chenxin619315@gmail.com>
*/
public class SentenceSegTest 
{

    public static void main(String[] args) 
    {
        String doc = "冰岛时间7月1日，正在当地拍片的汤姆·克鲁斯通过发言人承认，他与第三任妻子凯蒂·赫尔墨斯（第一二任妻子分别为咪咪·罗杰斯、妮可·基德曼）的婚姻即将结束。"
                + "讽刺的是，3个女人都是在33岁离开这位“碟中谍”英雄的。"
                + "“三进三出”的婚姻令阿汤哥昔日“万人迷”的形象遭受严重冲击，也让公众不解，阿汤哥为什么找不到一个能长相依的爱人呢？"
                + "记者调查发现，阿汤哥的超强控制欲和生活中的种种“怪异行为”是导致其婚姻屡屡失败的原因。";
    
        try {
            SentenceSeg seg = new SentenceSeg(new StringReader(doc));
            Sentence sen = null;
            while ( (sen = seg.next()) != null )
            {
                System.out.println(sen);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
