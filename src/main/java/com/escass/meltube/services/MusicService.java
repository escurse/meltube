package com.escass.meltube.services;

import com.escass.meltube.entities.MusicEntity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class MusicService {

    public MusicEntity crawlMelon(String id) throws IOException {
        if (id == null || id.length() != 8) {
            return null;
        }
        String url = String.format("https://www.melon.com/song/detail.htm?songId=%s", id);
        Document doc = Jsoup.connect(url).get();
        Elements $name = doc.select(".song_name");
        $name.select(".none").remove();
        Elements $artist = doc.select(".artist_name > span:first-child");
        Elements $list = doc.select("dl.list");
        Elements $album = $list.select("dd:nth-child(2)");
        Elements $release = $list.select("dd:nth-child(4)");
        Elements $genre = $list.select("dd:nth-child(6)");
        Elements $lyric = doc.select(".lyric");
        Elements $cover = doc.select("img[src^=\"https://cdnimg.melon.co.kr/cm2/album/images/\"]");

        MusicEntity music = new MusicEntity();
        music.setArtist($artist.text());
        music.setAlbum($album.text());
        music.setReleaseDate(LocalDate.parse($release.text().replace(".", "-"), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        music.setGenre($genre.text());
        music.setName($name.text());
        music.setLyrics($lyric.text());
        music.setCoverFileName($cover.attr("src"));

        String searchQuery = URLEncoder.encode(String.format("%s %s site:www.youtube.com", music.getArtist(), music.getName()), StandardCharsets.UTF_8);
        Document googleSearchResult = Jsoup.connect(String.format("https://www.google.com/search?q=%s", searchQuery)).get();
        Element $firstH3 = googleSearchResult.selectFirst("h3");
        Element $anchor = $firstH3.parent();
        String href = $anchor.attr("href");
        String youtubeId = href.split("=")[1];
        music.setYoutubeId(youtubeId);
        return music;
    }

    public boolean verifyYoutubeId(String id) throws IOException, InterruptedException {
        if (id == null || id.length() != 11) {
            return false;
        }
        String url = String.format("http://img.youtube.com/vi/%s/mqdefault.jpg", id);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        return response.statusCode() != 404;
    }
}
