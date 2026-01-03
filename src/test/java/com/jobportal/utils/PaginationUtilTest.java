package com.jobportal.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PaginationUtil.
 */
class PaginationUtilTest {

    @Test
    void testBasicPagination() {
        PaginationUtil pagination = new PaginationUtil(1, 100, 10);
        
        assertEquals(1, pagination.getCurrentPage());
        assertEquals(100, pagination.getTotalItems());
        assertEquals(10, pagination.getItemsPerPage());
        assertEquals(10, pagination.getTotalPages());
        assertEquals(0, pagination.getStartItem());
        assertEquals(10, pagination.getEndItem());
    }

    @Test
    void testLastPage() {
        PaginationUtil pagination = new PaginationUtil(5, 45, 10);
        
        assertEquals(5, pagination.getCurrentPage());
        assertEquals(5, pagination.getTotalPages());
        assertEquals(40, pagination.getStartItem());
        assertEquals(45, pagination.getEndItem());
    }

    @Test
    void testHasPreviousAndNext() {
        PaginationUtil firstPage = new PaginationUtil(1, 100, 10);
        assertFalse(firstPage.hasPrevious());
        assertTrue(firstPage.hasNext());
        
        PaginationUtil middlePage = new PaginationUtil(5, 100, 10);
        assertTrue(middlePage.hasPrevious());
        assertTrue(middlePage.hasNext());
        
        PaginationUtil lastPage = new PaginationUtil(10, 100, 10);
        assertTrue(lastPage.hasPrevious());
        assertFalse(lastPage.hasNext());
    }

    @Test
    void testGetSqlLimit() {
        PaginationUtil pagination = new PaginationUtil(3, 100, 10);
        assertEquals("LIMIT 10 OFFSET 20", pagination.getSqlLimit());
    }

    @Test
    void testPageNumbers() {
        PaginationUtil pagination = new PaginationUtil(5, 100, 10);
        int[] pages = pagination.getPageNumbers(5);
        
        assertArrayEquals(new int[]{3, 4, 5, 6, 7}, pages);
    }

    @Test
    void testInvalidPageNumber() {
        PaginationUtil pagination = new PaginationUtil(15, 100, 10);
        assertEquals(10, pagination.getCurrentPage()); // Should be capped at total pages
    }

    @Test
    void testZeroItems() {
        PaginationUtil pagination = new PaginationUtil(1, 0, 10);
        assertEquals(0, pagination.getTotalPages());
        assertEquals(0, pagination.getStartItem());
        assertEquals(0, pagination.getEndItem());
    }
}
