package com.example.handson.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.handson.model.Post;

@Service
public class PostService {
    // デモ用にシンプルなインメモリストレージを使用しています
    private final Map<Long, Post> posts = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1L);

    public List<Post> searchPostsWithFilters(String keyword, Long beforeId, Long afterId, String fromDate,
            String toDate, Boolean isDraft, Integer minWordCount, Integer maxWordCount) {
        List<Post> result = new java.util.ArrayList<>();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        java.util.Date fromD = null;
        java.util.Date toD = null;
        try {
            if (fromDate != null && !fromDate.isEmpty())
                fromD = sdf.parse(fromDate);
            if (toDate != null && !toDate.isEmpty())
                toD = sdf.parse(toDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Map.Entry<Long, Post> entry : posts.entrySet()) {
            Post p = entry.getValue();
            boolean match = true;
            if (isDraft != null && p.isDraft() != isDraft)
                match = false;
            if (match && keyword != null && !keyword.isEmpty()) {
                if (p.getContent() == null || !p.getContent().toLowerCase().contains(keyword.toLowerCase()))
                    match = false;
            }
            if (match && beforeId != null) {
                if (p.getId() >= beforeId)
                    match = false;
            }
            if (match && afterId != null) {
                if (p.getId() <= afterId)
                    match = false;
            }
            if (match && fromD != null) {
                if (p.getCreatedAt() == null || p.getCreatedAt().isBefore(fromD.toInstant()))
                    match = false;
            }
            if (match && toD != null) {
                if (p.getCreatedAt() == null || p.getCreatedAt().isAfter(toD.toInstant()))
                    match = false;
            }
            if (match && minWordCount != null) {
                if (p.getContent() == null || p.getContent().split("\\s+").length < minWordCount)
                    match = false;
            }
            if (match && maxWordCount != null) {
                if (p.getContent() == null || p.getContent().split("\\s+").length > maxWordCount)
                    match = false;
            }
            if (match)
                result.add(p);
        }
        return result;
    }

    public Post createDraft(String content) {
        Post post = new Post(content);
        post.setId(idGenerator.getAndIncrement());
        posts.put(post.getId(), post);
        return post;
    }
    
    public Post publishPost(Long id) {
        Post post = posts.get(id);
        if (post != null && post.isDraft()) {
            post.setDraft(false);
            post.setPublishedAt(new Date().toInstant());
            return post;
        }
        return null;
    }
    
    public boolean deletePost(Long id) {
        return posts.remove(id) != null;
    }
    
    public Post getPost(Long id) {
        return posts.get(id);
    }
    
    public List<Post> getAllPublishedPosts() {
        return posts.values().stream()
                .filter(post -> !post.isDraft())
                .collect(Collectors.toList());
    }
    
    public List<Post> getAllDraftPosts() {
        return posts.values().stream()
                .filter(Post::isDraft)
                .collect(Collectors.toList());
    }
}
