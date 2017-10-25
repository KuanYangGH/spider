package util;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import yotta.yangkuan.ZhihuProcessor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.text.SimpleDateFormat;

public class SqlPipeline implements Pipeline{
    //主题表
    String topicTable = "domain_topic";
    //分面表
    String facetTable = "facet";
    //碎片表
    String fragmentTable = "assemble_fragment";
    //课程名
    String course = "数据结构";
    //

    public void process(ResultItems resultItems, Task task) {
        for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()){
            for(String fragmentContent:(List<String>)entry.getValue()){
                //分面信息
                Map<String,Object> facetTableMap = resultItems.getRequest().getExtras();

                String addSql = "insert into " + fragmentTable
                        + "(FragmentContent,FragmentScratchTime,TermID,TermName,FacetName,FacetLayer,ClassName) values (?,?,?,?,?,?,?)";

                //定义插入语句参数
                List<Object> params = new ArrayList<Object>();
                Mysql mysql = new Mysql();
                //添加碎片表需要的元组值
                /*FragmentID 碎片ID 自动递增不需要*/
                /* FragmentContent 碎片内容*/
                params.add(fragmentContent);
                /*FragmentScratchTime 碎片爬取时间*/
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                String date = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳
                params.add(date);
                /*TermID 主题ID*/
                params.add(facetTableMap.get("TermID"));
                /*TermName 主题名*/
                params.add(facetTableMap.get("TermName"));
                /*FacetName 分面名*/
                params.add(facetTableMap.get("FacetName"));
                /*FacetLayer 分面层*/
                params.add(facetTableMap.get("FacetLayer"));
                /*ClassName 课程名*/
                params.add(facetTableMap.get("ClassName"));
                try {
                    mysql.addDeleteModify(addSql,params);
                }
                catch (SQLException exception){
                    System.out.println(exception.getMessage());
                } finally {
                    mysql.closeconnection();
                }
            }

        }
    }
}