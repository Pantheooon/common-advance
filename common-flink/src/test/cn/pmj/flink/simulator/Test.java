package cn.pmj.flink.simulator;

import com.cloudwise.sdg.dic.DicInitializer;
import com.cloudwise.sdg.template.TemplateAnalyzer;

public class Test {


    //https://github.com/CloudWise-OpenSource/Data-Processer
    @org.junit.Test
    public void test() throws Exception {
        //加载词典(只需执行一次即可)
        DicInitializer.init();
        //编辑模版
        String tplName = "abc.tpl";
        String tpl = "My name is $Dic{name}, my age is $Func{intRand(1,5)}";
        //创建模版分析器（一个模版new一个TemplateAnalyzer对象即可）
        TemplateAnalyzer testTplAnalyzer = new TemplateAnalyzer(null, tpl);
        //分析模版生成模拟数据
        String abc = testTplAnalyzer.analyse();
        //打印分析结果
        System.out.println(abc);
    }
}
