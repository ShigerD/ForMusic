package com.example.ningyuwen.music.model.entity.lyric;

import java.util.List;

/**
 * 歌词
 * Created by ningyuwen on 17-10-10.
 */

public class Lyric {


    /**
     * code : 0
     * count : 15
     * result : [{"aid":1563419,"artist_id":9208,"lrc":"http://s.gecimi.com/lrc/166/16685/1668536.lrc","sid":1668536,"song":"海阔天空"},{"aid":1567586,"artist_id":9208,"lrc":"http://s.gecimi.com/lrc/167/16739/1673997.lrc","sid":1673997,"song":"海阔天空"},{"aid":1571906,"artist_id":9208,"lrc":"http://s.gecimi.com/lrc/167/16796/1679605.lrc","sid":1679605,"song":"海阔天空"},{"aid":1573814,"artist_id":9208,"lrc":"http://s.gecimi.com/lrc/168/16819/1681961.lrc","sid":1681961,"song":"海阔天空"},{"aid":1656038,"artist_id":9208,"lrc":"http://s.gecimi.com/lrc/179/17907/1790768.lrc","sid":1790768,"song":"海阔天空"},{"aid":1718741,"artist_id":9208,"lrc":"http://s.gecimi.com/lrc/187/18757/1875769.lrc","sid":1875769,"song":"海阔天空"},{"aid":2003267,"artist_id":9208,"lrc":"http://s.gecimi.com/lrc/226/22642/2264296.lrc","sid":2264296,"song":"海阔天空"},{"aid":2020610,"artist_id":9208,"lrc":"http://s.gecimi.com/lrc/228/22889/2288967.lrc","sid":2288967,"song":"海阔天空"},{"aid":2051678,"artist_id":9208,"lrc":"http://s.gecimi.com/lrc/233/23323/2332322.lrc","sid":2332322,"song":"海阔天空"},{"aid":2412704,"artist_id":9208,"lrc":"http://s.gecimi.com/lrc/283/28376/2837689.lrc","sid":2837689,"song":"海阔天空"},{"aid":2607041,"artist_id":9208,"lrc":"http://s.gecimi.com/lrc/311/31116/3111659.lrc","sid":3111659,"song":"海阔天空"},{"aid":2647055,"artist_id":9208,"lrc":"http://s.gecimi.com/lrc/316/31663/3166350.lrc","sid":3166350,"song":"海阔天空"},{"aid":2657468,"artist_id":9208,"lrc":"http://s.gecimi.com/lrc/318/31803/3180339.lrc","sid":3180339,"song":"海阔天空"},{"aid":3093833,"artist_id":9208,"lrc":"http://s.gecimi.com/lrc/377/37740/3774083.lrc","sid":3774083,"song":"海阔天空"},{"aid":3161846,"artist_id":9208,"lrc":"http://s.gecimi.com/lrc/386/38612/3861244.lrc","sid":3861244,"song":"海阔天空"}]
     */

    private int code;
    private int count;
    private List<LyricUrl> result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<LyricUrl> getResult() {
        return result;
    }

    public void setResult(List<LyricUrl> result) {
        this.result = result;
    }

    public static class LyricUrl {
        /**
         * aid : 1563419
         * artist_id : 9208
         * lrc : http://s.gecimi.com/lrc/166/16685/1668536.lrc
         * sid : 1668536
         * song : 海阔天空
         */

        private int aid;
        private int artist_id;
        private String lrc;
        private int sid;
        private String song;

        public int getAid() {
            return aid;
        }

        public void setAid(int aid) {
            this.aid = aid;
        }

        public int getArtist_id() {
            return artist_id;
        }

        public void setArtist_id(int artist_id) {
            this.artist_id = artist_id;
        }

        public String getLrc() {
            return lrc;
        }

        public void setLrc(String lrc) {
            this.lrc = lrc;
        }

        public int getSid() {
            return sid;
        }

        public void setSid(int sid) {
            this.sid = sid;
        }

        public String getSong() {
            return song;
        }

        public void setSong(String song) {
            this.song = song;
        }
    }
}
