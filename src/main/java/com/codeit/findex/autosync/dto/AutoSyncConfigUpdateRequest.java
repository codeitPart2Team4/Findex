package com.codeit.findex.autosync.dto;

import lombok.Data;

public record AutoSyncConfigUpdateRequest(
        boolean enabled
) {}