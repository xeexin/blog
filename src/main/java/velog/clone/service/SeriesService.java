package velog.clone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import velog.clone.domain.Blog;
import velog.clone.domain.Post;
import velog.clone.domain.Series;
import velog.clone.domain.User;
import velog.clone.repository.SeriesRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SeriesService {

    private final SeriesRepository seriesRepository;

    // Series : id, Post , SeriesName

    public Series createSeries(Post post, String seriesName) {
        Series series = new Series();
        series.setSeriesName(seriesName);
        return seriesRepository.save(series);
    }


    public List<Series> findByBlogId(Long blogId) {
        return seriesRepository.findByBlogId(blogId);
    }

    public void save(Series series) {
        seriesRepository.save(series);
    }

    public Series findBySeriesId(Long seriesId) {
        return seriesRepository.findById(seriesId)
                .orElseThrow(() -> new IllegalArgumentException("시리즈가 없습니다."));
    }

    public void deleteSeries(Series series) {
        seriesRepository.delete(series);

    }


    public Series findBySeriesName(String seriesName) {
        return seriesRepository.findBySeriesName(seriesName);
    }
}
