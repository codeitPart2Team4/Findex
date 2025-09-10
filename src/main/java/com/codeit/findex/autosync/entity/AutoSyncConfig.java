package com.codeit.findex.autosync.entity;

import com.codeit.findex.common.entity.BaseEntity;
import com.codeit.findex.indexinfo.entity.IndexInfo;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "auto_sync")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AutoSyncConfig extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "index_info_id", nullable = false, unique = true)
    private IndexInfo indexInfo;

    @Column(nullable = false)
    private boolean enabled;

    public void updateEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
