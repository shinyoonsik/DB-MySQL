package com.example.indextest.common;

import java.util.List;

public record PageCursorByCreatedDate<T>(
        CursorRequestByCreatedDate cursorRequestByCreatedDate,
        List<T> contents
) {
}
