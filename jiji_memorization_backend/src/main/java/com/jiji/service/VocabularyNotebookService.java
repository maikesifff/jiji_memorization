package com.jiji.service;

import com.jiji.dto.VocabularyNotebookDTO;
import com.jiji.entity.Meaning;
import com.jiji.entity.VocabularyNotebook;
import com.jiji.entity.VocabularyStatus;
import com.jiji.entity.Word;
import com.jiji.repository.MeaningRepository;
import com.jiji.repository.VocabularyNotebookRepository;
import com.jiji.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class VocabularyNotebookService {
    
    @Autowired
    private VocabularyNotebookRepository vocabularyNotebookRepository;
    
    @Autowired
    private WordRepository wordRepository;
    
    @Autowired
    private MeaningRepository meaningRepository;
    
    // 添加单词到生词本
    public VocabularyNotebook addWordToNotebook(Long userId, Long wordId) {
        // 检查是否已经存在
        if (vocabularyNotebookRepository.existsByUserIdAndWordId(userId, wordId)) {
            throw new RuntimeException("该单词已在生词本中");
        }
        
        VocabularyNotebook notebook = new VocabularyNotebook(userId, wordId);
        return vocabularyNotebookRepository.save(notebook);
    }
    
    // 从生词本中移除单词
    public void removeWordFromNotebook(Long userId, Long wordId) {
        Optional<VocabularyNotebook> notebook = vocabularyNotebookRepository.findByUserIdAndWordId(userId, wordId);
        if (notebook.isPresent()) {
            vocabularyNotebookRepository.delete(notebook.get());
        }
    }
    
    // 获取用户的生词本
    public List<VocabularyNotebook> getUserNotebook(Long userId) {
        return vocabularyNotebookRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    // 根据状态获取用户的生词本
    public List<VocabularyNotebook> getUserNotebookByStatus(Long userId, VocabularyStatus status) {
        return vocabularyNotebookRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status);
    }
    
    // 获取包含单词信息的生词本数据
    public List<VocabularyNotebookDTO> getUserNotebookWithWords(Long userId) {
        List<VocabularyNotebook> notebooks = vocabularyNotebookRepository.findByUserIdOrderByCreatedAtDesc(userId);
        
        return notebooks.stream().map(notebook -> {
            Optional<Word> wordOpt = wordRepository.findById(notebook.getWordId());
            if (wordOpt.isPresent()) {
                Word word = wordOpt.get();
                // 获取词义信息
                List<Meaning> meanings = meaningRepository.findByWordId(word.getId());
                String formattedMeanings = formatMeanings(meanings);
                return new VocabularyNotebookDTO(notebook, word.getWordText(), word.getAmericanPhonetic(), word.getBritishPhonetic(), word.getPronUs(), word.getPronUk(), formattedMeanings);
            } else {
                // 如果单词不存在，返回基本信息
                return new VocabularyNotebookDTO(notebook, "未知单词", "", "", "", "", "暂无词义信息");
            }
        }).collect(Collectors.toList());
    }
    
    // 获取包含单词信息的生词本数据（分页）
    public Page<VocabularyNotebookDTO> getUserNotebookWithWords(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<VocabularyNotebook> notebookPage = vocabularyNotebookRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        
        return notebookPage.map(notebook -> {
            Optional<Word> wordOpt = wordRepository.findById(notebook.getWordId());
            if (wordOpt.isPresent()) {
                Word word = wordOpt.get();
                // 获取词义信息
                List<Meaning> meanings = meaningRepository.findByWordId(word.getId());
                String formattedMeanings = formatMeanings(meanings);
                return new VocabularyNotebookDTO(notebook, word.getWordText(), word.getAmericanPhonetic(), word.getBritishPhonetic(), word.getPronUs(), word.getPronUk(), formattedMeanings);
            } else {
                // 如果单词不存在，返回基本信息
                return new VocabularyNotebookDTO(notebook, "未知单词", "", "", "", "", "暂无词义信息");
            }
        });
    }
    
    // 根据状态获取包含单词信息的生词本数据
    public List<VocabularyNotebookDTO> getUserNotebookWithWordsByStatus(Long userId, VocabularyStatus status) {
        List<VocabularyNotebook> notebooks = vocabularyNotebookRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status);
        
        return notebooks.stream().map(notebook -> {
            Optional<Word> wordOpt = wordRepository.findById(notebook.getWordId());
            if (wordOpt.isPresent()) {
                Word word = wordOpt.get();
                // 获取词义信息
                List<Meaning> meanings = meaningRepository.findByWordId(word.getId());
                String formattedMeanings = formatMeanings(meanings);
                return new VocabularyNotebookDTO(notebook, word.getWordText(), word.getAmericanPhonetic(), word.getBritishPhonetic(), word.getPronUs(), word.getPronUk(), formattedMeanings);
            } else {
                // 如果单词不存在，返回基本信息
                return new VocabularyNotebookDTO(notebook, "未知单词", "", "", "", "", "暂无词义信息");
            }
        }).collect(Collectors.toList());
    }
    
    // 根据状态获取包含单词信息的生词本数据（分页）
    public Page<VocabularyNotebookDTO> getUserNotebookWithWordsByStatus(Long userId, VocabularyStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<VocabularyNotebook> notebookPage = vocabularyNotebookRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status, pageable);
        
        return notebookPage.map(notebook -> {
            Optional<Word> wordOpt = wordRepository.findById(notebook.getWordId());
            if (wordOpt.isPresent()) {
                Word word = wordOpt.get();
                // 获取词义信息
                List<Meaning> meanings = meaningRepository.findByWordId(word.getId());
                String formattedMeanings = formatMeanings(meanings);
                return new VocabularyNotebookDTO(notebook, word.getWordText(), word.getAmericanPhonetic(), word.getBritishPhonetic(), word.getPronUs(), word.getPronUk(), formattedMeanings);
            } else {
                // 如果单词不存在，返回基本信息
                return new VocabularyNotebookDTO(notebook, "未知单词", "", "", "", "", "暂无词义信息");
            }
        });
    }
    
    // 更新单词复习记录
    public VocabularyNotebook updateReview(Long userId, Long wordId, boolean correct) {
        Optional<VocabularyNotebook> notebookOpt = vocabularyNotebookRepository.findByUserIdAndWordId(userId, wordId);
        if (notebookOpt.isPresent()) {
            VocabularyNotebook notebook = notebookOpt.get();
            notebook.setReviewCount(notebook.getReviewCount() + 1);
            notebook.setLastReviewedAt(LocalDateTime.now());
            
            // 根据正确率调整状态
            if (correct) {
                if (notebook.getReviewCount() >= 3) {
                    notebook.setStatus(VocabularyStatus.MASTERED);
                } else {
                    notebook.setStatus(VocabularyStatus.LEARNING);
                }
            } else {
                notebook.setStatus(VocabularyStatus.LEARNING);
            }
            
            return vocabularyNotebookRepository.save(notebook);
        }
        throw new RuntimeException("生词本记录不存在");
    }
    
    // 更新单词难度等级
    public VocabularyNotebook updateDifficultyLevel(Long userId, Long wordId, Integer difficultyLevel) {
        Optional<VocabularyNotebook> notebookOpt = vocabularyNotebookRepository.findByUserIdAndWordId(userId, wordId);
        if (notebookOpt.isPresent()) {
            VocabularyNotebook notebook = notebookOpt.get();
            notebook.setDifficultyLevel(difficultyLevel);
            return vocabularyNotebookRepository.save(notebook);
        }
        throw new RuntimeException("生词本记录不存在");
    }
    
    // 更新单词笔记
    public VocabularyNotebook updateNotes(Long userId, Long wordId, String notes) {
        Optional<VocabularyNotebook> notebookOpt = vocabularyNotebookRepository.findByUserIdAndWordId(userId, wordId);
        if (notebookOpt.isPresent()) {
            VocabularyNotebook notebook = notebookOpt.get();
            notebook.setNotes(notes);
            return vocabularyNotebookRepository.save(notebook);
        }
        throw new RuntimeException("生词本记录不存在");
    }
    
    // 获取需要复习的单词
    public List<VocabularyNotebook> getWordsForReview(Long userId) {
        LocalDateTime reviewTime = LocalDateTime.now().minusDays(1); // 1天前复习过的需要再次复习
        return vocabularyNotebookRepository.findWordsForReview(userId, reviewTime);
    }
    
    // 获取生词本统计信息
    public VocabularyNotebookStats getNotebookStats(Long userId) {
        long totalWords = vocabularyNotebookRepository.countByUserId(userId);
        long newWords = vocabularyNotebookRepository.countByUserIdAndStatus(userId, VocabularyStatus.NEW);
        long learningWords = vocabularyNotebookRepository.countByUserIdAndStatus(userId, VocabularyStatus.LEARNING);
        long masteredWords = vocabularyNotebookRepository.countByUserIdAndStatus(userId, VocabularyStatus.MASTERED);
        
        return new VocabularyNotebookStats(totalWords, newWords, learningWords, masteredWords);
    }
    
    // 检查单词是否在生词本中
    public boolean isWordInNotebook(Long userId, Long wordId) {
        return vocabularyNotebookRepository.existsByUserIdAndWordId(userId, wordId);
    }
    
    // 生词本统计信息内部类
    public static class VocabularyNotebookStats {
        private final long totalWords;
        private final long newWords;
        private final long learningWords;
        private final long masteredWords;
        
        public VocabularyNotebookStats(long totalWords, long newWords, long learningWords, long masteredWords) {
            this.totalWords = totalWords;
            this.newWords = newWords;
            this.learningWords = learningWords;
            this.masteredWords = masteredWords;
        }
        
        public long getTotalWords() { return totalWords; }
        public long getNewWords() { return newWords; }
        public long getLearningWords() { return learningWords; }
        public long getMasteredWords() { return masteredWords; }
    }
    
    // 格式化词义信息
    private String formatMeanings(List<Meaning> meanings) {
        if (meanings == null || meanings.isEmpty()) {
            return "暂无词义信息";
        }
        
        return meanings.stream()
                .map(meaning -> {
                    String pos = meaning.getPos() != null ? meaning.getPos() : "";
                    String content = meaning.getContent() != null ? meaning.getContent() : "";
                    return "[" + pos + "]" + content;
                })
                .collect(Collectors.joining(","));
    }
}
