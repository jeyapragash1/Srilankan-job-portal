package com.jobportal.utils;

/**
 * Pagination utility for managing paginated results.
 */
public class PaginationUtil {
    
    private int currentPage;
    private int totalItems;
    private int itemsPerPage;
    private int totalPages;
    private int startItem;
    private int endItem;

    public PaginationUtil(int currentPage, int totalItems, int itemsPerPage) {
        this.currentPage = Math.max(1, currentPage);
        this.totalItems = Math.max(0, totalItems);
        this.itemsPerPage = Math.max(1, itemsPerPage);
        
        calculatePagination();
    }

    private void calculatePagination() {
        this.totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
        
        if (currentPage > totalPages && totalPages > 0) {
            currentPage = totalPages;
        }
        
        this.startItem = (currentPage - 1) * itemsPerPage;
        this.endItem = Math.min(startItem + itemsPerPage, totalItems);
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public int getItemsPerPage() {
        return itemsPerPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getStartItem() {
        return startItem;
    }

    public int getEndItem() {
        return endItem;
    }

    public boolean hasPrevious() {
        return currentPage > 1;
    }

    public boolean hasNext() {
        return currentPage < totalPages;
    }

    public int getPreviousPage() {
        return Math.max(1, currentPage - 1);
    }

    public int getNextPage() {
        return Math.min(totalPages, currentPage + 1);
    }

    /**
     * Gets the SQL LIMIT clause for pagination.
     *
     * @return SQL LIMIT clause
     */
    public String getSqlLimit() {
        return String.format("LIMIT %d OFFSET %d", itemsPerPage, startItem);
    }

    /**
     * Generates page numbers for display (e.g., for pagination UI).
     *
     * @param maxPages maximum number of page links to show
     * @return array of page numbers
     */
    public int[] getPageNumbers(int maxPages) {
        int start = Math.max(1, currentPage - maxPages / 2);
        int end = Math.min(totalPages, start + maxPages - 1);
        
        if (end - start < maxPages - 1) {
            start = Math.max(1, end - maxPages + 1);
        }
        
        int[] pages = new int[end - start + 1];
        for (int i = 0; i < pages.length; i++) {
            pages[i] = start + i;
        }
        return pages;
    }
}
