package com.gengzc.util;

import java.util.List;

/**
 * The Class PageListBean.
 * @param <T> the generic type
 * @author sunyd
 */
public class PageListBean<T> {

    /** 
     * 总记录数
     */
    private int total;
    
    final int defaltPageSize = 10;
    
    /**
     *  每页显示的记录数
     */
    private int pageSize = defaltPageSize;
    
    final int page = 1;
    
    /** 
     * 当前页号. 
     */
    private int currentPage = page;
    /** 
     * 总页数
     */
    private int totalPages;
    
    /**
     * 要排序的列名
     */
    private String sortColumn;
    
    /**
     * 排序方式
     */
    private String sortord;

    /** 
     * 列表 list
     */
    private List<T> data;

    /**
     * Instantiates a new page list bean.
     * @param total the total results
     * @param pageSize the page size
     * @param currentPage the current page
     * @param data the list
     */
    public PageListBean(int total, int pageSize, int currentPage, List<T> data) {
        this.total = total;
        this.pageSize = pageSize;
        if (this.pageSize > 0) {
            this.currentPage = currentPage;
            this.totalPages = (int) Math.ceil(total / (double) pageSize);
        } else {
            this.currentPage = 1;
            this.totalPages = 1;
        }
        this.data = data;
    }

    /**
     * Gets the total results.
     * @return the total results
     */
    public int getTotal() {
        return total;
    }

    /**
     * Gets the page size.
     * @return the page size
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * Gets the current page.
     * @return the current page
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * Gets the total pages.
     * @return the total pages
     */
    public int getTotalPages() {
        return totalPages;
    }

    /**
     * Gets the list.
     * @return the list
     */
    public List<T> getData() {
        return data;
    }
    
    /**
     * get sort  column
     * @return sortColumn
     */
    public String getSortColumn() {
		return sortColumn;
	}

    /**
     * set sort column value
     * @param sortColumn
     */
	public void setSortColumn(String sortColumn) {
		this.sortColumn = sortColumn;
	}

	/**
	 * get sort order, asc or desc 
	 * @return sortord
	 */
	public String getSortord() {
		return sortord;
	}

	/**
	 * set sort order value
	 * @param sortord
	 */
	public void setSortord(String sortord) {
		this.sortord = sortord;
	}

	/*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "totalResults:" + this.total + " pageSize:" + pageSize + " currentPage:" + currentPage
                + " totalPages:" + totalPages + " list:" + data.toString();
    }

}
