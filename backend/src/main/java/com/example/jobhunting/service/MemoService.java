package com.example.jobhunting.service;

import com.example.jobhunting.entity.JobApplication;
import com.example.jobhunting.entity.Memo;
import com.example.jobhunting.exception.ResourceNotFoundException;
import com.example.jobhunting.repository.JobApplicationRepository;
import com.example.jobhunting.repository.MemoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * メモのビジネスロジック。
 *
 * 設計理由:
 * - updateMemo では content のみ更新する（jobApplication は変更不可）。
 *   メモを別の応募に移動するユースケースは存在しないため、
 *   誤って company が書き換わるリスクをゼロにする。
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemoService {

    private final MemoRepository memoRepository;
    private final JobApplicationRepository applicationRepository;

    public List<Memo> getMemosByApplication(Long applicationId) {
        return memoRepository.findByJobApplicationIdOrderByCreatedAtDesc(applicationId);
    }

    @Transactional
    public Memo createMemo(Long applicationId, Memo memo) {
        JobApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("JobApplication", applicationId));
        memo.setJobApplication(application);
        return memoRepository.save(memo);
    }

    @Transactional
    public Memo updateMemo(Long id, Memo updated) {
        Memo memo = memoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Memo", id));
        memo.setContent(updated.getContent());
        return memoRepository.save(memo);
    }

    @Transactional
    public void deleteMemo(Long id) {
        Memo memo = memoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Memo", id));
        memoRepository.delete(memo);
    }
}
