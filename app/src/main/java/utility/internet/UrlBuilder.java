package utility.internet;

import java.util.ArrayList;

import utility.StringUtils;

/**
 * Builder для сетевых запросов
 */
public class UrlBuilder{
    ArrayList<Pair> pairs;
    String oper;
    String version;
    String url;
    public UrlBuilder(String url,String oper,String version){
        this.oper=oper;
        this.version=version;
        this.url=url;
        pairs=new ArrayList<>();
    }
    public UrlBuilder put(String property,String value){
        pairs.add(new Pair(property,value));
        return this;
    }
    public UrlBuilder put(String property,int value){
        pairs.add(new Pair(property,String.valueOf(value)));
        return this;
    }
    public String build(){
        String word="COWBOW";
        String param="";
        String result=url+"?ReqName="+oper+"&version="+version;
        for (Pair p:pairs){
            param+=p.value;
            result+="&"+p.property+"="+p.value;
        }
        String hash= StringUtils.MD5(word+param+version+oper);
        result+="&hash="+hash;
        return result;
    }
    private class Pair{
        String property;
        String value;
        public Pair(String property,String value){
            this.property=property;
            this.value=value;
        }
    }
}
