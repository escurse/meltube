package com.escass.meltube;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@SpringBootTest
class MeltubeApplicationTests {

    @Test
    void contextLoads() throws IOException, InterruptedException {
        Document doc = Jsoup.connect("https://www.melon.com/song/detail.htm?songId=36356993").get();
        Elements $list = doc.select(".list");
        Elements $album = $list.select("dd:nth-child(2)");
        Elements $release = $list.select("dd:nth-child(4)");
        Elements $genre = $list.select("dd:nth-child(6)");
        Elements $lyric = doc.select(".lyric");

        System.out.println("앨범");
        System.out.println($album.text());

        System.out.println("발매일");
        System.out.println($release.text());

        System.out.println("장르");
        System.out.println($genre.text());

        System.out.println("가사");
        System.out.println($lyric.text());
    }

}
