package com.heiheilianzai.app.comic.been;

import com.heiheilianzai.app.bean.BaseAd;
import com.heiheilianzai.app.bean.BaseTag;
import com.heiheilianzai.app.book.been.ReadHistory;

import java.util.List;

public class DiscoveryComic  extends BaseAd {
    public String comic_id;//": 10086, //漫画id
    public String cover; //竖封面
    public String description;//"": "美貌千金与帅气王爷", //描述
    public List<BaseTag> tag;
    public String flag;//"": "更新至32话", //角标
    public String title;//"": "乔乔的奇妙冒险", //漫画名称

    @Override
    public String toString() {
        return "DiscoveryComic{" +
                "comic_id='" + comic_id + '\'' +
                ", cover='" + cover + '\'' +
                ", description='" + description + '\'' +
                ", tag=" + tag +
                ", flag='" + flag + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
