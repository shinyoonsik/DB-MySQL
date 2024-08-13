package com.example.indextest.common;

import java.util.List;

public record PageCursor<T>(
        CursorRequest cursorRequest,
        List<T> contents
) {
}
