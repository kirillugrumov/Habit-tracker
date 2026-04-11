package com.example.habittracker.cache;

import java.util.Objects;

public class HabitSearchCacheKey {

    private final HabitSearchQueryType queryType;
    private final String username;
    private final String categoryName;
    private final int pageNumber;
    private final int pageSize;
    private final String sort;

    public HabitSearchCacheKey(HabitSearchQueryType queryType,
                               String username,
                               String categoryName,
                               int pageNumber,
                               int pageSize,
                               String sort) {
        this.queryType = queryType;
        this.username = username;
        this.categoryName = categoryName;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.sort = sort;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HabitSearchCacheKey that)) {
            return false;
        }
        return pageNumber == that.pageNumber
                && pageSize == that.pageSize
                && queryType == that.queryType
                && Objects.equals(username, that.username)
                && Objects.equals(categoryName, that.categoryName)
                && Objects.equals(sort, that.sort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(queryType, username, categoryName, pageNumber, pageSize, sort);
    }
}
