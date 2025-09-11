package com.codeit.findex.syncjob.entity;

import com.codeit.findex.common.entity.BaseEntity;
import com.codeit.findex.indexinfo.entity.IndexInfo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "sync_job")
@Getter @Setter
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
public class SyncJob extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "index_info_id", nullable = false)
    private IndexInfo indexInfo;

    @JoinColumn(name = "job_type", nullable = false)
    private String jobType;

    @JoinColumn(name = "target_date", nullable = false)
    private LocalDate targetDate;


    @Column(nullable = false, length = 100)
    private String worker;

    @LastModifiedDate
    @JoinColumn(name = "job_time", nullable = false)
    private LocalDateTime jobTime;

    @Column(nullable = false, length = 20)
    private String result;

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        SyncJob syncJob = (SyncJob) object;
        return Objects.equals(indexInfo, syncJob.indexInfo) && Objects.equals(jobType, syncJob.jobType) && Objects.equals(targetDate, syncJob.targetDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(indexInfo, jobType, targetDate);
    }
}
