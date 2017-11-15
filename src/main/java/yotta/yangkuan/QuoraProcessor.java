package yotta.yangkuan;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import util.Config;
import util.ProcessorSQL;
import util.SqlPipeline;
import util.Translate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuoraProcessor implements PageProcessor{

    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setTimeOut(10000);
    public Site getSite() {
        return site;
    }
    public void process(Page page) {
        List<String> fragments;
        //爬取碎片
        fragments = page.getHtml().xpath("div[@class='truncated_q_text search_result_snippet']").all();
        //提交数据
        page.putField("fragment",fragments);
    }
    public static void main(String[] args){

        //1.获取分面名
        ProcessorSQL processorSQL = new ProcessorSQL();
        List<Map<String, Object>> allFacetsInformation = processorSQL.getAllFacets(Config.facetTable);
        //2.添加连接请求
        List<Request> requests = new ArrayList<Request>();
        for(Map<String, Object> facetInformation:allFacetsInformation){
            Request request = new Request();

            //中文翻译成英文
            String termName = Translate.translateCE2En((String) facetInformation.get("TermName"));
            String facetName = Translate.translateCE2En((String) facetInformation.get("FacetName"));

            System.out.println(termName+" "+facetName);
            String url = "https://www.quora.com/search?q="
                    //+facetInformation.get("ClassName")+" "
                    +termName +" "
                    +facetName;
            //添加链接;设置额外信息
            requests.add(request.setUrl(url).setExtras(facetInformation));
        }
        //3.创建ZhihuProcessor
        YangKuanSpider.create(new QuoraProcessor())
                .addRequests(requests)
                .thread(5)
                .addPipeline(new SqlPipeline())
                .runAsync();
    }
}
